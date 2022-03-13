import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {IServicePoints} from '../service-points.model';

@Component({
  selector: 'jhi-service-points-detail',
  templateUrl: './service-points-detail.component.html',
})
export class ServicePointsDetailComponent implements OnInit {
  servicePoints: IServicePoints | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ servicePoints }) => {
      this.servicePoints = servicePoints;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
