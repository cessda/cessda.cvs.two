/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
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
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { JhiLanguageService } from 'ng-jhipster';

import { CvsTestModule } from '../../test.module';
import { VocabularySearchResultComponent } from 'app/shared/vocabulary-search-result/vocabulary-search-result.component';
import { VocabularyLanguageFromKeyPipe } from 'app/shared/language/vocabulary-language-from-key.pipe';
import { createNewVocabulary, Vocabulary } from 'app/shared/model/vocabulary.model';
import { Code, createNewCode } from 'app/shared/model/code.model';

describe('Component Tests', () => {
  describe('Vocabulary Search Result Component', () => {
    let comp: VocabularySearchResultComponent;
    let fixture: ComponentFixture<VocabularySearchResultComponent>;

    const vocabulary = (): Vocabulary =>
      createNewVocabulary({
        notation: 'AnalysisUnit',
        versionNumber: '2.0.0',
        selectedLang: 'en',
        sourceLanguage: 'en',
        titleEn: 'Analysis Unit',
        definitionEn: 'The unit of analysis',
        versionEn: '2.0.0',
        titleDe: 'Analyseeinheit',
        definitionDe: 'Die Analyseeinheit',
        versionDe: '2.0.1_DRAFT',
      });

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [VocabularySearchResultComponent],
        providers: [VocabularyLanguageFromKeyPipe],
      })
        .overrideTemplate(VocabularySearchResultComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(VocabularySearchResultComponent);
      comp = fixture.componentInstance;
    });

    describe('where the results link to', () => {
      it('should link into the editor when shown in the editor', () => {
        comp.appScope = 'EDITOR' as unknown as typeof comp.appScope;

        expect(comp.isEditorScope()).toBe(true);
        expect(comp.getBaseUrl()).toBe('/editor/vocabulary');
      });

      it('should link to the public page otherwise', () => {
        comp.appScope = 'PUBLICATION' as unknown as typeof comp.appScope;

        expect(comp.isEditorScope()).toBe(false);
        expect(comp.getBaseUrl()).toBe('/vocabulary');
      });
    });

    describe('reading a vocabulary in the selected language', () => {
      it('should take the title and definition from the selected language', () => {
        const vocab = vocabulary();

        expect(comp.getTitleByLang(vocab)).toBe('Analysis Unit');
        expect(comp.getDefinitionByLang(vocab)).toBe('The unit of analysis');
        expect(comp.getVersionByLang(vocab)).toBe('2.0.0');
      });

      it('should follow the selected language when it changes', () => {
        const vocab = { ...vocabulary(), selectedLang: 'de' };

        expect(comp.getTitleByLang(vocab)).toBe('Analyseeinheit');
        expect(comp.getVersionByLang(vocab)).toBe('2.0.1_DRAFT');
      });
    });

    describe('reading a code', () => {
      const code = (): Code => ({ ...createNewCode(), notation: 'Individual', titleEn: 'Individual', definitionEn: 'A person' });

      it('should take the title and definition from the given language', () => {
        expect(comp.getCodeTitleByLang(code(), 'en')).toBe('Individual');
        expect(comp.getCodeDefinitionByLang(code(), 'en')).toBe('A person');
      });

      it('should mark a deprecated term in the title', () => {
        expect(comp.getCodeTitleByLang({ ...code(), deprecated: true }, 'en')).toBe('Individual (DEPRECATED TERM)');
      });
    });

    describe('version status of a language', () => {
      it('should find the status carried in the version string', () => {
        expect(comp.isVersionContains(vocabulary(), 'de', 'DRAFT')).toBe(true);
        expect(comp.isVersionContains(vocabulary(), 'en', 'DRAFT')).toBe(false);
      });

      it('should match a language version against the vocabulary bundle', () => {
        expect(comp.isLangVersionInBundle(vocabulary(), 'en')).toBe(true);
        expect(comp.isLangVersionInBundle(vocabulary(), 'de')).toBe(false);
      });

      it('should honour an explicitly given bundle', () => {
        expect(comp.isLangVersionInBundle(vocabulary(), 'de', '2.0.1_DRAFT')).toBe(true);
      });
    });

    describe('formatting a language for display', () => {
      it('should name the language and its version', () => {
        expect(comp.getFormattedLangIso(vocabulary(), 'en', 'de')).toBe('English (en) 2.0.0');
      });

      it('should mark the source language', () => {
        expect(comp.getFormattedLangIso(vocabulary(), 'en', 'en')).toBe('English (en) 2.0.0 SOURCE');
      });

      it('should append the status when the version carries one', () => {
        expect(comp.getFormattedLangIso(vocabulary(), 'de', 'en')).toBe('German (de) 2.0.1_DRAFT (DRAFT)');
      });

      it('should refuse a language the vocabulary has no title for', () => {
        expect(() => comp.getFormattedLangIso(vocabulary(), 'sk', 'en')).toThrow();
      });
    });

    describe('sorting languages', () => {
      it('should put the source language first', () => {
        expect(comp.sortLangByEnum(['de', 'en', 'bg'], 'en')).toEqual(['en', 'bg', 'de']);
      });

      it('should cope with nothing to sort', () => {
        expect(comp.sortLangByEnum(undefined, undefined)).toEqual(['']);
      });
    });

    it('should track results by notation', () => {
      expect(comp.trackNotation(0, { ...createNewCode(), notation: 'Individual' })).toBe('Individual');
    });

    it('should report the interface language the language service holds', () => {
      (TestBed.inject(JhiLanguageService) as unknown as { currentLang: string }).currentLang = 'de';

      expect(comp.getCurrentLanguage()).toBe('de');
    });
  });
});
