import {Component, ElementRef, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {finalize} from 'rxjs/operators';

import {IServices, Services} from '../services.model';
import {ServicesService} from '../service/services.service';
import {AlertError} from 'app/shared/alert/alert-error.model';
import {EventManager, EventWithContent} from 'app/core/util/event-manager.service';
import {DataUtils, FileLoadError} from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-services-update',
  templateUrl: './services-update.component.html',
})
export class ServicesUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    title: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
    description: [null, [Validators.required]],
    order: [null, [Validators.required]],
    icon: [null, [Validators.required]],
    iconContentType: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected servicesService: ServicesService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ services }) => {
      this.updateForm(services);
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
    const services = this.createFromForm();
    if (services.id !== undefined) {
      this.subscribeToSaveResponse(this.servicesService.update(services));
    } else {
      this.subscribeToSaveResponse(this.servicesService.create(services));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IServices>>): void {
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

  protected updateForm(services: IServices): void {
    this.editForm.patchValue({
      id: services.id,
      title: services.title,
      description: services.description,
      order: services.order,
      icon: services.icon,
      iconContentType: services.iconContentType,
    });
  }

  protected createFromForm(): IServices {
    return {
      ...new Services(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      description: this.editForm.get(['description'])!.value,
      order: this.editForm.get(['order'])!.value,
      iconContentType: this.editForm.get(['iconContentType'])!.value,
      icon: this.editForm.get(['icon'])!.value,
    };
  }
}
