import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiDataUtils, JhiEventManager, JhiEventWithContent, JhiFileLoadError } from 'ng-jhipster';

import { IMetadataValue, MetadataValue } from 'app/shared/model/metadata-value.model';
import { MetadataValueService } from './metadata-value.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { IMetadataField } from 'app/shared/model/metadata-field.model';
import { MetadataFieldService } from 'app/entities/metadata-field/metadata-field.service';

@Component({
  selector: 'jhi-metadata-value-update',
  templateUrl: './metadata-value-update.component.html'
})
export class MetadataValueUpdateComponent implements OnInit {
  isSaving = false;
  metadatafields: IMetadataField[] = [];

  editForm = this.fb.group({
    id: [],
    identifier: [null, [Validators.maxLength(240)]],
    position: [],
    value: [],
    objectType: [],
    objectId: [],
    metadataFieldId: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected metadataValueService: MetadataValueService,
    protected metadataFieldService: MetadataFieldService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ metadataValue }) => {
      this.updateForm(metadataValue);

      this.metadataFieldService.query().subscribe((res: HttpResponse<IMetadataField[]>) => (this.metadatafields = res.body || []));
    });
  }

  updateForm(metadataValue: IMetadataValue): void {
    this.editForm.patchValue({
      id: metadataValue.id,
      identifier: metadataValue.identifier,
      position: metadataValue.position,
      value: metadataValue.value,
      objectType: metadataValue.objectType,
      objectId: metadataValue.objectId,
      metadataFieldId: metadataValue.metadataFieldId
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
    const metadataValue = this.createFromForm();
    if (metadataValue.id !== undefined) {
      this.subscribeToSaveResponse(this.metadataValueService.update(metadataValue));
    } else {
      this.subscribeToSaveResponse(this.metadataValueService.create(metadataValue));
    }
  }

  private createFromForm(): IMetadataValue {
    return {
      ...new MetadataValue(),
      id: this.editForm.get(['id'])!.value,
      identifier: this.editForm.get(['identifier'])!.value,
      position: this.editForm.get(['position'])!.value,
      value: this.editForm.get(['value'])!.value,
      objectType: this.editForm.get(['objectType'])!.value,
      objectId: this.editForm.get(['objectId'])!.value,
      metadataFieldId: this.editForm.get(['metadataFieldId'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMetadataValue>>): void {
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

  trackById(index: number, item: IMetadataField): any {
    return item.id;
  }
}
