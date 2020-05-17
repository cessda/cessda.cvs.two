import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ILicence, Licence } from 'app/shared/model/licence.model';
import { LicenceService } from './licence.service';

@Component({
  selector: 'jhi-licence-update',
  templateUrl: './licence-update.component.html'
})
export class LicenceUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.maxLength(255)]],
    link: [null, [Validators.maxLength(255)]],
    logoLink: [null, [Validators.maxLength(255)]],
    abbr: [null, [Validators.maxLength(255)]]
  });

  constructor(protected licenceService: LicenceService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

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
      logoLink: licence.logoLink,
      abbr: licence.abbr
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const licence = this.createFromForm();
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
      logoLink: this.editForm.get(['logoLink'])!.value,
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
}
