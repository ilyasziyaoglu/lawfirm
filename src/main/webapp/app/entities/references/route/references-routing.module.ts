import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access.service';
import {ReferencesComponent} from '../list/references.component';
import {ReferencesDetailComponent} from '../detail/references-detail.component';
import {ReferencesUpdateComponent} from '../update/references-update.component';
import {ReferencesRoutingResolveService} from './references-routing-resolve.service';

const referencesRoute: Routes = [
  {
    path: '',
    component: ReferencesComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ReferencesDetailComponent,
    resolve: {
      references: ReferencesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ReferencesUpdateComponent,
    resolve: {
      references: ReferencesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ReferencesUpdateComponent,
    resolve: {
      references: ReferencesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(referencesRoute)],
  exports: [RouterModule],
})
export class ReferencesRoutingModule {}
