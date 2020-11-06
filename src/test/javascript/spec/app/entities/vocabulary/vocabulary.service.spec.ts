import {getTestBed, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import * as moment from 'moment';
import {DATE_FORMAT, DATE_TIME_FORMAT} from 'app/shared/constants/input.constants';
import {VocabularyService} from 'app/entities/vocabulary/vocabulary.service';
import {IVocabulary, Vocabulary} from 'app/shared/model/vocabulary.model';

describe('Service Tests', () => {
  describe('Vocabulary Service', () => {
    let injector: TestBed;
    let service: VocabularyService;
    let httpMock: HttpTestingController;
    let elemDefault: IVocabulary;
    let expectedResult: IVocabulary | IVocabulary[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(VocabularyService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Vocabulary(
        0,
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        0,
        0,
        false,
        false,
        false,
        'AAAAAAA',
        'en',
        'AAAAAAA',
        '1.0',
        0,
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        currentDate,
        currentDate,
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA'
      );
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            publicationDate: currentDate.format(DATE_FORMAT),
            lastModified: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
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
            lastModified: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            publicationDate: currentDate,
            lastModified: currentDate
          },
          returnedFromService
        );

        service.create(new Vocabulary()).subscribe(resp => (expectedResult = resp.body));

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
            definitionSv: 'BBBBBB'
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            publicationDate: currentDate,
            lastModified: currentDate
          },
          returnedFromService
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
            definitionSv: 'BBBBBB'
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            publicationDate: currentDate,
            lastModified: currentDate
          },
          returnedFromService
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
