import {AfterViewInit, Component, Input, NgZone, OnInit} from '@angular/core';
import {HomeService} from 'app/home/home.service';
import {EditorService} from 'app/editor/editor.service';
import {JhiEventManager} from 'ng-jhipster';
import {AppScope} from 'app/shared/model/enumerations/app-scope.model';
import {IVersion} from 'app/shared/model/version.model';
import {FormGroup} from '@angular/forms';
import VocabularyUtil from 'app/shared/util/vocabulary-util';

@Component({
  selector: 'jhi-vocabulary-download',
  templateUrl: './vocabulary-download.component.html'
})
export class VocabularyDownloadComponent implements OnInit, AfterViewInit {
  @Input() appScope!: AppScope;
  @Input() versions!: IVersion[];
  @Input() slVersionNumber!: string;
  @Input() enableDocxExport!: boolean;
  @Input() downloadFormGroup!: FormGroup;
  @Input() notation!: string;

  downloadCheckboxes: string[];
  skosSelected: boolean[];
  pdfSelected: boolean[];
  htmlSelected: boolean[];
  docxSelected: boolean[];

  constructor(
    private homeService: HomeService,
    private editorService: EditorService,
    protected eventManager: JhiEventManager,
    private _ngZone: NgZone
  ) {
    this.downloadCheckboxes = [];
    this.skosSelected = [];
    this.pdfSelected = [];
    this.htmlSelected = [];
    this.docxSelected = [];
  }

  ngOnInit(): void {

    // initialize download checkbox
    const languages: string[] = this.getUniqueVersionLangs();
    for (let i = 0; i < languages.length; i++) {
      const versions: IVersion[] = this.getVersionsByLang(languages[i]);
      this.downloadCheckboxes[i] = languages[i] + '-' + versions[0].number;
      this.skosSelected[i] = true;
      this.pdfSelected[i] = true;
      this.htmlSelected[i] = true;
      this.docxSelected[i] = true;
    }
    this.downloadFormGroup.patchValue({
      skosItems: this.skosSelected,
      pdfItems: this.pdfSelected,
      htmlItems: this.htmlSelected,
      docxItems: this.docxSelected
    });
  }

  ngAfterViewInit(): void {
    this._ngZone.run(() => {
      this.resetExport();
    });
  }

  resetExport(): void {
    this.skosSelected.fill(false);
    this.pdfSelected.fill(false);
    this.htmlSelected.fill(false);
    this.docxSelected.fill(false);
  }

  getUniqueVersionLangs(): string[]{
    return VocabularyUtil.getUniqueVersionLangs(this.versions, this.appScope);
  }

  getFormattedLanguage(langIso?: string): string {
    return VocabularyUtil.getLangIsoFormatted(langIso);
  }

  getVersionsByLang(lang?: string): IVersion[] {
    return this.versions.filter(v => v.language === lang);
  }

  downloadSkos(): void {
    this.downloadEditorVocabularyFile('rdf', this.getCheckedItems(this.skosSelected), 'text/xml');
  }

  downloadPdf(): void {
    this.downloadEditorVocabularyFile('pdf', this.getCheckedItems(this.pdfSelected), 'application/pdf');
  }

  downloadHtml(): void {
    this.downloadEditorVocabularyFile('html', this.getCheckedItems(this.htmlSelected), 'text/html');
  }

  toggleSelectAll(downloadType: string, checked: boolean): void {
    switch (downloadType) {
      case 'skos':
        this.skosSelected.fill(checked);
        break;
      case 'pdf':
        this.pdfSelected.fill(checked);
        break;
      case 'html':
        this.htmlSelected.fill(checked);
        break;
      case 'docx':
        this.docxSelected.fill(checked);
        break;
    }
  }

  getCheckedItems(checkedArray: boolean[]): string {
    let selectedVersion = '';
    for (let i = 0; i < checkedArray.length; i++) {
      if (checkedArray[i] === true) {
        selectedVersion += this.downloadCheckboxes[i] + '_';
      }
    }
    if (selectedVersion.length > 0) {
      selectedVersion = selectedVersion.substr(0, selectedVersion.length - 1);
    }
    return selectedVersion;
  }

  updateCheckboxValue(i: number, lang: string, versionNimber: string): void {
    this.downloadCheckboxes[i] = lang + '_' + versionNimber;
  }

  downloadDocx(): void {
    this.downloadEditorVocabularyFile(
      'docx',
      this.getCheckedItems(this.docxSelected),
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
    );
  }

  private downloadEditorVocabularyFile(fileFormat: string, checkedItems: string, mimeType: string): void {
    if (this.appScope === AppScope.EDITOR) {
      this.editorService
        .downloadVocabularyFile(this.notation, this.slVersionNumber, fileFormat, {
          lv: checkedItems
        })
        .subscribe((res: Blob) => {
          this.generateDownloadFile(res, mimeType, checkedItems, fileFormat);
        });
    } else {
      this.homeService
        .downloadVocabularyFile(this.notation, this.slVersionNumber, fileFormat, {
          languageVersion: checkedItems
        })
        .subscribe((res: Blob) => {
          this.generateDownloadFile(res, mimeType, checkedItems, fileFormat);
        });
    }
  }

  private generateDownloadFile(res: Blob, mimeType: string, checkedItems: string, fileFormat: string): void {
    const newBlob = new Blob([res], {type: mimeType});
    if (window.navigator && window.navigator.msSaveOrOpenBlob) {
      window.navigator.msSaveOrOpenBlob(newBlob);
      return;
    }
    const data = window.URL.createObjectURL(newBlob);
    const link = document.createElement('a');
    link.href = data;
    link.download = this.notation + '-' + this.slVersionNumber + '_' + checkedItems + '.' + fileFormat;
    link.dispatchEvent(new MouseEvent('click', {bubbles: true, cancelable: true, view: window}));
    setTimeout(function (): void {
      window.URL.revokeObjectURL(data);
      link.remove();
    }, 100);
  }
}