import {Component, OnInit} from '@angular/core';
import {HttpHeaders, HttpResponse} from '@angular/common/http';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

import {IConfigs} from '../configs.model';

import {ASC, DESC, ITEMS_PER_PAGE} from 'app/config/pagination.constants';
import {ConfigsService} from '../service/configs.service';
import {ConfigsDeleteDialogComponent} from '../delete/configs-delete-dialog.component';
import {ParseLinks} from 'app/core/util/parse-links.service';

@Component({
  selector: 'jhi-configs',
  templateUrl: './configs.component.html',
})
export class ConfigsComponent implements OnInit {
  configs: IConfigs[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(protected configsService: ConfigsService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.configs = [];
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

    this.configsService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<IConfigs[]>) => {
          this.isLoading = false;
          this.paginateConfigs(res.body, res.headers);
        },
        error: () => {
          this.isLoading = false;
        },
      });
  }

  reset(): void {
    this.page = 0;
    this.configs = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IConfigs): number {
    return item.id!;
  }

  delete(configs: IConfigs): void {
    const modalRef = this.modalService.open(ConfigsDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.configs = configs;
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

  protected paginateConfigs(data: IConfigs[] | null, headers: HttpHeaders): void {
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
        this.configs.push(d);
      }
    }
  }
}