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
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiDataUtils, JhiEventManager, JhiEventWithContent, JhiFileLoadError } from 'ng-jhipster';

import { VocabularyChange } from 'app/shared/model/vocabulary-change.model';
import { VocabularyChangeService } from './vocabulary-change.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap';
import { Moment } from 'moment';

@Component({
  selector: 'jhi-vocabulary-change-update',
  templateUrl: './vocabulary-change-update.component.html',
})
export class VocabularyChangeUpdateComponent implements OnInit {
  isSaving = false;
  dateDp: NgbInputDatepicker | undefined;

  editForm = this.fb.group({
    id: new FormControl<number | null>(null),
    vocabularyId: new FormControl<number | null>(null),
    versionId: new FormControl<number | null>(null),
    changeType: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.maxLength(60)] }),
    description: new FormControl<string | null>(null),
    userId: new FormControl<number | null>(null),
    userName: new FormControl<string | null>(null, [Validators.maxLength(120)]),
    date: new FormControl<Moment | null>(null),
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected vocabularyChangeService: VocabularyChangeService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ vocabularyChange }) => {
      this.updateForm(vocabularyChange);
    });
  }

  updateForm(vocabularyChange: VocabularyChange): void {
    this.editForm.patchValue({
      id: vocabularyChange.id,
      vocabularyId: vocabularyChange.vocabularyId,
      versionId: vocabularyChange.versionId,
      changeType: vocabularyChange.changeType,
      description: vocabularyChange.description,
      userId: vocabularyChange.userId,
      userName: vocabularyChange.userName,
      date: vocabularyChange.date,
    });
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe(null, (err: JhiFileLoadError) => {
      this.eventManager.broadcast(new JhiEventWithContent<AlertError>('cvsApp.error', { ...err, key: 'error.file.' + err.key }));
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const vocabularyChange = this.createFromForm();
    if (vocabularyChange.id !== undefined) {
      this.subscribeToSaveResponse(this.vocabularyChangeService.update(vocabularyChange));
    } else {
      this.subscribeToSaveResponse(this.vocabularyChangeService.create(vocabularyChange));
    }
  }

  private createFromForm(): VocabularyChange {
    return {
      id: this.editForm.controls.id.value !== null ? this.editForm.controls.id.value : undefined,
      vocabularyId: this.editForm.controls.vocabularyId.value !== null ? this.editForm.controls.vocabularyId.value : undefined,
      versionId: this.editForm.controls.versionId.value !== null ? this.editForm.controls.versionId.value : undefined,
      changeType: this.editForm.controls.changeType.value,
      description: this.editForm.controls.description.value || undefined,
      userId: this.editForm.controls.userId.value !== null ? this.editForm.controls.userId.value : undefined,
      userName: this.editForm.controls.userName.value || undefined,
      date: this.editForm.controls.date.value || undefined,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<VocabularyChange>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError(),
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }
}
