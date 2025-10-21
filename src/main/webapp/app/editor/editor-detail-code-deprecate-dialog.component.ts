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
import { Component, inject } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { EditorService } from 'app/editor/editor.service';
import { Version } from 'app/shared/model/version.model';
import { Router } from '@angular/router';
import { Concept } from 'app/shared/model/concept.model';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { Observable, Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { CodeSnippet } from 'app/shared/model/code-snippet.model';
import { HttpResponse } from '@angular/common/http';
import { Vocabulary } from 'app/shared/model/vocabulary.model';
import { ActionType } from 'app/shared/model/enumerations/action-type.model';

@Component({
  selector: 'jhi-editor-detail-code-deprecate-dialog',
  templateUrl: './editor-detail-code-deprecate-dialog.component.html',
  standalone: false,
})
export class EditorDetailCodeDeprecateDialogComponent {
  protected editorService = inject(EditorService);
  activeModal = inject(NgbActiveModal);
  private router = inject(Router);
  protected eventManager = inject(JhiEventManager);
  private fb = inject(FormBuilder);

  versionParam!: Version;
  conceptParam!: Concept;
  eventSubscriber?: Subscription;
  isSlForm?: boolean;
  isConfirmedDeprecation?: boolean;
  isConfirmedReplacementYes?: boolean;
  isConfirmedReplacementNo?: boolean;
  conceptList!: Concept[];
  replacingCodeId?: number;
  replacingCode?: Concept;
  codeSnippet?: CodeSnippet;

  deprecateCodeForm = this.fb.group({
    replacingCodeId: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });

  constructor() {
    this.isConfirmedDeprecation = false;
    this.isConfirmedReplacementYes = false;
    this.isConfirmedReplacementNo = false;
    this.replacingCodeId = undefined;
  }

  clear(): void {
    this.activeModal.dismiss();
  }

  confirmDeprecation(): void {
    this.isConfirmedDeprecation = true;
  }

  confirmReplacementYes(): void {
    this.isConfirmedReplacementYes = true;
  }

  confirmReplacementNo(): void {
    this.isConfirmedReplacementNo = true;
  }

  isSetReplacingCodeId(): boolean {
    return this.replacingCodeId != null && this.replacingCodeId >= 0;
  }

  isSetReplacingCode(): boolean {
    return this.replacingCode !== undefined;
  }

  setReplacingCode(): void {
    this.replacingCodeId = Number(this.deprecateCodeForm.controls.replacingCodeId.value);
    for (const concept of this.conceptList) {
      if (concept.id === this.replacingCodeId) {
        this.replacingCode = concept;
        break;
      }
    }
  }

  save(): void {
    if (this.isSlForm) {
      if (this.isConfirmedReplacementNo || (this.isConfirmedReplacementYes && this.deprecateCodeForm.valid)) {
        this.codeSnippet = {
          actionType: ActionType.DEPRECATE_CODE,
          versionId: this.versionParam.id,
          conceptId: this.conceptParam.id,
          replacedById: this.replacingCodeId,
        };
        this.editorService.deprecateCode(this.codeSnippet).subscribe(() => {
          this.router.navigate(['/editor/vocabulary/' + this.versionParam.notation]);
          // sent broadcast the no concept is selected now
          this.eventManager.broadcast('deselectConcept');
          this.activeModal.dismiss();
        });
      }
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<Vocabulary>>): void {
    result.subscribe(() => {
      this.router.navigate(['/editor/vocabulary/' + this.versionParam.notation], { queryParams: { lang: this.versionParam.language } });
      this.activeModal.dismiss(true);
      this.eventManager.broadcast('deselectConcept');
    });
  }
}
