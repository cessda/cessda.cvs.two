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
import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Agency, createNewAgency } from 'app/shared/model/agency.model';
import { AgencyService } from './agency.service';
import { AgencyComponent } from './agency.component';
import { AgencyDetailPopupComponent } from './agency-detail-dialog.component';
import { AgencyUpdateComponent } from './agency-update.component';

@Injectable({ providedIn: 'root' })
export class AgencyResolve implements Resolve<Agency> {
  constructor(
    private service: AgencyService,
    private router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Agency> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap(agency => {
          if (agency.body) {
            return of(agency.body);
          } else {
            this.router.navigate(['404'], { skipLocationChange: true });
            return EMPTY;
          }
        }),
      );
    }
    return of(createNewAgency());
  }
}

export const agencyRoute: Routes = [
  {
    path: '',
    component: AgencyComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams,
    },
    data: {
      defaultSort: 'id,asc',
      pageTitle: 'cvsApp.agency.home.title',
    },
  },
  {
    path: ':id/view',
    component: AgencyDetailPopupComponent,
    resolve: {
      agency: AgencyResolve,
    },
    data: {
      authorities: [Authority.ADMIN, Authority.ADMIN_CONTENT],
      pageTitle: 'cvsApp.agency.home.title',
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup',
  },
  {
    path: 'new',
    component: AgencyUpdateComponent,
    resolve: {
      agency: AgencyResolve,
    },
    data: {
      authorities: [Authority.ADMIN, Authority.ADMIN_CONTENT],
      pageTitle: 'cvsApp.agency.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AgencyUpdateComponent,
    resolve: {
      agency: AgencyResolve,
    },
    data: {
      authorities: [Authority.ADMIN, Authority.ADMIN_CONTENT],
      pageTitle: 'cvsApp.agency.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
