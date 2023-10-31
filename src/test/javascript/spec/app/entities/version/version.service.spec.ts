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
import moment from 'moment';
import { DATE_FORMAT, DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { VersionService } from 'app/entities/version/version.service';
import { createNewVersion, Version } from 'app/shared/model/version.model';

describe('Service Tests', () => {
  describe('Version Service', () => {
    let injector: TestBed;
    let service: VersionService;
    let httpMock: HttpTestingController;
    let elemDefault: Version;
    let expectedResult: Version | Version[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(VersionService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = {
        id: 0,
        status: 'AAAAAAA',
        itemType: 'AAAAAAA',
        language: 'AAAAAAA',
        publicationDate: currentDate,
        lastModified: currentDate,
        number: 'AAAAAAA',
        uri: 'AAAAAAA',
        canonicalUri: 'AAAAAAA',
        uriSl: 'AAAAAAA',
        notation: 'AAAAAAA',
        title: 'AAAAAAA',
        definition: 'AAAAAAA',
        previousVersion: 0,
        initialVersion: 0,
        creator: 0,
        publisher: 0,
        notes: 'AAAAAAA',
        versionNotes: 'AAAAAAA',
        versionChanges: 'AAAAAAA',
        discussionNotes: 'AAAAAAA',
        license: 'AAAAAAA',
        licenseId: 0,
        licenseName: 'AAAAAAA',
        licenseLink: 'AAAAAAA',
        licenseLogo: 'AAAAAAA',
        citation: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            publicationDate: currentDate.format(DATE_FORMAT),
            lastModified: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault,
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Version', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            publicationDate: currentDate.format(DATE_FORMAT),
            lastModified: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault,
        );

        const expected = Object.assign(
          {
            publicationDate: currentDate,
            lastModified: currentDate,
          },
          returnedFromService,
        );

        service.create(createNewVersion()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Version', () => {
        const returnedFromService = Object.assign(
          {
            status: 'BBBBBB',
            itemType: 'BBBBBB',
            language: 'BBBBBB',
            publicationDate: currentDate.format(DATE_FORMAT),
            lastModified: currentDate.format(DATE_TIME_FORMAT),
            number: 'BBBBBB',
            uri: 'BBBBBB',
            canonicalUri: 'BBBBBB',
            uriSl: 'BBBBBB',
            notation: 'BBBBBB',
            title: 'BBBBBB',
            definition: 'BBBBBB',
            previousVersion: 1,
            initialVersion: 1,
            creator: 1,
            publisher: 1,
            notes: 'BBBBBB',
            versionNotes: 'BBBBBB',
            versionChanges: 'BBBBBB',
            discussionNotes: 'BBBBBB',
            license: 'BBBBBB',
            licenseId: 1,
            citation: 'BBBBBB',
            ddiUsage: 'BBBBBB',
            translateAgency: 'BBBBBB',
            translateAgencyLink: 'BBBBBB',
          },
          elemDefault,
        );

        const expected = Object.assign(
          {
            publicationDate: currentDate,
            lastModified: currentDate,
          },
          returnedFromService,
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Version', () => {
        const returnedFromService = Object.assign(
          {
            status: 'BBBBBB',
            itemType: 'BBBBBB',
            language: 'BBBBBB',
            publicationDate: currentDate.format(DATE_FORMAT),
            lastModified: currentDate.format(DATE_TIME_FORMAT),
            number: 'BBBBBB',
            uri: 'BBBBBB',
            canonicalUri: 'BBBBBB',
            uriSl: 'BBBBBB',
            notation: 'BBBBBB',
            title: 'BBBBBB',
            definition: 'BBBBBB',
            previousVersion: 1,
            initialVersion: 1,
            creator: 1,
            publisher: 1,
            notes: 'BBBBBB',
            versionNotes: 'BBBBBB',
            versionChanges: 'BBBBBB',
            discussionNotes: 'BBBBBB',
            license: 'BBBBBB',
            licenseId: 1,
            citation: 'BBBBBB',
            ddiUsage: 'BBBBBB',
            translateAgency: 'BBBBBB',
            translateAgencyLink: 'BBBBBB',
          },
          elemDefault,
        );

        const expected = Object.assign(
          {
            publicationDate: currentDate,
            lastModified: currentDate,
          },
          returnedFromService,
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Version', () => {
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
