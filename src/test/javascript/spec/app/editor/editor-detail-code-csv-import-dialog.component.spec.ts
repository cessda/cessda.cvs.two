/*
 * Copyright © 2017-2026 CESSDA ERIC (support@cessda.eu)
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
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ElementRef } from '@angular/core';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of, throwError } from 'rxjs';

import { CvsTestModule } from '../../test.module';
import { MockActiveModal } from '../../helpers/mock-active-modal.service';
import { MockEventManager } from '../../helpers/mock-event-manager.service';
import { MockRouter } from '../../helpers/mock-route.service';
import { EditorDetailCodeCsvImportDialogComponent } from 'app/editor/editor-detail-code-csv-import-dialog.component';
import { EditorService } from 'app/editor/editor.service';
import { EventManager } from 'app/core/util/event-manager.service';
import { createNewVocabulary } from 'app/shared/model/vocabulary.model';
import { createNewVersion } from 'app/shared/model/version.model';
import { Concept } from 'app/shared/model/concept.model';
import { ActionType } from 'app/shared/model/enumerations/action-type.model';

describe('Component Tests', () => {
  describe('Editor Detail Code CSV Import Dialog Component', () => {
    let comp: EditorDetailCodeCsvImportDialogComponent;
    let fixture: ComponentFixture<EditorDetailCodeCsvImportDialogComponent>;

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [EditorDetailCodeCsvImportDialogComponent],
      })
        .overrideTemplate(EditorDetailCodeCsvImportDialogComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(EditorDetailCodeCsvImportDialogComponent);
      comp = fixture.componentInstance;
      comp.versionParam = { ...createNewVersion(1), notation: 'AnalysisUnit', language: 'en' };
    });

    it('should start in the upload stage importing everything', () => {
      expect(comp.csvImportWorkflow).toBe('UPLOAD');
      expect(comp.importAll).toBe(true);
      expect(comp.isImportError).toBe(false);
      expect(comp.isSaving).toBe(false);
      expect(comp.ignoredRows).toBe(0);
    });

    it('should dismiss the modal on clear', () => {
      comp.clear();

      expect(TestBed.inject(NgbActiveModal) as unknown as MockActiveModal).toBeDefined();
      expect((TestBed.inject(NgbActiveModal) as unknown as MockActiveModal).dismissSpy).toHaveBeenCalled();
    });

    describe('parseCSVToArray', () => {
      it('should split rows and columns on commas and newlines', () => {
        const rows = comp.parseCSVToArray('notation,title,definition\nIndividual,Individual,A person');

        expect(rows).toEqual([
          ['notation', 'title', 'definition'],
          ['Individual', 'Individual', 'A person'],
        ]);
      });

      it('should keep commas that sit inside quoted fields', () => {
        const rows = comp.parseCSVToArray('a,b\n"one, two",three');

        expect(rows[1]).toEqual(['one, two', 'three']);
      });

      it('should unescape doubled quotes inside a quoted field', () => {
        const rows = comp.parseCSVToArray('a\n"he said ""hello"""');

        expect(rows[1]).toEqual(['he said "hello"']);
      });

      it('should accept a custom delimiter', () => {
        const rows = comp.parseCSVToArray('a;b;c', ';');

        expect(rows[0]).toEqual(['a', 'b', 'c']);
      });

      it('should handle carriage returns from Windows files', () => {
        const rows = comp.parseCSVToArray('a,b\r\nc,d');

        expect(rows).toEqual([
          ['a', 'b'],
          ['c', 'd'],
        ]);
      });

      it('should return a single empty row for empty input', () => {
        // guards issue #316: an empty file must not throw
        expect(comp.parseCSVToArray('')).toEqual([[]]);
      });
    });

    describe('getHeaderArray', () => {
      it('should always return three header slots', () => {
        expect(comp.getHeaderArray([['notation', 'title', 'definition']])).toEqual(['notation', 'title', 'definition']);
      });

      it('should leave missing headers undefined', () => {
        const headers = comp.getHeaderArray([['notation']]);

        expect(headers).toHaveLength(3);
        expect(headers[0]).toBe('notation');
        expect(headers[1]).toBeUndefined();
      });
    });

    describe('getDataRecordsArrayFromCSVFile in a source language version', () => {
      beforeEach(() => {
        comp.isSlForm = true;
      });

      it('should skip the header row and keep the first three columns', () => {
        const records = comp.getDataRecordsArrayFromCSVFile([
          ['notation', 'title', 'definition'],
          ['Individual', 'Individual', 'A person', 'ignored fourth column'],
        ]);

        expect(records).toEqual([['Individual', 'Individual', 'A person']]);
      });

      it('should drop rows with fewer than three columns', () => {
        const records = comp.getDataRecordsArrayFromCSVFile([
          ['notation', 'title', 'definition'],
          ['Individual', 'Individual'],
        ]);

        expect(records).toHaveLength(0);
      });

      it('should drop rows without a notation or a title', () => {
        const records = comp.getDataRecordsArrayFromCSVFile([
          ['notation', 'title', 'definition'],
          ['   ', 'Individual', 'A person'],
          ['Individual', '  ', 'A person'],
        ]);

        expect(records).toHaveLength(0);
      });
    });

    describe('getDataRecordsArrayFromCSVFile in a translation version', () => {
      beforeEach(() => {
        comp.isSlForm = false;
        comp.vocabularyParam = createNewVocabulary({
          versions: [
            {
              ...createNewVersion(1),
              itemType: 'SL',
              concepts: [
                { notation: 'Individual', title: 'Individual' },
                { notation: 'Household', title: 'Household' },
              ],
            },
          ],
        });
      });

      it('should only import codes that already exist in the source language', () => {
        const records = comp.getDataRecordsArrayFromCSVFile([
          ['notation', 'title', 'definition'],
          ['Individual', 'Osoba', 'Jednotlivec'],
          ['Organisation', 'Organizacia', 'Not in the SL version'],
        ]);

        expect(records).toEqual([['Individual', 'Osoba', 'Jednotlivec']]);
      });

      it('should count the rows it ignored', () => {
        comp.getDataRecordsArrayFromCSVFile([
          ['notation', 'title', 'definition'],
          ['Organisation', 'Organizacia', 'Not in the SL version'],
          ['Country', 'Krajina', 'Also not in the SL version'],
        ]);

        expect(comp.ignoredRows).toBe(2);
      });

      it('should reset the ignored count on every parse', () => {
        const rows = [
          ['notation', 'title', 'definition'],
          ['Organisation', 'Organizacia', 'Not in the SL version'],
        ];
        comp.getDataRecordsArrayFromCSVFile(rows);
        comp.getDataRecordsArrayFromCSVFile(rows);

        expect(comp.ignoredRows).toBe(1);
      });
    });

    describe('createCodePreview', () => {
      // scrollIntoView is not implemented in jsdom, and the heading only exists in the template
      const heading = (): HTMLHeadingElement => ({ scrollIntoView: jest.fn() }) as unknown as HTMLHeadingElement;

      const withConcepts = (concepts: Concept[]): void => {
        comp.versionParam = { ...createNewVersion(1), notation: 'AnalysisUnit', language: 'en', concepts };
      };

      it('should skip the rows that are not marked for import', () => {
        withConcepts([]);
        comp.csvContents = [
          ['Individual', 'Individual', 'A person'],
          ['Family', 'Family', 'A household'],
        ];
        comp.markedRows = [false, true];

        comp.createCodePreview(heading());

        expect(comp.codeSnippets).toHaveLength(1);
        expect(comp.concepts.map(c => c.notation)).toEqual(['Family']);
      });

      it('should overwrite the title and definition of a concept that already exists', () => {
        withConcepts([{ notation: 'Individual', title: 'Old title', definition: 'Old definition', position: 1 }]);
        comp.csvContents = [['Individual', 'New title', 'New definition']];
        comp.markedRows = [true];

        comp.createCodePreview(heading());

        expect(comp.concepts).toHaveLength(1);
        expect(comp.concepts[0]).toEqual(expect.objectContaining({ title: 'New title', definition: 'New definition' }));
      });

      it('should leave the version it was given untouched when overwriting', () => {
        const original: Concept = { notation: 'Individual', title: 'Old title', definition: 'Old definition', position: 1 };
        withConcepts([original]);
        comp.csvContents = [['Individual', 'New title', 'New definition']];
        comp.markedRows = [true];

        comp.createCodePreview(heading());

        expect(original.title).toBe('Old title');
      });

      it('should add a top level concept at the end of the list', () => {
        withConcepts([{ notation: 'Individual', position: 1 }]);
        comp.csvContents = [['Family', 'Family', 'A household']];
        comp.markedRows = [true];

        comp.createCodePreview(heading());

        expect(comp.concepts[1]).toEqual(expect.objectContaining({ notation: 'Family', parent: undefined, position: 1, visible: false }));
      });

      it('should hang a child off its parent, taking the parent position when it is the first child', () => {
        withConcepts([{ notation: 'Individual', position: 4 }]);
        comp.csvContents = [['Individual.Child', 'A child', 'The first child']];
        comp.markedRows = [true];

        comp.createCodePreview(heading());

        expect(comp.concepts[1]).toEqual(expect.objectContaining({ notation: 'Individual.Child', parent: 'Individual', position: 4 }));
      });

      it('should place a further child after the last child of the parent', () => {
        withConcepts([
          { notation: 'Individual', position: 1 },
          { notation: 'Individual.First', parent: 'Individual', position: 2 },
          { notation: 'Individual.Second', parent: 'Individual', position: 7 },
        ]);
        comp.csvContents = [['Individual.Third', 'A third', 'Added last']];
        comp.markedRows = [true];

        comp.createCodePreview(heading());

        const added = comp.concepts.find(c => c.notation === 'Individual.Third');
        expect(added).toEqual(expect.objectContaining({ parent: 'Individual', position: 7 }));
      });

      it('should follow the tree down to the last descendant when the last child has children of its own', () => {
        withConcepts([
          { notation: 'Individual', position: 1 },
          { notation: 'Individual.First', parent: 'Individual', position: 3 },
          { notation: 'Individual.First.Deep', parent: 'Individual.First', position: 9 },
        ]);
        comp.csvContents = [['Individual.Second', 'A second', 'Added after the deep child']];
        comp.markedRows = [true];

        comp.createCodePreview(heading());

        expect(comp.concepts.find(c => c.notation === 'Individual.Second')?.position).toBe(9);
      });

      it('should skip a child whose parent is not there', () => {
        withConcepts([{ notation: 'Individual', position: 1 }]);
        comp.csvContents = [['Missing.Child', 'Orphan', 'No parent in the version']];
        comp.markedRows = [true];

        comp.createCodePreview(heading());

        expect(comp.concepts.map(c => c.notation)).toEqual(['Individual']);
        expect(comp.codeSnippets).toHaveLength(0);
      });

      it('should treat a notation whose dot comes too early as a top level concept', () => {
        withConcepts([]);
        comp.csvContents = [['A.Child', 'Short prefix', 'The dot is at index 1']];
        comp.markedRows = [true];

        comp.createCodePreview(heading());

        expect(comp.concepts[0]).toEqual(expect.objectContaining({ notation: 'A.Child', parent: undefined }));
      });

      it('should move on to the import stage and scroll the preview into view', () => {
        withConcepts([]);
        comp.csvContents = [['Individual', 'Individual', 'A person']];
        comp.markedRows = [true];
        const element = heading();

        comp.createCodePreview(element);

        expect(comp.csvImportWorkflow).toBe('IMPORT');
        expect(element.scrollIntoView).toHaveBeenCalled();
      });

      it('should start the preview again from the version each time it runs', () => {
        withConcepts([{ notation: 'Individual', position: 1 }]);
        comp.csvContents = [['Family', 'Family', 'A household']];
        comp.markedRows = [true];

        comp.createCodePreview(heading());
        comp.createCodePreview(heading());

        expect(comp.concepts.filter(c => c.notation === 'Family')).toHaveLength(1);
        expect(comp.codeSnippets).toHaveLength(1);
      });
    });

    describe('fillIsRowImported', () => {
      it('should mark every row when importing all', () => {
        comp.markedRows = new Array(3);
        comp.importAll = true;

        comp.fillIsRowImported();

        expect(comp.markedRows).toEqual([true, true, true]);
      });

      it('should clear every row when not importing all', () => {
        comp.markedRows = new Array(2);
        comp.importAll = false;

        comp.fillIsRowImported();

        expect(comp.markedRows).toEqual([false, false]);
      });
    });

    describe('parseCSVToArray edge cases', () => {
      it('should keep empty unquoted fields as empty strings', () => {
        expect(comp.parseCSVToArray('a,,c')).toEqual([['a', '', 'c']]);
      });

      it('should ignore surrounding blank lines and whitespace', () => {
        expect(comp.parseCSVToArray('\n  a,b\nc,d\n\n')).toEqual([
          ['a', 'b'],
          ['c', 'd'],
        ]);
      });

      it('should keep a line break that sits inside a quoted field', () => {
        const rows = comp.parseCSVToArray('a,b\n"first line\nsecond line",c');

        expect(rows).toEqual([
          ['a', 'b'],
          ['first line\nsecond line', 'c'],
        ]);
      });

      it('should accept a bare carriage return as a row separator', () => {
        expect(comp.parseCSVToArray('a,b\rc,d')).toEqual([
          ['a', 'b'],
          ['c', 'd'],
        ]);
      });
    });

    describe('selecting a file', () => {
      // a stand-in for the browser FileReader, so a test can decide when the file has been read
      class FakeFileReader {
        result: string | null = null;
        onload: (() => void) | null = null;
        readAsText = jest.fn();

        finishReading(text: string): void {
          this.result = text;
          this.onload?.();
        }
      }

      let reader: FakeFileReader;
      const file = new File([''], 'codes.csv', { type: 'text/csv' });
      const fileInput = (files: File[] | null): EventTarget => ({ files }) as unknown as EventTarget;

      beforeEach(() => {
        reader = new FakeFileReader();
        jest.spyOn(window, 'FileReader').mockImplementation(() => reader as unknown as FileReader);
        comp.isSlForm = true;
      });

      afterEach(() => {
        jest.restoreAllMocks();
      });

      it('should read the selected file as UTF-8 text', () => {
        comp.onFileSelect(fileInput([file]));

        expect(reader.readAsText).toHaveBeenCalledWith(file, 'UTF-8');
      });

      it('should do nothing more when the input has no files', () => {
        comp.onFileSelect(fileInput(null));

        expect(reader.readAsText).not.toHaveBeenCalled();
        expect(comp.csvImportWorkflow).toBe('UPLOAD');
      });

      it('should show the parsed rows, all marked for import, and move on to the select stage', () => {
        comp.importAll = false;
        comp.isImportError = true;

        comp.onFileSelect(fileInput([file]));
        reader.finishReading('notation,title,definition\nIndividual,Individual,A person\nFamily,Family,A household');

        expect(comp.csvHeaders).toEqual(['notation', 'title', 'definition']);
        expect(comp.csvContents).toEqual([
          ['Individual', 'Individual', 'A person'],
          ['Family', 'Family', 'A household'],
        ]);
        expect(comp.markedRows).toEqual([true, true]);
        expect(comp.importAll).toBe(true);
        expect(comp.isImportError).toBe(false);
        expect(comp.csvImportWorkflow).toBe('SELECT');
      });

      it('should flag an error and stay on the upload stage when the file has no usable rows', () => {
        comp.onFileSelect(fileInput([file]));
        reader.finishReading('notation,title,definition');

        expect(comp.csvContents).toHaveLength(0);
        expect(comp.isImportError).toBe(true);
        expect(comp.csvImportWorkflow).toBe('UPLOAD');
      });

      it('should start from a copy of the codes already in the version', () => {
        const existing: Concept = { notation: 'Individual', title: 'Individual', position: 1 };
        comp.versionParam = { ...comp.versionParam, concepts: [existing] };
        comp.codeSnippets = [{ notation: 'Stale' }];

        comp.onFileSelect(fileInput([file]));

        expect(comp.concepts).toEqual([existing]);
        expect(comp.concepts[0]).not.toBe(existing);
        expect(comp.codeSnippets).toHaveLength(0);
      });
    });

    describe('moving between stages', () => {
      const heading = (): HTMLHeadingElement => ({ scrollIntoView: jest.fn() }) as unknown as HTMLHeadingElement;

      it('should go back to the upload stage and clear the file input', () => {
        comp.csvImportWorkflow = 'SELECT';
        comp.csvInput = new ElementRef({ value: 'codes.csv' });
        const element = heading();

        comp.backToUploadStage(element);

        expect(comp.csvImportWorkflow).toBe('UPLOAD');
        expect(comp.csvInput.nativeElement.value).toBe('');
        expect(element.scrollIntoView).toHaveBeenCalledWith({ behavior: 'smooth', block: 'start', inline: 'nearest' });
      });

      it('should go back to the select stage', () => {
        comp.csvImportWorkflow = 'IMPORT';
        const element = heading();

        comp.backToSelectStage(element);

        expect(comp.csvImportWorkflow).toBe('SELECT');
        expect(element.scrollIntoView).toHaveBeenCalledWith({ behavior: 'smooth', block: 'start', inline: 'nearest' });
      });
    });

    describe('code snippets built by the preview', () => {
      const heading = (): HTMLHeadingElement => ({ scrollIntoView: jest.fn() }) as unknown as HTMLHeadingElement;

      it('should create a new code with its position and change log entry', () => {
        comp.versionParam = { ...comp.versionParam, id: 5, concepts: [{ notation: 'Individual', position: 1 }] };
        comp.csvContents = [['Individual.Child', 'A child', 'The first child']];
        comp.markedRows = [true];

        comp.createCodePreview(heading());

        expect(comp.codeSnippets).toEqual([
          {
            actionType: ActionType.CREATE_CODE,
            versionId: 5,
            notation: 'Individual.Child',
            parent: 'Individual',
            title: 'A child',
            definition: 'The first child',
            position: 1,
            changeType: 'Code added',
            changeDesc: 'Individual.Child',
          },
        ]);
      });

      it('should add a translation to a code that already exists', () => {
        comp.versionParam = {
          ...comp.versionParam,
          concepts: [{ id: 11, versionId: 5, notation: 'Individual', position: 1 }],
        };
        comp.csvContents = [['Individual', 'Osoba', 'Jednotlivec']];
        comp.markedRows = [true];

        comp.createCodePreview(heading());

        expect(comp.codeSnippets).toEqual([
          {
            actionType: ActionType.ADD_TL_CODE,
            conceptId: 11,
            notation: 'Individual',
            versionId: 5,
            title: 'Osoba',
            definition: 'Jednotlivec',
          },
        ]);
      });

      it('should fall back to position one for the first child of a parent without a position', () => {
        comp.versionParam = { ...comp.versionParam, concepts: [{ notation: 'Individual' }] };
        comp.csvContents = [['Individual.Child', 'A child', 'The first child']];
        comp.markedRows = [true];

        comp.createCodePreview(heading());

        expect(comp.codeSnippets[0]).toEqual(expect.objectContaining({ parent: 'Individual', position: 1 }));
      });

      it('should pick the child with the highest position even when it is listed first', () => {
        comp.versionParam = {
          ...comp.versionParam,
          concepts: [
            { notation: 'Individual', position: 1 },
            { notation: 'Individual.Second', parent: 'Individual', position: 7 },
            { notation: 'Individual.First', parent: 'Individual', position: 2 },
          ],
        };
        comp.csvContents = [['Individual.Third', 'A third', 'Added last']];
        comp.markedRows = [true];

        comp.createCodePreview(heading());

        expect(comp.codeSnippets[0]).toEqual(expect.objectContaining({ notation: 'Individual.Third', position: 7 }));
      });
    });

    describe('importing', () => {
      let service: EditorService;

      beforeEach(() => {
        service = TestBed.inject(EditorService);
        comp.codeSnippets = [{ notation: 'Individual', title: 'Individual' }];
      });

      it('should send the previewed snippets as one batch and show the result', () => {
        const imported: Concept[] = [{ notation: 'Individual', title: 'Individual' }];
        const spy = jest.spyOn(service, 'createBatchCode').mockReturnValue(
          of(
            new HttpResponse({
              body: imported,
              headers: new HttpHeaders({ 'import-status': '1 code imported' }),
            }),
          ),
        );

        comp.doCsvImport();

        expect(spy).toHaveBeenCalledWith([{ notation: 'Individual', title: 'Individual' }]);
        expect(comp.resultInfo).toBe('1 code imported');
        expect(comp.resultBody).toEqual(imported);
        expect(comp.isSaving).toBe(false);
        expect(comp.csvImportWorkflow).toBe('RESULT');
      });

      it('should cope with a response that has neither a status header nor a body', () => {
        jest.spyOn(service, 'createBatchCode').mockReturnValue(of(new HttpResponse<Concept[]>({ body: null })));

        comp.doCsvImport();

        expect(comp.resultInfo).toBe('');
        expect(comp.resultBody).toEqual([]);
        expect(comp.csvImportWorkflow).toBe('RESULT');
      });

      it('should stop saving and stay on the current stage when the import fails', () => {
        comp.csvImportWorkflow = 'IMPORT';
        jest.spyOn(service, 'createBatchCode').mockReturnValue(throwError(() => new HttpErrorResponse({ status: 500 })));

        comp.doCsvImport();

        expect(comp.isSaving).toBe(false);
        expect(comp.csvImportWorkflow).toBe('IMPORT');
      });
    });

    describe('closing after a successful import', () => {
      let mockRouter: MockRouter;
      let mockActiveModal: MockActiveModal;
      let mockEventManager: MockEventManager;

      beforeEach(() => {
        mockRouter = TestBed.inject(Router) as unknown as MockRouter;
        mockActiveModal = TestBed.inject(NgbActiveModal) as unknown as MockActiveModal;
        mockEventManager = TestBed.inject(EventManager) as unknown as MockEventManager;
      });

      it('should return to the source language vocabulary', () => {
        comp.isSlForm = true;

        comp.onCloseSuccess();

        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['/editor/vocabulary/AnalysisUnit']);
        expect(mockActiveModal.dismissSpy).toHaveBeenCalledWith(true);
        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith('deselectConcept');
      });

      it('should return to the vocabulary in the language of the translation', () => {
        comp.isSlForm = false;

        comp.onCloseSuccess();

        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['/editor/vocabulary/AnalysisUnit'], { queryParams: { lang: 'en' } });
        expect(mockActiveModal.dismissSpy).toHaveBeenCalledWith(true);
      });
    });
  });
});
