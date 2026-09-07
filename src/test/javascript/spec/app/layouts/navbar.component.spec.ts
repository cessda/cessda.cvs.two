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
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { Subject } from 'rxjs';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { SessionStorageService, provideNgxWebstorage, withNgxWebstorageConfig, withSessionStorage } from 'ngx-webstorage';

import { CvsTestModule } from '../../test.module';
import { MockRouter } from '../../helpers/mock-route.service';
import { MockAccountService } from '../../helpers/mock-account.service';
import { NavbarComponent } from 'app/layouts/navbar/navbar.component';
import { AccountService } from 'app/core/auth/account.service';
import { LoginService } from 'app/core/login/login.service';
import { LoginModalService } from 'app/core/login/login-modal.service';
import { VocabularyLanguageFromKeyPipe } from 'app/shared/language/vocabulary-language-from-key.pipe';

describe('Component Tests', () => {
  describe('Navbar Component', () => {
    let comp: NavbarComponent;
    let fixture: ComponentFixture<NavbarComponent>;
    let mockRouter: MockRouter;
    let mockAccountService: MockAccountService;
    let sessionStorage: SessionStorageService;

    const loginModal = { open: jasmine.createSpy('open') };
    const loginService = { logout: jasmine.createSpy('logout') };

    // MockRouter only spies navigate and navigateByUrl, so isActive has to be added here
    let isActiveSpy: jasmine.Spy;
    let isAuthenticatedSpy: jasmine.Spy;

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [NavbarComponent],
        providers: [
          VocabularyLanguageFromKeyPipe,
          { provide: LoginModalService, useValue: loginModal },
          { provide: LoginService, useValue: loginService },
          { provide: Location, useValue: { path: (): string => '' } },
          provideNgxWebstorage(withNgxWebstorageConfig({ prefix: 'jhi', separator: '-' }), withSessionStorage()),
        ],
      })
        .overrideTemplate(NavbarComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      mockRouter = TestBed.inject(Router) as unknown as MockRouter;
      mockAccountService = TestBed.inject(AccountService) as unknown as MockAccountService;
      sessionStorage = TestBed.inject(SessionStorageService);

      // the constructor subscribes to router events, which the mock router leaves null
      isActiveSpy = jasmine.createSpy('isActive').and.returnValue(false);
      const router = mockRouter as unknown as Record<string, unknown>;
      router.isActive = isActiveSpy;
      router.events = new Subject();

      isAuthenticatedSpy = jasmine.createSpy('isAuthenticated').and.returnValue(false);
      (mockAccountService as unknown as Record<string, unknown>).isAuthenticated = isAuthenticatedSpy;

      // clearing a search stores undefined, which cannot be read back - wipe it before the
      // constructor reads it
      sessionStorage.clear('lastSearch');

      fixture = TestBed.createComponent(NavbarComponent);
      comp = fixture.componentInstance;

      loginModal.open.calls.reset();
      loginService.logout.calls.reset();
    });

    describe('searching', () => {
      it('should keep the current query parameters while already on a search page', () => {
        isActiveSpy.and.returnValue(true);

        comp.search('unit');

        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(
          [],
          jasmine.objectContaining({ queryParams: { q: 'unit', sort: 'relevance' }, queryParamsHandling: 'merge' }),
        );
      });

      it('should sort by code when the query is cleared on a search page', () => {
        isActiveSpy.and.returnValue(true);

        comp.search(undefined);

        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(
          [],
          jasmine.objectContaining({ queryParams: { q: undefined, sort: 'code,asc' } }),
        );
      });

      it('should go to the public search from elsewhere, filtered by the current language', () => {
        comp.currentLang = 'de';

        comp.search('unit');

        expect(mockRouter.navigateSpy).toHaveBeenCalledWith([''], {
          queryParams: { q: 'unit', f: 'language:de', sort: 'relevance' },
        });
      });

      it('should go to the editor search when searching as an editor', () => {
        comp.isEditorSearch = true;
        comp.currentLang = 'en';

        comp.search('unit');

        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['/editor'], {
          queryParams: { q: 'unit', f: 'language:en', sort: 'relevance' },
        });
      });

      it('should remember the last search', () => {
        comp.search('unit');

        expect(comp.lastSearch).toBe('unit');
        expect(sessionStorage.retrieve('lastSearch')).toBe('unit');
      });

      it('should drop the current search when cleared', () => {
        comp.currentSearch = 'unit';

        comp.clear();

        expect(comp.currentSearch).toBeUndefined();
        expect(comp.lastSearch).toBeUndefined();
      });
    });

    describe('the signed in user', () => {
      it('should show nothing for a visitor who is not signed in', () => {
        isAuthenticatedSpy.and.returnValue(false);

        expect(comp.isAuthenticated()).toBe(false);
        expect(comp.getImageUrl()).toBe('');
        expect(comp.getUserName()).toBe('');
      });

      it('should read the name and picture from the account once signed in', () => {
        isAuthenticatedSpy.and.returnValue(true);
        const service = mockAccountService as unknown as Record<string, unknown>;
        service.getImageUrl = (): string => 'https://example.org/avatar.png';
        service.geUserName = (): string => 'Ada';

        expect(comp.getImageUrl()).toBe('https://example.org/avatar.png');
        expect(comp.getUserName()).toBe('Ada');
      });

      it('should open the login dialog', () => {
        comp.login();

        expect(loginModal.open).toHaveBeenCalled();
      });

      it('should collapse, sign out and return home on logout', () => {
        comp.isNavbarCollapsed = false;

        comp.logout();

        expect(comp.isNavbarCollapsed).toBe(true);
        expect(loginService.logout).toHaveBeenCalled();
        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['']);
      });
    });

    describe('the navbar itself', () => {
      it('should toggle open and shut', () => {
        comp.isNavbarCollapsed = true;

        comp.toggleNavbar();
        expect(comp.isNavbarCollapsed).toBe(false);

        comp.toggleNavbar();
        expect(comp.isNavbarCollapsed).toBe(true);
      });

      it('should collapse back to English', () => {
        comp.isNavbarCollapsed = false;
        comp.currentLang = 'de';

        comp.collapseNavbar();

        expect(comp.isNavbarCollapsed).toBe(true);
        expect(comp.currentLang).toBe('en');
      });

      it('should store the chosen language and tell the language service', () => {
        const languageService = TestBed.inject(JhiLanguageService) as unknown as { changeLanguageSpy?: jasmine.Spy };
        const changeLanguage = jasmine.createSpy('changeLanguage');
        (languageService as unknown as Record<string, unknown>).changeLanguage = changeLanguage;

        comp.changeLanguage('de');

        expect(sessionStorage.retrieve('locale')).toBe('de');
        expect(changeLanguage).toHaveBeenCalledWith('de');
      });
    });

    describe('which menu entry is highlighted', () => {
      const atUrl = (url: string): void => {
        (mockRouter as unknown as Record<string, unknown>).url = url;
      };

      it('should highlight home on the search page and on a vocabulary', () => {
        atUrl('/?q=unit');
        expect(comp.isLinkActive('/')).toBe(true);

        atUrl('/vocabulary/AnalysisUnit');
        expect(comp.isLinkActive('/')).toBe(true);
      });

      it('should highlight the editor inside the editor', () => {
        atUrl('/editor/vocabulary/AnalysisUnit');

        expect(comp.isLinkActive('editor')).toBe(true);
        expect(comp.isLinkActive('/')).toBe(false);
      });

      it('should highlight nothing elsewhere', () => {
        atUrl('/admin/user-management');

        expect(comp.isLinkActive('/')).toBe(false);
        expect(comp.isLinkActive('editor')).toBe(false);
      });

      it('should not highlight an entry it does not know', () => {
        atUrl('/editor');

        expect(comp.isLinkActive('something-else')).toBe(false);
      });
    });

    describe('formatting a language for the filter', () => {
      it('should name the special all-languages entry', () => {
        expect(comp.getLangFormatted('_all')).toBe('All languages');
      });

      it('should name an ordinary language', () => {
        expect(comp.getLangFormatted('de')).toBe('German (de)');
      });
    });
  });
});
