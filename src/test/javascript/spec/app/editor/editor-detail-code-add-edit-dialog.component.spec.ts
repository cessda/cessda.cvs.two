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
import { ComponentFixture, fakeAsync, flush, flushMicrotasks, TestBed, tick, waitForAsync } from '@angular/core/testing';
import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { EventManager } from 'app/core/util/event-manager.service';
import { HttpErrorResponse } from '@angular/common/http';
import { of, throwError } from 'rxjs';

import { CvsTestModule } from '../../test.module';
import { MockEventManager } from '../../helpers/mock-event-manager.service';
import { MockActiveModal } from '../../helpers/mock-active-modal.service';
import { MockRouter } from '../../helpers/mock-route.service';
import { MockAccountService } from '../../helpers/mock-account.service';
import { EditorDetailCodeAddEditDialogComponent } from 'app/editor/editor-detail-code-add-edit-dialog.component';
import { EditorService } from 'app/editor/editor.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { createNewVersion } from 'app/shared/model/version.model';
import { Concept } from 'app/shared/model/concept.model';
import { ActionType } from 'app/shared/model/enumerations/action-type.model';
import { CODE_ALREADY_EXIST_TYPE } from 'app/shared/constants/error.constants';
import { EditorDetailCvAddEditConfirmModalComponent } from 'app/editor/editor-detail-code-add-edit-confirm.component';

describe('Component Tests', () => {
  describe('Editor Detail Code Add Edit Dialog Component', () => {
    let comp: EditorDetailCodeAddEditDialogComponent;
    let fixture: ComponentFixture<EditorDetailCodeAddEditDialogComponent>;
    let service: EditorService;
    let mockEventManager: MockEventManager;
    let mockActiveModal: MockActiveModal;
    let mockRouter: MockRouter;
    let mockAccountService: MockAccountService;

    const account = { login: 'admin' } as Account;
    const modalService = { open: jest.fn() };

    beforeEach(waitForAsync(() => {
      modalService.open.mockReset();
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [EditorDetailCodeAddEditDialogComponent],
        providers: [{ provide: NgbModal, useValue: modalService }],
      })
        .overrideTemplate(EditorDetailCodeAddEditDialogComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(EditorDetailCodeAddEditDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(EditorService);
      mockEventManager = TestBed.inject(EventManager) as unknown as MockEventManager;
      mockActiveModal = TestBed.inject(NgbActiveModal) as unknown as MockActiveModal;
      mockRouter = TestBed.inject(Router) as unknown as MockRouter;
      mockAccountService = TestBed.inject(AccountService) as unknown as MockAccountService;
      mockAccountService.setIdentityResponse(account);

      comp.versionParam = { ...createNewVersion(7), notation: 'AnalysisUnit', language: 'en', concepts: [] };
    });

    it('should dismiss the modal on clear', () => {
      comp.clear();

      expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
    });

    describe('setting up the form', () => {
      it('should drop the notation field for a translation', () => {
        comp.isNew = false;
        comp.isSlForm = false;
        comp.conceptParam = { notation: 'Individual', title: 'Osoba', definition: 'Jednotlivec' };

        comp.ngOnInit();

        expect(comp.codeAddEditForm.controls.notation).toBeUndefined();
        expect(comp.codeAddEditForm.controls.title.value).toBe('Osoba');
        expect(comp.codeAddEditForm.controls.definition.value).toBe('Jednotlivec');
      });

      it('should show a nested code without its parent prefix when editing', () => {
        comp.isNew = false;
        comp.isSlForm = true;
        comp.conceptParam = { notation: 'A.1', parent: 'A', title: 'Child' };

        comp.ngOnInit();

        // the parent is fixed, so only the last segment is editable
        expect(comp.codeAddEditForm.controls.notation?.value).toBe('1');
      });

      it('should not ask for a change reason on a new code', () => {
        comp.isNew = true;
        comp.isSlForm = true;

        comp.ngOnInit();

        expect(comp.codeAddEditForm.controls.changeType).toBeUndefined();
        expect(comp.codeAddEditForm.controls.changeDesc).toBeUndefined();
      });

      it('should ask for a change reason when editing a code in a later version', () => {
        comp.isNew = false;
        comp.isSlForm = true;
        comp.versionParam = { ...comp.versionParam, id: 7, initialVersion: 3 };
        comp.conceptParam = { notation: 'Individual', title: 'Individual' };

        comp.ngOnInit();

        expect(comp.codeAddEditForm.controls.changeType).toBeDefined();
        expect(comp.codeAddEditForm.controls.changeDesc?.value).toBe('Individual');
      });
    });

    describe('tree preview', () => {
      it('should stay off when there are no codes yet', () => {
        comp.isNew = true;
        comp.isSlForm = true;

        comp.ngOnInit();

        expect(comp.isEnablePreview).toBe(false);
        expect(comp.codeAddEditForm.controls.codeInsertMode).toBeUndefined();
        expect(comp.codeInsertMode).toBe('INSERT_AS_ROOT');
      });

      it('should mark the selected code as the pivot and lock the rest', fakeAsync(() => {
        comp.isNew = true;
        comp.isSlForm = true;
        comp.versionParam = {
          ...comp.versionParam,
          concepts: [
            { id: 1, notation: 'A', position: 0 },
            { id: 2, notation: 'B', position: 1 },
          ],
        };
        comp.conceptParam = { id: 1, notation: 'A', position: 0 };

        comp.ngOnInit();
        tick(500);

        expect(comp.isEnablePreview).toBe(true);
        expect(comp.conceptsToPlaceTemp.find(c => c.notation === 'A')?.status).toBe('PIVOT');
        expect(comp.conceptsToPlaceTemp.find(c => c.notation === 'B')?.status).toBe('UNSELECTABLE');
        expect(comp.codeAddEditForm.controls.codeInsertMode?.value).toBe('INSERT_AFTER');
      }));

      it('should default to inserting at the root when no code is selected', fakeAsync(() => {
        comp.isNew = true;
        comp.isSlForm = true;
        comp.versionParam = { ...comp.versionParam, concepts: [{ id: 1, notation: 'A', position: 0 }] };

        comp.ngOnInit();
        tick(500);

        expect(comp.codeAddEditForm.controls.codeInsertMode?.value).toBe('INSERT_AS_ROOT');
      }));
    });

    describe('creating a code', () => {
      beforeEach(() => {
        comp.isNew = true;
        comp.isSlForm = true;
        comp.ngOnInit();
        comp.codeAddEditForm.patchValue({ notation: 'AB', title: 'A title', definition: 'A definition' });
      });

      it('should send it as a new root code', fakeAsync(() => {
        const spy = spyOn(service, 'createCode').and.returnValue(of({} as never));

        comp.save();
        tick();

        expect(spy).toHaveBeenCalled();
        expect(comp.codeSnippet).toMatchObject({
          actionType: ActionType.CREATE_CODE,
          versionId: 7,
          introducedInVersionId: 7,
          notation: 'AB',
          title: 'A title',
          definition: 'A definition',
          position: 0,
          insertionRefConceptId: undefined,
          relPosToRefConcept: 1,
        });
      }));

      it('should record the change as a code addition', fakeAsync(() => {
        spyOn(service, 'createCode').and.returnValue(of({} as never));

        comp.save();
        tick();

        expect(comp.codeSnippet?.changeType).toBe('Code added');
        expect(comp.codeSnippet?.changeDesc).toBe('AB');
      }));

      it('should leave an empty definition off the snippet', fakeAsync(() => {
        spyOn(service, 'createCode').and.returnValue(of({} as never));
        comp.codeAddEditForm.patchValue({ definition: '' });

        comp.save();
        tick();

        expect(comp.codeSnippet?.definition).toBeUndefined();
      }));

      it('should do nothing while the form is invalid', () => {
        const spy = spyOn(service, 'createCode').and.returnValue(of({} as never));
        // notation must be at least two characters
        comp.codeAddEditForm.patchValue({ notation: 'A' });

        comp.save();

        expect(spy).not.toHaveBeenCalled();
        expect(comp.isSaving).toBe(false);
        expect(comp.isSubmitting).toBe(true);
      });
    });

    describe('editing a code', () => {
      it('should prefix the notation with the unchanged parent', fakeAsync(() => {
        comp.isNew = false;
        comp.isSlForm = true;
        comp.conceptParam = { id: 9, notation: 'A.1', parent: 'A', title: 'Child' };
        comp.ngOnInit();
        comp.codeAddEditForm.patchValue({ notation: '12', title: 'Renamed' });
        spyOn(service, 'updateCode').and.returnValue(of({} as never));

        comp.save();
        tick();

        expect(comp.codeSnippet).toMatchObject({
          actionType: ActionType.EDIT_CODE,
          conceptId: 9,
          versionId: 7,
          notation: 'A.12',
          title: 'Renamed',
        });
      }));

      it('should add a translation for a code that has none', fakeAsync(() => {
        comp.isNew = false;
        comp.isSlForm = false;
        comp.conceptParam = { id: 9, notation: 'Individual' };
        comp.ngOnInit();
        comp.codeAddEditForm.patchValue({ title: 'Osoba' });
        spyOn(service, 'updateCode').and.returnValue(of({} as never));

        comp.save();
        tick();

        expect(comp.codeSnippet).toMatchObject({
          actionType: ActionType.ADD_TL_CODE,
          conceptId: 9,
          title: 'Osoba',
          changeType: 'Code translation added',
          changeDesc: 'Individual',
        });
      }));

      it('should edit a translation that already has a title', fakeAsync(() => {
        comp.isNew = false;
        comp.isSlForm = false;
        comp.conceptParam = { id: 9, notation: 'Individual', title: 'Osoba' };
        comp.ngOnInit();
        comp.codeAddEditForm.patchValue({ title: 'Jednotlivec' });
        spyOn(service, 'updateCode').and.returnValue(of({} as never));

        comp.save();
        tick();

        expect(comp.codeSnippet?.actionType).toBe(ActionType.EDIT_TL_CODE);
      }));
    });

    describe('after saving', () => {
      beforeEach(() => {
        comp.isNew = true;
        comp.isSlForm = true;
        comp.ngOnInit();
        comp.codeAddEditForm.patchValue({ notation: 'AB', title: 'A title' });
      });

      it('should return to the vocabulary and deselect the code', fakeAsync(() => {
        spyOn(service, 'createCode').and.returnValue(of({} as never));

        comp.save();
        tick();

        expect(comp.isSaving).toBe(false);
        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['/editor/vocabulary/AnalysisUnit']);
        expect(mockActiveModal.dismissSpy).toHaveBeenCalledWith(true);
        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith('deselectConcept');
      }));

      it('should keep the language when saving a translation', fakeAsync(() => {
        comp.isSlForm = false;
        spyOn(service, 'createCode').and.returnValue(of({} as never));

        comp.save();
        tick();

        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['/editor/vocabulary/AnalysisUnit'], {
          queryParams: { lang: 'en' },
        });
      }));

      it('should report a duplicate code back to the form', fakeAsync(() => {
        spyOn(service, 'createCode').and.returnValue(
          throwError(new HttpErrorResponse({ status: 400, error: { type: CODE_ALREADY_EXIST_TYPE } })),
        );

        comp.save();
        tick();

        expect(comp.errorCodeExists).toBe(true);
        expect(comp.isSaving).toBe(false);
      }));

      it('should not mistake another error for a duplicate code', fakeAsync(() => {
        spyOn(service, 'createCode').and.returnValue(throwError(new HttpErrorResponse({ status: 500, error: { type: 'something-else' } })));

        comp.save();
        tick();

        expect(comp.errorCodeExists).toBe(false);
      }));

      it('should leave the saving state when saving fails for another reason', fakeAsync(() => {
        spyOn(service, 'createCode').and.returnValue(throwError(new HttpErrorResponse({ status: 500, error: { type: 'something-else' } })));

        comp.save();
        tick();

        expect(comp.isSaving).toBe(false);
        expect(comp.errorCodeExists).toBe(false);
      }));
    });

    describe('creating a code next to a selected code', () => {
      const concepts: Concept[] = [
        { id: 1, notation: 'A', position: 0 },
        { id: 2, notation: 'A.X', parent: 'A', position: 1 },
        { id: 3, notation: 'B', position: 2 },
      ];
      let createSpy: jest.SpyInstance;

      const saveNextTo = (conceptParam: Concept, codeInsertMode: string): void => {
        comp.conceptParam = conceptParam;
        comp.ngOnInit();
        comp.codeAddEditForm.patchValue({ notation: 'NEW', title: 'New code', codeInsertMode });
        comp.save();
        flush();
      };

      beforeEach(() => {
        comp.isNew = true;
        comp.isSlForm = true;
        comp.versionParam = { ...comp.versionParam, concepts };
        createSpy = jest.spyOn(service, 'createCode').mockReturnValue(of({} as never));
      });

      it('should insert after a root code at the next position', fakeAsync(() => {
        saveNextTo(concepts[2], 'INSERT_AFTER');

        expect(createSpy).toHaveBeenCalledWith(
          expect.objectContaining({ notation: 'NEW', parent: undefined, position: 3, insertionRefConceptId: 3, relPosToRefConcept: 1 }),
        );
      }));

      it('should insert before a code at its own position', fakeAsync(() => {
        saveNextTo(concepts[2], 'INSERT_BEFORE');

        expect(createSpy).toHaveBeenCalledWith(
          expect.objectContaining({ notation: 'NEW', position: 2, insertionRefConceptId: 3, relPosToRefConcept: 0 }),
        );
      }));

      it('should insert as a child, prefixing the notation with the selected code', fakeAsync(() => {
        saveNextTo(concepts[0], 'INSERT_AS_CHILD');

        expect(createSpy).toHaveBeenCalledWith(
          expect.objectContaining({ notation: 'A.NEW', parent: 'A', position: 1, insertionRefConceptId: 1, changeDesc: 'A.NEW' }),
        );
      }));

      it('should insert after a nested code as its sibling', fakeAsync(() => {
        saveNextTo(concepts[1], 'INSERT_AFTER');

        expect(createSpy).toHaveBeenCalledWith(
          expect.objectContaining({ notation: 'A.NEW', parent: 'A', position: 2, insertionRefConceptId: 2 }),
        );
      }));
    });

    describe('placing the new code in the tree preview', () => {
      const concepts: Concept[] = [
        { id: 1, notation: 'A', position: 0 },
        { id: 2, notation: 'A.X', parent: 'A', position: 1 },
        { id: 3, notation: 'B', position: 2 },
      ];

      beforeEach(() => {
        comp.isNew = true;
        comp.isSlForm = true;
        comp.versionParam = { ...comp.versionParam, concepts };
      });

      const previewNextTo = (conceptParam: Concept, codeInsertMode: string): void => {
        comp.conceptParam = conceptParam;
        comp.ngOnInit();
        comp.codeAddEditForm.patchValue({ notation: 'NEW', codeInsertMode });
        comp.updateTreePreview();
        flush();
      };

      it('should show a child right below the selected code', fakeAsync(() => {
        previewNextTo(concepts[0], 'INSERT_AS_CHILD');

        expect(comp.conceptsToPlace).toHaveLength(4);
        expect(comp.conceptsToPlace[1]).toEqual({ notation: 'A.NEW', parent: 'A', status: 'TO_BE_INSERTED', position: 1 });
      }));

      it('should show a sibling of a nested code under the same parent', fakeAsync(() => {
        previewNextTo(concepts[1], 'INSERT_AFTER');

        expect(comp.conceptsToPlace[2]).toEqual({ notation: 'A.NEW', parent: 'A', status: 'TO_BE_INSERTED', position: 2 });
      }));

      it('should show a code inserted before another one at the position of that code', fakeAsync(() => {
        previewNextTo(concepts[2], 'INSERT_BEFORE');

        expect(comp.conceptsToPlace[2]).toEqual({ notation: 'NEW', parent: undefined, status: 'TO_BE_INSERTED', position: 2 });
        expect(comp.conceptsToPlace[3].notation).toBe('B');
      }));

      it('should leave the list of codes to choose from untouched', fakeAsync(() => {
        previewNextTo(concepts[0], 'INSERT_AS_CHILD');

        expect(comp.conceptsToPlaceTemp).toHaveLength(3);
        expect(concepts).toHaveLength(3);
      }));

      it('should scroll the new code into view once the preview is drawn', fakeAsync(() => {
        const placeholder = document.createElement('div');
        placeholder.className = 'to-be-inserted';
        placeholder.scrollIntoView = jest.fn();
        document.body.appendChild(placeholder);

        comp.conceptParam = concepts[0];
        comp.ngOnInit();
        tick(500);

        expect(placeholder.scrollIntoView).toHaveBeenCalledWith({ behavior: 'smooth' });
        placeholder.remove();
      }));

      it('should do nothing when the preview is off', () => {
        comp.isNew = false;
        comp.conceptParam = concepts[0];
        comp.ngOnInit();

        comp.updateTreePreview();

        expect(comp.conceptsToPlace).toHaveLength(0);
      });
    });

    describe('confirming a change', () => {
      let updateSpy: jest.SpyInstance;

      const confirmModalAnswering = (answer: string): void => {
        modalService.open.mockReturnValue({ result: Promise.resolve(answer) } as unknown as NgbModalRef);
      };

      beforeEach(() => {
        comp.isNew = false;
        comp.isSlForm = true;
        comp.versionParam = { ...comp.versionParam, id: 7, initialVersion: 3 };
        comp.conceptParam = { id: 9, notation: 'Individual', title: 'Individual' };
        comp.ngOnInit();
        updateSpy = jest.spyOn(service, 'updateCode').mockReturnValue(of({} as never));
      });

      it('should ask for confirmation before saving a changed code value', fakeAsync(() => {
        confirmModalAnswering('sure');
        comp.codeAddEditForm.patchValue({ changeType: 'Code value changed' });

        comp.confirmChange();
        flushMicrotasks();

        expect(modalService.open).toHaveBeenCalledWith(EditorDetailCvAddEditConfirmModalComponent);
        expect(updateSpy).toHaveBeenCalledWith(
          expect.objectContaining({ actionType: ActionType.EDIT_CODE, changeType: 'Code value changed', changeDesc: 'Individual' }),
        );
      }));

      it('should not save when the confirmation is not given', fakeAsync(() => {
        confirmModalAnswering('cancel');
        comp.codeAddEditForm.patchValue({ changeType: 'Code value changed' });

        comp.confirmChange();
        flushMicrotasks();

        expect(modalService.open).toHaveBeenCalled();
        expect(updateSpy).not.toHaveBeenCalled();
      }));

      it('should neither save nor fail when the confirmation is dismissed', fakeAsync(() => {
        modalService.open.mockReturnValue({ result: Promise.reject(new Error('dismissed')) } as unknown as NgbModalRef);
        comp.codeAddEditForm.patchValue({ changeType: 'Code value changed' });

        comp.confirmChange();

        expect(() => flushMicrotasks()).not.toThrow();
        expect(updateSpy).not.toHaveBeenCalled();
        expect(mockActiveModal.dismissSpy).not.toHaveBeenCalled();
      }));

      it('should save any other kind of change straight away', () => {
        comp.codeAddEditForm.patchValue({ changeType: 'Typo correction' });

        comp.confirmChange();

        expect(modalService.open).not.toHaveBeenCalled();
        expect(updateSpy).toHaveBeenCalledWith(expect.objectContaining({ changeType: 'Typo correction' }));
      });

      it('should not ask for confirmation while the form is invalid', () => {
        comp.codeAddEditForm.patchValue({ changeType: 'Code value changed', title: '' });

        comp.confirmChange();

        expect(modalService.open).not.toHaveBeenCalled();
        expect(updateSpy).not.toHaveBeenCalled();
        expect(comp.isSubmitting).toBe(true);
      });

      it('should save straight away when no change reason is asked for', () => {
        comp.versionParam = { ...comp.versionParam, initialVersion: 7 };
        comp.ngOnInit();

        comp.confirmChange();

        expect(comp.codeAddEditForm.controls.changeType).toBeUndefined();
        expect(modalService.open).not.toHaveBeenCalled();
        expect(updateSpy).toHaveBeenCalledWith(expect.objectContaining({ changeType: undefined, changeDesc: undefined }));
      });
    });
  });
});
