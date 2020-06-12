import { Component, OnInit } from '@angular/core';
import { IMetadataField } from 'app/shared/model/metadata-field.model';
import { EditorService } from 'app/editor/editor.service';
import { MetadataFieldService } from 'app/entities/metadata-field/metadata-field.service';
import { FormBuilder } from '@angular/forms';
import { METADATA_KEY_API } from 'app/shared/constants/metadata.constants';
import { HttpResponse } from '@angular/common/http';
import { IMetadataValue, MetadataValue } from 'app/shared/model/metadata-value.model';
import { ObjectType } from 'app/shared/model/enumerations/object-type.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'jhi-api-docs',
  templateUrl: './api-docs.component.html',
  styleUrls: ['./api-docs.component.scss']
})
export class ApiDocsComponent implements OnInit {
  metadataField?: IMetadataField | null;
  isWriteApiDocs = false;
  isSaving = false;

  quillModules: any = {
    toolbar: [['bold', 'italic', 'underline', 'strike'], ['blockquote'], [{ list: 'ordered' }, { list: 'bullet' }], ['link'], ['clean']]
  };

  ApiDocsForm = this.fb.group({
    content: ['', []]
  });

  constructor(protected editorService: EditorService, private metadataFieldService: MetadataFieldService, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.loadApiDocsGuide();
  }

  private loadApiDocsGuide(): void {
    this.metadataFieldService.findByKey(METADATA_KEY_API).subscribe((res: HttpResponse<IMetadataField>) => {
      this.metadataField = res.body;

      if (this.metadataField !== null) {
        if (this.metadataField.metadataValues?.length) {
          // add parent information to metadataValues
          this.metadataField.metadataValues.forEach(mv => {
            mv.metadataFieldId = this.metadataField!.id;
            mv.metadataKey = this.metadataField!.metadataKey;
          });
          this.ApiDocsForm.patchValue({ content: this.metadataField.metadataValues[0].value });
        }
      }
    });
  }

  private createFromForm(): IMetadataValue {
    if (this.metadataField?.metadataValues?.length) {
      return {
        ...this.metadataField?.metadataValues[0],
        value: this.ApiDocsForm.get(['content'])!.value
      };
    } else {
      return {
        ...new MetadataValue(),
        value: this.ApiDocsForm.get(['content'])!.value,
        objectType: ObjectType.SYSTEM,
        metadataKey: METADATA_KEY_API
      };
    }
  }

  saveApiDocs(): void {
    this.isSaving = true;
    const metadataValue = this.createFromForm();
    if (this.metadataField?.metadataValues?.length) {
      this.subscribeToSaveResponse(this.editorService.updateAppMetadata(metadataValue));
    } else {
      this.subscribeToSaveResponse(this.editorService.createAppMetadata(metadataValue));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMetadataValue>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.isWriteApiDocs = false;
    this.loadApiDocsGuide();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }
}
