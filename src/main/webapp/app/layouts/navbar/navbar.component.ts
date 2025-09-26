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
import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, IsActiveMatchOptions, Params, Router } from '@angular/router';
import { JhiEventManager, JhiEventWithContent, JhiLanguageService } from 'ng-jhipster';
import { SessionStorageService } from 'ngx-webstorage';

import { VERSION } from 'app/app.constants';
import { LANGUAGES } from 'app/core/language/language.constants';
import { AccountService } from 'app/core/auth/account.service';
import { LoginModalService } from 'app/core/login/login-modal.service';
import { LoginService } from 'app/core/login/login.service';
import { HomeService } from 'app/home/home.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { fromEvent, Subscription } from 'rxjs';
import { debounceTime, map } from 'rxjs/operators';
import { Location } from '@angular/common';
import { HttpResponse } from '@angular/common/http';
import { VocabularyLanguageFromKeyPipe } from 'app/shared';
import VocabularyUtil from 'app/shared/util/vocabulary-util';
import { Authority } from 'app/shared/constants/authority.constants';

@Component({
  selector: 'jhi-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['navbar.scss'],
})
export class NavbarComponent implements OnInit, AfterViewInit, OnDestroy {
  readonly authorities = [Authority.ADMIN, Authority.ADMIN_TECHNICAL];

  @ViewChild('searchInput', { static: true }) searchInput!: ElementRef<HTMLInputElement>;
  @ViewChild('searchLang', { static: true }) searchLang!: ElementRef<HTMLSelectElement>;
  inProduction = false;
  eventSubscriber: Subscription | undefined;
  isNavbarCollapsed = true;
  languages = LANGUAGES;
  swaggerEnabled = false;
  version: string;
  currentSearch: string | undefined;
  currentLang: string;
  isSearching: boolean;
  lastSearch: string | undefined;

  isEditorSearch = false;
  searchLangs: string[] = ['en', 'da', 'nl', 'fi', 'fr', 'de', 'it', 'ja', 'no', 'pt', 'sr', 'sl', 'sv', '_all'];

  constructor(
    private loginService: LoginService,
    private languageService: JhiLanguageService,
    private sessionStorage: SessionStorageService,
    private accountService: AccountService,
    protected activatedRoute: ActivatedRoute,
    private loginModalService: LoginModalService,
    private profileService: ProfileService,
    protected eventManager: JhiEventManager,
    private router: Router,
    private location: Location,
    private vocabLangPipeKey: VocabularyLanguageFromKeyPipe,
    private homeService: HomeService,
  ) {
    this.version = VERSION ? (VERSION.toLowerCase().startsWith('v') ? VERSION : 'v' + VERSION) : '';
    this.isSearching = false;
    this.currentLang = 'en';
    this.lastSearch = this.sessionStorage.retrieve('lastSearch');

    this.loadLanguages();

    this.activatedRoute.queryParams.subscribe(params => {
      this.currentSearch = params['q'];
      if (this.lastSearch === undefined || this.lastSearch === null) {
        this.lastSearch = params['q'];
      }
      if (params['f']) {
        const activeFilters: string[] = params['f'].split(';', 2);
        activeFilters.forEach(af => {
          const activeFilter: string[] = af.split(':', 2);
          if (activeFilter.length === 2) {
            if (activeFilter[0] === 'language') {
              if (this.searchLangs.some(lang => lang === activeFilter[1])) {
                this.currentLang = activeFilter[1];
              } else {
                this.currentLang = 'en';
              }
            }
          }
        });
      }
    });

    this.router.events.subscribe(() => {
      const isEditor = this.location.path().startsWith('/editor');
      if (isEditor !== this.isEditorSearch) {
        this.isEditorSearch = isEditor;
        this.loadLanguages();
      }
    });
  }

  loadLanguages(): void {
    this.homeService
      .getAvailableLanguagesIsos(
        this.isEditorSearch ? { s: 'DRAFT;REVIEW;READY_TO_TRANSLATE;READY_TO_PUBLISH;PUBLISHED;' } : { s: 'PUBLISHED' },
      )
      .subscribe((res: HttpResponse<string[]>) => {
        this.searchLangs = res.body || [];
        this.searchLangs = VocabularyUtil.sortLangByName(this.searchLangs, 'en');
        this.searchLangs.push('_all');
      });
  }

  ngOnInit(): void {
    this.profileService.getProfileInfo().subscribe(profileInfo => {
      this.inProduction = profileInfo.inProduction || false;
      this.swaggerEnabled = profileInfo.swaggerEnabled;
    });

    fromEvent(this.searchInput.nativeElement, 'keyup')
      .pipe(
        map((event: any) => {
          return [event.target.value, event.key] as [string, string];
        }),
        debounceTime(500),
        // distinctUntilChanged()
      )
      .subscribe(([text, key]) => {
        if (key == 'Enter' || text != this.lastSearch) {
          this.isSearching = true;
          this.search(text);
        }
      });

    fromEvent(this.searchInput.nativeElement, 'paste')
      .pipe(
        map((event: any) => {
          return event.target.value;
        }),
      )
      .subscribe((text: string) => {
        this.isSearching = true;
        this.search(text);
      });

    fromEvent(this.searchLang.nativeElement, 'change')
      .pipe(
        map((event: any) => {
          return event.target.value;
        }),
      )
      .subscribe(() => {
        this.isSearching = true;
        this.search(this.currentSearch);
      });

    this.registerCvOnSearchEvent();
  }

  private registerCvOnSearchEvent(): void {
    this.eventSubscriber = this.eventManager.subscribe('onSearching', (response: JhiEventWithContent<boolean>) => {
      this.isSearching = response.content;
    });
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      if (this.searchInput) {
        this.searchInput.nativeElement.focus();
      }
    }, 0);
  }

  search(query: string | undefined): void {
    const matchOptions: IsActiveMatchOptions = { paths: 'exact', queryParams: 'ignored', fragment: 'ignored', matrixParams: 'ignored' };
    if (this.router.isActive('', matchOptions) || this.router.isActive('/editor', matchOptions)) {
      // Keep current query params
      this.router.navigate([], {
        relativeTo: this.activatedRoute,
        queryParams: { q: query, sort: query ? 'relevance' : 'code,asc' },
        queryParamsHandling: 'merge',
      });
    } else {
      // Set sort to relevance if query is present
      const queryParams: Params = { q: query, f: 'language:' + this.currentLang, sort: query ? 'relevance' : 'code,asc' };

      if (this.isEditorSearch) {
        this.router.navigate(['/editor'], { queryParams: queryParams });
      } else {
        this.router.navigate([''], { queryParams: queryParams });
      }
    }
    this.lastSearch = query;
    this.sessionStorage.store('lastSearch', this.lastSearch);
  }

  clear(): void {
    this.currentSearch = undefined;
    this.search(undefined);
  }

  changeLanguage(languageKey: string): void {
    this.sessionStorage.store('locale', languageKey);
    this.languageService.changeLanguage(languageKey);
  }

  collapseNavbar(): void {
    this.isNavbarCollapsed = true;
    this.currentLang = 'en';
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }

  login(): void {
    this.loginModalService.open();
  }

  logout(): void {
    this.collapseNavbar();
    this.loginService.logout();
    this.router.navigate(['']);
  }

  toggleNavbar(): void {
    this.isNavbarCollapsed = !this.isNavbarCollapsed;
  }

  getImageUrl(): string {
    return this.isAuthenticated() ? this.accountService.getImageUrl() : '';
  }

  getUserName(): string {
    return this.isAuthenticated() ? this.accountService.geUserName() : '';
  }

  isLinkActive(link: string): boolean {
    const url = this.router.url.substring(1, this.router.url.length);
    if (link === '/') {
      return url.startsWith('?') || url.startsWith('vocabulary');
    } else if (link === 'editor') {
      return url.startsWith('editor');
    }
    return false;
  }

  getLangFormatted(lang: string): string {
    if (lang === '_all') return 'All languages';
    return this.vocabLangPipeKey.transform(lang);
  }
}
