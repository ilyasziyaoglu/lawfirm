import {Injectable} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {ActivatedRouteSnapshot, Resolve, Router} from '@angular/router';
import {EMPTY, Observable, of} from 'rxjs';
import {mergeMap} from 'rxjs/operators';

import {IServices, Services} from '../services.model';
import {ServicesService} from '../service/services.service';

@Injectable({ providedIn: 'root' })
export class ServicesRoutingResolveService implements Resolve<IServices> {
  constructor(protected service: ServicesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IServices> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((services: HttpResponse<Services>) => {
          if (services.body) {
            return of(services.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Services());
  }
}
