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
import { createRequestOption, Pagination } from 'app/shared/util/request-util';
import { User } from './user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  public resourceUrl = SERVER_API_URL + 'api/users';

  constructor(private http: HttpClient) {}

  create(user: User): Observable<User> {
    return this.http.post<User>(this.resourceUrl, user);
  }

  update(user: User): Observable<User> {
    return this.http.put<User>(this.resourceUrl, user);
  }

  find(login: string): Observable<User> {
    return this.http.get<User>(`${this.resourceUrl}/${login}`);
  }

  query(req?: Pagination): Observable<HttpResponse<User[]>> {
    const options = createRequestOption(req);
    return this.http.get<User[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(login: string) {
    return this.http.delete(`${this.resourceUrl}/${login}`);
  }

  authorities(): Observable<string[]> {
    return this.http.get<string[]>(SERVER_API_URL + 'api/users/authorities');
  }
}
