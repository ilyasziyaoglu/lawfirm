import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access.service';
import {EmployeesComponent} from '../list/employees.component';
import {EmployeesDetailComponent} from '../detail/employees-detail.component';
import {EmployeesUpdateComponent} from '../update/employees-update.component';
import {EmployeesRoutingResolveService} from './employees-routing-resolve.service';

const employeesRoute: Routes = [
  {
    path: '',
    component: EmployeesComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EmployeesDetailComponent,
    resolve: {
      employees: EmployeesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EmployeesUpdateComponent,
    resolve: {
      employees: EmployeesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EmployeesUpdateComponent,
    resolve: {
      employees: EmployeesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(employeesRoute)],
  exports: [RouterModule],
})
export class EmployeesRoutingModule {}
