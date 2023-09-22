import { VersionNumber } from './version-number.model';

export interface VersionStatusStat {
  language: string;
  type: string;
  versionNumber: VersionNumber;
  status: string;
  creationDate: string;
  date: string;
}
