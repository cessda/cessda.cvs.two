import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, SearchWithPagination } from 'app/shared/util/request-util';
import { IMetadataField } from 'app/shared/model/metadata-field.model';

type EntityResponseType = HttpResponse<IMetadataField>;
type EntityArrayResponseType = HttpResponse<IMetadataField[]>;

@Injectable({ providedIn: 'root' })
export class MetadataFieldService {
  public resourceUrl = SERVER_API_URL + 'api/metadata-fields';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/metadata-fields';

  constructor(protected http: HttpClient) {}

  create(metadataField: IMetadataField): Observable<EntityResponseType> {
    return this.http.post<IMetadataField>(this.resourceUrl, metadataField, { observe: 'response' });
  }

  update(metadataField: IMetadataField): Observable<EntityResponseType> {
    return this.http.put<IMetadataField>(this.resourceUrl, metadataField, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMetadataField>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMetadataField[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMetadataField[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }
}
