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
import moment from 'moment';
import { DATE_FORMAT, DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { VocabularyService } from 'app/entities/vocabulary/vocabulary.service';
import { createNewVocabulary, Status, Vocabulary } from 'app/shared/model/vocabulary.model';

describe('Service Tests', () => {
  describe('Vocabulary Service', () => {
    let injector: TestBed;
    let service: VocabularyService;
    let httpMock: HttpTestingController;
    let elemDefault: Vocabulary;
    let expectedResult: Vocabulary | Vocabulary[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(VocabularyService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = createNewVocabulary({
        id: 0,
        status: Status.DRAFT,
        uri: 'AAAAAA',
        notation: 'AAAAAA',
        versionNumber: 'AAAAAA',
        initialPublication: 0,
        previousPublication: 0,
        archived: false,
        withdrawn: false,
        discoverable: false,
        selectedLang: 'en',
        selectedCode: 'AAAAAA',
        selectedVersion: '1.0',
        agencyId: 0,
        agencyName: 'AAAAAA',
        agencyLogo: 'AAAAAA',
        agencyLink: 'AAAAAA',
        publicationDate: currentDate,
        lastModified: currentDate,
        notes: 'AAAAAA',
        versionSq: 'AAAAAA',
        titleSq: 'AAAAAA',
        definitionSq: 'AAAAAA',
        versionBs: 'AAAAAA',
        titleBs: 'AAAAAA',
        definitionBs: 'AAAAAA',
        versionBg: 'AAAAAA',
        titleBg: 'AAAAAA',
        definitionBg: 'AAAAAA',
        versionHr: 'AAAAAA',
        titleHr: 'AAAAAA',
        definitionHr: 'AAAAAA',
        versionCs: 'AAAAAA',
        titleCs: 'AAAAAA',
        definitionCs: 'AAAAAA',
        versionDa: 'AAAAAA',
        titleDa: 'AAAAAA',
        definitionDa: 'AAAAAA',
        versionNl: 'AAAAAA',
        titleNl: 'AAAAAA',
        definitionNl: 'AAAAAA',
        versionEn: 'AAAAAA',
        titleEn: 'AAAAAA',
        definitionEn: 'AAAAAA',
        versionEt: 'AAAAAA',
        titleEt: 'AAAAAA',
        definitionEt: 'AAAAAA',
        versionFi: 'AAAAAA',
        titleFi: 'AAAAAA',
        definitionFi: 'AAAAAA',
        versionFr: 'AAAAAA',
        titleFr: 'AAAAAA',
        definitionFr: 'AAAAAA',
        versionDe: 'AAAAAA',
        titleDe: 'AAAAAA',
        definitionDe: 'AAAAAA',
        versionEl: 'AAAAAA',
        titleEl: 'AAAAAA',
        definitionEl: 'AAAAAA',
        versionHu: 'AAAAAA',
        titleHu: 'AAAAAA',
        definitionHu: 'AAAAAA',
        versionIt: 'AAAAAA',
        titleIt: 'AAAAAA',
        definitionIt: 'AAAAAA',
        versionJa: 'AAAAAA',
        titleJa: 'AAAAAA',
        definitionJa: 'AAAAAA',
        versionLt: 'AAAAAA',
        titleLt: 'AAAAAA',
        definitionLt: 'AAAAAA',
        versionMk: 'AAAAAA',
        titleMk: 'AAAAAA',
        definitionMk: 'AAAAAA',
        versionNo: 'AAAAAA',
        titleNo: 'AAAAAA',
        definitionNo: 'AAAAAA',
        versionPl: 'AAAAAA',
        titlePl: 'AAAAAA',
        definitionPl: 'AAAAAA',
        versionPt: 'AAAAAA',
        titlePt: 'AAAAAA',
        definitionPt: 'AAAAAA',
        versionRo: 'AAAAAA',
        titleRo: 'AAAAAA',
        definitionRo: 'AAAAAA',
        versionRu: 'AAAAAA',
        titleRu: 'AAAAAA',
        definitionRu: 'AAAAAA',
        versionSr: 'AAAAAA',
        titleSr: 'AAAAAA',
        definitionSr: 'AAAAAA',
        versionSk: 'AAAAAA',
        titleSk: 'AAAAAA',
        definitionSk: 'AAAAAA',
        versionSl: 'AAAAAA',
        titleSl: 'AAAAAA',
        definitionSl: 'AAAAAA',
        versionEs: 'AAAAAA',
        titleEs: 'AAAAAA',
        definitionEs: 'AAAAAA',
        versionSv: 'AAAAAA',
        titleSv: 'AAAAAA',
        definitionSv: 'AAAAAA',
      });
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

      it('should create a Vocabulary', () => {
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

        service.create(createNewVocabulary()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Vocabulary', () => {
        const returnedFromService = Object.assign(
          {
            status: 'BBBBBB',
            uri: 'BBBBBB',
            notation: 'BBBBBB',
            versionNumber: 'BBBBBB',
            initialPublication: 1,
            previousPublication: 1,
            archived: true,
            withdrawn: true,
            discoverable: true,
            sourceLanguage: 'BBBBBB',
            selectedLang: 'BBBBBB',
            selectedCode: 'BBBBBB',
            selectedVersion: 'BBBBBB',
            agencyId: 1,
            agencyName: 'BBBBBB',
            agencyLogo: 'BBBBBB',
            agencyLink: 'BBBBBB',
            publicationDate: currentDate.format(DATE_FORMAT),
            lastModified: currentDate.format(DATE_TIME_FORMAT),
            notes: 'BBBBBB',
            versionSq: 'BBBBBB',
            titleSq: 'BBBBBB',
            definitionSq: 'BBBBBB',
            versionBs: 'BBBBBB',
            titleBs: 'BBBBBB',
            definitionBs: 'BBBBBB',
            versionBg: 'BBBBBB',
            titleBg: 'BBBBBB',
            definitionBg: 'BBBBBB',
            versionHr: 'BBBBBB',
            titleHr: 'BBBBBB',
            definitionHr: 'BBBBBB',
            versionCs: 'BBBBBB',
            titleCs: 'BBBBBB',
            definitionCs: 'BBBBBB',
            versionDa: 'BBBBBB',
            titleDa: 'BBBBBB',
            definitionDa: 'BBBBBB',
            versionNl: 'BBBBBB',
            titleNl: 'BBBBBB',
            definitionNl: 'BBBBBB',
            versionEn: 'BBBBBB',
            titleEn: 'BBBBBB',
            definitionEn: 'BBBBBB',
            versionEt: 'BBBBBB',
            titleEt: 'BBBBBB',
            definitionEt: 'BBBBBB',
            versionFi: 'BBBBBB',
            titleFi: 'BBBBBB',
            definitionFi: 'BBBBBB',
            versionFr: 'BBBBBB',
            titleFr: 'BBBBBB',
            definitionFr: 'BBBBBB',
            versionDe: 'BBBBBB',
            titleDe: 'BBBBBB',
            definitionDe: 'BBBBBB',
            versionEl: 'BBBBBB',
            titleEl: 'BBBBBB',
            definitionEl: 'BBBBBB',
            versionHu: 'BBBBBB',
            titleHu: 'BBBBBB',
            definitionHu: 'BBBBBB',
            versionIt: 'BBBBBB',
            titleIt: 'BBBBBB',
            definitionIt: 'BBBBBB',
            versionJa: 'BBBBBB',
            titleJa: 'BBBBBB',
            definitionJa: 'BBBBBB',
            versionLt: 'BBBBBB',
            titleLt: 'BBBBBB',
            definitionLt: 'BBBBBB',
            versionMk: 'BBBBBB',
            titleMk: 'BBBBBB',
            definitionMk: 'BBBBBB',
            versionNo: 'BBBBBB',
            titleNo: 'BBBBBB',
            definitionNo: 'BBBBBB',
            versionPl: 'BBBBBB',
            titlePl: 'BBBBBB',
            definitionPl: 'BBBBBB',
            versionPt: 'BBBBBB',
            titlePt: 'BBBBBB',
            definitionPt: 'BBBBBB',
            versionRo: 'BBBBBB',
            titleRo: 'BBBBBB',
            definitionRo: 'BBBBBB',
            versionRu: 'BBBBBB',
            titleRu: 'BBBBBB',
            definitionRu: 'BBBBBB',
            versionSr: 'BBBBBB',
            titleSr: 'BBBBBB',
            definitionSr: 'BBBBBB',
            versionSk: 'BBBBBB',
            titleSk: 'BBBBBB',
            definitionSk: 'BBBBBB',
            versionSl: 'BBBBBB',
            titleSl: 'BBBBBB',
            definitionSl: 'BBBBBB',
            versionEs: 'BBBBBB',
            titleEs: 'BBBBBB',
            definitionEs: 'BBBBBB',
            versionSv: 'BBBBBB',
            titleSv: 'BBBBBB',
            definitionSv: 'BBBBBB',
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

      it('should return a list of Vocabulary', () => {
        const returnedFromService = Object.assign(
          {
            status: 'BBBBBB',
            uri: 'BBBBBB',
            notation: 'BBBBBB',
            versionNumber: 'BBBBBB',
            initialPublication: 1,
            previousPublication: 1,
            archived: true,
            withdrawn: true,
            discoverable: true,
            sourceLanguage: 'BBBBBB',
            selectedLang: 'BBBBBB',
            selectedCode: 'BBBBBB',
            selectedVersion: 'BBBBBB',
            agencyId: 1,
            agencyName: 'BBBBBB',
            agencyLogo: 'BBBBBB',
            publicationDate: currentDate.format(DATE_FORMAT),
            lastModified: currentDate.format(DATE_TIME_FORMAT),
            notes: 'BBBBBB',
            versionSq: 'BBBBBB',
            titleSq: 'BBBBBB',
            definitionSq: 'BBBBBB',
            versionBs: 'BBBBBB',
            titleBs: 'BBBBBB',
            definitionBs: 'BBBBBB',
            versionBg: 'BBBBBB',
            titleBg: 'BBBBBB',
            definitionBg: 'BBBBBB',
            versionHr: 'BBBBBB',
            titleHr: 'BBBBBB',
            definitionHr: 'BBBBBB',
            versionCs: 'BBBBBB',
            titleCs: 'BBBBBB',
            definitionCs: 'BBBBBB',
            versionDa: 'BBBBBB',
            titleDa: 'BBBBBB',
            definitionDa: 'BBBBBB',
            versionNl: 'BBBBBB',
            titleNl: 'BBBBBB',
            definitionNl: 'BBBBBB',
            versionEn: 'BBBBBB',
            titleEn: 'BBBBBB',
            definitionEn: 'BBBBBB',
            versionEt: 'BBBBBB',
            titleEt: 'BBBBBB',
            definitionEt: 'BBBBBB',
            versionFi: 'BBBBBB',
            titleFi: 'BBBBBB',
            definitionFi: 'BBBBBB',
            versionFr: 'BBBBBB',
            titleFr: 'BBBBBB',
            definitionFr: 'BBBBBB',
            versionDe: 'BBBBBB',
            titleDe: 'BBBBBB',
            definitionDe: 'BBBBBB',
            versionEl: 'BBBBBB',
            titleEl: 'BBBBBB',
            definitionEl: 'BBBBBB',
            versionHu: 'BBBBBB',
            titleHu: 'BBBBBB',
            definitionHu: 'BBBBBB',
            versionIt: 'BBBBBB',
            titleIt: 'BBBBBB',
            definitionIt: 'BBBBBB',
            versionJa: 'BBBBBB',
            titleJa: 'BBBBBB',
            definitionJa: 'BBBBBB',
            versionLt: 'BBBBBB',
            titleLt: 'BBBBBB',
            definitionLt: 'BBBBBB',
            versionMk: 'BBBBBB',
            titleMk: 'BBBBBB',
            definitionMk: 'BBBBBB',
            versionNo: 'BBBBBB',
            titleNo: 'BBBBBB',
            definitionNo: 'BBBBBB',
            versionPl: 'BBBBBB',
            titlePl: 'BBBBBB',
            definitionPl: 'BBBBBB',
            versionPt: 'BBBBBB',
            titlePt: 'BBBBBB',
            definitionPt: 'BBBBBB',
            versionRo: 'BBBBBB',
            titleRo: 'BBBBBB',
            definitionRo: 'BBBBBB',
            versionRu: 'BBBBBB',
            titleRu: 'BBBBBB',
            definitionRu: 'BBBBBB',
            versionSr: 'BBBBBB',
            titleSr: 'BBBBBB',
            definitionSr: 'BBBBBB',
            versionSk: 'BBBBBB',
            titleSk: 'BBBBBB',
            definitionSk: 'BBBBBB',
            versionSl: 'BBBBBB',
            titleSl: 'BBBBBB',
            definitionSl: 'BBBBBB',
            versionEs: 'BBBBBB',
            titleEs: 'BBBBBB',
            definitionEs: 'BBBBBB',
            versionSv: 'BBBBBB',
            titleSv: 'BBBBBB',
            definitionSv: 'BBBBBB',
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

      it('should delete a Vocabulary', () => {
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
