import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IMetadataField, MetadataField } from 'app/shared/model/metadata-field.model';
import { MetadataFieldService } from './metadata-field.service';
import { MetadataFieldComponent } from './metadata-field.component';
import { MetadataFieldDetailComponent } from './metadata-field-detail.component';
import { MetadataFieldUpdateComponent } from './metadata-field-update.component';

@Injectable({ providedIn: 'root' })
export class MetadataFieldResolve implements Resolve<IMetadataField> {
  constructor(private service: MetadataFieldService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMetadataField> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((metadataField: HttpResponse<MetadataField>) => {
          if (metadataField.body) {
            return of(metadataField.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new MetadataField());
  }
}

export const metadataFieldRoute: Routes = [
  {
    path: '',
    component: MetadataFieldComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'cvsApp.metadataField.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: MetadataFieldDetailComponent,
    resolve: {
      metadataField: MetadataFieldResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.metadataField.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: MetadataFieldUpdateComponent,
    resolve: {
      metadataField: MetadataFieldResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.metadataField.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: MetadataFieldUpdateComponent,
    resolve: {
      metadataField: MetadataFieldResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.metadataField.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
