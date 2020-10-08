export interface IVersionHistory {
  id?: number;
  version?: string;
  date?: string;
  note?: string;
  changes?: string;
  prevVersion?: number;
  visible?: boolean;
}

export class VersionHistory implements IVersionHistory {
  constructor(
    public id?: number,
    public version?: string,
    public date?: string,
    public note?: string,
    public changes?: string,
    public prevVersion?: number,
    public visible?: boolean
  ) {
    this.visible = false;
  }
}
