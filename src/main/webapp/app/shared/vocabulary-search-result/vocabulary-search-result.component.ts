/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

import {Component, ElementRef, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {EditorService} from 'app/editor/editor.service';
import {
  JhiAlertService,
  JhiDataUtils,
  JhiEventManager,
  JhiEventWithContent,
  JhiLanguageService,
  JhiParseLinks
} from 'ng-jhipster';
import {AppScope} from 'app/shared/model/enumerations/app-scope.model';
import {FormBuilder} from '@angular/forms';
import VocabularyUtil from 'app/shared/util/vocabulary-util';
import {Account} from 'app/core/user/account.model';
import {IVocabulary} from 'app/shared/model/vocabulary.model';
import {Observable, of, Subscription} from 'rxjs';
import {AGGR_AGENCY, AGGR_STATUS, ITEMS_PER_PAGE, PAGING_SIZE} from 'app/shared';
import {IBucket} from 'app/shared/model/bucket';
import {AccountService} from 'app/core/auth/account.service';
import {LoginModalService} from 'app/core/login/login-modal.service';
import {ActivatedRoute, NavigationEnd, Router} from '@angular/router';
import {ICode} from 'app/shared/model/code.model';
import {HttpHeaders, HttpResponse} from '@angular/common/http';
import {ICvResult} from 'app/shared/model/cv-result.model';
import {IAggr} from 'app/shared/model/aggr';
import {HomeService} from 'app/home/home.service';
import {VocabularyLanguageFromKeyPipe} from 'app/shared';

@Component({
  selector: 'jhi-vocabulary-search-result',
  templateUrl: './vocabulary-search-result.component.html'
})
export class VocabularySearchResultComponent implements OnInit, OnDestroy {
  @Input() appScope!: AppScope;
  @ViewChild('filterPanels', { static: true }) filterPanels!: ElementRef;

  account: Account | null = null;

  vocabularies?: IVocabulary[];
  eventSubscriber?: Subscription;
  currentSearch?: string;
  links: any;

  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  pagingSize = PAGING_SIZE;
  page!: number;
  predicate = 'code';
  ascending!: boolean;
  ngbPaginationPage = 1;

  aggAgencyBucket?: IBucket[];
  aggStatusBucket?: IBucket[];
  activeAggAgency?: string[];
  activeAggLanguage?: string[];
  activeAggStatus?: string[];
  activeAgg = '';

  isAggAgencyCollapsed = true;
  isAggStatusCollapsed = true;
  isFilterCollapse = false;
  isActionCollapse = false;

  searchForm = this.fb.group({
    aggAgency: [],
    aggStatus: [],
    size: [this.itemsPerPage],
    sortBy: ['code,asc']
  });

  constructor(
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    protected languageService: JhiLanguageService,
    protected homeService: HomeService,
    protected editorService: EditorService,
    protected parseLinks: JhiParseLinks,
    protected jhiAlertService: JhiAlertService,
    protected activatedRoute: ActivatedRoute,
    protected dataUtils: JhiDataUtils,
    protected router: Router,
    protected eventManager: JhiEventManager,
    private fb: FormBuilder,
    private vocabLangPipeKey: VocabularyLanguageFromKeyPipe
  ) {
    this.currentSearch = '';
    this.activeAggAgency = [];
    this.activeAggLanguage = [];
    this.activeAggStatus = [];
    this.activatedRoute.queryParams.subscribe(params => {
      if (params['q']) {
        this.currentSearch = params['q'];
      }
      if (params['size']) {
        this.itemsPerPage = params['size'];
      }
      if (params['page']) {
        this.page = params['page'];
      }
      if (params['sort']) {
        const sortProp: string[] = params['sort'].split(',');
        this.predicate = sortProp[0];
        if (sortProp.length === 2) {
          this.ascending = sortProp[0] === 'asc';
        }
      }
      if (params['f']) {
        this.activeAgg = params['f'];
        const activeFilters: string[] = params['f'].split(';', 2);
        activeFilters.forEach(af => {
          const activeFilter: string[] = af.split(':', 2);
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
      }
    });

    this.isAggAgencyCollapsed = this.activeAggAgency.length === 0;
    this.isAggStatusCollapsed = this.activeAggStatus.length === 0;
  }

  getBaseUrl(): string {
    return this.isEditorScope() ? '/editor/vocabulary': '/vocabulary';
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

  isVersionContains(vocab: IVocabulary, lang: string, versionType: string): boolean {
    return VocabularyUtil.getTitleDefByLangIso(vocab, lang)[2].includes(versionType);
  }

  getTitleByLang(vocab: IVocabulary): string {
    return VocabularyUtil.getTitleDefByLangIso(vocab, vocab.selectedLang!)[0];
  }

  getDefinitionByLang(vocab: IVocabulary): string {
    return VocabularyUtil.getTitleDefByLangIso(vocab, vocab.selectedLang!)[1];
  }

  getCodeTitleByLang(code: ICode, selectedLang: string): string {
    return VocabularyUtil.getTitleDefByLangIso(code, selectedLang)[0];
  }

  getCodeDefinitionByLang(code: ICode, selectedLang: string): string {
    return VocabularyUtil.getTitleDefByLangIso(code, selectedLang)[1];
  }

  getVersionByLang(vocab: IVocabulary): string {
    return VocabularyUtil.getTitleDefByLangIso(vocab, vocab.selectedLang!)[2];
  }

  loadPage(page?: number): void {
    this.eventManager.broadcast({ name: 'onSearching', content: true });

    const pageToLoad: number = page ? page : this.page;
    if (this.appScope === AppScope.EDITOR ) {
      this.editorService
        .search(this.getSearchRequest(page ? page : this.page))
        .subscribe(
          (res: HttpResponse<ICvResult>) => this.onSuccess(res.body, res.headers, pageToLoad),
          () => this.onError()
        );
    } else {
      this.homeService
        .search(this.getSearchRequest(page ? page : this.page))
        .subscribe(
          (res: HttpResponse<ICvResult>) => this.onSuccess(res.body, res.headers, pageToLoad),
          () => this.onError()
        );
    }

  }

  private getSearchRequest(pageToLoad: number): any {
    return {
      q: this.currentSearch,
      f: this.activeAgg,
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.sort()
    }
  }

  protected onSuccess(data: ICvResult | null, headers: HttpHeaders, page: number): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    this.vocabularies = data!.vocabularies;
    // assign selectedLang if still null
    this.vocabularies!.forEach(v => {
      if (v.selectedLang === null) {
        v.selectedLang = v.sourceLanguage;
      }
    });
    this.updateForm(data!.aggrs!);
    this.eventManager.broadcast({ name: 'onSearching', content: false });
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page;
  }

  search(query: string, pred?: string): void {
    this.page = 0;
    this.currentSearch = query;
    this.predicate = 'relevance';
    if (query === '') {
      this.ascending = true;
      this.predicate = 'code';
    }
    this.clearFilter();
    if (pred) {
      this.activeAggLanguage!.push(pred);
    }
    this.buildFilterAndRefreshSearch();
  }

  clearFilter(): void {
    this.activeAggAgency = [];
    this.activeAggLanguage = [];
    this.activeAggStatus = [];
    this.activeAgg = '';
    this.searchForm.patchValue({ aggAgency: [] });
    this.searchForm.patchValue({ aggStatus: [] });
  }

  clearFilterAndReload(): void {
    this.clearFilter();
    this.buildFilterAndRefreshSearch();
  }

  trackNotation(notation: string, item: IVocabulary): string {
    return item.notation!;
  }

  sort(): string[] {
    if (this.predicate === 'relevance') return [this.predicate];
    return [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
  }

  ngOnInit(): void {
    this.router.events.subscribe(evt => {
      if (!(evt instanceof NavigationEnd)) {
        return;
      }
      window.scrollTo(0, 0);
    });
    this.activatedRoute.data.subscribe(data => {
      this.page = data.pagingParams.page;
      this.ascending = data.pagingParams.ascending;
      this.ngbPaginationPage = data.pagingParams.page;
      this.loadPage();
    });
    this.registerCvSearchEvent();
  }

  login(): void {
    this.loginModalService.open();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventSubscriber.unsubscribe();
    }
  }

  private registerCvSearchEvent(): void {
    this.eventSubscriber = this.eventManager.subscribe('doCvPublicationSearch', (response: JhiEventWithContent<any>) => {
      this.search(response.content.term, response.content.lang);
    });
  }

  updateForm(aggrs: IAggr[]): void {
    // patch value for sort and size
    this.searchForm.patchValue({
      size: this.itemsPerPage,
      sortBy: this.sort()
    });
    // patch value for filter
    aggrs.forEach(aggr => {
      if (aggr.field === AGGR_AGENCY) {
        // format bucket and add as autocomplete and patch form value
        this.aggAgencyBucket = this.formatBuckets(aggr.buckets!.concat(aggr.filteredBuckets!));
        this.searchForm.patchValue({ aggAgency: this.prepareActiveBuckets(this.aggAgencyBucket, aggr) });
      } else if (aggr.field === AGGR_STATUS) {
        this.aggStatusBucket = this.formatBuckets(aggr.buckets!.concat(aggr.filteredBuckets!));
        this.searchForm.patchValue({ aggStatus: this.prepareActiveBuckets(this.aggStatusBucket, aggr) });
      }
    });
  }
  private prepareActiveBuckets(buckets: IBucket[], aggr: IAggr): IBucket[] {
    const activeBucket: IBucket[] = [];
    aggr.values!.forEach(activeVal => {
      activeBucket.push(buckets.find(b => b.k === activeVal)!);
    });
    return activeBucket;
  }

  private formatBucketLanguages(buckets: IBucket[]): IBucket[] {
    if (buckets !== undefined && buckets.length > 0) {
      buckets.forEach(bucket => {
        bucket.value = bucket.k;
        bucket.display = this.vocabLangPipeKey.transform(bucket.k!) + ' (' + bucket.v + ')';
      });
    }
    return buckets;
  }

  private formatBuckets(buckets: IBucket[]): IBucket[] {
    if (buckets !== undefined && buckets.length > 0) {
      buckets.forEach(bucket => {
        bucket.value = bucket.k;
        bucket.display = bucket.k + ' (' + bucket.v + ')';
      });
    }
    return buckets;
  }

  sortLangByEnum(languages: string[], sourceLang: string): string[] {
    return VocabularyUtil.sortLangByEnum(languages, sourceLang);
  }

  getFormattedLangIso(vocab: IVocabulary, lang: string, sourceLang: string): string {
    const statusInfo = VocabularyUtil.getTitleDefByLangIso(vocab, lang)[2];
    const indexOf = statusInfo.indexOf('_');
    return (
      this.vocabLangPipeKey.transform(lang) +
      (lang === sourceLang ? ' SOURCE' : '') +
      (indexOf > 0 ? ' (' + statusInfo.substr(indexOf + 1) + ')' : '')
    );
  }

  public formatFilterText(value: any): Observable<object> {
    value.name = value.f;
    return of(value);
  }

  loadPageClicked(pageNo: number): void {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: { page: pageNo },
      queryParamsHandling: 'merge'
    });
    this.page = pageNo;
    this.loadPage();
  }

  refreshSearchBySize(s: string): void {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: { size: s },
      queryParamsHandling: 'merge'
    });
    this.itemsPerPage = +s; // convert string to number
    this.loadPage();
  }

  refreshSearchBySort(s: string): void {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: { sort: s },
      queryParamsHandling: 'merge'
    });
    this.ascending = true;
    this.predicate = 'relevance';
    if (s === 'code,asc') {
      this.predicate = 'code';
    } else if (s === 'code,desc') {
      this.predicate = 'code';
      this.ascending = false;
    }
    this.loadPage();
  }

  onAddAgency(addedItem?: IBucket): void {
    this.activeAggAgency!.push(addedItem!.k!);
    this.buildFilterAndRefreshSearch();
  }

  onRemoveAgency(removedItem?: IBucket): void {
    this.activeAggAgency!.forEach((item, index) => {
      if (item === removedItem!.k!) this.activeAggAgency!.splice(index, 1);
    });
    this.buildFilterAndRefreshSearch();
  }

  onAddStatus(addedItem?: IBucket): void {
    this.activeAggStatus!.push(addedItem!.k!);
    this.buildFilterAndRefreshSearch();
  }

  onRemoveStatus(removedItem?: IBucket): void {
    this.activeAggStatus!.forEach((item, index) => {
      if (item === removedItem!.k!) this.activeAggStatus!.splice(index, 1);
    });
    this.buildFilterAndRefreshSearch();
  }

  buildFilterAndRefreshSearch(): void {
    this.activeAgg = '';
    if (this.activeAggAgency!.length > 0) {
      this.activeAgg = 'agency:' + this.activeAggAgency!.join(',');
    }
    if (this.activeAggLanguage!.length > 0) {
      if (this.activeAggAgency!.length > 0) {
        this.activeAgg += ';';
      }
      this.activeAgg += 'language:' + this.activeAggLanguage!.join(',');
    }
    if (this.activeAggStatus!.length > 0) {
      if (this.activeAggAgency!.length > 0 || this.activeAggLanguage!.length > 0) {
        this.activeAgg += ';';
      }
      this.activeAgg += 'status:' + this.activeAggStatus!.join(',');
    }

    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: {
        q: this.currentSearch === '' ? null : this.currentSearch,
        f: this.activeAgg === '' ? null : this.activeAgg,
        sort: this.sort()
      },
      queryParamsHandling: 'merge'
    });
    this.loadPage(1);
  }
}
