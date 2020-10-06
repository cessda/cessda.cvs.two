import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CvsSharedModule } from 'app/shared/shared.module';
import { AgencyComponent } from './agency.component';
import { AgencyDetailDialogComponent, AgencyDetailPopupComponent } from './agency-detail-dialog.component';
import { AgencyUpdateComponent } from './agency-update.component';
import { AgencyDeleteDialogComponent } from './agency-delete-dialog.component';
import { agencyRoute } from './agency.route';
import { PieChartModule } from '@swimlane/ngx-charts';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild(agencyRoute), PieChartModule],
  declarations: [
    AgencyComponent,
    AgencyDetailDialogComponent,
    AgencyUpdateComponent,
    AgencyDeleteDialogComponent,
    AgencyDetailPopupComponent
  ],
  entryComponents: [AgencyDeleteDialogComponent, AgencyDetailPopupComponent]
})
export class CvsAgencyModule {}
