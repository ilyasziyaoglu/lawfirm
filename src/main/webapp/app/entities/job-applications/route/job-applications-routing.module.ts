import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access.service';
import {JobApplicationsComponent} from '../list/job-applications.component';
import {JobApplicationsDetailComponent} from '../detail/job-applications-detail.component';
import {JobApplicationsUpdateComponent} from '../update/job-applications-update.component';
import {JobApplicationsRoutingResolveService} from './job-applications-routing-resolve.service';

const jobApplicationsRoute: Routes = [
  {
    path: '',
    component: JobApplicationsComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: JobApplicationsDetailComponent,
    resolve: {
      jobApplications: JobApplicationsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: JobApplicationsUpdateComponent,
    resolve: {
      jobApplications: JobApplicationsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: JobApplicationsUpdateComponent,
    resolve: {
      jobApplications: JobApplicationsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(jobApplicationsRoute)],
  exports: [RouterModule],
})
export class JobApplicationsRoutingModule {}
