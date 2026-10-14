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
import { Pipe, PipeTransform } from '@angular/core';

/**
 * Sorts the items by the given field, or the items themselves when no field is given, in ascending order unless `reverse` is set.
 */
@Pipe({
  name: 'orderBy',
  standalone: false,
})
export class OrderByPipe implements PipeTransform {
  transform(values: any[], predicate = '', reverse = false): any[] {
    if (!values) {
      return values;
    }
    const direction = reverse ? -1 : 1;
    const valueOf = (item: any): any => (predicate === '' ? item : item?.[predicate]);
    return [...values].sort((a, b) => {
      if (valueOf(a) < valueOf(b)) {
        return -direction;
      }
      if (valueOf(b) < valueOf(a)) {
        return direction;
      }
      return 0;
    });
  }
}
