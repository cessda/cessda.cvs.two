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

import { ResolverService } from 'app/admin/resolver/resolver.service';
import { Resolver } from 'app/shared/model/resolver.model';

describe('Service Tests', () => {
  describe('Resolver Service', () => {
    let service: ResolverService;
    let httpMock: HttpTestingController;

    const resolver: Resolver = { id: 5, resourceUrl: 'https://vocabularies.cessda.eu', resolverURI: 'doi:10.5281/zenodo.1' };

    beforeEach(() => {
      TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });

      service = TestBed.inject(ResolverService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
      httpMock.verify();
    });

    it('should post a new resolver', () => {
      service.create(resolver).subscribe();

      const req = httpMock.expectOne({ method: 'POST' });

      expect(req.request.url).toBe(service.resourceUrl);
      expect(req.request.body).toEqual(resolver);
      req.flush(resolver);
    });

    it('should put an updated resolver', () => {
      service.update(resolver).subscribe();

      const req = httpMock.expectOne({ method: 'PUT' });

      expect(req.request.url).toBe(service.resourceUrl);
      expect(req.request.body).toEqual(resolver);
      req.flush(resolver);
    });

    it('should request a single resolver by id', () => {
      let body;
      service.find(5).subscribe(res => (body = res.body));

      const req = httpMock.expectOne({ method: 'GET' });

      expect(req.request.url).toBe(`${service.resourceUrl}/5`);
      req.flush(resolver);
      expect(body).toEqual(resolver);
    });

    it('should delete a resolver by id', () => {
      let status;
      service.delete(5).subscribe(res => (status = res.status));

      const req = httpMock.expectOne({ method: 'DELETE' });

      expect(req.request.url).toBe(`${service.resourceUrl}/5`);
      req.flush({});
      expect(status).toBe(200);
    });

    it('should pass the pagination through as query parameters', () => {
      service.query({ page: 1, size: 20, sort: ['id,asc'] }).subscribe();

      const req = httpMock.expectOne(r => r.url === service.resourceUrl);

      expect(req.request.params.get('page')).toBe('1');
      expect(req.request.params.get('size')).toBe('20');
      req.flush([resolver]);
    });

    it('should carry the query to the search endpoint', () => {
      service.search({ query: 'AnalysisUnit', page: 0, size: 10, sort: ['id,asc'] }).subscribe();

      const req = httpMock.expectOne(r => r.url === service.resourceSearchUrl);

      expect(req.request.params.get('query')).toBe('AnalysisUnit');
      req.flush([resolver]);
    });
  });
});
