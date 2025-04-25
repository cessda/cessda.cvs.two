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
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CvsSharedModule } from 'app/shared';
import { EditorCvAddDialogComponent, EditorCvAddPopupComponent } from './editor-cv-add-dialog.component';
import { EditorDetailCvAddEditConfirmModalComponent } from './editor-detail-code-add-edit-confirm.component';
import { EditorDetailCodeAddEditDialogComponent } from './editor-detail-code-add-edit-dialog.component';
import { EditorDetailCodeCsvImportDialogComponent } from './editor-detail-code-csv-import-dialog.component';
import { EditorDetailCodeDeleteDialogComponent } from './editor-detail-code-delete-dialog.component';
import { EditorDetailCodeDeprecateDialogComponent } from './editor-detail-code-deprecate-dialog.component';
import { EditorDetailCodeReorderDialogComponent } from './editor-detail-code-reorder-dialog.component';
import { EditorDetailCvAddEditDialogComponent } from './editor-detail-cv-add-edit-dialog.component';
import { EditorDetailCvCommentDialogComponent } from './editor-detail-cv-comment-dialog.component';
import { EditorDetailCvCommentItemComponent } from './editor-detail-cv-comment-item.component';
import { EditorDetailCvDeleteDialogComponent } from './editor-detail-cv-delete-dialog.component';
import { EditorDetailCvForwardStatusDialogComponent } from './editor-detail-cv-forward-status-dialog.component';
import { EditorDetailCvNewVersionDialogComponent } from './editor-detail-cv-new-version-dialog.component';
import { EditorDetailComponent } from './editor-detail.component';
import { EditorComponent } from './editor.component';
import { EDITOR_ROUTE } from './editor.route';
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
    EditorDetailCodeDeprecateDialogComponent,
    EditorDetailCodeDeleteDialogComponent,
    EditorDetailCvAddEditConfirmModalComponent,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class CvsEditorModule {}
