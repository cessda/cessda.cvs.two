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
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { JhiEventManager } from 'ng-jhipster';
import { FormBuilder, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { IMetadataValue } from 'app/shared/model/metadata-value.model';
import { ObjectType } from 'app/shared/model/enumerations/object-type.model';
import { EditorService } from 'app/editor/editor.service';
import { IMetadataField } from 'app/shared/model/metadata-field.model';

interface Quill {}

@Component({
  selector: 'jhi-metadata-item',
  templateUrl: './metadata-item.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MetadataItemComponent implements OnInit {
  @Input() metadataField?: IMetadataField;
  @Input() metadataValue!: IMetadataValue;
  @Input() isWriting!: boolean;
  @Input() position!: number;
  @Input() newTabLink!: boolean;

  // @ts-ignore
  public quill: Quill;

  isSaving = false;

  metadataForm = this.fb.group({
    identifier: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(40), Validators.pattern('^[a-z0-9-]*$')]],
    position: ['', [Validators.required]],
    content: ['', [Validators.minLength(30)]],
  });

  constructor(
    protected editorService: EditorService,
    protected eventManager: JhiEventManager,
    private fb: FormBuilder,
  ) {}

  public editorCreated(event: Quill): void {
    this.quill = event;
    const value = this.metadataForm.get(['content'])!.value;
    if (value) {
      // @ts-ignore
      this.quill.clipboard.dangerouslyPasteHTML(value);
    } else {
      // @ts-ignore
      this.quill.clipboard.dangerouslyPasteHTML('');
    }
  }

  ngOnInit(): void {
    this.metadataForm.patchValue({
      identifier: this.metadataValue.identifier ? this.metadataValue.identifier : 'section-' + this.position,
      position: this.metadataValue.position ? this.metadataValue.position : this.position,
      content: this.metadataValue.value,
      tableRow: 3,
      tableColumn: 3,
    });
  }

  cancel(): void {
    this.isWriting = false;
    this.metadataForm.patchValue({
      content: this.metadataValue.value,
    });
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
      metadataKey: this.metadataField!.metadataKey,
    };
  }

  saveMetadata(): void {
    this.isSaving = true;
    const metadataValue = this.createFromForm();
    if (!this.newTabLink) {
      // remove any target="_blank"
      metadataValue.value = (metadataValue.value as string).split(' rel="noopener noreferrer" target="_blank"').join('');
      this.metadataForm.patchValue({
        content: metadataValue.value,
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
      () => this.onSaveError(),
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
