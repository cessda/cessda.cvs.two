import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { errorRoute } from './layouts/error/error.route';
import { navbarRoute } from './layouts/navbar/navbar.route';
import { DEBUG_INFO_ENABLED } from 'app/app.constants';
import { Authority } from 'app/shared/constants/authority.constants';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';

const LAYOUT_ROUTES = [navbarRoute, ...errorRoute];

@NgModule({
  imports: [
    RouterModule.forRoot(
      [
        {
          path: 'admin',
          data: {
            authorities: [Authority.ADMIN]
          },
          canActivate: [UserRouteAccessService],
          loadChildren: () => import('./admin/admin-routing.module').then(m => m.AdminRoutingModule)
        },
        {
          path: 'editor',
          data: {
            authorities: [Authority.ADMIN, Authority.USER]
          },
          canActivate: [UserRouteAccessService],
          loadChildren: () => import('./editor/editor.module').then(m => m.CvsEditorModule)
        },
        {
          path: 'agency',
          loadChildren: () => import('./agency/agency.module').then(m => m.CvsAgencyModule)
        },
        {
          path: 'account',
          loadChildren: () => import('./account/account.module').then(m => m.AccountModule)
        },
        ...LAYOUT_ROUTES
      ],
      { enableTracing: DEBUG_INFO_ENABLED, onSameUrlNavigation: 'reload' }
    )
  ],
  exports: [RouterModule]
})
export class CvsAppRoutingModule {}
