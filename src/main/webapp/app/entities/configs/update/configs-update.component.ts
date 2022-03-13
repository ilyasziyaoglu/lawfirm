import {Component, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {finalize} from 'rxjs/operators';

import {Configs, IConfigs} from '../configs.model';
import {ConfigsService} from '../service/configs.service';

@Component({
  selector: 'jhi-configs-update',
  templateUrl: './configs-update.component.html',
})
export class ConfigsUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    key: [null, [Validators.required]],
    value: [null, [Validators.required]],
  });

  constructor(protected configsService: ConfigsService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ configs }) => {
      this.updateForm(configs);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const configs = this.createFromForm();
    if (configs.id !== undefined) {
      this.subscribeToSaveResponse(this.configsService.update(configs));
    } else {
      this.subscribeToSaveResponse(this.configsService.create(configs));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IConfigs>>): void {
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

  protected updateForm(configs: IConfigs): void {
    this.editForm.patchValue({
      id: configs.id,
      key: configs.key,
      value: configs.value,
    });
  }

  protected createFromForm(): IConfigs {
    return {
      ...new Configs(),
      id: this.editForm.get(['id'])!.value,
      key: this.editForm.get(['key'])!.value,
      value: this.editForm.get(['value'])!.value,
    };
  }
}
