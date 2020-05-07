import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IVersion } from 'app/shared/model/version.model';
import { VersionService } from './version.service';

@Component({
  templateUrl: './version-delete-dialog.component.html'
})
export class VersionDeleteDialogComponent {
  version?: IVersion;

  constructor(protected versionService: VersionService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.versionService.delete(id).subscribe(() => {
      this.eventManager.broadcast('versionListModification');
      this.activeModal.close();
    });
  }
}
