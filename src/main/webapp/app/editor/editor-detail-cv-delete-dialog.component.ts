import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IVocabulary } from 'app/shared/model/vocabulary.model';
import { EditorService } from 'app/editor/editor.service';
import { IVersion } from 'app/shared/model/version.model';
import { Router } from '@angular/router';
import { JhiEventManager } from 'ng-jhipster';

@Component({
  templateUrl: './editor-detail-cv-delete-dialog.component.html'
})
export class EditorDetailCvDeleteDialogComponent implements OnInit {
  vocabularyParam!: IVocabulary;
  versionParam!: IVersion;
  deleteType = 'versionTl';

  constructor(
    protected editorService: EditorService,
    public activeModal: NgbActiveModal,
    private router: Router,
    protected eventManager: JhiEventManager
  ) {}

  clear(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.editorService.deleteVocabulary(id).subscribe(() => {
      if (this.deleteType === 'vocabulary') {
        this.router.navigate(['/editor']);
      } else {
        this.router.navigate(['/editor/vocabulary/' + this.versionParam.notation]);
      }
      this.activeModal.dismiss();
      this.eventManager.broadcast('deselectConcept');
    });
  }

  ngOnInit(): void {
    if (this.versionParam.itemType === 'SL') {
      this.deleteType = 'versionSl';
      if (!this.versionParam.initialVersion) {
        this.deleteType = 'vocabulary';
      }
    }
  }
}
