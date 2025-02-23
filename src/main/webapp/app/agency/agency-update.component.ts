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

import { Agency, createNewAgency } from 'app/shared/model/agency.model';
import { AgencyService } from './agency.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { FileUploadService } from 'app/shared/upload/file-upload.service';
import { Licence } from 'app/shared/model/licence.model';
import { LicenceService } from 'app/admin/licence/licence.service';

@Component({
  selector: 'jhi-agency-update',
  templateUrl: './agency-update.component.html',
})
export class AgencyUpdateComponent implements OnInit {
  isSaving = false;
  currentFileUpload: File | null = null;
  currentImage?: string;
  licences: Licence[] = [];
  progress: {
    percentage: number;
  } = {
    percentage: 0,
  };

  editForm = this.fb.group({
    id: new FormControl<number | null>(null),
    name: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.maxLength(240)] }),
    link: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.pattern(
          '(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})',
        ),
      ],
    }),
    description: new FormControl<string | null>(null),
    licenseId: new FormControl<number | null>(null),
    uri: new FormControl<string | null>(null, [Validators.maxLength(255)]),
    uriCode: new FormControl<string | null>(null, [Validators.maxLength(255)]),
    canonicalUri: new FormControl<string | null>(null, [Validators.maxLength(255)]),
  });

  constructor(
    private licenceService: LicenceService,
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected agencyService: AgencyService,
    protected activatedRoute: ActivatedRoute,
    protected fileUploadService: FileUploadService,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ agency }) => {
      this.licenceService
        .query({
          page: 0,
          size: 50,
          sort: ['id,asc'],
        })
        .subscribe((res: HttpResponse<Licence[]>) => {
          this.licences = res.body || [];
          this.updateForm(agency);
        });
    });
  }

  updateForm(agency: Agency): void {
    this.editForm.patchValue({
      id: agency.id,
      name: agency.name,
      link: agency.link,
      description: agency.description,
      licenseId: agency.licenseId,
      uri: agency.uri,
      uriCode: agency.uriCode,
      canonicalUri: agency.canonicalUri,
    });
    this.currentImage = agency.logopath;
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
    const agency = this.createFromForm();
    agency.logopath = this.currentImage;
    if (agency.id !== undefined) {
      this.subscribeToSaveResponse(this.agencyService.update(agency));
    } else {
      this.subscribeToSaveResponse(this.agencyService.create(agency));
    }
  }

  private createFromForm(): Agency {
    const agency = createNewAgency({
      id: this.editForm.controls.id.value || undefined,
      name: this.editForm.controls.name.value,
      link: this.editForm.controls.link.value,
      description: this.editForm.controls.description.value || undefined,
      licenseId: this.editForm.controls.licenseId.value || undefined,
      uri: this.editForm.controls.uri.value || undefined,
      uriCode: this.editForm.controls.uriCode.value || undefined,
      canonicalUri: this.editForm.controls.canonicalUri.value || undefined,
    });
    if (agency.licenseId) {
      agency.license = this.licences.filter(l => l.id === agency.licenseId)[0].name;
    }
    return agency;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<Agency>>): void {
    result.subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  selectFile(selectFileEvent: Event): void {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    const selectedFiles = (selectFileEvent.target as HTMLInputElement).files!;

    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    this.currentFileUpload = selectedFiles[0]!;

    this.progress.percentage = 0;
    this.fileUploadService
      .uploadAgencyImage(this.currentFileUpload)
      .pipe(FileUploadService.uploadFileHandler)
      .subscribe(status => {
        this.progress.percentage = status.progress;
        if (status.location) {
          this.currentImage = status.location.split('/').pop();
        }
      });
  }

  removePicture(): void {
    this.currentImage = undefined;
  }
}
