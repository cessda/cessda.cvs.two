import { Moment } from 'moment';
import { IConcept } from 'app/shared/model/concept.model';
import { IVersionHistory } from 'app/shared/model/version-history';

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
  citation?: any;
  ddiUsage?: any;
  translateAgency?: string;
  translateAgencyLink?: string;
  concepts?: IConcept[];
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
    public citation?: any,
    public ddiUsage?: any,
    public translateAgency?: string,
    public translateAgencyLink?: string,
    public concepts?: IConcept[],
    public vocabularyId?: number,
    public versionHistories?: IVersionHistory[],
    public languageSl?: string
  ) {}
}
