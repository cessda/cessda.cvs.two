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
import { EditorDetailCodeReorderDialogComponent } from 'app/editor/editor-detail-code-reorder-dialog.component';
import { EditorService } from 'app/editor/editor.service';
import { createNewVersion } from 'app/shared/model/version.model';
import { Concept } from 'app/shared/model/concept.model';
import { ActionType } from 'app/shared/model/enumerations/action-type.model';

describe('Component Tests', () => {
  describe('Editor Detail Code Reorder Dialog Component', () => {
    let comp: EditorDetailCodeReorderDialogComponent;
    let fixture: ComponentFixture<EditorDetailCodeReorderDialogComponent>;
    let service: EditorService;
    let mockEventManager: MockEventManager;
    let mockActiveModal: MockActiveModal;
    let mockRouter: MockRouter;

    const conceptsOf = (...concepts: Concept[]): Concept[] => concepts;

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [EditorDetailCodeReorderDialogComponent],
      })
        .overrideTemplate(EditorDetailCodeReorderDialogComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(EditorDetailCodeReorderDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(EditorService);
      mockEventManager = TestBed.inject(JhiEventManager) as unknown as MockEventManager;
      mockActiveModal = TestBed.inject(NgbActiveModal) as unknown as MockActiveModal;
      mockRouter = TestBed.inject(Router) as unknown as MockRouter;
    });

    describe('preparing a root level code', () => {
      beforeEach(() => {
        comp.versionParam = {
          ...createNewVersion(1),
          notation: 'AnalysisUnit',
          concepts: conceptsOf({ id: 1, notation: 'A' }, { id: 2, notation: 'A.1', parent: 'A' }, { id: 3, notation: 'B' }),
        };
        comp.conceptParam = { id: 1, notation: 'A' };
        comp.ngOnInit();
      });

      it('should move the code together with its descendants', () => {
        expect(comp.conceptsToMove.map(c => c.notation)).toEqual(['A', 'A.1']);
      });

      it('should mark everything it moves as being reordered', () => {
        expect(comp.conceptsToMove.every(c => c.status === 'REORDER')).toBe(true);
      });

      it('should offer the remaining codes as placement targets', () => {
        expect(comp.conceptsToPlace.map(c => c.notation)).toEqual(['B']);
      });

      it('should not touch the concepts on the version itself', () => {
        // ngOnInit works on a deep copy
        expect(comp.versionParam.concepts.map(c => c.notation)).toEqual(['A', 'A.1', 'B']);
        expect(comp.versionParam.concepts.every(c => c.status === undefined)).toBe(true);
      });
    });

    describe('preparing a nested code', () => {
      beforeEach(() => {
        comp.versionParam = {
          ...createNewVersion(1),
          notation: 'AnalysisUnit',
          concepts: conceptsOf(
            { id: 1, notation: 'A' },
            { id: 2, notation: 'A.1', parent: 'A' },
            { id: 3, notation: 'A.1.1', parent: 'A.1' },
          ),
        };
        comp.conceptParam = { id: 2, notation: 'A.1' };
        comp.ngOnInit();
      });

      it('should strip the shared parent prefix from the notations', () => {
        // A.1 becomes the new root, so the block is renumbered relative to it
        expect(comp.conceptsToMove.map(c => c.notation)).toEqual(['1', '1.1']);
      });

      it('should detach the new root and rebase its descendants', () => {
        expect(comp.conceptsToMove[0].parent).toBeUndefined();
        expect(comp.conceptsToMove[1].parent).toBe('1');
      });
    });

    describe('confirmReorder', () => {
      beforeEach(() => {
        comp.versionParam = {
          ...createNewVersion(7),
          notation: 'AnalysisUnit',
          concepts: conceptsOf({ id: 1, notation: 'A' }, { id: 3, notation: 'B' }),
        };
        comp.conceptParam = { id: 1, notation: 'A' };
        comp.ngOnInit();
      });

      it('should send the target order as notations and ids', fakeAsync(() => {
        const spy = spyOn(service, 'reorderCode').and.returnValue(of({} as never));

        comp.confirmReorder();
        tick();

        expect(spy).toHaveBeenCalledWith({
          actionType: ActionType.REORDER_CODE,
          conceptId: 1,
          versionId: 7,
          conceptStructures: ['B'],
          conceptStructureIds: [3],
        });
      }));

      it('should return to the vocabulary and deselect the concept once saved', fakeAsync(() => {
        spyOn(service, 'reorderCode').and.returnValue(of({} as never));

        comp.confirmReorder();
        tick();

        expect(comp.isSaving).toBe(false);
        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['/editor/vocabulary/AnalysisUnit']);
        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith('deselectConcept');
        expect(mockActiveModal.dismissSpy).toHaveBeenCalledWith(true);
      }));
    });

    describe('clear', () => {
      it('should dismiss the modal and drop the reorder marks', () => {
        comp.versionParam = {
          ...createNewVersion(1),
          notation: 'AnalysisUnit',
          concepts: conceptsOf({ id: 1, notation: 'A' }, { id: 3, notation: 'B' }),
        };
        comp.conceptParam = { id: 1, notation: 'A' };
        comp.ngOnInit();

        comp.clear();

        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
        expect(comp.conceptsToMove.every(c => c.status === undefined)).toBe(true);
      });
    });
  });
});
