import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {CvsSharedModule} from 'app/shared/shared.module';
import {AboutComponent} from './about.component';
import {aboutRoute} from './about.route';
import {QuillModule} from 'ngx-quill';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild(aboutRoute), QuillModule],
  declarations: [AboutComponent]
})
export class CvsAboutModule {}
