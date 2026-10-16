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
import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import { ElementRef } from '@angular/core';
import { ActivatedRoute, NavigationEnd, NavigationStart, Router, RouterEvent } from '@angular/router';
import { EventManager } from 'app/core/util/event-manager.service';
import { BehaviorSubject, Subject } from 'rxjs';

import { CvsTestModule } from '../../test.module';
import { MockEventManager } from '../../helpers/mock-event-manager.service';
import { MockRouter } from '../../helpers/mock-route.service';
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
    let mockRouter: MockRouter;
    let queryParams: BehaviorSubject<Record<string, string>>;
    let routeData: Subject<{ vocabulary: Vocabulary }>;
    let previousRoutePath: BehaviorSubject<string>;

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
      queryParams = new BehaviorSubject<Record<string, string>>({});
      routeData = new Subject<{ vocabulary: Vocabulary }>();
      previousRoutePath = new BehaviorSubject<string>('');
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [HomeDetailComponent],
        providers: [
          VocabularyLanguageFromKeyPipe,
          // the real service subscribes to router events, which the mock router does not emit
          { provide: RouteEventsService, useValue: { previousRoutePath } },
          { provide: ActivatedRoute, useValue: { queryParams, data: routeData } },
        ],
      })
        .overrideTemplate(HomeDetailComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(HomeDetailComponent);
      comp = fixture.componentInstance;
      mockEventManager = TestBed.inject(EventManager) as unknown as MockEventManager;
      mockRouter = TestBed.inject(Router) as unknown as MockRouter;

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

      it('should still list a language whose version has no number yet', () => {
        const unnumbered: Version = { ...createNewVersion(5), itemType: 'SL', language: 'en', number: undefined };
        comp.vocabulary = vocabularyWith([unnumbered, tlVersion]);

        expect(comp.getUniqueVersionLang()).toEqual(['de', 'en']);
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

    describe('reading the query parameters', () => {
      it('should open the detail tab with nothing selected by default', () => {
        expect(comp.initialTabSelected).toBe('detail');
        expect(comp.initialLangSelect).toBeUndefined();
        expect(comp.currentSelectedCode).toBe('');
        expect(comp.enableDocxExport).toBe(false);
      });

      it('should take the language and tab', () => {
        queryParams.next({ lang: 'de', tab: 'version' });

        expect(comp.initialLangSelect).toBe('de');
        expect(comp.initialTabSelected).toBe('version');
      });

      it('should jump to a code on the detail tab whatever tab was asked for', () => {
        queryParams.next({ tab: 'version', code: 'Individual' });

        expect(comp.initialTabSelected).toBe('detail');
        expect(comp.currentSelectedCode).toBe('Individual');
      });

      it('should open the export tab and enable the DOCX export when asked to', () => {
        queryParams.next({ 'docx-export': 'true' });

        expect(comp.initialTabSelected).toBe('export');
        expect(comp.enableDocxExport).toBe(true);
      });

      it('should open the export tab but keep the DOCX export off for any other value', () => {
        queryParams.next({ 'docx-export': 'false' });

        expect(comp.initialTabSelected).toBe('export');
        expect(comp.enableDocxExport).toBe(false);
      });
    });

    describe('loading the vocabulary', () => {
      let routerEvents: Subject<RouterEvent>;

      const freshVocabulary = (overrides: Partial<Vocabulary> = {}): Vocabulary => ({
        ...vocabularyWith([{ ...slVersion }, { ...tlVersion }, { ...oldTlVersion }]),
        status: 'PUBLISHED',
        ...overrides,
      });

      const load = (vocabulary: Vocabulary): void => {
        comp.ngOnInit();
        routeData.next({ vocabulary });
      };

      beforeEach(() => {
        routerEvents = new Subject<RouterEvent>();
        mockRouter.setEvents(routerEvents);
      });

      afterEach(() => {
        jest.restoreAllMocks();
      });

      it('should show the vocabulary from the route on the initially selected tab', () => {
        queryParams.next({ tab: 'license' });
        const vocabulary = freshVocabulary();

        load(vocabulary);

        expect(comp.vocabulary).toBe(vocabulary);
        expect(comp.detailForm.get('tabSelected')!.value).toBe('license');
      });

      it('should select the version of the selected language', () => {
        load(freshVocabulary({ selectedLang: 'de' }));

        expect(comp.vocabulary.selectedVersion).toBe('2.0.1');
      });

      it('should keep a version that is already selected', () => {
        load(freshVocabulary({ selectedVersion: '1.0.1' }));

        expect(comp.vocabulary.selectedVersion).toBe('1.0.1');
      });

      it('should not select a version without a selected language', () => {
        load(freshVocabulary({ selectedLang: undefined }));

        expect(comp.vocabulary.selectedVersion).toBeUndefined();
      });

      it('should select the language asked for in the url', () => {
        queryParams.next({ lang: 'fr' });

        load(freshVocabulary());

        expect(comp.vocabulary.selectedLang).toBe('fr');
        expect(comp.vocabulary.selectedVersion).toBe('1.0.1');
      });

      it('should fall back to the source language when the vocabulary lacks the language asked for', () => {
        queryParams.next({ lang: 'sk' });

        load(freshVocabulary({ selectedLang: undefined }));

        expect(comp.vocabulary.selectedLang).toBe('en');
        expect(comp.vocabulary.selectedVersion).toBe('2.0.0');
      });

      it('should keep the language asked for when there is no source language to fall back to', () => {
        queryParams.next({ lang: 'sk' });

        load(freshVocabulary({ sourceLanguage: undefined, selectedVersion: '2.0.0' }));

        expect(comp.vocabulary.selectedLang).toBe('sk');
      });

      describe('pointing at a newer version', () => {
        it('should not point anywhere when the published version is the one shown', () => {
          load(freshVocabulary());

          expect(comp.newerVersionNumber).toBeNull();
        });

        it('should point at a newer published version', () => {
          load(freshVocabulary({ versionNumber: '3.0.0' }));

          expect(comp.newerVersionNumber).toBe('3.0.0');
        });

        it('should take the latest source language version of an unpublished vocabulary', () => {
          load(freshVocabulary({ status: 'DRAFT', versionEn: '2.1.0' }));

          expect(comp.newerVersionNumber).toBe('2.1.0');
        });

        it('should not point anywhere when the unpublished vocabulary is not ahead', () => {
          load(freshVocabulary({ status: 'DRAFT' }));

          expect(comp.newerVersionNumber).toBeNull();
        });

        it('should refuse a published vocabulary without a version number', fakeAsync(() => {
          load(freshVocabulary({ versionNumber: undefined }));

          // the error is thrown inside the subscription and reported asynchronously by rxjs
          expect(() => tick()).toThrow('this.vocabulary.versionNumber is not a string');
        }));
      });

      describe('jumping to a code', () => {
        let element: HTMLElement;
        let scrollIntoView: jest.Mock;

        beforeEach(() => {
          element = document.createElement('div');
          element.id = 'code_Individual';
          scrollIntoView = jest.fn();
          element.scrollIntoView = scrollIntoView;
          document.body.appendChild(element);
        });

        afterEach(() => {
          element.remove();
        });

        it('should take the code selected on the vocabulary', fakeAsync(() => {
          load(freshVocabulary({ selectedCode: 'Individual' }));

          expect(comp.currentSelectedCode).toBe('Individual');
          tick(6500);
        }));

        it('should scroll to the code and highlight it for five seconds', fakeAsync(() => {
          queryParams.next({ code: 'Individual' });
          load(freshVocabulary());

          tick(1499);
          expect(scrollIntoView).not.toHaveBeenCalled();

          tick(1);
          expect(scrollIntoView).toHaveBeenCalledWith({ behavior: 'smooth' });
          expect(element.classList.contains('highlight')).toBe(true);

          tick(5000);
          expect(element.classList.contains('highlight')).toBe(false);
        }));

        it('should show deprecated codes when the selected language has some', fakeAsync(() => {
          queryParams.next({ code: 'Individual' });
          const deprecatedSl = { ...slVersion, concepts: [{ notation: 'Old', deprecated: true }] };

          load(freshVocabulary({ versions: [deprecatedSl, { ...tlVersion }] }));

          expect(comp.isShowingDeprecatedCodes).toBe(true);
          tick(6500);
        }));

        it('should hide deprecated codes when the selected language has none', fakeAsync(() => {
          queryParams.next({ code: 'Individual' });
          const deprecatedTl = { ...tlVersion, concepts: [{ notation: 'Old', deprecated: true }] };

          load(freshVocabulary({ versions: [{ ...slVersion }, deprecatedTl] }));

          expect(comp.isShowingDeprecatedCodes).toBe(false);
          tick(6500);
        }));

        it('should fail when the code is not on the page', fakeAsync(() => {
          queryParams.next({ code: 'Missing' });
          load(freshVocabulary());

          expect(() => tick(1500)).toThrow('element #code_Missing is null');
          expect(scrollIntoView).not.toHaveBeenCalled();
        }));

        it('should not scroll anywhere without a selected code', fakeAsync(() => {
          load(freshVocabulary());
          tick(6500);

          expect(scrollIntoView).not.toHaveBeenCalled();
          expect(comp.isShowingDeprecatedCodes).toBe(false);
        }));
      });

      it('should scroll to the top once a navigation has ended', () => {
        const scrollTo = jest.spyOn(window, 'scrollTo').mockImplementation(() => undefined);
        load(freshVocabulary());

        routerEvents.next(new NavigationStart(1, '/vocabulary/AnalysisUnit'));
        expect(scrollTo).not.toHaveBeenCalled();

        routerEvents.next(new NavigationEnd(1, '/vocabulary/AnalysisUnit', '/vocabulary/AnalysisUnit'));
        expect(scrollTo).toHaveBeenCalledWith(0, 0);
      });
    });

    describe('collapsible panels', () => {
      type PanelKey = 'detailPanel' | 'versionPanel' | 'identityPanel' | 'usagePanel' | 'licensePanel' | 'exportPanel';
      type FlagKey =
        'isDetailCollapse' | 'isVersionCollapse' | 'isIdentityCollapse' | 'isUsageCollapse' | 'isLicenseCollapse' | 'isExportCollapse';

      const panels: [string, PanelKey, FlagKey][] = [
        ['toggleDetailPanel', 'detailPanel', 'isDetailCollapse'],
        ['toggleVersionPanel', 'versionPanel', 'isVersionCollapse'],
        ['toggleIdentityPanel', 'identityPanel', 'isIdentityCollapse'],
        ['toggleUsagePanel', 'usagePanel', 'isUsageCollapse'],
        ['toggleLicensePanel', 'licensePanel', 'isLicenseCollapse'],
        ['toggleExportPanel', 'exportPanel', 'isExportCollapse'],
      ];

      const toggle = (method: string): void => (comp as unknown as Record<string, () => void>)[method]();

      describe.each(panels)('%s', (method, panel, flag) => {
        let nativeElement: HTMLElement;

        beforeEach(() => {
          nativeElement = document.createElement('div');
          comp[panel] = new ElementRef(nativeElement);
        });

        it('should expand a panel that has no display set yet', () => {
          toggle(method);

          expect(nativeElement.style.display).toBe('block');
          expect(comp[flag]).toBe(false);
        });

        it('should expand a hidden panel', () => {
          nativeElement.style.display = 'none';

          toggle(method);

          expect(nativeElement.style.display).toBe('block');
          expect(comp[flag]).toBe(false);
        });

        it('should collapse an expanded panel', () => {
          toggle(method);
          toggle(method);

          expect(nativeElement.style.display).toBe('none');
          expect(comp[flag]).toBe(true);
        });
      });
    });

    it('should toggle showing deprecated codes', () => {
      comp.toggleDeprecatedCodesDisplay();
      expect(comp.isShowingDeprecatedCodes).toBe(true);

      comp.toggleDeprecatedCodesDisplay();
      expect(comp.isShowingDeprecatedCodes).toBe(false);
    });

    it('should go back in the browser history', () => {
      const back = jest.spyOn(window.history, 'back').mockImplementation(() => undefined);

      comp.previousState();

      expect(back).toHaveBeenCalled();
      back.mockRestore();
    });

    describe('returning to the search', () => {
      it.each(['/', '/?q=gender&page=2'])('should restore the previous search %p', previous => {
        previousRoutePath.next(previous);

        comp.goToCvSearch();

        expect(mockRouter.navigateByUrlSpy).toHaveBeenCalledWith(previous);
        expect(mockRouter.navigateSpy).not.toHaveBeenCalled();
      });

      it.each(['', '/vocabulary/AnalysisUnit', '/editor'])('should open a fresh search when coming from %p', previous => {
        previousRoutePath.next(previous);

        comp.goToCvSearch();

        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['']);
        expect(mockRouter.navigateByUrlSpy).not.toHaveBeenCalled();
      });
    });
  });
});
