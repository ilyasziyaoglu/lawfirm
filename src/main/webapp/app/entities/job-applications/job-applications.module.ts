import {NgModule} from '@angular/core';
import {SharedModule} from 'app/shared/shared.module';
import {JobApplicationsComponent} from './list/job-applications.component';
import {JobApplicationsDetailComponent} from './detail/job-applications-detail.component';
import {JobApplicationsUpdateComponent} from './update/job-applications-update.component';
import {JobApplicationsDeleteDialogComponent} from './delete/job-applications-delete-dialog.component';
import {JobApplicationsRoutingModule} from './route/job-applications-routing.module';

@NgModule({
  imports: [SharedModule, JobApplicationsRoutingModule],
  declarations: [
    JobApplicationsComponent,
    JobApplicationsDetailComponent,
    JobApplicationsUpdateComponent,
    JobApplicationsDeleteDialogComponent,
  ],
  entryComponents: [JobApplicationsDeleteDialogComponent],
})
export class JobApplicationsModule {}
