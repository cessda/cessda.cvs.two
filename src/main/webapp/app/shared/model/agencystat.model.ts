import { VocabStat } from './vocab-stat.model';

export interface AgencyStat {
  id: number;
  name: string;
  url: string;
  description: string;
  logo: string;
  vocabStats: VocabStat[];
}
