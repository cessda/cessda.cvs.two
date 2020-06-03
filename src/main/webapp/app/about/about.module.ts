import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CvsSharedModule } from 'app/shared/shared.module';
import { AboutComponent } from './about.component';
import { aboutRoute } from './about.route';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild(aboutRoute)],
  declarations: [AboutComponent]
})
export class CvsAboutModule {}
