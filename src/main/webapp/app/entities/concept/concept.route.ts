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
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IConcept, Concept } from 'app/shared/model/concept.model';
import { ConceptService } from './concept.service';
import { ConceptComponent } from './concept.component';
import { ConceptDetailComponent } from './concept-detail.component';
import { ConceptUpdateComponent } from './concept-update.component';

@Injectable({ providedIn: 'root' })
export class ConceptResolve implements Resolve<IConcept> {
  constructor(private service: ConceptService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IConcept> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((concept: HttpResponse<Concept>) => {
          if (concept.body) {
            return of(concept.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Concept());
  }
}

export const conceptRoute: Routes = [
  {
    path: '',
    component: ConceptComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'cvsApp.concept.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: ConceptDetailComponent,
    resolve: {
      concept: ConceptResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.concept.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: ConceptUpdateComponent,
    resolve: {
      concept: ConceptResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.concept.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: ConceptUpdateComponent,
    resolve: {
      concept: ConceptResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.concept.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
