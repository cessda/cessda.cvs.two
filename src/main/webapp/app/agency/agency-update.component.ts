import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiDataUtils, JhiFileLoadError, JhiEventManager, JhiEventWithContent } from 'ng-jhipster';

import { IAgency, Agency } from 'app/shared/model/agency.model';
import { AgencyService } from './agency.service';
import { AlertError } from 'app/shared/alert/alert-error.model';

@Component({
  selector: 'jhi-agency-update',
  templateUrl: './agency-update.component.html'
})
export class AgencyUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.maxLength(240)]],
    link: [null, [Validators.maxLength(255)]],
    description: [],
    logopath: [null, [Validators.maxLength(255)]],
    license: [null, [Validators.maxLength(240)]],
    licenseId: [],
    uri: [null, [Validators.maxLength(255)]],
    canonicalUri: [null, [Validators.maxLength(255)]]
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected agencyService: AgencyService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ agency }) => {
      this.updateForm(agency);
    });
  }

  updateForm(agency: IAgency): void {
    this.editForm.patchValue({
      id: agency.id,
      name: agency.name,
      link: agency.link,
      description: agency.description,
      logopath: agency.logopath,
      license: agency.license,
      licenseId: agency.licenseId,
      uri: agency.uri,
      canonicalUri: agency.canonicalUri
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
    const agency = this.createFromForm();
    if (agency.id !== undefined) {
      this.subscribeToSaveResponse(this.agencyService.update(agency));
    } else {
      this.subscribeToSaveResponse(this.agencyService.create(agency));
    }
  }

  private createFromForm(): IAgency {
    return {
      ...new Agency(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      link: this.editForm.get(['link'])!.value,
      description: this.editForm.get(['description'])!.value,
      logopath: this.editForm.get(['logopath'])!.value,
      license: this.editForm.get(['license'])!.value,
      licenseId: this.editForm.get(['licenseId'])!.value,
      uri: this.editForm.get(['uri'])!.value,
      canonicalUri: this.editForm.get(['canonicalUri'])!.value
    };
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
}
