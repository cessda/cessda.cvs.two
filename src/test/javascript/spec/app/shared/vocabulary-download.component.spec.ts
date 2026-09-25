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
import { UntypedFormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { of } from 'rxjs';

import { CvsTestModule } from '../../test.module';
import { VocabularyDownloadComponent } from 'app/shared/vocabulary-download/vocabulary-download.component';
import { EditorService } from 'app/editor/editor.service';
import { HomeService } from 'app/home/home.service';
import { createNewVersion, Version } from 'app/shared/model/version.model';

describe('Component Tests', () => {
  describe('Vocabulary Download Component', () => {
    let comp: VocabularyDownloadComponent;
    let fixture: ComponentFixture<VocabularyDownloadComponent>;
    let editorService: EditorService;
    let homeService: HomeService;

    const slVersion: Version = { ...createNewVersion(1), itemType: 'SL', language: 'en', number: '2.0.0' };
    const tlVersion: Version = { ...createNewVersion(2), itemType: 'TL', language: 'de', number: '2.0.1' };
    // belongs to an older bundle, so it must not be offered for download
    const oldVersion: Version = { ...createNewVersion(3), itemType: 'TL', language: 'fr', number: '1.0.1' };

    const asScope = (scope: string): VocabularyDownloadComponent['appScope'] => scope as unknown as VocabularyDownloadComponent['appScope'];

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [VocabularyDownloadComponent],
      })
        .overrideTemplate(VocabularyDownloadComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      const mockRouter = TestBed.inject(Router) as unknown as Record<string, unknown>;
      // the constructor opts out of route reuse, which the mock router has no strategy for
      mockRouter.routeReuseStrategy = { shouldReuseRoute: (): boolean => true };

      fixture = TestBed.createComponent(VocabularyDownloadComponent);
      comp = fixture.componentInstance;
      editorService = fixture.debugElement.injector.get(EditorService);
      homeService = fixture.debugElement.injector.get(HomeService);

      comp.notation = 'AnalysisUnit';
      comp.slVersionNumber = '2.0.0';
      comp.versions = [slVersion, tlVersion, oldVersion];
      comp.appScope = asScope('PUBLICATION');
      comp.downloadFormGroup = TestBed.inject(UntypedFormBuilder).group({
        skosItems: [[]],
        pdfItems: [[]],
        htmlItems: [[]],
        docxItems: [[]],
      });
    });

    describe('setting up the checkboxes', () => {
      it('should offer every language of the current bundle, preselected', () => {
        comp.ngOnInit();

        expect(comp.downloadCheckboxes).toEqual(['en-2.0.0', 'de-2.0.1']);
        expect(comp.skosSelected).toEqual([true, true]);
        expect(comp.pdfSelected).toEqual([true, true]);
        expect(comp.htmlSelected).toEqual([true, true]);
        expect(comp.docxSelected).toEqual([true, true]);
      });

      it('should leave out a language that only exists in an older bundle', () => {
        comp.ngOnInit();

        expect(comp.downloadCheckboxes).not.toContain('fr-1.0.1');
      });

      it('should hand the preselection to the form', () => {
        comp.ngOnInit();

        expect(comp.downloadFormGroup.get('skosItems')?.value).toEqual([true, true]);
        expect(comp.downloadFormGroup.get('docxItems')?.value).toEqual([true, true]);
      });

      it('should clear every selection on reset', () => {
        comp.ngOnInit();

        comp.resetExport();

        expect(comp.skosSelected).toEqual([false, false]);
        expect(comp.pdfSelected).toEqual([false, false]);
        expect(comp.htmlSelected).toEqual([false, false]);
        expect(comp.docxSelected).toEqual([false, false]);
      });
    });

    describe('reading the versions', () => {
      it('should filter versions by language', () => {
        expect(comp.getVersionsByLang('de')).toEqual([tlVersion]);
        expect(comp.getVersionsByLang('sk')).toEqual([]);
      });

      it('should fall back to the source language version number', () => {
        expect(comp.getSlMajorMinorVersionNumber()).toBe('2.0');
      });

      it('should use a version number it is given', () => {
        expect(comp.getSlMajorMinorVersionNumber('4.2.7')).toBe('4.2');
      });
    });

    describe('selecting what to download', () => {
      beforeEach(() => {
        comp.ngOnInit();
        comp.resetExport();
      });

      const checkbox = (checked: boolean): Event => ({ target: { checked } }) as unknown as Event;

      it('should join the selected languages with an underscore', () => {
        comp.skosSelected = [true, true];

        expect(comp.getCheckedItems(comp.skosSelected)).toBe('en-2.0.0_de-2.0.1');
      });

      it('should return only what is selected, without a trailing separator', () => {
        comp.skosSelected = [false, true];

        expect(comp.getCheckedItems(comp.skosSelected)).toBe('de-2.0.1');
      });

      it('should return nothing when nothing is selected', () => {
        expect(comp.getCheckedItems(comp.skosSelected)).toBe('');
      });

      it('should select and clear all of one format at a time', () => {
        comp.toggleSelectAll('pdf', checkbox(true));

        expect(comp.pdfSelected).toEqual([true, true]);
        expect(comp.skosSelected).toEqual([false, false]);

        comp.toggleSelectAll('pdf', checkbox(false));

        expect(comp.pdfSelected).toEqual([false, false]);
      });

      it('should ignore a format it does not know', () => {
        comp.toggleSelectAll('something-else', checkbox(true));

        expect(comp.skosSelected).toEqual([false, false]);
        expect(comp.pdfSelected).toEqual([false, false]);
      });

      it('should rewrite the label when another version is picked for a language', () => {
        comp.updateCheckboxValue(1, 'de', { target: { value: '2.0.2' } } as unknown as Event);

        expect(comp.downloadCheckboxes[1]).toBe('de-2.0.2');
      });

      it('should record a single checkbox being ticked', () => {
        comp.updateSelection(comp.htmlSelected, 1, checkbox(true));

        expect(comp.htmlSelected).toEqual([false, true]);
      });
    });

    describe('asking for the file', () => {
      let editorSpy: jasmine.Spy;
      let homeSpy: jasmine.Spy;

      beforeEach(() => {
        comp.ngOnInit();
        editorSpy = spyOn(editorService, 'downloadVocabularyFile').and.returnValue(of(new Blob()));
        homeSpy = spyOn(homeService, 'downloadVocabularyFile').and.returnValue(of(new Blob()));
      });

      it('should ask for RDF, PDF, HTML and DOCX by their own media types', () => {
        comp.downloadSkos();
        comp.downloadPdf();
        comp.downloadHtml();
        comp.downloadDocx();

        const requested = homeSpy.calls.allArgs().map(args => args[2]);
        expect(requested).toEqual([
          'application/rdf+xml',
          'application/pdf',
          'text/html',
          'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        ]);
      });

      it('should pass the selected languages along', () => {
        comp.downloadPdf();

        expect(homeSpy).toHaveBeenCalledWith('AnalysisUnit', '2.0.0', 'application/pdf', {
          languageVersion: 'en-2.0.0_de-2.0.1',
        });
      });

      it('should go through the editor service inside the editor', () => {
        comp.appScope = asScope('EDITOR');

        comp.downloadPdf();

        expect(editorSpy).toHaveBeenCalledWith('AnalysisUnit', '2.0.0', 'application/pdf', { lv: 'en-2.0.0_de-2.0.1' });
        expect(homeSpy).not.toHaveBeenCalled();
      });
    });
  });
});
