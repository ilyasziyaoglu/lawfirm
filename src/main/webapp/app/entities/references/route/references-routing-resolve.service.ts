import {Injectable} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {ActivatedRouteSnapshot, Resolve, Router} from '@angular/router';
import {EMPTY, Observable, of} from 'rxjs';
import {mergeMap} from 'rxjs/operators';

import {IReferences, References} from '../references.model';
import {ReferencesService} from '../service/references.service';

@Injectable({ providedIn: 'root' })
export class ReferencesRoutingResolveService implements Resolve<IReferences> {
  constructor(protected service: ReferencesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IReferences> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((references: HttpResponse<References>) => {
          if (references.body) {
            return of(references.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new References());
  }
}
