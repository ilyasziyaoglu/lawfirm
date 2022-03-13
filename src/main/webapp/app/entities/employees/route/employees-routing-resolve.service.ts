import {Injectable} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {ActivatedRouteSnapshot, Resolve, Router} from '@angular/router';
import {EMPTY, Observable, of} from 'rxjs';
import {mergeMap} from 'rxjs/operators';

import {Employees, IEmployees} from '../employees.model';
import {EmployeesService} from '../service/employees.service';

@Injectable({ providedIn: 'root' })
export class EmployeesRoutingResolveService implements Resolve<IEmployees> {
  constructor(protected service: EmployeesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEmployees> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((employees: HttpResponse<Employees>) => {
          if (employees.body) {
            return of(employees.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Employees());
  }
}
