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
import moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiDataUtils, JhiEventManager, JhiEventWithContent, JhiFileLoadError } from 'ng-jhipster';

import { Comment } from 'app/shared/model/comment.model';
import { CommentService } from './comment.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { Version } from 'app/shared/model/version.model';
import { VersionService } from 'app/entities/version/version.service';

@Component({
  selector: 'jhi-comment-update',
  templateUrl: './comment-update.component.html',
})
export class CommentUpdateComponent implements OnInit {
  isSaving = false;
  versions: Version[] = [];

  editForm = this.fb.group({
    id: new FormControl<number | null>(null),
    info: new FormControl<string | null>(null, [Validators.maxLength(255)]),
    content: new FormControl<string | null>(null),
    userId: new FormControl<number | null>(null),
    dateTime: new FormControl<string | null>(null),
    versionId: new FormControl<number | null>(null),
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected commentService: CommentService,
    protected versionService: VersionService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ comment }) => {
      if (!comment.id) {
        const today = moment().startOf('day');
        comment.dateTime = today;
      }

      this.updateForm(comment);

      this.versionService.query().subscribe((res: HttpResponse<Version[]>) => (this.versions = res.body || []));
    });
  }

  updateForm(comment: Comment): void {
    this.editForm.patchValue({
      id: comment.id,
      info: comment.info,
      content: comment.content,
      userId: comment.userId,
      dateTime: comment.dateTime ? comment.dateTime.format(DATE_TIME_FORMAT) : null,
      versionId: comment.versionId,
    });
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: JhiFileLoadError) => {
        this.eventManager.broadcast(new JhiEventWithContent<AlertError>('cvsApp.error', { ...err, key: 'error.file.' + err.key }));
      },
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const comment = this.createFromForm();
    if (comment.id !== undefined) {
      this.subscribeToSaveResponse(this.commentService.update(comment));
    } else {
      this.subscribeToSaveResponse(this.commentService.create(comment));
    }
  }

  private createFromForm(): Comment {
    // Extract dateTime to its own variable so that it can be truthy-tested
    const dateTime = this.editForm.controls.dateTime.value;

    return {
      id: this.editForm.controls.id.value !== null ? this.editForm.controls.id.value : undefined,
      info: this.editForm.controls.info.value || undefined,
      content: this.editForm.controls.content.value || undefined,
      userId: this.editForm.controls.userId.value || undefined,
      dateTime: dateTime ? moment(dateTime, DATE_TIME_FORMAT) : undefined,
      versionId: this.editForm.controls.versionId.value !== null ? this.editForm.controls.versionId.value : undefined,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<Comment>>): void {
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

  trackById(_index: number, item: Version): number {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    return item.id!;
  }
}
