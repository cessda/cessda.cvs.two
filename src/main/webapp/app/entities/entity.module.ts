import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'vocabulary',
        loadChildren: () => import('./vocabulary/vocabulary.module').then(m => m.CvsVocabularyModule)
      },
      {
        path: 'version',
        loadChildren: () => import('./version/version.module').then(m => m.CvsVersionModule)
      },
      {
        path: 'concept',
        loadChildren: () => import('./concept/concept.module').then(m => m.CvsConceptModule)
      },
      {
        path: 'licence',
        loadChildren: () => import('./licence/licence.module').then(m => m.CvsLicenceModule)
      },
      {
        path: 'metadata-field',
        loadChildren: () => import('./metadata-field/metadata-field.module').then(m => m.CvsMetadataFieldModule)
      },
      {
        path: 'metadata-value',
        loadChildren: () => import('./metadata-value/metadata-value.module').then(m => m.CvsMetadataValueModule)
      },
      {
        path: 'resolver',
        loadChildren: () => import('./resolver/resolver.module').then(m => m.CvsResolverModule)
      },
      {
        path: 'vocabulary-change',
        loadChildren: () => import('./vocabulary-change/vocabulary-change.module').then(m => m.CvsVocabularyChangeModule)
      },
      {
        path: 'agency',
        loadChildren: () => import('./agency/agency.module').then(m => m.CvsAgencyModule)
      },
      {
        path: 'user-agency',
        loadChildren: () => import('./user-agency/user-agency.module').then(m => m.CvsUserAgencyModule)
      },
      {
        path: 'comment',
        loadChildren: () => import('./comment/comment.module').then(m => m.CvsCommentModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class CvsEntityModule {}
