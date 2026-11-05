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
import { Router } from '@angular/router';

import { AuthExpiredInterceptor } from 'app/blocks/interceptor/auth-expired.interceptor';
import { LoginService } from 'app/core/login/login.service';
import { LoginModalService } from 'app/core/login/login-modal.service';
import { StateStorageService } from 'app/core/auth/state-storage.service';

describe('Interceptor Tests', () => {
  describe('Auth Expired Interceptor', () => {
    let http: HttpClient;
    let httpMock: HttpTestingController;

    let logout: jasmine.Spy;
    let open: jasmine.Spy;
    let storeUrl: jasmine.Spy;
    let navigate: jasmine.Spy;

    const failWith = (url: string, status: number): void => {
      http.get(url).subscribe({ error: () => undefined });
      httpMock.expectOne(url).error(new ProgressEvent('error'), { status, statusText: 'Error' });
    };

    beforeEach(() => {
      logout = jasmine.createSpy('logout');
      open = jasmine.createSpy('open');
      storeUrl = jasmine.createSpy('storeUrl');
      navigate = jasmine.createSpy('navigate');

      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [
          { provide: LoginService, useValue: { logout } },
          { provide: LoginModalService, useValue: { open } },
          { provide: StateStorageService, useValue: { storeUrl } },
          { provide: Router, useValue: { navigate, routerState: { snapshot: { url: '/editor/vocabulary' } } } },
          { provide: HTTP_INTERCEPTORS, useClass: AuthExpiredInterceptor, multi: true },
        ],
      });

      http = TestBed.inject(HttpClient);
      httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
      httpMock.verify();
    });

    describe('when a request is refused as unauthorised', () => {
      beforeEach(() => {
        failWith('api/vocabularies', 401);
      });

      it('should remember the page the user was on', () => {
        expect(storeUrl).toHaveBeenCalledWith('/editor/vocabulary');
      });

      it('should sign the user out', () => {
        expect(logout).toHaveBeenCalled();
      });

      it('should send them to the home page', () => {
        expect(navigate).toHaveBeenCalledWith(['']);
      });

      it('should offer them the login dialog', () => {
        expect(open).toHaveBeenCalled();
      });
    });

    it('should leave a refusal from the account endpoint alone', () => {
      failWith('api/account', 401);

      expect(logout).not.toHaveBeenCalled();
      expect(open).not.toHaveBeenCalled();
    });

    it('should leave any other failure alone', () => {
      failWith('api/vocabularies', 500);

      expect(logout).not.toHaveBeenCalled();
      expect(open).not.toHaveBeenCalled();
    });

    it('should leave a request that succeeds alone', () => {
      http.get('api/vocabularies').subscribe();
      httpMock.expectOne('api/vocabularies').flush({});

      expect(logout).not.toHaveBeenCalled();
      expect(open).not.toHaveBeenCalled();
    });
  });
});
