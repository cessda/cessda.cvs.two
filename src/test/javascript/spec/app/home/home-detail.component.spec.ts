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
import { JhiEventManager } from 'ng-jhipster';
import { BehaviorSubject } from 'rxjs';

import { CvsTestModule } from '../../test.module';
import { MockEventManager } from '../../helpers/mock-event-manager.service';
import { HomeDetailComponent } from 'app/home/home-detail.component';
import { VocabularyLanguageFromKeyPipe } from 'app/shared/language/vocabulary-language-from-key.pipe';
import { RouteEventsService } from 'app/shared/service/route-events.service';
import { createNewVocabulary, Vocabulary } from 'app/shared/model/vocabulary.model';
import { createNewVersion, Version } from 'app/shared/model/version.model';
import { Concept } from 'app/shared/model/concept.model';

describe('Component Tests', () => {
  describe('Home Detail Component', () => {
    let comp: HomeDetailComponent;
    let fixture: ComponentFixture<HomeDetailComponent>;
    let mockEventManager: MockEventManager;

    const slVersion: Version = {
      ...createNewVersion(1),
      itemType: 'SL',
      language: 'en',
      number: '2.0.0',
      versionHistories: [{ version: '2.0.0' }, { version: '1.1.0' }, { version: '1.0.0' }],
    } as unknown as Version;

    const tlVersion: Version = { ...createNewVersion(2), itemType: 'TL', language: 'de', number: '2.0.1' };
    const oldTlVersion: Version = { ...createNewVersion(3), itemType: 'TL', language: 'fr', number: '1.0.1' };

    const vocabularyWith = (versions: Version[]): Vocabulary =>
      createNewVocabulary({
        id: 3,
        notation: 'AnalysisUnit',
        sourceLanguage: 'en',
        versionNumber: '2.0.0',
        versionEn: '2.0.0',
        selectedLang: 'en',
        versions,
      });

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [HomeDetailComponent],
        providers: [
          VocabularyLanguageFromKeyPipe,
          // the real service subscribes to router events, which the mock router does not emit
          { provide: RouteEventsService, useValue: { previousRoutePath: new BehaviorSubject<string>('') } },
        ],
      })
        .overrideTemplate(HomeDetailComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(HomeDetailComponent);
      comp = fixture.componentInstance;
      mockEventManager = TestBed.inject(JhiEventManager) as unknown as MockEventManager;

      comp.vocabulary = vocabularyWith([slVersion, tlVersion, oldTlVersion]);
    });

    describe('choosing a language and version', () => {
      it('should remember the choice on the vocabulary', () => {
        comp.setVocabularyLangVersion('de', '2.0.1');

        expect(comp.vocabulary.selectedLang).toBe('de');
        expect(comp.vocabulary.selectedVersion).toBe('2.0.1');
      });

      it('should close any open comparison', () => {
        comp.setVocabularyLangVersion('de', '2.0.1');

        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith({ name: 'closeComparison', content: true });
      });
    });

    describe('reading version numbers', () => {
      it('should read the latest number from the source language', () => {
        expect(comp.getLatestVersionNumber()).toBe('2.0.0');
      });

      it('should refuse a vocabulary without a source language', () => {
        comp.vocabulary = createNewVocabulary({ sourceLanguage: undefined, versions: [] });

        expect(() => comp.getLatestVersionNumber()).toThrow();
      });

      it('should pick the SL version and its number', () => {
        expect(comp.getSlVersion()).toBe(slVersion);
        expect(comp.getSlVersionNumber()).toBe('2.0.0');
        expect(comp.getSlVersionNumber('4.2.7')).toBe('4.2.7');
      });

      it('should find a version by the selected language and number', () => {
        comp.vocabulary = { ...comp.vocabulary, selectedLang: 'de' };

        expect(comp.getVersionByLangNumber('2.0.1')).toBe(tlVersion);
      });
    });

    describe('listing languages', () => {
      it('should list the languages of the current bundle before the older ones', () => {
        // fr only exists in the 1.0 bundle, so it comes after the current en and de
        expect(comp.getUniqueVersionLang()).toEqual(['en', 'de', 'fr']);
      });

      it('should list each language once', () => {
        comp.vocabulary = vocabularyWith([slVersion, tlVersion, { ...tlVersion, id: 4, number: '2.0.2' }]);

        expect(comp.getUniqueVersionLang()).toEqual(['en', 'de']);
      });

      it('should filter versions by language', () => {
        expect(comp.getVersionsByLanguage('de')).toEqual([tlVersion]);
        expect(comp.getVersionsByLanguage('sk')).toEqual([]);
      });
    });

    describe('describing a version', () => {
      it('should name the language and number', () => {
        expect(comp.getFormattedVersionTooltip(tlVersion)).toBe('German (de) v.2.0.1');
      });

      it('should mark the source language', () => {
        expect(comp.getFormattedVersionTooltip(slVersion, 'en')).toBe('English (en) v.2.0.0 SOURCE');
      });

      it('should not repeat the status the way the editor does', () => {
        // the public page shows published vocabularies only, so there is no status to show
        expect(comp.getFormattedVersionTooltip(slVersion)).toBe('English (en) v.2.0.0');
      });
    });

    describe('bundle membership', () => {
      it('should fall back to the vocabulary version number', () => {
        expect(comp.isAnyLangVersionInBundle(comp.vocabulary, 'en')).toBe(true);
        expect(comp.isAnyLangVersionInBundle(comp.vocabulary, 'de')).toBe(false);
      });

      it('should honour an explicitly given bundle', () => {
        expect(comp.isAnyLangVersionInBundle(comp.vocabulary, 'de', '2.0.1')).toBe(true);
      });

      it('should refuse a vocabulary with no version number and no bundle given', () => {
        const vocab = createNewVocabulary({ versionNumber: undefined, versions: [] });

        expect(() => comp.isAnyLangVersionInBundle(vocab, 'en')).toThrow();
      });
    });

    describe('the version a translation is missing from', () => {
      it('should point at the next source language version when asked about the current one', () => {
        expect(comp.getMissingTlVersion('2.0.0')).toBe('2.0.x');
      });

      it('should point at the following history entry for an older version', () => {
        expect(comp.getMissingTlVersion('1.1.0')).toBe('1.0.x');
      });

      it('should return nothing for a version that is not in the history', () => {
        expect(comp.getMissingTlVersion('9.9.9')).toBe('');
      });
    });

    it('should report deprecated concepts', () => {
      const concepts: Concept[] = [{ notation: 'a' }, { notation: 'b', deprecated: true }];

      expect(comp.hasDeprecatedConcepts(concepts)).toBe(true);
      expect(comp.hasDeprecatedConcepts([concepts[0]])).toBe(false);
    });

    it('should expose the server origin', () => {
      expect(comp.getServerUrl()).toBe(window.location.origin);
    });
  });
});
