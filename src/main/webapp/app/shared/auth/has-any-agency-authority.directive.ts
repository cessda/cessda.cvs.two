import { Directive, Input, OnDestroy, TemplateRef, ViewContainerRef } from '@angular/core';
import { Subscription } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';

/**
 * @whatItDoes Conditionally includes an HTML element if current user has any
 * of the agency role ang _language actionType in agency passed as the `expression`.
 *
 * @howToUse
 * ```
 *     <some-element *jhiHasAnyAgencyAuthority="'ROLE_ADMIN'">...</some-element>
 *
 *     <some-element *jhiHasAnyAgencyAuthority="['ROLE_ADMIN', 'ROLE_USER']">...</some-element>
 * ```
 */
@Directive({
  selector: '[jhiHasAnyAgencyAuthority]'
})
export class HasAnyAgencyAuthorityDirective implements OnDestroy {
  private agencyAuthority: any;
  private authenticationSubscription?: Subscription;

  constructor(private accountService: AccountService, private templateRef: TemplateRef<any>, private viewContainerRef: ViewContainerRef) {}

  @Input('jhiHasAnyAgencyAuthority')
  set jhiHasAnyAgencyAuthority(value: any) {
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
    const hasAnyAuthority = this.accountService.hasAnyAgencyAuthority(
      this.agencyAuthority.actionType,
      this.agencyAuthority.agencyId,
      this.agencyAuthority.agencyRoles,
      this.agencyAuthority.language
    );
    this.viewContainerRef.clear();
    if (hasAnyAuthority) {
      this.viewContainerRef.createEmbeddedView(this.templateRef);
    }
  }
}
