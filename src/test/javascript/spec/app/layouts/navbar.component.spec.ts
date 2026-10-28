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
import { ComponentFixture, TestBed, fakeAsync, tick, waitForAsync } from '@angular/core/testing';
import { ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { BehaviorSubject, Subject, Subscription, of } from 'rxjs';
import { Location } from '@angular/common';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { LanguageService } from 'app/core/language/language.service';
import { SessionStorageService, provideNgxWebstorage, withNgxWebstorageConfig, withSessionStorage } from 'ngx-webstorage';

import { CvsTestModule } from '../../test.module';
import { MockRouter } from '../../helpers/mock-route.service';
import { MockAccountService } from '../../helpers/mock-account.service';
import { NavbarComponent } from 'app/layouts/navbar/navbar.component';
import { AccountService } from 'app/core/auth/account.service';
import { LoginService } from 'app/core/login/login.service';
import { LoginModalService } from 'app/core/login/login-modal.service';
import { VocabularyLanguageFromKeyPipe } from 'app/shared/language/vocabulary-language-from-key.pipe';
import { HomeService } from 'app/home/home.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { ProfileInfo } from 'app/layouts/profiles/profile-info.model';
import { EventManager } from 'app/core/util/event-manager.service';

describe('Component Tests', () => {
  describe('Navbar Component', () => {
    let comp: NavbarComponent;
    let fixture: ComponentFixture<NavbarComponent>;
    let mockRouter: MockRouter;
    let mockAccountService: MockAccountService;
    let sessionStorage: SessionStorageService;
    let queryParams: BehaviorSubject<Params>;

    const loginModal = { open: jasmine.createSpy('open') };
    const loginService = { logout: jasmine.createSpy('logout') };

    // MockRouter only spies navigate and navigateByUrl, so isActive has to be added here
    let isActiveSpy: jasmine.Spy;
    let isAuthenticatedSpy: jasmine.Spy;

    beforeEach(waitForAsync(() => {
      queryParams = new BehaviorSubject<Params>({});
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [NavbarComponent],
        providers: [
          VocabularyLanguageFromKeyPipe,
          { provide: LoginModalService, useValue: loginModal },
          { provide: LoginService, useValue: loginService },
          { provide: Location, useValue: { path: (): string => '' } },
          { provide: ActivatedRoute, useValue: { queryParams } },
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
          jasmine.objectContaining({
            queryParams: jasmine.objectContaining({ q: 'unit', sort: 'relevance' }),
            queryParamsHandling: 'merge',
          }),
        );
      });

      it('should start a new search on a search page at the first page', () => {
        // the results of the new query may not reach the page the previous one was left on (#816)
        isActiveSpy.and.returnValue(true);

        comp.search('unit');

        expect(mockRouter.navigateSpy.calls.mostRecent().args[1].queryParams.page).toBeNull();
      });

      it('should sort by code when the query is cleared on a search page', () => {
        isActiveSpy.and.returnValue(true);

        comp.search(undefined);

        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(
          [],
          jasmine.objectContaining({ queryParams: jasmine.objectContaining({ q: undefined, sort: 'code,asc', page: null }) }),
        );
      });

      describe('picking a language on a search page', () => {
        const filtersAfterSearch = (): string => mockRouter.navigateSpy.calls.mostRecent().args[1].queryParams.f;

        beforeEach(() => isActiveSpy.and.returnValue(true));

        it('should filter by the language picked (#786)', () => {
          queryParams.next({ f: 'language:en' });

          comp.currentLang = 'de';
          comp.search(comp.currentSearch);

          expect(filtersAfterSearch()).toBe('language:de');
        });

        it('should keep the other filters while changing the language (#972)', () => {
          queryParams.next({ q: 'unit', f: 'agency:CESSDA;language:en;status:PUBLISHED' });

          comp.currentLang = 'fi';
          comp.search(comp.currentSearch);

          expect(filtersAfterSearch()).toBe('agency:CESSDA;language:fi;status:PUBLISHED');
        });

        it('should add the language to filters that had none', () => {
          queryParams.next({ f: 'agency:CESSDA;status:PUBLISHED' });

          comp.currentLang = 'de';
          comp.search(undefined);

          expect(filtersAfterSearch()).toBe('agency:CESSDA;language:de;status:PUBLISHED');
        });

        it('should filter by the language shown when nothing was filtered yet', () => {
          comp.search('unit');

          expect(filtersAfterSearch()).toBe('language:en');
        });
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
        const languageService = TestBed.inject(LanguageService) as unknown as { changeLanguageSpy?: jasmine.Spy };
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

    describe('the languages offered for searching', () => {
      let homeService: HomeService;

      beforeEach(() => {
        homeService = TestBed.inject(HomeService);
      });

      it('should offer the published languages sorted by name, English first and all languages last', () => {
        const spy = jest
          .spyOn(homeService, 'getAvailableLanguagesIsos')
          .mockReturnValue(of(new HttpResponse({ body: ['fi', 'de', 'en'] })));

        comp.loadLanguages();

        expect(spy).toHaveBeenCalledWith({ s: 'PUBLISHED' });
        expect(comp.searchLangs).toEqual(['en', 'fi', 'de', '_all']);
      });

      it('should offer the languages of every workflow status in the editor', () => {
        const spy = jest.spyOn(homeService, 'getAvailableLanguagesIsos').mockReturnValue(of(new HttpResponse<string[]>({ body: null })));
        comp.isEditorSearch = true;

        comp.loadLanguages();

        expect(spy).toHaveBeenCalledWith({ s: 'DRAFT;REVIEW;READY_TO_TRANSLATE;READY_TO_PUBLISH;PUBLISHED;' });
        expect(comp.searchLangs).toEqual(['en', '_all']);
      });

      it('should reload the languages when navigating into and out of the editor', () => {
        const spy = jest.spyOn(homeService, 'getAvailableLanguagesIsos').mockReturnValue(of(new HttpResponse({ body: ['en'] })));
        const location = TestBed.inject(Location) as unknown as { path: () => string };
        const events = mockRouter.events as unknown as Subject<unknown>;
        let path = '/editor/vocabulary/AnalysisUnit';
        location.path = (): string => path;

        events.next({});
        expect(comp.isEditorSearch).toBe(true);
        expect(spy).toHaveBeenCalledTimes(1);

        events.next({});
        expect(spy).toHaveBeenCalledTimes(1);

        path = '/vocabulary/AnalysisUnit';
        events.next({});
        expect(comp.isEditorSearch).toBe(false);
        expect(spy).toHaveBeenCalledTimes(2);
      });
    });

    describe('reading the search from the address', () => {
      it('should pick the language filtered by', () => {
        queryParams.next({ q: 'unit', f: 'agency:CESSDA;language:de;status:PUBLISHED' });

        expect(comp.currentSearch).toBe('unit');
        expect(comp.currentLang).toBe('de');
      });

      it('should fall back on English for a language it does not offer', () => {
        comp.currentLang = 'de';

        queryParams.next({ f: 'language:xx' });

        expect(comp.currentLang).toBe('en');
      });

      it('should keep the language when the filters name none', () => {
        comp.currentLang = 'fi';

        queryParams.next({ f: 'agency:CESSDA;malformed' });

        expect(comp.currentLang).toBe('fi');
      });

      it('should take the last search from the address when none was stored', () => {
        queryParams.next({ q: 'unit' });

        expect(comp.lastSearch).toBe('unit');
      });

      it('should prefer the last search stored in the session', () => {
        sessionStorage.store('lastSearch', 'stored');
        queryParams.next({ q: 'unit' });

        const other = TestBed.createComponent(NavbarComponent).componentInstance;

        expect(other.lastSearch).toBe('stored');
        expect(other.currentSearch).toBe('unit');
      });
    });

    describe('the search box', () => {
      let input: HTMLInputElement;
      let select: HTMLSelectElement;
      let searchSpy: jest.SpyInstance;
      let onSearching: ((event: { content: boolean }) => void) | undefined;
      let eventManager: Record<string, unknown>;

      const type = (value: string, key: string): void => {
        input.value = value;
        input.dispatchEvent(new KeyboardEvent('keyup', { key }));
      };

      beforeEach(() => {
        input = document.createElement('input');
        select = document.createElement('select');
        comp.searchInput = new ElementRef(input);
        comp.searchLang = new ElementRef(select);
        jest
          .spyOn(TestBed.inject(ProfileService), 'getProfileInfo')
          .mockReturnValue(of({ activeProfiles: ['prod'], inProduction: true, swaggerEnabled: true }));
        eventManager = TestBed.inject(EventManager) as unknown as Record<string, unknown>;
        eventManager.subscribe = jest.fn((_name: string, callback: (event: { content: boolean }) => void) => {
          onSearching = callback;
          return new Subscription();
        });
        eventManager.destroy = jest.fn();
        searchSpy = jest.spyOn(comp, 'search').mockImplementation(() => undefined);
      });

      afterEach(() => {
        jest.restoreAllMocks();
      });

      it('should read the profile', () => {
        comp.ngOnInit();

        expect(comp.inProduction).toBe(true);
        expect(comp.swaggerEnabled).toBe(true);
      });

      it('should search once typing pauses', fakeAsync(() => {
        comp.ngOnInit();

        type('un', 'n');
        tick(200);
        type('unit', 't');
        tick(499);
        expect(searchSpy).not.toHaveBeenCalled();

        tick(1);
        expect(searchSpy).toHaveBeenCalledTimes(1);
        expect(searchSpy).toHaveBeenCalledWith('unit');
        expect(comp.isSearching).toBe(true);
      }));

      it('should not repeat the last search unless Enter is pressed', fakeAsync(() => {
        comp.lastSearch = 'unit';
        comp.ngOnInit();

        type('unit', 'ArrowLeft');
        tick(500);
        expect(searchSpy).not.toHaveBeenCalled();

        type('unit', 'Enter');
        tick(500);
        expect(searchSpy).toHaveBeenCalledWith('unit');
      }));

      it('should search right away when text is pasted', () => {
        comp.ngOnInit();

        input.value = 'pasted';
        input.dispatchEvent(new Event('paste'));

        expect(searchSpy).toHaveBeenCalledWith('pasted');
        expect(comp.isSearching).toBe(true);
      });

      it('should search again when another language is picked', () => {
        comp.ngOnInit();
        comp.currentSearch = 'unit';

        select.dispatchEvent(new Event('change'));

        expect(searchSpy).toHaveBeenCalledWith('unit');
      });

      it('should follow the searching state broadcast by the search results', () => {
        comp.ngOnInit();

        expect(eventManager.subscribe).toHaveBeenCalledWith('onSearching', expect.any(Function));
        onSearching?.({ content: true });
        expect(comp.isSearching).toBe(true);
        onSearching?.({ content: false });
        expect(comp.isSearching).toBe(false);
      });

      it('should stop listening on destroy', () => {
        comp.ngOnInit();

        comp.ngOnDestroy();

        expect(eventManager.destroy).toHaveBeenCalledWith(comp.eventSubscriber);
      });

      it('should have nothing to stop when it was never initialised', () => {
        comp.ngOnDestroy();

        expect(eventManager.destroy).not.toHaveBeenCalled();
      });

      it('should focus the search box once the view is ready', fakeAsync(() => {
        const focus = jest.spyOn(input, 'focus');

        comp.ngAfterViewInit();
        tick();

        expect(focus).toHaveBeenCalled();
      }));

      it('should not fail when there is no search box to focus', fakeAsync(() => {
        comp.searchInput = undefined as unknown as ElementRef<HTMLInputElement>;

        comp.ngAfterViewInit();

        expect(() => tick()).not.toThrow();
      }));

      it('should treat a profile that does not say otherwise as not in production', () => {
        jest
          .spyOn(TestBed.inject(ProfileService), 'getProfileInfo')
          .mockReturnValue(of({ activeProfiles: [], swaggerEnabled: false } as unknown as ProfileInfo));

        comp.ngOnInit();

        expect(comp.inProduction).toBe(false);
        expect(comp.swaggerEnabled).toBe(false);
      });
    });
  });
});
