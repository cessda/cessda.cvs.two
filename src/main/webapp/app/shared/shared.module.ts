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

import {NgModule} from '@angular/core';
import {CvsSharedLibsModule} from './shared-libs.module';
import {FindLanguageFromKeyPipe} from './language/find-language-from-key.pipe';
import {VocabularyLanguageFromKeyPipe} from './language/vocabulary-language-from-key.pipe';
import {AlertComponent} from './alert/alert.component';
import {AlertErrorComponent} from './alert/alert-error.component';
import {LoginModalComponent} from './login/login.component';
import {HasAnyAuthorityDirective} from './auth/has-any-authority.directive';
import {HasAnyAgencyAuthorityDirective} from './auth/has-any-agency-authority.directive';
import {TreeComponent} from './tree/tree.component';
import {TreeEditorComponent} from './tree-editor/tree-editor.component';
import {RouteEventsService} from 'app/shared/service/route-events.service';
import {TreeReorderComponent} from 'app/shared/tree-reorder/tree-reorder.component';
import {LinkHttpPipe} from 'app/shared/pipe/link-http-pipe';
import {SafeHtmlPipe} from 'app/shared/pipe/safe-html-pipe';
import {VersionCompareComponent} from './version-compare/version-compare.component';
import {VocabularyDownloadComponent} from './vocabulary-download/vocabulary-download.component';
import {VocabularySearchResultComponent} from './vocabulary-search-result/vocabulary-search-result.component';
import {NgxTextDiffModule} from 'ngx-text-diff';
import {MetadataItemComponent} from 'app/shared/metadata-item/metadata-item.component';
import {QuillModule} from 'ngx-quill';
import {RouterModule} from '@angular/router';
import {TagInputModule} from 'ngx-chips';
import {CustomPageComponent} from 'app/shared/custom-page/custom-page.component';

@NgModule({
  imports: [CvsSharedLibsModule, NgxTextDiffModule, QuillModule, RouterModule, TagInputModule],
  declarations: [
    FindLanguageFromKeyPipe,
    VocabularyLanguageFromKeyPipe,
    LinkHttpPipe,
    SafeHtmlPipe,
    AlertComponent,
    AlertErrorComponent,
    LoginModalComponent,
    HasAnyAuthorityDirective,
    HasAnyAgencyAuthorityDirective,
    CustomPageComponent,
    MetadataItemComponent,
    TreeComponent,
    TreeEditorComponent,
    TreeReorderComponent,
    VersionCompareComponent,
    VocabularyDownloadComponent,
    VocabularySearchResultComponent
  ],
  entryComponents: [LoginModalComponent],
  exports: [
    CvsSharedLibsModule,
    FindLanguageFromKeyPipe,
    VocabularyLanguageFromKeyPipe,
    LinkHttpPipe,
    SafeHtmlPipe,
    AlertComponent,
    AlertErrorComponent,
    LoginModalComponent,
    HasAnyAuthorityDirective,
    HasAnyAgencyAuthorityDirective,
    CustomPageComponent,
    MetadataItemComponent,
    TreeComponent,
    TreeEditorComponent,
    TreeReorderComponent,
    VersionCompareComponent,
    VocabularyDownloadComponent,
    VocabularySearchResultComponent
  ],
  providers: [RouteEventsService, VocabularyLanguageFromKeyPipe]
})
export class CvsSharedModule {}
