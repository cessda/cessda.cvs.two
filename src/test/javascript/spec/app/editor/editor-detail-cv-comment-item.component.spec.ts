/*
 * Copyright © 2017-2026 CESSDA ERIC (support@cessda.eu)
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
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { EventManager } from 'app/core/util/event-manager.service';
import { of, throwError } from 'rxjs';
import moment from 'moment';

import { CvsTestModule } from '../../test.module';
import { MockEventManager } from '../../helpers/mock-event-manager.service';
import { EditorDetailCvCommentItemComponent } from 'app/editor/editor-detail-cv-comment-item.component';
import { EditorService } from 'app/editor/editor.service';
import { createNewVersion } from 'app/shared/model/version.model';
import { Comment } from 'app/shared/model/comment.model';

describe('Component Tests', () => {
  describe('Editor Detail CV Comment Item Component', () => {
    let comp: EditorDetailCvCommentItemComponent;
    let fixture: ComponentFixture<EditorDetailCvCommentItemComponent>;
    let service: EditorService;
    let mockEventManager: MockEventManager;

    let comment: Comment;
    let otherComment: Comment;

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [EditorDetailCvCommentItemComponent],
      })
        .overrideTemplate(EditorDetailCvCommentItemComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(EditorDetailCvCommentItemComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(EditorService);
      mockEventManager = TestBed.inject(EventManager) as unknown as MockEventManager;

      comment = { id: 5, content: '<p>Original</p>', versionId: 12 };
      otherComment = { id: 6, content: '<p>Another</p>', versionId: 12 };
      comp.comment = comment;
      comp.versionParam = { ...createNewVersion(12), comments: [otherComment, comment] };
    });

    it('should start out showing the comment, not the editor', () => {
      expect(comp.isSaving).toBe(false);
      expect(comp.isWriteComment).toBe(false);
    });

    it('should describe the comment time relative to now', () => {
      expect(comp.parseDateTimeAgo(moment().subtract(2, 'hours'))).toBe('2 hours ago');
    });

    describe('editing', () => {
      it('should open the editor filled with the current content', () => {
        comp.doEditComment();

        expect(comp.isWriteComment).toBe(true);
        expect(comp.commentForm.get('content')!.value).toBe('<p>Original</p>');
      });

      it('should close the editor on clear without saving', () => {
        const spy = spyOn(service, 'updateComment');
        comp.doEditComment();

        comp.clear();

        expect(comp.isWriteComment).toBe(false);
        expect(spy).not.toHaveBeenCalled();
      });

      it('should require some content', () => {
        comp.commentForm.patchValue({ content: '' });

        expect(comp.commentForm.invalid).toBe(true);
      });
    });

    describe('saving', () => {
      beforeEach(() => {
        comp.doEditComment();
        comp.commentForm.patchValue({ content: '<p>Changed</p>' });
      });

      it('should send the edited content with a fresh timestamp', () => {
        const spy = spyOn(service, 'updateComment').and.returnValue(of(new HttpResponse({ body: comment })));

        comp.saveComment();

        expect(spy).toHaveBeenCalledWith(comment);
        expect(comment.content).toBe('<p>Changed</p>');
        expect(moment.isMoment(comment.dateTime)).toBe(true);
        expect(comment.dateTime!.isSame(moment(), 'minute')).toBe(true);
      });

      it('should close the editor and announce the change on success', () => {
        spyOn(service, 'updateComment').and.returnValue(of(new HttpResponse({ body: comment })));

        comp.saveComment();

        expect(comp.isSaving).toBe(false);
        expect(comp.isWriteComment).toBe(false);
        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith('commentListModification');
      });

      it('should keep the editor open and announce nothing on failure', () => {
        spyOn(service, 'updateComment').and.returnValue(throwError(() => new Error('500')));

        comp.saveComment();

        expect(comp.isSaving).toBe(false);
        expect(comp.isWriteComment).toBe(true);
        expect(mockEventManager.broadcastSpy).not.toHaveBeenCalled();
      });

      it('should be saving while the request is in flight', () => {
        spyOn(service, 'updateComment').and.returnValue(of());

        comp.saveComment();

        expect(comp.isSaving).toBe(true);
      });
    });

    describe('deleting', () => {
      it('should delete the comment by its id', () => {
        const spy = spyOn(service, 'deleteComment').and.returnValue(of(new HttpResponse<void>()));

        comp.doDeleteComment();

        expect(spy).toHaveBeenCalledWith(5);
      });

      it('should remove the comment from the version and announce the change', () => {
        spyOn(service, 'deleteComment').and.returnValue(of(new HttpResponse<void>()));

        comp.doDeleteComment();

        expect(comp.versionParam.comments).toEqual([otherComment]);
        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith('commentListModification');
      });

      it('should leave the list alone when the comment is not in it', () => {
        spyOn(service, 'deleteComment').and.returnValue(of(new HttpResponse<void>()));
        comp.versionParam = { ...createNewVersion(12), comments: [otherComment] };

        comp.doDeleteComment();

        expect(comp.versionParam.comments).toEqual([otherComment]);
        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith('commentListModification');
      });
    });
  });
});
