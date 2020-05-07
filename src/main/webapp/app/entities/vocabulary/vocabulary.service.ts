import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, SearchWithPagination } from 'app/shared/util/request-util';
import { IVocabulary } from 'app/shared/model/vocabulary.model';

type EntityResponseType = HttpResponse<IVocabulary>;
type EntityArrayResponseType = HttpResponse<IVocabulary[]>;

@Injectable({ providedIn: 'root' })
export class VocabularyService {
  public resourceUrl = SERVER_API_URL + 'api/vocabularies';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/vocabularies';

  constructor(protected http: HttpClient) {}

  create(vocabulary: IVocabulary): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(vocabulary);
    return this.http
      .post<IVocabulary>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(vocabulary: IVocabulary): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(vocabulary);
    return this.http
      .put<IVocabulary>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IVocabulary>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IVocabulary[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IVocabulary[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  protected convertDateFromClient(vocabulary: IVocabulary): IVocabulary {
    const copy: IVocabulary = Object.assign({}, vocabulary, {
      publicationDate:
        vocabulary.publicationDate && vocabulary.publicationDate.isValid() ? vocabulary.publicationDate.format(DATE_FORMAT) : undefined,
      lastModified: vocabulary.lastModified && vocabulary.lastModified.isValid() ? vocabulary.lastModified.toJSON() : undefined
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
      res.body.forEach((vocabulary: IVocabulary) => {
        vocabulary.publicationDate = vocabulary.publicationDate ? moment(vocabulary.publicationDate) : undefined;
        vocabulary.lastModified = vocabulary.lastModified ? moment(vocabulary.lastModified) : undefined;
      });
    }
    return res;
  }
}
