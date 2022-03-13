import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {IServices, Services} from '../services.model';

import {ServicesService} from './services.service';

describe('Services Service', () => {
  let service: ServicesService;
  let httpMock: HttpTestingController;
  let elemDefault: IServices;
  let expectedResult: IServices | IServices[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ServicesService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      title: 'AAAAAAA',
      description: 'AAAAAAA',
      order: 0,
      iconContentType: 'image/png',
      icon: 'AAAAAAA',
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

    it('should create a Services', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Services()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Services', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          title: 'BBBBBB',
          description: 'BBBBBB',
          order: 1,
          icon: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Services', () => {
      const patchObject = Object.assign(
        {
          title: 'BBBBBB',
          order: 1,
          icon: 'BBBBBB',
        },
        new Services()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Services', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          title: 'BBBBBB',
          description: 'BBBBBB',
          order: 1,
          icon: 'BBBBBB',
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

    it('should delete a Services', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addServicesToCollectionIfMissing', () => {
      it('should add a Services to an empty array', () => {
        const services: IServices = { id: 123 };
        expectedResult = service.addServicesToCollectionIfMissing([], services);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(services);
      });

      it('should not add a Services to an array that contains it', () => {
        const services: IServices = { id: 123 };
        const servicesCollection: IServices[] = [
          {
            ...services,
          },
          { id: 456 },
        ];
        expectedResult = service.addServicesToCollectionIfMissing(servicesCollection, services);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Services to an array that doesn't contain it", () => {
        const services: IServices = { id: 123 };
        const servicesCollection: IServices[] = [{ id: 456 }];
        expectedResult = service.addServicesToCollectionIfMissing(servicesCollection, services);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(services);
      });

      it('should add only unique Services to an array', () => {
        const servicesArray: IServices[] = [{ id: 123 }, { id: 456 }, { id: 50283 }];
        const servicesCollection: IServices[] = [{ id: 123 }];
        expectedResult = service.addServicesToCollectionIfMissing(servicesCollection, ...servicesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const services: IServices = { id: 123 };
        const services2: IServices = { id: 456 };
        expectedResult = service.addServicesToCollectionIfMissing([], services, services2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(services);
        expect(expectedResult).toContain(services2);
      });

      it('should accept null and undefined values', () => {
        const services: IServices = { id: 123 };
        expectedResult = service.addServicesToCollectionIfMissing([], null, services, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(services);
      });

      it('should return initial array if no Services is added', () => {
        const servicesCollection: IServices[] = [{ id: 123 }];
        expectedResult = service.addServicesToCollectionIfMissing(servicesCollection, undefined, null);
        expect(expectedResult).toEqual(servicesCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
