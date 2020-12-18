import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {HttpHeaders, HttpResponse} from '@angular/common/http';
import {Observable, of, Subscription} from 'rxjs';
import {LoginModalService} from 'app/core/login/login-modal.service';
import {AccountService} from 'app/core/auth/account.service';
import {Account} from 'app/core/user/account.model';
import {IVocabulary} from 'app/shared/model/vocabulary.model';
import {
  JhiAlertService,
  JhiDataUtils,
  JhiEventManager,
  JhiEventWithContent,
  JhiLanguageService,
  JhiParseLinks
} from 'ng-jhipster';
import {HomeService} from 'app/home/home.service';
import {ActivatedRoute, NavigationEnd, Params, Router} from '@angular/router';

import {AGGR_AGENCY, ITEMS_PER_PAGE, PAGING_SIZE} from 'app/shared';
import {ICvResult} from 'app/shared/model/cv-result.model';
import VocabularyUtil from 'app/shared/util/vocabulary-util';
import {ICode} from 'app/shared/model/code.model';
import {IAggr} from 'app/shared/model/aggr';
import {FormBuilder} from '@angular/forms';
import {IBucket} from 'app/shared/model/bucket';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit, OnDestroy {
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
  activeAggAgency?: string[];
  activeAggLanguage?: string[];
  activeAgg = '';

  isAggAgencyCollapsed = true;
  isFilterCollapse = true;

  searchForm = this.fb.group({
    aggAgency: [],
    aggLanguage: [],
    size: [this.itemsPerPage],
    sortBy: ['code,asc']
  });

  constructor(
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    protected languageService: JhiLanguageService,
    protected homeService: HomeService,
    protected parseLinks: JhiParseLinks,
    protected jhiAlertService: JhiAlertService,
    protected activatedRoute: ActivatedRoute,
    protected dataUtils: JhiDataUtils,
    protected router: Router,
    protected eventManager: JhiEventManager,
    private fb: FormBuilder
  ) {
    this.currentSearch = '';
    this.activeAggAgency = [];
    this.activeAggLanguage = [];
    this.activatedRoute.queryParams.subscribe(params => {
      this.assignQueryParamsToVariables(params);
    });

    this.isAggAgencyCollapsed = this.activeAggAgency.length === 0;
  }

  private assignQueryParamsToVariables(params: Params): void {
    if (params['size']) {
      this.itemsPerPage = params['size'];
    }
    if (params['q']) {
      this.currentSearch = params['q'];
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
          if (activeFilter[0] === 'language') {
            this.activeAggLanguage = activeFilter[1].split(',');
          } else if (activeFilter[0] === 'agency') {
            this.activeAggAgency = activeFilter[1].split(',');
          }
        }
      });
      if (params['page']) {
        this.page = params['page'];
      }
    }
  }

  getCurrentLanguage(): string {
    return this.languageService.currentLang;
  }

  getVersionByLang(vocab: IVocabulary): string {
    return VocabularyUtil.getVocabularyVersionBySelectedLang(vocab, vocab.selectedLang!);
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

  getCodeTitleByLang(code: ICode, selectedLang: string): string {
    return VocabularyUtil.getCodeTitleBySelectedLang(code, selectedLang);
  }

  getCodeDefinitionByLang(code: ICode, selectedLang: string): string {
    return VocabularyUtil.getCodeDefinitionBySelectedLang(code, selectedLang);
  }

  getTitleByLang(vocab: IVocabulary): string {
    return VocabularyUtil.getVocabularyTitleBySelectedLang(vocab);
  }

  getDefinitionByLang(vocab: IVocabulary): string {
    return VocabularyUtil.getVocabularyDefinitionBySelectedLang(vocab);
  }

  loadPage(page?: number): void {
    this.eventManager.broadcast({ name: 'onSearching', content: true });
    const pageToLoad: number = page ? page : this.page;
    this.homeService
      .search({
        page: pageToLoad - 1,
        q: this.currentSearch,
        sort: this.sort(),
        f: this.activeAgg,
        size: this.itemsPerPage
      })
      .subscribe(
        (res: HttpResponse<ICvResult>) => this.onSuccess(res.body, res.headers, pageToLoad),
        () => this.onError()
      );
    return;
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
      this.predicate = 'code';
      this.ascending = true;
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
    this.activeAgg = '';
    this.searchForm.patchValue({ aggAgency: [] });
    this.searchForm.patchValue({ aggLanguage: [] });
  }

  clearFilterAndReload(): void {
    this.clearFilter();
    this.buildFilterAndRefreshSearch();
  }

  trackNotation(notation: string, item: IVocabulary): string {
    return item.notation!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    return this.dataUtils.openFile(contentType, base64String);
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
        bucket.display = VocabularyUtil.getLangIsoFormatted(bucket.k) + ' (' + bucket.v + ')';
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

  getFormattedLangIso(lang: string, sourceLang: string): string {
    return VocabularyUtil.getLangIsoFormatted(lang) + (lang === sourceLang ? ' SOURCE' : '');
  }

  public formatFilterText(value: any): Observable<object> {
    value.name = value.f;
    return of(value);
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

  loadPageClicked(pageNo: number): void {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: { page: pageNo },
      queryParamsHandling: 'merge'
    });
    this.page = pageNo;
    this.loadPage();
  }

  refreshSearchBySort(s: string): void {
    this.router.navigate([], {
      queryParams: { sort: s },
      relativeTo: this.activatedRoute,
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

  buildFilterAndRefreshSearch(): void {
    this.activeAgg = '';

    if (this.activeAggLanguage!.length > 0) {
      if (this.activeAggAgency!.length > 0) {
        this.activeAgg += ';';
      }
      this.activeAgg += 'language:' + this.activeAggLanguage!.join(',');
    }

    if (this.activeAggAgency!.length > 0) {
      this.activeAgg = 'agency:' + this.activeAggAgency!.join(',');
    }

    this.router.navigate([], {
      queryParams: {
        q: this.currentSearch === '' ? null : this.currentSearch,
        f: this.activeAgg === '' ? null : this.activeAgg,
        sort: this.sort()
      },
      relativeTo: this.activatedRoute,
      queryParamsHandling: 'merge'
    });
    this.loadPage(1);
  }
}
