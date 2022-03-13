import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {IServicePoints} from '../service-points.model';
import {ServicePointsService} from '../service/service-points.service';

@Component({
  templateUrl: './service-points-delete-dialog.component.html',
})
export class ServicePointsDeleteDialogComponent {
  servicePoints?: IServicePoints;

  constructor(protected servicePointsService: ServicePointsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.servicePointsService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
