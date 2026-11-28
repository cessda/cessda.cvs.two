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
import { Component } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Subject } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { MockAccountService } from '../../../helpers/mock-account.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { Authority } from 'app/shared/constants/authority.constants';
import { HasAnyAuthorityDirective } from 'app/shared/auth/has-any-authority.directive';

@Component({
  template: '<div *jhiHasAnyAuthority="authorities" class="guarded">restricted</div>',
  standalone: false,
})
class HostComponent {
  authorities: Authority | Authority[] = Authority.ADMIN;
}

describe('Directive Tests', () => {
  describe('HasAnyAuthority Directive', () => {
    let fixture: ComponentFixture<HostComponent>;
    let comp: HostComponent;

    // SpyObject only creates the spies its subclass lists, so these have to be added here
    let hasAnyAuthoritySpy: jasmine.Spy;
    let authenticationState: Subject<Account | null>;

    const guarded = (): unknown => fixture.debugElement.query(By.css('.guarded'));

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [HasAnyAuthorityDirective, HostComponent],
      }).compileComponents();
    }));

    beforeEach(() => {
      const mockAccountService = TestBed.inject(AccountService) as unknown as MockAccountService;

      hasAnyAuthoritySpy = jasmine.createSpy('hasAnyAuthority').and.returnValue(false);
      authenticationState = new Subject<Account | null>();
      const service = mockAccountService as unknown as Record<string, unknown>;
      service.hasAnyAuthority = hasAnyAuthoritySpy;
      service.getAuthenticationState = (): Subject<Account | null> => authenticationState;

      fixture = TestBed.createComponent(HostComponent);
      comp = fixture.componentInstance;
    });

    it('should render the element when the user holds the authority', () => {
      hasAnyAuthoritySpy.and.returnValue(true);

      fixture.detectChanges();

      expect(guarded()).toBeTruthy();
    });

    it('should leave the element out when the user does not', () => {
      hasAnyAuthoritySpy.and.returnValue(false);

      fixture.detectChanges();

      expect(guarded()).toBeNull();
    });

    it('should wrap a single authority into a list before asking', () => {
      comp.authorities = Authority.ADMIN;

      fixture.detectChanges();

      expect(hasAnyAuthoritySpy).toHaveBeenCalledWith([Authority.ADMIN]);
    });

    it('should pass a list of authorities through as it is', () => {
      comp.authorities = [Authority.ADMIN, Authority.USER];

      fixture.detectChanges();

      expect(hasAnyAuthoritySpy).toHaveBeenCalledWith([Authority.ADMIN, Authority.USER]);
    });

    describe('when the authentication state changes', () => {
      it('should reveal the element once the user gains the authority', () => {
        hasAnyAuthoritySpy.and.returnValue(false);
        fixture.detectChanges();

        hasAnyAuthoritySpy.and.returnValue(true);
        authenticationState.next(null);

        expect(guarded()).toBeTruthy();
      });

      it('should remove the element once the user loses it', () => {
        hasAnyAuthoritySpy.and.returnValue(true);
        fixture.detectChanges();

        hasAnyAuthoritySpy.and.returnValue(false);
        authenticationState.next(null);

        expect(guarded()).toBeNull();
      });

      it('should not render the element twice when the state changes repeatedly', () => {
        hasAnyAuthoritySpy.and.returnValue(true);
        fixture.detectChanges();

        authenticationState.next(null);
        authenticationState.next(null);

        expect(fixture.debugElement.queryAll(By.css('.guarded'))).toHaveLength(1);
      });

      it('should stop listening once destroyed', () => {
        hasAnyAuthoritySpy.and.returnValue(true);
        fixture.detectChanges();

        fixture.destroy();
        hasAnyAuthoritySpy.calls.reset();
        authenticationState.next(null);

        expect(hasAnyAuthoritySpy).not.toHaveBeenCalled();
      });
    });
  });
});
