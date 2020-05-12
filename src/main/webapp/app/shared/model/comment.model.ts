import { Moment } from 'moment';

export interface IComment {
  id?: number;
  info?: string;
  content?: any;
  userId?: number;
  dateTime?: Moment;
  versionId?: number;
}

export class Comment implements IComment {
  constructor(
    public id?: number,
    public info?: string,
    public content?: any,
    public userId?: number,
    public dateTime?: Moment,
    public versionId?: number
  ) {}
}
