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
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Authority } from 'app/shared/constants/authority.constants';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'entity/vocabulary',
        data: {
          authorities: [Authority.ADMIN, Authority.ADMIN_CONTENT],
        },
        loadChildren: () => import('./vocabulary/vocabulary.module').then(m => m.CvsVocabularyModule),
      },
      {
        path: 'entity/version',
        data: {
          authorities: [Authority.ADMIN, Authority.ADMIN_CONTENT],
        },
        loadChildren: () => import('./version/version.module').then(m => m.CvsVersionModule),
      },
      {
        path: 'entity/concept',
        data: {
          authorities: [Authority.ADMIN, Authority.ADMIN_CONTENT],
        },
        loadChildren: () => import('./concept/concept.module').then(m => m.CvsConceptModule),
      },
      {
        path: 'entity/metadata-field',
        data: {
          authorities: [Authority.ADMIN, Authority.ADMIN_CONTENT],
        },
        loadChildren: () => import('./metadata-field/metadata-field.module').then(m => m.CvsMetadataFieldModule),
      },
      {
        path: 'entity/metadata-value',
        data: {
          authorities: [Authority.ADMIN, Authority.ADMIN_CONTENT],
        },
        loadChildren: () => import('./metadata-value/metadata-value.module').then(m => m.CvsMetadataValueModule),
      },
      {
        path: 'entity/vocabulary-change',
        data: {
          authorities: [Authority.ADMIN, Authority.ADMIN_CONTENT],
        },
        loadChildren: () => import('./vocabulary-change/vocabulary-change.module').then(m => m.CvsVocabularyChangeModule),
      },
      {
        path: 'entity/user-agency',
        data: {
          authorities: [Authority.ADMIN, Authority.ADMIN_CONTENT],
        },
        loadChildren: () => import('./user-agency/user-agency.module').then(m => m.CvsUserAgencyModule),
      },
      {
        path: 'entity/comment',
        data: {
          authorities: [Authority.ADMIN, Authority.ADMIN_CONTENT],
        },
        loadChildren: () => import('./comment/comment.module').then(m => m.CvsCommentModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class CvsEntityModule {}
