import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { AccountService } from 'app/core/auth/account.service';
import { FormBuilder, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { Account } from 'app/core/user/account.model';
import VocabularyUtil from 'app/shared/util/vocabulary-util';
import { CODE_ALREADY_EXIST_TYPE } from 'app/shared';
import { EditorService } from 'app/editor/editor.service';
import { IVersion } from 'app/shared/model/version.model';
import { IConcept } from 'app/shared/model/concept.model';
import { CodeSnippet, ICodeSnippet } from 'app/shared/model/code-snippet.model';

@Component({
  selector: 'jhi-editor-detail-code-add-edit-dialog',
  templateUrl: './editor-detail-code-add-edit-dialog.component.html',
  styleUrls: ['editor.scss']
})
export class EditorDetailCodeAddEditDialogComponent implements OnInit {
  isSaving: boolean;
  account!: Account;
  languages: string[] = [];
  errorCodeExists = false;
  codeSnippet?: ICodeSnippet;
  versionParam?: IVersion;
  conceptParam?: IConcept;
  codeInsertMode?: string;
  isNew?: boolean;
  isSlForm?: boolean;

  codeAddEditForm = this.fb.group({
    notation: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(240), Validators.pattern('^[_+A-Za-z0-9-]*$')]],
    title: ['', [Validators.required]],
    definition: ['', []]
  });

  constructor(
    private accountService: AccountService,
    protected editorService: EditorService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager,
    private fb: FormBuilder,
    private router: Router
  ) {
    this.isSaving = false;
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
  }

  private createFromForm(): ICodeSnippet {
    if (this.isNew) {
      const codeSnippet = {
        ...new CodeSnippet(),
        actionType: 'CREATE_CODE',
        versionId: this.versionParam!.id,
        title: this.codeAddEditForm.get(['title'])!.value,
        definition: this.codeAddEditForm.get(['definition'])!.value
      };
      if (this.codeInsertMode === 'INSERT_AS_ROOT') {
        codeSnippet.position = this.versionParam!.concepts!.length;
        codeSnippet.notation = this.codeAddEditForm.get(['notation'])!.value;
      } else if (this.codeInsertMode === 'INSERT_AS_CHILD') {
        codeSnippet.parent = this.conceptParam!.notation;
        codeSnippet.position = this.conceptParam!.position! + 1;
        codeSnippet.notation = codeSnippet.parent + '.' + this.codeAddEditForm.get(['notation'])!.value;
      }
      return codeSnippet;
    } else {
      if (this.isSlForm) {
        const cdSnippet = {
          ...this.codeSnippet,
          actionType: 'EDIT_CODE',
          conceptId: this.conceptParam!.id,
          versionId: this.versionParam!.id,
          notation: this.codeAddEditForm.get(['notation'])!.value,
          title: this.codeAddEditForm.get(['title'])!.value,
          definition: this.codeAddEditForm.get(['definition'])!.value
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
          versionId: this.versionParam!.id,
          title: this.codeAddEditForm.get(['title'])!.value,
          definition: this.codeAddEditForm.get(['definition'])!.value
        };
        if (this.conceptParam!.title) {
          cdSnippet.actionType = 'EDIT_TL_CODE';
        }
        return cdSnippet;
      }
    }
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
      this.router.navigate(['/editor/vocabulary/' + this.versionParam!.notation]);
    } else {
      this.router.navigate(['/editor/vocabulary/' + this.versionParam!.notation], { queryParams: { lang: this.versionParam!.language } });
    }
    this.activeModal.dismiss(true);
    this.eventManager.broadcast('deselectConcept');
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  getLangIsoFormatted(langIso: string): string {
    return VocabularyUtil.getLangIsoFormatted(langIso);
  }

  private processError(response: HttpErrorResponse): void {
    if (response.status === 400 && response.error.type === CODE_ALREADY_EXIST_TYPE) {
      this.errorCodeExists = true;
      this.isSaving = false;
    }
  }
}
