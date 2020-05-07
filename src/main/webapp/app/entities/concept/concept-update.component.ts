import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiDataUtils, JhiFileLoadError, JhiEventManager, JhiEventWithContent } from 'ng-jhipster';

import { IConcept, Concept } from 'app/shared/model/concept.model';
import { ConceptService } from './concept.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { IVersion } from 'app/shared/model/version.model';
import { VersionService } from 'app/entities/version/version.service';

@Component({
  selector: 'jhi-concept-update',
  templateUrl: './concept-update.component.html'
})
export class ConceptUpdateComponent implements OnInit {
  isSaving = false;
  versions: IVersion[] = [];

  editForm = this.fb.group({
    id: [],
    uri: [],
    notation: [null, [Validators.required, Validators.maxLength(240)]],
    title: [],
    definition: [],
    previousConcept: [],
    slConcept: [],
    parent: [null, [Validators.maxLength(240)]],
    position: [],
    versionId: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected conceptService: ConceptService,
    protected versionService: VersionService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ concept }) => {
      this.updateForm(concept);

      this.versionService.query().subscribe((res: HttpResponse<IVersion[]>) => (this.versions = res.body || []));
    });
  }

  updateForm(concept: IConcept): void {
    this.editForm.patchValue({
      id: concept.id,
      uri: concept.uri,
      notation: concept.notation,
      title: concept.title,
      definition: concept.definition,
      previousConcept: concept.previousConcept,
      slConcept: concept.slConcept,
      parent: concept.parent,
      position: concept.position,
      versionId: concept.versionId
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
    const concept = this.createFromForm();
    if (concept.id !== undefined) {
      this.subscribeToSaveResponse(this.conceptService.update(concept));
    } else {
      this.subscribeToSaveResponse(this.conceptService.create(concept));
    }
  }

  private createFromForm(): IConcept {
    return {
      ...new Concept(),
      id: this.editForm.get(['id'])!.value,
      uri: this.editForm.get(['uri'])!.value,
      notation: this.editForm.get(['notation'])!.value,
      title: this.editForm.get(['title'])!.value,
      definition: this.editForm.get(['definition'])!.value,
      previousConcept: this.editForm.get(['previousConcept'])!.value,
      slConcept: this.editForm.get(['slConcept'])!.value,
      parent: this.editForm.get(['parent'])!.value,
      position: this.editForm.get(['position'])!.value,
      versionId: this.editForm.get(['versionId'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IConcept>>): void {
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

  trackById(index: number, item: IVersion): any {
    return item.id;
  }
}
