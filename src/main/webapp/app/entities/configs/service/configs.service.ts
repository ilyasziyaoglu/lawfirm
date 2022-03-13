import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {isPresent} from 'app/core/util/operators';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {createRequestOption} from 'app/core/request/request-util';
import {getConfigsIdentifier, IConfigs} from '../configs.model';

export type EntityResponseType = HttpResponse<IConfigs>;
export type EntityArrayResponseType = HttpResponse<IConfigs[]>;

@Injectable({ providedIn: 'root' })
export class ConfigsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/configs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(configs: IConfigs): Observable<EntityResponseType> {
    return this.http.post<IConfigs>(this.resourceUrl, configs, { observe: 'response' });
  }

  update(configs: IConfigs): Observable<EntityResponseType> {
    return this.http.put<IConfigs>(`${this.resourceUrl}/${getConfigsIdentifier(configs) as number}`, configs, { observe: 'response' });
  }

  partialUpdate(configs: IConfigs): Observable<EntityResponseType> {
    return this.http.patch<IConfigs>(`${this.resourceUrl}/${getConfigsIdentifier(configs) as number}`, configs, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IConfigs>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IConfigs[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addConfigsToCollectionIfMissing(configsCollection: IConfigs[], ...configsToCheck: (IConfigs | null | undefined)[]): IConfigs[] {
    const configs: IConfigs[] = configsToCheck.filter(isPresent);
    if (configs.length > 0) {
      const configsCollectionIdentifiers = configsCollection.map(configsItem => getConfigsIdentifier(configsItem)!);
      const configsToAdd = configs.filter(configsItem => {
        const configsIdentifier = getConfigsIdentifier(configsItem);
        if (configsIdentifier == null || configsCollectionIdentifiers.includes(configsIdentifier)) {
          return false;
        }
        configsCollectionIdentifiers.push(configsIdentifier);
        return true;
      });
      return [...configsToAdd, ...configsCollection];
    }
    return configsCollection;
  }
}
