import { IUserAgency } from 'app/shared/model/user-agency.model';

export interface IAgency {
  id?: number;
  name?: string;
  link?: string;
  description?: any;
  logopath?: string;
  license?: string;
  licenseId?: number;
  uri?: string;
  canonicalUri?: string;
  userAgencies?: IUserAgency[];
}

export class Agency implements IAgency {
  constructor(
    public id?: number,
    public name?: string,
    public link?: string,
    public description?: any,
    public logopath?: string,
    public license?: string,
    public licenseId?: number,
    public uri?: string,
    public canonicalUri?: string,
    public userAgencies?: IUserAgency[]
  ) {}
}
