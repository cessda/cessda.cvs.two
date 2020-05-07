export interface IUserAgency {
  id?: number;
  agencyRole?: string;
  language?: string;
  agencyId?: number;
}

export class UserAgency implements IUserAgency {
  constructor(public id?: number, public agencyRole?: string, public language?: string, public agencyId?: number) {}
}
