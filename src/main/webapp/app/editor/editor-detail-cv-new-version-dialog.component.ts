import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IVocabulary } from 'app/shared/model/vocabulary.model';
import { EditorService } from 'app/editor/editor.service';
import { IVersion } from 'app/shared/model/version.model';
import { Router } from '@angular/router';
import { JhiEventManager } from 'ng-jhipster';
import VocabularyUtil from 'app/shared/util/vocabulary-util';

@Component({
  templateUrl: './editor-detail-cv-new-version-dialog.component.html'
})
export class EditorDetailCvNewVersionDialogComponent {
  vocabularyParam!: IVocabulary;
  versionParam!: IVersion;
  isSaving: boolean;

  constructor(
    protected editorService: EditorService,
    public activeModal: NgbActiveModal,
    private router: Router,
    protected eventManager: JhiEventManager
  ) {
    this.isSaving = false;
  }

  clear(): void {
    this.activeModal.dismiss();
  }

  getLangIsoFormatted(langIso: string): string {
    return VocabularyUtil.getLangIsoFormatted(langIso);
  }

  confirmCreateNewVersion(id: number): void {
    this.isSaving = true;
    this.editorService.createNewVersion(id).subscribe(() => {
      if (this.versionParam.itemType === 'SL') {
        this.router.navigate(['/editor/vocabulary/' + this.versionParam.notation]);
      } else {
        this.router.navigate(['/editor/vocabulary/' + this.versionParam.notation], { queryParams: { lang: this.versionParam.language } });
      }
      this.activeModal.dismiss();
      this.eventManager.broadcast('deselectConcept');
      this.isSaving = false;
    });
  }
}
