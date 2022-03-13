import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {IConfigs} from '../configs.model';

@Component({
  selector: 'jhi-configs-detail',
  templateUrl: './configs-detail.component.html',
})
export class ConfigsDetailComponent implements OnInit {
  configs: IConfigs | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ configs }) => {
      this.configs = configs;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
