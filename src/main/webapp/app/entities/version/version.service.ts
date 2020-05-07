import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, SearchWithPagination } from 'app/shared/util/request-util';
import { IVersion } from 'app/shared/model/version.model';

type EntityResponseType = HttpResponse<IVersion>;
type EntityArrayResponseType = HttpResponse<IVersion[]>;

@Injectable({ providedIn: 'root' })
export class VersionService {
  public resourceUrl = SERVER_API_URL + 'api/versions';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/versions';

  constructor(protected http: HttpClient) {}

  create(version: IVersion): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(version);
    return this.http
      .post<IVersion>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(version: IVersion): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(version);
    return this.http
      .put<IVersion>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IVersion>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IVersion[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IVersion[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  protected convertDateFromClient(version: IVersion): IVersion {
    const copy: IVersion = Object.assign({}, version, {
      publicationDate:
        version.publicationDate && version.publicationDate.isValid() ? version.publicationDate.format(DATE_FORMAT) : undefined,
      lastModified: version.lastModified && version.lastModified.isValid() ? version.lastModified.toJSON() : undefined
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
      res.body.forEach((version: IVersion) => {
        version.publicationDate = version.publicationDate ? moment(version.publicationDate) : undefined;
        version.lastModified = version.lastModified ? moment(version.lastModified) : undefined;
      });
    }
    return res;
  }
}
