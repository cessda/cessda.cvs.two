/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
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
import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ResolverService } from 'app/admin/resolver/resolver.service';
import { Resolver } from 'app/shared/model/resolver.model';

describe('Service Tests', () => {
  describe('Resolver Service', () => {
    let injector: TestBed;
    let service: ResolverService;
    let httpMock: HttpTestingController;
    let elemDefault: Resolver;
    let expectedResult: Resolver | Resolver[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(ResolverService);
      httpMock = injector.get(HttpTestingController);

      elemDefault = {
        id: 0,
        resourceId: 'AAAAAAA',
        resourceType: 'VOCABULARY',
        resourceUrl: 'AAAAAAA',
        resolverType: 'DOI',
        resolverURI: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Resolver', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault,
        );

        const expected = Object.assign({}, returnedFromService);

        service.create({ resolverURI: '', resourceUrl: '' }).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Resolver', () => {
        const returnedFromService = Object.assign(
          {
            resourceId: 'BBBBBB',
            resourceType: 'BBBBBB',
            resourceUrl: 'BBBBBB',
            resolverType: 'BBBBBB',
            resolverURI: 'BBBBBB',
          },
          elemDefault,
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Resolver', () => {
        const returnedFromService = Object.assign(
          {
            resourceId: 'BBBBBB',
            resourceType: 'BBBBBB',
            resourceUrl: 'BBBBBB',
            resolverType: 'BBBBBB',
            resolverURI: 'BBBBBB',
          },
          elemDefault,
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Resolver', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
