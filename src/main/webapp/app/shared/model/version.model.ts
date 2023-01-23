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
import { IConcept } from 'app/shared/model/concept.model';
import { IVersionHistory } from 'app/shared/model/version-history';
import { IComment } from 'app/shared/model/comment.model';

export interface IVersion {
  id?: number;
  status?: string;
  itemType?: string;
  language?: string;
  publicationDate?: Moment;
  lastModified?: Moment;
  number?: string;
  uri?: string;
  canonicalUri?: string;
  uriSl?: string;
  notation?: string;
  titleSl?: any;
  title?: any;
  definitionSl?: any;
  definition?: any;
  previousVersion?: number;
  initialVersion?: number;
  creator?: number;
  publisher?: number;
  notesSl?: any;
  notes?: any;
  versionNotes?: any;
  versionChanges?: any;
  discussionNotes?: any;
  license?: string;
  licenseId?: number;
  licenseName?: string;
  licenseLink?: string;
  licenseLogo?: string;
  citation?: any;
  ddiUsage?: any;
  translateAgency?: string;
  translateAgencyLink?: string;
  concepts?: IConcept[];
  comments?: IComment[];
  vocabularyId?: number;
  versionHistories?: IVersionHistory[];
  languageSl?: string;
}

export class Version implements IVersion {
  constructor(
    public id?: number,
    public status?: string,
    public itemType?: string,
    public language?: string,
    public publicationDate?: Moment,
    public lastModified?: Moment,
    public number?: string,
    public uri?: string,
    public canonicalUri?: string,
    public uriSl?: string,
    public notation?: string,
    public titleSl?: any,
    public title?: any,
    public definitionSl?: any,
    public definition?: any,
    public previousVersion?: number,
    public initialVersion?: number,
    public creator?: number,
    public publisher?: number,
    public notesSl?: any,
    public notes?: any,
    public versionNotes?: any,
    public versionChanges?: any,
    public discussionNotes?: any,
    public license?: string,
    public licenseId?: number,
    public licenseName?: string,
    public licenseLink?: string,
    public licenseLogo?: string,
    public citation?: any,
    public ddiUsage?: any,
    public translateAgency?: string,
    public translateAgencyLink?: string,
    public concepts?: IConcept[],
    public comments?: IComment[],
    public vocabularyId?: number,
    public versionHistories?: IVersionHistory[],
    public languageSl?: string
  ) {}
}
