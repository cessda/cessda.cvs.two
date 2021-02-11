/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

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

  confirmDelete(): void {
    let versionId = this.versionParam.id!;
    if (this.deleteType === 'vocabulary') {
      if (this.versionParam.initialVersion !== undefined && this.versionParam.initialVersion !== null) {
        versionId = this.versionParam.initialVersion;
      }
    }

    this.editorService.deleteVocabulary(versionId).subscribe(() => {
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
      if (this.versionParam.initialVersion === undefined || this.versionParam.initialVersion === this.versionParam.id) {
        this.deleteType = 'vocabulary';
      }
    }
  }
}
