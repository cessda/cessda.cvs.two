import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CvsSharedModule } from 'app/shared/shared.module';
import { VersionComponent } from './version.component';
import { VersionDetailComponent } from './version-detail.component';
import { VersionUpdateComponent } from './version-update.component';
import { VersionDeleteDialogComponent } from './version-delete-dialog.component';
import { versionRoute } from './version.route';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild(versionRoute)],
  declarations: [VersionComponent, VersionDetailComponent, VersionUpdateComponent, VersionDeleteDialogComponent],
  entryComponents: [VersionDeleteDialogComponent]
})
export class CvsVersionModule {}
