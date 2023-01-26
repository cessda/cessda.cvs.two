/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
