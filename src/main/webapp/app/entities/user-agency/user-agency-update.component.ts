/* eslint-disable @typescript-eslint/no-non-null-assertion */
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
import { FormBuilder, FormControl } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { UserAgency } from 'app/shared/model/user-agency.model';
import { UserAgencyService } from './user-agency.service';
import { Agency } from 'app/shared/model/agency.model';
import { AgencyService } from 'app/agency/agency.service';
import { AgencyRole } from 'app/shared/model/enumerations/agency-role.model';

@Component({
  selector: 'jhi-user-agency-update',
  templateUrl: './user-agency-update.component.html',
})
export class UserAgencyUpdateComponent implements OnInit {
  isSaving = false;
  agencies: Agency[] = [];

  editForm = this.fb.group({
    id: new FormControl<number | null>(null),
    agencyRole: new FormControl<AgencyRole | null>(null),
    language: new FormControl<string | null>(null),
    agencyId: new FormControl<number | null>(null),
  });

  constructor(
    protected userAgencyService: UserAgencyService,
    protected agencyService: AgencyService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userAgency }) => {
      this.updateForm(userAgency);

      this.agencyService.query().subscribe((res: HttpResponse<Agency[]>) => (this.agencies = res.body || []));
    });
  }

  updateForm(userAgency: UserAgency): void {
    this.editForm.patchValue({
      id: userAgency.id,
      agencyRole: userAgency.agencyRole,
      language: userAgency.language,
      agencyId: userAgency.agencyId,
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

  private createFromForm(): UserAgency {
    return {
      id: this.editForm.controls.id.value !== null ? this.editForm.controls.id.value : undefined,
      agencyRole: this.editForm.controls.agencyRole.value || undefined,
      language: this.editForm.controls.language.value || undefined,
      agencyId: this.editForm.controls.agencyId.value !== null ? this.editForm.controls.agencyId.value : undefined,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<UserAgency>>): void {
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

  trackById(index: number, item: Agency): number {
    return item.id || index;
  }
}
