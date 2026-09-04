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
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';

import { CvsTestModule } from '../../test.module';
import { MockActiveModal } from '../../helpers/mock-active-modal.service';
import { MockAccountService } from '../../helpers/mock-account.service';
import { EditorDetailCvForwardStatusDialogComponent } from 'app/editor/editor-detail-cv-forward-status-dialog.component';
import { AccountService } from 'app/core/auth/account.service';
import { LicenceService } from 'app/admin/licence/licence.service';
import { Account } from 'app/core/user/account.model';
import { createNewVersion } from 'app/shared/model/version.model';

describe('Component Tests', () => {
  describe('Editor Detail CV Forward Status Dialog Component', () => {
    let comp: EditorDetailCvForwardStatusDialogComponent;
    let fixture: ComponentFixture<EditorDetailCvForwardStatusDialogComponent>;
    let mockActiveModal: MockActiveModal;
    let mockAccountService: MockAccountService;
    let licenceService: LicenceService;

    const account = { login: 'admin' } as Account;

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [EditorDetailCvForwardStatusDialogComponent],
      })
        .overrideTemplate(EditorDetailCvForwardStatusDialogComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(EditorDetailCvForwardStatusDialogComponent);
      comp = fixture.componentInstance;
      mockActiveModal = TestBed.inject(NgbActiveModal) as unknown as MockActiveModal;
      mockAccountService = TestBed.inject(AccountService) as unknown as MockAccountService;
      mockAccountService.setIdentityResponse(account);
      licenceService = TestBed.inject(LicenceService);
      spyOn(licenceService, 'query').and.returnValue(of(new HttpResponse({ body: [] })));
    });

    it('should dismiss the modal on clear', () => {
      comp.clear();

      expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
    });

    describe('a version that is not under review', () => {
      beforeEach(() => {
        comp.versionParam = { ...createNewVersion(1), status: 'DRAFT', number: '2.0.0' };
        comp.isSlForm = true;
        comp.ngOnInit();
      });

      it('should ask for nothing beyond confirming the transition', () => {
        expect(comp.cvForwardStatusForm.controls.versionNotes).toBeUndefined();
        expect(comp.cvForwardStatusForm.controls.versionChanges).toBeUndefined();
        expect(comp.cvForwardStatusForm.controls.versionNumberSl).toBeUndefined();
        expect(comp.cvForwardStatusForm.controls.licenseId).toBeUndefined();
      });

      it('should not look up licences', () => {
        expect(licenceService.query).not.toHaveBeenCalled();
      });
    });

    describe('publishing a source language version under review', () => {
      beforeEach(() => {
        comp.versionParam = { ...createNewVersion(1), status: 'REVIEW', number: '2.1.0', licenseId: 4 };
        comp.isSlForm = true;
        comp.ngOnInit();
      });

      it('should offer the version number as major and minor only', () => {
        // the SL number carries no patch, which is what the field pattern accepts
        expect(comp.cvForwardStatusForm.controls.versionNumberSl?.value).toBe('2.1');
      });

      it('should load the licences and preselect the current one', () => {
        expect(licenceService.query).toHaveBeenCalled();
        expect(comp.cvForwardStatusForm.controls.licenseId?.value).toBe(4);
      });

      it('should reject a version number that carries a patch', () => {
        comp.cvForwardStatusForm.controls.versionNumberSl?.setValue('2.1.3');

        expect(comp.cvForwardStatusForm.controls.versionNumberSl?.valid).toBe(false);
      });

      it('should accept a two part version number', () => {
        comp.cvForwardStatusForm.controls.versionNumberSl?.setValue('10.2');

        expect(comp.cvForwardStatusForm.controls.versionNumberSl?.valid).toBe(true);
      });
    });

    describe('publishing a translation under review', () => {
      beforeEach(() => {
        comp.versionParam = { ...createNewVersion(1), status: 'REVIEW', number: '2.1.4' };
        comp.isSlForm = false;
        comp.ngOnInit();
      });

      it('should not ask for a source language version number', () => {
        expect(comp.cvForwardStatusForm.controls.versionNumberSl).toBeUndefined();
      });

      it('should propose the patch number taken from the translation version', () => {
        expect(comp.proposedPatchNumber).toBe(4);
      });
    });

    describe('an initial version', () => {
      it('should not ask for version notes or changes when there is no previous version', () => {
        comp.versionParam = { ...createNewVersion(1), status: 'REVIEW', number: '1.0.0', previousVersion: undefined };
        comp.isSlForm = true;

        comp.ngOnInit();

        expect(comp.cvForwardStatusForm.controls.versionNotes).toBeUndefined();
        expect(comp.cvForwardStatusForm.controls.versionChanges).toBeUndefined();
      });
    });
  });
});
