import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CvsSharedModule } from 'app/shared/shared.module';
import { VocabularyComponent } from './vocabulary.component';
import { VocabularyDetailComponent } from './vocabulary-detail.component';
import { VocabularyUpdateComponent } from './vocabulary-update.component';
import { VocabularyDeleteDialogComponent } from './vocabulary-delete-dialog.component';
import { vocabularyRoute } from './vocabulary.route';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild(vocabularyRoute)],
  declarations: [VocabularyComponent, VocabularyDetailComponent, VocabularyUpdateComponent, VocabularyDeleteDialogComponent],
  entryComponents: [VocabularyDeleteDialogComponent]
})
export class CvsVocabularyModule {}
