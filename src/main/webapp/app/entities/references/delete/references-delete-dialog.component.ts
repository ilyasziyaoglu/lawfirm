import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {IReferences} from '../references.model';
import {ReferencesService} from '../service/references.service';

@Component({
  templateUrl: './references-delete-dialog.component.html',
})
export class ReferencesDeleteDialogComponent {
  references?: IReferences;

  constructor(protected referencesService: ReferencesService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.referencesService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
