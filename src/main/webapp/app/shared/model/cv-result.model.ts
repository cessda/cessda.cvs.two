import { IVocabulary } from 'app/shared/model/vocabulary.model';
import { Aggr } from 'app/shared/model/aggr';

export interface ICvResult {
  vocabularies?: IVocabulary[];
  totalElements?: number;
  totalPage?: number;
  numberOfElements?: number;
  number?: number;
  size?: number;
  last?: boolean;
  first?: boolean;
  aggrs?: Aggr[];
}

export class CvResult implements ICvResult {
  constructor(
    public vocabularies?: IVocabulary[],
    public totalElements?: number,
    public totalPage?: number,
    public numberOfElements?: number,
    public number?: number,
    public size?: number,
    public last?: boolean,
    public first?: boolean,
    public aggrs?: Aggr[]
  ) {}
}
