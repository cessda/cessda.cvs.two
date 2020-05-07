import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IConcept } from 'app/shared/model/concept.model';
import { ConceptService } from './concept.service';

@Component({
  templateUrl: './concept-delete-dialog.component.html'
})
export class ConceptDeleteDialogComponent {
  concept?: IConcept;

  constructor(protected conceptService: ConceptService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.conceptService.delete(id).subscribe(() => {
      this.eventManager.broadcast('conceptListModification');
      this.activeModal.close();
    });
  }
}
