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
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, SearchWithPagination } from 'app/shared/util/request-util';
import { Resolver } from 'app/shared/model/resolver.model';

type EntityResponseType = HttpResponse<Resolver>;
type EntityArrayResponseType = HttpResponse<Resolver[]>;

@Injectable({ providedIn: 'root' })
export class ResolverService {
  protected http = inject(HttpClient);

  public resourceUrl = SERVER_API_URL + 'api/resolvers';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/resolvers';

  create(resolver: Resolver): Observable<EntityResponseType> {
    return this.http.post<Resolver>(this.resourceUrl, resolver, { observe: 'response' });
  }

  update(resolver: Resolver): Observable<EntityResponseType> {
    return this.http.put<Resolver>(this.resourceUrl, resolver, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<Resolver>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<Resolver[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<object>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<Resolver[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }
}
