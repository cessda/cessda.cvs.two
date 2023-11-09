/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { EditorService } from 'app/editor/editor.service';
import { Version } from 'app/shared/model/version.model';
import { Router } from '@angular/router';
import { Concept } from 'app/shared/model/concept.model';
import { Observable, Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { CodeSnippet } from 'app/shared/model/code-snippet.model';
import { HttpResponse } from '@angular/common/http';

@Component({
  templateUrl: './editor-detail-code-delete-dialog.component.html',
})
export class EditorDetailCodeDeleteDialogComponent {
  versionParam!: Version;
  conceptParam!: Concept;
  eventSubscriber?: Subscription;
  isSlForm?: boolean;

  constructor(
    protected editorService: EditorService,
    public activeModal: NgbActiveModal,
    private router: Router,
    protected eventManager: JhiEventManager,
  ) {}

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
        definition: null,
      };
      this.subscribeToSaveResponse(this.editorService.updateCode(cdSnippet));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<unknown>>): void {
    result.subscribe(() => {
      this.router.navigate(['/editor/vocabulary/' + this.versionParam.notation], { queryParams: { lang: this.versionParam.language } });
      this.activeModal.dismiss(true);
      this.eventManager.broadcast('deselectConcept');
    });
  }
}
