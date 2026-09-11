/*
 * Copyright © 2017-2026 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { TestBed } from '@angular/core/testing';
import { LoadChildrenCallback, Route, ROUTES } from '@angular/router';

import { CvsAppRoutingModule } from 'app/app-routing.module';
import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { AdminRoutingModule } from 'app/admin/admin-routing.module';
import { CvsEditorModule } from 'app/editor/editor.module';
import { CvsAboutModule } from 'app/about/about.module';
import { CvsApiDocsModule } from 'app/api-docs/api-docs.module';
import { DocsModule } from 'app/docs/docs.module';
import { AccountModule } from 'app/account/account.module';
import { NavbarComponent } from 'app/layouts/navbar/navbar.component';
import { ErrorComponent } from 'app/layouts/error/error.component';

describe('CvsAppRoutingModule', () => {
  let routes: Route[];

  const routeFor = (path: string): Route => {
    const route = routes.find(r => r.path === path);
    if (!route) {
      throw new Error(`no route declared for '${path}', found: ${routes.map(r => r.path).join(', ')}`);
    }
    return route;
  };

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [CvsAppRoutingModule] });
    // forRoot registers the table under ROUTES as an array of arrays
    routes = TestBed.inject(ROUTES).flat();
  });

  describe('lazily loaded areas', () => {
    it.each([
      { path: 'admin', module: AdminRoutingModule },
      { path: 'editor', module: CvsEditorModule },
      { path: 'about', module: CvsAboutModule },
      { path: 'api-docs', module: CvsApiDocsModule },
      { path: 'swagger', module: DocsModule },
      { path: 'account', module: AccountModule },
    ])('should load $path from its own module', async ({ path, module }) => {
      const loadChildren = routeFor(path).loadChildren as LoadChildrenCallback;

      await expect(loadChildren()).resolves.toBe(module);
    });

    // agency is declared lazily like the rest, but its module pulls in @swimlane/ngx-charts, whose
    // d3 dependencies ship as untransformed ESM and cannot be loaded under jest
    it('should declare agency lazily as well', () => {
      expect(typeof routeFor('agency').loadChildren).toBe('function');
    });
  });

  describe('access control', () => {
    it.each([
      { path: 'admin', authorities: [Authority.ADMIN, Authority.ADMIN_CONTENT, Authority.ADMIN_TECHNICAL] },
      { path: 'editor', authorities: [Authority.ADMIN, Authority.USER, Authority.ADMIN_CONTENT] },
    ])('should guard $path and demand one of its authorities', ({ path, authorities }) => {
      const route = routeFor(path);

      expect(route.canActivate).toEqual([UserRouteAccessService]);
      expect(route.data?.authorities).toEqual(authorities);
    });

    it.each([{ path: 'agency' }, { path: 'about' }, { path: 'api-docs' }, { path: 'swagger' }, { path: 'account' }])(
      'should leave $path open to anyone',
      ({ path }) => {
        const route = routeFor(path);

        expect(route.canActivate).toBeUndefined();
        expect(route.data?.authorities).toBeUndefined();
      },
    );
  });

  describe('the layout routes', () => {
    it('should keep the catch-all last, so it cannot swallow the routes above it', () => {
      expect(routes[routes.length - 1].path).toBe('**');
      expect(routes.filter(r => r.path === '**')).toHaveLength(1);
    });

    it('should render the navbar in its own outlet', () => {
      const navbar = routes.find(r => r.outlet === 'navbar');

      expect(navbar?.component).toBe(NavbarComponent);
      expect(navbar?.path).toBe('');
    });

    it.each([
      { path: 'error', errorMessage: undefined },
      { path: 'accessdenied', errorMessage: 'error.http.403' },
      { path: '**', errorMessage: 'error.http.404' },
    ])('should show the error component for $path', ({ path, errorMessage }) => {
      const route = routeFor(path);

      expect(route.component).toBe(ErrorComponent);
      expect(route.data?.errorMessage).toBe(errorMessage);
    });
  });
});
