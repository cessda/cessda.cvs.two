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
import { VersionHistory } from 'app/shared/model/version-history';
import { IComment } from 'app/shared/model/comment.model';

export interface Version {
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
  concepts: Concept[];
  comments: IComment[];
  vocabularyId?: number;
  versionHistories: VersionHistory[];
  titleSl?: string;
  languageSl?: string;
  definitionSl?: string;
  notesSl?: string;
}

export function createNewVersion(id?: number): Version {
  return {
    id: id,
    status: 'DRAFT',
    itemType: 'TL',
    concepts: [],
    comments: [],
    versionHistories: [],
  };
}
