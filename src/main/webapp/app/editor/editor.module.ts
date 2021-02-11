/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

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
