import { AfterViewInit, Component, ElementRef, NgZone, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IVocabulary } from 'app/shared/model/vocabulary.model';
import { IVersion } from 'app/shared/model/version.model';

import VocabularyUtil from 'app/shared/util/vocabulary-util';
import { FormBuilder } from '@angular/forms';
import { HomeService } from 'app/home/home.service';
import { RouteEventsService } from 'app/shared';
import { DiffContent } from 'ngx-text-diff/lib/ngx-text-diff.model';
import { Observable, Subject } from 'rxjs';

@Component({
  selector: 'jhi-home-detail',
  templateUrl: './home-detail.component.html'
})
export class HomeDetailComponent implements OnInit, AfterViewInit {
  @ViewChild('detailPanel', { static: true }) detailPanel!: ElementRef;
  @ViewChild('versionPanel', { static: true }) versionPanel!: ElementRef;
  @ViewChild('identityPanel', { static: true }) identityPanel!: ElementRef;
  @ViewChild('usagePanel', { static: true }) usagePanel!: ElementRef;
  @ViewChild('licensePanel', { static: true }) licensePanel!: ElementRef;
  @ViewChild('exportPanel', { static: true }) exportPanel!: ElementRef;

  vocabulary: IVocabulary | null = null;

  isDetailCollapse = true;
  isVersionCollapse = true;
  isIdentityCollapse = true;
  isUsageCollapse = true;
  isLicenseCollapse = true;
  isExportCollapse = true;

  isCurrentVersionHistoryOpen = true;

  initialTabSelected?: string;
  initialLangSelect = null;

  dwnldCbVal: string[];
  skosSelected: boolean[];
  pdfSelected: boolean[];
  htmlSelected: boolean[];
  docxSelected: boolean[];

  currentSelectedCode?: string;

  enableDocxExport = false;

  contentObservable: Subject<DiffContent> = new Subject<DiffContent>();
  contentObservable$: Observable<DiffContent> = this.contentObservable.asObservable();

  detailForm = this.fb.group({
    tabSelected: [],
    skosItems: [],
    pdfItems: [],
    htmlItems: [],
    docxItems: []
  });
  constructor(
    protected dataUtils: JhiDataUtils,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected homeService: HomeService,
    private fb: FormBuilder,
    private routeEventsService: RouteEventsService,
    private _ngZone: NgZone
  ) {
    this.initialTabSelected = 'detail';
    this.dwnldCbVal = [];
    this.skosSelected = [];
    this.pdfSelected = [];
    this.htmlSelected = [];
    this.docxSelected = [];
    this.currentSelectedCode = '';
    this.activatedRoute.queryParams.subscribe(params => {
      if (params['lang']) {
        this.initialLangSelect = params['lang'];
      }
      if (params['tab']) {
        this.initialTabSelected = params['tab'];
      }
      if (params['code']) {
        this.initialTabSelected = 'detail';
        // handle code jump here
        this.currentSelectedCode = params['code'];
      }
      if (params['docx-export']) {
        this.initialTabSelected = 'export';
        if (params['docx-export'] === 'true') {
          this.enableDocxExport = true;
        }
      }
    });
  }

  setVocabularyLangVersion(language: string, number: string): void {
    this.vocabulary!.selectedLang = language;
    this.vocabulary!.selectedVersion = number;
  }

  getSlVersion(): IVersion {
    return VocabularyUtil.getSlVersion(this.vocabulary!);
  }

  getVersionByLangNumber(versionNumber?: string): IVersion {
    return VocabularyUtil.getVersionByLangAndNumber(this.vocabulary!, versionNumber);
  }

  getFormattedLang(langIso?: string): string {
    return VocabularyUtil.getLangIsoFormatted(langIso);
  }

  getUniqueVersionLang(): string[] {
    let uniqueLang: string[] = [];
    this.vocabulary!.versions!.forEach(v => {
      if (v.number!.startsWith(this.vocabulary!.versions![0]!.number!) && !uniqueLang.some(l => l === v.language)) {
        uniqueLang.push(v.language!);
      }
    });
    // sort only the SL & TLs in the current version
    uniqueLang = VocabularyUtil.sortLangByEnum(uniqueLang, uniqueLang[0]);

    this.vocabulary!.versions!.forEach(v => {
      if (!v.number!.startsWith(this.vocabulary!.versions![0]!.number!) && !uniqueLang.some(l => l === v.language)) {
        uniqueLang.push(v.language!);
      }
    });
    return uniqueLang;
  }

  getVersionsByLanguage(lang?: string): IVersion[] {
    return this.vocabulary!.versions!.filter(v => v.language === lang);
  }
  getFormattedVersionTooltip(version?: IVersion, sourceLang?: string): string {
    return (
      VocabularyUtil.getLangIsoFormatted(version!.language) + ' v.' + version!.number + (version!.language === sourceLang ? ' SOURCE' : '')
    );
  }
  getServerUrl(): string {
    return window.location.origin;
  }

  toggleDetailPanel(): void {
    if (this.detailPanel.nativeElement.style.display === 'none' || this.detailPanel.nativeElement.style.display === '') {
      this.isDetailCollapse = false;
      this.detailPanel.nativeElement.style.display = 'block';
    } else {
      this.isDetailCollapse = true;
      this.detailPanel.nativeElement.style.display = 'none';
    }
  }

  toggleVersionPanel(): void {
    if (this.versionPanel.nativeElement.style.display === 'none' || this.versionPanel.nativeElement.style.display === '') {
      this.isVersionCollapse = false;
      this.versionPanel.nativeElement.style.display = 'block';
    } else {
      this.isVersionCollapse = true;
      this.versionPanel.nativeElement.style.display = 'none';
    }
  }

  toggleIdentityPanel(): void {
    if (this.identityPanel.nativeElement.style.display === 'none' || this.identityPanel.nativeElement.style.display === '') {
      this.isIdentityCollapse = false;
      this.identityPanel.nativeElement.style.display = 'block';
    } else {
      this.isIdentityCollapse = true;
      this.identityPanel.nativeElement.style.display = 'none';
    }
  }

  toggleUsagePanel(): void {
    if (this.usagePanel.nativeElement.style.display === 'none' || this.usagePanel.nativeElement.style.display === '') {
      this.isUsageCollapse = false;
      this.usagePanel.nativeElement.style.display = 'block';
    } else {
      this.isUsageCollapse = true;
      this.usagePanel.nativeElement.style.display = 'none';
    }
  }

  toggleLicensePanel(): void {
    if (this.licensePanel.nativeElement.style.display === 'none' || this.licensePanel.nativeElement.style.display === '') {
      this.isLicenseCollapse = false;
      this.licensePanel.nativeElement.style.display = 'block';
    } else {
      this.isLicenseCollapse = true;
      this.licensePanel.nativeElement.style.display = 'none';
    }
  }

  toggleExportPanel(): void {
    if (this.exportPanel.nativeElement.style.display === 'none' || this.exportPanel.nativeElement.style.display === '') {
      this.isExportCollapse = false;
      this.exportPanel.nativeElement.style.display = 'block';
    } else {
      this.isExportCollapse = true;
      this.exportPanel.nativeElement.style.display = 'none';
    }
  }

  getSlVersionByVersion(vnumber: string): string {
    return VocabularyUtil.getSlVersionByVersionNumber(vnumber);
  }

  ngOnInit(): void {
    this.router.events.subscribe(evt => {
      if (!(evt instanceof NavigationEnd)) {
        return;
      }
      window.scrollTo(0, 0);
    });
    this.activatedRoute.data.subscribe(({ vocabulary }) => {
      this.vocabulary = vocabulary;
      if (this.initialLangSelect !== null) {
        if (!this.vocabulary!.versions!.some(v => v.language === this.initialLangSelect)) {
          this.vocabulary!.selectedLang = this.vocabulary!.sourceLanguage!;
        } else {
          this.vocabulary!.selectedLang = this.initialLangSelect!;
        }
      }

      const langs: string[] = this.getUniqueVersionLang();
      for (let i = 0; i < langs.length; i++) {
        const versions: IVersion[] = this.getVersionsByLanguage(langs[i]);
        if (versions[0].number!.startsWith(this.getVersionsByLanguage(langs[0])[0].number!)) {
          this.dwnldCbVal[i] = langs[i] + '-' + versions[0].number;
          this.skosSelected[i] = true;
          this.pdfSelected[i] = true;
          this.htmlSelected[i] = true;
          this.docxSelected[i] = true;
        }
      }
    });

    this.detailForm.patchValue({
      tabSelected: this.initialTabSelected,
      skosItems: this.skosSelected,
      pdfItems: this.pdfSelected,
      htmlItems: this.htmlSelected,
      docxItems: this.docxSelected
    });

    if (this.currentSelectedCode !== '') {
      this._ngZone.runOutsideAngular(() => {
        setTimeout(() => {
          const element = document.querySelector('#code_' + this.currentSelectedCode);
          element!.scrollIntoView({ behavior: 'smooth' });
          element!.classList.add('highlight');
          this._ngZone.runOutsideAngular(() => {
            window.setTimeout(() => {
              element!.classList.remove('highlight');
            }, 5000);
          });
        }, 1500);
      });
    }
  }
  ngAfterViewInit(): void {
    this._ngZone.run(() => {
      this.resetExport();
    });
  }

  resetExport(): void {
    this.fillBolleanArray(this.skosSelected, false);
    this.fillBolleanArray(this.pdfSelected, false);
    this.fillBolleanArray(this.htmlSelected, false);
    this.fillBolleanArray(this.docxSelected, false);
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
  }

  previousState(): void {
    window.history.back();
  }

  updateCheckboxValue(i: number, lang: string, versionNimber: string): void {
    this.dwnldCbVal[i] = lang + '_' + versionNimber;
  }

  downloadSkos(): void {
    this.downloadVocabularyFile('rdf', this.getCheckedItems(this.skosSelected), 'text/xml');
  }

  downloadPdf(): void {
    this.downloadVocabularyFile('pdf', this.getCheckedItems(this.pdfSelected), 'application/pdf');
  }

  downloadHtml(): void {
    this.downloadVocabularyFile('html', this.getCheckedItems(this.htmlSelected), 'text/html');
  }

  downloadDocx(): void {
    this.downloadVocabularyFile(
      'docx',
      this.getCheckedItems(this.docxSelected),
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
    );
  }

  private downloadVocabularyFile(fileFormat: string, checkedItems: string, mimeType: string): void {
    this.homeService
      .downloadVocabularyFile(this.vocabulary!.notation!, this.getSlVersion()!.number!, fileFormat, {
        lv: checkedItems
      })
      .subscribe((res: Blob) => {
        const newBlob = new Blob([res], { type: mimeType });
        if (window.navigator && window.navigator.msSaveOrOpenBlob) {
          window.navigator.msSaveOrOpenBlob(newBlob);
          return;
        }
        const data = window.URL.createObjectURL(newBlob);
        const link = document.createElement('a');
        link.href = data;
        link.download = this.vocabulary!.notation! + '-' + this.getSlVersion()!.number! + '_' + checkedItems + '.' + fileFormat;
        link.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, view: window }));
        setTimeout(function(): void {
          window.URL.revokeObjectURL(data);
          link.remove();
        }, 100);
      });
  }

  getCheckedItems(checkedArray: boolean[]): string {
    let selectedVersion = '';
    for (let i = 0; i < checkedArray.length; i++) {
      if (checkedArray[i] === true) {
        selectedVersion += this.dwnldCbVal[i] + '_';
      }
    }
    if (selectedVersion.length > 0) {
      selectedVersion = selectedVersion.substr(0, selectedVersion.length - 1);
    }
    return selectedVersion;
  }

  goToCvSearch(): void {
    const prevHistory = this.routeEventsService.previousRoutePath.value;
    if (prevHistory === '/' || prevHistory.startsWith('/?')) {
      this.router.navigateByUrl(prevHistory);
    } else {
      this.router.navigate(['']);
    }
  }

  toggleSelectAll(downloadType: string, checked: boolean): void {
    switch (downloadType) {
      case 'skos':
        this.fillBolleanArray(this.skosSelected, checked);
        break;
      case 'pdf':
        this.fillBolleanArray(this.pdfSelected, checked);
        break;
      case 'html':
        this.fillBolleanArray(this.htmlSelected, checked);
        break;
      case 'docx':
        this.fillBolleanArray(this.docxSelected, checked);
        break;
    }
  }

  private fillBolleanArray(bollArray: boolean[], checked: boolean): void {
    for (let i = 0; i < bollArray.length; i++) {
      bollArray[i] = checked;
    }
  }

  getMissingTlVersion(version: string): string {
    let i = 0;
    this.getSlVersion()!.versionHistories!.forEach(function(vhSl, index): void {
      if (version.startsWith(vhSl.version!)) {
        i = index + 1;
      }
    });
    if (i > 0) {
      return this.getSlVersion()!.versionHistories![i]!.version + '.x';
    }
    return '';
  }
}
