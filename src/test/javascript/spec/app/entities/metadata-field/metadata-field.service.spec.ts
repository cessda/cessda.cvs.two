import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MetadataFieldService } from 'app/entities/metadata-field/metadata-field.service';
import { IMetadataField, MetadataField } from 'app/shared/model/metadata-field.model';
import { ObjectType } from 'app/shared/model/enumerations/object-type.model';

describe('Service Tests', () => {
  describe('MetadataField Service', () => {
    let injector: TestBed;
    let service: MetadataFieldService;
    let httpMock: HttpTestingController;
    let elemDefault: IMetadataField;
    let expectedResult: IMetadataField | IMetadataField[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(MetadataFieldService);
      httpMock = injector.get(HttpTestingController);

      elemDefault = new MetadataField(0, 'AAAAAAA', 'AAAAAAA', ObjectType.AGENCY);
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a MetadataField', () => {
        const returnedFromService = Object.assign(
          {
            id: 0
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new MetadataField()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a MetadataField', () => {
        const returnedFromService = Object.assign(
          {
            metadataKey: 'BBBBBB',
            description: 'BBBBBB',
            objectType: 'BBBBBB'
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of MetadataField', () => {
        const returnedFromService = Object.assign(
          {
            metadataKey: 'BBBBBB',
            description: 'BBBBBB',
            objectType: 'BBBBBB'
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

      it('should delete a MetadataField', () => {
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
