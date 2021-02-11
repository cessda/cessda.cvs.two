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

import {Component, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import * as moment from 'moment';
import {DATE_TIME_FORMAT} from 'app/shared/constants/input.constants';
import {JhiDataUtils, JhiEventManager, JhiEventWithContent, JhiFileLoadError} from 'ng-jhipster';

import {IVersion, Version} from 'app/shared/model/version.model';
import {VersionService} from './version.service';
import {AlertError} from 'app/shared/alert/alert-error.model';
import {IVocabulary} from 'app/shared/model/vocabulary.model';
import {VocabularyService} from 'app/entities/vocabulary/vocabulary.service';

@Component({
  selector: 'jhi-version-update',
  templateUrl: './version-update.component.html'
})
export class VersionUpdateComponent implements OnInit {
  isSaving = false;
  vocabularies: IVocabulary[] = [];
  publicationDateDp: any;

  editForm = this.fb.group({
    id: [],
    status: [null, [Validators.required, Validators.maxLength(20)]],
    itemType: [null, [Validators.required, Validators.maxLength(20)]],
    language: [null, [Validators.maxLength(20)]],
    publicationDate: [],
    lastModified: [],
    number: [null, [Validators.maxLength(20)]],
    uri: [],
    canonicalUri: [],
    uriSl: [],
    notation: [null, [Validators.maxLength(240)]],
    title: [],
    definition: [],
    previousVersion: [],
    initialVersion: [],
    creator: [],
    publisher: [],
    notes: [],
    versionNotes: [],
    versionChanges: [],
    discussionNotes: [],
    license: [],
    licenseId: [],
    citation: [],
    ddiUsage: [],
    translateAgency: [],
    translateAgencyLink: [],
    vocabularyId: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected versionService: VersionService,
    protected vocabularyService: VocabularyService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ version }) => {
      if (!version.id) {
        const today = moment().startOf('day');
        version.lastModified = today;
      }

      this.updateForm(version);

      this.vocabularyService.query().subscribe((res: HttpResponse<IVocabulary[]>) => (this.vocabularies = res.body || []));
    });
  }

  updateForm(version: IVersion): void {
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
      vocabularyId: version.vocabularyId
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
    const version = this.createFromForm();
    if (version.id !== undefined) {
      this.subscribeToSaveResponse(this.versionService.update(version));
    } else {
      this.subscribeToSaveResponse(this.versionService.create(version));
    }
  }

  private createFromForm(): IVersion {
    return {
      ...new Version(),
      id: this.editForm.get(['id'])!.value,
      status: this.editForm.get(['status'])!.value,
      itemType: this.editForm.get(['itemType'])!.value,
      language: this.editForm.get(['language'])!.value,
      publicationDate: this.editForm.get(['publicationDate'])!.value,
      lastModified: this.editForm.get(['lastModified'])!.value
        ? moment(this.editForm.get(['lastModified'])!.value, DATE_TIME_FORMAT)
        : undefined,
      number: this.editForm.get(['number'])!.value,
      uri: this.editForm.get(['uri'])!.value,
      canonicalUri: this.editForm.get(['canonicalUri'])!.value,
      uriSl: this.editForm.get(['uriSl'])!.value,
      notation: this.editForm.get(['notation'])!.value,
      title: this.editForm.get(['title'])!.value,
      definition: this.editForm.get(['definition'])!.value,
      previousVersion: this.editForm.get(['previousVersion'])!.value,
      initialVersion: this.editForm.get(['initialVersion'])!.value,
      creator: this.editForm.get(['creator'])!.value,
      publisher: this.editForm.get(['publisher'])!.value,
      notes: this.editForm.get(['notes'])!.value,
      versionNotes: this.editForm.get(['versionNotes'])!.value,
      versionChanges: this.editForm.get(['versionChanges'])!.value,
      discussionNotes: this.editForm.get(['discussionNotes'])!.value,
      license: this.editForm.get(['license'])!.value,
      licenseId: this.editForm.get(['licenseId'])!.value,
      citation: this.editForm.get(['citation'])!.value,
      ddiUsage: this.editForm.get(['ddiUsage'])!.value,
      translateAgency: this.editForm.get(['translateAgency'])!.value,
      translateAgencyLink: this.editForm.get(['translateAgencyLink'])!.value,
      vocabularyId: this.editForm.get(['vocabularyId'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVersion>>): void {
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

  trackById(index: number, item: IVocabulary): any {
    return item.id;
  }
}
