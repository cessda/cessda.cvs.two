import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IVocabularyChange } from 'app/shared/model/vocabulary-change.model';
import { VocabularyChangeService } from './vocabulary-change.service';

@Component({
  templateUrl: './vocabulary-change-delete-dialog.component.html'
})
export class VocabularyChangeDeleteDialogComponent {
  vocabularyChange?: IVocabularyChange;

  constructor(
    protected vocabularyChangeService: VocabularyChangeService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.vocabularyChangeService.delete(id).subscribe(() => {
      this.eventManager.broadcast('vocabularyChangeListModification');
      this.activeModal.close();
    });
  }
}
