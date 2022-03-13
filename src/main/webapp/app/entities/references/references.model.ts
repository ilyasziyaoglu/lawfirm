export interface IReferences {
  id?: number;
  name?: string;
  order?: number;
  imageContentType?: string;
  image?: string;
}

export class References implements IReferences {
  constructor(public id?: number, public name?: string, public order?: number, public imageContentType?: string, public image?: string) {}
}

export function getReferencesIdentifier(references: IReferences): number | undefined {
  return references.id;
}
