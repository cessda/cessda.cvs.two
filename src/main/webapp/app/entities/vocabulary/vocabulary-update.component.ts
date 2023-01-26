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
import {Component, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import * as moment from 'moment';
import {DATE_TIME_FORMAT} from 'app/shared/constants/input.constants';
import {JhiDataUtils, JhiEventManager, JhiEventWithContent, JhiFileLoadError} from 'ng-jhipster';

import {IVocabulary, Vocabulary} from 'app/shared/model/vocabulary.model';
import {VocabularyService} from './vocabulary.service';
import {AlertError} from 'app/shared/alert/alert-error.model';

@Component({
  selector: 'jhi-vocabulary-update',
  templateUrl: './vocabulary-update.component.html'
})
export class VocabularyUpdateComponent implements OnInit {
  isSaving = false;
  publicationDateDp: any;

  editForm = this.fb.group({
    id: [],
    status: [null, [Validators.required, Validators.maxLength(20)]],
    uri: [null, [Validators.maxLength(240)]],
    notation: [null, [Validators.required, Validators.maxLength(240)]],
    versionNumber: [null, [Validators.required, Validators.maxLength(20)]],
    initialPublication: [],
    previousPublication: [],
    archived: [],
    withdrawn: [],
    discoverable: [],
    sourceLanguage: [null, [Validators.required, Validators.maxLength(20)]],
    agencyId: [null, [Validators.required]],
    agencyName: [null, [Validators.required]],
    agencyLogo: [],
    publicationDate: [],
    lastModified: [],
    notes: [],
    versionSq: [null, [Validators.maxLength(20)]],
    titleSq: [],
    definitionSq: [],
    versionBs: [null, [Validators.maxLength(20)]],
    titleBs: [],
    definitionBs: [],
    versionBg: [null, [Validators.maxLength(20)]],
    titleBg: [],
    definitionBg: [],
    versionHr: [null, [Validators.maxLength(20)]],
    titleHr: [],
    definitionHr: [],
    versionCs: [null, [Validators.maxLength(20)]],
    titleCs: [],
    definitionCs: [],
    versionDa: [null, [Validators.maxLength(20)]],
    titleDa: [],
    definitionDa: [],
    versionNl: [null, [Validators.maxLength(20)]],
    titleNl: [],
    definitionNl: [],
    versionEn: [null, [Validators.maxLength(20)]],
    titleEn: [],
    definitionEn: [],
    versionEt: [null, [Validators.maxLength(20)]],
    titleEt: [],
    definitionEt: [],
    versionFi: [null, [Validators.maxLength(20)]],
    titleFi: [],
    definitionFi: [],
    versionFr: [null, [Validators.maxLength(20)]],
    titleFr: [],
    definitionFr: [],
    versionDe: [null, [Validators.maxLength(20)]],
    titleDe: [],
    definitionDe: [],
    versionEl: [null, [Validators.maxLength(20)]],
    titleEl: [],
    definitionEl: [],
    versionHu: [null, [Validators.maxLength(20)]],
    titleHu: [],
    definitionHu: [],
    versionIt: [null, [Validators.maxLength(20)]],
    titleIt: [],
    definitionIt: [],
    versionJa: [null, [Validators.maxLength(20)]],
    titleJa: [],
    definitionJa: [],
    versionLt: [null, [Validators.maxLength(20)]],
    titleLt: [],
    definitionLt: [],
    versionMk: [null, [Validators.maxLength(20)]],
    titleMk: [],
    definitionMk: [],
    versionNo: [null, [Validators.maxLength(20)]],
    titleNo: [],
    definitionNo: [],
    versionPl: [null, [Validators.maxLength(20)]],
    titlePl: [],
    definitionPl: [],
    versionPt: [null, [Validators.maxLength(20)]],
    titlePt: [],
    definitionPt: [],
    versionRo: [null, [Validators.maxLength(20)]],
    titleRo: [],
    definitionRo: [],
    versionRu: [null, [Validators.maxLength(20)]],
    titleRu: [],
    definitionRu: [],
    versionSr: [null, [Validators.maxLength(20)]],
    titleSr: [],
    definitionSr: [],
    versionSk: [null, [Validators.maxLength(20)]],
    titleSk: [],
    definitionSk: [],
    versionSl: [null, [Validators.maxLength(20)]],
    titleSl: [],
    definitionSl: [],
    versionEs: [null, [Validators.maxLength(20)]],
    titleEs: [],
    definitionEs: [],
    versionSv: [null, [Validators.maxLength(20)]],
    titleSv: [],
    definitionSv: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected vocabularyService: VocabularyService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
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

  updateForm(vocabulary: IVocabulary): void {
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
      definitionSv: vocabulary.definitionSv
    });
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe(null, (err: JhiFileLoadError) => {
      this.eventManager.broadcast(
        new JhiEventWithContent<AlertError>('cvsApp.error', { ...err, key: 'error.file.' + err.key })
      );
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

  private createFromForm(): IVocabulary {
    return {
      ...new Vocabulary(),
      id: this.editForm.get(['id'])!.value,
      status: this.editForm.get(['status'])!.value,
      uri: this.editForm.get(['uri'])!.value,
      notation: this.editForm.get(['notation'])!.value,
      versionNumber: this.editForm.get(['versionNumber'])!.value,
      initialPublication: this.editForm.get(['initialPublication'])!.value,
      previousPublication: this.editForm.get(['previousPublication'])!.value,
      archived: this.editForm.get(['archived'])!.value,
      withdrawn: this.editForm.get(['withdrawn'])!.value,
      discoverable: this.editForm.get(['discoverable'])!.value,
      sourceLanguage: this.editForm.get(['sourceLanguage'])!.value,
      agencyId: this.editForm.get(['agencyId'])!.value,
      agencyName: this.editForm.get(['agencyName'])!.value,
      agencyLogo: this.editForm.get(['agencyLogo'])!.value,
      publicationDate: this.editForm.get(['publicationDate'])!.value,
      lastModified: this.editForm.get(['lastModified'])!.value
        ? moment(this.editForm.get(['lastModified'])!.value, DATE_TIME_FORMAT)
        : undefined,
      notes: this.editForm.get(['notes'])!.value,
      versionSq: this.editForm.get(['versionSq'])!.value,
      titleSq: this.editForm.get(['titleSq'])!.value,
      definitionSq: this.editForm.get(['definitionSq'])!.value,
      versionBs: this.editForm.get(['versionBs'])!.value,
      titleBs: this.editForm.get(['titleBs'])!.value,
      definitionBs: this.editForm.get(['definitionBs'])!.value,
      versionBg: this.editForm.get(['versionBg'])!.value,
      titleBg: this.editForm.get(['titleBg'])!.value,
      definitionBg: this.editForm.get(['definitionBg'])!.value,
      versionHr: this.editForm.get(['versionHr'])!.value,
      titleHr: this.editForm.get(['titleHr'])!.value,
      definitionHr: this.editForm.get(['definitionHr'])!.value,
      versionCs: this.editForm.get(['versionCs'])!.value,
      titleCs: this.editForm.get(['titleCs'])!.value,
      definitionCs: this.editForm.get(['definitionCs'])!.value,
      versionDa: this.editForm.get(['versionDa'])!.value,
      titleDa: this.editForm.get(['titleDa'])!.value,
      definitionDa: this.editForm.get(['definitionDa'])!.value,
      versionNl: this.editForm.get(['versionNl'])!.value,
      titleNl: this.editForm.get(['titleNl'])!.value,
      definitionNl: this.editForm.get(['definitionNl'])!.value,
      versionEn: this.editForm.get(['versionEn'])!.value,
      titleEn: this.editForm.get(['titleEn'])!.value,
      definitionEn: this.editForm.get(['definitionEn'])!.value,
      versionEt: this.editForm.get(['versionEt'])!.value,
      titleEt: this.editForm.get(['titleEt'])!.value,
      definitionEt: this.editForm.get(['definitionEt'])!.value,
      versionFi: this.editForm.get(['versionFi'])!.value,
      titleFi: this.editForm.get(['titleFi'])!.value,
      definitionFi: this.editForm.get(['definitionFi'])!.value,
      versionFr: this.editForm.get(['versionFr'])!.value,
      titleFr: this.editForm.get(['titleFr'])!.value,
      definitionFr: this.editForm.get(['definitionFr'])!.value,
      versionDe: this.editForm.get(['versionDe'])!.value,
      titleDe: this.editForm.get(['titleDe'])!.value,
      definitionDe: this.editForm.get(['definitionDe'])!.value,
      versionEl: this.editForm.get(['versionEl'])!.value,
      titleEl: this.editForm.get(['titleEl'])!.value,
      definitionEl: this.editForm.get(['definitionEl'])!.value,
      versionHu: this.editForm.get(['versionHu'])!.value,
      titleHu: this.editForm.get(['titleHu'])!.value,
      definitionHu: this.editForm.get(['definitionHu'])!.value,
      versionIt: this.editForm.get(['versionIt'])!.value,
      titleIt: this.editForm.get(['titleIt'])!.value,
      definitionIt: this.editForm.get(['definitionIt'])!.value,
      versionJa: this.editForm.get(['versionJa'])!.value,
      titleJa: this.editForm.get(['titleJa'])!.value,
      definitionJa: this.editForm.get(['definitionJa'])!.value,
      versionLt: this.editForm.get(['versionLt'])!.value,
      titleLt: this.editForm.get(['titleLt'])!.value,
      definitionLt: this.editForm.get(['definitionLt'])!.value,
      versionMk: this.editForm.get(['versionMk'])!.value,
      titleMk: this.editForm.get(['titleMk'])!.value,
      definitionMk: this.editForm.get(['definitionMk'])!.value,
      versionNo: this.editForm.get(['versionNo'])!.value,
      titleNo: this.editForm.get(['titleNo'])!.value,
      definitionNo: this.editForm.get(['definitionNo'])!.value,
      versionPl: this.editForm.get(['versionPl'])!.value,
      titlePl: this.editForm.get(['titlePl'])!.value,
      definitionPl: this.editForm.get(['definitionPl'])!.value,
      versionPt: this.editForm.get(['versionPt'])!.value,
      titlePt: this.editForm.get(['titlePt'])!.value,
      definitionPt: this.editForm.get(['definitionPt'])!.value,
      versionRo: this.editForm.get(['versionRo'])!.value,
      titleRo: this.editForm.get(['titleRo'])!.value,
      definitionRo: this.editForm.get(['definitionRo'])!.value,
      versionRu: this.editForm.get(['versionRu'])!.value,
      titleRu: this.editForm.get(['titleRu'])!.value,
      definitionRu: this.editForm.get(['definitionRu'])!.value,
      versionSr: this.editForm.get(['versionSr'])!.value,
      titleSr: this.editForm.get(['titleSr'])!.value,
      definitionSr: this.editForm.get(['definitionSr'])!.value,
      versionSk: this.editForm.get(['versionSk'])!.value,
      titleSk: this.editForm.get(['titleSk'])!.value,
      definitionSk: this.editForm.get(['definitionSk'])!.value,
      versionSl: this.editForm.get(['versionSl'])!.value,
      titleSl: this.editForm.get(['titleSl'])!.value,
      definitionSl: this.editForm.get(['definitionSl'])!.value,
      versionEs: this.editForm.get(['versionEs'])!.value,
      titleEs: this.editForm.get(['titleEs'])!.value,
      definitionEs: this.editForm.get(['definitionEs'])!.value,
      versionSv: this.editForm.get(['versionSv'])!.value,
      titleSv: this.editForm.get(['titleSv'])!.value,
      definitionSv: this.editForm.get(['definitionSv'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVocabulary>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
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
