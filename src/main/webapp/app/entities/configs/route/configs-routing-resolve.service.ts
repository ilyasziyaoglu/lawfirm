import {Injectable} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {ActivatedRouteSnapshot, Resolve, Router} from '@angular/router';
import {EMPTY, Observable, of} from 'rxjs';
import {mergeMap} from 'rxjs/operators';

import {Configs, IConfigs} from '../configs.model';
import {ConfigsService} from '../service/configs.service';

@Injectable({ providedIn: 'root' })
export class ConfigsRoutingResolveService implements Resolve<IConfigs> {
  constructor(protected service: ConfigsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IConfigs> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((configs: HttpResponse<Configs>) => {
          if (configs.body) {
            return of(configs.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Configs());
  }
}
