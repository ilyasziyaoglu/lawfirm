import {Component, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

import {IEmployees} from '../employees.model';
import {EmployeesService} from '../service/employees.service';
import {EmployeesDeleteDialogComponent} from '../delete/employees-delete-dialog.component';
import {DataUtils} from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-employees',
  templateUrl: './employees.component.html',
})
export class EmployeesComponent implements OnInit {
  employees?: IEmployees[];
  isLoading = false;

  constructor(protected employeesService: EmployeesService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.employeesService.query().subscribe({
      next: (res: HttpResponse<IEmployees[]>) => {
        this.isLoading = false;
        this.employees = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IEmployees): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(employees: IEmployees): void {
    const modalRef = this.modalService.open(EmployeesDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.employees = employees;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
