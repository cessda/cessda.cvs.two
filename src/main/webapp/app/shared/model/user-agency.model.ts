import { AgencyRole } from './enumerations/agency-role.model';

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
export interface IUserAgency {
  id?: number;
  userId?: number;
  agencyName?: string;
  agencyRole?: AgencyRole;
  language?: string;
  agencyId?: number;
}

export class UserAgency implements IUserAgency {
  constructor(
    public id?: number,
    public userId?: number,
    public agency?: string,
    public agencyRole?: AgencyRole,
    public language?: string,
    public agencyId?: number,
  ) {}
}
