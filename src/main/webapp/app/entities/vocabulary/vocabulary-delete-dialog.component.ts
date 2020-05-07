import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IVocabulary } from 'app/shared/model/vocabulary.model';
import { VocabularyService } from './vocabulary.service';

@Component({
  templateUrl: './vocabulary-delete-dialog.component.html'
})
export class VocabularyDeleteDialogComponent {
  vocabulary?: IVocabulary;

  constructor(
    protected vocabularyService: VocabularyService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.vocabularyService.delete(id).subscribe(() => {
      this.eventManager.broadcast('vocabularyListModification');
      this.activeModal.close();
    });
  }
}
