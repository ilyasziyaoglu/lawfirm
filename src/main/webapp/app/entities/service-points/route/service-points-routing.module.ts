import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access.service';
import {ServicePointsComponent} from '../list/service-points.component';
import {ServicePointsDetailComponent} from '../detail/service-points-detail.component';
import {ServicePointsUpdateComponent} from '../update/service-points-update.component';
import {ServicePointsRoutingResolveService} from './service-points-routing-resolve.service';

const servicePointsRoute: Routes = [
  {
    path: '',
    component: ServicePointsComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ServicePointsDetailComponent,
    resolve: {
      servicePoints: ServicePointsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ServicePointsUpdateComponent,
    resolve: {
      servicePoints: ServicePointsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ServicePointsUpdateComponent,
    resolve: {
      servicePoints: ServicePointsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(servicePointsRoute)],
  exports: [RouterModule],
})
export class ServicePointsRoutingModule {}
