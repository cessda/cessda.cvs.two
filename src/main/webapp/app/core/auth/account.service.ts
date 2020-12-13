import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {JhiLanguageService} from 'ng-jhipster';
import {SessionStorageService} from 'ngx-webstorage';
import {Observable, of, ReplaySubject} from 'rxjs';
import {catchError, shareReplay, tap} from 'rxjs/operators';
import {StateStorageService} from 'app/core/auth/state-storage.service';

import {SERVER_API_URL} from 'app/app.constants';
import {Account} from 'app/core/user/account.model';
import {IUserAgency} from 'app/shared/model/user-agency.model';

@Injectable({ providedIn: 'root' })
export class AccountService {
  private userIdentity: Account | null = null;
  private authenticationState = new ReplaySubject<Account | null>(1);
  private accountCache$?: Observable<Account | null>;

  constructor(
    private languageService: JhiLanguageService,
    private sessionStorage: SessionStorageService,
    private http: HttpClient,
    private stateStorageService: StateStorageService,
    private router: Router
  ) {}

  save(account: Account): Observable<{}> {
    return this.http.post(SERVER_API_URL + 'api/account', account);
  }

  authenticate(identity: Account | null): void {
    this.userIdentity = identity;
    this.authenticationState.next(this.userIdentity);
  }

  hasAnyAuthority(authorities: string[] | string): boolean {
    if (!this.userIdentity || !this.userIdentity.authorities) {
      return false;
    }
    if (!Array.isArray(authorities)) {
      authorities = [authorities];
    }
    return this.userIdentity.authorities.some((authority: string) => authorities.includes(authority));
  }

  /**
   * Check if user has authority to access certain DOM
   * @param actionType
   * @param agencyId, the agency ID, ID = 0 means that any agency
   * @param agencyRoles
   * @param language
   */
  hasAnyAgencyAuthority(actionType: string, agencyId: number, agencyRoles: string[], language?: string): boolean {
    if (!this.userIdentity || !this.userIdentity.authorities) {
      return false;
    }
    // check if admin
    if (this.isAdmin()) {
      return true;
    }

    // check by actionType, agencyId, agencyRoles, languages, aginst user.userAgency
    switch (actionType) {
      case 'CREATE_CV':
      case 'EDIT_CV':
      case 'EDIT_DDI_CV':
      case 'EDIT_NOTE_CV':
      case 'EDIT_VERSION_INFO_CV':
      case 'DELETE_CV':
      case 'ADD_USAGE_CV':
      case 'ADD_TL_CV':
      case 'WITHDRAWN_CV':
      case 'FORWARD_CV_SL_STATUS_REVIEW':
      case 'FORWARD_CV_SL_STATUS_PUBLISH':
      case 'FORWARD_CV_TL_STATUS_REVIEW':
      case 'FORWARD_CV_TL_STATUS_PUBLISH':
      case 'CREATE_CODE':
      case 'EDIT_CODE':
      case 'REORDER_CODE':
      case 'DELETE_CODE':
      case 'ADD_TL_CODE':
      case 'EDIT_TL_CODE':
      case 'DELETE_TL_CODE':
      case 'PERFORM_ACTION_IN_REVIEW':
      case 'CREATE_NEW_CV_SL_VERSION':
      case 'CREATE_NEW_CV_TL_VERSION': {
        return this.hasAgencyRole(agencyId, agencyRoles, language);
      }
    }

    return false;
  }

  isAdmin(): boolean {
    // always allow if user has ROLE_ADMIN or ROLE_ADMIN_CONTENT
    const authorities = ['ROLE_ADMIN', 'ROLE_ADMIN_CONTENT'];
    return this.userIdentity!.authorities.some((authority: string) => authorities.includes(authority));
  }

  private hasAgencyRole(agencyId: number, agencyRoles: string[], language?: string): boolean {
    if (language && language === 'none') {
      return false;
    }
    let hasAuth = false;
    if (agencyId === 0) {
      // only check for agencyRoles
      return this.userIdentity!.userAgencies.some((userAgency: IUserAgency) => agencyRoles.includes(userAgency.agencyRole!));
    } else {
      // check for agency, roles and language
      this.userIdentity!.userAgencies.forEach(userAgency => {
        if (userAgency.agencyId === agencyId && agencyRoles.includes(userAgency.agencyRole!)) {
          if (language && language !== 'any') {
            if (language === userAgency.language) {
              hasAuth = true;
            }
          } else {
            hasAuth = true;
          }
        }
      });
    }

    return hasAuth;
  }

  identity(force?: boolean): Observable<Account | null> {
    if (!this.accountCache$ || force || !this.isAuthenticated()) {
      this.accountCache$ = this.fetch().pipe(
        catchError(() => {
          return of(null);
        }),
        tap((account: Account | null) => {
          this.authenticate(account);

          // After retrieve the account info, the language will be changed to
          // the user's preferred language configured in the account setting
          if (account && account.langKey) {
            const langKey = this.sessionStorage.retrieve('locale') || account.langKey;
            this.languageService.changeLanguage(langKey);
          }

          if (account) {
            this.navigateToStoredUrl();
          }
        }),
        shareReplay()
      );
    }
    return this.accountCache$;
  }

  isAuthenticated(): boolean {
    return this.userIdentity !== null;
  }

  getAuthenticationState(): Observable<Account | null> {
    return this.authenticationState.asObservable();
  }

  getImageUrl(): string {
    return this.userIdentity ? this.userIdentity.imageUrl : '';
  }

  geUserName(): string {
    let userName = '';
    if (this.userIdentity) {
      userName = this.userIdentity.firstName ? this.userIdentity.firstName : this.userIdentity.lastName;
    }
    return userName;
  }

  private fetch(): Observable<Account> {
    return this.http.get<Account>(SERVER_API_URL + 'api/account');
  }

  private navigateToStoredUrl(): void {
    // previousState can be set in the authExpiredInterceptor and in the userRouteAccessService
    // if login is successful, go to stored previousState and clear previousState
    const previousUrl = this.stateStorageService.getUrl();
    if (previousUrl) {
      this.stateStorageService.clearUrl();
      this.router.navigateByUrl(previousUrl);
    }
  }
}
