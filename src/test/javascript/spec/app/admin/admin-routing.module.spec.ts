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

import { AdminRoutingModule } from 'app/admin/admin-routing.module';
import { AuditsModule } from 'app/admin/audits/audits.module';
import { ConfigurationModule } from 'app/admin/configuration/configuration.module';
import { HealthModule } from 'app/admin/health/health.module';
import { LogsModule } from 'app/admin/logs/logs.module';
import { MaintenanceModule } from 'app/admin/maintenance/maintenance.module';
import { MetricsModule } from 'app/admin/metrics/metrics.module';
import { CvsLicenceModule } from 'app/admin/licence/licence.module';
import { CvsResolverModule } from 'app/admin/resolver/resolver.module';

describe('AdminRoutingModule', () => {
  let routes: Route[];

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [AdminRoutingModule] });
    routes = TestBed.inject(ROUTES).flat();
  });

  it.each([
    { path: 'audits', module: AuditsModule },
    { path: 'configuration', module: ConfigurationModule },
    { path: 'health', module: HealthModule },
    { path: 'logs', module: LogsModule },
    { path: 'maintenance', module: MaintenanceModule },
    { path: 'metrics', module: MetricsModule },
    { path: 'licence', module: CvsLicenceModule },
    { path: 'resolver', module: CvsResolverModule },
  ])('should load $path from its own module', async ({ path, module }) => {
    const route = routes.find(r => r.path === path);

    await expect((route?.loadChildren as LoadChildrenCallback)()).resolves.toBe(module);
  });

  // user-management is asserted through the route table only: its module pulls in
  // @swimlane/ngx-charts, whose d3 dependencies ship as untransformed ESM and cannot load under jest

  it('should declare every admin area lazily and nothing else', () => {
    expect(routes.map(r => r.path)).toEqual([
      'user-management',
      'audits',
      'configuration',
      'health',
      'logs',
      'maintenance',
      'metrics',
      'licence',
      'resolver',
    ]);
    expect(routes.every(r => typeof r.loadChildren === 'function')).toBe(true);
  });

  it('should title the user management area, which the navbar reads', () => {
    const userManagement = routes.find(r => r.path === 'user-management');

    expect(userManagement?.data?.pageTitle).toBe('userManagement.home.title');
  });
});
