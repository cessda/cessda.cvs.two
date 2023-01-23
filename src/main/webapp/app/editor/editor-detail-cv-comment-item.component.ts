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
import {Component, Input} from '@angular/core';

import {EditorService} from 'app/editor/editor.service';
import {IVersion} from 'app/shared/model/version.model';
import {JhiEventManager} from 'ng-jhipster';
import {FormBuilder, Validators} from '@angular/forms';
import {Account} from 'app/core/user/account.model';
import {IComment} from 'app/shared/model/comment.model';
import * as moment from 'moment';
import {Moment} from 'moment';
import {Observable} from 'rxjs';
import {HttpResponse} from '@angular/common/http';

@Component({
  selector: 'jhi-comment-item',
  templateUrl: './editor-detail-cv-comment-item.component.html'
})
export class EditorDetailCvCommentItemComponent {
  @Input() account?: Account;
  @Input() comment!: IComment;
  @Input() versionParam!: IVersion;
  isSaving: boolean;
  isWriteComment = false;

  quillModules: any = {
    toolbar: [['bold', 'italic', 'underline', 'strike'], ['blockquote'], [{ list: 'ordered' }, { list: 'bullet' }], ['link'], ['clean']]
  };

  commentForm = this.fb.group({
    content: ['', [Validators.required]]
  });

  constructor(protected editorService: EditorService, protected eventManager: JhiEventManager, private fb: FormBuilder) {
    this.isSaving = false;
  }

  clear(): void {
    this.isWriteComment = false;
  }

  parseDateTimeAgo(dateTime: Moment): string {
    return moment(dateTime).fromNow();
  }

  doEditComment(): void {
    this.isWriteComment = true;
    this.commentForm.patchValue({ content: this.comment.content });
  }

  doDeleteComment(): void {
    this.editorService.deleteComment(this.comment.id!).subscribe(() => {
      const index = this.versionParam.comments!.indexOf(this.comment, 0);
      if (index > -1) {
        this.versionParam.comments!.splice(index, 1);
      }
      this.eventManager.broadcast('commentListModification');
    });
  }

  saveComment(): void {
    this.isSaving = true;
    this.comment.content = this.commentForm.get(['content'])!.value;
    this.comment.dateTime = moment();
    this.subscribeToSaveResponse(this.editorService.updateComment(this.comment));
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IComment>>): void {
    result.subscribe(
      response => this.onSaveSuccess(response.body!),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(newComment: IComment): void {
    this.isSaving = false;
    this.isWriteComment = false;
    this.eventManager.broadcast('commentListModification');
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }
}
