import {NgModule} from '@angular/core';
import {SharedModule} from 'app/shared/shared.module';
import {EmployeesComponent} from './list/employees.component';
import {EmployeesDetailComponent} from './detail/employees-detail.component';
import {EmployeesUpdateComponent} from './update/employees-update.component';
import {EmployeesDeleteDialogComponent} from './delete/employees-delete-dialog.component';
import {EmployeesRoutingModule} from './route/employees-routing.module';

@NgModule({
  imports: [SharedModule, EmployeesRoutingModule],
  declarations: [EmployeesComponent, EmployeesDetailComponent, EmployeesUpdateComponent, EmployeesDeleteDialogComponent],
  entryComponents: [EmployeesDeleteDialogComponent],
})
export class EmployeesModule {}
