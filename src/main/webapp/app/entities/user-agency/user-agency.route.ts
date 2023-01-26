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
import { IUserAgency, UserAgency } from 'app/shared/model/user-agency.model';
import { UserAgencyService } from './user-agency.service';
import { UserAgencyComponent } from './user-agency.component';
import { UserAgencyDetailComponent } from './user-agency-detail.component';
import { UserAgencyUpdateComponent } from './user-agency-update.component';

@Injectable({ providedIn: 'root' })
export class UserAgencyResolve implements Resolve<IUserAgency> {
  constructor(private service: UserAgencyService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUserAgency> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((userAgency: HttpResponse<UserAgency>) => {
          if (userAgency.body) {
            return of(userAgency.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new UserAgency());
  }
}

export const userAgencyRoute: Routes = [
  {
    path: '',
    component: UserAgencyComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'cvsApp.userAgency.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: UserAgencyDetailComponent,
    resolve: {
      userAgency: UserAgencyResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.userAgency.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: UserAgencyUpdateComponent,
    resolve: {
      userAgency: UserAgencyResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.userAgency.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: UserAgencyUpdateComponent,
    resolve: {
      userAgency: UserAgencyResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.userAgency.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
