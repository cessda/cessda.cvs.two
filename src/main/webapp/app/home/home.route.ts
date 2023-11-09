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
import { HomeComponent } from './home.component';
import { Vocabulary } from 'app/shared/model/vocabulary.model';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { HomeService } from 'app/home/home.service';
import { HomeDetailComponent } from 'app/home/home-detail.component';
import { mergeMap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class VocabularyResolve implements Resolve<Vocabulary> {
  constructor(
    private service: HomeService,
    private router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Vocabulary> {
    let notation = route.params['notation'];

    if (typeof notation !== 'string') {
      return EMPTY;
    }

    const codeIndex = notation.indexOf('_');
    let code = '';
    if (codeIndex > 0) {
      code = notation
        .substring(codeIndex + 1)
        .split('.')
        .join('');
      notation = notation.substring(0, codeIndex);
    }
    let version = '';
    let lang = '';
    if (route.queryParams.v) version = route.queryParams.v;
    if (route.queryParams.lang) lang = route.queryParams.lang;

    const queryString: Record<string, string> = {};
    if (version !== '') {
      queryString.v = version;
    }
    return this.service.getVocabulary(notation, queryString).pipe(
      mergeMap((vocabulary: HttpResponse<Vocabulary>) => {
        if (vocabulary.body) {
          vocabulary.body.selectedLang = lang !== '' ? lang : vocabulary.body.sourceLanguage;
          if (code !== '') {
            vocabulary.body.selectedCode = code;
          }
          return of(vocabulary.body);
        } else {
          this.router.navigate(['404'], { skipLocationChange: true });
          return EMPTY;
        }
      }),
    );
  }
}

export const HOME_ROUTE: Routes = [
  {
    path: '',
    component: HomeComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams,
    },
    data: {
      authorities: [],
      pageTitle: 'home.title',
    },
  },
  {
    path: 'vocabulary/:notation',
    component: HomeDetailComponent,
    resolve: {
      vocabulary: VocabularyResolve,
    },
    data: {
      pageTitle: 'home.title',
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: 'vocabulary/:notation/:version',
    component: HomeDetailComponent,
    resolve: {
      vocabulary: VocabularyResolve,
    },
    data: {
      pageTitle: 'home.title',
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: 'vocabulary/:notation/:version/:lang',
    component: HomeDetailComponent,
    resolve: {
      vocabulary: VocabularyResolve,
    },
    data: {
      pageTitle: 'home.title',
    },
    runGuardsAndResolvers: 'always',
  },
];
