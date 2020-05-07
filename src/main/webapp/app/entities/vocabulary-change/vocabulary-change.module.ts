import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CvsSharedModule } from 'app/shared/shared.module';
import { VocabularyChangeComponent } from './vocabulary-change.component';
import { VocabularyChangeDetailComponent } from './vocabulary-change-detail.component';
import { VocabularyChangeUpdateComponent } from './vocabulary-change-update.component';
import { VocabularyChangeDeleteDialogComponent } from './vocabulary-change-delete-dialog.component';
import { vocabularyChangeRoute } from './vocabulary-change.route';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild(vocabularyChangeRoute)],
  declarations: [
    VocabularyChangeComponent,
    VocabularyChangeDetailComponent,
    VocabularyChangeUpdateComponent,
    VocabularyChangeDeleteDialogComponent
  ],
  entryComponents: [VocabularyChangeDeleteDialogComponent]
})
export class CvsVocabularyChangeModule {}
