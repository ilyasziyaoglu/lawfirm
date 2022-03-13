import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {IEmployees} from '../employees.model';
import {EmployeesService} from '../service/employees.service';

@Component({
  templateUrl: './employees-delete-dialog.component.html',
})
export class EmployeesDeleteDialogComponent {
  employees?: IEmployees;

  constructor(protected employeesService: EmployeesService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.employeesService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
