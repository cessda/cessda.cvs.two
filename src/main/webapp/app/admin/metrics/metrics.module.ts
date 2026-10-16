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
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CvsSharedModule } from 'app/shared/shared.module';

import { MetricsComponent } from './metrics.component';
import { JvmMemoryComponent } from './blocks/jvm-memory/jvm-memory.component';
import { JvmThreadsComponent } from './blocks/jvm-threads/jvm-threads.component';
import { ThreadModalComponent } from './blocks/jvm-threads/thread-modal.component';
import { MetricsSystemComponent } from './blocks/metrics-system/metrics-system.component';
import { MetricsGarbageCollectorComponent } from './blocks/metrics-garbagecollector/metrics-garbagecollector.component';
import { MetricsRequestComponent } from './blocks/metrics-request/metrics-request.component';
import { MetricsEndpointsRequestsComponent } from './blocks/metrics-endpoints-requests/metrics-endpoints-requests.component';
import { MetricsCacheComponent } from './blocks/metrics-cache/metrics-cache.component';
import { MetricsDatasourceComponent } from './blocks/metrics-datasource/metrics-datasource.component';

import { metricsRoute } from './metrics.route';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild([metricsRoute])],
  declarations: [
    MetricsComponent,
    JvmMemoryComponent,
    JvmThreadsComponent,
    ThreadModalComponent,
    MetricsSystemComponent,
    MetricsGarbageCollectorComponent,
    MetricsRequestComponent,
    MetricsEndpointsRequestsComponent,
    MetricsCacheComponent,
    MetricsDatasourceComponent,
  ],
})
export class MetricsModule {}
