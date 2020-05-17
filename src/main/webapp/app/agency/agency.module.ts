import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CvsSharedModule } from 'app/shared/shared.module';
import { AgencyComponent } from './agency.component';
import { AgencyDetailComponent } from './agency-detail.component';
import { AgencyUpdateComponent } from './agency-update.component';
import { AgencyDeleteDialogComponent } from './agency-delete-dialog.component';
import { agencyRoute } from './agency.route';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild(agencyRoute)],
  declarations: [AgencyComponent, AgencyDetailComponent, AgencyUpdateComponent, AgencyDeleteDialogComponent],
  entryComponents: [AgencyDeleteDialogComponent]
})
export class CvsAgencyModule {}
