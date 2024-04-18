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

export interface Code {
  id?: number;
  uri?: string;
  notation?: string;
  archived: boolean;
  withdrawn: boolean;
  discoverable: boolean;
  sourceLanguage?: string;
  parent?: string;
  position?: number;
  publicationDate?: Moment;
  lastModified?: Moment;
  deprecated?: boolean;
  titleSq?: string;
  definitionSq?: string;
  titleBs?: string;
  definitionBs?: string;
  titleBg?: string;
  definitionBg?: string;
  titleHr?: string;
  definitionHr?: string;
  titleCs?: string;
  definitionCs?: string;
  titleDa?: string;
  definitionDa?: string;
  titleNl?: string;
  definitionNl?: string;
  titleEn?: string;
  definitionEn?: string;
  titleEt?: string;
  definitionEt?: string;
  titleFi?: string;
  definitionFi?: string;
  titleFr?: string;
  definitionFr?: string;
  titleDe?: string;
  definitionDe?: string;
  titleEl?: string;
  definitionEl?: string;
  titleHu?: string;
  definitionHu?: string;
  titleIt?: string;
  definitionIt?: string;
  titleJa?: string;
  definitionJa?: string;
  titleLt?: string;
  definitionLt?: string;
  titleMk?: string;
  definitionMk?: string;
  titleNo?: string;
  definitionNo?: string;
  titlePl?: string;
  definitionPl?: string;
  titlePt?: string;
  definitionPt?: string;
  titleRo?: string;
  definitionRo?: string;
  titleRu?: string;
  definitionRu?: string;
  titleSr?: string;
  definitionSr?: string;
  titleSk?: string;
  definitionSk?: string;
  titleSl?: string;
  definitionSl?: string;
  titleEs?: string;
  definitionEs?: string;
  titleSv?: string;
  definitionSv?: string;
  versionSq?: string;
  versionBs?: string;
  versionBg?: string;
  versionHr?: string;
  versionCs?: string;
  versionDa?: string;
  versionNl?: string;
  versionEn?: string;
  versionEt?: string;
  versionFi?: string;
  versionFr?: string;
  versionDe?: string;
  versionEl?: string;
  versionHu?: string;
  versionIt?: string;
  versionJa?: string;
  versionLt?: string;
  versionMk?: string;
  versionNo?: string;
  versionPl?: string;
  versionPt?: string;
  versionRo?: string;
  versionRu?: string;
  versionSr?: string;
  versionSk?: string;
  versionSl?: string;
  versionEs?: string;
  versionSv?: string;
}

export function createNewCode(code?: Partial<Code>): Code {
  return {
    archived: false,
    withdrawn: false,
    discoverable: false,
    ...code,
  };
}
