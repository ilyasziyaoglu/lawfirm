export interface IServices {
  id?: number;
  title?: string;
  description?: string;
  order?: number;
  iconContentType?: string;
  icon?: string;
}

export class Services implements IServices {
  constructor(
    public id?: number,
    public title?: string,
    public description?: string,
    public order?: number,
    public iconContentType?: string,
    public icon?: string
  ) {}
}

export function getServicesIdentifier(services: IServices): number | undefined {
  return services.id;
}
