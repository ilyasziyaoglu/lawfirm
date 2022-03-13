import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {IConfigs} from '../configs.model';
import {ConfigsService} from '../service/configs.service';

@Component({
  templateUrl: './configs-delete-dialog.component.html',
})
export class ConfigsDeleteDialogComponent {
  configs?: IConfigs;

  constructor(protected configsService: ConfigsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.configsService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
