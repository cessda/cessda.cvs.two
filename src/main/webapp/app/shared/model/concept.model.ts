/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

export interface IConcept {
  id?: number;
  uri?: string;
  notation?: string;
  title?: any;
  definition?: any;
  previousConcept?: number;
  slConcept?: number;
  parent?: string;
  position?: number;
  deprecated?: boolean;
  versionId?: number;
  visible?: boolean;
  titleSl?: any;
  definitionSl?: any;
  status?: string;
}

export class Concept implements IConcept {
  constructor(
    public id?: number,
    public uri?: string,
    public notation?: string,
    public title?: any,
    public definition?: any,
    public previousConcept?: number,
    public slConcept?: number,
    public parent?: string,
    public position?: number,
    public deprecated?: boolean,
    public versionId?: number,
    public visible?: boolean,
    public titleSl?: any,
    public definitionSl?: any,
    public status?: string
  ) {
    this.parent = '';
    // used as flag for tree open collapse
    this.visible = true;
  }
}
