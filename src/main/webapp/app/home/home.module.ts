import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CvsSharedModule } from 'app/shared';
import { HOME_ROUTE, HomeComponent, HomeDetailComponent } from './';
import { TagInputModule } from 'ngx-chips';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild(HOME_ROUTE), TagInputModule],
  declarations: [HomeComponent, HomeDetailComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CvsHomeModule {}
