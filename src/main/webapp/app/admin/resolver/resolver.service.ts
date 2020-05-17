import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, SearchWithPagination } from 'app/shared/util/request-util';
import { IResolver } from 'app/shared/model/resolver.model';

type EntityResponseType = HttpResponse<IResolver>;
type EntityArrayResponseType = HttpResponse<IResolver[]>;

@Injectable({ providedIn: 'root' })
export class ResolverService {
  public resourceUrl = SERVER_API_URL + 'api/resolvers';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/resolvers';

  constructor(protected http: HttpClient) {}

  create(resolver: IResolver): Observable<EntityResponseType> {
    return this.http.post<IResolver>(this.resourceUrl, resolver, { observe: 'response' });
  }

  update(resolver: IResolver): Observable<EntityResponseType> {
    return this.http.put<IResolver>(this.resourceUrl, resolver, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IResolver>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IResolver[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IResolver[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }
}
