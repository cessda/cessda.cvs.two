import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, SearchWithPagination } from 'app/shared/util/request-util';
import { ILicence } from 'app/shared/model/licence.model';

type EntityResponseType = HttpResponse<ILicence>;
type EntityArrayResponseType = HttpResponse<ILicence[]>;

@Injectable({ providedIn: 'root' })
export class LicenceService {
  public resourceUrl = SERVER_API_URL + 'api/licences';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/licences';

  constructor(protected http: HttpClient) {}

  create(licence: ILicence): Observable<EntityResponseType> {
    return this.http.post<ILicence>(this.resourceUrl, licence, { observe: 'response' });
  }

  update(licence: ILicence): Observable<EntityResponseType> {
    return this.http.put<ILicence>(this.resourceUrl, licence, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILicence>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILicence[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILicence[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }
}
