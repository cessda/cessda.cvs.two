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
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { throwError } from 'rxjs';

import { TranslateDirective } from 'app/shared/language/translate.directive';

@Component({
  template: `<span jhiTranslate="greeting" [translateValues]="{ name: name }">Fallback</span>`,
  standalone: false,
})
class TestHostComponent {
  name = 'World';
}

describe('TranslateDirective', () => {
  let fixture: ComponentFixture<TestHostComponent>;
  let translateService: TranslateService;
  const text = () => fixture.nativeElement.querySelector('span').innerHTML;

  describe('with translations', () => {
    beforeEach(() => {
      TestBed.configureTestingModule({ imports: [TranslateModule.forRoot()], declarations: [TranslateDirective, TestHostComponent] });
      translateService = TestBed.inject(TranslateService);
      translateService.setTranslation('en', { greeting: 'Hello <b>{{ name }}</b>' });
      translateService.setTranslation('de', { greeting: 'Hallo <b>{{ name }}</b>' });
      translateService.use('en');
      fixture = TestBed.createComponent(TestHostComponent);
      fixture.detectChanges();
    });

    it('should replace the content with the translation, markup included', () => {
      expect(text()).toBe('Hello <b>World</b>');
    });

    it('should translate again when the values change', () => {
      fixture.componentInstance.name = 'CESSDA';
      fixture.detectChanges();

      expect(text()).toBe('Hello <b>CESSDA</b>');
    });

    it('should translate again when the language changes', () => {
      translateService.use('de');

      expect(text()).toBe('Hallo <b>World</b>');
    });

    it('should mark a key whose translation fails to load', () => {
      jest.spyOn(translateService, 'get').mockReturnValue(throwError(() => new Error('not loaded')));

      fixture.componentInstance.name = 'CESSDA';
      fixture.detectChanges();

      expect(text()).toBe('translation-not-found[greeting]');
    });
  });

  it('should leave the content alone without a translation service', () => {
    TestBed.configureTestingModule({ declarations: [TranslateDirective, TestHostComponent] });
    fixture = TestBed.createComponent(TestHostComponent);
    fixture.detectChanges();

    expect(text()).toBe('Fallback');
  });
});
