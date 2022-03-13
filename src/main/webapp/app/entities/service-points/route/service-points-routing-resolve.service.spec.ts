import {TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, Router} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {of} from 'rxjs';

import {IServicePoints, ServicePoints} from '../service-points.model';
import {ServicePointsService} from '../service/service-points.service';

import {ServicePointsRoutingResolveService} from './service-points-routing-resolve.service';

describe('ServicePoints routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: ServicePointsRoutingResolveService;
  let service: ServicePointsService;
  let resultServicePoints: IServicePoints | undefined;

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
    routingResolveService = TestBed.inject(ServicePointsRoutingResolveService);
    service = TestBed.inject(ServicePointsService);
    resultServicePoints = undefined;
  });

  describe('resolve', () => {
    it('should return IServicePoints returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultServicePoints = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultServicePoints).toEqual({ id: 123 });
    });

    it('should return new IServicePoints if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultServicePoints = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultServicePoints).toEqual(new ServicePoints());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as ServicePoints })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultServicePoints = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultServicePoints).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
