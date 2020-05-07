import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IMetadataField } from 'app/shared/model/metadata-field.model';
import { MetadataFieldService } from './metadata-field.service';

@Component({
  templateUrl: './metadata-field-delete-dialog.component.html'
})
export class MetadataFieldDeleteDialogComponent {
  metadataField?: IMetadataField;

  constructor(
    protected metadataFieldService: MetadataFieldService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.metadataFieldService.delete(id).subscribe(() => {
      this.eventManager.broadcast('metadataFieldListModification');
      this.activeModal.close();
    });
  }
}
