import { Component, Input, OnInit } from '@angular/core';

import { JhiEventManager } from 'ng-jhipster';
import { FormBuilder, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { IMetadataValue } from 'app/shared/model/metadata-value.model';
import { ObjectType } from 'app/shared/model/enumerations/object-type.model';
import { EditorService } from 'app/editor/editor.service';
import { IMetadataField } from 'app/shared/model/metadata-field.model';

@Component({
  selector: 'jhi-metadata-item',
  templateUrl: './metadata-item.component.html'
})
export class MetadataItemComponent implements OnInit {
  @Input() metadataField?: IMetadataField;
  @Input() metadataValue!: IMetadataValue;
  @Input() isWriting!: boolean;
  @Input() position!: number;
  @Input() newTabLink!: boolean;
  isSaving = false;

  quillModules: any = {
    toolbar: [['bold', 'italic', 'underline', 'strike'], ['blockquote'], [{ list: 'ordered' }, { list: 'bullet' }], ['link'], ['clean']]
  };

  metadataForm = this.fb.group({
    identifier: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(40), Validators.pattern('^[a-z0-9-]*$')]],
    position: ['', [Validators.required]],
    content: ['', [Validators.minLength(30)]]
  });

  constructor(protected editorService: EditorService, protected eventManager: JhiEventManager, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.metadataForm.patchValue({
      identifier: this.metadataValue.identifier ? this.metadataValue.identifier : 'section-' + this.position,
      position: this.metadataValue.position ? this.metadataValue.position : this.position,
      content: this.metadataValue.value
    });
  }

  clear(): void {
    this.isWriting = false;
  }

  doEditMetadata(): void {
    this.isWriting = true;
  }

  doDeleteMetadata(): void {
    this.editorService.deleteAppMetadata(this.metadataValue.id!).subscribe(() => {
      this.eventManager.broadcast('metadataListModification');
    });
  }

  private createFromForm(): IMetadataValue {
    return {
      ...this.metadataValue,
      identifier: this.metadataForm.get(['identifier'])!.value,
      position: this.metadataForm.get(['position'])!.value ? this.metadataForm.get(['position'])!.value : this.position,
      value: this.metadataForm.get(['content'])!.value,
      objectType: ObjectType.SYSTEM,
      metadataKey: this.metadataField!.metadataKey
    };
  }

  saveMetadata(): void {
    this.isSaving = true;
    const metadataValue = this.createFromForm();
    if (!this.newTabLink) {
      // remove any target="_blank"
      metadataValue.value = (metadataValue.value as string).split(' rel="noopener noreferrer" target="_blank"').join('');
      this.metadataForm.patchValue({
        content: metadataValue.value
      });
    }
    if (this.metadataValue.id !== undefined) {
      this.subscribeToSaveResponse(this.editorService.updateAppMetadata(metadataValue));
    } else {
      this.subscribeToSaveResponse(this.editorService.createAppMetadata(metadataValue));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMetadataValue>>): void {
    result.subscribe(
      response => this.onSaveSuccess(response.body!),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(newComment: IMetadataValue): void {
    this.isSaving = false;
    this.isWriting = false;
    this.eventManager.broadcast('metadataListModification');
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }
}
