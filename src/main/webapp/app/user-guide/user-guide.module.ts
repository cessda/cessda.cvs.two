import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CvsSharedModule } from 'app/shared/shared.module';
import { UserGuideComponent } from './user-guide.component';
import { userGuideRoute } from './user-guide.route';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild(userGuideRoute)],
  declarations: [UserGuideComponent]
})
export class CvsUserGuideModule {}
