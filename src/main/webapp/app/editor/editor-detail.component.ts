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
import { Component, ElementRef, NgZone, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { JhiDataUtils, JhiEventManager, JhiEventWithContent } from 'ng-jhipster';

import { IVocabulary } from 'app/shared/model/vocabulary.model';
import { IVersion } from 'app/shared/model/version.model';

import VocabularyUtil from 'app/shared/util/vocabulary-util';
import { FormBuilder, Validators } from '@angular/forms';
import { EditorService } from 'app/editor/editor.service';
import { RouteEventsService } from 'app/shared';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EditorDetailCvAddEditDialogComponent } from 'app/editor/editor-detail-cv-add-edit-dialog.component';
import { EditorDetailCvDeleteDialogComponent } from 'app/editor/editor-detail-cv-delete-dialog.component';
import { EditorDetailCodeAddEditDialogComponent } from 'app/editor/editor-detail-code-add-edit-dialog.component';
import { Concept, IConcept } from 'app/shared/model/concept.model';
import { Observable, Subscription } from 'rxjs';
import { EditorDetailCodeDeprecateDialogComponent } from 'app/editor/editor-detail-code-deprecate-dialog.component';
import { EditorDetailCodeDeleteDialogComponent } from 'app/editor/editor-detail-code-delete-dialog.component';
import { EditorDetailCodeReorderDialogComponent } from 'app/editor/editor-detail-code-reorder-dialog.component';
import { EditorDetailCvForwardStatusDialogComponent } from 'app/editor/editor-detail-cv-forward-status-dialog.component';
import { IVocabularySnippet, VocabularySnippet } from 'app/shared/model/vocabulary-snippet.model';
import { HttpResponse } from '@angular/common/http';
import { EditorDetailCvNewVersionDialogComponent } from 'app/editor/editor-detail-cv-new-version-dialog.component';
import { EditorDetailCodeCsvImportDialogComponent, EditorDetailCvCommentDialogComponent } from '.';
import * as moment from 'moment';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { AppScope } from 'app/shared/model/enumerations/app-scope.model';
import { VocabularyLanguageFromKeyPipe } from 'app/shared';

@Component({
  selector: 'jhi-editor-detail',
  templateUrl: './editor-detail.component.html',
})
export class EditorDetailComponent implements OnInit, OnDestroy {
  @ViewChild('detailEPanel', { static: true }) detailEPanel!: ElementRef;
  @ViewChild('versionEPanel', { static: true }) versionEPanel!: ElementRef;
  @ViewChild('identityEPanel', { static: true }) identityEPanel!: ElementRef;
  @ViewChild('usageEPanel', { static: true }) usageEPanel!: ElementRef;
  @ViewChild('licenseEPanel', { static: true }) licenseEPanel!: ElementRef;
  @ViewChild('exportEPanel', { static: true }) exportEPanel!: ElementRef;

  appScope: AppScope = AppScope.EDITOR;
  account!: Account;

  eventSubscriber?: Subscription;
  eventSubscriber2?: Subscription;

  vocabulary: IVocabulary | null = null;
  version: IVersion | null = null;
  concept: IConcept | null = null;

  isDetailCollapse = true;
  isVersionCollapse = true;
  isIdentityCollapse = true;
  isUsageCollapse = true;
  isLicenseCollapse = true;
  isExportCollapse = true;
  isCvActionCollapse = false;
  isCodeActionCollapse = false;
  isCurrentVersionHistoryOpen = true;
  enableAddTl = false;
  enablePublishTl = false;

  initialTabSelected?: string;
  initialLangSelect = null;

  availableLanguages: string[] = [];
  availableTlLanguages: string[] = [];
  tlLanguageStatus = 'none';

  currentSelectedCode?: string;
  activePopup?: string;

  enableDocxExport = false;

  protected ngbModalRef?: NgbModalRef | null;

  isCurrentVersionInfoEdit = false;
  isDdiUsageEdit = false;
  isIdentityEdit = false;
  isNotesEdit = false;

  isShowingDeprecatedCodes = false;

  codeTlActionRoles = ['ADMIN', 'ADMIN_TL', 'ADMIN_CONTENT'];

  noOfComments = 0;

  quillModules: any = {
    toolbar: [
      ['bold', 'italic', 'underline', 'strike'],
      ['blockquote'],
      [{ list: 'ordered' }, { list: 'bullet' }],
      [{ header: [1, 2, 3, 4, 5, 6, false] }],
      [{ color: [] }, { background: [] }],
      ['link'],
      ['clean'],
    ],
  };

  editorDetailForm = this.fb.group({
    tabSelected: [],
    downloadFormGroup: this.fb.group({
      skosItems: [],
      pdfItems: [],
      htmlItems: [],
      docxItems: [],
    }),
    ddiUsage: [],
    translateAgency: [],
    translateAgencyLink: [
      '',
      [
        Validators.required,
        Validators.pattern(
          '(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})'
        ),
      ],
    ],
    notes: [],
    versionNotes: [],
    versionChanges: [],
  });

  // @ts-ignore
  public versionNotesEditor: Quill;
  // @ts-ignore
  public versionChangesEditor: Quill;
  // @ts-ignore
  public ddiUsageEditor: Quill;

  constructor(
    private accountService: AccountService,
    protected dataUtils: JhiDataUtils,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected editorService: EditorService,
    private fb: FormBuilder,
    private routeEventsService: RouteEventsService,
    private _ngZone: NgZone,
    protected modalService: NgbModal,
    protected eventManager: JhiEventManager,
    private vocabLangPipeKey: VocabularyLanguageFromKeyPipe
  ) {
    this.initialTabSelected = 'detail';
    this.currentSelectedCode = '';
    this.activePopup = '';
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
      if (params['popup']) {
        this.activePopup = params['popup'];
      }
      if (params['docx-export']) {
        this.initialTabSelected = 'export';
        if (params['docx-export'] === 'true') {
          this.enableDocxExport = true;
        }
      }
    });
  }

  getSlVersion(): IVersion {
    return VocabularyUtil.getSlVersionOfVocabulary(this.vocabulary!);
  }

  getSlVersionNumber(vnumber?: string): string {
    if (vnumber) {
      return VocabularyUtil.getSlVersionNumber(vnumber);
    } else {
      return VocabularyUtil.getSlVersionNumberOfVocabulary(this.vocabulary!);
    }
  }

  getSlMajorMinorVersionNumber(): string {
    return VocabularyUtil.getSlMajorMinorVersionNumber(this.getSlVersion().number!);
  }

  getSlMajorVersionNumber(): number {
    return VocabularyUtil.getSlMajorVersionNumberOfVersion(this.version!);
  }

  getSlMinorVersionNumber(): number {
    return VocabularyUtil.getSlMinorVersionNumberOfVersion(this.version!);
  }

  setActiveVersion(language: string, versionNumber?: string): void {
    this.vocabulary!.selectedLang = language;
    this.vocabulary!.selectedVersion = versionNumber;
    this.version = VocabularyUtil.getVersionByLangAndNumber(this.vocabulary!, versionNumber);
    this.closeNotes();
    this.closeCurrentVersionInfo();
    this.closeDdiUsage();
    this.closeTranslateIdentity();

    this.editorDetailForm.patchValue({
      ddiUsage: this.version.ddiUsage,
      translateAgency: this.version.translateAgency,
      translateAgencyLink: this.version.translateAgencyLink,
      notes: this.version.notes,
      versionNotes: this.version.versionNotes,
      versionChanges: this.version.versionChanges,
    });

    this.noOfComments = this.version.comments!.length;

    this.codeTlActionRoles = ['ADMIN', 'ADMIN_TL', 'ADMIN_CONTENT'];

    // make sure that version TL concepts follows the version SL concept in structure and some properties
    if (this.version.itemType === 'TL') {
      this.enableAddTl = false;
      this.enablePublishTl = false;
      // find SL version
      const slVersion = this.getSlVersion();
      this.version.languageSl = slVersion.language;

      const slConcepts = slVersion.concepts;

      for (let i = 0; i < slConcepts!.length; i++) {
        const tlCodes = this.version.concepts!.filter(c => c.notation === slConcepts![i].notation);
        if (tlCodes.length) {
          tlCodes[0].position = slConcepts![i].position;
          tlCodes[0].parent = slConcepts![i].parent ? slConcepts![i].parent : undefined;
          tlCodes[0].titleSl = slConcepts![i].title;
          tlCodes[0].definitionSl = slConcepts![i].definition;
        } else {
          // TL code not found, generate new one from SL
          const nonExistTlCode = {
            ...new Concept(),
            position: slConcepts![i].position,
            parent: slConcepts![i].parent ? slConcepts![i].parent : undefined,
            notation: slConcepts![i].notation,
            titleSl: slConcepts![i].title,
            definitionSl: slConcepts![i].definition,
            slConcept: slConcepts![i].id,
          };
          // insert
          this.version.concepts!.splice(i, 0, nonExistTlCode);
        }
      }
    } else {
      if (this.version.status === 'READY_TO_TRANSLATE' || this.version.status === 'PUBLISHED') {
        this.enableAddTl = true;
      } else {
        this.enableAddTl = false;
      }
      // check all versions if there is any READY_TO_PUBLISH state for TL
      for (const vocab of this.vocabulary!.versions!) {
        if (vocab.status! === 'READY_TO_PUBLISH') {
          this.enablePublishTl = true;
          break;
        } else {
          this.enablePublishTl = false;
        }
      }
    }
    // remove selection on version change
    this.eventManager.broadcast('deselectConcept');
  }

  getUniqueVersionLang(): string[] {
    const uniqueLang: string[] = [];
    this.vocabulary!.versions!.forEach(v => {
      if (!uniqueLang.some(l => l === v.language)) {
        uniqueLang.push(v.language!);
      }
    });
    return VocabularyUtil.sortLangByEnum(uniqueLang, uniqueLang[0]);
  }

  getVersionsByLanguage(lang?: string): IVersion[] {
    return this.vocabulary!.versions!.filter(v => v.language === lang);
  }
  getFormattedVersionTooltip(version?: IVersion, sourceLang?: string): string {
    return (
      this.vocabLangPipeKey.transform(version!.language!) +
      ' v.' +
      (version!.status === 'PUBLISHED' ? '' : version!.status + '-') +
      version!.number +
      (version!.language === sourceLang ? ' SOURCE' : '')
    );
  }
  getServerUrl(): string {
    return window.location.origin;
  }

  isVersionStatus(lang: string, versionType: string): boolean {
    const version = this.getVersionsByLanguage(lang)[0];
    return version.status === versionType;
  }

  isAnyLangVersionInBundle(vocab: IVocabulary, lang: string, bundle?: string): boolean {
    if (bundle === undefined) {
      bundle = vocab.versionNumber;
    }
    const versions = this.getVersionsByLanguage(lang);
    return VocabularyUtil.isAnyVersionInBundle(versions, bundle!);
  }

  hasDeprecatedConcepts(concepts: IConcept[]): boolean {
    return VocabularyUtil.hasDeprecatedConcepts(concepts);
  }

  toggleDetailEPanel(): void {
    if (this.detailEPanel.nativeElement.style.display === 'none' || this.detailEPanel.nativeElement.style.display === '') {
      this.isDetailCollapse = false;
      this.detailEPanel.nativeElement.style.display = 'block';
    } else {
      this.isDetailCollapse = true;
      this.detailEPanel.nativeElement.style.display = 'none';
    }
  }

  toggleVersionEPanel(): void {
    if (this.versionEPanel.nativeElement.style.display === 'none' || this.versionEPanel.nativeElement.style.display === '') {
      this.isVersionCollapse = false;
      this.versionEPanel.nativeElement.style.display = 'block';
    } else {
      this.isVersionCollapse = true;
      this.versionEPanel.nativeElement.style.display = 'none';
    }
  }

  toggleIdentityEPanel(): void {
    if (this.identityEPanel.nativeElement.style.display === 'none' || this.identityEPanel.nativeElement.style.display === '') {
      this.isIdentityCollapse = false;
      this.identityEPanel.nativeElement.style.display = 'block';
    } else {
      this.isIdentityCollapse = true;
      this.identityEPanel.nativeElement.style.display = 'none';
    }
  }

  toggleUsageEPanel(): void {
    if (this.usageEPanel.nativeElement.style.display === 'none' || this.usageEPanel.nativeElement.style.display === '') {
      this.isUsageCollapse = false;
      this.usageEPanel.nativeElement.style.display = 'block';
    } else {
      this.isUsageCollapse = true;
      this.usageEPanel.nativeElement.style.display = 'none';
    }
  }

  toggleLicenseEPanel(): void {
    if (this.licenseEPanel.nativeElement.style.display === 'none' || this.licenseEPanel.nativeElement.style.display === '') {
      this.isLicenseCollapse = false;
      this.licenseEPanel.nativeElement.style.display = 'block';
    } else {
      this.isLicenseCollapse = true;
      this.licenseEPanel.nativeElement.style.display = 'none';
    }
  }

  toggleExportEPanel(): void {
    if (this.exportEPanel.nativeElement.style.display === 'none' || this.exportEPanel.nativeElement.style.display === '') {
      this.isExportCollapse = false;
      this.exportEPanel.nativeElement.style.display = 'block';
    } else {
      this.isExportCollapse = true;
      this.exportEPanel.nativeElement.style.display = 'none';
    }
  }

  toggleDeprecatedCodesDisplay(): void {
    this.isShowingDeprecatedCodes = !this.isShowingDeprecatedCodes;
  }

  ngOnInit(): void {
    this.router.events.subscribe(evt => {
      if (!(evt instanceof NavigationEnd)) {
        return;
      }
      window.scrollTo(0, 0);
    });
    this.activatedRoute.data.subscribe(({ vocabulary }) => {
      VocabularyUtil.convertVocabularyToThreeDigitVersionNumer(vocabulary);
      this.vocabulary = vocabulary;
      this.availableLanguages = VocabularyUtil.getAvailableCvLanguage(vocabulary.versions);
      this.accountService.identity().subscribe(account => {
        if (account) {
          this.account = account;
          this.account.userAgencies.forEach(ua => {
            if (
              ua.agencyRole === 'ADMIN_TL' &&
              ua.agencyId === this.vocabulary!.agencyId &&
              ua.language &&
              this.availableLanguages.includes(ua.language)
            ) {
              this.availableTlLanguages.push(ua.language);
            }
          });
          this.tlLanguageStatus = this.availableTlLanguages.length ? 'any' : 'none';
        }
      });
      if (this.initialLangSelect !== null) {
        if (!this.vocabulary!.versions!.some(v => v.language === this.initialLangSelect)) {
          this.setActiveVersion(this.vocabulary!.sourceLanguage!);
        } else {
          this.setActiveVersion(this.initialLangSelect!);
        }
      } else {
        this.setActiveVersion(this.vocabulary!.selectedLang!);
      }
      if (this.vocabulary!.selectedVersion == null) {
        if (this.vocabulary!.selectedLang !== null) {
          this.vocabulary!.selectedVersion = VocabularyUtil.getVersionByLang(this.vocabulary!).number;
        }
      }

      // open popup, based on query param
      if (this.activePopup !== '') {
        if (this.activePopup === 'add-cv') {
          this.openAddEditCvPopup(true, true);
        } else if (this.activePopup === 'edit-cv') {
          this.openAddEditCvPopup(false, true);
        } else if (this.activePopup === 'add-tl-cv') {
          this.openAddEditCvPopup(true, false);
        } else if (this.activePopup === 'edit-tl-cv') {
          this.openAddEditCvPopup(false, false);
        }
      }
    });

    this.editorDetailForm.patchValue({
      tabSelected: this.initialTabSelected,
    });

    this.subscribeSelectConceptEvent();

    if (this.currentSelectedCode !== '') {
      this.isShowingDeprecatedCodes = this.version!.concepts!.some(concept => {
        return concept.deprecated;
      });
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

  private subscribeSelectConceptEvent(): void {
    this.eventSubscriber = this.eventManager.subscribe('selectConcept', (response: JhiEventWithContent<IConcept>) => {
      this.concept = response.content;
    });
    this.eventSubscriber2 = this.eventManager.subscribe('deselectConcept', () => {
      this.concept = null;
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

  openAddEditCvPopup(isNew: boolean, isSlForm: boolean): void {
    this.ngbModalRef = this.modalService.open(EditorDetailCvAddEditDialogComponent as Component, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.componentInstance.vocabularyParam = this.vocabulary;
    this.ngbModalRef.componentInstance.isNew = isNew;
    this.ngbModalRef.componentInstance.isSlForm = isSlForm;
    if (!isSlForm) {
      // find SL version
      const slVersion = this.getSlVersion();
      this.version!.titleSl = slVersion.title;
      this.version!.definitionSl = slVersion.definition;
      this.version!.notesSl = slVersion.notes;
      this.ngbModalRef.componentInstance.versionSlParam = slVersion;
    }
    this.ngbModalRef.componentInstance.versionParam = this.version;
  }

  openForwardStatusCvPopup(): void {
    this.ngbModalRef = this.modalService.open(EditorDetailCvForwardStatusDialogComponent as Component, {
      size: 'xl',
      backdrop: 'static',
    });
    this.ngbModalRef.componentInstance.vocabularyParam = this.vocabulary;
    this.ngbModalRef.componentInstance.versionParam = this.version;
    this.ngbModalRef.componentInstance.isSlForm = this.version!.itemType === 'SL';
    const slVersion = this.getSlVersion();
    this.ngbModalRef.componentInstance.slVersionNumber = slVersion.number;
  }

  openCreateNewCvVersionPopup(): void {
    this.ngbModalRef = this.modalService.open(EditorDetailCvNewVersionDialogComponent as Component, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.componentInstance.vocabularyParam = this.vocabulary;
    this.ngbModalRef.componentInstance.versionParam = this.version;
  }

  openDeleteCvPopup(): void {
    this.ngbModalRef = this.modalService.open(EditorDetailCvDeleteDialogComponent as Component, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.componentInstance.vocabularyParam = this.vocabulary;
    this.ngbModalRef.componentInstance.versionParam = this.version;
  }

  ngOnDestroy(): void {
    this.ngbModalRef = null;
    if (this.eventSubscriber) {
      this.eventSubscriber.unsubscribe();
    }
    if (this.eventSubscriber2) {
      this.eventSubscriber2.unsubscribe();
    }
  }

  openAddEditCodePopup(isNew: boolean, codeInsertMode?: string): void {
    this.ngbModalRef = this.modalService.open(EditorDetailCodeAddEditDialogComponent as Component, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.componentInstance.versionParam = this.version;
    this.ngbModalRef.componentInstance.conceptParam = this.concept;
    this.ngbModalRef.componentInstance.isNew = isNew;
    this.ngbModalRef.componentInstance.codeInsertMode = codeInsertMode;
    this.ngbModalRef.componentInstance.isSlForm = this.version!.itemType === 'SL';
  }

  openAddCodePopup(): void {
    this.openAddEditCodePopup(true);
  }

  openEditCodePopup(): void {
    this.openAddEditCodePopup(false);
  }

  openAddTlCode(): void {
    this.openAddEditCodePopup(false);
  }

  openEditTlCode(): void {
    this.openAddEditCodePopup(false);
  }

  openRemoveTlCode(): void {
    this.openDeleteCodeWindow();
  }

  openDeprecateCodePopup(): void {
    this.openDeprecateCodeWindow();
  }

  openDeprecateCodeWindow(): void {
    this.ngbModalRef = this.modalService.open(EditorDetailCodeDeprecateDialogComponent as Component, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.componentInstance.versionParam = this.version;
    this.ngbModalRef.componentInstance.conceptParam = this.concept;
    this.ngbModalRef.componentInstance.isSlForm = this.version!.itemType === 'SL';
    this.ngbModalRef.componentInstance.conceptList = this.version!.concepts;
  }

  openDeleteCodePopup(): void {
    this.openDeleteCodeWindow();
  }

  openDeleteCodeWindow(): void {
    this.ngbModalRef = this.modalService.open(EditorDetailCodeDeleteDialogComponent as Component, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.componentInstance.versionParam = this.version;
    this.ngbModalRef.componentInstance.conceptParam = this.concept;
    this.ngbModalRef.componentInstance.isSlForm = this.version!.itemType === 'SL';
  }

  openCsvImportCodeWindow(): void {
    this.ngbModalRef = this.modalService.open(EditorDetailCodeCsvImportDialogComponent as Component, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.componentInstance.vocabularyParam = this.vocabulary;
    this.ngbModalRef.componentInstance.versionParam = this.version;
    this.ngbModalRef.componentInstance.isSlForm = this.version!.itemType === 'SL';
  }

  openReorderCode(): void {
    this.ngbModalRef = this.modalService.open(EditorDetailCodeReorderDialogComponent as Component, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.componentInstance.versionParam = this.version;
    this.ngbModalRef.componentInstance.conceptParam = this.concept;
  }

  openCvCommentPopup(): void {
    this.ngbModalRef = this.modalService.open(EditorDetailCvCommentDialogComponent as Component, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.componentInstance.vocabularyParam = this.vocabulary;
    this.ngbModalRef.componentInstance.versionParam = this.version;
  }

  saveDdiUsage(): void {
    const vocabSnippet = {
      ...new VocabularySnippet(),
      actionType: 'EDIT_DDI_CV',
      agencyId: this.vocabulary!.agencyId,
      vocabularyId: this.vocabulary!.id,
      versionId: this.version!.id,
      language: this.version!.language,
      itemType: this.version!.itemType,
      ddiUsage: this.editorDetailForm.get(['ddiUsage'])!.value,
    };
    this.subscribeToSaveResponse(this.editorService.updateVocabulary(vocabSnippet), vocabSnippet);
  }

  saveTranslateIdentity(): void {
    const vocabSnippet = {
      ...new VocabularySnippet(),
      actionType: 'EDIT_IDENTITY_CV',
      agencyId: this.vocabulary!.agencyId,
      vocabularyId: this.vocabulary!.id,
      versionId: this.version!.id,
      language: this.version!.language,
      itemType: this.version!.itemType,
      translateAgency: this.editorDetailForm.get(['translateAgency'])!.value,
      translateAgencyLink: this.editorDetailForm.get(['translateAgencyLink'])!.value,
    };

    if (this.editorDetailForm.valid) {
      this.subscribeToSaveResponse(this.editorService.updateVocabulary(vocabSnippet), vocabSnippet);
    }
  }

  saveNotes(): void {
    const vocabSnippet = {
      ...new VocabularySnippet(),
      actionType: 'EDIT_NOTE_CV',
      agencyId: this.vocabulary!.agencyId,
      vocabularyId: this.vocabulary!.id,
      versionId: this.version!.id,
      language: this.version!.language,
      itemType: this.version!.itemType,
      notes: this.editorDetailForm.get(['notes'])!.value,
    };
    this.subscribeToSaveResponse(this.editorService.updateVocabulary(vocabSnippet), vocabSnippet);
  }

  saveCurrentVersionInfo(): void {
    const vocabSnippet = {
      ...new VocabularySnippet(),
      actionType: 'EDIT_VERSION_INFO_CV',
      agencyId: this.vocabulary!.agencyId,
      vocabularyId: this.vocabulary!.id,
      versionId: this.version!.id,
      language: this.version!.language,
      itemType: this.version!.itemType,
      versionNotes: this.editorDetailForm.get(['versionNotes'])!.value,
      versionChanges: this.editorDetailForm.get(['versionChanges'])!.value,
    };
    this.subscribeToSaveResponse(this.editorService.updateVocabulary(vocabSnippet), vocabSnippet);
  }

  subscribeToSaveResponse(result: Observable<HttpResponse<IVocabulary>>, vocabSnippet: IVocabularySnippet): void {
    result.subscribe(() => {
      if (vocabSnippet.actionType === 'EDIT_DDI_CV') {
        this.isDdiUsageEdit = !this.isDdiUsageEdit;
        this.version!.ddiUsage = vocabSnippet.ddiUsage;
      } else if (vocabSnippet.actionType === 'EDIT_IDENTITY_CV') {
        this.isIdentityEdit = !this.isIdentityEdit;
        this.version!.translateAgency = vocabSnippet.translateAgency;
        this.version!.translateAgencyLink = vocabSnippet.translateAgencyLink;
      } else if (vocabSnippet.actionType === 'EDIT_NOTE_CV') {
        this.isNotesEdit = !this.isNotesEdit;
        this.version!.notes = vocabSnippet.notes;
      } else if (vocabSnippet.actionType === 'EDIT_VERSION_INFO_CV') {
        this.isCurrentVersionInfoEdit = !this.isCurrentVersionInfoEdit;
        this.version!.versionNotes = vocabSnippet.versionNotes;
        this.version!.versionChanges = vocabSnippet.versionChanges;
      }
    });
  }

  closeDdiUsage(): void {
    this.isDdiUsageEdit = false;
    this.editorDetailForm.patchValue({ ddiUsage: this.version!.ddiUsage });
  }

  closeTranslateIdentity(): void {
    this.isIdentityEdit = false;
    this.editorDetailForm.patchValue({ translateAgency: this.version!.translateAgency });
    this.editorDetailForm.patchValue({ translateAgencyLink: this.version!.translateAgencyLink });
  }

  closeNotes(): void {
    this.isNotesEdit = false;
    this.editorDetailForm.patchValue({ notes: this.version!.notes });
  }

  closeCurrentVersionInfo(): void {
    this.isCurrentVersionInfoEdit = false;
    this.editorDetailForm.patchValue({ versionNotes: this.version!.versionNotes, versionChanges: this.version!.versionChanges });
  }

  exportAsCsv(): void {
    // specify how you want to handle null values here
    const header = ['Code value', 'Code term', 'Code definition'];
    const csv = this.version!.concepts!.map(concept =>
      [this.escapeCsvContent(concept.notation), this.escapeCsvContent(concept.title), this.escapeCsvContent(concept.definition)].join(',')
    );
    csv.unshift(header.join(','));

    // #219 - applications such as Excel don't interpret CSVs as UTF-8 unless a byte order mark is present
    const csvString = '\uFEFF' + csv.join('\r\n');

    const a = document.createElement('a');
    const blob = new Blob([csvString], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);

    a.href = url;
    a.download =
      // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
      this.version!.notation +
      '_' +
      this.version!.language +
      '_' +
      this.version!.number +
      '(' +
      this.version!.status +
      ')_' +
      moment().format('YYYY-MM-DD') +
      '.csv';
    a.click();
    window.URL.revokeObjectURL(url);
    a.remove();
  }

  escapeCsvContent(content?: string): string {
    let escapedContent = content === null || content === undefined ? '' : content;
    escapedContent = escapedContent.replace(/\n+$/, '').replace(/"/g, '""');
    if (escapedContent.search(/("|,|\n)/g) >= 0) {
      escapedContent = `"${escapedContent}"`;
    }
    return escapedContent;
  }

  switchLang(): void {
    this.vocabulary!.selectedLang = this.version!.language;
    this.vocabulary!.selectedVersion = this.version!.number;
    this.eventManager.broadcast({ name: 'closeComparison', content: true });
  }

  getMissingTlVersion(version: string): string {
    if (version.startsWith(this.getSlVersion().number!)) {
      return this.getSlVersion().versionHistories![0].version + '.x';
    }
    let i = 0;
    this.getSlVersion().versionHistories!.forEach(function (vhSl, index): void {
      if (version.startsWith(vhSl.version!)) {
        i = index + 1;
      }
    });
    if (i > 0) {
      return this.getSlVersion().versionHistories![i].version + '.x';
    }
    return '';
  }

  // @ts-ignore
  onVersionNotesEditorCreated(event: Quill): void {
    this.versionNotesEditor = event;
    if (this.editorDetailForm.get(['versionNotes'])!.value) {
      // @ts-ignore
      this.versionNotesEditor.clipboard.dangerouslyPasteHTML(this.editorDetailForm.get(['versionNotes']).value);
    }
  }

  // @ts-ignore
  onVersionChangesEditorCreated(event: Quill): void {
    this.versionChangesEditor = event;
    if (this.editorDetailForm.get(['versionChanges'])!.value) {
      // @ts-ignore
      this.versionChangesEditor.clipboard.dangerouslyPasteHTML(this.editorDetailForm.get(['versionChanges']).value);
    }
  }

  // @ts-ignore
  onDdiUsageEditorCreated(event: Quill): void {
    this.ddiUsageEditor = event;
    if (this.editorDetailForm.get(['ddiUsage'])!.value) {
      // @ts-ignore
      this.ddiUsageEditor.clipboard.dangerouslyPasteHTML(this.editorDetailForm.get(['ddiUsage']).value);
    }
  }
}
