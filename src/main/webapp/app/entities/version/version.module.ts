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
