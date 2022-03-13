import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {IJobApplications, JobApplications} from '../job-applications.model';

import {JobApplicationsService} from './job-applications.service';

describe('JobApplications Service', () => {
  let service: JobApplicationsService;
  let httpMock: HttpTestingController;
  let elemDefault: IJobApplications;
  let expectedResult: IJobApplications | IJobApplications[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(JobApplicationsService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      surname: 'AAAAAAA',
      email: 'AAAAAAA',
      phone: 'AAAAAAA',
      area: 'AAAAAAA',
      message: 'AAAAAAA',
      cvContentType: 'image/png',
      cv: 'AAAAAAA',
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

    it('should create a JobApplications', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new JobApplications()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a JobApplications', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          surname: 'BBBBBB',
          email: 'BBBBBB',
          phone: 'BBBBBB',
          area: 'BBBBBB',
          message: 'BBBBBB',
          cv: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a JobApplications', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          email: 'BBBBBB',
          area: 'BBBBBB',
          message: 'BBBBBB',
        },
        new JobApplications()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of JobApplications', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          surname: 'BBBBBB',
          email: 'BBBBBB',
          phone: 'BBBBBB',
          area: 'BBBBBB',
          message: 'BBBBBB',
          cv: 'BBBBBB',
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

    it('should delete a JobApplications', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addJobApplicationsToCollectionIfMissing', () => {
      it('should add a JobApplications to an empty array', () => {
        const jobApplications: IJobApplications = { id: 123 };
        expectedResult = service.addJobApplicationsToCollectionIfMissing([], jobApplications);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(jobApplications);
      });

      it('should not add a JobApplications to an array that contains it', () => {
        const jobApplications: IJobApplications = { id: 123 };
        const jobApplicationsCollection: IJobApplications[] = [
          {
            ...jobApplications,
          },
          { id: 456 },
        ];
        expectedResult = service.addJobApplicationsToCollectionIfMissing(jobApplicationsCollection, jobApplications);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a JobApplications to an array that doesn't contain it", () => {
        const jobApplications: IJobApplications = { id: 123 };
        const jobApplicationsCollection: IJobApplications[] = [{ id: 456 }];
        expectedResult = service.addJobApplicationsToCollectionIfMissing(jobApplicationsCollection, jobApplications);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(jobApplications);
      });

      it('should add only unique JobApplications to an array', () => {
        const jobApplicationsArray: IJobApplications[] = [{ id: 123 }, { id: 456 }, { id: 55107 }];
        const jobApplicationsCollection: IJobApplications[] = [{ id: 123 }];
        expectedResult = service.addJobApplicationsToCollectionIfMissing(jobApplicationsCollection, ...jobApplicationsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const jobApplications: IJobApplications = { id: 123 };
        const jobApplications2: IJobApplications = { id: 456 };
        expectedResult = service.addJobApplicationsToCollectionIfMissing([], jobApplications, jobApplications2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(jobApplications);
        expect(expectedResult).toContain(jobApplications2);
      });

      it('should accept null and undefined values', () => {
        const jobApplications: IJobApplications = { id: 123 };
        expectedResult = service.addJobApplicationsToCollectionIfMissing([], null, jobApplications, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(jobApplications);
      });

      it('should return initial array if no JobApplications is added', () => {
        const jobApplicationsCollection: IJobApplications[] = [{ id: 123 }];
        expectedResult = service.addJobApplicationsToCollectionIfMissing(jobApplicationsCollection, undefined, null);
        expect(expectedResult).toEqual(jobApplicationsCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
