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
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { AgencyService } from 'app/agency/agency.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Agency } from 'app/shared/model/agency.model';
import { AccountService } from 'app/core/auth/account.service';
import { VocabularyService } from 'app/entities/vocabulary/vocabulary.service';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { Vocabulary } from 'app/shared/model/vocabulary.model';
import { Account } from 'app/core/user/account.model';
import { LanguageIso } from 'app/shared/model/enumerations/language-iso.model';
import { VOCABULARY_ALREADY_EXIST_TYPE } from 'app/shared';
import { EditorService } from 'app/editor/editor.service';
import { VocabularySnippet } from 'app/shared/model/vocabulary-snippet.model';
import { ActionType } from 'app/shared/model/enumerations/action-type.model';

@Component({
  selector: 'jhi-editor-cv-add-dialog',
  templateUrl: './editor-cv-add-dialog.component.html',
})
export class EditorCvAddDialogComponent implements OnInit {
  isSaving: boolean;
  account!: Account;
  agencies: Agency[] = [];
  languages: string[] = [];
  errorNotationExists = false;

  cvAddForm = this.fb.group({
    agency: new FormControl<number | null>(null),
    sourceLanguage: new FormControl<string | null>(null),
    notation: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(2), Validators.maxLength(240), Validators.pattern('^[+A-Za-z0-9-]*$')],
    }),
    title: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
    definition: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
    notes: new FormControl<string | null>(null),
  });

  constructor(
    protected agencyService: AgencyService,
    private accountService: AccountService,
    protected editorService: EditorService,
    protected vocabularyService: VocabularyService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager,
    private fb: FormBuilder,
    private router: Router,
  ) {
    this.isSaving = false;
  }

  loadAgencies(): void {
    this.agencyService
      .query({
        page: 0,
        size: 1000,
        sort: ['name,asc'],
      })
      .subscribe((res: HttpResponse<Agency[]>) => {
        this.agencies = res.body ? this.filterAgencies(res.body) : [];
        // select first agency
        this.cvAddForm.patchValue({ agency: this.agencies[0].id });
        this.updateLanguageCheckbox(this.agencies[0].id);
      });
  }

  filterAgencies(agcs: Agency[]): Agency[] {
    if (this.accountService.isAdmin()) {
      return agcs;
    }
    const ags: Agency[] = [];
    this.account.userAgencies.forEach(ua => {
      if (ua.agencyRole === 'ADMIN_SL') {
        const ag = agcs.filter(agc => agc.id === ua.agencyId)[0];
        if (ag && !ags.includes(ag)) {
          ags.push(ag);
        }
      }
    });

    return ags;
  }

  updateLanguageCheckbox(agencyId: number | undefined): void {
    this.languages = [];
    if (this.accountService.isAdmin()) {
      for (const langIso in LanguageIso) {
        if (parseInt(langIso, 10) >= 0) {
          this.languages.push(LanguageIso[langIso]);
        }
      }
    } else {
      this.account.userAgencies.forEach(ua => {
        if (ua.agencyRole === 'ADMIN_SL' && ua.agencyId === agencyId) {
          if (ua.language && !this.languages.includes(ua.language)) {
            this.languages.push(ua.language);
          }
        }
      });
    }
    this.cvAddForm.patchValue({ sourceLanguage: this.languages[0] });
  }

  clear(): void {
    this.activeModal.dismiss('cancel');
  }

  ngOnInit(): void {
    this.isSaving = false;
    this.accountService.identity().subscribe(account => {
      if (account) {
        this.account = account;
        this.loadAgencies();
      }
    });
  }

  private createFromForm(): VocabularySnippet {
    const selectedAgencyId = Number(this.cvAddForm.controls.agency.value);
    return {
      actionType: ActionType.CREATE_CV,
      agencyId: selectedAgencyId,
      language: this.cvAddForm.controls.sourceLanguage.value || undefined,
      itemType: 'SL',
      notation: this.cvAddForm.controls.notation.value,
      versionNumber: '1.0.0',
      status: 'DRAFT',
      title: this.cvAddForm.controls.title.value,
      definition: this.cvAddForm.controls.definition.value,
      notes: this.cvAddForm.controls.notes.value || undefined,
    };
  }

  save(): void {
    this.isSaving = true;
    this.errorNotationExists = false;
    const vocabularySnippet = this.createFromForm();
    this.subscribeToSaveResponse(this.editorService.createVocabulary(vocabularySnippet), vocabularySnippet.notation!);
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<Vocabulary>>, notation: string): void {
    result.subscribe(
      () => this.onSaveSuccess(notation),
      response => this.processError(response),
    );
  }

  protected onSaveSuccess(notation: string): void {
    this.isSaving = false;
    //    this.router.navigate(['/editor/vocabulary/' + notation]);
    window.location.href = '/editor/vocabulary/' + notation;
    this.activeModal.dismiss(true);
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  private processError(response: HttpErrorResponse): void {
    if (response.status === 400 && response.error.type === VOCABULARY_ALREADY_EXIST_TYPE) {
      this.errorNotationExists = true;
    }
  }
}

@Component({
  selector: 'jhi-editor-cv-add-popup',
  template: '',
})
export class EditorCvAddPopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef | undefined;

  constructor(
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal,
  ) {}

  ngOnInit(): void {
    this.ngbModalRef = this.modalService.open(EditorCvAddDialogComponent, { size: 'xl', backdrop: 'static' });
    this.ngbModalRef.result.then(
      () => {
        this.router.navigate(['/editor', { outlets: { popup: null } }]);
        this.ngbModalRef = undefined;
      },
      () => {
        this.router.navigate(['/editor', { outlets: { popup: null } }]);
        this.ngbModalRef = undefined;
      },
    );
  }

  ngOnDestroy(): void {
    this.ngbModalRef = undefined;
  }
}
