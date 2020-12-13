import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {CvsSharedModule} from 'app/shared/shared.module';

import {AuditsComponent} from './audits.component';

import {auditsRoute} from './audits.route';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild([auditsRoute])],
  declarations: [AuditsComponent]
})
export class AuditsModule {}
