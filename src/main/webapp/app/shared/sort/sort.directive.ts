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
import { Directive, EventEmitter, Input, Output } from '@angular/core';

export type SortIcon = 'sort' | 'sort-up' | 'sort-down';

/**
 * Holds the sort state of a table, toggled by its `[jhiSortBy]` headers, e.g.
 * `<tr jhiSort #sort="jhiSort" [(predicate)]="predicate" [(ascending)]="ascending" [callback]="loadPage.bind(this)">`.
 * Replaces the directive formerly provided by ng-jhipster.
 */
@Directive({
  selector: '[jhiSort]',
  exportAs: 'jhiSort',
  standalone: false,
})
export class SortDirective {
  @Input() predicate?: string;
  @Input() ascending?: boolean;
  @Input() callback?: () => void;

  @Output() predicateChange = new EventEmitter<string>();
  @Output() ascendingChange = new EventEmitter<boolean>();

  sort(field: string): void {
    // a relevance ordered search result has no column to sort by
    if (!this.predicate || this.predicate === '_score') {
      return;
    }
    this.ascending = field !== this.predicate ? true : !this.ascending;
    this.predicate = field;
    this.predicateChange.emit(field);
    this.ascendingChange.emit(this.ascending);
    this.callback?.();
  }

  iconFor(field: string): SortIcon {
    if (!this.predicate || this.predicate === '_score' || this.predicate !== field) {
      return 'sort';
    }
    return this.ascending ? 'sort-up' : 'sort-down';
  }
}
