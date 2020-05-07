import { IMetadataValue } from 'app/shared/model/metadata-value.model';
import { ObjectType } from 'app/shared/model/enumerations/object-type.model';

export interface IMetadataField {
  id?: number;
  metadataKey?: string;
  description?: any;
  objectType?: ObjectType;
  metadataValues?: IMetadataValue[];
}

export class MetadataField implements IMetadataField {
  constructor(
    public id?: number,
    public metadataKey?: string,
    public description?: any,
    public objectType?: ObjectType,
    public metadataValues?: IMetadataValue[]
  ) {}
}
