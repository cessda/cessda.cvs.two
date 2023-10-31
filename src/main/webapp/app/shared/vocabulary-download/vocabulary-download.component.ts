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
import { AfterViewInit, Component, Input, NgZone, OnInit } from '@angular/core';
import { HomeService } from 'app/home/home.service';
import { EditorService } from 'app/editor/editor.service';
import { JhiEventManager } from 'ng-jhipster';
import { AppScope } from 'app/shared/model/enumerations/app-scope.model';
import { Version } from 'app/shared/model/version.model';
import { FormGroup } from '@angular/forms';
import VocabularyUtil from 'app/shared/util/vocabulary-util';
import { Router } from '@angular/router';

@Component({
  selector: 'jhi-vocabulary-download',
  templateUrl: './vocabulary-download.component.html',
})
export class VocabularyDownloadComponent implements OnInit, AfterViewInit {
  @Input() appScope!: AppScope;
  @Input() versions!: Version[];
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
    private router: Router,
    private homeService: HomeService,
    private editorService: EditorService,
    protected eventManager: JhiEventManager,
    private _ngZone: NgZone,
  ) {
    this.downloadCheckboxes = [];
    this.skosSelected = [];
    this.pdfSelected = [];
    this.htmlSelected = [];
    this.docxSelected = [];
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
  }

  ngOnInit(): void {
    // initialize download checkbox
    const languages: string[] = this.getUniqueVersionLangs();
    for (let i = 0; i < languages.length; i++) {
      const versions: Version[] = this.getVersionsByLang(languages[i]);
      if (!versions[0].number!.startsWith(this.getSlMajorMinorVersionNumber(this.slVersionNumber))) {
        continue;
      }
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
      docxItems: this.docxSelected,
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

  getUniqueVersionLangs(): string[] {
    return VocabularyUtil.getUniqueVersionLangs(this.versions, this.appScope);
  }

  getVersionsByLang(lang?: string): Version[] {
    return this.versions.filter(v => v.language === lang);
  }

  getSlMajorMinorVersionNumber(vnumber?: string): string {
    if (vnumber) {
      return VocabularyUtil.getSlMajorMinorVersionNumber(vnumber);
    } else {
      return VocabularyUtil.getSlMajorMinorVersionNumber(this.slVersionNumber);
    }
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

  toggleSelectAll(downloadType: string, event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
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

  updateCheckboxValue(i: number, lang: string, event: Event): void {
    const versionNumber = (event.target as HTMLInputElement).value;
    this.downloadCheckboxes[i] = lang + '-' + versionNumber;
  }

  updateSelection(array: boolean[], i: number, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    array[i] = checked;
  }

  downloadDocx(): void {
    this.downloadEditorVocabularyFile(
      'docx',
      this.getCheckedItems(this.docxSelected),
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    );
  }

  private downloadEditorVocabularyFile(fileFormat: string, checkedItems: string, mimeType: string): void {
    if (this.appScope === AppScope.EDITOR) {
      this.editorService
        .downloadVocabularyFile(this.notation, this.slVersionNumber, fileFormat, {
          lv: checkedItems,
        })
        .subscribe((res: Blob) => {
          this.generateDownloadFile(res, mimeType, checkedItems, fileFormat);
        });
    } else {
      this.homeService
        .downloadVocabularyFile(this.notation, this.slVersionNumber, fileFormat, {
          languageVersion: checkedItems,
        })
        .subscribe((res: Blob) => {
          this.generateDownloadFile(res, mimeType, checkedItems, fileFormat);
        });
    }
  }

  private generateDownloadFile(res: Blob, mimeType: string, checkedItems: string, fileFormat: string): void {
    const newBlob = new Blob([res], { type: mimeType });
    if (window.navigator && (window.navigator as any).msSaveOrOpenBlob) {
      (window.navigator as any).msSaveOrOpenBlob(newBlob);
      return;
    }
    const data = window.URL.createObjectURL(newBlob);
    const link = document.createElement('a');
    link.href = data;
    link.download = this.notation + '-' + this.slVersionNumber + '_' + checkedItems + '.' + fileFormat;
    link.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, view: window }));
    setTimeout(function (): void {
      window.URL.revokeObjectURL(data);
      link.remove();
    }, 100);
  }
}
