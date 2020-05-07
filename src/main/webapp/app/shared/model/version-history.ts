export interface IVersionHistory {
  version?: string;
  date?: string;
  note?: string;
  changes?: string;
  visible?: boolean;
}

export class VersionHistory implements IVersionHistory {
  constructor(public version?: string, public date?: string, public note?: string, public changes?: string, public visible?: boolean) {
    this.visible = false;
  }
}
