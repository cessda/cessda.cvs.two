import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CvsSharedModule } from 'app/shared/shared.module';
import { LicenceComponent } from './licence.component';
import { LicenceDetailComponent } from './licence-detail.component';
import { LicenceUpdateComponent } from './licence-update.component';
import { LicenceDeleteDialogComponent } from './licence-delete-dialog.component';
import { licenceRoute } from './licence.route';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild(licenceRoute)],
  declarations: [LicenceComponent, LicenceDetailComponent, LicenceUpdateComponent, LicenceDeleteDialogComponent],
  entryComponents: [LicenceDeleteDialogComponent]
})
export class CvsLicenceModule {}
