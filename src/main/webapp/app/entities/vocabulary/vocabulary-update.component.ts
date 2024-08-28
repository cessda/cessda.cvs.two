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
import moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiDataUtils, JhiEventManager, JhiEventWithContent, JhiFileLoadError } from 'ng-jhipster';

import { createNewVocabulary, Status, Vocabulary } from 'app/shared/model/vocabulary.model';
import { VocabularyService } from './vocabulary.service';
import { AlertError } from 'app/shared/alert/alert-error.model';

@Component({
  selector: 'jhi-vocabulary-update',
  templateUrl: './vocabulary-update.component.html',
})
export class VocabularyUpdateComponent implements OnInit {
  isSaving = false;
  publicationDateDp: unknown;

  editForm = this.fb.group({
    id: new FormControl<number | null>(null),
    status: new FormControl(Status.DRAFT, { nonNullable: true, validators: [Validators.required, Validators.maxLength(20)] }),
    uri: new FormControl<string | null>(null, [Validators.maxLength(240)]),
    notation: new FormControl('NEW_VOCABULARY', { nonNullable: true, validators: [Validators.required, Validators.maxLength(240)] }),
    versionNumber: new FormControl('1.0.0', { nonNullable: true, validators: [Validators.required, Validators.maxLength(20)] }),
    initialPublication: new FormControl<number | null>(null),
    previousPublication: new FormControl<number | null>(null),
    archived: new FormControl<boolean>(false, { nonNullable: true }),
    withdrawn: new FormControl<boolean>(false, { nonNullable: true }),
    discoverable: new FormControl<boolean>(false, { nonNullable: true }),
    sourceLanguage: new FormControl('en', { nonNullable: true, validators: [Validators.required, Validators.maxLength(20)] }),
    agencyId: new FormControl<number>(0, { nonNullable: true, validators: [Validators.required] }),
    agencyName: new FormControl('DEFAULT_AGENCY', { nonNullable: true, validators: [Validators.required] }),
    agencyLogo: new FormControl<string | null>(null),
    publicationDate: new FormControl<moment.Moment | null>(null),
    lastModified: new FormControl<string | null>(null),
    notes: new FormControl<string | null>(null),
    versionSq: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleSq: new FormControl<string | null>(null),
    definitionSq: new FormControl<string | null>(null),
    versionBs: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleBs: new FormControl<string | null>(null),
    definitionBs: new FormControl<string | null>(null),
    versionBg: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleBg: new FormControl<string | null>(null),
    definitionBg: new FormControl<string | null>(null),
    versionHr: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleHr: new FormControl<string | null>(null),
    definitionHr: new FormControl<string | null>(null),
    versionCs: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleCs: new FormControl<string | null>(null),
    definitionCs: new FormControl<string | null>(null),
    versionDa: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleDa: new FormControl<string | null>(null),
    definitionDa: new FormControl<string | null>(null),
    versionNl: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleNl: new FormControl<string | null>(null),
    definitionNl: new FormControl<string | null>(null),
    versionEn: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleEn: new FormControl<string | null>(null),
    definitionEn: new FormControl<string | null>(null),
    versionEt: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleEt: new FormControl<string | null>(null),
    definitionEt: new FormControl<string | null>(null),
    versionFi: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleFi: new FormControl<string | null>(null),
    definitionFi: new FormControl<string | null>(null),
    versionFr: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleFr: new FormControl<string | null>(null),
    definitionFr: new FormControl<string | null>(null),
    versionDe: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleDe: new FormControl<string | null>(null),
    definitionDe: new FormControl<string | null>(null),
    versionEl: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleEl: new FormControl<string | null>(null),
    definitionEl: new FormControl<string | null>(null),
    versionHu: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleHu: new FormControl<string | null>(null),
    definitionHu: new FormControl<string | null>(null),
    versionIt: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleIt: new FormControl<string | null>(null),
    definitionIt: new FormControl<string | null>(null),
    versionJa: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleJa: new FormControl<string | null>(null),
    definitionJa: new FormControl<string | null>(null),
    versionLt: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleLt: new FormControl<string | null>(null),
    definitionLt: new FormControl<string | null>(null),
    versionMk: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleMk: new FormControl<string | null>(null),
    definitionMk: new FormControl<string | null>(null),
    versionNo: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleNo: new FormControl<string | null>(null),
    definitionNo: new FormControl<string | null>(null),
    versionPl: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titlePl: new FormControl<string | null>(null),
    definitionPl: new FormControl<string | null>(null),
    versionPt: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titlePt: new FormControl<string | null>(null),
    definitionPt: new FormControl<string | null>(null),
    versionRo: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleRo: new FormControl<string | null>(null),
    definitionRo: new FormControl<string | null>(null),
    versionRu: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleRu: new FormControl<string | null>(null),
    definitionRu: new FormControl<string | null>(null),
    versionSr: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleSr: new FormControl<string | null>(null),
    definitionSr: new FormControl<string | null>(null),
    versionSk: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleSk: new FormControl<string | null>(null),
    definitionSk: new FormControl<string | null>(null),
    versionSl: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleSl: new FormControl<string | null>(null),
    definitionSl: new FormControl<string | null>(null),
    versionEs: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleEs: new FormControl<string | null>(null),
    definitionEs: new FormControl<string | null>(null),
    versionSv: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    titleSv: new FormControl<string | null>(null),
    definitionSv: new FormControl<string | null>(null),
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected vocabularyService: VocabularyService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ vocabulary }) => {
      if (!vocabulary.id) {
        const today = moment().startOf('day');
        vocabulary.lastModified = today;
      }

      this.updateForm(vocabulary);
    });
  }

  updateForm(vocabulary: Vocabulary): void {
    this.editForm.patchValue({
      id: vocabulary.id,
      status: vocabulary.status,
      uri: vocabulary.uri,
      notation: vocabulary.notation,
      versionNumber: vocabulary.versionNumber,
      initialPublication: vocabulary.initialPublication,
      previousPublication: vocabulary.previousPublication,
      archived: vocabulary.archived,
      withdrawn: vocabulary.withdrawn,
      discoverable: vocabulary.discoverable,
      sourceLanguage: vocabulary.sourceLanguage,
      agencyId: vocabulary.agencyId,
      agencyName: vocabulary.agencyName,
      agencyLogo: vocabulary.agencyLogo,
      publicationDate: vocabulary.publicationDate,
      lastModified: vocabulary.lastModified ? vocabulary.lastModified.format(DATE_TIME_FORMAT) : null,
      notes: vocabulary.notes,
      versionSq: vocabulary.versionSq,
      titleSq: vocabulary.titleSq,
      definitionSq: vocabulary.definitionSq,
      versionBs: vocabulary.versionBs,
      titleBs: vocabulary.titleBs,
      definitionBs: vocabulary.definitionBs,
      versionBg: vocabulary.versionBg,
      titleBg: vocabulary.titleBg,
      definitionBg: vocabulary.definitionBg,
      versionHr: vocabulary.versionHr,
      titleHr: vocabulary.titleHr,
      definitionHr: vocabulary.definitionHr,
      versionCs: vocabulary.versionCs,
      titleCs: vocabulary.titleCs,
      definitionCs: vocabulary.definitionCs,
      versionDa: vocabulary.versionDa,
      titleDa: vocabulary.titleDa,
      definitionDa: vocabulary.definitionDa,
      versionNl: vocabulary.versionNl,
      titleNl: vocabulary.titleNl,
      definitionNl: vocabulary.definitionNl,
      versionEn: vocabulary.versionEn,
      titleEn: vocabulary.titleEn,
      definitionEn: vocabulary.definitionEn,
      versionEt: vocabulary.versionEt,
      titleEt: vocabulary.titleEt,
      definitionEt: vocabulary.definitionEt,
      versionFi: vocabulary.versionFi,
      titleFi: vocabulary.titleFi,
      definitionFi: vocabulary.definitionFi,
      versionFr: vocabulary.versionFr,
      titleFr: vocabulary.titleFr,
      definitionFr: vocabulary.definitionFr,
      versionDe: vocabulary.versionDe,
      titleDe: vocabulary.titleDe,
      definitionDe: vocabulary.definitionDe,
      versionEl: vocabulary.versionEl,
      titleEl: vocabulary.titleEl,
      definitionEl: vocabulary.definitionEl,
      versionHu: vocabulary.versionHu,
      titleHu: vocabulary.titleHu,
      definitionHu: vocabulary.definitionHu,
      versionIt: vocabulary.versionIt,
      titleIt: vocabulary.titleIt,
      definitionIt: vocabulary.definitionIt,
      versionJa: vocabulary.versionJa,
      titleJa: vocabulary.titleJa,
      definitionJa: vocabulary.definitionJa,
      versionLt: vocabulary.versionLt,
      titleLt: vocabulary.titleLt,
      definitionLt: vocabulary.definitionLt,
      versionMk: vocabulary.versionMk,
      titleMk: vocabulary.titleMk,
      definitionMk: vocabulary.definitionMk,
      versionNo: vocabulary.versionNo,
      titleNo: vocabulary.titleNo,
      definitionNo: vocabulary.definitionNo,
      versionPl: vocabulary.versionPl,
      titlePl: vocabulary.titlePl,
      definitionPl: vocabulary.definitionPl,
      versionPt: vocabulary.versionPt,
      titlePt: vocabulary.titlePt,
      definitionPt: vocabulary.definitionPt,
      versionRo: vocabulary.versionRo,
      titleRo: vocabulary.titleRo,
      definitionRo: vocabulary.definitionRo,
      versionRu: vocabulary.versionRu,
      titleRu: vocabulary.titleRu,
      definitionRu: vocabulary.definitionRu,
      versionSr: vocabulary.versionSr,
      titleSr: vocabulary.titleSr,
      definitionSr: vocabulary.definitionSr,
      versionSk: vocabulary.versionSk,
      titleSk: vocabulary.titleSk,
      definitionSk: vocabulary.definitionSk,
      versionSl: vocabulary.versionSl,
      titleSl: vocabulary.titleSl,
      definitionSl: vocabulary.definitionSl,
      versionEs: vocabulary.versionEs,
      titleEs: vocabulary.titleEs,
      definitionEs: vocabulary.definitionEs,
      versionSv: vocabulary.versionSv,
      titleSv: vocabulary.titleSv,
      definitionSv: vocabulary.definitionSv,
    });
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: JhiFileLoadError) => {
        this.eventManager.broadcast(new JhiEventWithContent<AlertError>('cvsApp.error', { ...err, key: 'error.file.' + err.key }));
      },
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const vocabulary = this.createFromForm();
    if (vocabulary.id !== undefined) {
      this.subscribeToSaveResponse(this.vocabularyService.update(vocabulary));
    } else {
      this.subscribeToSaveResponse(this.vocabularyService.create(vocabulary));
    }
  }

  private createFromForm(): Vocabulary {
    return {
      ...createNewVocabulary(),
      id: this.editForm.controls.id.value !== null ? this.editForm.controls.id.value : undefined,
      status: this.editForm.controls.status.value,
      uri: this.editForm.controls.uri.value || undefined,
      notation: this.editForm.controls.notation.value,
      versionNumber: this.editForm.controls.versionNumber.value,
      initialPublication: this.editForm.controls.initialPublication.value || undefined,
      previousPublication: this.editForm.controls.previousPublication.value || undefined,
      archived: this.editForm.controls.archived.value,
      withdrawn: this.editForm.controls.withdrawn.value,
      discoverable: this.editForm.controls.discoverable.value,
      sourceLanguage: this.editForm.controls.sourceLanguage.value,
      agencyId: this.editForm.controls.agencyId.value,
      agencyName: this.editForm.controls.agencyName.value,
      agencyLogo: this.editForm.controls.agencyLogo.value || undefined,
      publicationDate: this.editForm.controls.publicationDate.value || undefined,
      lastModified: this.editForm.controls.lastModified.value
        ? moment(this.editForm.controls.lastModified.value, DATE_TIME_FORMAT)
        : undefined,
      notes: this.editForm.controls.notes.value || undefined,
      versionSq: this.editForm.controls.versionSq.value || undefined,
      titleSq: this.editForm.controls.titleSq.value || undefined,
      definitionSq: this.editForm.controls.definitionSq.value || undefined,
      versionBs: this.editForm.controls.versionBs.value || undefined,
      titleBs: this.editForm.controls.titleBs.value || undefined,
      definitionBs: this.editForm.controls.definitionBs.value || undefined,
      versionBg: this.editForm.controls.versionBg.value || undefined,
      titleBg: this.editForm.controls.titleBg.value || undefined,
      definitionBg: this.editForm.controls.definitionBg.value || undefined,
      versionHr: this.editForm.controls.versionHr.value || undefined,
      titleHr: this.editForm.controls.titleHr.value || undefined,
      definitionHr: this.editForm.controls.definitionHr.value || undefined,
      versionCs: this.editForm.controls.versionCs.value || undefined,
      titleCs: this.editForm.controls.titleCs.value || undefined,
      definitionCs: this.editForm.controls.definitionCs.value || undefined,
      versionDa: this.editForm.controls.versionDa.value || undefined,
      titleDa: this.editForm.controls.titleDa.value || undefined,
      definitionDa: this.editForm.controls.definitionDa.value || undefined,
      versionNl: this.editForm.controls.versionNl.value || undefined,
      titleNl: this.editForm.controls.titleNl.value || undefined,
      definitionNl: this.editForm.controls.definitionNl.value || undefined,
      versionEn: this.editForm.controls.versionEn.value || undefined,
      titleEn: this.editForm.controls.titleEn.value || undefined,
      definitionEn: this.editForm.controls.definitionEn.value || undefined,
      versionEt: this.editForm.controls.versionEt.value || undefined,
      titleEt: this.editForm.controls.titleEt.value || undefined,
      definitionEt: this.editForm.controls.definitionEt.value || undefined,
      versionFi: this.editForm.controls.versionFi.value || undefined,
      titleFi: this.editForm.controls.titleFi.value || undefined,
      definitionFi: this.editForm.controls.definitionFi.value || undefined,
      versionFr: this.editForm.controls.versionFr.value || undefined,
      titleFr: this.editForm.controls.titleFr.value || undefined,
      definitionFr: this.editForm.controls.definitionFr.value || undefined,
      versionDe: this.editForm.controls.versionDe.value || undefined,
      titleDe: this.editForm.controls.titleDe.value || undefined,
      definitionDe: this.editForm.controls.definitionDe.value || undefined,
      versionEl: this.editForm.controls.versionEl.value || undefined,
      titleEl: this.editForm.controls.titleEl.value || undefined,
      definitionEl: this.editForm.controls.definitionEl.value || undefined,
      versionHu: this.editForm.controls.versionHu.value || undefined,
      titleHu: this.editForm.controls.titleHu.value || undefined,
      definitionHu: this.editForm.controls.definitionHu.value || undefined,
      versionIt: this.editForm.controls.versionIt.value || undefined,
      titleIt: this.editForm.controls.titleIt.value || undefined,
      definitionIt: this.editForm.controls.definitionIt.value || undefined,
      versionJa: this.editForm.controls.versionJa.value || undefined,
      titleJa: this.editForm.controls.titleJa.value || undefined,
      definitionJa: this.editForm.controls.definitionJa.value || undefined,
      versionLt: this.editForm.controls.versionLt.value || undefined,
      titleLt: this.editForm.controls.titleLt.value || undefined,
      definitionLt: this.editForm.controls.definitionLt.value || undefined,
      versionMk: this.editForm.controls.versionMk.value || undefined,
      titleMk: this.editForm.controls.titleMk.value || undefined,
      definitionMk: this.editForm.controls.definitionMk.value || undefined,
      versionNo: this.editForm.controls.versionNo.value || undefined,
      titleNo: this.editForm.controls.titleNo.value || undefined,
      definitionNo: this.editForm.controls.definitionNo.value || undefined,
      versionPl: this.editForm.controls.versionPl.value || undefined,
      titlePl: this.editForm.controls.titlePl.value || undefined,
      definitionPl: this.editForm.controls.definitionPl.value || undefined,
      versionPt: this.editForm.controls.versionPt.value || undefined,
      titlePt: this.editForm.controls.titlePt.value || undefined,
      definitionPt: this.editForm.controls.definitionPt.value || undefined,
      versionRo: this.editForm.controls.versionRo.value || undefined,
      titleRo: this.editForm.controls.titleRo.value || undefined,
      definitionRo: this.editForm.controls.definitionRo.value || undefined,
      versionRu: this.editForm.controls.versionRu.value || undefined,
      titleRu: this.editForm.controls.titleRu.value || undefined,
      definitionRu: this.editForm.controls.definitionRu.value || undefined,
      versionSr: this.editForm.controls.versionSr.value || undefined,
      titleSr: this.editForm.controls.titleSr.value || undefined,
      definitionSr: this.editForm.controls.definitionSr.value || undefined,
      versionSk: this.editForm.controls.versionSk.value || undefined,
      titleSk: this.editForm.controls.titleSk.value || undefined,
      definitionSk: this.editForm.controls.definitionSk.value || undefined,
      versionSl: this.editForm.controls.versionSl.value || undefined,
      titleSl: this.editForm.controls.titleSl.value || undefined,
      definitionSl: this.editForm.controls.definitionSl.value || undefined,
      versionEs: this.editForm.controls.versionEs.value || undefined,
      titleEs: this.editForm.controls.titleEs.value || undefined,
      definitionEs: this.editForm.controls.definitionEs.value || undefined,
      versionSv: this.editForm.controls.versionSv.value || undefined,
      titleSv: this.editForm.controls.titleSv.value || undefined,
      definitionSv: this.editForm.controls.definitionSv.value || undefined,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<Vocabulary>>): void {
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
