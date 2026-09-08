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

import { LicenceService } from 'app/admin/licence/licence.service';
import { Licence } from 'app/shared/model/licence.model';

describe('Service Tests', () => {
  describe('Licence Service', () => {
    let service: LicenceService;
    let httpMock: HttpTestingController;

    const licence: Licence = { id: 2, name: 'Creative Commons Attribution 4.0', abbr: 'CC BY 4.0', link: 'https://creativecommons.org' };

    beforeEach(() => {
      TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });

      service = TestBed.inject(LicenceService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
      httpMock.verify();
    });

    it('should post a new licence', () => {
      service.create(licence).subscribe();

      const req = httpMock.expectOne({ method: 'POST' });

      expect(req.request.url).toBe(service.resourceUrl);
      expect(req.request.body).toEqual(licence);
      req.flush(licence);
    });

    it('should put an updated licence', () => {
      service.update(licence).subscribe();

      const req = httpMock.expectOne({ method: 'PUT' });

      expect(req.request.url).toBe(service.resourceUrl);
      expect(req.request.body).toEqual(licence);
      req.flush(licence);
    });

    it('should request a single licence by id', () => {
      let body;
      service.find(2).subscribe(res => (body = res.body));

      const req = httpMock.expectOne({ method: 'GET' });

      expect(req.request.url).toBe(`${service.resourceUrl}/2`);
      req.flush(licence);
      expect(body).toEqual(licence);
    });

    it('should delete a licence by id', () => {
      let status;
      service.delete(2).subscribe(res => (status = res.status));

      const req = httpMock.expectOne({ method: 'DELETE' });

      expect(req.request.url).toBe(`${service.resourceUrl}/2`);
      req.flush({});
      expect(status).toBe(200);
    });

    it('should pass the pagination through as query parameters', () => {
      service.query({ page: 1, size: 20, sort: ['name,asc'] }).subscribe();

      const req = httpMock.expectOne(r => r.url === service.resourceUrl);

      expect(req.request.params.get('page')).toBe('1');
      expect(req.request.params.get('size')).toBe('20');
      expect(req.request.params.getAll('sort')).toEqual(['name,asc']);
      req.flush([licence]);
    });

    it('should ask without parameters when no pagination is given', () => {
      service.query().subscribe();

      const req = httpMock.expectOne({ method: 'GET' });

      expect(req.request.params.keys()).toHaveLength(0);
      req.flush([]);
    });

    it('should carry the query to the search endpoint', () => {
      service.search({ query: 'attribution', page: 0, size: 10, sort: ['id,asc'] }).subscribe();

      const req = httpMock.expectOne(r => r.url === service.resourceSearchUrl);

      expect(req.request.params.get('query')).toBe('attribution');
      expect(req.request.params.get('page')).toBe('0');
      req.flush([licence]);
    });
  });
});
