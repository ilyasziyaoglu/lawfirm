import {Component, ElementRef, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {finalize} from 'rxjs/operators';

import {IReferences, References} from '../references.model';
import {ReferencesService} from '../service/references.service';
import {AlertError} from 'app/shared/alert/alert-error.model';
import {EventManager, EventWithContent} from 'app/core/util/event-manager.service';
import {DataUtils, FileLoadError} from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-references-update',
  templateUrl: './references-update.component.html',
})
export class ReferencesUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
    order: [null, [Validators.required]],
    image: [null, [Validators.required]],
    imageContentType: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected referencesService: ReferencesService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ references }) => {
      this.updateForm(references);
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
    const references = this.createFromForm();
    if (references.id !== undefined) {
      this.subscribeToSaveResponse(this.referencesService.update(references));
    } else {
      this.subscribeToSaveResponse(this.referencesService.create(references));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReferences>>): void {
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

  protected updateForm(references: IReferences): void {
    this.editForm.patchValue({
      id: references.id,
      name: references.name,
      order: references.order,
      image: references.image,
      imageContentType: references.imageContentType,
    });
  }

  protected createFromForm(): IReferences {
    return {
      ...new References(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      order: this.editForm.get(['order'])!.value,
      imageContentType: this.editForm.get(['imageContentType'])!.value,
      image: this.editForm.get(['image'])!.value,
    };
  }
}
