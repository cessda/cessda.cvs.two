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
import { Vocabulary } from 'app/shared/model/vocabulary.model';
import { Aggr } from 'app/shared/model/aggr';

export interface CvResult {
  vocabularies: Vocabulary[];
  totalElements: number;
  totalPage: number;
  numberOfElements: number;
  number: number;
  size: number;
  last: boolean;
  first: boolean;
  aggrs: Aggr[];
}
