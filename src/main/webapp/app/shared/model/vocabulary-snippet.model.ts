import { ActionType } from './enumerations/action-type.model';

/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
export interface VocabularySnippet {
  actionType: ActionType;
  agencyId?: number;
  language?: string;
  vocabularyId?: number;
  versionId?: number;
  versionSlId?: number;
  licenseId?: number;
  itemType?: string;
  notation?: string;
  versionNumber?: string;
  status?: string;
  title?: string;
  definition?: string;
  notes?: string;
  versionNotes?: string;
  versionChanges?: string;
  discussionNotes?: string;
  ddiUsage?: string;
  translateAgency?: string;
  translateAgencyLink?: string;
  changeType?: string;
  changeDesc?: string;
}
