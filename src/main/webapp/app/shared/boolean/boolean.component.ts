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
import { Component, Input, OnInit } from '@angular/core';

/**
 * Renders a boolean value as an icon, or as a badge when a text is given.
 * Replaces the component formerly provided by ng-jhipster.
 */
@Component({
  selector: 'jhi-boolean',
  template: `<span [ngClass]="value ? classTrue : classFalse" [innerHtml]="value ? textTrue : textFalse"></span>`,
  standalone: false,
})
export class BooleanComponent implements OnInit {
  @Input() value?: boolean;
  @Input() classTrue?: string;
  @Input() classFalse?: string;
  @Input() textTrue?: string;
  @Input() textFalse?: string;

  ngOnInit(): void {
    this.classTrue ??= this.textTrue === undefined ? 'fa fa-lg fa-check text-success' : 'badge badge-success';
    this.classFalse ??= this.textFalse === undefined ? 'fa fa-lg fa-times text-danger' : 'badge badge-danger';
  }
}
