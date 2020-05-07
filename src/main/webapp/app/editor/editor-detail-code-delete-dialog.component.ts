import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { EditorService } from 'app/editor/editor.service';
import { IVersion } from 'app/shared/model/version.model';
import { Router } from '@angular/router';
import { IConcept } from 'app/shared/model/concept.model';
import { Observable, Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import VocabularyUtil from 'app/shared/util/vocabulary-util';
import { CodeSnippet } from 'app/shared/model/code-snippet.model';
import { HttpResponse } from '@angular/common/http';
import { IVocabulary } from 'app/shared/model/vocabulary.model';

@Component({
  templateUrl: './editor-detail-code-delete-dialog.component.html'
})
export class EditorDetailCodeDeleteDialogComponent {
  versionParam!: IVersion;
  conceptParam!: IConcept;
  eventSubscriber?: Subscription;
  isSlForm?: boolean;

  constructor(
    protected editorService: EditorService,
    public activeModal: NgbActiveModal,
    private router: Router,
    protected eventManager: JhiEventManager
  ) {}

  getLangIsoFormatted(langIso: string): string {
    return VocabularyUtil.getLangIsoFormatted(langIso);
  }

  clear(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    if (this.isSlForm) {
      this.editorService.deleteCode(id).subscribe(() => {
        this.router.navigate(['/editor/vocabulary/' + this.versionParam.notation]);
        // sent broadcast the no concept is selected now
        this.eventManager.broadcast('deselectConcept');
        this.activeModal.dismiss();
      });
    } else {
      const cdSnippet = {
        ...new CodeSnippet(),
        actionType: 'DELETE_TL_CODE',
        conceptId: this.conceptParam.id,
        versionId: this.versionParam.id,
        title: null,
        definition: null
      };
      this.subscribeToSaveResponse(this.editorService.updateCode(cdSnippet));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVocabulary>>): void {
    result.subscribe(() => {
      this.router.navigate(['/editor/vocabulary/' + this.versionParam.notation], { queryParams: { lang: this.versionParam.language } });
      this.activeModal.dismiss(true);
      this.eventManager.broadcast('deselectConcept');
    });
  }
}
