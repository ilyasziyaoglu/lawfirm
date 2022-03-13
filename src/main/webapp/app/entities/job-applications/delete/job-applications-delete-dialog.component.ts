import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {IJobApplications} from '../job-applications.model';
import {JobApplicationsService} from '../service/job-applications.service';

@Component({
  templateUrl: './job-applications-delete-dialog.component.html',
})
export class JobApplicationsDeleteDialogComponent {
  jobApplications?: IJobApplications;

  constructor(protected jobApplicationsService: JobApplicationsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.jobApplicationsService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
