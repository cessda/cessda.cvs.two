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
import { TestBed } from '@angular/core/testing';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

import { LanguageService } from 'app/core/language/language.service';
import { missingTranslationHandler } from 'app/core/language/translation.config';

describe('LanguageService', () => {
  let languageService: LanguageService;
  let translateService: TranslateService;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [TranslateModule.forRoot()] });
    languageService = TestBed.inject(LanguageService);
    translateService = TestBed.inject(TranslateService);
  });

  it('should start in English', () => {
    languageService.init();

    expect(translateService.getDefaultLang()).toBe('en');
    expect(translateService.currentLang).toBe('en');
    expect(languageService.getCurrentLanguage()).toBe('en');
  });

  it('should switch the translations to the chosen language', () => {
    languageService.init();

    languageService.changeLanguage('de');

    expect(translateService.currentLang).toBe('de');
    expect(languageService.getCurrentLanguage()).toBe('de');
    expect(languageService.currentLang).toBe('de');
  });
});

describe('missingTranslationHandler', () => {
  it('should mark a key that has no translation', () => {
    const handler = missingTranslationHandler();

    expect(handler.handle({ key: 'global.unknown', translateService: {} as TranslateService })).toBe(
      'translation-not-found[global.unknown]',
    );
  });
});
