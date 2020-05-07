import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IVersion, Version } from 'app/shared/model/version.model';
import { VersionService } from './version.service';
import { VersionComponent } from './version.component';
import { VersionDetailComponent } from './version-detail.component';
import { VersionUpdateComponent } from './version-update.component';

@Injectable({ providedIn: 'root' })
export class VersionResolve implements Resolve<IVersion> {
  constructor(private service: VersionService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IVersion> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((version: HttpResponse<Version>) => {
          if (version.body) {
            return of(version.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Version());
  }
}

export const versionRoute: Routes = [
  {
    path: '',
    component: VersionComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'cvsApp.version.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: VersionDetailComponent,
    resolve: {
      version: VersionResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.version.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: VersionUpdateComponent,
    resolve: {
      version: VersionResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.version.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: VersionUpdateComponent,
    resolve: {
      version: VersionResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.version.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
