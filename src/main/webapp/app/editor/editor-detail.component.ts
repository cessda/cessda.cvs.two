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

import { createNewVocabulary, Vocabulary } from 'app/shared/model/vocabulary.model';
import { Version } from 'app/shared/model/version.model';

import VocabularyUtil from 'app/shared/util/vocabulary-util';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { EditorService } from 'app/editor/editor.service';
import { RouteEventsService } from 'app/shared';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EditorDetailCvAddEditDialogComponent } from 'app/editor/editor-detail-cv-add-edit-dialog.component';
import { EditorDetailCvCommentDialogComponent } from 'app/editor/editor-detail-cv-comment-dialog.component';
import { EditorDetailCvDeleteDialogComponent } from 'app/editor/editor-detail-cv-delete-dialog.component';
import { EditorDetailCodeAddEditDialogComponent } from 'app/editor/editor-detail-code-add-edit-dialog.component';
import { Concept } from 'app/shared/model/concept.model';
import { Observable, Subscription } from 'rxjs';
import { EditorDetailCodeCsvImportDialogComponent } from 'app/editor/editor-detail-code-csv-import-dialog.component';
import { EditorDetailCodeDeprecateDialogComponent } from 'app/editor/editor-detail-code-deprecate-dialog.component';
import { EditorDetailCodeDeleteDialogComponent } from 'app/editor/editor-detail-code-delete-dialog.component';
import { EditorDetailCodeReorderDialogComponent } from 'app/editor/editor-detail-code-reorder-dialog.component';
import { EditorDetailCvForwardStatusDialogComponent } from 'app/editor/editor-detail-cv-forward-status-dialog.component';
import { VocabularySnippet } from 'app/shared/model/vocabulary-snippet.model';
import { HttpResponse } from '@angular/common/http';
import { EditorDetailCvNewVersionDialogComponent } from 'app/editor/editor-detail-cv-new-version-dialog.component';
import moment from 'moment';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { AppScope } from 'app/shared/model/enumerations/app-scope.model';
import { VocabularyLanguageFromKeyPipe } from 'app/shared';
import { Quill } from 'quill';
import { QuillModules } from 'ngx-quill';
import { AgencyRole } from 'app/shared/model/enumerations/agency-role.model';
import { ActionType } from 'app/shared/model/enumerations/action-type.model';

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

  selectConceptSubscription?: Subscription;
  eventSubscriber2?: Subscription;

  vocabulary: Vocabulary = createNewVocabulary();
  version: Version | null = null;
  concept: Concept | null = null;

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
  initialLangSelect: string | null = null;

  availableLanguages: string[] = [];
  availableTlLanguages: string[] = [];
  tlLanguageStatus = 'none';

  currentSelectedCode?: string;
  activePopup?: string;

  enableDocxExport = false;

  protected ngbModalRef: NgbModalRef | null = null;

  isCurrentVersionInfoEdit = false;
  isDdiUsageEdit = false;
  isIdentityEdit = false;
  isNotesEdit = false;

  isShowingDeprecatedCodes = false;

  codeTlActionRoles: AgencyRole[] = ['ADMIN', 'ADMIN_TL', 'ADMIN_CONTENT'];

  noOfComments = 0;

  quillModules: QuillModules = {
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

  downloadFormGroup = this.fb.group({
    skosItems: [],
    pdfItems: [],
    htmlItems: [],
    docxItems: [],
  });

  editorDetailForm = this.fb.group({
    tabSelected: new FormControl<string | null>(null),
    downloadFormGroup: this.downloadFormGroup,
    ddiUsage: new FormControl<string | null>(null),
    translateAgency: new FormControl<string | null>(null),
    translateAgencyLink: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.pattern(
          '(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})',
        ),
      ],
    }),
    notes: new FormControl<string | null>(null),
    versionNotes: new FormControl<string | null>(null),
    versionChanges: new FormControl<string | null>(null),
  });

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
    private vocabLangPipeKey: VocabularyLanguageFromKeyPipe,
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

  getSlVersion(): Version {
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
    this.vocabulary.selectedLang = language;
    this.vocabulary.selectedVersion = versionNumber;
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

    this.noOfComments = this.version.comments ? this.version.comments.length : 0;

    this.codeTlActionRoles = ['ADMIN', 'ADMIN_TL', 'ADMIN_CONTENT'];

    // make sure that version TL concepts follows the version SL concept in structure and some properties
    if (this.version.itemType === 'TL') {
      this.enableAddTl = false;
      this.enablePublishTl = false;

      // find SL version
      const slVersion = this.getSlVersion();
      this.version.languageSl = slVersion.language;

      const slConcepts = slVersion.concepts;

      for (let i = 0; i < slConcepts.length; i++) {
        const tlCodes = this.version.concepts.filter(c => c.notation === slConcepts[i].notation);
        if (tlCodes.length) {
          tlCodes[0].position = slConcepts[i].position;
          tlCodes[0].parent = slConcepts[i].parent ? slConcepts[i].parent : undefined;
          tlCodes[0].titleSl = slConcepts[i].title;
          tlCodes[0].definitionSl = slConcepts[i].definition;
        } else {
          // TL code not found, generate new one from SL
          const nonExistTlCode: Concept = {
            visible: true,
            position: slConcepts[i].position,
            parent: slConcepts[i].parent ? slConcepts[i].parent : undefined,
            notation: slConcepts[i].notation,
            titleSl: slConcepts[i].title,
            definitionSl: slConcepts[i].definition,
            slConcept: slConcepts[i].id,
          };
          // insert
          this.version.concepts.splice(i, 0, nonExistTlCode);
        }
      }
    } else {
      if (this.version.status === 'READY_TO_TRANSLATE' || this.version.status === 'PUBLISHED') {
        this.enableAddTl = true;
      } else {
        this.enableAddTl = false;
      }
      // check all versions if there is any READY_TO_PUBLISH state for TL
      for (const vocab of this.vocabulary?.versions || []) {
        if (vocab.status === 'READY_TO_PUBLISH') {
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

    if (!this.vocabulary?.versions) {
      return [];
    }

    this.vocabulary.versions.forEach(v => {
      if (!uniqueLang.some(l => l === v.language)) {
        uniqueLang.push(v.language!);
      }
    });
    return VocabularyUtil.sortLangByEnum(uniqueLang, uniqueLang[0]);
  }

  getVersionsByLanguage(lang?: string): Version[] {
    if (!this.vocabulary?.versions) {
      return [];
    }

    return this.vocabulary.versions.filter(v => v.language === lang);
  }

  getFormattedVersionTooltip(version: Version, sourceLang?: string): string {
    return (
      this.vocabLangPipeKey.transform(version.language!) +
      ' v.' +
      (version.status === 'PUBLISHED' ? '' : version.status + '-') +
      version.number +
      (version.language === sourceLang ? ' SOURCE' : '')
    );
  }

  getServerUrl(): string {
    return window.location.origin;
  }

  isVersionStatus(lang: string, versionType: string): boolean {
    const version = this.getVersionsByLanguage(lang)[0];
    return version.status === versionType;
  }

  isAnyLangVersionInBundle(vocab: Vocabulary, lang: string, bundle?: string): boolean {
    if (bundle === undefined) {
      bundle = vocab.versionNumber;
    }
    const versions = this.getVersionsByLanguage(lang);
    return VocabularyUtil.isAnyVersionInBundle(versions, bundle);
  }

  hasDeprecatedConcepts(concepts: Concept[]): boolean {
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
      this.vocabulary = vocabulary as Vocabulary;
      this.availableLanguages = VocabularyUtil.getAvailableCvLanguage(vocabulary.versions);
      this.accountService.identity().subscribe(account => {
        if (account) {
          this.account = account;
          this.account.userAgencies.forEach(ua => {
            if (
              ua.agencyRole === 'ADMIN_TL' &&
              ua.agencyId === this.vocabulary.agencyId &&
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
        if (!this.vocabulary.versions.some(v => v.language === this.initialLangSelect)) {
          this.setActiveVersion(this.vocabulary.sourceLanguage);
        } else {
          this.setActiveVersion(this.initialLangSelect);
        }
      } else {
        this.setActiveVersion(this.vocabulary.selectedLang);
      }
      if (this.vocabulary.selectedVersion) {
        if (!this.vocabulary.selectedLang) {
          this.vocabulary.selectedVersion = VocabularyUtil.getVersionByLang(this.vocabulary).number;
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
      this.isShowingDeprecatedCodes = this.version!.concepts.some(concept => {
        return concept.deprecated;
      });
      this._ngZone.runOutsideAngular(() => {
        setTimeout(() => {
          const element = document.querySelector('#code_' + this.currentSelectedCode);

          if (!element) {
            return;
          }

          element.scrollIntoView({ behavior: 'smooth' });
          element.classList.add('highlight');
          this._ngZone.runOutsideAngular(() => {
            window.setTimeout(() => {
              element.classList.remove('highlight');
            }, 5000);
          });
        }, 1500);
      });
    }
  }

  private subscribeSelectConceptEvent(): void {
    this.selectConceptSubscription = this.eventManager.subscribe('selectConcept', (response: JhiEventWithContent<Concept>) => {
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
    this.ngbModalRef = this.modalService.open(EditorDetailCvAddEditDialogComponent, { size: 'xl', backdrop: 'static' });
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
    this.ngbModalRef = this.modalService.open(EditorDetailCvForwardStatusDialogComponent, {
      size: 'xl',
      backdrop: 'static',
    });
    this.ngbModalRef.componentInstance.vocabularyParam = this.vocabulary;
    this.ngbModalRef.componentInstance.versionParam = this.version;
    this.ngbModalRef.componentInstance.isSlForm = this.version?.itemType === 'SL';
    const slVersion = this.getSlVersion();
    this.ngbModalRef.componentInstance.slVersionNumber = slVersion.number;
  }

  openCreateNewCvVersionPopup(): void {
    this.ngbModalRef = this.modalService.open(EditorDetailCvNewVersionDialogComponent, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.componentInstance.vocabularyParam = this.vocabulary;
    this.ngbModalRef.componentInstance.versionParam = this.version;
  }

  openDeleteCvPopup(): void {
    this.ngbModalRef = this.modalService.open(EditorDetailCvDeleteDialogComponent, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.componentInstance.vocabularyParam = this.vocabulary;
    this.ngbModalRef.componentInstance.versionParam = this.version;
  }

  ngOnDestroy(): void {
    this.ngbModalRef = null;
    if (this.selectConceptSubscription) {
      this.selectConceptSubscription.unsubscribe();
    }
    if (this.eventSubscriber2) {
      this.eventSubscriber2.unsubscribe();
    }
  }

  openAddEditCodePopup(isNew: boolean, codeInsertMode?: string): void {
    if (!this.concept) {
      throw new TypeError('concept was null');
    }

    if (!this.version) {
      throw new TypeError('version was null');
    }

    this.ngbModalRef = this.modalService.open(EditorDetailCodeAddEditDialogComponent, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.componentInstance.conceptParam = this.concept;
    this.ngbModalRef.componentInstance.versionParam = this.version;
    this.ngbModalRef.componentInstance.isNew = isNew;
    this.ngbModalRef.componentInstance.codeInsertMode = codeInsertMode;
    this.ngbModalRef.componentInstance.isSlForm = this.version.itemType === 'SL';
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
    this.ngbModalRef = this.modalService.open(EditorDetailCodeDeprecateDialogComponent, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.componentInstance.versionParam = this.version;
    this.ngbModalRef.componentInstance.conceptParam = this.concept;
    this.ngbModalRef.componentInstance.isSlForm = this.version!.itemType === 'SL';
    this.ngbModalRef.componentInstance.conceptList = this.version!.concepts;
  }

  openDeleteCodePopup(): void {
    this.openDeleteCodeWindow();
  }

  openDeleteCodeWindow(): void {
    this.ngbModalRef = this.modalService.open(EditorDetailCodeDeleteDialogComponent, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.componentInstance.versionParam = this.version;
    this.ngbModalRef.componentInstance.conceptParam = this.concept;
    this.ngbModalRef.componentInstance.isSlForm = this.version!.itemType === 'SL';
  }

  openCsvImportCodeWindow(): void {
    this.ngbModalRef = this.modalService.open(EditorDetailCodeCsvImportDialogComponent, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.componentInstance.vocabularyParam = this.vocabulary;
    this.ngbModalRef.componentInstance.versionParam = this.version;
    this.ngbModalRef.componentInstance.isSlForm = this.version!.itemType === 'SL';
  }

  openReorderCode(): void {
    this.ngbModalRef = this.modalService.open(EditorDetailCodeReorderDialogComponent, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.componentInstance.versionParam = this.version;
    this.ngbModalRef.componentInstance.conceptParam = this.concept;
  }

  openCvCommentPopup(): void {
    this.ngbModalRef = this.modalService.open(EditorDetailCvCommentDialogComponent, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.componentInstance.vocabularyParam = this.vocabulary;
    this.ngbModalRef.componentInstance.versionParam = this.version;
  }

  saveDdiUsage(): void {
    const vocabSnippet: VocabularySnippet = {
      actionType: ActionType.EDIT_DDI_CV,
      agencyId: this.vocabulary.agencyId,
      vocabularyId: this.vocabulary.id,
      versionId: this.version!.id,
      language: this.version!.language,
      itemType: this.version!.itemType,
      ddiUsage: this.editorDetailForm.controls.ddiUsage.value || undefined,
    };
    this.subscribeToSaveResponse(this.editorService.updateVocabulary(vocabSnippet), vocabSnippet);
  }

  saveTranslateIdentity(): void {
    const vocabSnippet: VocabularySnippet = {
      actionType: ActionType.EDIT_IDENTITY_CV,
      agencyId: this.vocabulary!.agencyId,
      vocabularyId: this.vocabulary!.id,
      versionId: this.version!.id,
      language: this.version!.language,
      itemType: this.version!.itemType,
      translateAgency: this.editorDetailForm.controls.translateAgency.value || undefined,
      translateAgencyLink: this.editorDetailForm.controls.translateAgencyLink.value,
    };

    if (this.editorDetailForm.valid) {
      this.subscribeToSaveResponse(this.editorService.updateVocabulary(vocabSnippet), vocabSnippet);
    }
  }

  saveNotes(): void {
    const vocabSnippet: VocabularySnippet = {
      actionType: ActionType.EDIT_NOTE_CV,
      agencyId: this.vocabulary!.agencyId,
      vocabularyId: this.vocabulary!.id,
      versionId: this.version!.id,
      language: this.version!.language,
      itemType: this.version!.itemType,
      notes: this.editorDetailForm.controls.notes.value || undefined,
    };
    this.subscribeToSaveResponse(this.editorService.updateVocabulary(vocabSnippet), vocabSnippet);
  }

  saveCurrentVersionInfo(): void {
    const vocabSnippet: VocabularySnippet = {
      actionType: ActionType.EDIT_VERSION_INFO_CV,
      agencyId: this.vocabulary?.agencyId,
      vocabularyId: this.vocabulary?.id,
      versionId: this.version?.id,
      language: this.version?.language,
      itemType: this.version?.itemType,
      versionNotes: this.editorDetailForm.controls.versionNotes.value || undefined,
      versionChanges: this.editorDetailForm.controls.versionChanges.value || undefined,
    };
    this.subscribeToSaveResponse(this.editorService.updateVocabulary(vocabSnippet), vocabSnippet);
  }

  subscribeToSaveResponse(result: Observable<HttpResponse<Vocabulary>>, vocabSnippet: VocabularySnippet): void {
    result.subscribe(() => {
      if (vocabSnippet.actionType === ActionType.EDIT_DDI_CV) {
        this.isDdiUsageEdit = !this.isDdiUsageEdit;
        this.version!.ddiUsage = vocabSnippet.ddiUsage;
      } else if (vocabSnippet.actionType === ActionType.EDIT_IDENTITY_CV) {
        this.isIdentityEdit = !this.isIdentityEdit;
        this.version!.translateAgency = vocabSnippet.translateAgency;
        this.version!.translateAgencyLink = vocabSnippet.translateAgencyLink;
      } else if (vocabSnippet.actionType === ActionType.EDIT_NOTE_CV) {
        this.isNotesEdit = !this.isNotesEdit;
        this.version!.notes = vocabSnippet.notes;
      } else if (vocabSnippet.actionType === ActionType.EDIT_VERSION_INFO_CV) {
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
    const csv = this.version!.concepts.map(concept =>
      [this.escapeCsvContent(concept.notation), this.escapeCsvContent(concept.title), this.escapeCsvContent(concept.definition)].join(','),
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
    if (!this.version || !this.version.language) {
      return;
    }
    this.vocabulary.selectedLang = this.version.language;
    this.vocabulary.selectedVersion = this.version.number;
    this.eventManager.broadcast({ name: 'closeComparison', content: true });
  }

  getMissingTlVersion(version: string): string {
    if (version.startsWith(this.getSlVersion().number!)) {
      return this.getSlVersion().versionHistories[0].version + '.x';
    }
    let i = 0;
    this.getSlVersion().versionHistories.forEach(function (vhSl, index): void {
      if (version.startsWith(vhSl.version)) {
        i = index + 1;
      }
    });
    if (i > 0) {
      return this.getSlVersion().versionHistories[i].version + '.x';
    }
    return '';
  }

  onVersionNotesEditorCreated(quill: Quill): void {
    if (this.editorDetailForm.controls.versionNotes!.value) {
      quill.clipboard.dangerouslyPasteHTML(this.editorDetailForm.controls.versionNotes!.value);
    }
  }

  onVersionChangesEditorCreated(quill: Quill): void {
    if (this.editorDetailForm.controls.versionChanges!.value) {
      quill.clipboard.dangerouslyPasteHTML(this.editorDetailForm.controls.versionChanges!.value);
    }
  }

  onDdiUsageEditorCreated(quill: Quill): void {
    if (this.editorDetailForm.get(['ddiUsage'])!.value) {
      quill.clipboard.dangerouslyPasteHTML(this.editorDetailForm.get(['ddiUsage'])!.value);
    }
  }
}
