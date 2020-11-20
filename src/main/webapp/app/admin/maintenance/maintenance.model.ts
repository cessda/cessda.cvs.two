import { Moment } from 'moment';

export class Maintenance {
  constructor(public output: string, public timestamp: Moment, public type: string) {}
}
