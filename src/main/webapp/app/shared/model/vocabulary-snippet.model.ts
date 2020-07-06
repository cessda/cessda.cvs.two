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
