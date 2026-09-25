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
import { HttpResponse } from '@angular/common/http';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { of, throwError } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { MockActiveModal } from '../../../helpers/mock-active-modal.service';
import { MockEventManager } from '../../../helpers/mock-event-manager.service';
import { LicenceDeleteDialogComponent } from 'app/admin/licence/licence-delete-dialog.component';
import { LicenceService } from 'app/admin/licence/licence.service';

describe('Component Tests', () => {
  describe('Licence Delete Dialog Component', () => {
    let comp: LicenceDeleteDialogComponent;
    let fixture: ComponentFixture<LicenceDeleteDialogComponent>;
    let service: LicenceService;
    let mockActiveModal: MockActiveModal;
    let mockEventManager: MockEventManager;

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [LicenceDeleteDialogComponent],
      })
        .overrideTemplate(LicenceDeleteDialogComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(LicenceDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(LicenceService);
      mockActiveModal = TestBed.inject(NgbActiveModal) as unknown as MockActiveModal;
      mockEventManager = TestBed.inject(JhiEventManager) as unknown as MockEventManager;
    });

    it('should dismiss the dialog on cancel', () => {
      comp.cancel();

      expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
    });

    it('should delete the licence by id and close', () => {
      const spy = spyOn(service, 'delete').and.returnValue(of(new HttpResponse<unknown>({ body: null })));

      comp.confirmDelete(2);

      expect(spy).toHaveBeenCalledWith(2);
      expect(mockActiveModal.closeSpy).toHaveBeenCalled();
    });

    it('should announce the change so the list reloads', () => {
      spyOn(service, 'delete').and.returnValue(of(new HttpResponse<unknown>({ body: null })));

      comp.confirmDelete(2);

      expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith('licenceListModification');
    });

    it('should leave the dialog open when the deletion fails', () => {
      spyOn(service, 'delete').and.returnValue(throwError(() => new Error('in use')));

      comp.confirmDelete(2);

      expect(mockActiveModal.closeSpy).not.toHaveBeenCalled();
      expect(mockEventManager.broadcastSpy).not.toHaveBeenCalled();
    });
  });
});
