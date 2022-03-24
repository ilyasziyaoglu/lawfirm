import {Component, OnInit} from '@angular/core';
import {IServices} from "../entities/services/services.model";
import {ServicesService} from "../entities/services/service/services.service";
import {HttpResponse} from "@angular/common/http";

@Component({
  selector: 'jhi-services',
  templateUrl: './services.component.html',
  styleUrls: ['./services.component.scss']
})
export class ServicesComponent implements OnInit {
  services: IServices[] | null = [];

  constructor(
    private servicesService: ServicesService,
  ) {}

  ngOnInit(): void {
    this.servicesService.query().subscribe(
      (res: HttpResponse<IServices[]>) => {
        this.services = res.body;
      },
    );
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
