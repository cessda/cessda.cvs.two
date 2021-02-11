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

import { Moment } from 'moment';
import { IVersion } from 'app/shared/model/version.model';
import { ICode } from 'app/shared/model/code.model';

export interface IVocabulary {
  id?: number;
  status?: string;
  uri?: string;
  notation?: string;
  versionNumber?: string;
  initialPublication?: number;
  previousPublication?: number;
  archived?: boolean;
  withdrawn?: boolean;
  discoverable?: boolean;
  selectedLang?: string;
  selectedCode?: string;
  selectedVersion?: string;
  sourceLanguage?: string;
  agencyId?: number;
  agencyName?: string;
  agencyLogo?: string;
  agencyLink?: string;
  publicationDate?: Moment;
  lastModified?: Moment;
  notes?: any;
  versionSq?: any;
  titleSq?: any;
  definitionSq?: any;
  versionBs?: any;
  titleBs?: any;
  definitionBs?: any;
  versionBg?: any;
  titleBg?: any;
  definitionBg?: any;
  versionHr?: any;
  titleHr?: any;
  definitionHr?: any;
  versionCs?: any;
  titleCs?: any;
  definitionCs?: any;
  versionDa?: any;
  titleDa?: any;
  definitionDa?: any;
  versionNl?: any;
  titleNl?: any;
  definitionNl?: any;
  versionEn?: any;
  titleEn?: any;
  definitionEn?: any;
  versionEt?: any;
  titleEt?: any;
  definitionEt?: any;
  versionFi?: any;
  titleFi?: any;
  definitionFi?: any;
  versionFr?: any;
  titleFr?: any;
  definitionFr?: any;
  versionDe?: any;
  titleDe?: any;
  definitionDe?: any;
  versionEl?: any;
  titleEl?: any;
  definitionEl?: any;
  versionHu?: any;
  titleHu?: any;
  definitionHu?: any;
  versionIt?: any;
  titleIt?: any;
  definitionIt?: any;
  versionJa?: any;
  titleJa?: any;
  definitionJa?: any;
  versionLt?: any;
  titleLt?: any;
  definitionLt?: any;
  versionMk?: any;
  titleMk?: any;
  definitionMk?: any;
  versionNo?: any;
  titleNo?: any;
  definitionNo?: any;
  versionPl?: any;
  titlePl?: any;
  definitionPl?: any;
  versionPt?: any;
  titlePt?: any;
  definitionPt?: any;
  versionRo?: any;
  titleRo?: any;
  definitionRo?: any;
  versionRu?: any;
  titleRu?: any;
  definitionRu?: any;
  versionSr?: any;
  titleSr?: any;
  definitionSr?: any;
  versionSk?: any;
  titleSk?: any;
  definitionSk?: any;
  versionSl?: any;
  titleSl?: any;
  definitionSl?: any;
  versionEs?: any;
  titleEs?: any;
  definitionEs?: any;
  versionSv?: any;
  titleSv?: any;
  definitionSv?: any;
  versions?: IVersion[];
  codes?: ICode[];
  languages?: string[];
  languagesPublished?: string[];
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
    public archived?: boolean,
    public withdrawn?: boolean,
    public discoverable?: boolean,
    public sourceLanguage?: string,
    public selectedLang?: string,
    public selectedCode?: string,
    public selectedVersion?: string,
    public agencyId?: number,
    public agencyName?: string,
    public agencyLogo?: string,
    public agencyLink?: string,
    public publicationDate?: Moment,
    public lastModified?: Moment,
    public notes?: any,
    public versionSq?: any,
    public titleSq?: any,
    public definitionSq?: any,
    public versionBs?: any,
    public titleBs?: any,
    public definitionBs?: any,
    public versionBg?: any,
    public titleBg?: any,
    public definitionBg?: any,
    public versionHr?: any,
    public titleHr?: any,
    public definitionHr?: any,
    public versionCs?: any,
    public titleCs?: any,
    public definitionCs?: any,
    public versionDa?: any,
    public titleDa?: any,
    public definitionDa?: any,
    public versionNl?: any,
    public titleNl?: any,
    public definitionNl?: any,
    public versionEn?: any,
    public titleEn?: any,
    public definitionEn?: any,
    public versionEt?: any,
    public titleEt?: any,
    public definitionEt?: any,
    public versionFi?: any,
    public titleFi?: any,
    public definitionFi?: any,
    public versionFr?: any,
    public titleFr?: any,
    public definitionFr?: any,
    public versionDe?: any,
    public titleDe?: any,
    public definitionDe?: any,
    public versionEl?: any,
    public titleEl?: any,
    public definitionEl?: any,
    public versionHu?: any,
    public titleHu?: any,
    public definitionHu?: any,
    public versionIt?: any,
    public titleIt?: any,
    public definitionIt?: any,
    public versionJa?: any,
    public titleJa?: any,
    public definitionJa?: any,
    public versionLt?: any,
    public titleLt?: any,
    public definitionLt?: any,
    public versionMk?: any,
    public titleMk?: any,
    public definitionMk?: any,
    public versionNo?: any,
    public titleNo?: any,
    public definitionNo?: any,
    public versionPl?: any,
    public titlePl?: any,
    public definitionPl?: any,
    public versionPt?: any,
    public titlePt?: any,
    public definitionPt?: any,
    public versionRo?: any,
    public titleRo?: any,
    public definitionRo?: any,
    public versionRu?: any,
    public titleRu?: any,
    public definitionRu?: any,
    public versionSr?: any,
    public titleSr?: any,
    public definitionSr?: any,
    public versionSk?: any,
    public titleSk?: any,
    public definitionSk?: any,
    public versionSl?: any,
    public titleSl?: any,
    public definitionSl?: any,
    public versionEs?: any,
    public titleEs?: any,
    public definitionEs?: any,
    public versionSv?: any,
    public titleSv?: any,
    public definitionSv?: any,
    public versions?: IVersion[],
    public codes?: ICode[],
    public languages?: string[],
    public languagesPublished?: string[]
  ) {
    this.archived = this.archived || false;
    this.withdrawn = this.withdrawn || false;
    this.discoverable = this.discoverable || false;
    this.selectedLang = 'en';
  }
}
