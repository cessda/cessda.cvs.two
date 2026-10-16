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
import { EditorDetailCvDeleteDialogComponent } from 'app/editor/editor-detail-cv-delete-dialog.component';
import { EditorService } from 'app/editor/editor.service';
import { createNewVersion, Version } from 'app/shared/model/version.model';

describe('Component Tests', () => {
  describe('Editor Detail CV Delete Dialog Component', () => {
    let comp: EditorDetailCvDeleteDialogComponent;
    let fixture: ComponentFixture<EditorDetailCvDeleteDialogComponent>;
    let service: EditorService;
    let mockEventManager: MockEventManager;
    let mockActiveModal: MockActiveModal;
    let mockRouter: MockRouter;

    const version = (overrides: Partial<Version>): Version => ({ ...createNewVersion(9), notation: 'AnalysisUnit', ...overrides });

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [EditorDetailCvDeleteDialogComponent],
      })
        .overrideTemplate(EditorDetailCvDeleteDialogComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(EditorDetailCvDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(EditorService);
      mockEventManager = TestBed.inject(EventManager) as unknown as MockEventManager;
      mockActiveModal = TestBed.inject(NgbActiveModal) as unknown as MockActiveModal;
      mockRouter = TestBed.inject(Router) as unknown as MockRouter;
    });

    describe('deciding what is deleted', () => {
      it('should delete only the translation for a TL version', () => {
        comp.versionParam = version({ itemType: 'TL', initialVersion: 1 });

        comp.ngOnInit();

        expect(comp.deleteType).toBe('versionTl');
      });

      it('should delete the whole vocabulary for an SL version without an initial version', () => {
        comp.versionParam = version({ itemType: 'SL', initialVersion: undefined });

        comp.ngOnInit();

        expect(comp.deleteType).toBe('vocabulary');
      });

      it('should delete the whole vocabulary for the initial SL version', () => {
        comp.versionParam = version({ itemType: 'SL', initialVersion: 9 });

        comp.ngOnInit();

        expect(comp.deleteType).toBe('vocabulary');
      });

      it('should delete only the SL version for a later SL version', () => {
        comp.versionParam = version({ itemType: 'SL', initialVersion: 4 });

        comp.ngOnInit();

        expect(comp.deleteType).toBe('versionSl');
      });
    });

    it('should dismiss the modal without deleting anything on clear', () => {
      const spy = spyOn(service, 'deleteVocabulary');

      comp.clear();

      expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
      expect(spy).not.toHaveBeenCalled();
    });

    describe('confirming a vocabulary delete', () => {
      it('should delete the initial version and return to the editor list', () => {
        const spy = spyOn(service, 'deleteVocabulary').and.returnValue(of({} as never));
        comp.versionParam = version({ itemType: 'SL', initialVersion: 9 });
        comp.ngOnInit();

        comp.confirmDelete();

        expect(spy).toHaveBeenCalledWith(9);
        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['/editor']);
        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith('deselectConcept');
      });

      it('should delete by the initial version id when it differs from the version id', () => {
        const spy = spyOn(service, 'deleteVocabulary').and.returnValue(of({} as never));
        comp.versionParam = version({ itemType: 'SL', initialVersion: 4 });
        comp.deleteType = 'vocabulary';

        comp.confirmDelete();

        expect(spy).toHaveBeenCalledWith(4);
      });

      it('should delete by the version id when there is no initial version', () => {
        const spy = spyOn(service, 'deleteVocabulary').and.returnValue(of({} as never));
        comp.versionParam = version({ itemType: 'SL', initialVersion: undefined });
        comp.ngOnInit();

        comp.confirmDelete();

        expect(spy).toHaveBeenCalledWith(9);
      });
    });

    describe('confirming a version delete', () => {
      it.each(['versionSl', 'versionTl'])('should delete the version itself and return to the vocabulary for %s', deleteType => {
        const spy = spyOn(service, 'deleteVocabulary').and.returnValue(of({} as never));
        comp.versionParam = version({ itemType: 'TL', initialVersion: 4 });
        comp.deleteType = deleteType;

        comp.confirmDelete();

        // the initial version is only used when the whole vocabulary goes
        expect(spy).toHaveBeenCalledWith(9);
        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['/editor/vocabulary/AnalysisUnit']);
        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith('deselectConcept');
      });

      it('should keep the dialog open until the server has answered', () => {
        const response = new Subject<never>();
        spyOn(service, 'deleteVocabulary').and.returnValue(response.asObservable());
        comp.versionParam = version({ itemType: 'TL' });
        comp.ngOnInit();

        comp.confirmDelete();

        expect(mockRouter.navigateSpy).not.toHaveBeenCalled();
        expect(mockActiveModal.dismissSpy).not.toHaveBeenCalled();
        expect(mockEventManager.broadcastSpy).not.toHaveBeenCalled();
      });
    });
  });
});
