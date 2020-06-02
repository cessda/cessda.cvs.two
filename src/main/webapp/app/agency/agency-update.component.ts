import { Component, OnInit } from '@angular/core';
import { HttpEventType, HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiDataUtils, JhiFileLoadError, JhiEventManager, JhiEventWithContent } from 'ng-jhipster';

import { IAgency, Agency } from 'app/shared/model/agency.model';
import { AgencyService } from './agency.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { FileUploadService } from 'app/shared/upload/file-upload.service';
import { ILicence } from 'app/shared/model/licence.model';
import { LicenceService } from 'app/admin/licence/licence.service';

@Component({
  selector: 'jhi-agency-update',
  templateUrl: './agency-update.component.html'
})
export class AgencyUpdateComponent implements OnInit {
  isSaving = false;
  selectedFiles?: FileList;
  currentFileUpload?: File | null;
  currentImage?: string;
  licences?: ILicence[];
  progress: {
    percentage: number;
  } = {
    percentage: 0
  };

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.maxLength(240)]],
    link: [
      null,
      [
        Validators.pattern(
          '(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})'
        )
      ]
    ],
    description: [],
    licenseId: [],
    uri: [null, [Validators.maxLength(255)]],
    canonicalUri: [null, [Validators.maxLength(255)]]
  });

  constructor(
    private licenceService: LicenceService,
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected agencyService: AgencyService,
    protected activatedRoute: ActivatedRoute,
    protected fileUploadService: FileUploadService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ agency }) => {
      this.licenceService
        .query({
          page: 0,
          size: 50,
          sort: ['id,asc']
        })
        .subscribe((res: HttpResponse<ILicence[]>) => {
          this.licences = res.body!;
          this.updateForm(agency);
        });
    });
  }

  updateForm(agency: IAgency): void {
    this.editForm.patchValue({
      id: agency.id,
      name: agency.name,
      link: agency.link,
      description: agency.description,
      licenseId: agency.licenseId,
      uri: agency.uri,
      canonicalUri: agency.canonicalUri
    });
    this.currentImage = agency.logopath;
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
    const agency = this.createFromForm();
    agency.logopath = this.currentImage;
    if (agency.id !== undefined) {
      this.subscribeToSaveResponse(this.agencyService.update(agency));
    } else {
      this.subscribeToSaveResponse(this.agencyService.create(agency));
    }
  }

  private createFromForm(): IAgency {
    const agency = {
      ...new Agency(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      link: this.editForm.get(['link'])!.value,
      description: this.editForm.get(['description'])!.value,
      licenseId: +this.editForm.get(['licenseId'])!.value,
      uri: this.editForm.get(['uri'])!.value,
      canonicalUri: this.editForm.get(['canonicalUri'])!.value
    };
    if (agency.licenseId) {
      agency.license = this.licences!.filter(l => l.id === agency.licenseId)![0].name;
    }
    return agency;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAgency>>): void {
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
    this.fileUploadService.uploadAgencyImage(this.currentFileUpload!).subscribe(event => {
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
