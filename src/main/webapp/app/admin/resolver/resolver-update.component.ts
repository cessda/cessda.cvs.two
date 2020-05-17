import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IResolver, Resolver } from 'app/shared/model/resolver.model';
import { ResolverService } from './resolver.service';

@Component({
  selector: 'jhi-resolver-update',
  templateUrl: './resolver-update.component.html'
})
export class ResolverUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    resourceId: [],
    resourceType: [],
    resourceUrl: [null, [Validators.required]],
    resolverType: [],
    resolverURI: [null, [Validators.required]]
  });

  constructor(protected resolverService: ResolverService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ resolver }) => {
      this.updateForm(resolver);
    });
  }

  updateForm(resolver: IResolver): void {
    this.editForm.patchValue({
      id: resolver.id,
      resourceId: resolver.resourceId,
      resourceType: resolver.resourceType,
      resourceUrl: resolver.resourceUrl,
      resolverType: resolver.resolverType,
      resolverURI: resolver.resolverURI
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

  private createFromForm(): IResolver {
    return {
      ...new Resolver(),
      id: this.editForm.get(['id'])!.value,
      resourceId: this.editForm.get(['resourceId'])!.value,
      resourceType: this.editForm.get(['resourceType'])!.value,
      resourceUrl: this.editForm.get(['resourceUrl'])!.value,
      resolverType: this.editForm.get(['resolverType'])!.value,
      resolverURI: this.editForm.get(['resolverURI'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IResolver>>): void {
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
