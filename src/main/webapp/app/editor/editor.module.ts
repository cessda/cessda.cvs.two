import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CvsSharedModule } from 'app/shared';
import {
  EDITOR_ROUTE,
  EditorComponent,
  EditorCvAddDialogComponent,
  EditorCvAddPopupComponent,
  EditorDetailCodeAddEditDialogComponent,
  EditorDetailCodeCsvImportDialogComponent,
  EditorDetailCodeDeleteDialogComponent,
  EditorDetailCodeReorderDialogComponent,
  EditorDetailComponent,
  EditorDetailCvAddEditDialogComponent,
  EditorDetailCvCommentDialogComponent,
  EditorDetailCvCommentItemComponent,
  EditorDetailCvDeleteDialogComponent,
  EditorDetailCvForwardStatusDialogComponent,
  EditorDetailCvNewVersionDialogComponent
} from './';
import { TagInputModule } from 'ngx-chips';
import { QuillModule } from 'ngx-quill';
import { NgxTextDiffModule } from 'ngx-text-diff';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild(EDITOR_ROUTE), TagInputModule, QuillModule, NgxTextDiffModule],
  declarations: [
    EditorComponent,
    EditorDetailComponent,
    EditorCvAddDialogComponent,
    EditorCvAddPopupComponent,
    EditorDetailCvAddEditDialogComponent,
    EditorDetailCvDeleteDialogComponent,
    EditorDetailCvForwardStatusDialogComponent,
    EditorDetailCvNewVersionDialogComponent,
    EditorDetailCvCommentDialogComponent,
    EditorDetailCvCommentItemComponent,
    EditorDetailCodeAddEditDialogComponent,
    EditorDetailCodeCsvImportDialogComponent,
    EditorDetailCodeReorderDialogComponent,
    EditorDetailCodeDeleteDialogComponent
  ],
  entryComponents: [
    EditorCvAddDialogComponent,
    EditorCvAddPopupComponent,
    EditorDetailCvAddEditDialogComponent,
    EditorDetailCvDeleteDialogComponent,
    EditorDetailCvForwardStatusDialogComponent,
    EditorDetailCvNewVersionDialogComponent,
    EditorDetailCvCommentDialogComponent,
    EditorDetailCodeAddEditDialogComponent,
    EditorDetailCodeCsvImportDialogComponent,
    EditorDetailCodeReorderDialogComponent,
    EditorDetailCodeDeleteDialogComponent
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CvsEditorModule {}
