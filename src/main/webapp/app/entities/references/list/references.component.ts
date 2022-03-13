import {Component, OnInit} from '@angular/core';
import {HttpHeaders, HttpResponse} from '@angular/common/http';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

import {IReferences} from '../references.model';

import {ASC, DESC, ITEMS_PER_PAGE} from 'app/config/pagination.constants';
import {ReferencesService} from '../service/references.service';
import {ReferencesDeleteDialogComponent} from '../delete/references-delete-dialog.component';
import {DataUtils} from 'app/core/util/data-util.service';
import {ParseLinks} from 'app/core/util/parse-links.service';

@Component({
  selector: 'jhi-references',
  templateUrl: './references.component.html',
})
export class ReferencesComponent implements OnInit {
  references: IReferences[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(
    protected referencesService: ReferencesService,
    protected dataUtils: DataUtils,
    protected modalService: NgbModal,
    protected parseLinks: ParseLinks
  ) {
    this.references = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    this.isLoading = true;

    this.referencesService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<IReferences[]>) => {
          this.isLoading = false;
          this.paginateReferences(res.body, res.headers);
        },
        error: () => {
          this.isLoading = false;
        },
      });
  }

  reset(): void {
    this.page = 0;
    this.references = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IReferences): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(references: IReferences): void {
    const modalRef = this.modalService.open(ReferencesDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.references = references;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.reset();
      }
    });
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateReferences(data: IReferences[] | null, headers: HttpHeaders): void {
    const linkHeader = headers.get('link');
    if (linkHeader) {
      this.links = this.parseLinks.parse(linkHeader);
    } else {
      this.links = {
        last: 0,
      };
    }
    if (data) {
      for (const d of data) {
        this.references.push(d);
      }
    }
  }
}
