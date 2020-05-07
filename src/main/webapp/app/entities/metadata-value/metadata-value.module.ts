import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CvsSharedModule } from 'app/shared/shared.module';
import { MetadataValueComponent } from './metadata-value.component';
import { MetadataValueDetailComponent } from './metadata-value-detail.component';
import { MetadataValueUpdateComponent } from './metadata-value-update.component';
import { MetadataValueDeleteDialogComponent } from './metadata-value-delete-dialog.component';
import { metadataValueRoute } from './metadata-value.route';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild(metadataValueRoute)],
  declarations: [MetadataValueComponent, MetadataValueDetailComponent, MetadataValueUpdateComponent, MetadataValueDeleteDialogComponent],
  entryComponents: [MetadataValueDeleteDialogComponent]
})
export class CvsMetadataValueModule {}
