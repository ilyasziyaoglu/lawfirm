import {Component, ElementRef, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {finalize, map} from 'rxjs/operators';

import {Employees, IEmployees} from '../employees.model';
import {EmployeesService} from '../service/employees.service';
import {AlertError} from 'app/shared/alert/alert-error.model';
import {EventManager, EventWithContent} from 'app/core/util/event-manager.service';
import {DataUtils, FileLoadError} from 'app/core/util/data-util.service';
import {IServicePoints} from 'app/entities/service-points/service-points.model';
import {ServicePointsService} from 'app/entities/service-points/service/service-points.service';
import {IServices} from 'app/entities/services/services.model';
import {ServicesService} from 'app/entities/services/service/services.service';
import {IMAGE_PATH} from 'app/app.constants';

@Component({
  selector: 'jhi-employees-update',
  templateUrl: './employees-update.component.html',
})
export class EmployeesUpdateComponent implements OnInit {
  IMAGE_PATH = IMAGE_PATH;
  isSaving = false;

  servicePointsSharedCollection: IServicePoints[] = [];
  servicesSharedCollection: IServices[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
    surname: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
    title: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
    story: [],
    order: [null, [Validators.required]],
    imageName: [null, [Validators.required]],
    image: [null, [Validators.required]],
    imageContentType: [],
    servicePoint: [],
    services: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected employeesService: EmployeesService,
    protected servicePointsService: ServicePointsService,
    protected servicesService: ServicesService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ employees }) => {
      this.updateForm(employees);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('lawfirmApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const employees = this.createFromForm();
    if (employees.id !== undefined) {
      this.subscribeToSaveResponse(this.employeesService.update(employees));
    } else {
      this.subscribeToSaveResponse(this.employeesService.create(employees));
    }
  }

  trackServicePointsById(index: number, item: IServicePoints): number {
    return item.id!;
  }

  trackServicesById(index: number, item: IServices): number {
    return item.id!;
  }

  getSelectedServices(option: IServices, selectedVals?: IServices[]): IServices {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEmployees>>): void {
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

  protected updateForm(employees: IEmployees): void {
    this.editForm.patchValue({
      id: employees.id,
      name: employees.name,
      surname: employees.surname,
      title: employees.title,
      story: employees.story,
      order: employees.order,
      imageName: employees.imageName,
      image: employees.image,
      imageContentType: employees.imageContentType,
      servicePoint: employees.servicePoint,
      services: employees.services,
    });

    this.servicePointsSharedCollection = this.servicePointsService.addServicePointsToCollectionIfMissing(
      this.servicePointsSharedCollection,
      employees.servicePoint
    );
    this.servicesSharedCollection = this.servicesService.addServicesToCollectionIfMissing(
      this.servicesSharedCollection,
      ...(employees.services ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.servicePointsService
      .query()
      .pipe(map((res: HttpResponse<IServicePoints[]>) => res.body ?? []))
      .pipe(
        map((servicePoints: IServicePoints[]) =>
          this.servicePointsService.addServicePointsToCollectionIfMissing(servicePoints, this.editForm.get('servicePoint')!.value)
        )
      )
      .subscribe((servicePoints: IServicePoints[]) => (this.servicePointsSharedCollection = servicePoints));

    this.servicesService
      .query()
      .pipe(map((res: HttpResponse<IServices[]>) => res.body ?? []))
      .pipe(
        map((services: IServices[]) =>
          this.servicesService.addServicesToCollectionIfMissing(services, ...(this.editForm.get('services')!.value ?? []))
        )
      )
      .subscribe((services: IServices[]) => (this.servicesSharedCollection = services));
  }

  protected createFromForm(): IEmployees {
    return {
      ...new Employees(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      surname: this.editForm.get(['surname'])!.value,
      title: this.editForm.get(['title'])!.value,
      story: this.editForm.get(['story'])!.value,
      order: this.editForm.get(['order'])!.value,
      imageName: this.editForm.get(['imageName'])!.value,
      imageContentType: this.editForm.get(['imageContentType'])!.value,
      image: this.editForm.get(['image'])!.value,
      servicePoint: this.editForm.get(['servicePoint'])!.value,
      services: this.editForm.get(['services'])!.value,
    };
  }
}
