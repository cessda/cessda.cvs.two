import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CvsSharedModule } from 'app/shared/shared.module';
import { ApiDocsComponent } from './api-docs.component';
import { apiDocsRoute } from './api-docs.route';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild(apiDocsRoute)],
  declarations: [ApiDocsComponent]
})
export class CvsApiDocsModule {}
