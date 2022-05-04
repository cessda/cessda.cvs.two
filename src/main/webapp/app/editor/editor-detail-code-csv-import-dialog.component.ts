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

import {Component, ElementRef, ViewChild} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {EditorService} from 'app/editor/editor.service';
import {IVersion} from 'app/shared/model/version.model';
import {Router} from '@angular/router';
import {Concept, IConcept} from 'app/shared/model/concept.model';
import {Subscription} from 'rxjs';
import {JhiEventManager} from 'ng-jhipster';
import {CodeSnippet, ICodeSnippet} from 'app/shared/model/code-snippet.model';
import {HttpHeaders, HttpResponse} from '@angular/common/http';
import {IVocabulary} from 'app/shared/model/vocabulary.model';

@Component({
  templateUrl: './editor-detail-code-csv-import-dialog.component.html'
})
export class EditorDetailCodeCsvImportDialogComponent {
  @ViewChild('csvInput', { static: true }) csvInput!: ElementRef;
  isSaving: boolean;

  vocabularyParam!: IVocabulary;
  versionParam!: IVersion;
  concepts: IConcept[] = [];
  codeSnippets: ICodeSnippet[] = [];
  eventSubscriber?: Subscription;
  isSlForm?: boolean;
  csvHeaders: any[] = [];
  csvContents: any[] = [];
  markedRows: boolean[] = [];

  // UPLOAD ~ file upload visible
  // SELECT ~ chose which dataset to import
  // IMPORT ~ import codes
  // RESULT ~ importing codes completed
  csvImportWorkflow?: string;
  importAll: boolean;
  isImportError: boolean;
  resultInfo?: string;
  resultBody: IConcept[] = [];
  ignoredRows: number;

  constructor(
    protected editorService: EditorService,
    public activeModal: NgbActiveModal,
    private router: Router,
    protected eventManager: JhiEventManager
  ) {
    this.csvImportWorkflow = 'UPLOAD';
    this.importAll = true;
    this.isImportError = false;
    this.isSaving = false;
    this.ignoredRows = 0;
  }

  clear(): void {
    this.activeModal.dismiss();
  }

  onFileLoad(fileLoadedEvent: any): void {
    const textFromFileLoaded = fileLoadedEvent.target.result;
    this.csvContents = textFromFileLoaded;
    alert(this.csvContents);
  }

  onFileSelect(input: EventTarget): void {
    this.resetConceptsAndCodeSnippets();
    const file = (input as HTMLInputElement).files![0];
    const fileReader = new FileReader();
    fileReader.readAsText(file, 'UTF-8');
    this.importAll = true;
    this.isImportError = false;
    fileReader.onload = () => {
      const csvData = fileReader.result;
      const csvRecordsArray = this.parseCSVToArray(csvData as string);
      this.csvHeaders = this.getHeaderArray(csvRecordsArray);
      this.csvContents = this.getDataRecordsArrayFromCSVFile(csvRecordsArray);
      this.markedRows = new Array(this.csvContents.length);
      this.fillIsRowImported();
      if (this.csvContents.length === 0) {
        this.isImportError = true;
      } else {
        this.csvImportWorkflow = 'SELECT';
      }
    };
  }

  private resetConceptsAndCodeSnippets(): void {
    if (this.versionParam.concepts!.length > 0) {
      // deep copy so the changes will not influence the original concepts
      this.concepts = this.versionParam.concepts!.map(x => Object.assign({ ...x }));
    } else {
      this.concepts = [];
    }
    this.codeSnippets = [];
  }

  backToUploadStage($element: any): void {
    this.csvImportWorkflow = 'UPLOAD';
    this.csvInput.nativeElement.value = '';
    $element.scrollIntoView({ behavior: 'smooth', block: 'start', inline: 'nearest' });
  }

  backToSelectStage($element: any): void {
    this.csvImportWorkflow = 'SELECT';
    $element.scrollIntoView({ behavior: 'smooth', block: 'start', inline: 'nearest' });
  }

  fillIsRowImported(): void {
    this.markedRows.fill(this.importAll);
  }

  // see https://www.bennadel.com/blog/1504-ask-ben-parsing-csv-strings-with-javascript-exec-regular-expression-command.htm
  parseCSVToArray (csvString: string, delimiter?: string): any[] {
    delimiter = (delimiter || ','); // user-supplied delimiter or default comma

    const pattern = new RegExp( // regular expression to parse the CSV values.
      ( // Delimiters:
        "(\\" + delimiter + "|\\r?\\n|\\r|^)" +
        // Quoted fields.
        "(?:\"([^\"]*(?:\"\"[^\"]*)*)\"|" +
        // Standard fields.
        "([^\"\\" + delimiter + "\\r\\n]*))"
      ), "gi"
    );

    const rows: string[][] = [[]];  // array to hold our data. First row is column headers.

    // #316 -->
    if (!csvString) {
      return rows;
    }
    csvString = csvString.trim();
    // <-- #316

    // array to hold our individual pattern matching groups:
    let matches; // false if we don't find any matches
    // Loop until we no longer find a regular expression match
    while ((matches = pattern.exec( csvString )) !== null) {
      const matchedDelimiter = matches[1]; // Get the matched delimiter
      // Check if the delimiter has a length (and is not the start of string)
      // and if it matches field delimiter. If not, it is a row delimiter.
      if (matchedDelimiter.length && matchedDelimiter !== delimiter) {
        // Since this is a new row of data, add an empty row to the array.
        rows.push( [] );
      }
      let matchedValue;
      // Once we have eliminated the delimiter, check to see
      // what kind of value was captured (quoted or unquoted):
      if (matches[2]) { // found quoted value. unescape any double quotes.
        matchedValue = matches[2].replace(
          new RegExp( "\"\"", "g" ), "\""
        );
      } else { // found a non-quoted value
        matchedValue = matches[3];
      }
      // Now that we have our value string, let's add
      // it to the data array.
      rows[rows.length - 1].push(matchedValue);
    }
    return rows; // Return the parsed data Array
  }

  getDataRecordsArrayFromCSVFile(csvRecordsArray: any): any {
    // TL only allow existing code notations
    const existingCode: string[] = [];
    this.ignoredRows = 0;
    if (!this.isSlForm) {
      const slVersion = this.vocabularyParam.versions!.filter(v => v.itemType === 'SL')[0];
      slVersion.concepts!.forEach(c => {
        existingCode.push(c.notation!);
      });
    }

    const csvArr = [];
    for (let i = 1; i < csvRecordsArray.length; i++) {
      const splittedContent = csvRecordsArray[i];
      if (splittedContent && splittedContent.length > 2 && splittedContent[0].trim() !== '' && splittedContent[1].trim() !== '') {
        if (!this.isSlForm && !existingCode.some(notation => notation === splittedContent[0])) {
          this.ignoredRows++;
          continue;
        }
        csvArr.push([splittedContent[0], splittedContent[1], splittedContent[2]]);
      }
    }
    return csvArr;
  }

  getHeaderArray(csvRecordsArr: any): any {
    const headers = csvRecordsArr[0];
    const headerArray = new Array(3).fill('');
    for (let j = 0; j < headers.length; j++) {
      headerArray[j] = headers[j];
    }
    return headerArray;
  }

  createCodePreview($element: any): void {
    this.resetConceptsAndCodeSnippets();

    for (let i = 0; i < this.csvContents.length; i++) {
      if (this.markedRows[i] === false) {
        continue;
      }
      // check for existing concepts, overwrite if exist
      if (this.concepts.some(c => c.notation === this.csvContents[i][0])) {
        const concept = this.concepts.filter(c => c.notation === this.csvContents[i][0])[0];
        concept.title = this.csvContents[i][1];
        concept.definition = this.csvContents[i][2];
        this.codeSnippets.push(this.editCodeFromConcept(concept));
      } else {
        let conceptPosition = this.concepts.length;
        let parentConcept: IConcept | undefined;
        const parentIndex = this.csvContents[i][0].lastIndexOf('.');
        if (parentIndex > 1) {
          // parent need at least 2 character
          const parent = this.csvContents[i][0].substring(0, parentIndex);
          // there is no parent on the
          if (!this.concepts.some(c => c.notation === parent)) {
            continue;
          }
          // find parent
          parentConcept = this.concepts.filter(c => c.notation === parent)[0];
          // calculate conceptPosition as last child of parent
          const conceptChildren = this.concepts.filter(c => c.parent === parentConcept!.notation);
          if (conceptChildren.length === 0) {
            conceptPosition = parentConcept.position! + 1;
          } else {
            conceptPosition = this.findLastChildForPosition(parentConcept).position! + 1;
            // check for equal or larger position if exist and increment to normalize
            this.concepts.filter(c => c.position! >= conceptPosition).forEach(c => (c.position = c.position! + 1));
          }
        }
        const newConcept = {
          ...new Concept(),
          notation: this.csvContents[i][0],
          parent: parentConcept !== undefined ? parentConcept.notation : undefined,
          position: conceptPosition,
          title: this.csvContents[i][1],
          definition: this.csvContents[i][2],
          visible: false
        };
        this.concepts.push(newConcept);
        this.codeSnippets.push(this.createCodeFromConcept(newConcept));
      }
    }
    this.csvImportWorkflow = 'IMPORT';
    $element.scrollIntoView({ behavior: 'smooth', block: 'start', inline: 'nearest' });
  }

  findLastChildForPosition(parentConcept: IConcept): IConcept {
    const lastChild = this.concepts
      .filter(c => c.parent === parentConcept.notation)
      .reduce((prevConcept, nextConcept) => (prevConcept.position! > nextConcept.position! ? prevConcept : nextConcept));
    if (this.concepts.filter(c => c.parent === lastChild.notation).length > 0) {
      return this.findLastChildForPosition(lastChild);
    }
    return lastChild;
  }

  doCsvImport(): void {
    this.isSaving = true;
    this.editorService.createBatchCode(this.codeSnippets).subscribe(
      (res: HttpResponse<IConcept[]>) => this.onSaveSuccess(res.headers, res.body),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(headers: HttpHeaders, body: IConcept[] | null): void {
    this.resultInfo = headers.get('import-status') ? headers.get('import-status')! : '';
    this.resultBody = body === null ? [] : body;
    this.isSaving = false;
    this.csvImportWorkflow = 'RESULT';
  }

  onCloseSuccess(): void {
    if (this.isSlForm) {
      this.router.navigate(['/editor/vocabulary/' + this.versionParam.notation]);
    } else {
      this.router.navigate(['/editor/vocabulary/' + this.versionParam.notation], { queryParams: { lang: this.versionParam.language } });
    }
    this.activeModal.dismiss(true);
    this.eventManager.broadcast('deselectConcept');
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  private editCodeFromConcept(concept: IConcept): ICodeSnippet {
    return {
      ...new CodeSnippet(),
      actionType: 'ADD_TL_CODE',
      conceptId: concept.id,
      notation: concept.notation,
      versionId: concept.versionId,
      title: concept.title,
      definition: concept.definition
    };
  }

  private createCodeFromConcept(concept: IConcept): ICodeSnippet {
    return {
      ...new CodeSnippet(),
      actionType: 'CREATE_CODE',
      versionId: this.versionParam.id,
      notation: concept.notation,
      parent: concept.parent,
      title: concept.title,
      definition: concept.definition,
      position: concept.position,
      changeType: 'Code added',
      changeDesc: concept.notation
    };
  }
}
