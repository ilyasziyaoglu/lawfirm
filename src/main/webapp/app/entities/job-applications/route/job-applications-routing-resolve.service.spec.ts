import {TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, Router} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {of} from 'rxjs';

import {IJobApplications, JobApplications} from '../job-applications.model';
import {JobApplicationsService} from '../service/job-applications.service';

import {JobApplicationsRoutingResolveService} from './job-applications-routing-resolve.service';

describe('JobApplications routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: JobApplicationsRoutingResolveService;
  let service: JobApplicationsService;
  let resultJobApplications: IJobApplications | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(JobApplicationsRoutingResolveService);
    service = TestBed.inject(JobApplicationsService);
    resultJobApplications = undefined;
  });

  describe('resolve', () => {
    it('should return IJobApplications returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultJobApplications = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultJobApplications).toEqual({ id: 123 });
    });

    it('should return new IJobApplications if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultJobApplications = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultJobApplications).toEqual(new JobApplications());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as JobApplications })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultJobApplications = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultJobApplications).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
