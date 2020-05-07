import { IBucket } from 'app/shared/model/bucket';

export interface IAggr {
  type?: string;
  field?: string;
  values?: string[];
  buckets?: IBucket[];
  filteredBuckets?: IBucket[];
}

export class Aggr implements IAggr {
  constructor(
    public type?: string,
    public field?: string,
    public values?: string[],
    public buckets?: IBucket[],
    public filteredBuckets?: IBucket[]
  ) {}
}
