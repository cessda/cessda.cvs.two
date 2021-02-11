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

import {Injectable} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {ActivatedRouteSnapshot, Resolve, Router, Routes} from '@angular/router';
import {JhiResolvePagingParams} from 'ng-jhipster';
import {EMPTY, Observable, of} from 'rxjs';
import {flatMap} from 'rxjs/operators';
import {UserRouteAccessService} from 'app/core/auth/user-route-access-service';
import {ILicence, Licence} from 'app/shared/model/licence.model';
import {LicenceService} from './licence.service';
import {LicenceComponent} from './licence.component';
import {LicenceDetailComponent} from './licence-detail.component';
import {LicenceUpdateComponent} from './licence-update.component';

@Injectable({ providedIn: 'root' })
export class LicenceResolve implements Resolve<ILicence> {
  constructor(private service: LicenceService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILicence> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((licence: HttpResponse<Licence>) => {
          if (licence.body) {
            return of(licence.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Licence());
  }
}

export const licenceRoute: Routes = [
  {
    path: '',
    component: LicenceComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      defaultSort: 'id,asc',
      pageTitle: 'cvsApp.licence.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: LicenceDetailComponent,
    resolve: {
      licence: LicenceResolve
    },
    data: {
      pageTitle: 'cvsApp.licence.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: LicenceUpdateComponent,
    resolve: {
      licence: LicenceResolve
    },
    data: {
      pageTitle: 'cvsApp.licence.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: LicenceUpdateComponent,
    resolve: {
      licence: LicenceResolve
    },
    data: {
      pageTitle: 'cvsApp.licence.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
