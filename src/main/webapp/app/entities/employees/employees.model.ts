import {IServicePoints} from 'app/entities/service-points/service-points.model';
import {IServices} from 'app/entities/services/services.model';

export interface IEmployees {
  id?: number;
  name?: string;
  surname?: string;
  title?: string;
  story?: string | null;
  order?: number;
  imageContentType?: string;
  image?: string;
  servicePoint?: IServicePoints | null;
  services?: IServices[] | null;
}

export class Employees implements IEmployees {
  constructor(
    public id?: number,
    public name?: string,
    public surname?: string,
    public title?: string,
    public story?: string | null,
    public order?: number,
    public imageContentType?: string,
    public image?: string,
    public servicePoint?: IServicePoints | null,
    public services?: IServices[] | null
  ) {}
}

export function getEmployeesIdentifier(employees: IEmployees): number | undefined {
  return employees.id;
}
