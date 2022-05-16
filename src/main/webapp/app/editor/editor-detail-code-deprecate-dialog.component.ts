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

import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {EditorService} from 'app/editor/editor.service';
import {IVersion} from 'app/shared/model/version.model';
import {Router} from '@angular/router';
import {IConcept} from 'app/shared/model/concept.model';
import {Observable, Subscription} from 'rxjs';
import {JhiEventManager} from 'ng-jhipster';
import {CodeSnippet} from 'app/shared/model/code-snippet.model';
import {HttpResponse} from '@angular/common/http';
import {IVocabulary} from 'app/shared/model/vocabulary.model';

@Component({
  templateUrl: './editor-detail-code-deprecate-dialog.component.html'
})
export class EditorDetailCodeDeprecateDialogComponent {
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

  clear(): void {
    this.activeModal.dismiss();
  }

  confirmDeprecate(id: number): void {
    if (this.isSlForm) {
      this.editorService.deprecateCode(id).subscribe(() => {
        this.router.navigate(['/editor/vocabulary/' + this.versionParam.notation]);
        // sent broadcast the no concept is selected now
        this.eventManager.broadcast('deselectConcept');
        this.activeModal.dismiss();
      });
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
