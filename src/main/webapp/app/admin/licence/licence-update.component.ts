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
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { Licence } from 'app/shared/model/licence.model';
import { LicenceService } from './licence.service';
import { FileUploadService } from 'app/shared/upload/file-upload.service';

@Component({
  selector: 'jhi-licence-update',
  templateUrl: './licence-update.component.html',
})
export class LicenceUpdateComponent implements OnInit {
  isSaving = false;

  selectedFiles: FileList | null = null;
  currentFileUpload: File | null = null;
  currentImage?: string;
  progress: { percentage: number } = {
    percentage: 0,
  };

  editForm = this.fb.group({
    id: new FormControl<number | null>(null),
    name: new FormControl<string>('', { nonNullable: true, validators: [Validators.required, Validators.maxLength(255)] }),
    link: new FormControl<string | null>(null, [
      Validators.pattern(
        '(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})',
      ),
    ]),
    abbr: new FormControl<string>('', { nonNullable: true, validators: [Validators.required, Validators.maxLength(100)] }),
  });

  constructor(
    protected licenceService: LicenceService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder,
    protected fileUploadService: FileUploadService,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ licence }) => {
      this.updateForm(licence);
    });
  }

  updateForm(licence: Licence): void {
    this.editForm.patchValue({
      id: licence.id,
      name: licence.name,
      link: licence.link,
      abbr: licence.abbr,
    });
    this.currentImage = licence.logoLink;
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const licence = this.createFromForm();
    licence.logoLink = this.currentImage;
    if (licence.id !== undefined) {
      this.subscribeToSaveResponse(this.licenceService.update(licence));
    } else {
      this.subscribeToSaveResponse(this.licenceService.create(licence));
    }
  }

  private createFromForm(): Licence {
    return {
      id: this.editForm.controls.id.value || undefined,
      name: this.editForm.controls.name.value,
      link: this.editForm.controls.link.value || undefined,
      abbr: this.editForm.controls.abbr.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<Licence>>): void {
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

  selectFile(selectFileEvent: Event): void {
    this.selectedFiles = (selectFileEvent.target as HTMLInputElement).files;
    if (!this.selectedFiles) {
      return;
    }
    this.currentFileUpload = this.selectedFiles.item(0);
    if (!this.currentFileUpload) {
      return;
    }
    this.progress.percentage = 0;
    this.fileUploadService.uploadLicenseImage(this.currentFileUpload).subscribe(event => {
      if (event.type === HttpEventType.UploadProgress && event.total) {
        this.progress.percentage = Math.round((100 * event.loaded) / event.total);
      } else if (event instanceof HttpResponse && event.body) {
        this.currentImage = event.body.toString();
      }
    });

    this.selectedFiles = null;
  }

  removePicture(): void {
    this.currentImage = undefined;
  }
}
