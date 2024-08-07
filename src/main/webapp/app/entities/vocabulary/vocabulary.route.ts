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
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { createNewVocabulary, Vocabulary } from 'app/shared/model/vocabulary.model';
import { VocabularyService } from './vocabulary.service';
import { VocabularyComponent } from './vocabulary.component';
import { VocabularyDetailComponent } from './vocabulary-detail.component';
import { VocabularyUpdateComponent } from './vocabulary-update.component';

@Injectable({ providedIn: 'root' })
export class VocabularyResolve implements Resolve<Vocabulary> {
  constructor(
    private service: VocabularyService,
    private router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Vocabulary> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap(vocabulary => {
          if (vocabulary.body) {
            return of(vocabulary.body);
          } else {
            this.router.navigate(['404'], { skipLocationChange: true });
            return EMPTY;
          }
        }),
      );
    }
    return of(createNewVocabulary());
  }
}

export const vocabularyRoute: Routes = [
  {
    path: '',
    component: VocabularyComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams,
    },
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'cvsApp.vocabulary.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: VocabularyDetailComponent,
    resolve: {
      vocabulary: VocabularyResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.vocabulary.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: VocabularyUpdateComponent,
    resolve: {
      vocabulary: VocabularyResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.vocabulary.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: VocabularyUpdateComponent,
    resolve: {
      vocabulary: VocabularyResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.vocabulary.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
