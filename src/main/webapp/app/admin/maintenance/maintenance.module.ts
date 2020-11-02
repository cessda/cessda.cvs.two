import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {CvsSharedModule} from 'app/shared/shared.module';

import {MaintenanceComponent} from './maintenance.component';

import {maintenanceRoute} from './maintenance.route';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild([maintenanceRoute])],
  declarations: [MaintenanceComponent]
})
export class MaintenanceModule {}
