import {Component, OnInit} from '@angular/core';
import {ServicesService} from "../../entities/services/service/services.service";
import {IServices} from "../../entities/services/services.model";
import {HttpResponse} from "@angular/common/http";

@Component({
  selector: 'jhi-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {
  services: IServices[] | null = [];

  constructor(
    private servicesService: ServicesService
  ) {
  }

  ngOnInit(): void {
    this.servicesService.query().subscribe(
      (res: HttpResponse<IServices[]>) => {
        this.services = res.body;
      });
  }
}
