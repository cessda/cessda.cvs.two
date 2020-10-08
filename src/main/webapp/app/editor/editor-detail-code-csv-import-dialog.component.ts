import { Component, ElementRef, ViewChild } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { EditorService } from 'app/editor/editor.service';
import { IVersion } from 'app/shared/model/version.model';
import { Router } from '@angular/router';
import { Concept, IConcept } from 'app/shared/model/concept.model';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import VocabularyUtil from 'app/shared/util/vocabulary-util';
import { CodeSnippet, ICodeSnippet } from 'app/shared/model/code-snippet.model';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { IVocabulary } from 'app/shared/model/vocabulary.model';

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

  getLangIsoFormatted(langIso: string): string {
    return VocabularyUtil.getLangIsoFormatted(langIso);
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
      const csvRecordsArray = (csvData as string).split(/\r\n|\n/);
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
      const splittedContent = this.parseCSVToArray(csvRecordsArray[i] as string);
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
    const headers = (csvRecordsArr[0] as string).split(',', 3);
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

      // check for existing concepts, skip if exist
      if (this.concepts.some(c => c.notation === this.csvContents[i][0])) {
        continue;
      }

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

  parseCSVToArray(strData: string, strDelimiter?: string): any[] {
    // Check to see if the delimiter is defined. If not,
    // then default to comma.
    strDelimiter = strDelimiter || ',';

    // Create a regular expression to parse the CSV values.
    const objPattern = new RegExp(
      // Delimiters.
      '(\\' +
        strDelimiter +
        '|\\r?\\n|\\r|^)' +
        // Quoted fields.
        '(?:"([^"]*(?:""[^"]*)*)"|' +
        // Standard fields.
        '([^"\\' +
        strDelimiter +
        '\\r\\n]*))',
      'gi'
    );

    const a = [];

    // Create an array to hold our individual pattern
    // matching groups.
    let arrMatches = null;

    // Keep looping over the regular expression matches
    // until we can no longer find a match.
    while ((arrMatches = objPattern.exec(strData))) {
      // Get the delimiter that was found.
      const strMatchedDelimiter = arrMatches[1];

      // Check to see if the given delimiter has a length
      // (is not the start of string) and if it matches
      // field delimiter. If id does not, then we know
      // that this delimiter is a row delimiter.
      if (strMatchedDelimiter.length && strMatchedDelimiter !== strDelimiter) {
        return [];
      }
      let strMatchedValue;
      // Now that we have our delimiter out of the way,
      // let's check to see which kind of value we
      // captured (quoted or unquoted).
      if (arrMatches[2]) {
        // We found a quoted value. When we capture
        // this value, unescape any double quotes.
        strMatchedValue = arrMatches[2].replace(new RegExp('""', 'g'), '"');
      } else {
        // We found a non-quoted value.
        strMatchedValue = arrMatches[3];
      }
      // Now that we have our value string, let's add
      // it to the data array.
      a.push(strMatchedValue);
    }
    // Return the parsed data.
    return a;
  }
}
