import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {IJobApplications} from '../job-applications.model';
import {DataUtils} from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-job-applications-detail',
  templateUrl: './job-applications-detail.component.html',
})
export class JobApplicationsDetailComponent implements OnInit {
  jobApplications: IJobApplications | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ jobApplications }) => {
      this.jobApplications = jobApplications;
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
