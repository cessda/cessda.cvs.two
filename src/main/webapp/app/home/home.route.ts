import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';

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
    let notation = route.params['notation'];
    const codeIndex = (notation as string).indexOf('_');
    let code = '';
    if (codeIndex > 0) {
      code = (notation as string)
        .substring(codeIndex + 1)
        .split('.')
        .join('');
      notation = (notation as string).substring(0, codeIndex);
    }
    let version = route.queryParams['version'];
    let lang = '';
    if (route.params['version']) version = route.params['version'];
    if (route.params['lang']) lang = route.params['lang'];
    if (notation) {
      return this.service
        .getVocabularyFile(notation, {
          v: version,
          rand: new Date().getTime()
        })
        .pipe(
          flatMap((vocabulary: HttpResponse<Vocabulary>) => {
            if (vocabulary.body) {
              vocabulary.body.selectedLang = lang !== '' ? lang : vocabulary.body.sourceLanguage;
              if (code !== '') {
                vocabulary.body.selectedCode = code;
              }
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
  },
  {
    path: 'vocabulary/:notation/:version',
    component: HomeDetailComponent,
    resolve: {
      vocabulary: VocabularyResolve
    },
    data: {
      pageTitle: 'home.title'
    },
    runGuardsAndResolvers: 'always'
  },
  {
    path: 'vocabulary/:notation/:version/:lang',
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
