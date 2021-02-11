/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

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
