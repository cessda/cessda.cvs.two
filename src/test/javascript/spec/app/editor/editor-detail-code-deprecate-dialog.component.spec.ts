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
import { EditorDetailCodeDeprecateDialogComponent } from 'app/editor/editor-detail-code-deprecate-dialog.component';
import { EditorService } from 'app/editor/editor.service';
import { createNewVersion } from 'app/shared/model/version.model';
import { ActionType } from 'app/shared/model/enumerations/action-type.model';

describe('Component Tests', () => {
  describe('Editor Detail Code Deprecate Dialog Component', () => {
    let comp: EditorDetailCodeDeprecateDialogComponent;
    let fixture: ComponentFixture<EditorDetailCodeDeprecateDialogComponent>;
    let service: EditorService;
    let mockEventManager: MockEventManager;
    let mockActiveModal: MockActiveModal;
    let mockRouter: MockRouter;

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [EditorDetailCodeDeprecateDialogComponent],
      })
        .overrideTemplate(EditorDetailCodeDeprecateDialogComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(EditorDetailCodeDeprecateDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(EditorService);
      mockEventManager = TestBed.inject(JhiEventManager) as unknown as MockEventManager;
      mockActiveModal = TestBed.inject(NgbActiveModal) as unknown as MockActiveModal;
      mockRouter = TestBed.inject(Router) as unknown as MockRouter;

      comp.versionParam = { ...createNewVersion(4), notation: 'AnalysisUnit', language: 'en' };
      comp.conceptParam = { id: 8, notation: 'Individual' };
      comp.conceptList = [
        { id: 8, notation: 'Individual' },
        { id: 9, notation: 'Household' },
      ];
      comp.isSlForm = true;
    });

    it('should start with nothing confirmed', () => {
      expect(comp.isConfirmedDeprecation).toBe(false);
      expect(comp.isConfirmedReplacementYes).toBe(false);
      expect(comp.isConfirmedReplacementNo).toBe(false);
      expect(comp.replacingCodeId).toBeUndefined();
    });

    it('should dismiss the modal on clear', () => {
      comp.clear();

      expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
    });

    it('should record each confirmation step', () => {
      comp.confirmDeprecation();
      comp.confirmReplacementYes();
      comp.confirmReplacementNo();

      expect(comp.isConfirmedDeprecation).toBe(true);
      expect(comp.isConfirmedReplacementYes).toBe(true);
      expect(comp.isConfirmedReplacementNo).toBe(true);
    });

    describe('choosing a replacement', () => {
      it('should resolve the selected id to a concept from the list', () => {
        comp.deprecateCodeForm.patchValue({ replacingCodeId: '9' });

        comp.setReplacingCode();

        expect(comp.replacingCodeId).toBe(9);
        expect(comp.replacingCode).toBe(comp.conceptList[1]);
      });

      it('should leave the replacement unset when the id is not in the list', () => {
        comp.deprecateCodeForm.patchValue({ replacingCodeId: '404' });

        comp.setReplacingCode();

        expect(comp.replacingCodeId).toBe(404);
        expect(comp.replacingCode).toBeUndefined();
      });

      it('should report whether an id and a concept have been chosen', () => {
        expect(comp.isSetReplacingCodeId()).toBe(false);
        expect(comp.isSetReplacingCode()).toBe(false);

        comp.deprecateCodeForm.patchValue({ replacingCodeId: '9' });
        comp.setReplacingCode();

        expect(comp.isSetReplacingCodeId()).toBe(true);
        expect(comp.isSetReplacingCode()).toBe(true);
      });
    });

    describe('save', () => {
      it('should deprecate without a replacement once that was confirmed', fakeAsync(() => {
        const spy = spyOn(service, 'deprecateCode').and.returnValue(of({} as never));
        comp.confirmReplacementNo();

        comp.save();
        tick();

        expect(spy).toHaveBeenCalledWith({
          actionType: ActionType.DEPRECATE_CODE,
          versionId: 4,
          conceptId: 8,
          replacedById: undefined,
        });
      }));

      it('should deprecate with the chosen replacement', fakeAsync(() => {
        const spy = spyOn(service, 'deprecateCode').and.returnValue(of({} as never));
        comp.confirmReplacementYes();
        comp.deprecateCodeForm.patchValue({ replacingCodeId: '9' });
        comp.setReplacingCode();

        comp.save();
        tick();

        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ replacedById: 9 }));
      }));

      it('should return to the vocabulary and deselect the concept', fakeAsync(() => {
        spyOn(service, 'deprecateCode').and.returnValue(of({} as never));
        comp.confirmReplacementNo();

        comp.save();
        tick();

        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['/editor/vocabulary/AnalysisUnit']);
        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith('deselectConcept');
        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
      }));

      it('should do nothing while a replacement was promised but not chosen', () => {
        const spy = spyOn(service, 'deprecateCode').and.returnValue(of({} as never));
        comp.confirmReplacementYes();

        comp.save();

        // the form still requires a replacing code
        expect(spy).not.toHaveBeenCalled();
      });

      it('should do nothing before either replacement question is answered', () => {
        const spy = spyOn(service, 'deprecateCode').and.returnValue(of({} as never));

        comp.save();

        expect(spy).not.toHaveBeenCalled();
      });

      it('should not deprecate from a translation version', () => {
        const spy = spyOn(service, 'deprecateCode').and.returnValue(of({} as never));
        comp.isSlForm = false;
        comp.confirmReplacementNo();

        comp.save();

        expect(spy).not.toHaveBeenCalled();
      });
    });
  });
});
