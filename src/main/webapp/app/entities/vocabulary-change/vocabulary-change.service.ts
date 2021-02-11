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

import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, SearchWithPagination } from 'app/shared/util/request-util';
import { IVocabularyChange } from 'app/shared/model/vocabulary-change.model';

type EntityResponseType = HttpResponse<IVocabularyChange>;
type EntityArrayResponseType = HttpResponse<IVocabularyChange[]>;

@Injectable({ providedIn: 'root' })
export class VocabularyChangeService {
  public resourceUrl = SERVER_API_URL + 'api/vocabulary-changes';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/vocabulary-changes';

  constructor(protected http: HttpClient) {}

  create(vocabularyChange: IVocabularyChange): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(vocabularyChange);
    return this.http
      .post<IVocabularyChange>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(vocabularyChange: IVocabularyChange): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(vocabularyChange);
    return this.http
      .put<IVocabularyChange>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IVocabularyChange>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IVocabularyChange[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IVocabularyChange[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  protected convertDateFromClient(vocabularyChange: IVocabularyChange): IVocabularyChange {
    const copy: IVocabularyChange = Object.assign({}, vocabularyChange, {
      date: vocabularyChange.date && vocabularyChange.date.isValid() ? vocabularyChange.date.format(DATE_FORMAT) : undefined
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
      res.body.forEach((vocabularyChange: IVocabularyChange) => {
        vocabularyChange.date = vocabularyChange.date ? moment(vocabularyChange.date) : undefined;
      });
    }
    return res;
  }

  getByVersionId(versionId: number): Observable<EntityArrayResponseType> {
    return this.http.get<IVocabularyChange[]>(`${this.resourceUrl}/version-id/${versionId}`, { observe: 'response' });
  }
}
