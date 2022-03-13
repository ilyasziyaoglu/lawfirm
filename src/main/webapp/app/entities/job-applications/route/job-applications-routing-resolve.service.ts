import {Injectable} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {ActivatedRouteSnapshot, Resolve, Router} from '@angular/router';
import {EMPTY, Observable, of} from 'rxjs';
import {mergeMap} from 'rxjs/operators';

import {IJobApplications, JobApplications} from '../job-applications.model';
import {JobApplicationsService} from '../service/job-applications.service';

@Injectable({ providedIn: 'root' })
export class JobApplicationsRoutingResolveService implements Resolve<IJobApplications> {
  constructor(protected service: JobApplicationsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IJobApplications> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((jobApplications: HttpResponse<JobApplications>) => {
          if (jobApplications.body) {
            return of(jobApplications.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new JobApplications());
  }
}
