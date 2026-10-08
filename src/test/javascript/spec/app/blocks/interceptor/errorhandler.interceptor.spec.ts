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
import { JhiEventManager } from 'ng-jhipster';

import { ErrorHandlerInterceptor } from 'app/blocks/interceptor/errorhandler.interceptor';

describe('Interceptor Tests', () => {
  describe('Error Handler Interceptor', () => {
    let http: HttpClient;
    let httpMock: HttpTestingController;
    let broadcast: jasmine.Spy;

    const failWith = (url: string, status: number): void => {
      http.get(url).subscribe({ error: () => undefined });
      httpMock.expectOne(url).error(new ProgressEvent('error'), { status, statusText: 'Error' });
    };

    beforeEach(() => {
      broadcast = jasmine.createSpy('broadcast');

      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [
          { provide: JhiEventManager, useValue: { broadcast } },
          { provide: HTTP_INTERCEPTORS, useClass: ErrorHandlerInterceptor, multi: true },
        ],
      });

      http = TestBed.inject(HttpClient);
      httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
      httpMock.verify();
    });

    it('should announce a server error', () => {
      failWith('api/vocabularies', 500);

      expect(broadcast).toHaveBeenCalled();
    });

    it('should announce it under the name the application listens for', () => {
      failWith('api/vocabularies', 500);

      expect(broadcast.calls.mostRecent().args[0].name).toBe('cvsApp.httpError');
    });

    it('should carry the response along so the alert can describe it', () => {
      failWith('api/vocabularies', 404);

      expect(broadcast.calls.mostRecent().args[0].content.status).toBe(404);
    });

    it('should announce a refusal on an ordinary endpoint', () => {
      failWith('api/vocabularies', 401);

      expect(broadcast).toHaveBeenCalled();
    });

    it('should stay quiet when the account endpoint reports nobody is signed in', () => {
      failWith('api/account', 401);

      expect(broadcast).not.toHaveBeenCalled();
    });

    it('should stay quiet on a request that succeeds', () => {
      http.get('api/vocabularies').subscribe();
      httpMock.expectOne('api/vocabularies').flush({});

      expect(broadcast).not.toHaveBeenCalled();
    });
  });
});
