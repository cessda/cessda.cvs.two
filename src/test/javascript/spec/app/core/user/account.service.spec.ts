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
import { Router } from '@angular/router';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { JhiDateUtils, JhiLanguageService } from 'ng-jhipster';
import { SessionStorageService, provideNgxWebstorage } from 'ngx-webstorage';

import { SERVER_API_URL } from 'app/app.constants';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { UserAgency } from 'app/shared/model/user-agency.model';
import { Authority } from 'app/shared/constants/authority.constants';
import { StateStorageService } from 'app/core/auth/state-storage.service';
import { MockLanguageService } from '../../../helpers/mock-language.service';
import { MockRouter } from '../../../helpers/mock-route.service';
import { MockStateStorageService } from '../../../helpers/mock-state-storage.service';

function accountWithAuthorities(authorities: Authority[]): Account {
  return {
    id: 1,
    activated: true,
    authorities,
    email: '',
    firstName: '',
    langKey: '',
    lastName: '',
    login: '',
    imageUrl: '',
    userAgencies: [],
  };
}

describe('Service Tests', () => {
  describe('Account Service', () => {
    let service: AccountService;
    let httpMock: HttpTestingController;
    let storageService: MockStateStorageService;
    let router: MockRouter;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [
          JhiDateUtils,
          {
            provide: JhiLanguageService,
            useClass: MockLanguageService,
          },
          {
            provide: StateStorageService,
            useClass: MockStateStorageService,
          },
          {
            provide: Router,
            useClass: MockRouter,
          },
          SessionStorageService,
          provideNgxWebstorage(),
        ],
      });

      service = TestBed.inject(AccountService);
      httpMock = TestBed.inject(HttpTestingController);
      storageService = TestBed.inject(StateStorageService) as unknown as MockStateStorageService;
      router = TestBed.inject(Router) as unknown as MockRouter;
    });

    afterEach(() => {
      httpMock.verify();
    });

    describe('authenticate', () => {
      it('authenticationState should emit null if input is null', () => {
        // GIVEN
        let userIdentity: Account | null = accountWithAuthorities([]);
        service.getAuthenticationState().subscribe(account => (userIdentity = account));

        // WHEN
        service.authenticate(null);

        // THEN
        expect(userIdentity).toBeNull();
        expect(service.isAuthenticated()).toBe(false);
      });

      it('authenticationState should emit the same account as was in input parameter', () => {
        // GIVEN
        const expectedResult = accountWithAuthorities([]);
        let userIdentity: Account | null = null;
        service.getAuthenticationState().subscribe(account => (userIdentity = account));

        // WHEN
        service.authenticate(expectedResult);

        // THEN
        expect(userIdentity).toEqual(expectedResult);
        expect(service.isAuthenticated()).toBe(true);
      });
    });

    describe('identity', () => {
      it('should call /account if user is undefined', () => {
        service.identity().subscribe();
        const req = httpMock.expectOne({ method: 'GET' });
        const resourceUrl = SERVER_API_URL + 'api/account';

        expect(req.request.url).toEqual(`${resourceUrl}`);
      });

      it('should call /account only once if not logged out after first authentication and should call /account again if user has logged out', () => {
        // Given the user is authenticated
        service.identity().subscribe();
        httpMock.expectOne({ method: 'GET' }).flush({});

        // When I call
        service.identity().subscribe();

        // Then there is no second request
        httpMock.expectNone({ method: 'GET' });

        // When I log out
        service.authenticate(null);
        // and then call
        service.identity().subscribe();

        // Then there is a new request
        httpMock.expectOne({ method: 'GET' });
      });

      describe('navigateToStoredUrl', () => {
        it('should navigate to the previous stored url post successful authentication', () => {
          // GIVEN
          storageService.setResponse('admin/users?page=0');

          // WHEN
          service.identity().subscribe();
          httpMock.expectOne({ method: 'GET' }).flush({});

          // THEN
          expect(storageService.getUrlSpy).toHaveBeenCalledTimes(1);
          expect(storageService.clearUrlSpy).toHaveBeenCalledTimes(1);
          expect(router.navigateByUrlSpy).toHaveBeenCalledWith('admin/users?page=0');
        });

        it('should not navigate to the previous stored url when authentication fails', () => {
          // WHEN
          service.identity().subscribe();
          httpMock.expectOne({ method: 'GET' }).error(new ErrorEvent(''));

          // THEN
          expect(storageService.getUrlSpy).not.toHaveBeenCalled();
          expect(storageService.clearUrlSpy).not.toHaveBeenCalled();
          expect(router.navigateByUrlSpy).not.toHaveBeenCalled();
        });

        it('should not navigate to the previous stored url when no such url exists post successful authentication', () => {
          // GIVEN
          storageService.setResponse(null);

          // WHEN
          service.identity().subscribe();
          httpMock.expectOne({ method: 'GET' }).flush({});

          // THEN
          expect(storageService.getUrlSpy).toHaveBeenCalledTimes(1);
          expect(storageService.clearUrlSpy).not.toHaveBeenCalled();
          expect(router.navigateByUrlSpy).not.toHaveBeenCalled();
        });
      });
    });

    describe('hasAnyAuthority', () => {
      describe('hasAnyAuthority string parameter', () => {
        it('should return false if user is not logged', () => {
          const hasAuthority = service.hasAnyAuthority(Authority.USER);
          expect(hasAuthority).toBe(false);
        });

        it('should return false if user is logged and has not authority', () => {
          service.authenticate(accountWithAuthorities([Authority.USER]));

          const hasAuthority = service.hasAnyAuthority(Authority.ADMIN);

          expect(hasAuthority).toBe(false);
        });

        it('should return true if user is logged and has authority', () => {
          service.authenticate(accountWithAuthorities([Authority.USER]));

          const hasAuthority = service.hasAnyAuthority(Authority.USER);

          expect(hasAuthority).toBe(true);
        });
      });

      describe('hasAnyAuthority array parameter', () => {
        it('should return false if user is not logged', () => {
          const hasAuthority = service.hasAnyAuthority([Authority.USER]);
          expect(hasAuthority).toBeFalsy();
        });

        it('should return false if user is logged and has not authority', () => {
          service.authenticate(accountWithAuthorities([Authority.USER]));

          const hasAuthority = service.hasAnyAuthority([Authority.ADMIN]);

          expect(hasAuthority).toBe(false);
        });

        it('should return true if user is logged and has authority', () => {
          service.authenticate(accountWithAuthorities([Authority.USER]));

          const hasAuthority = service.hasAnyAuthority([Authority.USER, Authority.ADMIN]);

          expect(hasAuthority).toBe(true);
        });
      });
    });
  });
});

describe('Service Tests', () => {
  describe('Account Service agency authorisation', () => {
    let service: AccountService;
    let httpMock: HttpTestingController;

    const EDIT = 'EDIT_CV';

    function accountWith(authorities: Authority[], userAgencies: UserAgency[]): Account {
      return {
        id: 1,
        activated: true,
        authorities,
        email: '',
        firstName: '',
        langKey: '',
        lastName: '',
        login: '',
        imageUrl: '',
        userAgencies,
      };
    }

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [
          JhiDateUtils,
          { provide: JhiLanguageService, useClass: MockLanguageService },
          { provide: StateStorageService, useClass: MockStateStorageService },
          { provide: Router, useClass: MockRouter },
          SessionStorageService,
          provideNgxWebstorage(),
        ],
      });

      service = TestBed.inject(AccountService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
      httpMock.verify();
    });

    describe('isAdmin', () => {
      it('should treat a full administrator as an admin', () => {
        service.authenticate(accountWith([Authority.ADMIN], []));

        expect(service.isAdmin()).toBe(true);
      });

      it('should treat a content administrator as an admin', () => {
        service.authenticate(accountWith([Authority.ADMIN_CONTENT], []));

        expect(service.isAdmin()).toBe(true);
      });

      it('should not treat a technical administrator as an admin', () => {
        // technical administration is about the running system, not about vocabularies
        service.authenticate(accountWith([Authority.ADMIN_TECHNICAL], []));

        expect(service.isAdmin()).toBe(false);
      });

      it('should not treat an ordinary user as an admin', () => {
        service.authenticate(accountWith([Authority.USER], []));

        expect(service.isAdmin()).toBe(false);
      });
    });

    describe('hasAnyAgencyAuthority', () => {
      it('should refuse anyone who is not logged in', () => {
        expect(service.hasAnyAgencyAuthority(EDIT, 1, ['ADMIN_SL'])).toBe(false);
      });

      it('should allow an admin regardless of agency or role', () => {
        service.authenticate(accountWith([Authority.ADMIN], []));

        expect(service.hasAnyAgencyAuthority(EDIT, 99, ['ADMIN_SL'], 'fr')).toBe(true);
      });

      it('should refuse an action it does not know', () => {
        service.authenticate(accountWith([Authority.USER], [{ agencyId: 1, agencyRole: 'ADMIN_SL', language: 'en' }]));

        expect(service.hasAnyAgencyAuthority('SOMETHING_ELSE', 1, ['ADMIN_SL'], 'en')).toBe(false);
      });

      describe('for a specific agency', () => {
        beforeEach(() => {
          service.authenticate(
            accountWith(
              [Authority.USER],
              [
                { agencyId: 1, agencyRole: 'ADMIN_SL', language: 'en' },
                { agencyId: 2, agencyRole: 'ADMIN_TL', language: 'de' },
              ],
            ),
          );
        });

        it('should allow the matching agency, role and language', () => {
          expect(service.hasAnyAgencyAuthority(EDIT, 1, ['ADMIN_SL'], 'en')).toBe(true);
        });

        it('should refuse a different agency', () => {
          expect(service.hasAnyAgencyAuthority(EDIT, 3, ['ADMIN_SL'], 'en')).toBe(false);
        });

        it('should refuse a role the user does not hold there', () => {
          expect(service.hasAnyAgencyAuthority(EDIT, 1, ['ADMIN_TL'], 'en')).toBe(false);
        });

        it('should refuse a language the user is not granted', () => {
          expect(service.hasAnyAgencyAuthority(EDIT, 1, ['ADMIN_SL'], 'de')).toBe(false);
        });

        it('should ignore the language when asked for any', () => {
          expect(service.hasAnyAgencyAuthority(EDIT, 1, ['ADMIN_SL'], 'any')).toBe(true);
        });

        it('should ignore the language when none is given', () => {
          expect(service.hasAnyAgencyAuthority(EDIT, 1, ['ADMIN_SL'])).toBe(true);
        });

        it('should refuse outright when the language is none', () => {
          expect(service.hasAnyAgencyAuthority(EDIT, 1, ['ADMIN_SL'], 'none')).toBe(false);
        });

        it('should accept any of several acceptable roles', () => {
          expect(service.hasAnyAgencyAuthority(EDIT, 2, ['ADMIN_SL', 'ADMIN_TL'], 'de')).toBe(true);
        });
      });

      describe('for any agency', () => {
        beforeEach(() => {
          service.authenticate(accountWith([Authority.USER], [{ agencyId: 7, agencyRole: 'ADMIN_TL', language: 'de' }]));
        });

        it('should look only at the role when the agency is zero', () => {
          expect(service.hasAnyAgencyAuthority(EDIT, 0, ['ADMIN_TL'])).toBe(true);
        });

        it('should still refuse a role the user holds nowhere', () => {
          expect(service.hasAnyAgencyAuthority(EDIT, 0, ['ADMIN_SL'])).toBe(false);
        });

        it('should not care about the language when the agency is zero', () => {
          expect(service.hasAnyAgencyAuthority(EDIT, 0, ['ADMIN_TL'], 'fr')).toBe(true);
        });
      });

      it('should cover the code actions as well as the vocabulary ones', () => {
        service.authenticate(accountWith([Authority.USER], [{ agencyId: 1, agencyRole: 'ADMIN_SL', language: 'en' }]));

        expect(service.hasAnyAgencyAuthority('CREATE_CODE', 1, ['ADMIN_SL'], 'en')).toBe(true);
        expect(service.hasAnyAgencyAuthority('DEPRECATE_CODE', 1, ['ADMIN_SL'], 'en')).toBe(true);
        expect(service.hasAnyAgencyAuthority('FORWARD_CV_SL_STATUS_PUBLISH', 1, ['ADMIN_SL'], 'en')).toBe(true);
      });
    });

    describe('reading the signed in user', () => {
      it('should report nobody as not authenticated', () => {
        expect(service.isAuthenticated()).toBe(false);
        expect(service.geUserName()).toBe('');
        expect(service.getImageUrl()).toBe('');
        expect(service.getUserAgencies()).toEqual([]);
      });

      it('should prefer the first name', () => {
        const account = accountWith([Authority.USER], []);
        account.firstName = 'Ada';
        account.lastName = 'Lovelace';
        service.authenticate(account);

        expect(service.geUserName()).toBe('Ada');
        expect(service.isAuthenticated()).toBe(true);
      });

      it('should fall back to the surname', () => {
        const account = accountWith([Authority.USER], []);
        account.firstName = '';
        account.lastName = 'Lovelace';
        service.authenticate(account);

        expect(service.geUserName()).toBe('Lovelace');
      });

      it('should list the names of the agencies the user belongs to', () => {
        service.authenticate(
          accountWith(
            [Authority.USER],
            [
              { agencyId: 1, agencyName: 'CESSDA', agencyRole: 'ADMIN_SL' },
              { agencyId: 2, agencyRole: 'ADMIN_TL' },
              { agencyId: 3, agencyName: 'GESIS', agencyRole: 'ADMIN_TL' },
            ],
          ),
        );

        // an agency without a name is skipped
        expect(service.getUserAgencies()).toEqual(['CESSDA', 'GESIS']);
      });

      it('should expose the image url', () => {
        const account = accountWith([Authority.USER], []);
        account.imageUrl = 'https://example.org/avatar.png';
        service.authenticate(account);

        expect(service.getImageUrl()).toBe('https://example.org/avatar.png');
      });
    });
  });
});
