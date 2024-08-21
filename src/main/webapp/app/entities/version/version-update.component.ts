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
import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import moment, { Moment } from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiDataUtils, JhiEventManager, JhiEventWithContent, JhiFileLoadError } from 'ng-jhipster';

import { Version } from 'app/shared/model/version.model';
import { VersionService } from './version.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { Vocabulary } from 'app/shared/model/vocabulary.model';
import { VocabularyService } from 'app/entities/vocabulary/vocabulary.service';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'jhi-version-update',
  templateUrl: './version-update.component.html',
})
export class VersionUpdateComponent implements OnInit {
  isSaving = false;
  vocabularies: Vocabulary[] = [];
  publicationDateDp: NgbInputDatepicker | undefined;

  editForm = this.fb.group({
    id: new FormControl<number | null>(null),
    status: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.maxLength(20)] }),
    itemType: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.maxLength(20)] }),
    language: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    publicationDate: new FormControl<Moment | null>(null),
    lastModified: new FormControl<string | null>(null),
    number: new FormControl<string | null>(null, [Validators.maxLength(20)]),
    uri: new FormControl<string | null>(null),
    canonicalUri: new FormControl<string | null>(null),
    uriSl: new FormControl<string | null>(null),
    notation: new FormControl<string | null>(null, [Validators.maxLength(240)]),
    title: new FormControl<string | null>(null),
    definition: new FormControl<string | null>(null),
    previousVersion: new FormControl<number | null>(null),
    initialVersion: new FormControl<number | null>(null),
    creator: new FormControl<number | null>(null),
    publisher: new FormControl<number | null>(null),
    notes: new FormControl<string | null>(null),
    versionNotes: new FormControl<string | null>(null),
    versionChanges: new FormControl<string | null>(null),
    discussionNotes: new FormControl<string | null>(null),
    license: new FormControl<string | null>(null),
    licenseId: new FormControl<number | null>(null),
    citation: new FormControl<string | null>(null),
    ddiUsage: new FormControl<string | null>(null),
    translateAgency: new FormControl<string | null>(null),
    translateAgencyLink: new FormControl<string | null>(null),
    vocabularyId: new FormControl<number | null>(null),
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected versionService: VersionService,
    protected vocabularyService: VocabularyService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ version }) => {
      if (!version.id) {
        const today = moment().startOf('day');
        version.lastModified = today;
      }

      this.updateForm(version);

      this.vocabularyService.query().subscribe((res: HttpResponse<Vocabulary[]>) => (this.vocabularies = res.body || []));
    });
  }

  updateForm(version: Version): void {
    this.editForm.patchValue({
      id: version.id,
      status: version.status,
      itemType: version.itemType,
      language: version.language,
      publicationDate: version.publicationDate,
      lastModified: version.lastModified ? version.lastModified.format(DATE_TIME_FORMAT) : null,
      number: version.number,
      uri: version.uri,
      canonicalUri: version.canonicalUri,
      uriSl: version.uriSl,
      notation: version.notation,
      title: version.title,
      definition: version.definition,
      previousVersion: version.previousVersion,
      initialVersion: version.initialVersion,
      creator: version.creator,
      publisher: version.publisher,
      notes: version.notes,
      versionNotes: version.versionNotes,
      versionChanges: version.versionChanges,
      discussionNotes: version.discussionNotes,
      license: version.license,
      licenseId: version.licenseId,
      citation: version.citation,
      ddiUsage: version.ddiUsage,
      translateAgency: version.translateAgency,
      translateAgencyLink: version.translateAgencyLink,
      vocabularyId: version.vocabularyId,
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
    const version = this.createFromForm();
    if (version.id !== undefined) {
      this.subscribeToSaveResponse(this.versionService.update(version));
    } else {
      this.subscribeToSaveResponse(this.versionService.create(version));
    }
  }

  private createFromForm(): Version {
    return {
      id: this.editForm.controls.id.value !== null ? this.editForm.controls.id.value : undefined,
      status: this.editForm.controls.status.value,
      itemType: this.editForm.controls.itemType.value,
      language: this.editForm.controls.language.value || undefined,
      publicationDate: this.editForm.controls.publicationDate.value || undefined,
      lastModified: this.editForm.controls.lastModified.value
        ? moment(this.editForm.controls.lastModified.value, DATE_TIME_FORMAT)
        : undefined,
      number: this.editForm.controls.number.value || undefined,
      uri: this.editForm.controls.uri.value || undefined,
      canonicalUri: this.editForm.controls.canonicalUri.value || undefined,
      uriSl: this.editForm.controls.uriSl.value || undefined,
      notation: this.editForm.controls.notation.value || undefined,
      title: this.editForm.controls.title.value || undefined,
      definition: this.editForm.controls.definition.value || undefined,
      previousVersion: this.editForm.controls.previousVersion.value !== null ? this.editForm.controls.previousVersion.value : undefined,
      initialVersion: this.editForm.controls.initialVersion.value !== null ? this.editForm.controls.initialVersion.value : undefined,
      creator: this.editForm.controls.creator.value !== null ? this.editForm.controls.creator.value : undefined,
      publisher: this.editForm.controls.publisher.value !== null ? this.editForm.controls.publisher.value : undefined,
      notes: this.editForm.controls.notes.value || undefined,
      versionNotes: this.editForm.controls.versionNotes.value || undefined,
      versionChanges: this.editForm.controls.versionChanges.value || undefined,
      discussionNotes: this.editForm.controls.discussionNotes.value || undefined,
      license: this.editForm.controls.license.value || undefined,
      licenseId: this.editForm.controls.licenseId.value !== null ? this.editForm.controls.licenseId.value : undefined,
      citation: this.editForm.controls.citation.value || undefined,
      ddiUsage: this.editForm.controls.ddiUsage.value || undefined,
      translateAgency: this.editForm.controls.translateAgency.value || undefined,
      translateAgencyLink: this.editForm.controls.translateAgencyLink.value || undefined,
      vocabularyId: this.editForm.controls.vocabularyId.value !== null ? this.editForm.controls.vocabularyId.value : undefined,
      concepts: [],
      comments: [],
      versionHistories: [],
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<Version>>): void {
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

  trackById(_index: number, item: Vocabulary): number {
    return item.id || 0;
  }
}
