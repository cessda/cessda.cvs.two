import { Moment } from 'moment';

export interface IVocabularyChange {
  id?: number;
  vocabularyId?: number;
  versionId?: number;
  changeType?: string;
  description?: any;
  userId?: number;
  userName?: string;
  date?: Moment;
}

export class VocabularyChange implements IVocabularyChange {
  constructor(
    public id?: number,
    public vocabularyId?: number,
    public versionId?: number,
    public changeType?: string,
    public description?: any,
    public userId?: number,
    public userName?: string,
    public date?: Moment
  ) {}
}
