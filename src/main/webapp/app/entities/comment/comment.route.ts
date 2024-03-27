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
import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Comment } from 'app/shared/model/comment.model';
import { CommentService } from './comment.service';
import { CommentComponent } from './comment.component';
import { CommentDetailComponent } from './comment-detail.component';
import { CommentUpdateComponent } from './comment-update.component';

@Injectable({ providedIn: 'root' })
export class CommentResolve implements Resolve<Comment> {
  constructor(
    private service: CommentService,
    private router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Comment> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((comment: HttpResponse<Comment>) => {
          if (comment.body) {
            return of(comment.body);
          } else {
            this.router.navigate(['404'], { skipLocationChange: true });
            return EMPTY;
          }
        }),
      );
    }
    return of({});
  }
}

export const commentRoute: Routes = [
  {
    path: '',
    component: CommentComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams,
    },
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'cvsApp.comment.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CommentDetailComponent,
    resolve: {
      comment: CommentResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.comment.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CommentUpdateComponent,
    resolve: {
      comment: CommentResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.comment.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CommentUpdateComponent,
    resolve: {
      comment: CommentResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.comment.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
