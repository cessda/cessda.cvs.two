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
import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpResponse } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import moment from 'moment';

import { CvsTestModule } from '../../test.module';
import { MockActiveModal } from '../../helpers/mock-active-modal.service';
import { MockAccountService } from '../../helpers/mock-account.service';
import { EditorDetailCvCommentDialogComponent } from 'app/editor/editor-detail-cv-comment-dialog.component';
import { EditorService } from 'app/editor/editor.service';
import { CommentService } from 'app/entities/comment/comment.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { createNewVersion } from 'app/shared/model/version.model';
import { Comment } from 'app/shared/model/comment.model';

describe('Component Tests', () => {
  describe('Editor Detail CV Comment Dialog Component', () => {
    let comp: EditorDetailCvCommentDialogComponent;
    let fixture: ComponentFixture<EditorDetailCvCommentDialogComponent>;
    let editorService: EditorService;
    let commentService: CommentService;
    let mockActiveModal: MockActiveModal;
    let mockAccountService: MockAccountService;

    const account = { id: 3, login: 'admin', firstName: 'Ada', lastName: 'Lovelace' } as Account;

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [EditorDetailCvCommentDialogComponent],
      })
        .overrideTemplate(EditorDetailCvCommentDialogComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(EditorDetailCvCommentDialogComponent);
      comp = fixture.componentInstance;
      editorService = fixture.debugElement.injector.get(EditorService);
      commentService = fixture.debugElement.injector.get(CommentService);
      mockActiveModal = TestBed.inject(NgbActiveModal) as unknown as MockActiveModal;
      mockAccountService = TestBed.inject(AccountService) as unknown as MockAccountService;
      mockAccountService.setIdentityResponse(account);

      comp.versionParam = { ...createNewVersion(12), comments: [] };
      spyOn(commentService, 'findAllByVersion').and.returnValue(of(new HttpResponse({ body: [] })));
    });

    it('should dismiss the modal on clear', () => {
      comp.clear();

      expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
    });

    it('should take the comments from the version it was opened on', fakeAsync(() => {
      const existing = [{ id: 1, content: 'First' } as Comment];
      comp.versionParam = { ...createNewVersion(12), comments: existing };

      comp.ngOnInit();
      tick(500);

      expect(comp.comments).toBe(existing);
      expect(comp.account).toBe(account);
    }));

    describe('saving a comment', () => {
      beforeEach(fakeAsync(() => {
        comp.ngOnInit();
        tick(500);
        comp.commentForm.patchValue({ content: 'Looks good to me' });
      }));

      it('should sign the comment with the account name', fakeAsync(() => {
        const spy = spyOn(editorService, 'createComment').and.returnValue(of(new HttpResponse({ body: {} as Comment })));

        comp.saveComment();
        tick();

        expect(spy).toHaveBeenCalledWith({
          info: 'Lovelace, Ada',
          userId: 3,
          content: 'Looks good to me',
          versionId: 12,
        });
      }));

      it('should sign with the surname alone when there is no first name', fakeAsync(() => {
        mockAccountService.setIdentityResponse({ id: 3, lastName: 'Lovelace' } as Account);
        comp.ngOnInit();
        tick(500);
        comp.commentForm.patchValue({ content: 'Looks good to me' });
        const spy = spyOn(editorService, 'createComment').and.returnValue(of(new HttpResponse({ body: {} as Comment })));

        comp.saveComment();
        tick();

        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ info: 'Lovelace' }));
      }));

      it('should add the saved comment to the version and clear the form', fakeAsync(() => {
        const saved = { id: 9, content: 'Looks good to me' } as Comment;
        spyOn(editorService, 'createComment').and.returnValue(of(new HttpResponse({ body: saved })));

        comp.saveComment();
        tick();

        expect(comp.versionParam.comments).toContain(saved);
        expect(comp.commentForm.controls.content.value).toBe('');
        expect(comp.isWriteComment).toBe(false);
        expect(comp.isSaving).toBe(false);
      }));

      it('should reload the comment list after saving', fakeAsync(() => {
        spyOn(editorService, 'createComment').and.returnValue(of(new HttpResponse({ body: {} as Comment })));

        comp.saveComment();
        tick();

        expect(commentService.findAllByVersion).toHaveBeenCalledWith(12);
      }));

      it('should stop saving when the request fails', fakeAsync(() => {
        spyOn(editorService, 'createComment').and.returnValue(throwError(new Error('nope')));

        comp.saveComment();
        tick();

        expect(comp.isSaving).toBe(false);
        expect(comp.versionParam.comments).toHaveLength(0);
      }));
    });

    describe('loadComment', () => {
      it('should replace the comments with what the server returns', fakeAsync(() => {
        const fromServer = [{ id: 4, content: 'Server side' } as Comment];
        (commentService.findAllByVersion as jasmine.Spy).and.returnValue(of(new HttpResponse({ body: fromServer })));
        comp.ngOnInit();
        tick(500);

        comp.loadComment();

        expect(comp.comments).toBe(fromServer);
      }));
    });

    describe('parseDateTimeAgo', () => {
      it('should render a timestamp as a relative time', () => {
        expect(comp.parseDateTimeAgo(moment().subtract(2, 'hours'))).toBe('2 hours ago');
      });
    });
  });
});
