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
import { Directive, HostListener, Input, inject } from '@angular/core';

import { SortDirective } from './sort.directive';

/**
 * Sorts the table of the enclosing `[jhiSort]` by the given field when the host header is clicked.
 * Show the sort state with `<fa-icon [icon]="sort.iconFor('field')">`, where `sort` refers to the `jhiSort` directive.
 * Replaces the directive formerly provided by ng-jhipster.
 */
@Directive({
  selector: '[jhiSortBy]',
  standalone: false,
})
export class SortByDirective {
  private sortDirective = inject(SortDirective, { host: true });

  @Input() jhiSortBy!: string;

  @HostListener('click')
  onClick(): void {
    this.sortDirective.sort(this.jhiSortBy);
  }
}
