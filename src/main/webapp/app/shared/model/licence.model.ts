export interface ILicence {
  id?: number;
  name?: string;
  link?: string;
  logoLink?: string;
  abbr?: string;
}

export class Licence implements ILicence {
  constructor(public id?: number, public name?: string, public link?: string, public logoLink?: string, public abbr?: string) {}
}
