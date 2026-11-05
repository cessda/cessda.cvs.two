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
import { firstValueFrom } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { MockAccountService } from '../../../helpers/mock-account.service';
import { MockRouter } from '../../../helpers/mock-route.service';
import { MockStateStorageService } from '../../../helpers/mock-state-storage.service';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { AccountService } from 'app/core/auth/account.service';
import { StateStorageService } from 'app/core/auth/state-storage.service';
import { LoginModalService } from 'app/core/login/login-modal.service';
import { Account } from 'app/core/user/account.model';
import { Authority } from 'app/shared/constants/authority.constants';

describe('Service Tests', () => {
  describe('User Route Access Service', () => {
    let service: UserRouteAccessService;
    let mockAccountService: MockAccountService;
    let mockRouter: MockRouter;
    let mockStateStorageService: MockStateStorageService;
    let loginModalService: { open: jest.Mock };
    let hasAnyAuthoritySpy: jasmine.Spy;

    const account = { login: 'user', authorities: [Authority.USER] } as Account;

    const routeRequiring = (authorities?: Authority[]): ActivatedRouteSnapshot =>
      ({ data: authorities === undefined ? {} : { authorities } }) as unknown as ActivatedRouteSnapshot;
    const stateAt = (url: string): RouterStateSnapshot => ({ url }) as RouterStateSnapshot;

    beforeEach(() => {
      loginModalService = { open: jest.fn() };
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        providers: [
          { provide: StateStorageService, useClass: MockStateStorageService },
          { provide: LoginModalService, useValue: loginModalService },
        ],
      });

      service = TestBed.inject(UserRouteAccessService);
      mockAccountService = TestBed.inject(AccountService) as unknown as MockAccountService;
      mockRouter = TestBed.inject(Router) as unknown as MockRouter;
      mockStateStorageService = TestBed.inject(StateStorageService) as unknown as MockStateStorageService;
      hasAnyAuthoritySpy = mockAccountService.spy('hasAnyAuthority').andReturn(false);
    });

    afterEach(() => {
      jest.restoreAllMocks();
    });

    describe('a route without required authorities', () => {
      it.each([undefined, []])('should let anyone in when authorities are %p', async authorities => {
        mockAccountService.setIdentityResponse(null);

        const result = await firstValueFrom(service.canActivate(routeRequiring(authorities), stateAt('/about')));

        expect(result).toBe(true);
        expect(mockRouter.navigateSpy).not.toHaveBeenCalled();
        expect(loginModalService.open).not.toHaveBeenCalled();
        expect(mockStateStorageService.storeUrlSpy).not.toHaveBeenCalled();
        // there is nothing to check the authorities against
        expect(hasAnyAuthoritySpy).not.toHaveBeenCalled();
      });

      it('should still resolve the identity so a server-side login is picked up', async () => {
        await firstValueFrom(service.canActivate(routeRequiring(), stateAt('/about')));

        expect(mockAccountService.identitySpy).toHaveBeenCalled();
      });
    });

    describe('a route with required authorities', () => {
      const required = [Authority.ADMIN, Authority.USER];

      it('should let a user with one of the authorities in', async () => {
        mockAccountService.setIdentityResponse(account);
        hasAnyAuthoritySpy.and.returnValue(true);

        const result = await firstValueFrom(service.canActivate(routeRequiring(required), stateAt('/editor')));

        expect(result).toBe(true);
        expect(hasAnyAuthoritySpy).toHaveBeenCalledWith(required);
        expect(mockRouter.navigateSpy).not.toHaveBeenCalled();
      });

      it('should send a user without the authorities to the access denied page', async () => {
        jest.spyOn(console, 'error').mockImplementation(() => undefined);
        mockAccountService.setIdentityResponse(account);
        hasAnyAuthoritySpy.and.returnValue(false);

        const result = await firstValueFrom(service.canActivate(routeRequiring([Authority.ADMIN]), stateAt('/admin/user-management')));

        expect(result).toBe(false);
        expect(hasAnyAuthoritySpy).toHaveBeenCalledWith([Authority.ADMIN]);
        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['accessdenied'], { skipLocationChange: true });
        // a logged-in user is not asked to log in again, and the url is not kept for later
        expect(loginModalService.open).not.toHaveBeenCalled();
        expect(mockStateStorageService.storeUrlSpy).not.toHaveBeenCalled();
      });

      it('should log the missing authorities in development mode', async () => {
        const consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => undefined);
        mockAccountService.setIdentityResponse(account);

        await firstValueFrom(service.canActivate(routeRequiring([Authority.ADMIN]), stateAt('/admin')));

        expect(consoleSpy).toHaveBeenCalledWith('User has not any of required authorities: ', [Authority.ADMIN]);
      });

      it('should remember the url, go home and ask an anonymous visitor to log in', async () => {
        mockAccountService.setIdentityResponse(null);

        const result = await firstValueFrom(service.canActivate(routeRequiring(required), stateAt('/editor/vocabulary/AnalysisUnit')));

        expect(result).toBe(false);
        expect(mockStateStorageService.storeUrlSpy).toHaveBeenCalledWith('/editor/vocabulary/AnalysisUnit');
        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['']);
        expect(loginModalService.open).toHaveBeenCalled();
        // authorities are not checked without an account
        expect(hasAnyAuthoritySpy).not.toHaveBeenCalled();
      });
    });

    it('should check the login directly with the given authorities and url', async () => {
      mockAccountService.setIdentityResponse(null);

      const result = await firstValueFrom(service.checkLogin([Authority.USER], '/editor'));

      expect(result).toBe(false);
      expect(mockStateStorageService.storeUrlSpy).toHaveBeenCalledWith('/editor');
    });
  });
});
