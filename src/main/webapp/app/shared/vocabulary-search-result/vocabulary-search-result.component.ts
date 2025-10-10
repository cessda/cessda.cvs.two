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
import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { EditorService } from 'app/editor/editor.service';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { AppScope } from 'app/shared/model/enumerations/app-scope.model';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import VocabularyUtil from 'app/shared/util/vocabulary-util';
import { Account } from 'app/core/user/account.model';
import { Vocabulary } from 'app/shared/model/vocabulary.model';
import { Observable } from 'rxjs';
import { AGGR_AGENCY, AGGR_STATUS } from 'app/shared/constants/aggregration.constants';
import { ITEMS_PER_PAGE, PAGING_SIZE } from 'app/shared/constants/pagination.constants';
import { Bucket } from 'app/shared/model/bucket';
import { AccountService } from 'app/core/auth/account.service';
import { LoginModalService } from 'app/core/login/login-modal.service';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { Code } from 'app/shared/model/code.model';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { CvResult } from 'app/shared/model/cv-result.model';
import { Aggr } from 'app/shared/model/aggr';
import { HomeService } from 'app/home/home.service';
import { VocabularyLanguageFromKeyPipe } from '../language/vocabulary-language-from-key.pipe';
import { TagModel, TagModelClass } from 'node_modules/ngx-chips/core/tag-model';

const INITIAL_PAGE = 1;
const DEFAULT_PREDICATE = 'code';

@Component({
  selector: 'jhi-vocabulary-search-result',
  templateUrl: './vocabulary-search-result.component.html',
  standalone: false,
})
export class VocabularySearchResultComponent implements OnInit {
  @Input() appScope!: AppScope;
  @ViewChild('filterPanels', { static: true }) filterPanels!: ElementRef;

  account: Account | null = null;

  vocabularies: Vocabulary[] = [];
  searching = true;

  currentSearch = '';

  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  pagingSize = PAGING_SIZE;
  page = INITIAL_PAGE;
  predicate = DEFAULT_PREDICATE;
  ascending = true;

  aggAgencyBucket: Bucket[] = [];
  aggStatusBucket: Bucket[] = [];
  activeAggAgency: string[] = [];
  activeAggLanguage: string[] = [];
  activeAggStatus: string[] = [];
  activeAgg = '';

  isAggAgencyCollapsed = false;
  isAggStatusCollapsed = false;
  isFilterCollapse = false;
  isActionCollapse = false;

  searchForm: UntypedFormGroup;

  constructor(
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    private languageService: JhiLanguageService,
    private homeService: HomeService,
    private editorService: EditorService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private eventManager: JhiEventManager,
    private fb: UntypedFormBuilder,
    private vocabLangPipeKey: VocabularyLanguageFromKeyPipe,
  ) {
    this.searchForm = this.fb.group({
      aggAgency: [],
      aggStatus: [],
      size: [this.itemsPerPage],
      sortBy: ['code,asc'],
    });

    // #352:
    // this.isAggAgencyCollapsed = this.activeAggAgency.length === 0;
    // this.isAggStatusCollapsed = this.activeAggStatus.length === 0;
  }

  getBaseUrl(): string {
    return this.isEditorScope() ? '/editor/vocabulary' : '/vocabulary';
  }

  isEditorScope(): boolean {
    return this.appScope === AppScope.EDITOR;
  }

  toggleFilterPanelHidden(): void {
    if (this.filterPanels.nativeElement.style.display === 'none' || this.filterPanels.nativeElement.style.display === '') {
      this.isFilterCollapse = false;
      this.filterPanels.nativeElement.style.display = 'block';
    } else {
      this.isFilterCollapse = true;
      this.filterPanels.nativeElement.style.display = 'none';
    }
  }

  getCurrentLanguage(): string {
    return this.languageService.currentLang;
  }

  isVersionContains(vocab: Vocabulary, lang: string, versionType: string): boolean {
    return (VocabularyUtil.getTitleDefByLangIso(vocab, lang)[2] || '').includes(versionType);
  }

  isLangVersionInBundle(vocab: Vocabulary, lang: string, bundle?: string): boolean {
    if (bundle === undefined) {
      bundle = vocab.versionNumber;
    }
    return bundle === VocabularyUtil.getVersionNumberByLangIso(vocab, lang);
  }

  getTitleByLang(vocab: Vocabulary) {
    return VocabularyUtil.getTitleDefByLangIso(vocab, vocab.selectedLang)[0];
  }

  getDefinitionByLang(vocab: Vocabulary) {
    return VocabularyUtil.getTitleDefByLangIso(vocab, vocab.selectedLang)[1];
  }

  getCodeTitleByLang(code: Code, selectedLang: string): string {
    return VocabularyUtil.getTitleDefByLangIso(code, selectedLang)[0] + (code.deprecated ? ' (DEPRECATED TERM)' : '');
  }

  getCodeDefinitionByLang(code: Code, selectedLang: string): string | undefined {
    return VocabularyUtil.getTitleDefByLangIso(code, selectedLang)[1];
  }

  getVersionByLang(vocab: Vocabulary) {
    return VocabularyUtil.getTitleDefByLangIso(vocab, vocab.selectedLang)[2];
  }

  private onSuccess(data: CvResult): void {
    this.searching = false;
    this.totalItems = data.totalElements;
    this.vocabularies = data.vocabularies;
    // assign selectedLang if still null
    this.vocabularies.forEach(v => {
      if (!v.selectedLang) {
        v.selectedLang = v.sourceLanguage;
      }
    });
    this.updateForm(data.aggrs);
    this.eventManager.broadcast({ name: 'onSearching', content: false });
  }

  private onError(e: HttpErrorResponse): void {
    this.searching = false;
    console.error(e);
    this.eventManager.broadcast({ name: 'onSearching', content: false });
  }

  search(query: string, pred?: string): void {
    if (query) {
      this.predicate = 'relevance';
    } else {
      this.ascending = true;
      this.predicate = 'code';
    }
    this.clearFilter();
    if (pred) {
      this.activeAggLanguage.push(pred);
    }
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: {
        page: null,
        q: query === '' ? null : query,
        sort: VocabularySearchResultComponent.sort(this.predicate, this.ascending),
      },
      queryParamsHandling: 'merge',
    });
  }

  isLanguageAdmin(): boolean {
    return this.appScope === AppScope.EDITOR && !this.accountService.isAdmin();
  }

  private filterAdminAgencies(): string {
    this.activeAggAgency = [];

    let adminAgencies: string[] = [];
    adminAgencies = this.accountService.getUserAgencies();
    adminAgencies.forEach(agency => {
      this.activeAggAgency.push(agency);
    });

    if (adminAgencies.length < 1) {
      this.aggAgencyBucket.forEach(agency => {
        this.activeAggAgency.push(agency.value!);
      });
    }

    if (this.activeAggAgency.length > 0) {
      return 'agency:' + this.activeAggAgency.join(',') + ';';
    } else {
      return '';
    }
  }

  clearFilter(): void {
    this.activeAggAgency = [];
    this.activeAggLanguage = [];
    this.activeAggStatus = [];
    this.searchForm.patchValue({ aggAgency: [] });
    this.searchForm.patchValue({ aggStatus: [] });
  }

  clearFilterAndReload(): void {
    this.clearFilter();
    this.buildFilterAndRefreshSearch();
  }

  trackNotation(_index: number, item: Vocabulary | Code): string {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    return item.notation!;
  }

  private static sort(predicate: string, ascending: boolean): string[] {
    if (predicate === 'relevance') {
      return [predicate];
    } else {
      return [predicate + ',' + (ascending ? 'asc' : 'desc')];
    }
  }

  ngOnInit(): void {
    this.activatedRoute.queryParamMap.subscribe(params => {
      const searchRequest: Record<string, string | string[] | number> = {};

      const query = params.get('q');
      if (query) {
        this.currentSearch = query;
      } else {
        this.currentSearch = '';
      }

      searchRequest['q'] = this.currentSearch;

      const size = params.get('size');
      if (size) {
        this.itemsPerPage = Number.parseInt(size);
      } else {
        this.itemsPerPage = ITEMS_PER_PAGE;
      }

      searchRequest['size'] = this.itemsPerPage;

      const page = params.get('page');
      if (page) {
        this.page = Number.parseInt(page);
      } else {
        this.page = INITIAL_PAGE;
      }

      // Pages requested from the server are zero-indexed
      searchRequest['page'] = this.page - 1;

      const sort = params.get('sort');
      if (sort) {
        const sortProp = sort.split(',');
        this.predicate = sortProp[0];
        if (sortProp.length === 2) {
          this.ascending = sortProp[1] === 'asc';
        } else if (this.predicate === 'relevance') {
          // Relevance search defaults to descending order
          this.ascending = false;
        }
      } else {
        this.predicate = DEFAULT_PREDICATE;
        this.ascending = true;
      }

      searchRequest['sort'] = VocabularySearchResultComponent.sort(this.predicate, this.ascending);

      const filters = params.get('f');
      if (filters) {
        const activeFilters = filters.split(';', 2);
        activeFilters.forEach(af => {
          const activeFilter = af.split(':', 2);
          if (activeFilter.length === 2) {
            if (activeFilter[0] === 'agency') {
              this.activeAggAgency = activeFilter[1].split(',');
            } else if (activeFilter[0] === 'language') {
              this.activeAggLanguage = activeFilter[1].split(',');
            } else if (activeFilter[0] === 'status') {
              this.activeAggStatus = activeFilter[1].split(',');
            }
          }
        });
        searchRequest['f'] = filters;
      } else {
        this.activeAggAgency = [];
        this.activeAggLanguage = [];
        this.activeAggStatus = [];
      }

      this.eventManager.broadcast({ name: 'onSearching', content: true });

      // Send the search request to the server
      let searchObservable: Observable<HttpResponse<CvResult>>;
      if (this.appScope === AppScope.EDITOR) {
        searchObservable = this.editorService.search(searchRequest);
      } else {
        searchObservable = this.homeService.search(searchRequest);
      }

      // Subscribe to the result of the search request
      searchObservable.subscribe({
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        next: (res: HttpResponse<CvResult>) => this.onSuccess(res.body!),
        error: (e: HttpErrorResponse) => this.onError(e),
      });
    });

    this.router.events.subscribe(evt => {
      if (!(evt instanceof NavigationEnd)) {
        return;
      }
      window.scrollTo(0, 0);
    });
  }

  login(): void {
    this.loginModalService.open();
  }

  private updateForm(aggrs: Aggr[]): void {
    // patch value for sort and size
    this.searchForm.patchValue({
      size: this.itemsPerPage,
      sortBy: VocabularySearchResultComponent.sort(this.predicate, this.ascending),
    });
    // patch value for filter
    aggrs.forEach(aggr => {
      if (aggr.field === AGGR_AGENCY) {
        // format bucket and add as autocomplete and patch form value
        this.aggAgencyBucket = this.formatBuckets(aggr.buckets.concat(aggr.filteredBuckets));
        this.searchForm.patchValue({ aggAgency: this.prepareActiveBuckets(this.aggAgencyBucket, aggr) });
      } else if (aggr.field === AGGR_STATUS) {
        this.aggStatusBucket = this.formatBuckets(aggr.buckets.concat(aggr.filteredBuckets));
        this.searchForm.patchValue({ aggStatus: this.prepareActiveBuckets(this.aggStatusBucket, aggr) });
      }
    });
  }

  private prepareActiveBuckets(buckets: Bucket[], aggr: Aggr): Bucket[] {
    const activeBucket: Bucket[] = [];
    aggr.values.forEach(activeVal => {
      activeBucket.push(buckets.find(b => b.k === activeVal)!);
    });
    return activeBucket;
  }

  private formatBuckets(buckets: Bucket[]): Bucket[] {
    buckets.forEach(bucket => {
      bucket.value = bucket.k;
      bucket.display = bucket.k + ' (' + bucket.v + ')';
    });
    return buckets;
  }

  sortLangByEnum(languages: string[] | undefined, sourceLang: string | undefined): string[] {
    return VocabularyUtil.sortLangByEnum(languages || [], sourceLang || '');
  }

  getFormattedLangIso(vocab: Vocabulary, lang: string, sourceLang: string): string {
    const statusInfo = VocabularyUtil.getTitleDefByLangIso(vocab, lang)[2];
    if (!statusInfo) {
      throw new TypeError(`Vocabulary ${vocab.notation} has no title for language ${lang}`);
    }
    const indexOf = statusInfo.indexOf('_');
    const langVersion = VocabularyUtil.getVersionNumberByLangIso(vocab, lang);
    return (
      this.vocabLangPipeKey.transform(lang) +
      ' ' +
      langVersion +
      (lang === sourceLang ? ' SOURCE' : '') +
      (indexOf > 0 ? ' (' + statusInfo.substr(indexOf + 1) + ')' : '')
    );
  }

  loadPageClicked(pageNo: number): void {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: { page: pageNo },
      queryParamsHandling: 'merge',
    });
  }

  refreshSearchBySize(event: Event): void {
    const s = (event.target as HTMLSelectElement).value;
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: { size: s },
      queryParamsHandling: 'merge',
    });
  }

  refreshSearchBySort(event: Event): void {
    const s = (event.target as HTMLSelectElement).value;
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: { sort: s },
      queryParamsHandling: 'merge',
    });
  }

  onAddAgency(addedItem: TagModel): void {
    this.activeAggAgency.push((addedItem as TagModelClass)['k']);
    this.buildFilterAndRefreshSearch();
  }

  onRemoveAgency(removedItem: TagModel): void {
    this.activeAggAgency.forEach((item, index) => {
      if (item === (removedItem as TagModelClass)['k']) {
        this.activeAggAgency.splice(index, 1);
      }
    });
    this.buildFilterAndRefreshSearch();
  }

  onAddStatus(addedItem: TagModel): void {
    this.activeAggStatus.push((addedItem as TagModelClass)['k']);
    this.buildFilterAndRefreshSearch();
  }

  onRemoveStatus(removedItem: TagModel): void {
    this.activeAggStatus.forEach((item, index) => {
      if (item === (removedItem as TagModelClass)['k']) {
        this.activeAggStatus.splice(index, 1);
      }
    });
    this.buildFilterAndRefreshSearch();
  }

  buildFilterAndRefreshSearch(): void {
    let activeAgg = '';
    if (this.isLanguageAdmin()) {
      activeAgg += this.filterAdminAgencies();
    }

    if (this.activeAggAgency.length > 0 && !this.isLanguageAdmin()) {
      activeAgg = 'agency:' + this.activeAggAgency.join(',');
    }
    if (this.activeAggLanguage.length > 0) {
      if (this.activeAggAgency.length > 0 && !this.isLanguageAdmin()) {
        activeAgg += ';';
      }
      activeAgg += 'language:' + this.activeAggLanguage.join(',');
    }
    if (this.activeAggStatus.length > 0) {
      if ((this.activeAggAgency.length > 0 && !this.isLanguageAdmin()) || this.activeAggLanguage.length > 0) {
        activeAgg += ';';
      }
      activeAgg += 'status:' + this.activeAggStatus.join(',');
    }

    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: {
        f: activeAgg === '' ? null : activeAgg,
      },
      queryParamsHandling: 'merge',
    });
  }
}
