import {Moment} from 'moment';

export interface ICode {
  id?: number;
  uri?: string;
  notation?: string;
  archived?: boolean;
  withdrawn?: boolean;
  discoverable?: boolean;
  sourceLanguage?: string;
  parent?: string;
  position?: number;
  publicationDate?: Moment;
  lastModified?: Moment;
  titleSq?: any;
  definitionSq?: any;
  titleBs?: any;
  definitionBs?: any;
  titleBg?: any;
  definitionBg?: any;
  titleHr?: any;
  definitionHr?: any;
  titleCs?: any;
  definitionCs?: any;
  titleDa?: any;
  definitionDa?: any;
  titleNl?: any;
  definitionNl?: any;
  titleEn?: any;
  definitionEn?: any;
  titleEt?: any;
  definitionEt?: any;
  titleFi?: any;
  definitionFi?: any;
  titleFr?: any;
  definitionFr?: any;
  titleDe?: any;
  definitionDe?: any;
  titleEl?: any;
  definitionEl?: any;
  titleHu?: any;
  definitionHu?: any;
  titleIt?: any;
  definitionIt?: any;
  titleJa?: any;
  definitionJa?: any;
  titleLt?: any;
  definitionLt?: any;
  titleMk?: any;
  definitionMk?: any;
  titleNo?: any;
  definitionNo?: any;
  titlePl?: any;
  definitionPl?: any;
  titlePt?: any;
  definitionPt?: any;
  titleRo?: any;
  definitionRo?: any;
  titleRu?: any;
  definitionRu?: any;
  titleSr?: any;
  definitionSr?: any;
  titleSk?: any;
  definitionSk?: any;
  titleSl?: any;
  definitionSl?: any;
  titleEs?: any;
  definitionEs?: any;
  titleSv?: any;
  definitionSv?: any;
  versionSq?: any;
  versionBs?: any;
  versionBg?: any;
  versionHr?: any;
  versionCs?: any;
  versionDa?: any;
  versionNl?: any;
  versionEn?: any;
  versionEt?: any;
  versionFi?: any;
  versionFr?: any;
  versionDe?: any;
  versionEl?: any;
  versionHu?: any;
  versionIt?: any;
  versionJa?: any;
  versionLt?: any;
  versionMk?: any;
  versionNo?: any;
  versionPl?: any;
  versionPt?: any;
  versionRo?: any;
  versionRu?: any;
  versionSr?: any;
  versionSk?: any;
  versionSl?: any;
  versionEs?: any;
  versionSv?: any;
}

export class Code implements ICode {
  constructor(
    public id?: number,
    public uri?: string,
    public notation?: string,
    public archived?: boolean,
    public withdrawn?: boolean,
    public discoverable?: boolean,
    public sourceLanguage?: string,
    public parent?: string,
    public position?: number,
    public publicationDate?: Moment,
    public lastModified?: Moment,
    public titleSq?: any,
    public definitionSq?: any,
    public titleBs?: any,
    public definitionBs?: any,
    public titleBg?: any,
    public definitionBg?: any,
    public titleHr?: any,
    public definitionHr?: any,
    public titleCs?: any,
    public definitionCs?: any,
    public titleDa?: any,
    public definitionDa?: any,
    public titleNl?: any,
    public definitionNl?: any,
    public titleEn?: any,
    public definitionEn?: any,
    public titleEt?: any,
    public definitionEt?: any,
    public titleFi?: any,
    public definitionFi?: any,
    public titleFr?: any,
    public definitionFr?: any,
    public titleDe?: any,
    public definitionDe?: any,
    public titleEl?: any,
    public definitionEl?: any,
    public titleHu?: any,
    public definitionHu?: any,
    public titleIt?: any,
    public definitionIt?: any,
    public titleJa?: any,
    public definitionJa?: any,
    public titleLt?: any,
    public definitionLt?: any,
    public titleMk?: any,
    public definitionMk?: any,
    public titleNo?: any,
    public definitionNo?: any,
    public titlePl?: any,
    public definitionPl?: any,
    public titlePt?: any,
    public definitionPt?: any,
    public titleRo?: any,
    public definitionRo?: any,
    public titleRu?: any,
    public definitionRu?: any,
    public titleSr?: any,
    public definitionSr?: any,
    public titleSk?: any,
    public definitionSk?: any,
    public titleSl?: any,
    public definitionSl?: any,
    public titleEs?: any,
    public definitionEs?: any,
    public titleSv?: any,
    public definitionSv?: any,
    public versionSq?: any,
    public versionBs?: any,
    public versionBg?: any,
    public versionHr?: any,
    public versionCs?: any,
    public versionDa?: any,
    public versionNl?: any,
    public versionEn?: any,
    public versionEt?: any,
    public versionFi?: any,
    public versionFr?: any,
    public versionDe?: any,
    public versionEl?: any,
    public versionHu?: any,
    public versionIt?: any,
    public versionJa?: any,
    public versionLt?: any,
    public versionMk?: any,
    public versionNo?: any,
    public versionPl?: any,
    public versionPt?: any,
    public versionRo?: any,
    public versionRu?: any,
    public versionSr?: any,
    public versionSk?: any,
    public versionSl?: any,
    public versionEs?: any,
    public versionSv?: any
  ) {
    this.archived = this.archived || false;
    this.withdrawn = this.withdrawn || false;
    this.discoverable = this.discoverable || false;
  }
}
