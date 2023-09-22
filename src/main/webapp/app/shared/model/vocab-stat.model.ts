import { VersionCodeStat } from './version-code-stat.model';
import { VersionStatusStat } from './version-status-stat.model';

export interface VocabStat {
  currentVersion: string;
  latestPublishedVersion: string;
  notation: string;
  languages: string[];
  sourceLanguage: string;
  versionCodeStats: VersionCodeStat[];
  versionStatusStats: VersionStatusStat[];
}
