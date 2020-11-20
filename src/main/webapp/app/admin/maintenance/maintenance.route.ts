import { Route } from '@angular/router';

import { MaintenanceComponent } from './maintenance.component';

export const maintenanceRoute: Route = {
  path: '',
  component: MaintenanceComponent,
  data: {
    authorities: ['ROLE_ADMIN', 'ROLE_ADMIN_TECHNICAL'],
    pageTitle: 'global.menu.admin.maintenance'
  }
};
