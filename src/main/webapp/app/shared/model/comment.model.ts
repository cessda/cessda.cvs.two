import { Moment } from 'moment';

export interface IComment {
  id?: number;
  content?: any;
  userId?: number;
  dateTime?: Moment;
  versionId?: number;
}

export class Comment implements IComment {
  constructor(public id?: number, public content?: any, public userId?: number, public dateTime?: Moment, public versionId?: number) {}
}
