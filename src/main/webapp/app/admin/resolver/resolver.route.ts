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
import {Injectable} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {ActivatedRouteSnapshot, Resolve, Router, Routes} from '@angular/router';
import {JhiResolvePagingParams} from 'ng-jhipster';
import {EMPTY, Observable, of} from 'rxjs';
import {flatMap} from 'rxjs/operators';
import {UserRouteAccessService} from 'app/core/auth/user-route-access-service';
import {IResolver, Resolver} from 'app/shared/model/resolver.model';
import {ResolverService} from './resolver.service';
import {ResolverComponent} from './resolver.component';
import {ResolverDetailComponent} from './resolver-detail.component';
import {ResolverUpdateComponent} from './resolver-update.component';

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
