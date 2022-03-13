import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {isPresent} from 'app/core/util/operators';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {createRequestOption} from 'app/core/request/request-util';
import {getServicePointsIdentifier, IServicePoints} from '../service-points.model';

export type EntityResponseType = HttpResponse<IServicePoints>;
export type EntityArrayResponseType = HttpResponse<IServicePoints[]>;

@Injectable({ providedIn: 'root' })
export class ServicePointsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/service-points');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(servicePoints: IServicePoints): Observable<EntityResponseType> {
    return this.http.post<IServicePoints>(this.resourceUrl, servicePoints, { observe: 'response' });
  }

  update(servicePoints: IServicePoints): Observable<EntityResponseType> {
    return this.http.put<IServicePoints>(`${this.resourceUrl}/${getServicePointsIdentifier(servicePoints) as number}`, servicePoints, {
      observe: 'response',
    });
  }

  partialUpdate(servicePoints: IServicePoints): Observable<EntityResponseType> {
    return this.http.patch<IServicePoints>(`${this.resourceUrl}/${getServicePointsIdentifier(servicePoints) as number}`, servicePoints, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IServicePoints>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IServicePoints[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addServicePointsToCollectionIfMissing(
    servicePointsCollection: IServicePoints[],
    ...servicePointsToCheck: (IServicePoints | null | undefined)[]
  ): IServicePoints[] {
    const servicePoints: IServicePoints[] = servicePointsToCheck.filter(isPresent);
    if (servicePoints.length > 0) {
      const servicePointsCollectionIdentifiers = servicePointsCollection.map(
        servicePointsItem => getServicePointsIdentifier(servicePointsItem)!
      );
      const servicePointsToAdd = servicePoints.filter(servicePointsItem => {
        const servicePointsIdentifier = getServicePointsIdentifier(servicePointsItem);
        if (servicePointsIdentifier == null || servicePointsCollectionIdentifiers.includes(servicePointsIdentifier)) {
          return false;
        }
        servicePointsCollectionIdentifiers.push(servicePointsIdentifier);
        return true;
      });
      return [...servicePointsToAdd, ...servicePointsCollection];
    }
    return servicePointsCollection;
  }
}
