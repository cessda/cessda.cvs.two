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
import { UserAgency } from 'app/shared/model/user-agency.model';

export interface IAgency {
  id?: number;
  name?: string;
  link?: string;
  description?: string;
  logopath?: string;
  license?: string;
  licenseId?: number;
  uri?: string;
  uriCode?: string;
  canonicalUri?: string;
  userAgencies?: UserAgency[];
  deletable?: boolean;
}

export class Agency implements IAgency {
  constructor(
    public id?: number,
    public name?: string,
    public link?: string,
    public description?: string,
    public logopath?: string,
    public license?: string,
    public licenseId?: number,
    public uri?: string,
    public uriCode?: string,
    public canonicalUri?: string,
    public userAgencies: UserAgency[] = [],
    public deletable?: boolean,
  ) {}
}
