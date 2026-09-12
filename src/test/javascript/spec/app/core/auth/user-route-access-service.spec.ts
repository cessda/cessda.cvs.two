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
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { of } from 'rxjs';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { AccountService } from 'app/core/auth/account.service';
import { LoginModalService } from 'app/core/login/login-modal.service';
import { StateStorageService } from 'app/core/auth/state-storage.service';
import { Account } from 'app/core/user/account.model';
import { Authority } from 'app/shared/constants/authority.constants';

describe('Service Tests', () => {
  describe('User Route Access Service', () => {
    let service: UserRouteAccessService;

    let identity: jasmine.Spy;
    let hasAnyAuthority: jasmine.Spy;
    let navigate: jasmine.Spy;
    let storeUrl: jasmine.Spy;
    let open: jasmine.Spy;

    const account = { login: 'admin' } as Account;

    const allowed = (authorities: Authority[], url = '/admin/licence'): boolean => {
      let granted!: boolean;
      service.checkLogin(authorities, url).subscribe(value => (granted = value));
      return granted;
    };

    beforeEach(() => {
      identity = jasmine.createSpy('identity').and.returnValue(of(account));
      hasAnyAuthority = jasmine.createSpy('hasAnyAuthority').and.returnValue(true);
      navigate = jasmine.createSpy('navigate');
      storeUrl = jasmine.createSpy('storeUrl');
      open = jasmine.createSpy('open');

      // isDevMode is true under test, so the guard logs a refusal it would not log in production
      spyOn(console, 'error');

      TestBed.configureTestingModule({
        providers: [
          { provide: AccountService, useValue: { identity, hasAnyAuthority } },
          { provide: LoginModalService, useValue: { open } },
          { provide: StateStorageService, useValue: { storeUrl } },
          { provide: Router, useValue: { navigate } },
        ],
      });

      service = TestBed.inject(UserRouteAccessService);
    });

    describe('a route that asks for no authority', () => {
      it('should let anyone through', () => {
        expect(allowed([])).toBe(true);
      });

      it('should let someone through who is not signed in', () => {
        identity.and.returnValue(of(null));

        expect(allowed([])).toBe(true);
      });

      it('should not even ask what authorities they hold', () => {
        allowed([]);

        expect(hasAnyAuthority).not.toHaveBeenCalled();
      });
    });

    describe('a signed in user', () => {
      it('should be let through when they hold one of the authorities', () => {
        hasAnyAuthority.and.returnValue(true);

        expect(allowed([Authority.ADMIN])).toBe(true);
      });

      it('should be asked about the authorities the route names', () => {
        allowed([Authority.ADMIN, Authority.USER]);

        expect(hasAnyAuthority).toHaveBeenCalledWith([Authority.ADMIN, Authority.USER]);
      });

      it('should be refused when they hold none of them', () => {
        hasAnyAuthority.and.returnValue(false);

        expect(allowed([Authority.ADMIN])).toBe(false);
      });

      it('should be sent to the access denied page when refused', () => {
        hasAnyAuthority.and.returnValue(false);

        allowed([Authority.ADMIN]);

        expect(navigate).toHaveBeenCalledWith(['accessdenied'], { skipLocationChange: true });
      });

      it('should not be offered the login dialog when refused', () => {
        hasAnyAuthority.and.returnValue(false);

        allowed([Authority.ADMIN]);

        expect(open).not.toHaveBeenCalled();
      });
    });

    describe('a visitor who is not signed in', () => {
      beforeEach(() => {
        identity.and.returnValue(of(null));
      });

      it('should be refused', () => {
        expect(allowed([Authority.ADMIN])).toBe(false);
      });

      it('should have the page they wanted remembered', () => {
        allowed([Authority.ADMIN], '/editor/vocabulary');

        expect(storeUrl).toHaveBeenCalledWith('/editor/vocabulary');
      });

      it('should be sent to the home page', () => {
        allowed([Authority.ADMIN]);

        expect(navigate).toHaveBeenCalledWith(['']);
      });

      it('should be offered the login dialog', () => {
        allowed([Authority.ADMIN]);

        expect(open).toHaveBeenCalled();
      });
    });

    describe('guarding a route', () => {
      it('should check against the authorities the route carries', () => {
        const route = { data: { authorities: [Authority.ADMIN] } } as unknown as ActivatedRouteSnapshot;
        const state = { url: '/admin/licence' } as RouterStateSnapshot;
        hasAnyAuthority.and.returnValue(false);

        let granted!: boolean;
        service.canActivate(route, state).subscribe(value => (granted = value));

        expect(hasAnyAuthority).toHaveBeenCalledWith([Authority.ADMIN]);
        expect(granted).toBe(false);
      });

      it('should remember the url the route was asked for', () => {
        const route = { data: { authorities: [Authority.ADMIN] } } as unknown as ActivatedRouteSnapshot;
        const state = { url: '/admin/resolver' } as RouterStateSnapshot;
        identity.and.returnValue(of(null));

        service.canActivate(route, state).subscribe();

        expect(storeUrl).toHaveBeenCalledWith('/admin/resolver');
      });
    });
  });
});
