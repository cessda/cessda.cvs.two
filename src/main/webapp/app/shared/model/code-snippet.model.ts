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

export interface ICodeSnippet {
  actionType?: string;
  versionId?: number;
  introducedInVersionId?: number | null;
  validUntilVersionId?: number | null;
  conceptId?: number;
  conceptSlId?: number;
  parent?: string;
  notation?: string;
  title?: any;
  definition?: any;
  position?: number;
  insertionRefConceptId?: any;
  relPosToRefConcept?: number;
  changeType?: any;
  changeDesc?: any;
  conceptStructures?: string[];
  conceptStructureIds?: number[];
  replacedBy?: number | null;
}

export class CodeSnippet implements ICodeSnippet {
  constructor(
    public actionType?: string,
    public versionId?: number,
    public introducedInVersionId?: number | null,
    public validUntilVersionId?: number | null,
    public conceptId?: number,
    public conceptSlId?: number,
    public parent?: string,
    public notation?: string,
    public title?: any,
    public definition?: any,
    public position?: number,
    public insertionRefConceptId?: any,
    public relPosToRefConcept?: number,
    public changeType?: any,
    public changeDesc?: any,
    public conceptStructures?: string[],
    public conceptStructureIds?: number[],
    public replacedBy?: number | null
  ) {}
}
