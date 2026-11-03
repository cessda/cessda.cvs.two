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
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ElementRef } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { NavigationEnd, NavigationStart } from '@angular/router';
import { LanguageService } from 'app/core/language/language.service';
import { of, throwError } from 'rxjs';

import { CvsTestModule } from '../../test.module';
import { QueryParamsNavigation } from '../../helpers/query-params-navigation';
import { VocabularySearchResultComponent } from 'app/shared/vocabulary-search-result/vocabulary-search-result.component';
import { VocabularyLanguageFromKeyPipe } from 'app/shared/language/vocabulary-language-from-key.pipe';
import { createNewVocabulary, Vocabulary } from 'app/shared/model/vocabulary.model';
import { Code, createNewCode } from 'app/shared/model/code.model';
import { CvResult } from 'app/shared/model/cv-result.model';
import { AppScope } from 'app/shared/model/enumerations/app-scope.model';
import { HomeService } from 'app/home/home.service';
import { EditorService, SearchRequest } from 'app/editor/editor.service';
import { EventManager } from 'app/core/util/event-manager.service';
import { AccountService } from 'app/core/auth/account.service';

describe('Component Tests', () => {
  describe('Vocabulary Search Result Component', () => {
    let comp: VocabularySearchResultComponent;
    let fixture: ComponentFixture<VocabularySearchResultComponent>;
    let navigation: QueryParamsNavigation;

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
      navigation = new QueryParamsNavigation();
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [VocabularySearchResultComponent],
        providers: [VocabularyLanguageFromKeyPipe, ...navigation.providers()],
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

      it('should find no status for a language the vocabulary lacks', () => {
        expect(comp.isVersionContains(vocabulary(), 'sk', 'DRAFT')).toBe(false);
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
      (TestBed.inject(LanguageService) as unknown as { currentLang: string }).currentLang = 'de';

      expect(comp.getCurrentLanguage()).toBe('de');
    });

    describe('searching from the URL', () => {
      let search: jasmine.Spy;

      const result = (overrides: Partial<CvResult> = {}): CvResult => ({
        vocabularies: [],
        totalElements: 0,
        totalPage: 0,
        numberOfElements: 0,
        number: 0,
        size: 30,
        last: true,
        first: true,
        aggrs: [],
        ...overrides,
      });

      const lastRequest = (): SearchRequest => search.calls.mostRecent().args[0];
      const requestedPages = (): number[] => search.calls.allArgs().map(([request]: [SearchRequest]) => request.page);

      beforeEach(() => {
        comp.appScope = AppScope.PUBLICATION;
        search = spyOn(TestBed.inject(HomeService), 'search').and.returnValue(
          of(new HttpResponse({ body: result({ totalElements: 95 }) })),
        );
      });

      it('should ask for the first page sorted by code when the URL carries nothing', () => {
        comp.ngOnInit();

        expect(lastRequest()).toEqual({ q: '', size: 30, page: 0, sort: ['code,asc'] });
        expect(comp.page).toBe(1);
      });

      it('should ask the server for the page in the URL, counting pages from zero', () => {
        navigation.open({ page: '2' });

        comp.ngOnInit();

        expect(lastRequest().page).toBe(1);
        expect(comp.page).toBe(2);
      });

      it('should search again each time the URL changes, as when going to the second page and back', () => {
        comp.ngOnInit();

        navigation.open({ page: '2' });
        navigation.open({});

        expect(requestedPages()).toEqual([0, 1, 0]);
        expect(comp.page).toBe(1);
      });

      it('should move to a clicked page through the URL alone', () => {
        comp.ngOnInit();
        search.calls.reset();

        comp.loadPageClicked(3);

        expect(navigation.current).toEqual({ page: 3 });
        expect(requestedPages()).toEqual([2]);
        expect(comp.page).toBe(3);
      });

      it('should keep the query, the filters, the size and the order when moving to another page', () => {
        navigation.open({ q: 'unit', f: 'agency:CESSDA', size: '50', sort: 'relevance' });
        comp.ngOnInit();

        comp.loadPageClicked(2);

        expect(lastRequest()).toEqual({ q: 'unit', size: 50, page: 1, sort: ['relevance'], f: 'agency:CESSDA' });
      });

      it('should not navigate by itself while loading, so coming back to the page cannot loop', () => {
        navigation.open({ q: 'unit', page: '2' });

        comp.ngOnInit();

        expect(navigation.navigate).not.toHaveBeenCalled();
        expect(search).toHaveBeenCalledTimes(1);
      });

      it('should ask for the number of results per page chosen', () => {
        comp.ngOnInit();

        comp.refreshSearchBySize({ target: { value: '50' } } as unknown as Event);

        expect(lastRequest().size).toBe(50);
        expect(comp.itemsPerPage).toBe(50);
      });

      it('should go back to the first page when the number of results per page changes', () => {
        navigation.open({ q: 'unit', page: '4' });
        comp.ngOnInit();

        comp.refreshSearchBySize({ target: { value: '100' } } as unknown as Event);

        expect(navigation.current).toEqual({ q: 'unit', size: '100' });
        expect(lastRequest().page).toBe(0);
        expect(comp.page).toBe(1);
      });

      it('should ask for the order chosen', () => {
        comp.ngOnInit();

        comp.refreshSearchBySort({ target: { value: 'title,desc' } } as unknown as Event);

        expect(lastRequest().sort).toEqual(['title,desc']);
        expect(comp.predicate).toBe('title');
        expect(comp.ascending).toBe(false);
      });

      it('should list the most relevant results first when sorting by relevance', () => {
        navigation.open({ q: 'unit', sort: 'relevance' });

        comp.ngOnInit();

        expect(comp.predicate).toBe('relevance');
        expect(comp.ascending).toBe(false);
      });

      it('should take the active filters from the URL', () => {
        navigation.open({ f: 'agency:CESSDA,UKDS;status:PUBLISHED' });

        comp.ngOnInit();

        expect(comp.activeAggAgency).toEqual(['CESSDA', 'UKDS']);
        expect(comp.activeAggStatus).toEqual(['PUBLISHED']);
        expect(lastRequest().f).toBe('agency:CESSDA,UKDS;status:PUBLISHED');
      });

      it('should take all three kinds of filter from the URL at once', () => {
        navigation.open({ f: 'agency:CESSDA;language:de;status:PUBLISHED' });

        comp.ngOnInit();

        expect(comp.activeAggAgency).toEqual(['CESSDA']);
        expect(comp.activeAggLanguage).toEqual(['de']);
        expect(comp.activeAggStatus).toEqual(['PUBLISHED']);
      });

      it('should keep the status filter when another agency is added to all three kinds', () => {
        navigation.open({ f: 'agency:CESSDA;language:de;status:PUBLISHED' });
        comp.ngOnInit();

        comp.onAddAgency({ k: 'UKDS' });

        expect(navigation.current).toEqual({ f: 'agency:CESSDA,UKDS;language:de;status:PUBLISHED' });
      });

      it('should forget the filters once the URL no longer carries them', () => {
        navigation.open({ f: 'language:de' });
        comp.ngOnInit();

        navigation.open({});

        expect(comp.activeAggLanguage).toEqual([]);
        expect(lastRequest().f).toBeUndefined();
      });

      it('should put a filter added in the panel into the URL', () => {
        comp.ngOnInit();

        comp.onAddStatus({ k: 'PUBLISHED' });

        expect(navigation.current).toEqual({ f: 'status:PUBLISHED' });
        expect(lastRequest().f).toBe('status:PUBLISHED');
      });

      it('should go back to the first page when a filter is added', () => {
        navigation.open({ q: 'unit', page: '4' });
        comp.ngOnInit();

        comp.onAddAgency({ k: 'CESSDA' });

        expect(navigation.current).toEqual({ q: 'unit', f: 'agency:CESSDA' });
        expect(lastRequest().page).toBe(0);
      });

      it('should go back to the first page when a filter is removed', () => {
        navigation.open({ f: 'agency:CESSDA;status:PUBLISHED', page: '3' });
        comp.ngOnInit();

        comp.onRemoveStatus({ k: 'PUBLISHED' });

        expect(navigation.current).toEqual({ f: 'agency:CESSDA' });
        expect(lastRequest().page).toBe(0);
      });

      it('should go back to the first page when the filters are cleared', () => {
        navigation.open({ f: 'agency:CESSDA', page: '3' });
        comp.ngOnInit();

        comp.clearFilterAndReload();

        expect(navigation.current).toEqual({});
        expect(lastRequest().page).toBe(0);
      });

      it('should show the results and how many there are in all', () => {
        const vocabulary = createNewVocabulary({ notation: 'AnalysisUnit', sourceLanguage: 'en' });
        search.and.returnValue(of(new HttpResponse({ body: result({ totalElements: 95, vocabularies: [vocabulary] }) })));

        comp.ngOnInit();

        expect(comp.searching).toBe(false);
        expect(comp.totalItems).toBe(95);
        expect(comp.vocabularies).toEqual([{ ...vocabulary, selectedLang: 'en' }]);
      });

      it('should offer the agencies found as filters and mark the active ones', () => {
        navigation.open({ f: 'agency:CESSDA' });
        search.and.returnValue(
          of(
            new HttpResponse({
              body: result({
                aggrs: [
                  {
                    type: 'terms',
                    field: 'agencyName',
                    values: ['CESSDA'],
                    buckets: [{ k: 'CESSDA', v: 12 }],
                    filteredBuckets: [{ k: 'UKDS', v: 3 }],
                  },
                ],
              }),
            }),
          ),
        );

        comp.ngOnInit();

        expect(comp.aggAgencyBucket.map(b => b.display)).toEqual(['CESSDA (12)', 'UKDS (3)']);
        expect(comp.searchForm.value.aggAgency.map((b: { k: string }) => b.k)).toEqual(['CESSDA']);
      });

      it('should stop searching when the request fails', () => {
        spyOn(console, 'error');
        search.and.returnValue(throwError(() => new HttpErrorResponse({ status: 500 })));

        comp.ngOnInit();

        expect(comp.searching).toBe(false);
        expect(comp.vocabularies).toEqual([]);
      });

      it('should search the editor index when shown in the editor', () => {
        comp.appScope = AppScope.EDITOR;
        const editorSearch = spyOn(TestBed.inject(EditorService), 'search').and.returnValue(of(new HttpResponse({ body: result() })));

        comp.ngOnInit();

        expect(editorSearch).toHaveBeenCalled();
        expect(search).not.toHaveBeenCalled();
      });

      it('should select the source language of a result that has none selected and keep a selected one', () => {
        const unselected = createNewVocabulary({ notation: 'AnalysisUnit', sourceLanguage: 'de', selectedLang: undefined });
        const selected = createNewVocabulary({ notation: 'TimeMethod', sourceLanguage: 'en', selectedLang: 'fi' });
        search.and.returnValue(of(new HttpResponse({ body: result({ vocabularies: [unselected, selected] }) })));

        comp.ngOnInit();

        expect(comp.vocabularies.map(v => v.selectedLang)).toEqual(['de', 'fi']);
      });

      it('should tell the rest of the page when searching starts and stops', () => {
        const eventManager = TestBed.inject(EventManager) as unknown as { broadcastSpy: jasmine.Spy };

        comp.ngOnInit();

        expect(eventManager.broadcastSpy.calls.allArgs()).toEqual([
          [{ name: 'onSearching', content: true }],
          [{ name: 'onSearching', content: false }],
        ]);
      });

      it('should offer the statuses found as filters and mark the active ones, ignoring other fields', () => {
        navigation.open({ f: 'status:PUBLISHED' });
        search.and.returnValue(
          of(
            new HttpResponse({
              body: result({
                aggrs: [
                  { type: 'terms', field: 'statuses', values: ['PUBLISHED'], buckets: [{ k: 'PUBLISHED', v: 8 }], filteredBuckets: [] },
                  { type: 'terms', field: 'languages', values: [], buckets: [{ k: 'en', v: 8 }], filteredBuckets: [] },
                ],
              }),
            }),
          ),
        );

        comp.ngOnInit();

        expect(comp.aggStatusBucket).toEqual([{ k: 'PUBLISHED', v: 8, value: 'PUBLISHED', display: 'PUBLISHED (8)' }]);
        expect(comp.searchForm.value.aggStatus).toEqual(comp.aggStatusBucket);
        expect(comp.aggAgencyBucket).toEqual([]);
      });

      it('should keep the sort direction given with a relevance sort', () => {
        navigation.open({ q: 'unit', sort: 'relevance,asc' });

        comp.ngOnInit();

        expect(comp.predicate).toBe('relevance');
        expect(comp.ascending).toBe(true);
      });

      it('should ignore filters it cannot read', () => {
        navigation.open({ f: 'agency;unknown:x;language:de' });

        comp.ngOnInit();

        expect(comp.activeAggAgency).toEqual([]);
        expect(comp.activeAggLanguage).toEqual(['de']);
        expect(lastRequest().f).toBe('agency;unknown:x;language:de');
      });

      it('should keep the ascending order when the sort names no direction', () => {
        navigation.open({ sort: 'title' });

        comp.ngOnInit();

        expect(comp.predicate).toBe('title');
        expect(comp.ascending).toBe(true);
        expect(lastRequest().sort).toEqual(['title,asc']);
      });

      it('should leave the filters alone when removing one that is not active', () => {
        navigation.open({ f: 'agency:CESSDA;status:PUBLISHED' });
        comp.ngOnInit();

        comp.onRemoveStatus({ k: 'DRAFT' });

        expect(navigation.current).toEqual({ f: 'agency:CESSDA;status:PUBLISHED' });
      });

      it('should remove an agency filter', () => {
        navigation.open({ f: 'agency:CESSDA,UKDS;language:de', page: '2' });
        comp.ngOnInit();

        comp.onRemoveAgency({ k: 'CESSDA' });

        expect(navigation.current).toEqual({ f: 'agency:UKDS;language:de' });
      });

      it('should remove every occurrence of a repeated agency filter', () => {
        navigation.open({ f: 'agency:CESSDA,CESSDA,UKDS' });
        comp.ngOnInit();

        comp.onRemoveAgency({ k: 'CESSDA' });

        expect(comp.activeAggAgency).toEqual(['UKDS']);
        expect(navigation.current).toEqual({ f: 'agency:UKDS' });
      });

      it('should remove every occurrence of a repeated status filter', () => {
        navigation.open({ f: 'status:DRAFT,DRAFT,PUBLISHED' });
        comp.ngOnInit();

        comp.onRemoveStatus({ k: 'DRAFT' });

        expect(comp.activeAggStatus).toEqual(['PUBLISHED']);
        expect(navigation.current).toEqual({ f: 'status:PUBLISHED' });
      });

      it('should drop the filter parameter once the last filter is removed', () => {
        navigation.open({ f: 'agency:CESSDA' });
        comp.ngOnInit();

        comp.onRemoveAgency({ k: 'CESSDA' });

        expect(navigation.current).toEqual({});
      });

      it('should join the language and status filters', () => {
        navigation.open({ f: 'language:de' });
        comp.ngOnInit();

        comp.onAddStatus({ k: 'DRAFT' });

        expect(navigation.current).toEqual({ f: 'language:de;status:DRAFT' });
      });

      describe('in the editor, for a user who is not an administrator', () => {
        let accountService: Record<string, unknown>;

        beforeEach(() => {
          comp.appScope = AppScope.EDITOR;
          spyOn(TestBed.inject(EditorService), 'search').and.returnValue(of(new HttpResponse({ body: result() })));
          accountService = TestBed.inject(AccountService) as unknown as Record<string, unknown>;
          accountService.isAdmin = (): boolean => false;
          accountService.getUserAgencies = (): string[] => ['CESSDA'];
        });

        it('should count as a language administrator', () => {
          expect(comp.isLanguageAdmin()).toBe(true);
        });

        it('should always filter by the agencies of the user', () => {
          comp.ngOnInit();

          comp.onAddStatus({ k: 'DRAFT' });

          expect(navigation.current).toEqual({ f: 'agency:CESSDA;status:DRAFT' });
        });

        it('should filter by the agencies of the user and the language picked', () => {
          navigation.open({ f: 'language:de' });
          comp.ngOnInit();

          comp.onAddStatus({ k: 'DRAFT' });

          expect(navigation.current).toEqual({ f: 'agency:CESSDA;language:de;status:DRAFT' });
        });

        it('should filter by every agency offered when the user belongs to none', () => {
          accountService.getUserAgencies = (): string[] => [];
          comp.aggAgencyBucket = [
            { k: 'CESSDA', v: 1, value: 'CESSDA' },
            { k: 'UKDS', v: 1, value: 'UKDS' },
          ];
          comp.ngOnInit();

          comp.clearFilterAndReload();

          expect(navigation.current).toEqual({ f: 'agency:CESSDA,UKDS' });
        });

        it('should filter by the agencies of the user alone without a trailing separator', () => {
          comp.ngOnInit();

          comp.clearFilterAndReload();

          expect(navigation.current).toEqual({ f: 'agency:CESSDA' });
        });

        it('should not filter by agency when neither the user nor the results have one', () => {
          accountService.getUserAgencies = (): string[] => [];
          comp.ngOnInit();

          comp.clearFilterAndReload();

          expect(navigation.current).toEqual({});
        });

        it('should not count an administrator as a language administrator', () => {
          accountService.isAdmin = (): boolean => true;

          expect(comp.isLanguageAdmin()).toBe(false);
        });
      });

      it('should scroll to the top once a navigation ends, and only then', () => {
        const scrollTo = jest.spyOn(window, 'scrollTo').mockImplementation(() => undefined);
        comp.ngOnInit();

        navigation.events.next(new NavigationStart(1, '/?page=2'));
        expect(scrollTo).not.toHaveBeenCalled();

        navigation.events.next(new NavigationEnd(1, '/?page=2', '/?page=2'));
        expect(scrollTo).toHaveBeenCalledWith(0, 0);
        scrollTo.mockRestore();
      });
    });

    describe('the filter panel', () => {
      let panel: HTMLDivElement;

      beforeEach(() => {
        panel = document.createElement('div');
        comp.filterPanels = new ElementRef(panel);
      });

      it('should show a panel that has not been toggled yet', () => {
        comp.toggleFilterPanelHidden();

        expect(panel.style.display).toBe('block');
        expect(comp.isFilterCollapse).toBe(false);
      });

      it('should hide a shown panel and show it again', () => {
        panel.style.display = 'block';

        comp.toggleFilterPanelHidden();
        expect(panel.style.display).toBe('none');
        expect(comp.isFilterCollapse).toBe(true);

        comp.toggleFilterPanelHidden();
        expect(panel.style.display).toBe('block');
        expect(comp.isFilterCollapse).toBe(false);
      });
    });

    it('should open the login dialog', () => {
      const loginModalService = { open: jest.fn() };
      (comp as unknown as { loginModalService: typeof loginModalService }).loginModalService = loginModalService;

      comp.login();

      expect(loginModalService.open).toHaveBeenCalled();
    });
  });
});
