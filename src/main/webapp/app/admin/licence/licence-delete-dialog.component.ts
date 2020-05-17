import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ILicence } from 'app/shared/model/licence.model';
import { LicenceService } from './licence.service';

@Component({
  templateUrl: './licence-delete-dialog.component.html'
})
export class LicenceDeleteDialogComponent {
  licence?: ILicence;

  constructor(protected licenceService: LicenceService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.licenceService.delete(id).subscribe(() => {
      this.eventManager.broadcast('licenceListModification');
      this.activeModal.close();
    });
  }
}
