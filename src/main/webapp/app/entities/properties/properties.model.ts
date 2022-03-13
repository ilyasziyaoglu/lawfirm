export interface IProperties {
  id?: number;
  key?: string;
  value?: string;
  language?: string | null;
}

export class Properties implements IProperties {
  constructor(public id?: number, public key?: string, public value?: string, public language?: string | null) {}
}

export function getPropertiesIdentifier(properties: IProperties): number | undefined {
  return properties.id;
}
