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
import { MetadataFieldService } from 'app/entities/metadata-field/metadata-field.service';
import { MetadataField } from 'app/shared/model/metadata-field.model';
import { ObjectType } from 'app/shared/model/enumerations/object-type.model';

describe('Service Tests', () => {
  describe('MetadataField Service', () => {
    let injector: TestBed;
    let service: MetadataFieldService;
    let httpMock: HttpTestingController;
    let elemDefault: MetadataField;
    let expectedResult: MetadataField | MetadataField[] | boolean | null;
    let mimeType: string;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(MetadataFieldService);
      httpMock = injector.get(HttpTestingController);

      elemDefault = {
        id: 0,
        metadataKey: 'AAAAAAA',
        description: 'AAAAAAA',
        objectType: ObjectType.AGENCY,
        metadataValues: [],
      };

      mimeType = 'application/pdf';
    });

    describe('Service methods', () => {
      it('should find an element by key', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.findByKey(elemDefault.metadataKey).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should download metadata file', () => {
        const jsonString = JSON.stringify(Object.assign({}, elemDefault));
        const returnedFromService = new TextEncoder().encode(jsonString);

        let responseStream: Promise<ArrayBuffer> = Promise.reject();
        service.downloadMetadataFile(elemDefault.metadataKey, mimeType).subscribe(resp => (responseStream = resp.arrayBuffer()));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(new Blob([returnedFromService]));
        responseStream.then(r => {
          const result = new TextDecoder().decode(r);
          expect(result).toMatchObject(jsonString);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
