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
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { AccountService } from 'app/core/auth/account.service';
import { VocabularyService } from 'app/entities/vocabulary/vocabulary.service';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { Vocabulary } from 'app/shared/model/vocabulary.model';
import { Account } from 'app/core/user/account.model';
import VocabularyUtil from 'app/shared/util/vocabulary-util';
import { VOCABULARY_ALREADY_EXIST_TYPE } from 'app/shared';
import { EditorService } from 'app/editor/editor.service';
import { VocabularySnippet } from 'app/shared/model/vocabulary-snippet.model';
import { Version } from 'app/shared/model/version.model';
import { ActionType } from 'app/shared/model/enumerations/action-type.model';

@Component({
  selector: 'jhi-editor-detail-cv-add-edit-dialog',
  templateUrl: './editor-detail-cv-add-edit-dialog.component.html',
})
export class EditorDetailCvAddEditDialogComponent implements OnInit {
  isSaving = false;
  isSubmitting = false;
  account!: Account;
  languages: string[] = [];
  errorNotationExists = false;
  notation? = '';
  vocabularySnippet!: VocabularySnippet;
  vocabularyParam!: Vocabulary;
  versionParam!: Version;
  versionSlParam!: Version;
  isNew = false;
  isSlForm = false;
  selectedLanguage = '';

  private readonly formControls = {
    language: new FormControl<string | null>(null),
    notation: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(2), Validators.maxLength(240), Validators.pattern('^[_+A-Za-z0-9-]*$')],
    }),
    title: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    definition: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    notes: new FormControl<string | null>(null),
    translateAgency: new FormControl<string | null>(null),
    translateAgencyLink: new FormControl('', [
      Validators.pattern(
        '(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})',
      ),
    ]),
    changeType: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    changeDesc: new FormControl<string | null>(null),
  };

  cvAddEditForm = this.fb.group<
    Omit<typeof this.formControls, 'language' | 'notation' | 'translateAgency' | 'translateAgencyLink' | 'changeType' | 'changeDesc'> & {
      language?: FormControl<string | null>;
      notation?: FormControl<string>;
      translateAgency?: FormControl<string | null>;
      translateAgencyLink?: FormControl<string | null>;
      changeType?: FormControl<string>;
      changeDesc?: FormControl<string | null>;
    }
  >(this.formControls);

  constructor(
    private accountService: AccountService,
    protected vocabularyService: VocabularyService,
    protected editorService: EditorService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager,
    private fb: FormBuilder,
    private router: Router,
  ) {}

  updateLanguageCheckbox(agencyId: number): void {
    // for Add new CV set taken language with []
    const versions = this.isNew && this.isSlForm ? [] : this.vocabularyParam.versions;
    const availableLanguages = VocabularyUtil.getAvailableCvLanguage(versions);
    this.languages = [];

    if (this.isNew) {
      if (
        this.accountService.isAdmin() ||
        this.account.userAgencies.some(ua => (ua.agencyRole === 'ADMIN' || ua.agencyRole === 'ADMIN_CONTENT') && ua.agencyId === agencyId)
      ) {
        this.languages = availableLanguages;
      } else {
        this.account.userAgencies.forEach(ua => {
          if (this.isSlForm) {
            if (ua.agencyRole === 'ADMIN_SL' && ua.agencyId === agencyId && ua.language && availableLanguages.includes(ua.language)) {
              this.languages.push(ua.language);
            }
          } else {
            if (ua.agencyRole === 'ADMIN_TL' && ua.agencyId === agencyId && ua.language && availableLanguages.includes(ua.language)) {
              this.languages.push(ua.language);
            }
          }
        });
      }
    }
    this.languages = VocabularyUtil.sortLangByName(this.languages, null);
    this.fillForm();
  }

  private selectLanguage(languages: string[], sourceLanguage: string): string {
    if (languages && languages.length > 0) {
      const pos = languages.indexOf(sourceLanguage);
      return languages[pos < 0 ? 0 : pos];
    }
    return '';
  }

  private fillForm(): void {
    this.selectedLanguage = this.selectLanguage(this.languages, this.vocabularyParam.sourceLanguage);
    if (this.isSlForm) {
      this.cvAddEditForm.removeControl('translateAgency');
      this.cvAddEditForm.removeControl('translateAgencyLink');
    } else {
      this.cvAddEditForm.removeControl('notation');
    }
    if (this.isNew) {
      this.cvAddEditForm.patchValue({ language: this.selectedLanguage });
      this.cvAddEditForm.removeControl('changeType');
      this.cvAddEditForm.removeControl('changeDesc');
    } else {
      this.vocabularyParam.selectedLang = this.vocabularyParam.sourceLanguage;
      this.cvAddEditForm.removeControl('language');
      this.cvAddEditForm.removeControl('notation');
      this.cvAddEditForm.patchValue({
        title: this.versionParam.title,
        definition: this.versionParam.definition,
        notes: this.versionParam.notes,
      });
      if (!this.isSlForm) {
        this.cvAddEditForm.patchValue({
          translateAgency: this.versionParam.translateAgency,
          translateAgencyLink: this.versionParam.translateAgencyLink,
        });
      }
      if (
        !this.versionParam.initialVersion ||
        (this.versionParam.initialVersion && this.versionParam.initialVersion === this.versionParam.id)
      ) {
        this.cvAddEditForm.removeControl('changeType');
        this.cvAddEditForm.removeControl('changeDesc');
      } else {
        this.cvAddEditForm.patchValue({ changeDesc: this.versionParam.notation });
      }
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
        this.updateLanguageCheckbox(this.vocabularyParam.agencyId);
      }
    });
  }

  private createFromForm(): VocabularySnippet {
    if (this.isNew) {
      if (this.isSlForm) {
        return {
          actionType: ActionType.CREATE_CV,
          agencyId: this.vocabularyParam.agencyId,
          language: this.cvAddEditForm.controls.language?.value || undefined,
          itemType: 'SL',
          notation: this.cvAddEditForm.controls.notation?.value,
          versionNumber: '1.0.0',
          status: 'DRAFT',
          title: this.cvAddEditForm.controls.title.value,
          definition: this.cvAddEditForm.controls.definition.value,
          notes: this.cvAddEditForm.controls.notes.value || undefined,
        };
      } else {
        return {
          actionType: ActionType.ADD_TL_CV,
          agencyId: this.vocabularyParam.agencyId,
          language: this.cvAddEditForm.controls.language?.value || undefined,
          itemType: 'TL',
          notation: this.vocabularyParam.notation,
          versionNumber: this.versionSlParam.number,
          status: 'DRAFT',
          vocabularyId: this.vocabularyParam.id,
          versionSlId: this.versionSlParam.id,
          title: this.cvAddEditForm.controls.title.value,
          definition: this.cvAddEditForm.controls.definition.value,
          notes: this.cvAddEditForm.controls.notes.value || undefined,
          translateAgency: this.cvAddEditForm.controls.translateAgency?.value || undefined,
          translateAgencyLink: this.cvAddEditForm.controls.translateAgencyLink?.value || undefined,
        };
      }
    } else {
      return {
        ...this.vocabularySnippet,
        actionType: ActionType.EDIT_CV,
        vocabularyId: this.vocabularyParam.id,
        versionId: this.versionParam.id,
        language: this.versionParam.language,
        itemType: this.versionParam.itemType,
        notation: this.versionParam.notation,
        notes: this.cvAddEditForm.controls.notes.value || undefined,
        title: this.cvAddEditForm.controls.title.value,
        definition: this.cvAddEditForm.controls.definition.value,
        translateAgency: this.isSlForm ? undefined : this.cvAddEditForm.controls.translateAgency?.value || undefined,
        translateAgencyLink: this.isSlForm ? undefined : this.cvAddEditForm.controls.translateAgencyLink?.value || undefined,
        changeType: this.cvAddEditForm.controls.changeType?.value,
        changeDesc: this.cvAddEditForm.controls.changeDesc?.value || undefined,
      };
    }
  }

  save(): void {
    this.isSubmitting = true;
    if (this.cvAddEditForm.valid) {
      this.isSaving = true;
      this.errorNotationExists = false;
      this.vocabularySnippet = this.createFromForm();
      this.notation = this.vocabularySnippet.notation;
      if (this.isNew) {
        this.subscribeToSaveResponse(this.editorService.createVocabulary(this.vocabularySnippet));
      } else {
        this.subscribeToSaveResponse(this.editorService.updateVocabulary(this.vocabularySnippet));
      }
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<Vocabulary>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      response => this.processError(response),
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    if (this.isSlForm) {
      this.router.navigate(['/editor/vocabulary/' + this.notation]);
    } else {
      // unable to complete refresh
      // this.router.navigate(['/editor/vocabulary/' + this.notation], { queryParams: { lang: this.vocabularySnippet!.language } });
      // changed to below, in order to complete refresh
      window.location.href = '/editor/vocabulary/' + this.notation + '?lang=' + this.vocabularySnippet.language;
    }
    this.activeModal.dismiss(true);
    this.eventManager.broadcast('deselectConcept');
  }

  private processError(response: HttpErrorResponse): void {
    if (response.status === 400 && response.error.type === VOCABULARY_ALREADY_EXIST_TYPE) {
      this.errorNotationExists = true;
      this.isSaving = false;
    }
  }

  changeLanguage(event: Event): void {
    this.selectedLanguage = (event.target as HTMLSelectElement).value;
  }
}
