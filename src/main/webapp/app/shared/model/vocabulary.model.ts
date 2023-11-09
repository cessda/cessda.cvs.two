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

export interface IVocabulary {
  id?: number;
  status?: string;
  uri?: string;
  notation?: string;
  versionNumber?: string;
  initialPublication?: number;
  previousPublication?: number;
  archived: boolean;
  withdrawn: boolean;
  discoverable: boolean;
  selectedLang: string;
  selectedCode?: string;
  selectedVersion?: string;
  sourceLanguage: string;
  agencyId?: number;
  agencyName?: string;
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

export class Vocabulary implements IVocabulary {
  constructor(
    public id?: number,
    public status?: string,
    public uri?: string,
    public notation?: string,
    public versionNumber?: string,
    public initialPublication?: number,
    public previousPublication?: number,
    public archived: boolean = false,
    public withdrawn: boolean = false,
    public discoverable: boolean = false,
    public sourceLanguage: string = 'en',
    public selectedLang: string = 'en',
    public selectedCode?: string,
    public selectedVersion?: string,
    public agencyId?: number,
    public agencyName?: string,
    public agencyLogo?: string,
    public agencyLink?: string,
    public publicationDate?: Moment,
    public lastModified?: Moment,
    public notes?: string,
    public versionSq?: string,
    public titleSq?: string,
    public definitionSq?: string,
    public versionBs?: string,
    public titleBs?: string,
    public definitionBs?: string,
    public versionBg?: string,
    public titleBg?: string,
    public definitionBg?: string,
    public versionHr?: string,
    public titleHr?: string,
    public definitionHr?: string,
    public versionCs?: string,
    public titleCs?: string,
    public definitionCs?: string,
    public versionDa?: string,
    public titleDa?: string,
    public definitionDa?: string,
    public versionNl?: string,
    public titleNl?: string,
    public definitionNl?: string,
    public versionEn?: string,
    public titleEn?: string,
    public definitionEn?: string,
    public versionEt?: string,
    public titleEt?: string,
    public definitionEt?: string,
    public versionFi?: string,
    public titleFi?: string,
    public definitionFi?: string,
    public versionFr?: string,
    public titleFr?: string,
    public definitionFr?: string,
    public versionDe?: string,
    public titleDe?: string,
    public definitionDe?: string,
    public versionEl?: string,
    public titleEl?: string,
    public definitionEl?: string,
    public versionHu?: string,
    public titleHu?: string,
    public definitionHu?: string,
    public versionIt?: string,
    public titleIt?: string,
    public definitionIt?: string,
    public versionJa?: string,
    public titleJa?: string,
    public definitionJa?: string,
    public versionLt?: string,
    public titleLt?: string,
    public definitionLt?: string,
    public versionMk?: string,
    public titleMk?: string,
    public definitionMk?: string,
    public versionNo?: string,
    public titleNo?: string,
    public definitionNo?: string,
    public versionPl?: string,
    public titlePl?: string,
    public definitionPl?: string,
    public versionPt?: string,
    public titlePt?: string,
    public definitionPt?: string,
    public versionRo?: string,
    public titleRo?: string,
    public definitionRo?: string,
    public versionRu?: string,
    public titleRu?: string,
    public definitionRu?: string,
    public versionSr?: string,
    public titleSr?: string,
    public definitionSr?: string,
    public versionSk?: string,
    public titleSk?: string,
    public definitionSk?: string,
    public versionSl?: string,
    public titleSl?: string,
    public definitionSl?: string,
    public versionEs?: string,
    public titleEs?: string,
    public definitionEs?: string,
    public versionSv?: string,
    public titleSv?: string,
    public definitionSv?: string,
    public versions: Version[] = [],
    public codes: Code[] = [],
    public languages: string[] = [],
    public languagesPublished: string[] = [],
  ) {}
}
