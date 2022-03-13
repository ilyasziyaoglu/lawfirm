import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {isPresent} from 'app/core/util/operators';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {createRequestOption} from 'app/core/request/request-util';
import {getServicesIdentifier, IServices} from '../services.model';

export type EntityResponseType = HttpResponse<IServices>;
export type EntityArrayResponseType = HttpResponse<IServices[]>;

@Injectable({ providedIn: 'root' })
export class ServicesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/services');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(services: IServices): Observable<EntityResponseType> {
    return this.http.post<IServices>(this.resourceUrl, services, { observe: 'response' });
  }

  update(services: IServices): Observable<EntityResponseType> {
    return this.http.put<IServices>(`${this.resourceUrl}/${getServicesIdentifier(services) as number}`, services, { observe: 'response' });
  }

  partialUpdate(services: IServices): Observable<EntityResponseType> {
    return this.http.patch<IServices>(`${this.resourceUrl}/${getServicesIdentifier(services) as number}`, services, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IServices>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IServices[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addServicesToCollectionIfMissing(servicesCollection: IServices[], ...servicesToCheck: (IServices | null | undefined)[]): IServices[] {
    const services: IServices[] = servicesToCheck.filter(isPresent);
    if (services.length > 0) {
      const servicesCollectionIdentifiers = servicesCollection.map(servicesItem => getServicesIdentifier(servicesItem)!);
      const servicesToAdd = services.filter(servicesItem => {
        const servicesIdentifier = getServicesIdentifier(servicesItem);
        if (servicesIdentifier == null || servicesCollectionIdentifiers.includes(servicesIdentifier)) {
          return false;
        }
        servicesCollectionIdentifiers.push(servicesIdentifier);
        return true;
      });
      return [...servicesToAdd, ...servicesCollection];
    }
    return servicesCollection;
  }
}
