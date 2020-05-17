import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ILicence, Licence } from 'app/shared/model/licence.model';
import { LicenceService } from './licence.service';
import { LicenceComponent } from './licence.component';
import { LicenceDetailComponent } from './licence-detail.component';
import { LicenceUpdateComponent } from './licence-update.component';

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
