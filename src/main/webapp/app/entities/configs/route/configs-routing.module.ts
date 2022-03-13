import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access.service';
import {ConfigsComponent} from '../list/configs.component';
import {ConfigsDetailComponent} from '../detail/configs-detail.component';
import {ConfigsUpdateComponent} from '../update/configs-update.component';
import {ConfigsRoutingResolveService} from './configs-routing-resolve.service';

const configsRoute: Routes = [
  {
    path: '',
    component: ConfigsComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ConfigsDetailComponent,
    resolve: {
      configs: ConfigsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ConfigsUpdateComponent,
    resolve: {
      configs: ConfigsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ConfigsUpdateComponent,
    resolve: {
      configs: ConfigsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(configsRoute)],
  exports: [RouterModule],
})
export class ConfigsRoutingModule {}
