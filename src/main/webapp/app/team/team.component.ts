import {Component, OnInit} from '@angular/core';
import {HttpResponse} from "@angular/common/http";
import {IEmployees} from "../entities/employees/employees.model";
import {EmployeesService} from "../entities/employees/service/employees.service";

@Component({
  selector: 'jhi-team',
  templateUrl: './team.component.html',
  styleUrls: ['./team.component.scss']
})
export class TeamComponent  implements OnInit {
  teamMembers: IEmployees[] | null = [];

  constructor(
    private employeesService: EmployeesService
  ) {
  }

  ngOnInit(): void {
    this.employeesService.query()
      .subscribe({
        next: (res: HttpResponse<IEmployees[]>) => {
          this.teamMembers = res.body?.sort((a, b) => (a.order && b.order) ? a.order - b.order : 0) ?? [];
        }
      });
  }

  getFragmentedList(array: any[] | null): (any[])[] {
    const result: (any[])[] = [];
    if (array) {
      const chunk = 3;
      for (let i = 0; i < array.length; i += chunk) {
        result.push(array.slice(i, i + chunk));
      }
    }
    return result;
  }
}
