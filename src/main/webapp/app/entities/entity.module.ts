import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Authority } from 'app/shared/constants/authority.constants';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'entity/vocabulary',
        data: {
          authorities: [Authority.ADMIN]
        },
        loadChildren: () => import('./vocabulary/vocabulary.module').then(m => m.CvsVocabularyModule)
      },
      {
        path: 'entity/version',
        data: {
          authorities: [Authority.ADMIN]
        },
        loadChildren: () => import('./version/version.module').then(m => m.CvsVersionModule)
      },
      {
        path: 'entity/concept',
        data: {
          authorities: [Authority.ADMIN]
        },
        loadChildren: () => import('./concept/concept.module').then(m => m.CvsConceptModule)
      },
      {
        path: 'entity/metadata-field',
        data: {
          authorities: [Authority.ADMIN]
        },
        loadChildren: () => import('./metadata-field/metadata-field.module').then(m => m.CvsMetadataFieldModule)
      },
      {
        path: 'entity/metadata-value',
        data: {
          authorities: [Authority.ADMIN]
        },
        loadChildren: () => import('./metadata-value/metadata-value.module').then(m => m.CvsMetadataValueModule)
      },
      {
        path: 'entity/vocabulary-change',
        data: {
          authorities: [Authority.ADMIN]
        },
        loadChildren: () => import('./vocabulary-change/vocabulary-change.module').then(m => m.CvsVocabularyChangeModule)
      },
      {
        path: 'entity/user-agency',
        data: {
          authorities: [Authority.ADMIN]
        },
        loadChildren: () => import('./user-agency/user-agency.module').then(m => m.CvsUserAgencyModule)
      },
      {
        path: 'entity/comment',
        data: {
          authorities: [Authority.ADMIN]
        },
        loadChildren: () => import('./comment/comment.module').then(m => m.CvsCommentModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class CvsEntityModule {}
