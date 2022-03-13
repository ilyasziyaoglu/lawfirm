export interface IServicePoints {
  id?: number;
  name?: string;
  email?: string;
  phone?: string;
  address?: string;
  mapUrl?: string | null;
  latitude?: number | null;
  longitude?: number | null;
}

export class ServicePoints implements IServicePoints {
  constructor(
    public id?: number,
    public name?: string,
    public email?: string,
    public phone?: string,
    public address?: string,
    public mapUrl?: string | null,
    public latitude?: number | null,
    public longitude?: number | null
  ) {}
}

export function getServicePointsIdentifier(servicePoints: IServicePoints): number | undefined {
  return servicePoints.id;
}
