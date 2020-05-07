import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiDataUtils, JhiFileLoadError, JhiEventManager, JhiEventWithContent } from 'ng-jhipster';

import { IVocabularyChange, VocabularyChange } from 'app/shared/model/vocabulary-change.model';
import { VocabularyChangeService } from './vocabulary-change.service';
import { AlertError } from 'app/shared/alert/alert-error.model';

@Component({
  selector: 'jhi-vocabulary-change-update',
  templateUrl: './vocabulary-change-update.component.html'
})
export class VocabularyChangeUpdateComponent implements OnInit {
  isSaving = false;
  dateDp: any;

  editForm = this.fb.group({
    id: [],
    vocabularyId: [],
    versionId: [],
    changeType: [null, [Validators.required, Validators.maxLength(60)]],
    description: [],
    userId: [],
    userName: [null, [Validators.maxLength(120)]],
    date: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected vocabularyChangeService: VocabularyChangeService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ vocabularyChange }) => {
      this.updateForm(vocabularyChange);
    });
  }

  updateForm(vocabularyChange: IVocabularyChange): void {
    this.editForm.patchValue({
      id: vocabularyChange.id,
      vocabularyId: vocabularyChange.vocabularyId,
      versionId: vocabularyChange.versionId,
      changeType: vocabularyChange.changeType,
      description: vocabularyChange.description,
      userId: vocabularyChange.userId,
      userName: vocabularyChange.userName,
      date: vocabularyChange.date
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
    const vocabularyChange = this.createFromForm();
    if (vocabularyChange.id !== undefined) {
      this.subscribeToSaveResponse(this.vocabularyChangeService.update(vocabularyChange));
    } else {
      this.subscribeToSaveResponse(this.vocabularyChangeService.create(vocabularyChange));
    }
  }

  private createFromForm(): IVocabularyChange {
    return {
      ...new VocabularyChange(),
      id: this.editForm.get(['id'])!.value,
      vocabularyId: this.editForm.get(['vocabularyId'])!.value,
      versionId: this.editForm.get(['versionId'])!.value,
      changeType: this.editForm.get(['changeType'])!.value,
      description: this.editForm.get(['description'])!.value,
      userId: this.editForm.get(['userId'])!.value,
      userName: this.editForm.get(['userName'])!.value,
      date: this.editForm.get(['date'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVocabularyChange>>): void {
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
