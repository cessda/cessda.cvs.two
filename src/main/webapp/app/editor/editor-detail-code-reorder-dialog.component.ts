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
import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { EditorService } from 'app/editor/editor.service';
import { Version } from 'app/shared/model/version.model';
import { Router } from '@angular/router';
import { Concept } from 'app/shared/model/concept.model';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiEventWithContent } from 'ng-jhipster';
import { CodeSnippet } from 'app/shared/model/code-snippet.model';
import { ActionType } from 'app/shared/model/enumerations/action-type.model';

@Component({
  templateUrl: './editor-detail-code-reorder-dialog.component.html',
})
export class EditorDetailCodeReorderDialogComponent implements OnInit, OnDestroy {
  isSaving: boolean;
  versionParam!: Version;
  conceptParam!: Concept;
  selectedConceptToPlace!: Concept;
  eventSubscriber?: Subscription;
  codeSnippet?: CodeSnippet;
  insertMode = 'after';

  conceptsToMove: Concept[] = [];
  conceptsToPlace: Concept[] = [];
  conceptsToPlaceTemp: Concept[] = [];

  constructor(
    protected editorService: EditorService,
    public activeModal: NgbActiveModal,
    private router: Router,
    protected eventManager: JhiEventManager,
  ) {
    this.isSaving = false;
  }

  clear(): void {
    this.activeModal.dismiss();
    this.conceptsToMove.forEach(c => (c.status = undefined));
  }

  confirmReorder(): void {
    this.isSaving = true;
    this.codeSnippet = {
      actionType: ActionType.REORDER_CODE,
      conceptId: this.conceptParam.id,
      versionId: this.versionParam.id,
      conceptStructures: this.conceptsToPlace.map(c => c.notation),
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      conceptStructureIds: this.conceptsToPlace.map(c => c.id!),
    };
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    this.editorService.reorderCode(this.codeSnippet!).subscribe(() => this.onSaveSuccess());
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.router.navigate(['/editor/vocabulary/' + this.versionParam.notation]);
    // sent broadcast the no concept is selected now
    this.eventManager.broadcast('deselectConcept');
    this.activeModal.dismiss(true);
  }

  ngOnInit(): void {
    this.isSaving = false;
    // deep copy so that original concepts not affected
    const clonedConcepts = this.versionParam.concepts.map(x => Object.assign({}, x));

    // normalize selected code, so the first selected element is always a root code
    this.conceptsToMove = [
      clonedConcepts.filter(c => c.notation === this.conceptParam.notation)[0],
      ...clonedConcepts.filter(c => c.parent && c.parent.startsWith(this.conceptParam.notation)),
    ];
    // normalize selected code/code block
    const baseParent = this.conceptsToMove[0].parent;
    this.conceptsToMove.forEach(c => {
      if (baseParent !== undefined) {
        if (c.parent === this.conceptsToMove[0].parent) {
          c.parent = undefined;
        } else if (c.parent) {
          c.parent = c.parent.substring(baseParent.length + 1, c.parent.length);
        }
        c.notation = c.notation.substring(baseParent.length + 1, c.notation.length);
      }
      c.status = 'REORDER';
    });

    // initial value for preview, remove selected concepts from concept preview
    this.conceptsToPlaceTemp = clonedConcepts.filter(c => !this.conceptsToMove.includes(c));
    this.conceptsToPlace = [...this.conceptsToPlaceTemp];

    this.eventSubscriber = this.eventManager.subscribe('selectReorderConcept', (response: JhiEventWithContent<Concept>) => {
      this.selectedConceptToPlace = response.content;
      this.updatePreview();
    });
  }

  private updatePreview(): void {
    let index = this.conceptsToPlaceTemp.indexOf(this.selectedConceptToPlace);
    this.conceptsToPlace = [...this.conceptsToPlaceTemp];
    // deep copy objects array
    const conceptsToMoveCloned = this.conceptsToMove.map(x => Object.assign({ ...x }));
    if (this.insertMode !== 'before') {
      index++;
    }
    if (this.insertMode === 'child') {
      conceptsToMoveCloned.forEach(c => {
        c.parent = c.parent ? this.selectedConceptToPlace.notation + '.' + c.parent : this.selectedConceptToPlace.notation;
        c.notation = this.selectedConceptToPlace.notation + '.' + c.notation;
      });
    } else {
      conceptsToMoveCloned.forEach(c => {
        if (this.selectedConceptToPlace.parent) {
          c.parent = c.parent ? this.selectedConceptToPlace.parent + '.' + c.parent : this.selectedConceptToPlace.parent;
          c.notation = this.selectedConceptToPlace.parent + '.' + c.notation;
        }
      });
    }

    this.conceptsToPlace.splice(index, 0, ...conceptsToMoveCloned);
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventSubscriber.unsubscribe();
    }
  }

  updateTreePreview(): void {
    this.updatePreview();
  }
}
