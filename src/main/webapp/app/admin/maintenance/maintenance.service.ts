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

import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {SERVER_API_URL} from 'app/app.constants';
import {Maintenance} from 'app/admin/maintenance/maintenance.model';

type EntityResponseType = HttpResponse<Maintenance>;

@Injectable({ providedIn: 'root' })
export class MaintenanceService {
  public maintenanceUrl = SERVER_API_URL + 'api/maintenance';

  constructor(private http: HttpClient) {}

  generateJson(): Observable<EntityResponseType> {
    return this.http.post<Maintenance>(`${this.maintenanceUrl}/publication/generate-json`, null, { observe: 'response' });
  }

  indexAgency(): Observable<EntityResponseType> {
    return this.http.post<Maintenance>(`${this.maintenanceUrl}/index/agency`, null, { observe: 'response' });
  }

  indexAgencyStats(): Observable<EntityResponseType> {
    return this.http.post<Maintenance>(`${this.maintenanceUrl}/index/agency-stats`, null, { observe: 'response' });
  }

  indexVocabularyPublish(): Observable<EntityResponseType> {
    return this.http.post<Maintenance>(`${this.maintenanceUrl}/index/vocabulary`, null, { observe: 'response' });
  }

  indexVocabularyEditor(): Observable<EntityResponseType> {
    return this.http.post<Maintenance>(`${this.maintenanceUrl}/index/vocabulary/editor`, null, { observe: 'response' });
  }

  task398(): Observable<EntityResponseType> {
    return this.http.post<Maintenance>(`${this.maintenanceUrl}/task/398`, null, { observe: 'response' });
  }
}
