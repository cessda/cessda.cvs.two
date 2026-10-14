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
import { Pipe, PipeTransform } from '@angular/core';

/**
 * Turns an object into a list of its entries, e.g. `@for (entry of health.components | keys; ...)` with `entry.key` and `entry.value`.
 */
@Pipe({
  name: 'keys',
  standalone: false,
})
export class KeysPipe implements PipeTransform {
  // the entries are untyped, as they are matched against the various typed keys of the objects listed in templates
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  transform(value: { [key: string]: any } | null | undefined): any[] {
    return Object.keys(value ?? {}).map(key => ({ key, value: value![key] }));
  }
}
