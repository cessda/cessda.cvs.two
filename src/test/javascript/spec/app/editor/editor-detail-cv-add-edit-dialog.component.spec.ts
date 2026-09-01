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
import { HttpErrorResponse } from '@angular/common/http';
import { of, throwError } from 'rxjs';

import { CvsTestModule } from '../../test.module';
import { MockEventManager } from '../../helpers/mock-event-manager.service';
import { MockActiveModal } from '../../helpers/mock-active-modal.service';
import { MockRouter } from '../../helpers/mock-route.service';
import { MockAccountService } from '../../helpers/mock-account.service';
import { EditorDetailCvAddEditDialogComponent } from 'app/editor/editor-detail-cv-add-edit-dialog.component';
import { EditorService } from 'app/editor/editor.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { createNewVocabulary } from 'app/shared/model/vocabulary.model';
import { createNewVersion } from 'app/shared/model/version.model';
import { ActionType } from 'app/shared/model/enumerations/action-type.model';
import { VOCABULARY_ALREADY_EXIST_TYPE } from 'app/shared/constants/error.constants';

describe('Component Tests', () => {
  describe('Editor Detail CV Add Edit Dialog Component', () => {
    let comp: EditorDetailCvAddEditDialogComponent;
    let fixture: ComponentFixture<EditorDetailCvAddEditDialogComponent>;
    let service: EditorService;
    let mockEventManager: MockEventManager;
    let mockActiveModal: MockActiveModal;
    let mockRouter: MockRouter;
    let mockAccountService: MockAccountService;

    // SpyObject only creates the spies its subclass lists, so isAdmin has to be added here
    let isAdminSpy: jasmine.Spy;
    const setAdmin = (isAdmin: boolean): void => {
      isAdminSpy.and.returnValue(isAdmin);
    };

    const tlEditor = {
      login: 'editor',
      userAgencies: [{ agencyId: 5, agencyRole: 'ADMIN_TL', language: 'de' }],
    } as unknown as Account;

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [EditorDetailCvAddEditDialogComponent],
      })
        .overrideTemplate(EditorDetailCvAddEditDialogComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(EditorDetailCvAddEditDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(EditorService);
      mockEventManager = TestBed.inject(JhiEventManager) as unknown as MockEventManager;
      mockActiveModal = TestBed.inject(NgbActiveModal) as unknown as MockActiveModal;
      mockRouter = TestBed.inject(Router) as unknown as MockRouter;
      mockAccountService = TestBed.inject(AccountService) as unknown as MockAccountService;

      isAdminSpy = jasmine.createSpy('isAdmin').and.returnValue(true);
      (mockAccountService as unknown as Record<string, unknown>).isAdmin = isAdminSpy;

      comp.account = tlEditor;
      comp.vocabularyParam = createNewVocabulary({
        id: 3,
        agencyId: 5,
        notation: 'AnalysisUnit',
        sourceLanguage: 'en',
        versions: [{ ...createNewVersion(1), itemType: 'SL', language: 'en', number: '2.0.0' }],
      });
      comp.versionParam = { ...createNewVersion(9), itemType: 'SL', language: 'en', notation: 'AnalysisUnit' };
      comp.versionSlParam = { ...createNewVersion(1), itemType: 'SL', language: 'en', number: '2.0.0' };
    });

    it('should dismiss the modal on clear', () => {
      comp.clear();

      expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
    });

    it('should remember the language the user picks', () => {
      comp.changeLanguage({ target: { value: 'de' } } as unknown as Event);

      expect(comp.selectedLanguage).toBe('de');
    });

    describe('building the form', () => {
      it('should not ask a source language form for a translating agency', () => {
        comp.isNew = true;
        comp.isSlForm = true;

        comp.updateLanguageCheckbox(5);

        expect(comp.cvAddEditForm.controls.translateAgency).toBeUndefined();
        expect(comp.cvAddEditForm.controls.translateAgencyLink).toBeUndefined();
      });

      it('should not ask a translation form for a notation', () => {
        comp.isNew = true;
        comp.isSlForm = false;

        comp.updateLanguageCheckbox(5);

        expect(comp.cvAddEditForm.controls.notation).toBeUndefined();
        expect(comp.cvAddEditForm.controls.translateAgency).toBeDefined();
      });

      it('should not ask for a change reason on a new vocabulary', () => {
        comp.isNew = true;
        comp.isSlForm = true;

        comp.updateLanguageCheckbox(5);

        expect(comp.cvAddEditForm.controls.changeType).toBeUndefined();
        expect(comp.cvAddEditForm.controls.changeDesc).toBeUndefined();
      });

      it('should prefill the existing values when editing', () => {
        comp.isNew = false;
        comp.isSlForm = true;
        comp.versionParam = {
          ...comp.versionParam,
          title: 'Existing title',
          definition: 'Existing definition',
          notes: 'Existing notes',
        };

        comp.updateLanguageCheckbox(5);

        expect(comp.cvAddEditForm.controls.title.value).toBe('Existing title');
        expect(comp.cvAddEditForm.controls.definition.value).toBe('Existing definition');
        expect(comp.cvAddEditForm.controls.notes.value).toBe('Existing notes');
        expect(comp.cvAddEditForm.controls.language).toBeUndefined();
      });

      it('should ask for a change reason when editing a later version', () => {
        comp.isNew = false;
        comp.isSlForm = true;
        comp.versionParam = { ...comp.versionParam, id: 9, initialVersion: 4, notation: 'AnalysisUnit' };

        comp.updateLanguageCheckbox(5);

        expect(comp.cvAddEditForm.controls.changeType).toBeDefined();
        expect(comp.cvAddEditForm.controls.changeDesc?.value).toBe('AnalysisUnit');
      });
    });

    describe('which languages are offered', () => {
      it('should offer every free language to an admin', () => {
        comp.isNew = true;
        comp.isSlForm = false;
        setAdmin(true);

        comp.updateLanguageCheckbox(5);

        // English is taken by the existing source language version
        expect(comp.languages).not.toContain('en');
        expect(comp.languages).toContain('de');
      });

      it('should offer a translator only the language granted for that agency', () => {
        comp.isNew = true;
        comp.isSlForm = false;
        setAdmin(false);

        comp.updateLanguageCheckbox(5);

        expect(comp.languages).toEqual(['de']);
      });

      it('should offer nothing for an agency the translator has no role on', () => {
        comp.isNew = true;
        comp.isSlForm = false;
        setAdmin(false);

        comp.updateLanguageCheckbox(99);

        expect(comp.languages).toEqual([]);
      });

      it('should offer no choice at all when editing', () => {
        comp.isNew = false;
        comp.isSlForm = true;
        setAdmin(true);

        comp.updateLanguageCheckbox(5);

        expect(comp.languages).toEqual([]);
      });
    });

    describe('creating a translation', () => {
      beforeEach(() => {
        comp.isNew = true;
        comp.isSlForm = false;
        setAdmin(true);
        comp.updateLanguageCheckbox(5);
        comp.cvAddEditForm.patchValue({
          language: 'de',
          title: 'Ein Titel',
          definition: 'Eine Definition',
          translateAgency: 'GESIS',
          translateAgencyLink: 'https://www.gesis.org',
        });
      });

      it('should attach the translation to the source language version', fakeAsync(() => {
        const spy = spyOn(service, 'createVocabulary').and.returnValue(throwError(new HttpErrorResponse({ status: 500, error: {} })));

        comp.save();
        tick();

        expect(spy).toHaveBeenCalledWith(
          jasmine.objectContaining({
            actionType: ActionType.ADD_TL_CV,
            itemType: 'TL',
            language: 'de',
            notation: 'AnalysisUnit',
            versionNumber: '2.0.0',
            status: 'DRAFT',
            vocabularyId: 3,
            versionSlId: 1,
            translateAgency: 'GESIS',
            translateAgencyLink: 'https://www.gesis.org',
          }),
        );
      }));

      it('should reject a translating agency link that is not a URL', () => {
        comp.cvAddEditForm.patchValue({ translateAgencyLink: 'not a url' });

        expect(comp.cvAddEditForm.controls.translateAgencyLink?.valid).toBe(false);
      });

      it('should report a duplicate notation back to the form', fakeAsync(() => {
        spyOn(service, 'createVocabulary').and.returnValue(
          throwError(new HttpErrorResponse({ status: 400, error: { type: VOCABULARY_ALREADY_EXIST_TYPE } })),
        );

        comp.save();
        tick();

        expect(comp.errorNotationExists).toBe(true);
        expect(comp.isSaving).toBe(false);
      }));
    });

    describe('editing a vocabulary', () => {
      beforeEach(() => {
        comp.isNew = false;
        comp.isSlForm = true;
        setAdmin(true);
        comp.updateLanguageCheckbox(5);
        comp.cvAddEditForm.patchValue({ title: 'New title', definition: 'New definition' });
      });

      it('should send an edit carrying the existing identity of the version', fakeAsync(() => {
        const spy = spyOn(service, 'updateVocabulary').and.returnValue(of({} as never));

        comp.save();
        tick();

        expect(spy).toHaveBeenCalledWith(
          jasmine.objectContaining({
            actionType: ActionType.EDIT_CV,
            vocabularyId: 3,
            versionId: 9,
            language: 'en',
            itemType: 'SL',
            notation: 'AnalysisUnit',
            title: 'New title',
            definition: 'New definition',
          }),
        );
      }));

      it('should not carry a translating agency on a source language version', fakeAsync(() => {
        const spy = spyOn(service, 'updateVocabulary').and.returnValue(of({} as never));

        comp.save();
        tick();

        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ translateAgency: undefined, translateAgencyLink: undefined }));
      }));

      it('should return to the vocabulary and deselect the concept once saved', fakeAsync(() => {
        spyOn(service, 'updateVocabulary').and.returnValue(of({} as never));

        comp.save();
        tick();

        expect(comp.isSaving).toBe(false);
        expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['/editor/vocabulary/AnalysisUnit']);
        expect(mockActiveModal.dismissSpy).toHaveBeenCalledWith(true);
        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith('deselectConcept');
      }));

      it('should do nothing while the form is invalid', () => {
        const spy = spyOn(service, 'updateVocabulary').and.returnValue(of({} as never));
        comp.cvAddEditForm.patchValue({ title: '' });

        comp.save();

        expect(spy).not.toHaveBeenCalled();
        expect(comp.isSubmitting).toBe(true);
        expect(comp.isSaving).toBe(false);
      });
    });
  });
});
