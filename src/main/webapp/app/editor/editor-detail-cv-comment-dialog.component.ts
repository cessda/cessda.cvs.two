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
import { Component, NgZone, OnDestroy, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { EditorService } from 'app/editor/editor.service';
import { Version } from 'app/shared/model/version.model';
import { Router } from '@angular/router';
import { JhiEventManager } from 'ng-jhipster';
import { FormBuilder, Validators } from '@angular/forms';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { IComment } from 'app/shared/model/comment.model';
import { CommentService } from 'app/entities/comment/comment.service';
import { Observable, Subscription } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import moment from 'moment';
import { Moment } from 'moment';
import { QuillModules } from 'ngx-quill';

@Component({
  templateUrl: './editor-detail-cv-comment-dialog.component.html',
})
export class EditorDetailCvCommentDialogComponent implements OnInit, OnDestroy {
  isSaving: boolean;
  account!: Account;
  versionParam!: Version;
  comments: IComment[] | undefined = [];
  isWriteComment = false;

  eventSubscriber?: Subscription;

  quillModules: QuillModules = {
    toolbar: [['bold', 'italic', 'underline', 'strike'], ['blockquote'], [{ list: 'ordered' }, { list: 'bullet' }], ['link'], ['clean']],
  };

  commentForm = this.fb.group({
    content: ['', [Validators.required]],
  });

  constructor(
    private accountService: AccountService,
    private commentService: CommentService,
    protected editorService: EditorService,
    public activeModal: NgbActiveModal,
    private router: Router,
    protected eventManager: JhiEventManager,
    private fb: FormBuilder,
    private _ngZone: NgZone,
  ) {
    this.isSaving = false;
  }

  clear(): void {
    this.activeModal.dismiss();
  }

  ngOnInit(): void {
    this.comments = this.versionParam.comments;
    this.accountService.identity().subscribe(account => {
      if (account) {
        this.account = account;
      }
    });

    this._ngZone.runOutsideAngular(() => {
      setTimeout(() => {
        const element = document.querySelector('#commentInput');
        element!.scrollIntoView({ behavior: 'smooth' });
      }, 500);
    });
    this.eventSubscriber = this.eventManager.subscribe('commentListModification', () => this.loadComment());
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventSubscriber.unsubscribe();
    }
  }

  loadComment(): void {
    this.commentService.findAllByVersion(this.versionParam.id!).subscribe((res: HttpResponse<IComment[]>) => (this.comments = res.body!));
  }

  saveComment(): void {
    this.isSaving = true;
    const newComment = {
      info: this.account.lastName + (this.account.firstName ? ', ' + this.account.firstName : ''),
      userId: this.account.id,
      content: this.commentForm.get(['content'])!.value,
      versionId: this.versionParam.id,
    };
    this.subscribeToSaveResponse(this.editorService.createComment(newComment));
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IComment>>): void {
    result.subscribe(
      response => this.onSaveSuccess(response.body!),
      () => this.onSaveError(),
    );
  }

  protected onSaveSuccess(newComment: IComment): void {
    this.isSaving = false;
    this.versionParam.comments!.push(newComment);
    this.commentForm.patchValue({ content: '' });
    this.isWriteComment = false;
    this.loadComment();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  parseDateTimeAgo(dateTime: Moment): string {
    return moment(dateTime).fromNow();
  }
}
