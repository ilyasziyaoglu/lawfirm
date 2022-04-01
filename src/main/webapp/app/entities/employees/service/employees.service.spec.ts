import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {Employees, IEmployees} from '../employees.model';

import {EmployeesService} from './employees.service';

describe('Employees Service', () => {
  let service: EmployeesService;
  let httpMock: HttpTestingController;
  let elemDefault: IEmployees;
  let expectedResult: IEmployees | IEmployees[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EmployeesService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      surname: 'AAAAAAA',
      title: 'AAAAAAA',
      story: 'AAAAAAA',
      order: 0,
      imageName: 'AAAAAAA',
      imageContentType: 'image/png',
      image: 'AAAAAAA',
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

    it('should create a Employees', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Employees()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Employees', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          surname: 'BBBBBB',
          title: 'BBBBBB',
          story: 'BBBBBB',
          order: 1,
          imageName: 'BBBBBB',
          image: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Employees', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          surname: 'BBBBBB',
          title: 'BBBBBB',
          imageName: 'BBBBBB',
        },
        new Employees()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Employees', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          surname: 'BBBBBB',
          title: 'BBBBBB',
          story: 'BBBBBB',
          order: 1,
          imageName: 'BBBBBB',
          image: 'BBBBBB',
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

    it('should delete a Employees', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addEmployeesToCollectionIfMissing', () => {
      it('should add a Employees to an empty array', () => {
        const employees: IEmployees = { id: 123 };
        expectedResult = service.addEmployeesToCollectionIfMissing([], employees);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(employees);
      });

      it('should not add a Employees to an array that contains it', () => {
        const employees: IEmployees = { id: 123 };
        const employeesCollection: IEmployees[] = [
          {
            ...employees,
          },
          { id: 456 },
        ];
        expectedResult = service.addEmployeesToCollectionIfMissing(employeesCollection, employees);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Employees to an array that doesn't contain it", () => {
        const employees: IEmployees = { id: 123 };
        const employeesCollection: IEmployees[] = [{ id: 456 }];
        expectedResult = service.addEmployeesToCollectionIfMissing(employeesCollection, employees);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(employees);
      });

      it('should add only unique Employees to an array', () => {
        const employeesArray: IEmployees[] = [{ id: 123 }, { id: 456 }, { id: 58821 }];
        const employeesCollection: IEmployees[] = [{ id: 123 }];
        expectedResult = service.addEmployeesToCollectionIfMissing(employeesCollection, ...employeesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const employees: IEmployees = { id: 123 };
        const employees2: IEmployees = { id: 456 };
        expectedResult = service.addEmployeesToCollectionIfMissing([], employees, employees2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(employees);
        expect(expectedResult).toContain(employees2);
      });

      it('should accept null and undefined values', () => {
        const employees: IEmployees = { id: 123 };
        expectedResult = service.addEmployeesToCollectionIfMissing([], null, employees, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(employees);
      });

      it('should return initial array if no Employees is added', () => {
        const employeesCollection: IEmployees[] = [{ id: 123 }];
        expectedResult = service.addEmployeesToCollectionIfMissing(employeesCollection, undefined, null);
        expect(expectedResult).toEqual(employeesCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
