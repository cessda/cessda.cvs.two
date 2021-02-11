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

import {Component, ElementRef, NgZone, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, NavigationEnd, Router} from '@angular/router';
import {JhiDataUtils, JhiEventManager} from 'ng-jhipster';

import {IVocabulary} from 'app/shared/model/vocabulary.model';
import {IVersion} from 'app/shared/model/version.model';

import VocabularyUtil from 'app/shared/util/vocabulary-util';
import {FormBuilder} from '@angular/forms';
import {HomeService} from 'app/home/home.service';
import {RouteEventsService} from 'app/shared';
import {DiffContent} from 'ngx-text-diff/lib/ngx-text-diff.model';
import {Observable, Subject} from 'rxjs';
import {AppScope} from 'app/shared/model/enumerations/app-scope.model';

@Component({
  selector: 'jhi-home-detail',
  templateUrl: './home-detail.component.html'
})
export class HomeDetailComponent implements OnInit {
  @ViewChild('detailPanel', { static: true }) detailPanel!: ElementRef;
  @ViewChild('versionPanel', { static: true }) versionPanel!: ElementRef;
  @ViewChild('identityPanel', { static: true }) identityPanel!: ElementRef;
  @ViewChild('usagePanel', { static: true }) usagePanel!: ElementRef;
  @ViewChild('licensePanel', { static: true }) licensePanel!: ElementRef;
  @ViewChild('exportPanel', { static: true }) exportPanel!: ElementRef;

  appScope: AppScope = AppScope.PUBLICATION;

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

  currentSelectedCode?: string;

  enableDocxExport = false;

  contentObservable: Subject<DiffContent> = new Subject<DiffContent>();
  contentObservable$: Observable<DiffContent> = this.contentObservable.asObservable();

  detailForm = this.fb.group({
    tabSelected: [],
    downloadFormGroup: this.fb.group({
      skosItems: [],
      pdfItems: [],
      htmlItems: [],
      docxItems: []
    })
  });
  constructor(
    protected dataUtils: JhiDataUtils,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected homeService: HomeService,
    private fb: FormBuilder,
    private routeEventsService: RouteEventsService,
    private _ngZone: NgZone,
    protected eventManager: JhiEventManager
  ) {
    this.initialTabSelected = 'detail';
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
    this.eventManager.broadcast({ name: 'closeComparison', content: true });
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
      if (v.number!.startsWith(this.vocabulary!.versions![0].number!) && !uniqueLang.some(l => l === v.language)) {
        uniqueLang.push(v.language!);
      }
    });
    // sort only the SL & TLs in the current version
    uniqueLang = VocabularyUtil.sortLangByEnum(uniqueLang, uniqueLang[0]);

    this.vocabulary!.versions!.forEach(v => {
      if (!v.number!.startsWith(this.vocabulary!.versions![0].number!) && !uniqueLang.some(l => l === v.language)) {
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
      if (this.vocabulary!.selectedCode) {
        this.currentSelectedCode = this.vocabulary!.selectedCode;
      }
      if (this.initialLangSelect !== null) {
        if (!this.vocabulary!.versions!.some(v => v.language === this.initialLangSelect)) {
          this.vocabulary!.selectedLang = this.vocabulary!.sourceLanguage!;
        } else {
          this.vocabulary!.selectedLang = this.initialLangSelect!;
        }
      }

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
    });

    this.detailForm.patchValue({
      tabSelected: this.initialTabSelected
    });
  }

  previousState(): void {
    window.history.back();
  }

  goToCvSearch(): void {
    const prevHistory = this.routeEventsService.previousRoutePath.value;
    if (prevHistory === '/' || prevHistory.startsWith('/?')) {
      this.router.navigateByUrl(prevHistory);
    } else {
      this.router.navigate(['']);
    }
  }

  getMissingTlVersion(version: string): string {
    if (version.startsWith(this.getSlVersion().number!)) {
      return this.getSlVersion().versionHistories![0].version + '.x';
    }
    let i = 0;
    this.getSlVersion().versionHistories!.forEach(function(vhSl, index): void {
      if (version.startsWith(vhSl.version!)) {
        i = index + 1;
      }
    });
    if (i > 0) {
      return this.getSlVersion().versionHistories![i].version + '.x';
    }
    return '';
  }
}
