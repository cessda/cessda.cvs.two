import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IUserAgency } from 'app/shared/model/user-agency.model';
import { UserAgencyService } from './user-agency.service';

@Component({
  templateUrl: './user-agency-delete-dialog.component.html'
})
export class UserAgencyDeleteDialogComponent {
  userAgency?: IUserAgency;

  constructor(
    protected userAgencyService: UserAgencyService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userAgencyService.delete(id).subscribe(() => {
      this.eventManager.broadcast('userAgencyListModification');
      this.activeModal.close();
    });
  }
}
