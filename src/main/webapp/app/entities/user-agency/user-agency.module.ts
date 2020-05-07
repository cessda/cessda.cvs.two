import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CvsSharedModule } from 'app/shared/shared.module';
import { UserAgencyComponent } from './user-agency.component';
import { UserAgencyDetailComponent } from './user-agency-detail.component';
import { UserAgencyUpdateComponent } from './user-agency-update.component';
import { UserAgencyDeleteDialogComponent } from './user-agency-delete-dialog.component';
import { userAgencyRoute } from './user-agency.route';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild(userAgencyRoute)],
  declarations: [UserAgencyComponent, UserAgencyDetailComponent, UserAgencyUpdateComponent, UserAgencyDeleteDialogComponent],
  entryComponents: [UserAgencyDeleteDialogComponent]
})
export class CvsUserAgencyModule {}
