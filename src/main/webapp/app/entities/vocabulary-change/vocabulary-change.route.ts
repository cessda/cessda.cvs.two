/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IVocabularyChange, VocabularyChange } from 'app/shared/model/vocabulary-change.model';
import { VocabularyChangeService } from './vocabulary-change.service';
import { VocabularyChangeComponent } from './vocabulary-change.component';
import { VocabularyChangeDetailComponent } from './vocabulary-change-detail.component';
import { VocabularyChangeUpdateComponent } from './vocabulary-change-update.component';

@Injectable({ providedIn: 'root' })
export class VocabularyChangeResolve implements Resolve<IVocabularyChange> {
  constructor(private service: VocabularyChangeService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IVocabularyChange> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((vocabularyChange: HttpResponse<VocabularyChange>) => {
          if (vocabularyChange.body) {
            return of(vocabularyChange.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new VocabularyChange());
  }
}

export const vocabularyChangeRoute: Routes = [
  {
    path: '',
    component: VocabularyChangeComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'cvsApp.vocabularyChange.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: VocabularyChangeDetailComponent,
    resolve: {
      vocabularyChange: VocabularyChangeResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.vocabularyChange.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: VocabularyChangeUpdateComponent,
    resolve: {
      vocabularyChange: VocabularyChangeResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.vocabularyChange.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: VocabularyChangeUpdateComponent,
    resolve: {
      vocabularyChange: VocabularyChangeResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.vocabularyChange.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
