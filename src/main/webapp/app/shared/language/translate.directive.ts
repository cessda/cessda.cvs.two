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
import { Directive, ElementRef, Input, OnChanges, OnDestroy, OnInit, inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { translationNotFoundMessage } from 'app/core/language/translation.config';

/**
 * Replaces the content of the host element with the translation of the given key, e.g.
 * `<span jhiTranslate="global.title" [translateValues]="{ name }">Fallback text</span>`.
 * The translation is set as innerHTML, so it may contain markup but replaces anything nested in the element.
 * Replaces the directive formerly provided by ng-jhipster.
 */
@Directive({
  selector: '[jhiTranslate]',
  standalone: false,
})
export class TranslateDirective implements OnInit, OnChanges, OnDestroy {
  private el = inject(ElementRef);
  private translateService = inject(TranslateService, { optional: true });

  @Input() jhiTranslate!: string;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  @Input() translateValues?: { [key: string]: any };

  private readonly directiveDestroyed = new Subject<void>();

  ngOnInit(): void {
    this.translateService?.onLangChange.pipe(takeUntil(this.directiveDestroyed)).subscribe(() => this.getTranslation());
  }

  ngOnChanges(): void {
    this.getTranslation();
  }

  ngOnDestroy(): void {
    this.directiveDestroyed.next();
    this.directiveDestroyed.complete();
  }

  private getTranslation(): void {
    this.translateService
      ?.get(this.jhiTranslate, this.translateValues)
      .pipe(takeUntil(this.directiveDestroyed))
      .subscribe({
        next: value => (this.el.nativeElement.innerHTML = value),
        error: () => (this.el.nativeElement.innerHTML = `${translationNotFoundMessage}[${this.jhiTranslate}]`),
      });
  }
}
