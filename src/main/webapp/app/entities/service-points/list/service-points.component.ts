import {Component, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

import {IServicePoints} from '../service-points.model';
import {ServicePointsService} from '../service/service-points.service';
import {ServicePointsDeleteDialogComponent} from '../delete/service-points-delete-dialog.component';

@Component({
  selector: 'jhi-service-points',
  templateUrl: './service-points.component.html',
})
export class ServicePointsComponent implements OnInit {
  servicePoints?: IServicePoints[];
  isLoading = false;

  constructor(protected servicePointsService: ServicePointsService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.servicePointsService.query().subscribe({
      next: (res: HttpResponse<IServicePoints[]>) => {
        this.isLoading = false;
        this.servicePoints = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IServicePoints): number {
    return item.id!;
  }

  delete(servicePoints: IServicePoints): void {
    const modalRef = this.modalService.open(ServicePointsDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.servicePoints = servicePoints;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
