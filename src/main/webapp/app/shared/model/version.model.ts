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
import { Moment } from 'moment';
import { Concept } from 'app/shared/model/concept.model';
import { IVersionHistory } from 'app/shared/model/version-history';
import { IComment } from 'app/shared/model/comment.model';

export interface IVersion {
  id?: number;
  status: string;
  itemType: string;
  language?: string;
  publicationDate?: Moment;
  lastModified?: Moment;
  number?: string;
  uri?: string;
  canonicalUri?: string;
  uriSl?: string;
  notation?: string;
  title?: string;
  definition?: string;
  previousVersion?: number;
  initialVersion?: number;
  creator?: number;
  publisher?: number;
  notes?: string;
  versionNotes?: string;
  versionChanges?: string;
  discussionNotes?: string;
  license?: string;
  licenseId?: number;
  licenseName?: string;
  licenseLink?: string;
  licenseLogo?: string;
  citation?: string;
  ddiUsage?: string;
  translateAgency?: string;
  translateAgencyLink?: string;
  concepts?: Concept[];
  comments?: IComment[];
  vocabularyId?: number;
  versionHistories?: IVersionHistory[];
  titleSl?: string;
  languageSl?: string;
  definitionSl?: string;
  notesSl?: string;
}

export class Version implements IVersion {
  constructor(
    public id?: number,
    public status: string = 'DRAFT',
    public itemType: string = 'SL',
    public language?: string,
    public publicationDate?: Moment,
    public lastModified?: Moment,
    public number?: string,
    public uri?: string,
    public canonicalUri?: string,
    public uriSl?: string,
    public notation?: string,
    public title?: string,
    public definition?: string,
    public previousVersion?: number,
    public initialVersion?: number,
    public creator?: number,
    public publisher?: number,
    public notes?: string,
    public versionNotes?: string,
    public versionChanges?: string,
    public discussionNotes?: string,
    public license?: string,
    public licenseId?: number,
    public licenseName?: string,
    public licenseLink?: string,
    public licenseLogo?: string,
    public citation?: string,
    public ddiUsage?: string,
    public translateAgency?: string,
    public translateAgencyLink?: string,
    public concepts: Concept[] = [],
    public comments: IComment[] = [],
    public vocabularyId?: number,
    public versionHistories: IVersionHistory[] = [],
    public languageSl?: string,
  ) {}
}
