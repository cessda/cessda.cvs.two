import { ObjectType } from 'app/shared/model/enumerations/object-type.model';

export interface IMetadataValue {
  id?: number;
  value?: any;
  objectType?: ObjectType;
  objectId?: number;
  metadataFieldId?: number;
}

export class MetadataValue implements IMetadataValue {
  constructor(
    public id?: number,
    public value?: any,
    public objectType?: ObjectType,
    public objectId?: number,
    public metadataFieldId?: number
  ) {}
}
