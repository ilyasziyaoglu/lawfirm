import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {IConfig} from '../config.model';
import {ConfigService} from '../service/config.service';

@Component({
  templateUrl: './config-delete-dialog.component.html',
})
export class ConfigDeleteDialogComponent {
  config?: IConfig;

  constructor(protected configService: ConfigService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.configService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
