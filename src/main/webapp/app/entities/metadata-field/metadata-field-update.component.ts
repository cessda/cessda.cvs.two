import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiDataUtils, JhiFileLoadError, JhiEventManager, JhiEventWithContent } from 'ng-jhipster';

import { IMetadataField, MetadataField } from 'app/shared/model/metadata-field.model';
import { MetadataFieldService } from './metadata-field.service';
import { AlertError } from 'app/shared/alert/alert-error.model';

@Component({
  selector: 'jhi-metadata-field-update',
  templateUrl: './metadata-field-update.component.html'
})
export class MetadataFieldUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    metadataKey: [null, [Validators.required, Validators.maxLength(240)]],
    description: [],
    objectType: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected metadataFieldService: MetadataFieldService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ metadataField }) => {
      this.updateForm(metadataField);
    });
  }

  updateForm(metadataField: IMetadataField): void {
    this.editForm.patchValue({
      id: metadataField.id,
      metadataKey: metadataField.metadataKey,
      description: metadataField.description,
      objectType: metadataField.objectType
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
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
    const metadataField = this.createFromForm();
    if (metadataField.id !== undefined) {
      this.subscribeToSaveResponse(this.metadataFieldService.update(metadataField));
    } else {
      this.subscribeToSaveResponse(this.metadataFieldService.create(metadataField));
    }
  }

  private createFromForm(): IMetadataField {
    return {
      ...new MetadataField(),
      id: this.editForm.get(['id'])!.value,
      metadataKey: this.editForm.get(['metadataKey'])!.value,
      description: this.editForm.get(['description'])!.value,
      objectType: this.editForm.get(['objectType'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMetadataField>>): void {
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
