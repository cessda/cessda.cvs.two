import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CvsSharedModule } from 'app/shared';
import { HOME_ROUTE, HomeComponent, HomeDetailComponent } from './';
import { TagInputModule } from 'ngx-chips';
import { NgxTextDiffModule } from 'ngx-text-diff';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild(HOME_ROUTE), TagInputModule, NgxTextDiffModule],
  declarations: [HomeComponent, HomeDetailComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CvsHomeModule {}
