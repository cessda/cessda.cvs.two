import { AfterViewChecked, Component, ElementRef, NgZone, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { JhiDataUtils, JhiEventManager, JhiEventWithContent } from 'ng-jhipster';

import { IVocabulary } from 'app/shared/model/vocabulary.model';
import { IVersion } from 'app/shared/model/version.model';

import VocabularyUtil from 'app/shared/util/vocabulary-util';
import { FormBuilder } from '@angular/forms';
import { EditorService } from 'app/editor/editor.service';
import { RouteEventsService } from 'app/shared';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EditorDetailCvAddEditDialogComponent } from 'app/editor/editor-detail-cv-add-edit-dialog.component';
import { EditorDetailCvDeleteDialogComponent } from 'app/editor/editor-detail-cv-delete-dialog.component';
import { EditorDetailCodeAddEditDialogComponent } from 'app/editor/editor-detail-code-add-edit-dialog.component';
import { Concept, IConcept } from 'app/shared/model/concept.model';
import { Observable, Subscription } from 'rxjs';
import { EditorDetailCodeDeleteDialogComponent } from 'app/editor/editor-detail-code-delete-dialog.component';
import { EditorDetailCodeReorderDialogComponent } from 'app/editor/editor-detail-code-reorder-dialog.component';
import { EditorDetailCvForwardStatusDialogComponent } from 'app/editor/editor-detail-cv-forward-status-dialog.component';
import { IVocabularySnippet, VocabularySnippet } from 'app/shared/model/vocabulary-snippet.model';
import { HttpResponse } from '@angular/common/http';
import { EditorDetailCvNewVersionDialogComponent } from 'app/editor/editor-detail-cv-new-version-dialog.component';

@Component({
  selector: 'jhi-editor-detail',
  templateUrl: './editor-detail.component.html',
  styleUrls: ['editor.scss']
})
export class EditorDetailComponent implements OnInit, OnDestroy {
  @ViewChild('detailPanel', { static: true }) detailPanel!: ElementRef;
  @ViewChild('versionPanel', { static: true }) versionPanel!: ElementRef;
  @ViewChild('identityPanel', { static: true }) identityPanel!: ElementRef;
  @ViewChild('usagePanel', { static: true }) usagePanel!: ElementRef;
  @ViewChild('licensePanel', { static: true }) licensePanel!: ElementRef;
  @ViewChild('exportPanel', { static: true }) exportPanel!: ElementRef;

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

  initialTabSelected?: string;
  initialLangSelect = null;

  dwnldCbVal: string[];
  skosSelected: boolean[];
  pdfSelected: boolean[];
  htmlSelected: boolean[];
  docxSelected: boolean[];

  currentSelectedCode?: string;
  activePopup?: string;

  enableDocxExport = false;

  protected ngbModalRef?: NgbModalRef | null;

  isDdiUsageEdit = false;
  isNotesEdit = false;

  codeTlActionRoles = ['ADMIN', 'ADMIN_TL', 'CONTRIBUTOR_TL'];

  quillModules: any = {
    toolbar: [
      ['bold', 'italic', 'underline', 'strike'],
      ['blockquote'],
      [{ list: 'ordered' }, { list: 'bullet' }],
      [{ header: [1, 2, 3, 4, 5, 6, false] }],
      [{ color: [] }, { background: [] }],
      ['link'],
      ['clean']
    ]
  };

  detailForm = this.fb.group({
    tabSelected: [],
    skosItems: [],
    pdfItems: [],
    htmlItems: [],
    docxItems: [],
    ddiUsage: [],
    notes: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected editorService: EditorService,
    private fb: FormBuilder,
    private routeEventsService: RouteEventsService,
    private _ngZone: NgZone,
    protected modalService: NgbModal,
    protected eventManager: JhiEventManager
  ) {
    this.initialTabSelected = 'detail';
    this.dwnldCbVal = [];
    this.skosSelected = [];
    this.pdfSelected = [];
    this.htmlSelected = [];
    this.docxSelected = [];
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
    return VocabularyUtil.getSlVersion(this.vocabulary!);
  }

  setActiveVersion(language: string, versionNumber?: string): void {
    this.vocabulary!.selectedLang = language;
    this.vocabulary!.selectedVersion = versionNumber;
    this.version = VocabularyUtil.getVersionByLangAndNumber(this.vocabulary!, versionNumber);
    this.detailForm.patchValue({ ddiUsage: this.version.ddiUsage, notes: this.version.notes });

    if (this.version.status === 'FINAL_REVIEW') {
      this.codeTlActionRoles = ['ADMIN', 'ADMIN_TL'];
    } else {
      this.codeTlActionRoles = ['ADMIN', 'ADMIN_TL', 'CONTRIBUTOR_TL'];
    }
    // make sure that version TL concepts follows the version SL concept in structure and some properties
    if (this.version.itemType === 'TL') {
      this.enableAddTl = false;
      // find SL version
      const slVersion = this.getSlVersion();
      this.version.languageSl = slVersion.language;

      const slConcepts = slVersion.concepts;

      for (let i = 0; i < slConcepts!.length; i++) {
        const tlCodes = this.version.concepts!.filter(c => c.notation === slConcepts![i].notation);
        if (tlCodes.length) {
          tlCodes[0].position = slConcepts![i].position;
          (tlCodes[0].parent = slConcepts![i].parent === undefined ? undefined : slConcepts![i].parent),
            (tlCodes[0].titleSl = slConcepts![i].title);
          tlCodes[0].definitionSl = slConcepts![i].definition;
        } else {
          // TL code not found, generate new one from SL
          const nonExistTlCode = {
            ...new Concept(),
            position: slConcepts![i].position,
            parent: slConcepts![i].parent === undefined ? undefined : slConcepts![i].parent,
            notation: slConcepts![i].notation,
            titleSl: slConcepts![i].title,
            definitionSl: slConcepts![i].definition,
            slConcept: slConcepts![i].id
          };
          // insert
          this.version.concepts!.splice(i, 0, nonExistTlCode);
        }
      }
    } else {
      if (this.version.status === 'PUBLISHED') {
        this.enableAddTl = true;
      }
    }
    // remove selection on version change
    this.eventManager.broadcast('deselectConcept');
  }

  getFormattedLang(langIso?: string): string {
    return VocabularyUtil.getLangIsoFormatted(langIso);
  }

  getUniqueVersionLang(): string[] {
    const uniqueLang: string[] = [];
    this.vocabulary!.versions!.forEach(v => {
      uniqueLang.push(v.language!);
    });
    return [...new Set(uniqueLang)];
  }

  getVersionsByLanguage(lang?: string): IVersion[] {
    return this.vocabulary!.versions!.filter(v => v.language === lang);
  }
  getFormattedVersionTooltip(version?: IVersion, sourceLang?: string): string {
    return (
      VocabularyUtil.getLangIsoFormatted(version!.language) +
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
        this.setActiveVersion(this.initialLangSelect!);
      } else {
        this.setActiveVersion(this.vocabulary!.selectedLang!);
      }
      // build download checkbox
      const langs: string[] = this.getUniqueVersionLang();
      for (let i = 0; i < langs.length; i++) {
        const versions: IVersion[] = this.getVersionsByLanguage(langs[i]);
        this.dwnldCbVal[i] = langs[i] + '-' + versions[0].number;
        const cbInitialValue = langs[i] === this.vocabulary!.sourceLanguage;
        this.skosSelected[i] = cbInitialValue;
        this.pdfSelected[i] = cbInitialValue;
        this.htmlSelected[i] = cbInitialValue;
        this.docxSelected[i] = cbInitialValue;
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

    this.detailForm.patchValue({ tabSelected: this.initialTabSelected });

    this.subscribeSelectConceptEvent();

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

  private subscribeSelectConceptEvent(): void {
    this.eventSubscriber = this.eventManager.subscribe('selectConcept', (response: JhiEventWithContent<IConcept>) => {
      this.concept = response.content;
    });
    this.eventSubscriber2 = this.eventManager.subscribe('deselectConcept', () => {
      this.concept = null;
    });
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
    this.editorService
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
      backdrop: 'static'
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

  openAddCodeAsRootPopup(): void {
    this.openAddEditCodePopup(true, 'INSERT_AS_ROOT');
  }

  openAddCodeAsChildPopup(): void {
    this.openAddEditCodePopup(true, 'INSERT_AS_CHILD');
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

  openDeleteCodePopup(): void {
    this.openDeleteCodeWindow();
  }

  openDeleteCodeWindow(): void {
    this.ngbModalRef = this.modalService.open(EditorDetailCodeDeleteDialogComponent as Component, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.componentInstance.versionParam = this.version;
    this.ngbModalRef.componentInstance.conceptParam = this.concept;
    this.ngbModalRef.componentInstance.isSlForm = this.version!.itemType === 'SL';
  }

  openReorderCode(): void {
    this.ngbModalRef = this.modalService.open(EditorDetailCodeReorderDialogComponent as Component, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.componentInstance.versionParam = this.version;
    this.ngbModalRef.componentInstance.conceptParam = this.concept;
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
      ddiUsage: this.detailForm.get(['ddiUsage'])!.value
    };
    this.subscribeToSaveResponse(this.editorService.updateVocabulary(vocabSnippet), vocabSnippet);
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
      notes: this.detailForm.get(['notes'])!.value
    };
    this.subscribeToSaveResponse(this.editorService.updateVocabulary(vocabSnippet), vocabSnippet);
  }

  subscribeToSaveResponse(result: Observable<HttpResponse<IVocabulary>>, vocabSnippet: IVocabularySnippet): void {
    result.subscribe(() => {
      if (vocabSnippet.actionType === 'EDIT_DDI_CV') {
        this.isDdiUsageEdit = !this.isDdiUsageEdit;
        this.version!.ddiUsage = vocabSnippet.ddiUsage;
      } else if (vocabSnippet.actionType === 'EDIT_NOTE_CV') {
        this.isNotesEdit = !this.isNotesEdit;
        this.version!.notes = vocabSnippet.notes;
      }
    });
  }
}
