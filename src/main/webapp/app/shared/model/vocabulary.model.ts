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
import { Version } from 'app/shared/model/version.model';
import { Code } from 'app/shared/model/code.model';

export const enum Status {
  DRAFT = 'DRAFT',
  REVIEW = 'REVIEW',
  READY_TO_TRANSLATE = 'READY_TO_TRANSLATE',
  PUBLISHED = 'PUBLISHED',
  READY_TO_PUBLISH = 'READY_TO_PUBLISH',
  PUBLISHED_RESTRICT = 'PUBLISHED_RESTRICT',
  WITHDRAWN = 'WITHDRAWN',
}

export interface Vocabulary {
  id?: number;
  status: Status;
  uri?: string;
  notation: string;
  versionNumber: string;
  initialPublication?: number;
  previousPublication?: number;
  archived: boolean;
  withdrawn: boolean;
  discoverable: boolean;
  selectedLang: string;
  selectedCode?: string;
  selectedVersion?: string;
  sourceLanguage: string;
  agencyId: number;
  agencyName: string;
  agencyLogo?: string;
  agencyLink?: string;
  publicationDate?: Moment;
  lastModified?: Moment;
  notes?: string;
  versionSq?: string;
  titleSq?: string;
  definitionSq?: string;
  versionBs?: string;
  titleBs?: string;
  definitionBs?: string;
  versionBg?: string;
  titleBg?: string;
  definitionBg?: string;
  versionHr?: string;
  titleHr?: string;
  definitionHr?: string;
  versionCs?: string;
  titleCs?: string;
  definitionCs?: string;
  versionDa?: string;
  titleDa?: string;
  definitionDa?: string;
  versionNl?: string;
  titleNl?: string;
  definitionNl?: string;
  versionEn?: string;
  titleEn?: string;
  definitionEn?: string;
  versionEt?: string;
  titleEt?: string;
  definitionEt?: string;
  versionFi?: string;
  titleFi?: string;
  definitionFi?: string;
  versionFr?: string;
  titleFr?: string;
  definitionFr?: string;
  versionDe?: string;
  titleDe?: string;
  definitionDe?: string;
  versionEl?: string;
  titleEl?: string;
  definitionEl?: string;
  versionHu?: string;
  titleHu?: string;
  definitionHu?: string;
  versionIt?: string;
  titleIt?: string;
  definitionIt?: string;
  versionJa?: string;
  titleJa?: string;
  definitionJa?: string;
  versionLt?: string;
  titleLt?: string;
  definitionLt?: string;
  versionMk?: string;
  titleMk?: string;
  definitionMk?: string;
  versionNo?: string;
  titleNo?: string;
  definitionNo?: string;
  versionPl?: string;
  titlePl?: string;
  definitionPl?: string;
  versionPt?: string;
  titlePt?: string;
  definitionPt?: string;
  versionRo?: string;
  titleRo?: string;
  definitionRo?: string;
  versionRu?: string;
  titleRu?: string;
  definitionRu?: string;
  versionSr?: string;
  titleSr?: string;
  definitionSr?: string;
  versionSk?: string;
  titleSk?: string;
  definitionSk?: string;
  versionSl?: string;
  titleSl?: string;
  definitionSl?: string;
  versionEs?: string;
  titleEs?: string;
  definitionEs?: string;
  versionSv?: string;
  titleSv?: string;
  definitionSv?: string;
  versions: Version[];
  codes: Code[];
  languages: string[];
  languagesPublished: string[];
}

export function createNewVocabulary(vocabulary?: Partial<Vocabulary>): Vocabulary {
  return {
    archived: false,
    withdrawn: false,
    discoverable: false,
    status: Status.DRAFT,
    notation: 'NEW_VOCABULARY',
    sourceLanguage: 'en',
    versionNumber: '1.0.0',
    selectedLang: 'en',
    agencyId: 0,
    agencyName: 'DEFAULT_AGENCY',
    versions: [],
    codes: [],
    languages: [],
    languagesPublished: [],
    ...vocabulary,
  };
}
