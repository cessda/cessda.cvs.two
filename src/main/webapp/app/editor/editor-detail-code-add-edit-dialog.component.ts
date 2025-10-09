/* eslint-disable @typescript-eslint/no-non-null-assertion */
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
import { Component, NgZone, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { AccountService } from 'app/core/auth/account.service';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { Account } from 'app/core/user/account.model';
import { CODE_ALREADY_EXIST_TYPE } from 'app/shared';
import { EditorService } from 'app/editor/editor.service';
import { Version } from 'app/shared/model/version.model';
import { Concept } from 'app/shared/model/concept.model';
import { CodeSnippet } from 'app/shared/model/code-snippet.model';
import { EditorDetailCvAddEditConfirmModalComponent } from 'app/editor/editor-detail-code-add-edit-confirm.component';
import { ActionType } from 'app/shared/model/enumerations/action-type.model';

@Component({
    selector: 'jhi-editor-detail-code-add-edit-dialog',
    templateUrl: './editor-detail-code-add-edit-dialog.component.html',
    standalone: false
})
export class EditorDetailCodeAddEditDialogComponent implements OnInit {
  isSaving: boolean;
  isSubmitting: boolean;
  account!: Account;
  languages: string[] = [];
  errorCodeExists = false;
  codeSnippet?: CodeSnippet;
  versionParam!: Version;
  conceptParam!: Concept;
  codeInsertMode?: string;
  isNew!: boolean;
  isSlForm!: boolean;
  isEnablePreview: boolean;

  conceptsToPlace: Concept[] = [];
  conceptsToPlaceTemp: Concept[] = [];

  private readonly formControls = {
    notation: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(2), Validators.maxLength(240), Validators.pattern('^[_+A-Za-z0-9-]*$')],
    }),
    title: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    definition: ['', []],
    codeInsertMode: new FormControl(''),
    changeType: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    changeDesc: new FormControl(''),
  };

  // Make 'notation', 'codeInsertMode', 'changeType' and 'changeDesc' optional
  codeAddEditForm = this.fb.group<
    Omit<typeof this.formControls, 'notation' | 'codeInsertMode' | 'changeType' | 'changeDesc'> & {
      notation?: FormControl<string>;
      codeInsertMode?: FormControl<string | null>;
      changeType?: FormControl<string>;
      changeDesc?: FormControl<string | null>;
    }
  >(this.formControls);

  constructor(
    private accountService: AccountService,
    protected editorService: EditorService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager,
    private fb: FormBuilder,
    private router: Router,
    private modalService: NgbModal,
    private _ngZone: NgZone,
  ) {
    this.isSaving = false;
    this.isSubmitting = false;
    this.isEnablePreview = false;
    this.codeInsertMode = 'INSERT_AS_ROOT';
  }

  private fillForm(): void {
    if (!this.isSlForm) {
      this.codeAddEditForm.removeControl('notation');
      this.codeAddEditForm.patchValue({
        title: this.conceptParam.title,
        definition: this.conceptParam.definition,
      });
    }
    if (!this.isNew && this.isSlForm) {
      this.codeAddEditForm.patchValue({
        notation: this.conceptParam.notation,
        title: this.conceptParam.title,
        definition: this.conceptParam.definition,
      });
      if (this.conceptParam.parent) {
        this.codeAddEditForm.patchValue({
          notation: this.conceptParam.notation.substring(this.conceptParam.parent.length + 1),
        });
      }
    }
    if (
      this.isNew ||
      !this.versionParam.initialVersion ||
      (this.versionParam.initialVersion && this.versionParam.initialVersion === this.versionParam.id)
    ) {
      this.codeAddEditForm.removeControl('changeType');
      this.codeAddEditForm.removeControl('changeDesc');
    } else {
      this.codeAddEditForm.patchValue({ changeDesc: this.conceptParam.notation });
    }
  }

  clear(): void {
    this.activeModal.dismiss();
  }

  ngOnInit(): void {
    this.isSaving = false;
    this.isSubmitting = false;
    this.accountService.identity().subscribe(account => {
      if (account) {
        this.account = account;
        this.fillForm();
      }
    });

    this.isEnablePreview = this.isNew && this.versionParam.concepts.length > 0;
    if (this.isEnablePreview) {
      // deep copy so the changes will not influence the original concepts
      this.conceptsToPlaceTemp = this.versionParam.concepts.map(x => Object.assign({ ...x }));
      this.conceptsToPlaceTemp.forEach(c => {
        if (this.conceptParam && c.notation === this.conceptParam.notation) {
          c.status = 'PIVOT';
        } else {
          c.status = 'UNSELECTABLE';
        }
      });
      if (this.conceptParam) {
        this.codeAddEditForm.patchValue({ codeInsertMode: 'INSERT_AFTER' });
      } else {
        this.codeAddEditForm.patchValue({ codeInsertMode: 'INSERT_AS_ROOT' });
      }

      this.updateTreePreview();
    } else {
      this.codeAddEditForm.removeControl('codeInsertMode');
      this.codeInsertMode = 'INSERT_AS_ROOT';
    }
  }

  private createFromForm(): CodeSnippet {
    if (this.conceptParam) {
      this.codeInsertMode = this.codeAddEditForm.controls.codeInsertMode!.value!;
    }

    const pos = this.calculatePosition();
    const [insertionRefConceptId, relPosToRefConcept] = this.getConceptInsertionAddress();

    const codeSnippet: CodeSnippet = {
      actionType: ActionType.CREATE_CODE,
      versionId: this.versionParam.id,
      introducedInVersionId: this.versionParam.id,
      title: this.codeAddEditForm.controls.title.value,
      definition: this.codeAddEditForm.controls.definition.value || undefined,
      position: pos,
      insertionRefConceptId,
      relPosToRefConcept,
    };
    if (this.codeInsertMode === 'INSERT_AS_ROOT') {
      codeSnippet.notation = this.codeAddEditForm.controls.notation?.value;
    } else if (this.codeInsertMode === 'INSERT_AS_CHILD') {
      codeSnippet.parent = this.conceptParam.notation;
      codeSnippet.notation = codeSnippet.parent + '.' + this.codeAddEditForm.controls.notation!.value;
    } else {
      codeSnippet.parent = this.conceptParam.parent;
      codeSnippet.notation =
        (codeSnippet.parent !== undefined ? codeSnippet.parent + '.' : '') + this.codeAddEditForm.controls.notation!.value;
    }
    codeSnippet.changeType = 'Code added';
    codeSnippet.changeDesc = codeSnippet.notation;
    return codeSnippet;
  }

  private updateFromForm(): CodeSnippet {
    if (this.isSlForm) {
      const cdSnippet: CodeSnippet = {
        ...this.codeSnippet,
        actionType: ActionType.EDIT_CODE,
        conceptId: this.conceptParam.id,
        versionId: this.versionParam.id,
        notation: this.codeAddEditForm.controls.notation?.value,
        title: this.codeAddEditForm.controls.title.value,
        definition: this.codeAddEditForm.controls.definition.value || undefined,
        changeType: this.codeAddEditForm.controls.changeType?.value || undefined,
        changeDesc: this.codeAddEditForm.controls.changeDesc?.value || undefined,
      };
      if (this.conceptParam.parent) {
        cdSnippet.notation = this.conceptParam.parent + '.' + this.codeAddEditForm.controls.notation?.value;
      }
      return cdSnippet;
    } else {
      const cdSnippet: CodeSnippet = {
        ...this.codeSnippet,
        actionType: ActionType.ADD_TL_CODE,
        conceptId: this.conceptParam.id,
        versionId: this.versionParam.id,
        title: this.codeAddEditForm.controls.title.value,
        definition: this.codeAddEditForm.controls.definition.value || undefined,
        changeType: this.codeAddEditForm.controls.changeType?.value || 'Code translation added',
        changeDesc: this.codeAddEditForm.controls.changeDesc?.value || this.conceptParam.notation,
      };
      if (this.conceptParam.title) {
        cdSnippet.actionType = ActionType.EDIT_TL_CODE;
      }
      return cdSnippet;
    }
  }

  private calculatePosition(): number {
    let index = this.versionParam.concepts.length;
    if (this.conceptParam) {
      index = (this.conceptParam.position || 0) + 1;
    }

    if (this.codeInsertMode === 'INSERT_BEFORE') {
      index--;
    }
    return index;
  }

  private getConceptInsertionAddress(): [number | undefined, number] {
    let refConceptId = undefined;
    let relativePos = 1;
    if (this.conceptParam) {
      refConceptId = this.conceptParam.id;
    }
    if (this.codeInsertMode === 'INSERT_BEFORE') {
      relativePos = 0;
    }
    return [refConceptId, relativePos];
  }

  confirmChange(): void {
    if (this.codeAddEditForm.valid && this.codeAddEditForm.controls.changeType) {
      if (this.codeAddEditForm.controls.changeType.value === 'Code value changed') {
        const ngbModalRef: NgbModalRef = this.modalService.open(EditorDetailCvAddEditConfirmModalComponent);
        ngbModalRef.result
          .then(result => {
            if (result === 'sure') {
              this.save();
            }
          })
          .catch(err => {
            throw err;
          });
      } else {
        this.save();
      }
    } else {
      this.save();
    }
  }

  save(): void {
    this.isSubmitting = true;
    if (this.codeAddEditForm.valid) {
      this.isSaving = true;
      this.errorCodeExists = false;
      if (this.isNew) {
        this.codeSnippet = this.createFromForm();
        this.subscribeToSaveResponse(this.editorService.createCode(this.codeSnippet));
      } else {
        this.codeSnippet = this.updateFromForm();
        this.subscribeToSaveResponse(this.editorService.updateCode(this.codeSnippet));
      }
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<Concept>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      response => this.processError(response),
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    if (this.isSlForm) {
      this.router.navigate(['/editor/vocabulary/' + this.versionParam.notation]);
    } else {
      this.router.navigate(['/editor/vocabulary/' + this.versionParam.notation], { queryParams: { lang: this.versionParam.language } });
    }
    this.activeModal.dismiss(true);
    this.eventManager.broadcast('deselectConcept');
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  private processError(response: HttpErrorResponse): void {
    if (response.status === 400 && response.error.type === CODE_ALREADY_EXIST_TYPE) {
      this.errorCodeExists = true;
      this.isSaving = false;
    }
  }

  updateTreePreview(): void {
    if (!this.isEnablePreview) {
      return;
    }

    this.conceptsToPlace = [...(this.conceptsToPlaceTemp || [])]; // shallow copy for preview

    this.codeInsertMode = this.codeAddEditForm.controls.codeInsertMode?.value || undefined;

    const pos = this.calculatePosition();

    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    const notationFormValue: string = this.codeAddEditForm.controls.notation!.value;

    let parent: string | undefined;
    let notation: string;

    if (this.codeInsertMode === 'INSERT_AS_ROOT') {
      parent = undefined;
      notation = notationFormValue;
    } else if (this.codeInsertMode === 'INSERT_AS_CHILD') {
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      parent = this.conceptParam.notation;
      notation = parent + '.' + notationFormValue;
    } else {
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      parent = this.conceptParam.parent;
      notation = (parent !== undefined ? parent + '.' : '') + notationFormValue;
    }

    const newConcept: Concept = {
      notation: notation,
      parent: parent,
      status: 'TO_BE_INSERTED',
      position: pos,
    };

    this.conceptsToPlace.splice(pos, 0, newConcept);
    this._ngZone.runOutsideAngular(() => {
      setTimeout(() => {
        const element = document.querySelector('.to-be-inserted');
        if (element) {
          element.scrollIntoView({ behavior: 'smooth' });
        }
      }, 500);
    });
  }
}
