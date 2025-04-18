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
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, SearchWithPagination } from 'app/shared/util/request-util';
import { MetadataValue } from 'app/shared/model/metadata-value.model';

type EntityResponseType = HttpResponse<MetadataValue>;
type EntityArrayResponseType = HttpResponse<MetadataValue[]>;

@Injectable({ providedIn: 'root' })
export class MetadataValueService {
  public resourceUrl = SERVER_API_URL + 'api/metadata-values';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/metadata-values';

  constructor(protected http: HttpClient) {}

  create(metadataValue: MetadataValue): Observable<EntityResponseType> {
    return this.http.post<MetadataValue>(this.resourceUrl, metadataValue, { observe: 'response' });
  }

  update(metadataValue: MetadataValue): Observable<EntityResponseType> {
    return this.http.put<MetadataValue>(this.resourceUrl, metadataValue, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<MetadataValue>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<MetadataValue[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<object>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<MetadataValue[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }
}
