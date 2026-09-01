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
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import {
  LocalStorageService,
  SessionStorageService,
  provideNgxWebstorage,
  withLocalStorage,
  withNgxWebstorageConfig,
  withSessionStorage,
} from 'ngx-webstorage';

import { SERVER_API_URL } from 'app/app.constants';
import { AuthServerProvider } from 'app/core/auth/auth-jwt.service';
import { Login } from 'app/core/login/login.model';

describe('Service Tests', () => {
  describe('Auth JWT Service', () => {
    let service: AuthServerProvider;
    let httpMock: HttpTestingController;
    let localStorage: LocalStorageService;
    let sessionStorage: SessionStorageService;

    const credentials = (rememberMe: boolean): Login => ({ username: 'admin', password: 'admin', rememberMe });

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [
          // the same registration the application uses, so the prefixes match
          provideNgxWebstorage(withNgxWebstorageConfig({ prefix: 'jhi', separator: '-' }), withLocalStorage(), withSessionStorage()),
        ],
      });

      service = TestBed.inject(AuthServerProvider);
      httpMock = TestBed.inject(HttpTestingController);
      localStorage = TestBed.inject(LocalStorageService);
      sessionStorage = TestBed.inject(SessionStorageService);

      localStorage.clear('authenticationToken');
      sessionStorage.clear('authenticationToken');
    });

    afterEach(() => {
      httpMock.verify();
    });

    describe('getToken', () => {
      it('should return an empty string when nothing is stored', () => {
        expect(service.getToken()).toBe('');
      });

      it('should read a remembered token from local storage', () => {
        localStorage.store('authenticationToken', 'remembered');

        expect(service.getToken()).toBe('remembered');
      });

      it('should read a session token when nothing is remembered', () => {
        sessionStorage.store('authenticationToken', 'session-only');

        expect(service.getToken()).toBe('session-only');
      });

      it('should prefer the remembered token over the session one', () => {
        localStorage.store('authenticationToken', 'remembered');
        sessionStorage.store('authenticationToken', 'session-only');

        expect(service.getToken()).toBe('remembered');
      });
    });

    describe('login', () => {
      it('should post the credentials to the authenticate endpoint', () => {
        service.login(credentials(false)).subscribe();

        const req = httpMock.expectOne({ method: 'POST', url: SERVER_API_URL + 'api/authenticate' });
        expect(req.request.body).toEqual({ username: 'admin', password: 'admin', rememberMe: false });
        req.flush({ id_token: 'a-token' });
      });

      it('should keep the token for the session only when not remembering', () => {
        service.login(credentials(false)).subscribe();
        httpMock.expectOne({ method: 'POST' }).flush({ id_token: 'a-token' });

        expect(sessionStorage.retrieve('authenticationToken')).toBe('a-token');
        expect(localStorage.retrieve('authenticationToken')).toBeNull();
      });

      it('should remember the token across sessions when asked to', () => {
        service.login(credentials(true)).subscribe();
        httpMock.expectOne({ method: 'POST' }).flush({ id_token: 'a-token' });

        expect(localStorage.retrieve('authenticationToken')).toBe('a-token');
        expect(sessionStorage.retrieve('authenticationToken')).toBeNull();
      });

      it('should not store anything when the credentials are rejected', () => {
        service.login(credentials(true)).subscribe({ error: () => undefined });
        httpMock.expectOne({ method: 'POST' }).flush({}, { status: 401, statusText: 'Unauthorized' });

        expect(localStorage.retrieve('authenticationToken')).toBeNull();
        expect(sessionStorage.retrieve('authenticationToken')).toBeNull();
      });
    });

    describe('logout', () => {
      it('should clear the token from both storages', () => {
        localStorage.store('authenticationToken', 'remembered');
        sessionStorage.store('authenticationToken', 'session-only');

        service.logout().subscribe();

        expect(service.getToken()).toBe('');
      });

      it('should complete without emitting a value', () => {
        let emitted = false;
        let completed = false;

        service.logout().subscribe({
          next: () => (emitted = true),
          complete: () => (completed = true),
        });

        expect(emitted).toBe(false);
        expect(completed).toBe(true);
      });
    });
  });
});
