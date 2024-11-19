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

import { MetadataValue } from 'app/shared/model/metadata-value.model';
import { MetadataValueService } from './metadata-value.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { MetadataField } from 'app/shared/model/metadata-field.model';
import { MetadataFieldService } from 'app/entities/metadata-field/metadata-field.service';
import { ObjectType } from 'app/shared/model/enumerations/object-type.model';

@Component({
  selector: 'jhi-metadata-value-update',
  templateUrl: './metadata-value-update.component.html',
})
export class MetadataValueUpdateComponent implements OnInit {
  isSaving = false;
  metadatafields: MetadataField[] = [];

  editForm = this.fb.group({
    id: new FormControl<number | null>(null),
    identifier: new FormControl<string | null>(null, [Validators.maxLength(240)]),
    position: new FormControl<number | null>(null),
    value: new FormControl<string | null>(null),
    objectType: new FormControl<ObjectType | null>(null),
    objectId: new FormControl<number | null>(null),
    metadataFieldId: new FormControl<number | null>(null),
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected metadataValueService: MetadataValueService,
    protected metadataFieldService: MetadataFieldService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ metadataValue }) => {
      this.updateForm(metadataValue);

      this.metadataFieldService.query().subscribe((res: HttpResponse<MetadataField[]>) => (this.metadatafields = res.body || []));
    });
  }

  updateForm(metadataValue: MetadataValue): void {
    this.editForm.patchValue({
      id: metadataValue.id,
      identifier: metadataValue.identifier,
      position: metadataValue.position,
      value: metadataValue.value,
      objectType: metadataValue.objectType,
      objectId: metadataValue.objectId,
      metadataFieldId: metadataValue.metadataFieldId,
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
    const metadataValue = this.createFromForm();
    if (metadataValue.id !== undefined) {
      this.subscribeToSaveResponse(this.metadataValueService.update(metadataValue));
    } else {
      this.subscribeToSaveResponse(this.metadataValueService.create(metadataValue));
    }
  }

  private createFromForm(): MetadataValue {
    return {
      id: this.editForm.controls.id.value !== null ? this.editForm.controls.id.value : undefined,
      identifier: this.editForm.controls.identifier.value || undefined,
      position: this.editForm.controls.position.value !== null ? this.editForm.controls.position.value : undefined,
      value: this.editForm.controls.value.value || undefined,
      objectType: this.editForm.controls.objectType.value || undefined,
      objectId: this.editForm.controls.objectId.value !== null ? this.editForm.controls.objectId.value : undefined,
      metadataFieldId: this.editForm.controls.metadataFieldId.value !== null ? this.editForm.controls.metadataFieldId.value : undefined,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<MetadataValue>>): void {
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

  trackById(_index: number, item: MetadataField) {
    return item.id!;
  }
}
