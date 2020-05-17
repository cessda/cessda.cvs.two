import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ResolverService } from 'app/admin/resolver/resolver.service';
import { IResolver, Resolver } from 'app/shared/model/resolver.model';
import { ResourceType } from 'app/shared/model/enumerations/resource-type.model';
import { ResolverType } from 'app/shared/model/enumerations/resolver-type.model';

describe('Service Tests', () => {
  describe('Resolver Service', () => {
    let injector: TestBed;
    let service: ResolverService;
    let httpMock: HttpTestingController;
    let elemDefault: IResolver;
    let expectedResult: IResolver | IResolver[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(ResolverService);
      httpMock = injector.get(HttpTestingController);

      elemDefault = new Resolver(0, 'AAAAAAA', ResourceType.VOCABULARY, 'AAAAAAA', ResolverType.DOI, 'AAAAAAA');
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
            id: 0
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Resolver()).subscribe(resp => (expectedResult = resp.body));

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
            resolverURI: 'BBBBBB'
          },
          elemDefault
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
            resolverURI: 'BBBBBB'
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
