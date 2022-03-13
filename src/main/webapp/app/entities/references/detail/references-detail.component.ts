import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {IReferences} from '../references.model';
import {DataUtils} from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-references-detail',
  templateUrl: './references-detail.component.html',
})
export class ReferencesDetailComponent implements OnInit {
  references: IReferences | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ references }) => {
      this.references = references;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
