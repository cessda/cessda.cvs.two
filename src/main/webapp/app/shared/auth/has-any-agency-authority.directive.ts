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
import { Directive, Input, OnDestroy, TemplateRef, ViewContainerRef } from '@angular/core';
import { Subscription } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';
import { AgencyRole } from '../model/enumerations/agency-role.model';

export interface AgencyAuthority {
  actionType: string;
  agencyId: number;
  agencyRoles: AgencyRole[];
  language?: string;
}

/**
 * @whatItDoes Conditionally includes an HTML element if current user has any
 * of the agency role ang _language actionType in agency passed as the `expression`.
 *
 * @howToUse
 * ```
 *     <some-element *jhiHasAnyAgencyAuthority="{actionType:'SOME_ACTION', agencyId:1, agencyRoles:['ADMIN','ADMIN_SL'], language:en}">...</some-element>
 ** ```
 * note
 *  - agencyId = 0 (means Any.Agency or ignore specific agency)
 *  - language param is optional, it can also set to the specific language e.g. en, or a special literals "any" or "none"
 *     - any is equal with
 */
@Directive({
    selector: '[jhiHasAnyAgencyAuthority]',
    standalone: false
})
export class HasAnyAgencyAuthorityDirective implements OnDestroy {
  private agencyAuthority: AgencyAuthority | undefined = undefined;
  private authenticationSubscription?: Subscription;

  constructor(
    private accountService: AccountService,
    private templateRef: TemplateRef<unknown>,
    private viewContainerRef: ViewContainerRef,
  ) {}

  @Input()
  set jhiHasAnyAgencyAuthority(value: AgencyAuthority) {
    this.agencyAuthority = value;
    this.updateView();
    // Get notified each time authentication state changes.
    this.authenticationSubscription = this.accountService.getAuthenticationState().subscribe(() => this.updateView());
  }

  ngOnDestroy(): void {
    if (this.authenticationSubscription) {
      this.authenticationSubscription.unsubscribe();
    }
  }

  private updateView(): void {
    if (!this.agencyAuthority) {
      throw new TypeError('agencyAuthority is undefined');
    }

    const hasAnyAuthority = this.accountService.hasAnyAgencyAuthority(
      this.agencyAuthority.actionType,
      this.agencyAuthority.agencyId,
      this.agencyAuthority.agencyRoles,
      this.agencyAuthority.language,
    );
    this.viewContainerRef.clear();
    if (hasAnyAuthority) {
      this.viewContainerRef.createEmbeddedView(this.templateRef);
    }
  }
}
