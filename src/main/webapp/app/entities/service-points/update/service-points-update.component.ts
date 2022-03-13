import {Component, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {finalize} from 'rxjs/operators';

import {IServicePoints, ServicePoints} from '../service-points.model';
import {ServicePointsService} from '../service/service-points.service';

@Component({
  selector: 'jhi-service-points-update',
  templateUrl: './service-points-update.component.html',
})
export class ServicePointsUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    email: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
    phone: [null, [Validators.required, Validators.minLength(7), Validators.maxLength(14)]],
    address: [null, [Validators.required]],
    mapUrl: [],
    latitude: [],
    longitude: [],
  });

  constructor(protected servicePointsService: ServicePointsService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ servicePoints }) => {
      this.updateForm(servicePoints);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const servicePoints = this.createFromForm();
    if (servicePoints.id !== undefined) {
      this.subscribeToSaveResponse(this.servicePointsService.update(servicePoints));
    } else {
      this.subscribeToSaveResponse(this.servicePointsService.create(servicePoints));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IServicePoints>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(servicePoints: IServicePoints): void {
    this.editForm.patchValue({
      id: servicePoints.id,
      name: servicePoints.name,
      email: servicePoints.email,
      phone: servicePoints.phone,
      address: servicePoints.address,
      mapUrl: servicePoints.mapUrl,
      latitude: servicePoints.latitude,
      longitude: servicePoints.longitude,
    });
  }

  protected createFromForm(): IServicePoints {
    return {
      ...new ServicePoints(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      email: this.editForm.get(['email'])!.value,
      phone: this.editForm.get(['phone'])!.value,
      address: this.editForm.get(['address'])!.value,
      mapUrl: this.editForm.get(['mapUrl'])!.value,
      latitude: this.editForm.get(['latitude'])!.value,
      longitude: this.editForm.get(['longitude'])!.value,
    };
  }
}
