import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, SearchWithPagination } from 'app/shared/util/request-util';
import { IMetadataValue } from 'app/shared/model/metadata-value.model';

type EntityResponseType = HttpResponse<IMetadataValue>;
type EntityArrayResponseType = HttpResponse<IMetadataValue[]>;

@Injectable({ providedIn: 'root' })
export class MetadataValueService {
  public resourceUrl = SERVER_API_URL + 'api/metadata-values';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/metadata-values';

  constructor(protected http: HttpClient) {}

  create(metadataValue: IMetadataValue): Observable<EntityResponseType> {
    return this.http.post<IMetadataValue>(this.resourceUrl, metadataValue, { observe: 'response' });
  }

  update(metadataValue: IMetadataValue): Observable<EntityResponseType> {
    return this.http.put<IMetadataValue>(this.resourceUrl, metadataValue, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMetadataValue>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMetadataValue[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMetadataValue[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }
}
