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
/* eslint-disable @typescript-eslint/no-explicit-any */
import { Component, Input } from '@angular/core';

@Component({
  selector: 'jhi-metrics-system',
  templateUrl: './metrics-system.component.html',
  standalone: false,
})
export class MetricsSystemComponent {
  @Input() systemMetrics?: any;
  @Input() updating?: boolean;

  convertMillisecondsToDuration(ms: number): string {
    const times: { [unit: string]: number } = {
      year: 31557600000,
      month: 2629746000,
      day: 86400000,
      hour: 3600000,
      minute: 60000,
      second: 1000,
    };
    let timeString = '';
    for (const [unit, unitMs] of Object.entries(times)) {
      const amount = Math.floor(ms / unitMs);
      if (amount > 0) {
        timeString += `${amount} ${unit}${amount > 1 ? 's' : ''} `;
        ms -= amount * unitMs;
      }
    }
    return timeString;
  }
}
