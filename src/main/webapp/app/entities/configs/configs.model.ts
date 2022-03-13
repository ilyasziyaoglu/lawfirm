export interface IConfigs {
  id?: number;
  key?: string;
  value?: string;
}

export class Configs implements IConfigs {
  constructor(public id?: number, public key?: string, public value?: string) {}
}

export function getConfigsIdentifier(configs: IConfigs): number | undefined {
  return configs.id;
}
