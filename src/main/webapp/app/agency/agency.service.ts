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
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, SearchWithPagination } from 'app/shared/util/request-util';
import { IAgency } from 'app/shared/model/agency.model';
import { AgencyStat } from 'app/shared/model/agencystat.model';

@Injectable({ providedIn: 'root' })
export class AgencyService {
  public resourceUrl = SERVER_API_URL + 'api/agencies';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/agencies';
  public resourceStatUrl = SERVER_API_URL + 'api/agencies-stat';

  constructor(protected http: HttpClient) {}

  create(agency: IAgency) {
    return this.http.post<IAgency>(this.resourceUrl, agency, { observe: 'response' });
  }

  update(agency: IAgency) {
    return this.http.put<IAgency>(this.resourceUrl, agency, { observe: 'response' });
  }

  find(id: number) {
    return this.http.get<IAgency>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any) {
    const options = createRequestOption(req);
    return this.http.get<IAgency[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number) {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination) {
    const options = createRequestOption(req);
    return this.http.get<IAgency[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  statistic(id: number) {
    return this.http.get<AgencyStat>(`${this.resourceStatUrl}/${id}`, { observe: 'response' });
  }
}
