import {Component, OnInit} from '@angular/core';
import {HttpEventType, HttpResponse} from '@angular/common/http';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';

import {ILicence, Licence} from 'app/shared/model/licence.model';
import {LicenceService} from './licence.service';
import {FileUploadService} from 'app/shared/upload/file-upload.service';

@Component({
  selector: 'jhi-licence-update',
  templateUrl: './licence-update.component.html'
})
export class LicenceUpdateComponent implements OnInit {
  isSaving = false;

  selectedFiles?: FileList;
  currentFileUpload?: File | null;
  currentImage?: string;
  progress: { percentage: number } = {
    percentage: 0
  };

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.maxLength(255)]],
    link: [
      null,
      [
        Validators.pattern(
          '(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})'
        )
      ]
    ],
    abbr: [null, [Validators.required, Validators.maxLength(100)]]
  });

  constructor(
    protected licenceService: LicenceService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder,
    protected fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ licence }) => {
      this.updateForm(licence);
    });
  }

  updateForm(licence: ILicence): void {
    this.editForm.patchValue({
      id: licence.id,
      name: licence.name,
      link: licence.link,
      abbr: licence.abbr
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

  private createFromForm(): ILicence {
    return {
      ...new Licence(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      link: this.editForm.get(['link'])!.value,
      abbr: this.editForm.get(['abbr'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILicence>>): void {
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

  selectFile(selectFileEvent: { target: { files: FileList | undefined } }): void {
    this.selectedFiles = selectFileEvent.target.files;
    this.progress.percentage = 0;
    this.currentFileUpload = this.selectedFiles!.item(0);
    this.fileUploadService.uploadLicenseImage(this.currentFileUpload!).subscribe(event => {
      if (event.type === HttpEventType.UploadProgress) {
        this.progress.percentage = Math.round((100 * event.loaded) / event.total!);
      } else if (event instanceof HttpResponse) {
        this.currentImage = event.body!.toString();
      }
    });

    this.selectedFiles = undefined;
  }

  removePicture(): void {
    this.currentImage = undefined;
  }
}
