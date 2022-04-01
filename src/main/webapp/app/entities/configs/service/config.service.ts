import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {isPresent} from 'app/core/util/operators';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {createRequestOption} from 'app/core/request/request-util';
import {getConfigIdentifier, IConfig} from '../config.model';

export type EntityResponseType = HttpResponse<IConfig>;
export type EntityArrayResponseType = HttpResponse<IConfig[]>;

@Injectable({ providedIn: 'root' })
export class ConfigService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/configs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(config: IConfig): Observable<EntityResponseType> {
    return this.http.post<IConfig>(this.resourceUrl, config, { observe: 'response' });
  }

  update(config: IConfig): Observable<EntityResponseType> {
    return this.http.put<IConfig>(`${this.resourceUrl}/${getConfigIdentifier(config) as number}`, config, { observe: 'response' });
  }

  partialUpdate(config: IConfig): Observable<EntityResponseType> {
    return this.http.patch<IConfig>(`${this.resourceUrl}/${getConfigIdentifier(config) as number}`, config, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IConfig>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IConfig[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addConfigsToCollectionIfMissing(configCollection: IConfig[], ...configsToCheck: (IConfig | null | undefined)[]): IConfig[] {
    const configs: IConfig[] = configsToCheck.filter(isPresent);
    if (configs.length > 0) {
      const configCollectionIdentifiers = configCollection.map(configItem => getConfigIdentifier(configItem)!);
      const configsToAdd = configs.filter(configItem => {
        const configIdentifier = getConfigIdentifier(configItem);
        if (configIdentifier == null || configCollectionIdentifiers.includes(configIdentifier)) {
          return false;
        }
        configCollectionIdentifiers.push(configIdentifier);
        return true;
      });
      return [...configsToAdd, ...configCollection];
    }
    return configCollection;
  }
}
