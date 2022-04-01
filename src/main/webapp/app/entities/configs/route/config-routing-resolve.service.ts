import {Injectable} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {ActivatedRouteSnapshot, Resolve, Router} from '@angular/router';
import {EMPTY, Observable, of} from 'rxjs';
import {mergeMap} from 'rxjs/operators';

import {Config, IConfig} from '../config.model';
import {ConfigService} from '../service/config.service';

@Injectable({ providedIn: 'root' })
export class ConfigRoutingResolveService implements Resolve<IConfig> {
  constructor(protected service: ConfigService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IConfig> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((config: HttpResponse<Config>) => {
          if (config.body) {
            return of(config.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Config());
  }
}
