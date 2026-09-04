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
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { JhiEventManager } from 'ng-jhipster';
import { of } from 'rxjs';

import { CvsTestModule } from '../../test.module';
import { MockEventManager } from '../../helpers/mock-event-manager.service';
import { MockActiveModal } from '../../helpers/mock-active-modal.service';
import { MockRouter } from '../../helpers/mock-route.service';
import { EditorDetailCodeDeleteDialogComponent } from 'app/editor/editor-detail-code-delete-dialog.component';
import { EditorService } from 'app/editor/editor.service';
import { createNewVersion } from 'app/shared/model/version.model';
import { ActionType } from 'app/shared/model/enumerations/action-type.model';

describe('Component Tests', () => {
  describe('Editor Detail Code Delete Dialog Component', () => {
    let comp: EditorDetailCodeDeleteDialogComponent;
    let fixture: ComponentFixture<EditorDetailCodeDeleteDialogComponent>;
    let service: EditorService;
    let mockEventManager: MockEventManager;
    let mockActiveModal: MockActiveModal;
    let mockRouter: MockRouter;

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [EditorDetailCodeDeleteDialogComponent],
      })
        .overrideTemplate(EditorDetailCodeDeleteDialogComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(EditorDetailCodeDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(EditorService);
      mockEventManager = TestBed.inject(JhiEventManager) as unknown as MockEventManager;
      mockActiveModal = TestBed.inject(NgbActiveModal) as unknown as MockActiveModal;
      mockRouter = TestBed.inject(Router) as unknown as MockRouter;

      comp.versionParam = { ...createNewVersion(1), notation: 'AnalysisUnit', language: 'en' };
      comp.conceptParam = { notation: 'Individual', id: 55 };
    });

    it('should dismiss the modal without deleting anything on clear', () => {
      comp.clear();

      expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
    });

    describe('deleting from a source language version', () => {
      beforeEach(() => {
        comp.isSlForm = true;
      });

      it('should delete the code outright', fakeAsync(() => {
        const spy = spyOn(service, 'deleteCode').and.returnValue(of({} as never));

        comp.confirmDelete(99);
        tick();

        // the SL branch deletes the id it was given
        expect(spy).toHaveBeenCalledWith(99);
      }));

      it('should return to the vocabulary and deselect the concept', fakeAsync(() => {
        spyOn(service, 'deleteCode').and.returnValue(of({} as never));

        comp.confirmDelete(55);
        tick();

        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['/editor/vocabulary/AnalysisUnit']);
        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith('deselectConcept');
        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
      }));
    });

    describe('deleting from a translation version', () => {
      beforeEach(() => {
        comp.isSlForm = false;
      });

      it('should update the code with a DELETE_TL_CODE action instead of deleting it', fakeAsync(() => {
        const spy = spyOn(service, 'updateCode').and.returnValue(of({} as never));

        comp.confirmDelete(99);
        tick();

        // the TL branch ignores the argument and uses the selected concept
        expect(spy).toHaveBeenCalledWith({
          actionType: ActionType.DELETE_TL_CODE,
          conceptId: 55,
          versionId: 1,
        });
      }));

      it('should keep the language when returning to the vocabulary', fakeAsync(() => {
        spyOn(service, 'updateCode').and.returnValue(of({} as never));

        comp.confirmDelete(55);
        tick();

        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['/editor/vocabulary/AnalysisUnit'], {
          queryParams: { lang: 'en' },
        });
        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith('deselectConcept');
      }));

      it('should not call the outright delete', fakeAsync(() => {
        const deleteSpy = spyOn(service, 'deleteCode').and.returnValue(of({} as never));
        spyOn(service, 'updateCode').and.returnValue(of({} as never));

        comp.confirmDelete(55);
        tick();

        expect(deleteSpy).not.toHaveBeenCalled();
      }));
    });
  });
});
