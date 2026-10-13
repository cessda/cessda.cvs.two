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

type Predicate = (value: any) => boolean;

/**
 * Keeps the items matching the filter: a string matches case-insensitively as a substring of `field`,
 * a number or a boolean matches `field` exactly, and an object matches when all of its fields match the item's fields.
 * Without `field`, the items themselves are compared. An empty filter keeps all items.
 */
@Pipe({
  name: 'pureFilter',
  standalone: false,
})
export class PureFilterPipe implements PipeTransform {
  transform(input: any[], filter: any, field?: string): any[] {
    if (!input || filter === undefined || filter === null || filter === '') {
      return input;
    }
    const currentFilter = typeof filter === 'function' ? filter() : filter;
    if (typeof currentFilter === 'object') {
      return input.filter(value => Object.keys(currentFilter).every(key => matches(currentFilter[key])(value?.[key])));
    }
    return input.filter(value => matches(currentFilter)(field && typeof value === 'object' && value ? value[field] : value));
  }
}

function matches(filter: any): Predicate {
  if (typeof filter === 'string') {
    return value => typeof value === 'string' && value.toLowerCase().includes(filter.toLowerCase());
  }
  return value => value === filter;
}
