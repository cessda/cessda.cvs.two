/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

import { NgModule } from '@angular/core';
import { ExtraOptions, RouterModule } from '@angular/router';
import { errorRoute } from './layouts/error/error.route';
import { navbarRoute } from './layouts/navbar/navbar.route';
import { DEBUG_INFO_ENABLED } from 'app/app.constants';
import { Authority } from 'app/shared/constants/authority.constants';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';

const LAYOUT_ROUTES = [navbarRoute, ...errorRoute];

const routerOptions: ExtraOptions = {
  useHash: false,
  anchorScrolling: 'enabled',
  // scrollPositionRestoration: 'enabled',
  enableTracing: DEBUG_INFO_ENABLED,
  onSameUrlNavigation: 'reload',
};

@NgModule({
  imports: [
    RouterModule.forRoot(
      [
        {
          path: 'admin',
          data: {
            authorities: [Authority.ADMIN],
          },
          canActivate: [UserRouteAccessService],
          loadChildren: () => import('./admin/admin-routing.module').then(m => m.AdminRoutingModule),
        },
        {
          path: 'editor',
          data: {
            authorities: [Authority.ADMIN, Authority.USER],
          },
          canActivate: [UserRouteAccessService],
          loadChildren: () => import('./editor/editor.module').then(m => m.CvsEditorModule),
        },
        {
          path: 'agency',
          loadChildren: () => import('./agency/agency.module').then(m => m.CvsAgencyModule),
        },
        {
          path: 'about',
          loadChildren: () => import('./about/about.module').then(m => m.CvsAboutModule),
        },
        {
          path: 'api-docs',
          loadChildren: () => import('./api-docs/api-docs.module').then(m => m.CvsApiDocsModule),
        },
        {
          path: 'swagger',
          loadChildren: () => import('./docs/docs.module').then(m => m.DocsModule),
        },
        {
          path: 'account',
          loadChildren: () => import('./account/account.module').then(m => m.AccountModule),
        },
        ...LAYOUT_ROUTES,
      ],
      routerOptions
    ),
  ],
  exports: [RouterModule],
})
export class CvsAppRoutingModule {}
