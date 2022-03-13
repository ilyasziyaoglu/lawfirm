import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {IServicePoints, ServicePoints} from '../service-points.model';

import {ServicePointsService} from './service-points.service';

describe('ServicePoints Service', () => {
  let service: ServicePointsService;
  let httpMock: HttpTestingController;
  let elemDefault: IServicePoints;
  let expectedResult: IServicePoints | IServicePoints[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ServicePointsService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      email: 'AAAAAAA',
      phone: 'AAAAAAA',
      address: 'AAAAAAA',
      mapUrl: 'AAAAAAA',
      latitude: 0,
      longitude: 0,
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

    it('should create a ServicePoints', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new ServicePoints()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ServicePoints', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          email: 'BBBBBB',
          phone: 'BBBBBB',
          address: 'BBBBBB',
          mapUrl: 'BBBBBB',
          latitude: 1,
          longitude: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ServicePoints', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          phone: 'BBBBBB',
          address: 'BBBBBB',
          mapUrl: 'BBBBBB',
          latitude: 1,
        },
        new ServicePoints()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ServicePoints', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          email: 'BBBBBB',
          phone: 'BBBBBB',
          address: 'BBBBBB',
          mapUrl: 'BBBBBB',
          latitude: 1,
          longitude: 1,
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

    it('should delete a ServicePoints', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addServicePointsToCollectionIfMissing', () => {
      it('should add a ServicePoints to an empty array', () => {
        const servicePoints: IServicePoints = { id: 123 };
        expectedResult = service.addServicePointsToCollectionIfMissing([], servicePoints);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(servicePoints);
      });

      it('should not add a ServicePoints to an array that contains it', () => {
        const servicePoints: IServicePoints = { id: 123 };
        const servicePointsCollection: IServicePoints[] = [
          {
            ...servicePoints,
          },
          { id: 456 },
        ];
        expectedResult = service.addServicePointsToCollectionIfMissing(servicePointsCollection, servicePoints);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ServicePoints to an array that doesn't contain it", () => {
        const servicePoints: IServicePoints = { id: 123 };
        const servicePointsCollection: IServicePoints[] = [{ id: 456 }];
        expectedResult = service.addServicePointsToCollectionIfMissing(servicePointsCollection, servicePoints);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(servicePoints);
      });

      it('should add only unique ServicePoints to an array', () => {
        const servicePointsArray: IServicePoints[] = [{ id: 123 }, { id: 456 }, { id: 9849 }];
        const servicePointsCollection: IServicePoints[] = [{ id: 123 }];
        expectedResult = service.addServicePointsToCollectionIfMissing(servicePointsCollection, ...servicePointsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const servicePoints: IServicePoints = { id: 123 };
        const servicePoints2: IServicePoints = { id: 456 };
        expectedResult = service.addServicePointsToCollectionIfMissing([], servicePoints, servicePoints2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(servicePoints);
        expect(expectedResult).toContain(servicePoints2);
      });

      it('should accept null and undefined values', () => {
        const servicePoints: IServicePoints = { id: 123 };
        expectedResult = service.addServicePointsToCollectionIfMissing([], null, servicePoints, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(servicePoints);
      });

      it('should return initial array if no ServicePoints is added', () => {
        const servicePointsCollection: IServicePoints[] = [{ id: 123 }];
        expectedResult = service.addServicePointsToCollectionIfMissing(servicePointsCollection, undefined, null);
        expect(expectedResult).toEqual(servicePointsCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
