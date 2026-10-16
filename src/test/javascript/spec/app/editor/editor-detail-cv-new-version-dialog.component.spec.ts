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
import { Router } from '@angular/router';
import { EventManager } from 'app/core/util/event-manager.service';
import { of, Subject } from 'rxjs';

import { CvsTestModule } from '../../test.module';
import { MockEventManager } from '../../helpers/mock-event-manager.service';
import { MockActiveModal } from '../../helpers/mock-active-modal.service';
import { MockRouter } from '../../helpers/mock-route.service';
import { EditorDetailCvNewVersionDialogComponent } from 'app/editor/editor-detail-cv-new-version-dialog.component';
import { EditorService } from 'app/editor/editor.service';
import { VocabularyLanguageFromKeyPipe } from 'app/shared/language/vocabulary-language-from-key.pipe';
import { createNewVersion, Version } from 'app/shared/model/version.model';
import { createNewVocabulary } from 'app/shared/model/vocabulary.model';

describe('Component Tests', () => {
  describe('Editor Detail CV New Version Dialog Component', () => {
    let comp: EditorDetailCvNewVersionDialogComponent;
    let fixture: ComponentFixture<EditorDetailCvNewVersionDialogComponent>;
    let service: EditorService;
    let mockEventManager: MockEventManager;
    let mockActiveModal: MockActiveModal;
    let mockRouter: MockRouter;

    const slVersion: Version = { ...createNewVersion(1), itemType: 'SL', language: 'en', number: '2.0.0', notation: 'AnalysisUnit' };
    const publishedTl: Version = { ...createNewVersion(2), itemType: 'TL', language: 'de', number: '2.0.1', status: 'PUBLISHED' };
    const draftTl: Version = { ...createNewVersion(3), itemType: 'TL', language: 'fr', number: '2.0.1', status: 'DRAFT' };
    const reviewTl: Version = { ...createNewVersion(4), itemType: 'TL', language: 'fi', number: '2.0.1', status: 'REVIEW' };

    const openOn = (version: Version, versions: Version[]): void => {
      comp.versionParam = version;
      comp.vocabularyParam = createNewVocabulary({ notation: 'AnalysisUnit', versions });
      comp.ngOnInit();
    };

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [EditorDetailCvNewVersionDialogComponent],
        providers: [VocabularyLanguageFromKeyPipe],
      })
        .overrideTemplate(EditorDetailCvNewVersionDialogComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(EditorDetailCvNewVersionDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(EditorService);
      mockEventManager = TestBed.inject(EventManager) as unknown as MockEventManager;
      mockActiveModal = TestBed.inject(NgbActiveModal) as unknown as MockActiveModal;
      mockRouter = TestBed.inject(Router) as unknown as MockRouter;
    });

    describe('opening on a source language version', () => {
      it('should list every translation and the unpublished ones separately', () => {
        openOn(slVersion, [slVersion, publishedTl, draftTl, reviewTl]);

        expect(comp.allTls).toBe('- German (de) 2.0.1-PUBLISHED<br/>- French (fr) 2.0.1-DRAFT<br/>- Finnish (fi) 2.0.1-REVIEW');
        expect(comp.unPublishedTls).toBe('- French (fr) 2.0.1-DRAFT<br/>- Finnish (fi) 2.0.1-REVIEW');
      });

      it('should ask for agreement while translations are unpublished', () => {
        openOn(slVersion, [slVersion, publishedTl, draftTl]);

        expect(comp.newVersionForm.contains('agreeNewVersion')).toBe(true);
        expect(comp.newVersionForm.invalid).toBe(true);

        comp.newVersionForm.patchValue({ agreeNewVersion: 'yes' });

        expect(comp.newVersionForm.valid).toBe(true);
      });

      it('should not ask for agreement when all translations are published', () => {
        openOn(slVersion, [slVersion, publishedTl]);

        expect(comp.unPublishedTls).toBe('');
        expect(comp.newVersionForm.contains('agreeNewVersion')).toBe(false);
        expect(comp.newVersionForm.valid).toBe(true);
      });

      it('should not ask for agreement when there are no translations', () => {
        openOn(slVersion, [slVersion]);

        expect(comp.allTls).toBe('');
        expect(comp.newVersionForm.contains('agreeNewVersion')).toBe(false);
      });
    });

    it('should never ask for agreement on a translation version', () => {
      openOn({ ...draftTl, notation: 'AnalysisUnit' }, [slVersion, publishedTl, draftTl]);

      expect(comp.allTls).toBe('');
      expect(comp.unPublishedTls).toBe('');
      expect(comp.newVersionForm.contains('agreeNewVersion')).toBe(false);
    });

    it('should dismiss the modal without creating anything on clear', () => {
      const spy = spyOn(service, 'createNewVersion');

      comp.clear();

      expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
      expect(spy).not.toHaveBeenCalled();
    });

    describe('creating the new version', () => {
      it('should create a new SL version and return to the vocabulary', () => {
        const spy = spyOn(service, 'createNewVersion').and.returnValue(of({} as never));
        openOn(slVersion, [slVersion]);

        comp.confirmCreateNewVersion(1);

        expect(spy).toHaveBeenCalledWith(1);
        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['/editor/vocabulary/AnalysisUnit']);
        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith('deselectConcept');
        expect(comp.isSaving).toBe(false);
      });

      it('should keep the language when creating a new TL version', () => {
        const spy = spyOn(service, 'createNewVersion').and.returnValue(of({} as never));
        openOn({ ...draftTl, notation: 'AnalysisUnit' }, [slVersion, draftTl]);

        comp.confirmCreateNewVersion(3);

        expect(spy).toHaveBeenCalledWith(3);
        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['/editor/vocabulary/AnalysisUnit'], { queryParams: { lang: 'fr' } });
        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
      });

      it('should stay saving and open until the server has answered', () => {
        spyOn(service, 'createNewVersion').and.returnValue(new Subject<never>().asObservable());
        openOn(slVersion, [slVersion]);

        comp.confirmCreateNewVersion(1);

        expect(comp.isSaving).toBe(true);
        expect(mockRouter.navigateSpy).not.toHaveBeenCalled();
        expect(mockActiveModal.dismissSpy).not.toHaveBeenCalled();
      });
    });
  });
});
