import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {isPresent} from 'app/core/util/operators';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {createRequestOption} from 'app/core/request/request-util';
import {getEmployeesIdentifier, IEmployees} from '../employees.model';

export type EntityResponseType = HttpResponse<IEmployees>;
export type EntityArrayResponseType = HttpResponse<IEmployees[]>;

@Injectable({ providedIn: 'root' })
export class EmployeesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/employees');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(employees: IEmployees): Observable<EntityResponseType> {
    return this.http.post<IEmployees>(this.resourceUrl, employees, { observe: 'response' });
  }

  update(employees: IEmployees): Observable<EntityResponseType> {
    return this.http.put<IEmployees>(`${this.resourceUrl}/${getEmployeesIdentifier(employees) as number}`, employees, {
      observe: 'response',
    });
  }

  partialUpdate(employees: IEmployees): Observable<EntityResponseType> {
    return this.http.patch<IEmployees>(`${this.resourceUrl}/${getEmployeesIdentifier(employees) as number}`, employees, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEmployees>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEmployees[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addEmployeesToCollectionIfMissing(
    employeesCollection: IEmployees[],
    ...employeesToCheck: (IEmployees | null | undefined)[]
  ): IEmployees[] {
    const employees: IEmployees[] = employeesToCheck.filter(isPresent);
    if (employees.length > 0) {
      const employeesCollectionIdentifiers = employeesCollection.map(employeesItem => getEmployeesIdentifier(employeesItem)!);
      const employeesToAdd = employees.filter(employeesItem => {
        const employeesIdentifier = getEmployeesIdentifier(employeesItem);
        if (employeesIdentifier == null || employeesCollectionIdentifiers.includes(employeesIdentifier)) {
          return false;
        }
        employeesCollectionIdentifiers.push(employeesIdentifier);
        return true;
      });
      return [...employeesToAdd, ...employeesCollection];
    }
    return employeesCollection;
  }
}
