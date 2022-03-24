import {Component, OnInit} from '@angular/core';
import {IServices} from "../../entities/services/services.model";
import {ServicesService} from "../../entities/services/service/services.service";
import {HttpResponse} from "@angular/common/http";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'jhi-service-detail',
  templateUrl: './service-detail.component.html',
  styleUrls: ['./service-detail.component.scss']
})
export class ServiceDetailComponent implements OnInit {
  service: IServices | null | undefined;

  constructor(
    private route: ActivatedRoute,
    private servicesService: ServicesService,
  ) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.servicesService.find(params['id']).subscribe(
        (res: HttpResponse<IServices>) => {
          this.service = res.body;
        },
      );
    });
  }
}
