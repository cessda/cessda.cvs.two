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

import {Component, NgZone, OnInit} from '@angular/core';
import {Router} from '@angular/router';

import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiEventManager} from 'ng-jhipster';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {AccountService} from 'app/core/auth/account.service';
import {FormBuilder, Validators} from '@angular/forms';
import {Observable} from 'rxjs';
import {Account} from 'app/core/user/account.model';
import {CODE_ALREADY_EXIST_TYPE} from 'app/shared';
import {EditorService} from 'app/editor/editor.service';
import {IVersion} from 'app/shared/model/version.model';
import {Concept, IConcept} from 'app/shared/model/concept.model';
import {CodeSnippet, ICodeSnippet} from 'app/shared/model/code-snippet.model';

@Component({
  selector: 'jhi-editor-detail-code-add-edit-dialog',
  templateUrl: './editor-detail-code-add-edit-dialog.component.html'
})
export class EditorDetailCodeAddEditDialogComponent implements OnInit {
  isSaving: boolean;
  account!: Account;
  languages: string[] = [];
  errorCodeExists = false;
  codeSnippet?: ICodeSnippet;
  versionParam!: IVersion;
  conceptParam?: IConcept;
  codeInsertMode?: string;
  isNew?: boolean;
  isSlForm?: boolean;
  isEnablePreview: boolean;

  conceptsToPlace?: IConcept[] = [];
  conceptsToPlaceTemp?: IConcept[] = [];

  codeAddEditForm = this.fb.group({
    notation: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(240), Validators.pattern('^[_+A-Za-z0-9-]*$')]],
    title: ['', [Validators.required]],
    definition: ['', []],
    codeInsertMode: [],
    changeType: ['', [Validators.required]],
    changeDesc: []
  });

  constructor(
    private accountService: AccountService,
    protected editorService: EditorService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager,
    private fb: FormBuilder,
    private router: Router,
    private _ngZone: NgZone
  ) {
    this.isSaving = false;
    this.isEnablePreview = false;
    this.codeInsertMode = 'INSERT_AS_ROOT';
  }

  private fillForm(): void {
    if (!this.isSlForm) {
      this.codeAddEditForm.removeControl('notation');
      this.codeAddEditForm.patchValue({
        title: this.conceptParam!.title,
        definition: this.conceptParam!.definition
      });
    }
    if (!this.isNew && this.isSlForm) {
      this.codeAddEditForm.patchValue({
        notation: this.conceptParam!.notation,
        title: this.conceptParam!.title,
        definition: this.conceptParam!.definition
      });
      if (this.conceptParam!.parent) {
        this.codeAddEditForm.patchValue({
          notation: this.conceptParam!.notation!.substring(this.conceptParam!.parent.length + 1)
        });
      }
    }
    if (
      this.isNew ||
      !this.versionParam.initialVersion ||
      (this.versionParam.initialVersion && this.versionParam.initialVersion === this.versionParam.id) ||
      this.conceptParam!.previousConcept === undefined ||
      this.conceptParam!.previousConcept === null
    ) {
      this.codeAddEditForm.removeControl('changeType');
      this.codeAddEditForm.removeControl('changeDesc');
    } else {
      this.codeAddEditForm.patchValue({ changeDesc: this.conceptParam!.notation });
    }
  }

  clear(): void {
    this.activeModal.dismiss();
  }

  ngOnInit(): void {
    this.isSaving = false;
    this.accountService.identity().subscribe(account => {
      if (account) {
        this.account = account;
        this.fillForm();
      }
    });

    this.isEnablePreview = (this.isNew && this.versionParam.concepts!.length > 0)!;
    if (this.isEnablePreview) {
      // deep copy so the changes will not influence the original concepts
      this.conceptsToPlaceTemp = this.versionParam.concepts!.map(x => Object.assign({ ...x }));
      this.conceptsToPlaceTemp.forEach(c => {
        if (this.conceptParam && c.notation === this.conceptParam.notation) {
          c.status = 'PIVOT';
        } else {
          c.status = 'UNSELECTABLE';
        }
      });
      if (this.conceptParam! !== null) {
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

  private createFromForm(): ICodeSnippet {
    if (this.isNew) {
      if (this.conceptParam != null) {
        this.codeInsertMode = this.codeAddEditForm.get('codeInsertMode')!.value;
      }

      const pos = this.calculatePosition();

      const codeSnippet = {
        ...new CodeSnippet(),
        actionType: 'CREATE_CODE',
        versionId: this.versionParam.id,
        title: this.codeAddEditForm.get(['title'])!.value,
        definition: this.codeAddEditForm.get(['definition'])!.value,
        position: pos
      };
      if (this.codeInsertMode === 'INSERT_AS_ROOT') {
        codeSnippet.notation = this.codeAddEditForm.get(['notation'])!.value;
      } else if (this.codeInsertMode === 'INSERT_AS_CHILD') {
        codeSnippet.parent = this.conceptParam!.notation;
        codeSnippet.notation = codeSnippet.parent + '.' + this.codeAddEditForm.get(['notation'])!.value;
      } else {
        codeSnippet.parent = this.conceptParam!.parent;
        codeSnippet.notation =
          (codeSnippet.parent !== undefined ? codeSnippet.parent + '.' : '') + this.codeAddEditForm.get(['notation'])!.value;
      }
      codeSnippet.changeType = 'Code added';
      codeSnippet.changeDesc = codeSnippet.notation;
      return codeSnippet;
    } else {
      if (this.isSlForm) {
        const cdSnippet = {
          ...this.codeSnippet,
          actionType: 'EDIT_CODE',
          conceptId: this.conceptParam!.id,
          versionId: this.versionParam.id,
          notation: this.codeAddEditForm.get(['notation'])!.value,
          title: this.codeAddEditForm.get(['title'])!.value,
          definition: this.codeAddEditForm.get(['definition'])!.value,
          changeType: this.codeAddEditForm.get('changeType') ? this.codeAddEditForm.get('changeType')!.value : undefined,
          changeDesc: this.codeAddEditForm.get('changeDesc') ? this.codeAddEditForm.get('changeDesc')!.value : undefined
        };
        if (this.conceptParam!.parent) {
          cdSnippet.notation = this.conceptParam!.parent + '.' + this.codeAddEditForm.get(['notation'])!.value;
        }
        return cdSnippet;
      } else {
        const cdSnippet = {
          ...this.codeSnippet,
          actionType: 'ADD_TL_CODE',
          conceptId: this.conceptParam!.id,
          versionId: this.versionParam.id,
          title: this.codeAddEditForm.get(['title'])!.value,
          definition: this.codeAddEditForm.get(['definition'])!.value,
          changeType: this.codeAddEditForm.get('changeType') ? this.codeAddEditForm.get('changeType')!.value : 'Code translation added',
          changeDesc: this.codeAddEditForm.get('changeDesc') ? this.codeAddEditForm.get('changeDesc')!.value : this.conceptParam!.notation
        };
        if (this.conceptParam!.title) {
          cdSnippet.actionType = 'EDIT_TL_CODE';
        }
        return cdSnippet;
      }
    }
  }

  private calculatePosition(): number {
    let index = this.versionParam.concepts!.length;
    if (this.conceptParam !== null) {
      index = this.conceptParam!.position! + 1;
    }

    if (this.codeInsertMode === 'INSERT_BEFORE') {
      index--;
    }
    return index;
  }

  save(): void {
    this.isSaving = true;
    this.errorCodeExists = false;
    this.codeSnippet = this.createFromForm();
    if (this.isNew) {
      this.subscribeToSaveResponse(this.editorService.createCode(this.codeSnippet));
    } else {
      this.subscribeToSaveResponse(this.editorService.updateCode(this.codeSnippet));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IConcept>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      response => this.processError(response)
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

    this.conceptsToPlace = [...this.conceptsToPlaceTemp!]; // shallow copy for preview

    this.codeInsertMode = this.codeAddEditForm.get('codeInsertMode')!.value;

    const pos = this.calculatePosition();

    const newConcept = {
      ...new Concept(),
      status: 'TO_BE_INSERTED',
      position: pos
    };

    if (this.codeInsertMode === 'INSERT_AS_ROOT') {
      newConcept.parent = undefined;
      newConcept.notation = this.codeAddEditForm.get(['notation'])!.value;
    } else if (this.codeInsertMode === 'INSERT_AS_CHILD') {
      newConcept.parent = this.conceptParam!.notation;
      newConcept.notation = newConcept.parent + '.' + this.codeAddEditForm.get(['notation'])!.value;
    } else {
      newConcept.parent = this.conceptParam!.parent;
      newConcept.notation =
        (newConcept.parent !== undefined ? newConcept.parent + '.' : '') + this.codeAddEditForm.get(['notation'])!.value;
    }

    this.conceptsToPlace.splice(pos, 0, newConcept);
    this._ngZone.runOutsideAngular(() => {
      setTimeout(() => {
        const element = document.querySelector('.to-be-inserted');
        element!.scrollIntoView({ behavior: 'smooth' });
      }, 500);
    });
  }
}
