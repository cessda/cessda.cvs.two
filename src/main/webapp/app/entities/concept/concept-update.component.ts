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
import {JhiDataUtils, JhiEventManager, JhiEventWithContent, JhiFileLoadError} from 'ng-jhipster';

import {Concept, IConcept} from 'app/shared/model/concept.model';
import {ConceptService} from './concept.service';
import {AlertError} from 'app/shared/alert/alert-error.model';
import {IVersion} from 'app/shared/model/version.model';
import {VersionService} from 'app/entities/version/version.service';

@Component({
  selector: 'jhi-concept-update',
  templateUrl: './concept-update.component.html'
})
export class ConceptUpdateComponent implements OnInit {
  isSaving = false;
  versions: IVersion[] = [];

  editForm = this.fb.group({
    id: [],
    uri: [],
    notation: [null, [Validators.required, Validators.maxLength(240)]],
    title: [],
    definition: [],
    previousConcept: [],
    slConcept: [],
    parent: [null, [Validators.maxLength(240)]],
    position: [],
    versionId: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected conceptService: ConceptService,
    protected versionService: VersionService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ concept }) => {
      this.updateForm(concept);

      this.versionService.query().subscribe((res: HttpResponse<IVersion[]>) => (this.versions = res.body || []));
    });
  }

  updateForm(concept: IConcept): void {
    this.editForm.patchValue({
      id: concept.id,
      uri: concept.uri,
      notation: concept.notation,
      title: concept.title,
      definition: concept.definition,
      previousConcept: concept.previousConcept,
      slConcept: concept.slConcept,
      parent: concept.parent,
      position: concept.position,
      versionId: concept.versionId
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
    const concept = this.createFromForm();
    if (concept.id !== undefined) {
      this.subscribeToSaveResponse(this.conceptService.update(concept));
    } else {
      this.subscribeToSaveResponse(this.conceptService.create(concept));
    }
  }

  private createFromForm(): IConcept {
    return {
      ...new Concept(),
      id: this.editForm.get(['id'])!.value,
      uri: this.editForm.get(['uri'])!.value,
      notation: this.editForm.get(['notation'])!.value,
      title: this.editForm.get(['title'])!.value,
      definition: this.editForm.get(['definition'])!.value,
      previousConcept: this.editForm.get(['previousConcept'])!.value,
      slConcept: this.editForm.get(['slConcept'])!.value,
      parent: this.editForm.get(['parent'])!.value,
      position: this.editForm.get(['position'])!.value,
      versionId: this.editForm.get(['versionId'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IConcept>>): void {
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

  trackById(index: number, item: IVersion): any {
    return item.id;
  }
}
