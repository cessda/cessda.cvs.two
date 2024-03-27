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
import { ConceptService } from 'app/entities/concept/concept.service';
import { Concept } from 'app/shared/model/concept.model';

describe('Service Tests', () => {
  describe('Concept Service', () => {
    let injector: TestBed;
    let service: ConceptService;
    let httpMock: HttpTestingController;
    let elemDefault: Concept;
    let expectedResult: Concept | Concept[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.inject(ConceptService);
      httpMock = injector.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        uri: 'AAAAAAA',
        notation: 'AAAAAAA',
        title: 'AAAAAAA',
        definition: 'AAAAAAA',
        previousConcept: 0,
        slConcept: 0,
        parent: 'AAAAAAA',
        position: 0,
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

      it('should create a Concept', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault,
        );

        const expected = Object.assign({}, returnedFromService);

        service
          .create({
            notation: '',
            parent: '',
            visible: true,
          })
          .subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Concept', () => {
        const returnedFromService = Object.assign(
          {
            uri: 'BBBBBB',
            notation: 'BBBBBB',
            title: 'BBBBBB',
            definition: 'BBBBBB',
            previousConcept: 1,
            slConcept: 1,
            parent: 'BBBBBB',
            position: 1,
          },
          elemDefault,
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Concept', () => {
        const returnedFromService = Object.assign(
          {
            uri: 'BBBBBB',
            notation: 'BBBBBB',
            title: 'BBBBBB',
            definition: 'BBBBBB',
            previousConcept: 1,
            slConcept: 1,
            parent: 'BBBBBB',
            position: 1,
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

      it('should delete a Concept', () => {
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
