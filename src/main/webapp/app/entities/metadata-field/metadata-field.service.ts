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

import { SERVER_API_URL } from 'app/app.constants';
import { MetadataField } from 'app/shared/model/metadata-field.model';

type EntityResponseType = HttpResponse<MetadataField>;
@Injectable({ providedIn: 'root' })
export class MetadataFieldService {
  public resourceUrl = SERVER_API_URL + 'api/metadata-fields';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/metadata-fields';

  constructor(protected http: HttpClient) {}

  findByKey(metadataKey: string): Observable<EntityResponseType> {
    return this.http.get<MetadataField>(`${this.resourceUrl}/metadata-key/${metadataKey}`, { observe: 'response' });
  }

  downloadMetadataFile(metadataKey: string, mimeType: string): Observable<Blob> {
    return this.http.get(`${this.resourceUrl}/download/${metadataKey}`, {
      responseType: 'blob',
      headers: { accept: mimeType },
    });
  }
}
