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
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { of, throwError } from 'rxjs';

import { CvsTestModule } from '../../test.module';
import { MockActiveModal } from '../../helpers/mock-active-modal.service';
import { MockAccountService } from '../../helpers/mock-account.service';
import { EditorCvAddDialogComponent } from 'app/editor/editor-cv-add-dialog.component';
import { EditorService } from 'app/editor/editor.service';
import { AgencyService } from 'app/agency/agency.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { Agency } from 'app/shared/model/agency.model';
import { ActionType } from 'app/shared/model/enumerations/action-type.model';
import { VOCABULARY_ALREADY_EXIST_TYPE } from 'app/shared/constants/error.constants';

describe('Component Tests', () => {
  describe('Editor CV Add Dialog Component', () => {
    let comp: EditorCvAddDialogComponent;
    let fixture: ComponentFixture<EditorCvAddDialogComponent>;
    let editorService: EditorService;
    let agencyService: AgencyService;
    let mockActiveModal: MockActiveModal;
    let mockAccountService: MockAccountService;

    const agencies = [{ id: 1, name: 'CESSDA' } as Agency, { id: 2, name: 'GESIS' } as Agency];

    // a user who may only create source language vocabularies for GESIS, in German
    const slEditor = {
      login: 'editor',
      userAgencies: [
        { agencyId: 2, agencyRole: 'ADMIN_SL', language: 'de' },
        { agencyId: 1, agencyRole: 'ADMIN_TL', language: 'en' },
      ],
    } as unknown as Account;

    // SpyObject only creates the spies its subclass lists, so isAdmin has to be added here
    let isAdminSpy: jasmine.Spy;
    const setAdmin = (isAdmin: boolean): void => {
      isAdminSpy.and.returnValue(isAdmin);
    };

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [EditorCvAddDialogComponent],
      })
        .overrideTemplate(EditorCvAddDialogComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(EditorCvAddDialogComponent);
      comp = fixture.componentInstance;
      editorService = fixture.debugElement.injector.get(EditorService);
      agencyService = fixture.debugElement.injector.get(AgencyService);
      mockActiveModal = TestBed.inject(NgbActiveModal) as unknown as MockActiveModal;
      mockAccountService = TestBed.inject(AccountService) as unknown as MockAccountService;

      isAdminSpy = jasmine.createSpy('isAdmin').and.returnValue(false);
      (mockAccountService as unknown as Record<string, unknown>).isAdmin = isAdminSpy;

      comp.account = slEditor;
      spyOn(agencyService, 'query').and.returnValue(of(new HttpResponse({ body: agencies })));
    });

    it('should dismiss the modal as cancelled on clear', () => {
      comp.clear();

      expect(mockActiveModal.dismissSpy).toHaveBeenCalledWith('cancel');
    });

    describe('which agencies the user may publish under', () => {
      it('should offer every agency to an admin', () => {
        setAdmin(true);

        expect(comp.filterAgencies(agencies)).toEqual(agencies);
      });

      it('should offer only the agencies the user administers a source language for', () => {
        setAdmin(false);

        // ADMIN_TL on CESSDA is not enough to create a vocabulary
        expect(comp.filterAgencies(agencies)).toEqual([agencies[1]]);
      });

      it('should offer nothing when the user administers no source language', () => {
        setAdmin(false);
        comp.account = { login: 'x', userAgencies: [] } as unknown as Account;

        expect(comp.filterAgencies(agencies)).toEqual([]);
      });
    });

    describe('which languages the user may use', () => {
      it('should offer every ISO language to an admin', () => {
        setAdmin(true);

        comp.updateLanguageCheckbox(1);

        expect(comp.languages).toContain('en');
        expect(comp.languages).toContain('de');
        expect(comp.languages.length).toBeGreaterThan(20);
      });

      it('should offer only the languages granted for that agency', () => {
        setAdmin(false);

        comp.updateLanguageCheckbox(2);

        expect(comp.languages).toEqual(['de']);
      });

      it('should offer nothing for an agency the user has no source language role on', () => {
        setAdmin(false);

        comp.updateLanguageCheckbox(1);

        expect(comp.languages).toEqual([]);
      });

      it('should preselect the first available language', () => {
        setAdmin(false);

        comp.updateLanguageCheckbox(2);

        expect(comp.cvAddForm.controls.sourceLanguage.value).toBe('de');
      });
    });

    describe('loadAgencies', () => {
      it('should preselect the first agency and its languages', () => {
        setAdmin(false);

        comp.loadAgencies();

        expect(comp.agencies).toEqual([agencies[1]]);
        expect(comp.cvAddForm.controls.agency.value).toBe(2);
        expect(comp.cvAddForm.controls.sourceLanguage.value).toBe('de');
      });
    });

    describe('saving a new vocabulary', () => {
      beforeEach(() => {
        setAdmin(true);
        comp.cvAddForm.patchValue({
          agency: 2,
          sourceLanguage: 'de',
          notation: 'NewVocab',
          title: 'A title',
          definition: 'A definition',
          notes: 'Some notes',
        });
      });

      it('should create a draft source language version numbered 1.0.0', fakeAsync(() => {
        const spy = spyOn(editorService, 'createVocabulary').and.returnValue(throwError(new HttpErrorResponse({ status: 500, error: {} })));

        comp.save();
        tick();

        expect(spy).toHaveBeenCalledWith({
          actionType: ActionType.CREATE_CV,
          agencyId: 2,
          language: 'de',
          itemType: 'SL',
          notation: 'NewVocab',
          versionNumber: '1.0.0',
          status: 'DRAFT',
          title: 'A title',
          definition: 'A definition',
          notes: 'Some notes',
        });
      }));

      it('should leave empty notes off the snippet', fakeAsync(() => {
        const spy = spyOn(editorService, 'createVocabulary').and.returnValue(throwError(new HttpErrorResponse({ status: 500, error: {} })));
        comp.cvAddForm.patchValue({ notes: null });

        comp.save();
        tick();

        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ notes: undefined }));
      }));

      it('should report a duplicate notation back to the form', fakeAsync(() => {
        spyOn(editorService, 'createVocabulary').and.returnValue(
          throwError(new HttpErrorResponse({ status: 400, error: { type: VOCABULARY_ALREADY_EXIST_TYPE } })),
        );

        comp.save();
        tick();

        expect(comp.errorNotationExists).toBe(true);
      }));

      it('should not mistake another error for a duplicate notation', fakeAsync(() => {
        spyOn(editorService, 'createVocabulary').and.returnValue(
          throwError(new HttpErrorResponse({ status: 500, error: { type: 'something-else' } })),
        );

        comp.save();
        tick();

        expect(comp.errorNotationExists).toBe(false);
      }));
    });

    describe('the notation field', () => {
      it('should reject a notation shorter than two characters', () => {
        comp.cvAddForm.patchValue({ notation: 'A' });

        expect(comp.cvAddForm.controls.notation.valid).toBe(false);
      });

      it('should reject a notation with characters outside the allowed set', () => {
        comp.cvAddForm.patchValue({ notation: 'Not Allowed!' });

        expect(comp.cvAddForm.controls.notation.valid).toBe(false);
      });

      it('should accept letters, digits, plus and hyphen', () => {
        comp.cvAddForm.patchValue({ notation: 'Topic-Classification+1' });

        expect(comp.cvAddForm.controls.notation.valid).toBe(true);
      });
    });
  });
});
