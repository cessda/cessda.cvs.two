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
import { Concept } from 'app/shared/model/concept.model';

type EntityResponseType = HttpResponse<Concept>;
type EntityArrayResponseType = HttpResponse<Concept[]>;

@Injectable({ providedIn: 'root' })
export class ConceptService {
  public resourceUrl = SERVER_API_URL + 'api/concepts';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/concepts';

  constructor(protected http: HttpClient) {}

  create(concept: Concept): Observable<EntityResponseType> {
    return this.http.post<Concept>(this.resourceUrl, concept, { observe: 'response' });
  }

  update(concept: Concept): Observable<EntityResponseType> {
    return this.http.put<Concept>(this.resourceUrl, concept, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<Concept>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<Concept[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<object>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<Concept[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }
}
