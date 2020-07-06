import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiEventManager, JhiEventWithContent, JhiLanguageService } from 'ng-jhipster';
import { SessionStorageService } from 'ngx-webstorage';

import { VERSION } from 'app/app.constants';
import { LANGUAGES } from 'app/core/language/language.constants';
import { AccountService } from 'app/core/auth/account.service';
import { LoginModalService } from 'app/core/login/login-modal.service';
import { LoginService } from 'app/core/login/login.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { fromEvent, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, map } from 'rxjs/operators';
import { Location } from '@angular/common';

@Component({
  selector: 'jhi-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['navbar.scss']
})
export class NavbarComponent implements OnInit, OnDestroy {
  @ViewChild('searchInput', { static: true }) searchInput!: ElementRef;
  inProduction?: boolean;
  eventSubscriber?: Subscription;
  isNavbarCollapsed = true;
  languages = LANGUAGES;
  swaggerEnabled?: boolean;
  version: string;
  currentSearch?: string;
  isSearching: boolean;

  isEditorSearch = false;

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
    private location: Location
  ) {
    this.version = VERSION ? (VERSION.toLowerCase().startsWith('v') ? VERSION : 'v' + VERSION) : '';
    this.isSearching = false;

    this.activatedRoute.queryParams.subscribe(params => {
      this.currentSearch = params['q'];
    });

    this.router.events.subscribe(() => {
      this.isEditorSearch = this.location.path().startsWith('/editor');
    });
  }

  ngOnInit(): void {
    this.profileService.getProfileInfo().subscribe(profileInfo => {
      this.inProduction = profileInfo.inProduction;
      this.swaggerEnabled = profileInfo.swaggerEnabled;
    });

    fromEvent(this.searchInput.nativeElement, 'keyup')
      .pipe(
        map((event: any) => {
          return event.target.value;
        }),
        debounceTime(500),
        distinctUntilChanged()
      )
      .subscribe((text: string) => {
        this.isSearching = true;
        this.search(text);
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

  search(query: string): void {
    if (query !== null && query !== '') {
      if (this.isEditorSearch) {
        this.router.navigate(['/editor'], { queryParams: { q: query, sort: 'relevance' } });
      } else {
        this.router.navigate([''], { queryParams: { q: query, sort: 'relevance' } });
      }
    } else {
      if (this.isEditorSearch) {
        this.router.navigate(['/editor']);
      } else {
        this.router.navigate(['']);
      }
    }
    this.eventManager.broadcast({ name: 'doCvPublicationSearch', content: query });
  }

  clear(): void {
    this.currentSearch = '';
    this.search('');
  }

  changeLanguage(languageKey: string): void {
    this.sessionStorage.store('locale', languageKey);
    this.languageService.changeLanguage(languageKey);
  }

  collapseNavbar(): void {
    this.isNavbarCollapsed = true;
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

  isLinkActive(link: string): boolean {
    const url = this.router.url.substring(1, this.router.url.length);
    if (link === '/') {
      return url.startsWith('?') || url.startsWith('vocabulary');
    } else if (link === 'editor') {
      return url.startsWith('editor');
    }
    return false;
  }
}
