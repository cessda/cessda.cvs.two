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

import { Resolver } from 'app/shared/model/resolver.model';
import { ResolverService } from './resolver.service';
import { ResourceType } from 'app/shared/model/enumerations/resource-type.model';
import { ResolverType } from 'app/shared/model/enumerations/resolver-type.model';

@Component({
  selector: 'jhi-resolver-update',
  templateUrl: './resolver-update.component.html',
})
export class ResolverUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: new FormControl<number | null>(null),
    resourceId: new FormControl<string | null>(null),
    resourceType: new FormControl<ResourceType | null>(null),
    resourceUrl: new FormControl<string | null>(null, Validators.required),
    resolverType: new FormControl<ResolverType | null>(null),
    resolverURI: new FormControl<string | null>(null, Validators.required),
  });

  constructor(
    protected resolverService: ResolverService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ resolver }) => {
      this.updateForm(resolver);
    });
  }

  updateForm(resolver: Resolver): void {
    this.editForm.patchValue({
      id: resolver.id,
      resourceId: resolver.resourceId,
      resourceType: resolver.resourceType,
      resourceUrl: resolver.resourceUrl,
      resolverType: resolver.resolverType,
      resolverURI: resolver.resolverURI,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const resolver = this.createFromForm();
    if (resolver.id !== undefined) {
      this.subscribeToSaveResponse(this.resolverService.update(resolver));
    } else {
      this.subscribeToSaveResponse(this.resolverService.create(resolver));
    }
  }

  private createFromForm(): Resolver {
    return {
      id: this.editForm.controls.id.value!,
      resourceId: this.editForm.controls.resourceId.value!,
      resourceType: this.editForm.controls.resourceType.value!,
      resourceUrl: this.editForm.controls.resourceUrl.value!,
      resolverType: this.editForm.controls.resolverType.value!,
      resolverURI: this.editForm.controls.resolverURI.value!,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<Resolver>>): void {
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
}
