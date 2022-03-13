import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {isPresent} from 'app/core/util/operators';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {createRequestOption} from 'app/core/request/request-util';
import {getJobApplicationsIdentifier, IJobApplications} from '../job-applications.model';

export type EntityResponseType = HttpResponse<IJobApplications>;
export type EntityArrayResponseType = HttpResponse<IJobApplications[]>;

@Injectable({ providedIn: 'root' })
export class JobApplicationsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/job-applications');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(jobApplications: IJobApplications): Observable<EntityResponseType> {
    return this.http.post<IJobApplications>(this.resourceUrl, jobApplications, { observe: 'response' });
  }

  update(jobApplications: IJobApplications): Observable<EntityResponseType> {
    return this.http.put<IJobApplications>(
      `${this.resourceUrl}/${getJobApplicationsIdentifier(jobApplications) as number}`,
      jobApplications,
      { observe: 'response' }
    );
  }

  partialUpdate(jobApplications: IJobApplications): Observable<EntityResponseType> {
    return this.http.patch<IJobApplications>(
      `${this.resourceUrl}/${getJobApplicationsIdentifier(jobApplications) as number}`,
      jobApplications,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IJobApplications>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IJobApplications[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addJobApplicationsToCollectionIfMissing(
    jobApplicationsCollection: IJobApplications[],
    ...jobApplicationsToCheck: (IJobApplications | null | undefined)[]
  ): IJobApplications[] {
    const jobApplications: IJobApplications[] = jobApplicationsToCheck.filter(isPresent);
    if (jobApplications.length > 0) {
      const jobApplicationsCollectionIdentifiers = jobApplicationsCollection.map(
        jobApplicationsItem => getJobApplicationsIdentifier(jobApplicationsItem)!
      );
      const jobApplicationsToAdd = jobApplications.filter(jobApplicationsItem => {
        const jobApplicationsIdentifier = getJobApplicationsIdentifier(jobApplicationsItem);
        if (jobApplicationsIdentifier == null || jobApplicationsCollectionIdentifiers.includes(jobApplicationsIdentifier)) {
          return false;
        }
        jobApplicationsCollectionIdentifiers.push(jobApplicationsIdentifier);
        return true;
      });
      return [...jobApplicationsToAdd, ...jobApplicationsCollection];
    }
    return jobApplicationsCollection;
  }
}
