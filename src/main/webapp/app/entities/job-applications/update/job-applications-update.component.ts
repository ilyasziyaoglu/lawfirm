import {Component, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {finalize} from 'rxjs/operators';

import {IJobApplications, JobApplications} from '../job-applications.model';
import {JobApplicationsService} from '../service/job-applications.service';
import {AlertError} from 'app/shared/alert/alert-error.model';
import {EventManager, EventWithContent} from 'app/core/util/event-manager.service';
import {DataUtils, FileLoadError} from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-job-applications-update',
  templateUrl: './job-applications-update.component.html',
})
export class JobApplicationsUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
    surname: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
    email: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
    phone: [null, [Validators.required, Validators.minLength(7), Validators.maxLength(14)]],
    area: [null, [Validators.required, Validators.maxLength(100)]],
    message: [],
    cv: [null, [Validators.required]],
    cvContentType: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected jobApplicationsService: JobApplicationsService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ jobApplications }) => {
      this.updateForm(jobApplications);
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

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const jobApplications = this.createFromForm();
    if (jobApplications.id !== undefined) {
      this.subscribeToSaveResponse(this.jobApplicationsService.update(jobApplications));
    } else {
      this.subscribeToSaveResponse(this.jobApplicationsService.create(jobApplications));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IJobApplications>>): void {
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

  protected updateForm(jobApplications: IJobApplications): void {
    this.editForm.patchValue({
      id: jobApplications.id,
      name: jobApplications.name,
      surname: jobApplications.surname,
      email: jobApplications.email,
      phone: jobApplications.phone,
      area: jobApplications.area,
      message: jobApplications.message,
      cv: jobApplications.cv,
      cvContentType: jobApplications.cvContentType,
    });
  }

  protected createFromForm(): IJobApplications {
    return {
      ...new JobApplications(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      surname: this.editForm.get(['surname'])!.value,
      email: this.editForm.get(['email'])!.value,
      phone: this.editForm.get(['phone'])!.value,
      area: this.editForm.get(['area'])!.value,
      message: this.editForm.get(['message'])!.value,
      cvContentType: this.editForm.get(['cvContentType'])!.value,
      cv: this.editForm.get(['cv'])!.value,
    };
  }
}
