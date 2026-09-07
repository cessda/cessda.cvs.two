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
import { Component } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Subject } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { MockAccountService } from '../../../helpers/mock-account.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { AgencyAuthority, HasAnyAgencyAuthorityDirective } from 'app/shared/auth/has-any-agency-authority.directive';

@Component({
  template: '<div *jhiHasAnyAgencyAuthority="authority" class="guarded">editable</div>',
  standalone: false,
})
class HostComponent {
  authority: AgencyAuthority = { actionType: 'EDIT_CV', agencyId: 1, agencyRoles: ['ADMIN_SL'], language: 'en' };
}

describe('Directive Tests', () => {
  describe('HasAnyAgencyAuthority Directive', () => {
    let fixture: ComponentFixture<HostComponent>;
    let mockAccountService: MockAccountService;

    // SpyObject only creates the spies its subclass lists, so these have to be added here
    let hasAnyAgencyAuthoritySpy: jasmine.Spy;
    let authenticationState: Subject<Account | null>;

    const guarded = (): unknown => fixture.debugElement.query(By.css('.guarded'));

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [HasAnyAgencyAuthorityDirective, HostComponent],
      }).compileComponents();
    }));

    beforeEach(() => {
      mockAccountService = TestBed.inject(AccountService) as unknown as MockAccountService;

      hasAnyAgencyAuthoritySpy = jasmine.createSpy('hasAnyAgencyAuthority').and.returnValue(false);
      authenticationState = new Subject<Account | null>();
      const service = mockAccountService as unknown as Record<string, unknown>;
      service.hasAnyAgencyAuthority = hasAnyAgencyAuthoritySpy;
      service.getAuthenticationState = (): Subject<Account | null> => authenticationState;

      fixture = TestBed.createComponent(HostComponent);
    });

    it('should render the element when the user is authorised', () => {
      hasAnyAgencyAuthoritySpy.and.returnValue(true);

      fixture.detectChanges();

      expect(guarded()).toBeTruthy();
    });

    it('should leave the element out when the user is not authorised', () => {
      hasAnyAgencyAuthoritySpy.and.returnValue(false);

      fixture.detectChanges();

      expect(guarded()).toBeNull();
    });

    it('should ask about the action, agency, roles and language it was given', () => {
      fixture.detectChanges();

      expect(hasAnyAgencyAuthoritySpy).toHaveBeenCalledWith('EDIT_CV', 1, ['ADMIN_SL'], 'en');
    });

    it('should pass an absent language through as undefined', () => {
      fixture.componentInstance.authority = { actionType: 'DELETE_CV', agencyId: 0, agencyRoles: ['ADMIN'] };

      fixture.detectChanges();

      expect(hasAnyAgencyAuthoritySpy).toHaveBeenCalledWith('DELETE_CV', 0, ['ADMIN'], undefined);
    });

    describe('when the authentication state changes', () => {
      it('should reveal the element once the user gains the authority', () => {
        hasAnyAgencyAuthoritySpy.and.returnValue(false);
        fixture.detectChanges();
        expect(guarded()).toBeNull();

        hasAnyAgencyAuthoritySpy.and.returnValue(true);
        authenticationState.next({ login: 'admin' } as Account);
        fixture.detectChanges();

        expect(guarded()).toBeTruthy();
      });

      it('should remove the element once the user loses the authority', () => {
        hasAnyAgencyAuthoritySpy.and.returnValue(true);
        fixture.detectChanges();
        expect(guarded()).toBeTruthy();

        hasAnyAgencyAuthoritySpy.and.returnValue(false);
        authenticationState.next(null);
        fixture.detectChanges();

        expect(guarded()).toBeNull();
      });

      it('should not render the element twice when the state changes repeatedly', () => {
        hasAnyAgencyAuthoritySpy.and.returnValue(true);
        fixture.detectChanges();

        authenticationState.next({ login: 'admin' } as Account);
        authenticationState.next({ login: 'admin' } as Account);
        fixture.detectChanges();

        expect(fixture.debugElement.queryAll(By.css('.guarded'))).toHaveLength(1);
      });
    });

    it('should stop listening once destroyed', () => {
      fixture.detectChanges();
      expect(authenticationState.observed).toBe(true);

      fixture.destroy();

      expect(authenticationState.observed).toBe(false);
    });
  });
});
