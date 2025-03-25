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
import { MetadataFieldComponent } from './metadata-field.component';
import { MetadataFieldDetailComponent } from './metadata-field-detail.component';
import { MetadataFieldUpdateComponent } from './metadata-field-update.component';
import { MetadataFieldDeleteDialogComponent } from './metadata-field-delete-dialog.component';
import { metadataFieldRoute } from './metadata-field.route';

@NgModule({
    imports: [CvsSharedModule, RouterModule.forChild(metadataFieldRoute)],
    declarations: [MetadataFieldComponent, MetadataFieldDetailComponent, MetadataFieldUpdateComponent, MetadataFieldDeleteDialogComponent]
})
export class CvsMetadataFieldModule {}
