import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { HomeComponent } from './home.component';
import { IVocabulary, Vocabulary } from 'app/shared/model/vocabulary.model';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { HomeService } from 'app/home/home.service';
import { HomeDetailComponent } from 'app/home/home-detail.component';

@Injectable({ providedIn: 'root' })
export class VocabularyResolve implements Resolve<IVocabulary> {
  constructor(private service: HomeService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IVocabulary> | Observable<never> {
    const notation = route.params['notation'];
    const version = route.queryParams['v'];
    if (notation) {
      return this.service
        .getVocabularyFile(notation, {
          v: version
        })
        .pipe(
          flatMap((vocabulary: HttpResponse<Vocabulary>) => {
            if (vocabulary.body) {
              vocabulary.body.selectedLang = vocabulary.body.sourceLanguage;
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

export const HOME_ROUTE: Routes = [
  {
    path: '',
    component: HomeComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: [],
      pageTitle: 'home.title'
    }
  },
  {
    path: 'vocabulary/:notation',
    component: HomeDetailComponent,
    resolve: {
      vocabulary: VocabularyResolve
    },
    data: {
      pageTitle: 'home.title'
    },
    runGuardsAndResolvers: 'always'
  }
];
