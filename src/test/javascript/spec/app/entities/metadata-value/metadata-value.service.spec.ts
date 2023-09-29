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
import { getTestBed, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MetadataValueService } from 'app/entities/metadata-value/metadata-value.service';
import { MetadataValue } from 'app/shared/model/metadata-value.model';
import { ObjectType } from 'app/shared/model/enumerations/object-type.model';

describe('Service Tests', () => {
  describe('MetadataValue Service', () => {
    let injector: TestBed;
    let service: MetadataValueService;
    let httpMock: HttpTestingController;
    let elemDefault: MetadataValue;
    let expectedResult: MetadataValue | MetadataValue[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;

      injector = getTestBed();
      service = injector.get(MetadataValueService);
      httpMock = injector.get(HttpTestingController);

      elemDefault = {
        id: 0,
        identifier: 'AAAAAAA',
        value: 'AAAAAAA',
        objectType: ObjectType.AGENCY,
        objectId: 0,
        metadataFieldId: 0,
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

      it('should create a MetadataValue', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault,
        );

        const expected = Object.assign({}, returnedFromService);

        service.create({}).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a MetadataValue', () => {
        const returnedFromService = Object.assign(
          {
            identifier: 'BBBBBB',
            value: 'BBBBBB',
            objectType: 'BBBBBB',
            objectId: 1,
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

      it('should return a list of MetadataValue', () => {
        const returnedFromService = Object.assign(
          {
            identifier: 'BBBBBB',
            value: 'BBBBBB',
            objectType: 'BBBBBB',
            objectId: 1,
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

      it('should delete a MetadataValue', () => {
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
