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
  declarations: [MetadataFieldComponent, MetadataFieldDetailComponent, MetadataFieldUpdateComponent, MetadataFieldDeleteDialogComponent],
  entryComponents: [MetadataFieldDeleteDialogComponent]
})
export class CvsMetadataFieldModule {}
