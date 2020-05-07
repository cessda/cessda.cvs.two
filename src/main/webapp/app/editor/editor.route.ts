import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { EditorComponent } from './editor.component';
import { IVocabulary, Vocabulary } from 'app/shared/model/vocabulary.model';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { EditorService } from 'app/editor/editor.service';
import { EditorDetailComponent } from 'app/editor/editor-detail.component';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { EditorCvAddPopupComponent } from 'app/editor/editor-cv-add-dialog.component';

@Injectable({ providedIn: 'root' })
export class VocabularyResolve implements Resolve<IVocabulary> {
  constructor(private service: EditorService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IVocabulary> | Observable<never> {
    const notation = route.params['notation'];
    const version = route.queryParams['v'];
    if (notation) {
      return this.service.getVocabulary(notation).pipe(
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

export const EDITOR_ROUTE: Routes = [
  {
    path: '',
    component: EditorComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: [],
      pageTitle: 'home.title'
    }
  },
  {
    path: 'cv-add',
    component: EditorCvAddPopupComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'global.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'vocabulary/:notation',
    component: EditorDetailComponent,
    resolve: {
      vocabulary: VocabularyResolve
    },
    data: {
      pageTitle: 'home.title'
    },
    runGuardsAndResolvers: 'always'
    // children: [
    //   {
    //     path: 'detail-cv-add',
    //     component: EditorDetailCvAddPopupComponent,
    //     // resolve: {
    //     //   vocabulary: VocabularyResolve
    //     // },
    //     data: {
    //       authorities: ['ROLE_USER'],
    //       pageTitle: 'global.title'
    //     },
    //     canActivate: [UserRouteAccessService],
    //     outlet: 'popup'
    //   }
    // ]
  }
];
