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
