import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IMetadataValue } from 'app/shared/model/metadata-value.model';
import { MetadataValueService } from './metadata-value.service';

@Component({
  templateUrl: './metadata-value-delete-dialog.component.html'
})
export class MetadataValueDeleteDialogComponent {
  metadataValue?: IMetadataValue;

  constructor(
    protected metadataValueService: MetadataValueService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.metadataValueService.delete(id).subscribe(() => {
      this.eventManager.broadcast('metadataValueListModification');
      this.activeModal.close();
    });
  }
}
