import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, SearchWithPagination } from 'app/shared/util/request-util';
import { IUserAgency } from 'app/shared/model/user-agency.model';

type EntityResponseType = HttpResponse<IUserAgency>;
type EntityArrayResponseType = HttpResponse<IUserAgency[]>;

@Injectable({ providedIn: 'root' })
export class UserAgencyService {
  public resourceUrl = SERVER_API_URL + 'api/user-agencies';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/user-agencies';

  constructor(protected http: HttpClient) {}

  create(userAgency: IUserAgency): Observable<EntityResponseType> {
    return this.http.post<IUserAgency>(this.resourceUrl, userAgency, { observe: 'response' });
  }

  update(userAgency: IUserAgency): Observable<EntityResponseType> {
    return this.http.put<IUserAgency>(this.resourceUrl, userAgency, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IUserAgency>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUserAgency[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUserAgency[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }
}
