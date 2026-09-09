/*
 * Copyright © 2017-2026 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { AgencyService } from 'app/agency/agency.service';
import { Agency, createNewAgency } from 'app/shared/model/agency.model';
import { AgencyStat } from 'app/shared/model/agencystat.model';

describe('Service Tests', () => {
  describe('Agency Service', () => {
    let service: AgencyService;
    let httpMock: HttpTestingController;

    const agency: Agency = createNewAgency({ id: 4, name: 'CESSDA', link: 'https://www.cessda.eu' });

    beforeEach(() => {
      TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });

      service = TestBed.inject(AgencyService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
      // fails the test if a call went anywhere it was not expected to
      httpMock.verify();
    });

    it('should post a new agency', () => {
      service.create(agency).subscribe();

      const req = httpMock.expectOne({ method: 'POST' });

      expect(req.request.url).toBe(service.resourceUrl);
      expect(req.request.body).toEqual(agency);
      req.flush(agency);
    });

    it('should put an updated agency', () => {
      service.update(agency).subscribe();

      const req = httpMock.expectOne({ method: 'PUT' });

      expect(req.request.url).toBe(service.resourceUrl);
      expect(req.request.body).toEqual(agency);
      req.flush(agency);
    });

    it('should request a single agency by id', () => {
      let body;
      service.find(4).subscribe(res => (body = res.body));

      const req = httpMock.expectOne({ method: 'GET' });

      expect(req.request.url).toBe(`${service.resourceUrl}/4`);
      req.flush(agency);
      expect(body).toEqual(agency);
    });

    it('should delete an agency by id', () => {
      let status;
      service.delete(4).subscribe(res => (status = res.status));

      const req = httpMock.expectOne({ method: 'DELETE' });

      expect(req.request.url).toBe(`${service.resourceUrl}/4`);
      req.flush({});
      expect(status).toBe(200);
    });

    it('should request the agency statistics from their own endpoint', () => {
      let body;
      service.statistic(4).subscribe(res => (body = res.body));

      const req = httpMock.expectOne({ method: 'GET' });

      expect(req.request.url).toBe(`${service.resourceStatUrl}/4`);
      req.flush({ id: 4 } as AgencyStat);
      expect(body).toEqual({ id: 4 });
    });

    describe('listing agencies', () => {
      it('should pass the pagination through as query parameters', () => {
        service.query({ page: 1, size: 20, sort: ['name,asc'] }).subscribe();

        const req = httpMock.expectOne(r => r.url === service.resourceUrl);

        expect(req.request.params.get('page')).toBe('1');
        expect(req.request.params.get('size')).toBe('20');
        expect(req.request.params.getAll('sort')).toEqual(['name,asc']);
        req.flush([agency]);
      });

      it('should ask without parameters when no pagination is given', () => {
        service.query().subscribe();

        const req = httpMock.expectOne({ method: 'GET' });

        expect(req.request.url).toBe(service.resourceUrl);
        expect(req.request.params.keys()).toHaveLength(0);
        req.flush([]);
      });

      it('should carry the query and the pagination to the search endpoint', () => {
        service.search({ query: 'cessda', page: 0, size: 10, sort: ['name,asc'] }).subscribe();

        const req = httpMock.expectOne(r => r.url === service.resourceSearchUrl);

        expect(req.request.params.get('query')).toBe('cessda');
        expect(req.request.params.get('page')).toBe('0');
        expect(req.request.params.get('size')).toBe('10');
        req.flush([agency]);
      });

      it('should hand the body straight back to the caller', () => {
        let body;
        service.query().subscribe(res => (body = res.body));

        httpMock.expectOne({ method: 'GET' }).flush([agency]);

        expect(body).toEqual([agency]);
      });
    });
  });
});
