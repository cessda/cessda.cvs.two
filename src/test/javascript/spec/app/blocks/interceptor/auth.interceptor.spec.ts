/*
 * Copyright © 2017-2026 CESSDA ERIC (support@cessda.eu)
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
import { HTTP_INTERCEPTORS, HttpClient } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { LocalStorageService, SessionStorageService } from 'ngx-webstorage';

import { AuthInterceptor } from 'app/blocks/interceptor/auth.interceptor';

describe('Interceptor Tests', () => {
  describe('Auth Interceptor', () => {
    let http: HttpClient;
    let httpMock: HttpTestingController;

    let localToken: string | null;
    let sessionToken: string | null;

    // only the one key is ever asked for, so the stubs answer for it and nothing else
    const storageStub = (read: () => string | null): Partial<LocalStorageService> => ({
      retrieve: (key: string) => (key === 'authenticationToken' ? read() : null),
    });

    const sent = (url: string): string | null => {
      http.get(url).subscribe();
      const req = httpMock.expectOne(url);
      const header = req.request.headers.get('Authorization');
      req.flush({});
      return header;
    };

    beforeEach(() => {
      localToken = null;
      sessionToken = null;

      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [
          { provide: LocalStorageService, useValue: storageStub(() => localToken) },
          { provide: SessionStorageService, useValue: storageStub(() => sessionToken) },
          { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
        ],
      });

      http = TestBed.inject(HttpClient);
      httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
      httpMock.verify();
    });

    it('should send the token it finds in local storage', () => {
      localToken = 'a-token';

      expect(sent('api/vocabularies')).toBe('Bearer a-token');
    });

    it('should fall back to the token in session storage', () => {
      sessionToken = 'a-session-token';

      expect(sent('api/vocabularies')).toBe('Bearer a-session-token');
    });

    it('should prefer local storage when both hold one', () => {
      localToken = 'a-token';
      sessionToken = 'a-session-token';

      expect(sent('api/vocabularies')).toBe('Bearer a-token');
    });

    it('should send no authorisation when neither holds a token', () => {
      expect(sent('api/vocabularies')).toBeNull();
    });

    it('should leave a request to another host alone', () => {
      localToken = 'a-token';

      expect(sent('https://example.org/data')).toBeNull();
    });
  });
});
