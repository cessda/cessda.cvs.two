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
import { map } from 'rxjs/operators';
import moment from 'moment';

import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, SearchWithPagination } from 'app/shared/util/request-util';
import { VocabularyChange } from 'app/shared/model/vocabulary-change.model';

type EntityResponseType = HttpResponse<VocabularyChange>;
type EntityArrayResponseType = HttpResponse<VocabularyChange[]>;

@Injectable({ providedIn: 'root' })
export class VocabularyChangeService {
  public resourceUrl = SERVER_API_URL + 'api/vocabulary-changes';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/vocabulary-changes';

  constructor(protected http: HttpClient) {}

  create(vocabularyChange: VocabularyChange): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(vocabularyChange);
    return this.http
      .post<VocabularyChange>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(vocabularyChange: VocabularyChange): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(vocabularyChange);
    return this.http
      .put<VocabularyChange>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<VocabularyChange>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<VocabularyChange[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<object>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<VocabularyChange[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  protected convertDateFromClient(vocabularyChange: VocabularyChange): VocabularyChange {
    const copy: VocabularyChange = Object.assign({}, vocabularyChange, {
      date: vocabularyChange.date && vocabularyChange.date.isValid() ? vocabularyChange.date.format(DATE_FORMAT) : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date = res.body.date ? moment(res.body.date) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((vocabularyChange: VocabularyChange) => {
        vocabularyChange.date = vocabularyChange.date ? moment(vocabularyChange.date) : undefined;
      });
    }
    return res;
  }

  getByVersionId(versionId: number): Observable<EntityArrayResponseType> {
    return this.http.get<VocabularyChange[]>(`${this.resourceUrl}/version-id/${versionId}`, { observe: 'response' });
  }
}
