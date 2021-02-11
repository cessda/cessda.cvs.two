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

export interface IVocabularySnippet {
  actionType?: string;
  agencyId?: number;
  language?: string;
  vocabularyId?: number;
  versionId?: number;
  versionSlId?: number;
  licenseId?: number;
  itemType?: string;
  notation?: string;
  versionNumber?: string;
  status?: string;
  title?: any;
  definition?: any;
  notes?: any;
  versionNotes?: any;
  versionChanges?: any;
  discussionNotes?: any;
  ddiUsage?: any;
  translateAgency?: string;
  translateAgencyLink?: string;
  changeType?: string;
  changeDesc?: string;
}

export class VocabularySnippet implements IVocabularySnippet {
  constructor(
    public actionType?: string,
    public agencyId?: number,
    public language?: string,
    public vocabularyId?: number,
    public versionId?: number,
    public versionSlId?: number,
    public licenseId?: number,
    public itemType?: string,
    public notation?: string,
    public versionNumber?: string,
    public status?: string,
    public title?: any,
    public definition?: any,
    public notes?: any,
    public versionNotes?: any,
    public versionChanges?: any,
    public discussionNotes?: any,
    public ddiUsage?: any,
    public translateAgency?: string,
    public translateAgencyLink?: string,
    public changeType?: string,
    public changeDesc?: string
  ) {}
}
