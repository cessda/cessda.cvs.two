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
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { CvsTestModule } from '../../test.module';
import { MockActiveModal } from '../../helpers/mock-active-modal.service';
import { EditorDetailCodeCsvImportDialogComponent } from 'app/editor/editor-detail-code-csv-import-dialog.component';
import { createNewVocabulary } from 'app/shared/model/vocabulary.model';
import { createNewVersion } from 'app/shared/model/version.model';
import { Concept } from 'app/shared/model/concept.model';

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
  });
});
