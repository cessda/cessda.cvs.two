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
import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import { BehaviorSubject, of } from 'rxjs';

import { CvsTestModule } from '../../test.module';
import { EditorDetailComponent } from 'app/editor/editor-detail.component';
import { EditorService } from 'app/editor/editor.service';
import { VocabularyLanguageFromKeyPipe } from 'app/shared/language/vocabulary-language-from-key.pipe';
import { RouteEventsService } from 'app/shared/service/route-events.service';
import { createNewVocabulary, Vocabulary } from 'app/shared/model/vocabulary.model';
import { createNewVersion, Version } from 'app/shared/model/version.model';
import { Concept } from 'app/shared/model/concept.model';
import { ActionType } from 'app/shared/model/enumerations/action-type.model';

describe('Component Tests', () => {
  describe('Editor Detail Component', () => {
    let comp: EditorDetailComponent;
    let fixture: ComponentFixture<EditorDetailComponent>;
    let service: EditorService;

    const slVersion: Version = {
      ...createNewVersion(1),
      itemType: 'SL',
      language: 'en',
      number: '2.0.0',
      status: 'PUBLISHED',
      notation: 'AnalysisUnit',
    };
    const tlVersion: Version = {
      ...createNewVersion(2),
      itemType: 'TL',
      language: 'de',
      number: '2.0.1',
      status: 'DRAFT',
      notation: 'AnalysisUnit',
    };

    const vocabularyWith = (versions: Version[]): Vocabulary =>
      createNewVocabulary({
        id: 3,
        agencyId: 5,
        notation: 'AnalysisUnit',
        sourceLanguage: 'en',
        versionNumber: '2.0.0',
        selectedLang: 'en',
        versions,
      });

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [EditorDetailComponent],
        providers: [
          VocabularyLanguageFromKeyPipe,
          // the real service subscribes to router events, which the mock router does not emit
          { provide: RouteEventsService, useValue: { previousRoutePath: new BehaviorSubject<string>('') } },
        ],
      })
        .overrideTemplate(EditorDetailComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(EditorDetailComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(EditorService);

      comp.vocabulary = vocabularyWith([slVersion, tlVersion]);
      comp.version = slVersion;
    });

    describe('reading the source language version', () => {
      it('should pick the SL version out of the vocabulary', () => {
        expect(comp.getSlVersion()).toBe(slVersion);
      });

      it('should return the SL version number of the vocabulary when given nothing', () => {
        expect(comp.getSlVersionNumber()).toBe('2.0.0');
      });

      it('should normalise a version number it is given', () => {
        expect(comp.getSlVersionNumber('4.2.7')).toBe('4.2.7');
      });

      it('should expose the major and minor parts', () => {
        expect(comp.getSlMajorMinorVersionNumber()).toBe('2.0');
        expect(comp.getSlMajorVersionNumber()).toBe(2);
        expect(comp.getSlMinorVersionNumber()).toBe(0);
      });
    });

    describe('listing languages and versions', () => {
      it('should list each language once', () => {
        expect(comp.getUniqueVersionLang()).toEqual(['en', 'de']);
      });

      it('should return nothing when the vocabulary has no versions', () => {
        comp.vocabulary = createNewVocabulary({});

        expect(comp.getUniqueVersionLang()).toEqual([]);
        expect(comp.getVersionsByLanguage('en')).toEqual([]);
      });

      it('should filter the versions by language', () => {
        expect(comp.getVersionsByLanguage('de')).toEqual([tlVersion]);
        expect(comp.getVersionsByLanguage('fr')).toEqual([]);
      });
    });

    describe('describing a version', () => {
      it('should leave the status out of a published version', () => {
        expect(comp.getFormattedVersionTooltip(slVersion)).toBe('English (en) v.2.0.0');
      });

      it('should prefix an unpublished version with its status', () => {
        expect(comp.getFormattedVersionTooltip(tlVersion)).toBe('German (de) v.DRAFT-2.0.1');
      });

      it('should mark the source language', () => {
        expect(comp.getFormattedVersionTooltip(slVersion, 'en')).toBe('English (en) v.2.0.0 SOURCE');
      });

      it('should report the status of the first version in a language', () => {
        expect(comp.isVersionStatus('en', 'PUBLISHED')).toBe(true);
        expect(comp.isVersionStatus('de', 'PUBLISHED')).toBe(false);
      });
    });

    describe('bundle membership', () => {
      it('should fall back to the vocabulary version number', () => {
        expect(comp.isAnyLangVersionInBundle(comp.vocabulary, 'en')).toBe(true);
        expect(comp.isAnyLangVersionInBundle(comp.vocabulary, 'de')).toBe(false);
      });

      it('should honour an explicitly given bundle', () => {
        expect(comp.isAnyLangVersionInBundle(comp.vocabulary, 'de', '2.0.1')).toBe(true);
        expect(comp.isAnyLangVersionInBundle(comp.vocabulary, 'de', '9.9.9')).toBe(false);
      });
    });

    it('should report deprecated concepts', () => {
      const concepts: Concept[] = [{ notation: 'a' }, { notation: 'b', deprecated: true }];

      expect(comp.hasDeprecatedConcepts(concepts)).toBe(true);
      expect(comp.hasDeprecatedConcepts([concepts[0]])).toBe(false);
    });

    it('should expose the server origin', () => {
      expect(comp.getServerUrl()).toBe(window.location.origin);
    });

    describe('saving parts of the version', () => {
      let spy: jasmine.Spy;

      beforeEach(() => {
        spy = spyOn(service, 'updateVocabulary').and.returnValue(of({} as never));
      });

      it('should save the DDI usage on its own action', fakeAsync(() => {
        comp.editorDetailForm.patchValue({ ddiUsage: 'Some usage' });

        comp.saveDdiUsage();
        tick();

        expect(spy).toHaveBeenCalledWith({
          actionType: ActionType.EDIT_DDI_CV,
          agencyId: 5,
          vocabularyId: 3,
          versionId: 1,
          language: 'en',
          itemType: 'SL',
          ddiUsage: 'Some usage',
        });
      }));

      it('should write the saved DDI usage back onto the version', fakeAsync(() => {
        comp.editorDetailForm.patchValue({ ddiUsage: 'Some usage' });

        comp.saveDdiUsage();
        tick();

        expect(comp.version?.ddiUsage).toBe('Some usage');
      }));

      it('should save the notes on their own action', fakeAsync(() => {
        comp.editorDetailForm.patchValue({ notes: 'A note' });

        comp.saveNotes();
        tick();

        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ actionType: ActionType.EDIT_NOTE_CV, notes: 'A note' }));
      }));

      it('should leave empty notes off the snippet', fakeAsync(() => {
        comp.editorDetailForm.patchValue({ notes: '' });

        comp.saveNotes();
        tick();

        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ notes: undefined }));
      }));

      it('should save the version notes and changes together', fakeAsync(() => {
        comp.editorDetailForm.patchValue({ versionNotes: 'Notes', versionChanges: 'Changes' });

        comp.saveCurrentVersionInfo();
        tick();

        expect(spy).toHaveBeenCalledWith(
          jasmine.objectContaining({
            actionType: ActionType.EDIT_VERSION_INFO_CV,
            versionNotes: 'Notes',
            versionChanges: 'Changes',
          }),
        );
      }));

      it('should save the translating agency once the link is a URL', fakeAsync(() => {
        comp.editorDetailForm.patchValue({
          translateAgency: 'GESIS',
          translateAgencyLink: 'https://www.gesis.org',
        });

        comp.saveTranslateIdentity();
        tick();

        expect(spy).toHaveBeenCalledWith(
          jasmine.objectContaining({
            actionType: ActionType.EDIT_IDENTITY_CV,
            translateAgency: 'GESIS',
            translateAgencyLink: 'https://www.gesis.org',
          }),
        );
      }));

      it('should refuse to save a translating agency link that is not a URL', () => {
        comp.editorDetailForm.patchValue({ translateAgency: 'GESIS', translateAgencyLink: 'not a url' });

        comp.saveTranslateIdentity();

        expect(spy).not.toHaveBeenCalled();
      });
    });

    describe('closing an edit', () => {
      it('should restore the notes from the version', () => {
        comp.version = { ...slVersion, notes: 'Stored note' };
        comp.editorDetailForm.patchValue({ notes: 'Unsaved edit' });

        comp.closeNotes();

        expect(comp.isNotesEdit).toBe(false);
        expect(comp.editorDetailForm.controls.notes.value).toBe('Stored note');
      });
    });
  });
});
