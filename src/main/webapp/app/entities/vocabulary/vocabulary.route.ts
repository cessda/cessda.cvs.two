import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IVocabulary, Vocabulary } from 'app/shared/model/vocabulary.model';
import { VocabularyService } from './vocabulary.service';
import { VocabularyComponent } from './vocabulary.component';
import { VocabularyDetailComponent } from './vocabulary-detail.component';
import { VocabularyUpdateComponent } from './vocabulary-update.component';

@Injectable({ providedIn: 'root' })
export class VocabularyResolve implements Resolve<IVocabulary> {
  constructor(private service: VocabularyService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IVocabulary> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((vocabulary: HttpResponse<Vocabulary>) => {
          if (vocabulary.body) {
            return of(vocabulary.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Vocabulary());
  }
}

export const vocabularyRoute: Routes = [
  {
    path: '',
    component: VocabularyComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'cvsApp.vocabulary.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: VocabularyDetailComponent,
    resolve: {
      vocabulary: VocabularyResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.vocabulary.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: VocabularyUpdateComponent,
    resolve: {
      vocabulary: VocabularyResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.vocabulary.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: VocabularyUpdateComponent,
    resolve: {
      vocabulary: VocabularyResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'cvsApp.vocabulary.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
