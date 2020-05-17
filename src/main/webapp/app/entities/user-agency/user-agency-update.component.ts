import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IUserAgency, UserAgency } from 'app/shared/model/user-agency.model';
import { UserAgencyService } from './user-agency.service';
import { IAgency } from 'app/shared/model/agency.model';
import { AgencyService } from 'app/agency/agency.service';

@Component({
  selector: 'jhi-user-agency-update',
  templateUrl: './user-agency-update.component.html'
})
export class UserAgencyUpdateComponent implements OnInit {
  isSaving = false;
  agencies: IAgency[] = [];

  editForm = this.fb.group({
    id: [],
    agencyRole: [],
    language: [],
    agencyId: []
  });

  constructor(
    protected userAgencyService: UserAgencyService,
    protected agencyService: AgencyService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userAgency }) => {
      this.updateForm(userAgency);

      this.agencyService.query().subscribe((res: HttpResponse<IAgency[]>) => (this.agencies = res.body || []));
    });
  }

  updateForm(userAgency: IUserAgency): void {
    this.editForm.patchValue({
      id: userAgency.id,
      agencyRole: userAgency.agencyRole,
      language: userAgency.language,
      agencyId: userAgency.agencyId
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userAgency = this.createFromForm();
    if (userAgency.id !== undefined) {
      this.subscribeToSaveResponse(this.userAgencyService.update(userAgency));
    } else {
      this.subscribeToSaveResponse(this.userAgencyService.create(userAgency));
    }
  }

  private createFromForm(): IUserAgency {
    return {
      ...new UserAgency(),
      id: this.editForm.get(['id'])!.value,
      agencyRole: this.editForm.get(['agencyRole'])!.value,
      language: this.editForm.get(['language'])!.value,
      agencyId: this.editForm.get(['agencyId'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserAgency>>): void {
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

  trackById(index: number, item: IAgency): any {
    return item.id;
  }
}
