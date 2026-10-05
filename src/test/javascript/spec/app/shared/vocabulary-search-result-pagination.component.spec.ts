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
import { Component, forwardRef, Input, NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { ControlValueAccessor, NG_VALUE_ACCESSOR, ReactiveFormsModule } from '@angular/forms';
import { NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';
import { of, Subject } from 'rxjs';

import { CvsTestModule } from '../../test.module';
import { QueryParamsNavigation } from '../../helpers/query-params-navigation';
import { VocabularySearchResultComponent } from 'app/shared/vocabulary-search-result/vocabulary-search-result.component';
import { VocabularyLanguageFromKeyPipe } from 'app/shared/language/vocabulary-language-from-key.pipe';
import { createNewVocabulary } from 'app/shared/model/vocabulary.model';
import { CvResult } from 'app/shared/model/cv-result.model';
import { AppScope } from 'app/shared/model/enumerations/app-scope.model';
import { HomeService } from 'app/home/home.service';

/** Takes the place of the ngx-chips filter input, which the form needs a value accessor for */
@Component({
  // eslint-disable-next-line @angular-eslint/component-selector -- has to match the element the template uses
  selector: 'tag-input',
  template: '',
  providers: [{ provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => TagInputStubComponent), multi: true }],
})
class TagInputStubComponent implements ControlValueAccessor {
  // Angular refuses to bind an unknown property whose name starts with "on"
  @Input() onlyFromAutocomplete = false;

  writeValue(): void {}
  registerOnChange(): void {}
  registerOnTouched(): void {}
}

/*
 * Renders the real template, so that the pagination control itself takes part (#669, #786, #809,
 * #816). Directives the page does not need for paging, such as the translations, are left unknown.
 */
describe('Component Tests', () => {
  describe('Vocabulary Search Result Pagination', () => {
    let fixture: ComponentFixture<VocabularySearchResultComponent>;
    let navigation: QueryParamsNavigation;
    let search: jasmine.Spy;

    const results = (totalElements: number): HttpResponse<CvResult> =>
      new HttpResponse({
        body: {
          vocabularies: totalElements
            ? [
                createNewVocabulary({
                  notation: 'AnalysisUnit',
                  versionNumber: '2.0.0',
                  sourceLanguage: 'en',
                  titleEn: 'Analysis Unit',
                  definitionEn: 'The unit of analysis',
                  versionEn: '2.0.0',
                  languagesPublished: ['en'],
                }),
              ]
            : [],
          totalElements,
          totalPage: Math.ceil(totalElements / 30),
          numberOfElements: totalElements ? 1 : 0,
          number: 0,
          size: 30,
          last: false,
          first: true,
          aggrs: [],
        },
      });

    // the pagination reports a page change asynchronously, so a change it makes on its own only
    // shows once the fixture is stable
    const settle = async (): Promise<void> => {
      fixture.detectChanges();
      await fixture.whenStable();
      fixture.detectChanges();
    };

    const element = (): HTMLElement => fixture.nativeElement;
    const activePage = (): string | undefined => element().querySelector('ngb-pagination .page-item.active')?.textContent?.trim();
    const pageLink = (label: string): HTMLElement =>
      Array.from(element().querySelectorAll<HTMLElement>('ngb-pagination a.page-link')).find(a => a.textContent?.trim() === label)!;

    beforeEach(waitForAsync(() => {
      navigation = new QueryParamsNavigation();
      TestBed.configureTestingModule({
        imports: [CvsTestModule, ReactiveFormsModule, NgbPaginationModule, TagInputStubComponent],
        declarations: [VocabularySearchResultComponent, VocabularyLanguageFromKeyPipe],
        providers: [VocabularyLanguageFromKeyPipe, ...navigation.providers()],
        schemas: [NO_ERRORS_SCHEMA],
      }).compileComponents();
    }));

    beforeEach(() => {
      search = spyOn(TestBed.inject(HomeService), 'search').and.returnValue(of(results(95)));
      fixture = TestBed.createComponent(VocabularySearchResultComponent);
      fixture.componentInstance.appScope = AppScope.PUBLICATION;
    });

    it('should stay on the page in the URL while the results are still loading', async () => {
      const pending = new Subject<HttpResponse<CvResult>>();
      search.and.returnValue(pending);
      navigation.open({ q: 'unit', page: '2' });

      await settle();
      pending.next(results(95));
      await settle();

      expect(navigation.navigate).not.toHaveBeenCalled();
      expect(navigation.current).toEqual({ q: 'unit', page: '2' });
      expect(activePage()).toContain('2');
    });

    it('should go to the page clicked in the pagination', async () => {
      navigation.open({ q: 'unit' });
      await settle();

      pageLink('3').click();
      await settle();

      expect(navigation.current).toEqual({ q: 'unit', page: 3 });
      expect(search.calls.mostRecent().args[0].page).toBe(2);
      expect(activePage()).toContain('3');
    });

    it('should reach the second page and come back to the first', async () => {
      await settle();

      pageLink('2').click();
      await settle();
      pageLink('1').click();
      await settle();

      expect(search.calls.allArgs().map(([request]) => request.page)).toEqual([0, 1, 0]);
      expect(activePage()).toContain('1');
    });

    it('should show the first page of a new search started from another page', async () => {
      navigation.open({ q: 'unit', page: '3' });
      await settle();

      // a search from the navbar replaces the query and drops the page
      navigation.open({ q: 'household', sort: 'relevance' });
      await settle();

      expect(search.calls.mostRecent().args[0].page).toBe(0);
      expect(activePage()).toContain('1');
    });

    it('should leave the pagination out and say so when nothing is found', async () => {
      search.and.returnValue(of(results(0)));

      await settle();

      expect(element().querySelector('ngb-pagination')).toBeNull();
      expect(element().textContent).toContain('No vocabularies found');
    });

    it('should ask for the number of results per page picked', async () => {
      await settle();

      const size = element().querySelector<HTMLSelectElement>('#field_size')!;
      size.value = '50';
      size.dispatchEvent(new Event('change'));
      await settle();

      expect(navigation.current).toEqual({ size: '50' });
      expect(search.calls.mostRecent().args[0].size).toBe(50);
    });
  });
});
