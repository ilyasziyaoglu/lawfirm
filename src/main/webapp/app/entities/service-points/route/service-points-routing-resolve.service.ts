import {Injectable} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {ActivatedRouteSnapshot, Resolve, Router} from '@angular/router';
import {EMPTY, Observable, of} from 'rxjs';
import {mergeMap} from 'rxjs/operators';

import {IServicePoints, ServicePoints} from '../service-points.model';
import {ServicePointsService} from '../service/service-points.service';

@Injectable({ providedIn: 'root' })
export class ServicePointsRoutingResolveService implements Resolve<IServicePoints> {
  constructor(protected service: ServicePointsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IServicePoints> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((servicePoints: HttpResponse<ServicePoints>) => {
          if (servicePoints.body) {
            return of(servicePoints.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new ServicePoints());
  }
}
