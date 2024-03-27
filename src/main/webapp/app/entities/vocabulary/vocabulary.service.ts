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
import { Vocabulary } from 'app/shared/model/vocabulary.model';

type EntityResponseType = HttpResponse<Vocabulary>;
type EntityArrayResponseType = HttpResponse<Vocabulary[]>;

@Injectable({ providedIn: 'root' })
export class VocabularyService {
  public resourceUrl = SERVER_API_URL + 'api/vocabularies';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/vocabularies';

  constructor(protected http: HttpClient) {}

  create(vocabulary: Vocabulary): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(vocabulary);
    return this.http
      .post<Vocabulary>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(vocabulary: Vocabulary): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(vocabulary);
    return this.http
      .put<Vocabulary>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<Vocabulary>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<Vocabulary[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<unknown>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<Vocabulary[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  protected convertDateFromClient(vocabulary: Vocabulary): Vocabulary {
    const copy: Vocabulary = Object.assign({}, vocabulary, {
      publicationDate:
        vocabulary.publicationDate && vocabulary.publicationDate.isValid() ? vocabulary.publicationDate.format(DATE_FORMAT) : undefined,
      lastModified: vocabulary.lastModified && vocabulary.lastModified.isValid() ? vocabulary.lastModified.toJSON() : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.publicationDate = res.body.publicationDate ? moment(res.body.publicationDate) : undefined;
      res.body.lastModified = res.body.lastModified ? moment(res.body.lastModified) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((vocabulary: Vocabulary) => {
        vocabulary.publicationDate = vocabulary.publicationDate ? moment(vocabulary.publicationDate) : undefined;
        vocabulary.lastModified = vocabulary.lastModified ? moment(vocabulary.lastModified) : undefined;
      });
    }
    return res;
  }
}
