import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {isPresent} from 'app/core/util/operators';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {createRequestOption} from 'app/core/request/request-util';
import {getReferencesIdentifier, IReferences} from '../references.model';

export type EntityResponseType = HttpResponse<IReferences>;
export type EntityArrayResponseType = HttpResponse<IReferences[]>;

@Injectable({ providedIn: 'root' })
export class ReferencesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/references');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(references: IReferences): Observable<EntityResponseType> {
    return this.http.post<IReferences>(this.resourceUrl, references, { observe: 'response' });
  }

  update(references: IReferences): Observable<EntityResponseType> {
    return this.http.put<IReferences>(`${this.resourceUrl}/${getReferencesIdentifier(references) as number}`, references, {
      observe: 'response',
    });
  }

  partialUpdate(references: IReferences): Observable<EntityResponseType> {
    return this.http.patch<IReferences>(`${this.resourceUrl}/${getReferencesIdentifier(references) as number}`, references, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IReferences>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IReferences[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addReferencesToCollectionIfMissing(
    referencesCollection: IReferences[],
    ...referencesToCheck: (IReferences | null | undefined)[]
  ): IReferences[] {
    const references: IReferences[] = referencesToCheck.filter(isPresent);
    if (references.length > 0) {
      const referencesCollectionIdentifiers = referencesCollection.map(referencesItem => getReferencesIdentifier(referencesItem)!);
      const referencesToAdd = references.filter(referencesItem => {
        const referencesIdentifier = getReferencesIdentifier(referencesItem);
        if (referencesIdentifier == null || referencesCollectionIdentifiers.includes(referencesIdentifier)) {
          return false;
        }
        referencesCollectionIdentifiers.push(referencesIdentifier);
        return true;
      });
      return [...referencesToAdd, ...referencesCollection];
    }
    return referencesCollection;
  }
}
