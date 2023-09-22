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
import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { flatMap, mergeMap } from 'rxjs/operators';

import { EditorComponent } from './editor.component';
import { IVocabulary, Vocabulary } from 'app/shared/model/vocabulary.model';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { EditorService } from 'app/editor/editor.service';
import { EditorDetailComponent } from 'app/editor/editor-detail.component';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { EditorCvAddPopupComponent } from 'app/editor/editor-cv-add-dialog.component';

@Injectable({ providedIn: 'root' })
export class VocabularyResolve implements Resolve<IVocabulary> {
  constructor(
    private service: EditorService,
    private router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IVocabulary> | Observable<never> {
    const notation = route.params['notation'];
    if (notation) {
      return this.service.getVocabulary(notation).pipe(
        mergeMap((vocabulary: HttpResponse<Vocabulary>) => {
          if (vocabulary.body) {
            vocabulary.body.selectedLang = vocabulary.body.sourceLanguage;
            return of(vocabulary.body);
          } else {
            this.router.navigate(['404'], { skipLocationChange: true });
            return EMPTY;
          }
        }),
      );
    }
    return of(new Vocabulary());
  }
}

export const EDITOR_ROUTE: Routes = [
  {
    path: '',
    component: EditorComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams,
    },
    data: {
      authorities: [],
      pageTitle: 'home.title',
    },
  },
  {
    path: 'cv-add',
    component: EditorCvAddPopupComponent,
    data: {
      authorities: ['ROLE_USER', 'ROLE_ADMIN', 'ROLE_ADMIN_CONTENT'],
      pageTitle: 'global.title',
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup',
  },
  {
    path: 'vocabulary/:notation',
    component: EditorDetailComponent,
    resolve: {
      vocabulary: VocabularyResolve,
    },
    data: {
      pageTitle: 'home.title',
    },
    runGuardsAndResolvers: 'always',
  },
];
