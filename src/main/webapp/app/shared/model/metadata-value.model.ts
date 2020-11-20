import { ObjectType } from 'app/shared/model/enumerations/object-type.model';

export interface IMetadataValue {
  id?: number;
  identifier?: string;
  value?: any;
  objectType?: ObjectType;
  objectId?: number;
  metadataFieldId?: number;
  metadataKey?: string;
  position?: number;
}

export class MetadataValue implements IMetadataValue {
  constructor(
    public id?: number,
    public identifier?: string,
    public value?: any,
    public objectType?: ObjectType,
    public objectId?: number,
    public metadataFieldId?: number,
    public metadataKey?: string,
    public position?: number
  ) {}
}
