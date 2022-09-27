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

import {Component, OnDestroy, OnInit} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {EditorService} from 'app/editor/editor.service';
import {IVersion} from 'app/shared/model/version.model';
import {Router} from '@angular/router';
import {IConcept} from 'app/shared/model/concept.model';
import {FormBuilder, Validators} from '@angular/forms';
import {Observable, Subscription} from 'rxjs';
import {JhiEventManager} from 'ng-jhipster';
import {CodeSnippet, ICodeSnippet } from 'app/shared/model/code-snippet.model';
import {HttpResponse} from '@angular/common/http';
import {IVocabulary} from 'app/shared/model/vocabulary.model';

@Component({
  selector: 'jhi-editor-detail-code-deprecate-dialog',
  templateUrl: './editor-detail-code-deprecate-dialog.component.html'
})
export class EditorDetailCodeDeprecateDialogComponent implements OnInit {
  versionParam!: IVersion;
  conceptParam!: IConcept;
  eventSubscriber?: Subscription;
  isSlForm?: boolean;
  isConfirmedDeprecation?: boolean;
  isConfirmedReplacementYes?: boolean;
  isConfirmedReplacementNo?: boolean;
  conceptList!: IConcept[];
  replacingCodeId: number | null;
  replacingCode?: IConcept;
  codeSnippet?: ICodeSnippet;

  deprecateCodeForm = this.fb.group({
    replacingCodeId: ['', [Validators.required]]
  });

  constructor(
    protected editorService: EditorService,
    public activeModal: NgbActiveModal,
    private router: Router,
    protected eventManager: JhiEventManager,
    private fb: FormBuilder,
  ) {
    this.isConfirmedDeprecation = false;
    this.isConfirmedReplacementYes = false;
    this.isConfirmedReplacementNo = false;
    this.replacingCodeId = null;
  }

  ngOnInit(): void {
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
    this.replacingCodeId = Number(this.deprecateCodeForm.get(['replacingCodeId'])!.value);
    for (let i=0; i<this.conceptList.length; i++) {
        if (this.conceptList[i].id === this.replacingCodeId) {
          this.replacingCode = this.conceptList[i];
          break;
        }
    }
  }

  save(): void {
    if (this.isSlForm) {
      if (this.isConfirmedReplacementNo || (this.isConfirmedReplacementYes && this.deprecateCodeForm.valid)) {
        this.codeSnippet = {
          ...new CodeSnippet(),
          actionType: 'DEPRECATE_CODE',
          versionId: this.versionParam.id,
          conceptId: this.conceptParam.id,
          replacedById: this.replacingCodeId
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

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVocabulary>>): void {
    result.subscribe(() => {
      this.router.navigate(['/editor/vocabulary/' + this.versionParam.notation], { queryParams: { lang: this.versionParam.language } });
      this.activeModal.dismiss(true);
      this.eventManager.broadcast('deselectConcept');
    });
  }
}
