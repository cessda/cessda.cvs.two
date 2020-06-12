import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CvsSharedModule } from 'app/shared/shared.module';
import { UserGuideComponent } from './user-guide.component';
import { userGuideRoute } from './user-guide.route';
import { QuillModule } from 'ngx-quill';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild(userGuideRoute), QuillModule],
  declarations: [UserGuideComponent]
})
export class CvsUserGuideModule {}
