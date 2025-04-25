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
import { MetadataValue } from 'app/shared/model/metadata-value.model';
import { MetadataValueService } from './metadata-value.service';
import { MetadataValueComponent } from './metadata-value.component';
import { MetadataValueDetailComponent } from './metadata-value-detail.component';
import { MetadataValueUpdateComponent } from './metadata-value-update.component';

@Injectable({ providedIn: 'root' })
export class MetadataValueResolve implements Resolve<MetadataValue> {
  constructor(
    private service: MetadataValueService,
    private router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<MetadataValue> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((metadataValue: HttpResponse<MetadataValue>) => {
          if (metadataValue.body) {
            return of(metadataValue.body);
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

export const metadataValueRoute: Routes = [
  {
    path: '',
    component: MetadataValueComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams,
    },
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'cvsApp.metadataValue.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MetadataValueDetailComponent,
    resolve: {
      metadataValue: MetadataValueResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.metadataValue.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MetadataValueUpdateComponent,
    resolve: {
      metadataValue: MetadataValueResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.metadataValue.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MetadataValueUpdateComponent,
    resolve: {
      metadataValue: MetadataValueResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.metadataValue.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
