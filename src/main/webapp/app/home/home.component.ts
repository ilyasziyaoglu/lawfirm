import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';

import {AccountService} from 'app/core/auth/account.service';
import {Account} from 'app/core/auth/account.model';
import {ReferencesService} from "../entities/references/service/references.service";
import {HttpResponse} from "@angular/common/http";
import {IReferences} from "../entities/references/references.model";
import {IServices} from "../entities/services/services.model";
import {ServicesService} from "../entities/services/service/services.service";
import {IEmployees} from "../entities/employees/employees.model";
import {EmployeesService} from "../entities/employees/service/employees.service";
import {IMAGE_PATH} from "app/app.constants";

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  IMAGE_PATH = IMAGE_PATH;

  account: Account | null = null;
  references: IReferences[] | null = [];
  services: IServices[] | null = [];
  teamMembers: IEmployees[] | null = [];

  private readonly destroy$ = new Subject<void>();

  constructor(
    private accountService: AccountService,
    private router: Router,
    private referencesService: ReferencesService,
    private servicesService: ServicesService,
    private employeesService: EmployeesService
  ) {}

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));


    this.referencesService.query()
      .subscribe({
        next: (res: HttpResponse<IReferences[]>) => {
          this.references = res.body?.sort((a, b) => (a.order && b.order) ? a.order - b.order : 0) ?? [];;
        }
      });

    this.servicesService.query()
      .subscribe({
        next: (res: HttpResponse<IServices[]>) => {
          this.services = res.body?.sort((a, b) => (a.order && b.order) ? a.order - b.order : 0) ?? [];
        }
      });

    this.employeesService.query()
      .subscribe({
        next: (res: HttpResponse<IEmployees[]>) => {
          this.teamMembers = res.body?.sort((a, b) => (a.order && b.order) ? a.order - b.order : 0) ?? [];
        }
      });
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  getFragmentedList(array: any[] | null): (any[])[] {
    const result: (any[])[] = [];
    if (array) {
      const chunk = 4;
      for (let i = 0; i < array.length; i += chunk) {
        result.push(array.slice(i, i + chunk));
      }
    }
    return result;
  }
}
