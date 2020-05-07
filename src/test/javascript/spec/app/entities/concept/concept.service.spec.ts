import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ConceptService } from 'app/entities/concept/concept.service';
import { IConcept, Concept } from 'app/shared/model/concept.model';

describe('Service Tests', () => {
  describe('Concept Service', () => {
    let injector: TestBed;
    let service: ConceptService;
    let httpMock: HttpTestingController;
    let elemDefault: IConcept;
    let expectedResult: IConcept | IConcept[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(ConceptService);
      httpMock = injector.get(HttpTestingController);

      elemDefault = new Concept(0, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 0, 0, 'AAAAAAA', 0);
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
            id: 0
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Concept()).subscribe(resp => (expectedResult = resp.body));

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
            position: 1
          },
          elemDefault
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
            position: 1
          },
          elemDefault
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
