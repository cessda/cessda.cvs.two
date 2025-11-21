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
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { CvsSharedModule } from 'app/shared/shared.module';
import { CvsCoreModule } from 'app/core/core.module';
import { CvsAppRoutingModule } from './app-routing.module';
import { CvsHomeModule } from './home/home.module';
import { CvsEditorModule } from './editor/editor.module';
import { CvsAboutModule } from 'app/about/about.module';
import { CvsApiDocsModule } from 'app/api-docs/api-docs.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ActiveMenuDirective } from './layouts/navbar/active-menu.directive';
import { ErrorComponent } from './layouts/error/error.component';
// ngx-chips
import { TagInputModule } from 'ngx-chips';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
// ngx-quill rich text
import { QuillModule } from 'ngx-quill';
// ngx-text-diff
import { ScrollingModule } from '@angular/cdk/scrolling';
import { NgxTextDiffModule } from 'ngx-text-diff';
import { NgxChartsModule } from '@swimlane/ngx-charts';

import { HttpClient } from '@angular/common/http';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader, TRANSLATE_HTTP_LOADER_CONFIG } from '@ngx-translate/http-loader';

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader();
}

@NgModule({
  imports: [
    BrowserModule,
    CvsSharedModule,
    CvsCoreModule,
    CvsHomeModule,
    CvsEditorModule,
    CvsAboutModule,
    CvsApiDocsModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    CvsAppRoutingModule,
    TagInputModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    QuillModule.forRoot(),
    ScrollingModule,
    NgxTextDiffModule,
    NgxChartsModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient, TRANSLATE_HTTP_LOADER_CONFIG],
      },
    }),
  ],
  providers: [
    {
      provide: TRANSLATE_HTTP_LOADER_CONFIG,
      useValue: {
        prefix: '/i18n/',
        suffix: '.json',
      },
    },
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, ActiveMenuDirective, FooterComponent],
  exports: [],
  bootstrap: [MainComponent],
})
export class CvsAppModule {}
