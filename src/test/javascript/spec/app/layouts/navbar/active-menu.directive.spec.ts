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
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

import { CvsTestModule } from '../../../test.module';
import { ActiveMenuDirective } from 'app/layouts/navbar/active-menu.directive';

@Component({
  template: '<span jhiActiveMenu="en" class="entry">English</span>',
  standalone: false,
})
class HostComponent {}

describe('Directive Tests', () => {
  describe('ActiveMenu Directive', () => {
    let fixture: ComponentFixture<HostComponent>;
    let translateService: TranslateService;

    const entry = (): HTMLElement => fixture.debugElement.query(By.css('.entry')).nativeElement;
    const isActive = (): boolean => entry().classList.contains('active');

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule, TranslateModule.forRoot()],
        declarations: [ActiveMenuDirective, HostComponent],
      }).compileComponents();
    }));

    beforeEach(() => {
      translateService = TestBed.inject(TranslateService);
      fixture = TestBed.createComponent(HostComponent);
    });

    it('should mark the entry active when it is the current language', () => {
      translateService.currentLang = 'en';

      fixture.detectChanges();

      expect(isActive()).toBe(true);
    });

    it('should leave the entry alone when it is not', () => {
      translateService.currentLang = 'de';

      fixture.detectChanges();

      expect(isActive()).toBe(false);
    });

    describe('when the language changes', () => {
      it('should mark the entry active once its language is chosen', () => {
        translateService.currentLang = 'de';
        fixture.detectChanges();

        translateService.onLangChange.emit({ lang: 'en', translations: {} });

        expect(isActive()).toBe(true);
      });

      it('should drop the mark once another language is chosen', () => {
        translateService.currentLang = 'en';
        fixture.detectChanges();

        translateService.onLangChange.emit({ lang: 'de', translations: {} });

        expect(isActive()).toBe(false);
      });
    });
  });
});
