import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {IEmployees} from '../employees.model';
import {DataUtils} from 'app/core/util/data-util.service';
import {IMAGE_PATH} from "app/app.constants";

@Component({
  selector: 'jhi-employees-detail',
  templateUrl: './employees-detail.component.html',
})
export class EmployeesDetailComponent implements OnInit {
  IMAGE_PATH = IMAGE_PATH;
  employees: IEmployees | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ employees }) => {
      this.employees = employees;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
