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
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { UserAgency } from 'app/shared/model/user-agency.model';
import { UserAgencyService } from './user-agency.service';
import { IAgency } from 'app/shared/model/agency.model';
import { AgencyService } from 'app/agency/agency.service';

@Component({
  selector: 'jhi-user-agency-update',
  templateUrl: './user-agency-update.component.html',
})
export class UserAgencyUpdateComponent implements OnInit {
  isSaving = false;
  agencies: IAgency[] = [];

  editForm = this.fb.group({
    id: [],
    agencyRole: [],
    language: [],
    agencyId: [],
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

      this.agencyService.query().subscribe((res: HttpResponse<IAgency[]>) => (this.agencies = res.body || []));
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
      id: this.editForm.get(['id'])!.value,
      agencyRole: this.editForm.get(['agencyRole'])!.value,
      language: this.editForm.get(['language'])!.value,
      agencyId: this.editForm.get(['agencyId'])!.value,
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

  trackById(index: number, item: IAgency): number {
    return item.id || index;
  }
}
