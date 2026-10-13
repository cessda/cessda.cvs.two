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
import { Component, Input } from '@angular/core';

/**
 * Shows which items of a paginated list are on the current page, e.g. "Showing 21 - 40 of 95 items."
 * Replaces the component formerly provided by ng-jhipster.
 */
@Component({
  selector: 'jhi-item-count',
  template: `<div class="info jhi-item-count" jhiTranslate="global.item-count" [translateValues]="i18nValues()"></div>`,
  standalone: false,
})
export class ItemCountComponent {
  @Input() page = 1;
  @Input() total = 0;
  @Input() itemsPerPage = 0;

  i18nValues(): { first: number; second: number; total: number } {
    const first = (this.page - 1) * this.itemsPerPage + 1;
    const second = Math.min(this.page * this.itemsPerPage, this.total);
    return { first, second, total: this.total };
  }
}
