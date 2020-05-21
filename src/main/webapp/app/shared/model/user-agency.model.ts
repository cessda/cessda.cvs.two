export interface IUserAgency {
  id?: number;
  userId?: number;
  agencyRole?: string;
  language?: string;
  agencyId?: number;
}

export class UserAgency implements IUserAgency {
  constructor(public id?: number, public userId?: number, public agencyRole?: string, public language?: string, public agencyId?: number) {}
}
