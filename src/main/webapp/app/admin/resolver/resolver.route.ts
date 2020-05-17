import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IResolver, Resolver } from 'app/shared/model/resolver.model';
import { ResolverService } from './resolver.service';
import { ResolverComponent } from './resolver.component';
import { ResolverDetailComponent } from './resolver-detail.component';
import { ResolverUpdateComponent } from './resolver-update.component';

@Injectable({ providedIn: 'root' })
export class ResolverResolve implements Resolve<IResolver> {
  constructor(private service: ResolverService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IResolver> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((resolver: HttpResponse<Resolver>) => {
          if (resolver.body) {
            return of(resolver.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Resolver());
  }
}

export const resolverRoute: Routes = [
  {
    path: '',
    component: ResolverComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      defaultSort: 'id,asc',
      pageTitle: 'cvsApp.resolver.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: ResolverDetailComponent,
    resolve: {
      resolver: ResolverResolve
    },
    data: {
      pageTitle: 'cvsApp.resolver.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: ResolverUpdateComponent,
    resolve: {
      resolver: ResolverResolve
    },
    data: {
      pageTitle: 'cvsApp.resolver.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: ResolverUpdateComponent,
    resolve: {
      resolver: ResolverResolve
    },
    data: {
      pageTitle: 'cvsApp.resolver.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
