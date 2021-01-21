import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import './vendor';
import {CvsSharedModule} from 'app/shared/shared.module';
import {CvsCoreModule} from 'app/core/core.module';
import {CvsAppRoutingModule} from './app-routing.module';
import {CvsHomeModule} from './home/home.module';
import {CvsEditorModule} from './editor/editor.module';
import {CvsEntityModule} from './entities/entity.module';
import {CvsAboutModule} from 'app/about/about.module';
import {CvsApiDocsModule} from 'app/api-docs/api-docs.module';
import {CvsUserGuideModule} from 'app/user-guide/user-guide.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import {MainComponent} from './layouts/main/main.component';
import {NavbarComponent} from './layouts/navbar/navbar.component';
import {FooterComponent} from './layouts/footer/footer.component';
import {PageRibbonComponent} from './layouts/profiles/page-ribbon.component';
import {ActiveMenuDirective} from './layouts/navbar/active-menu.directive';
import {ErrorComponent} from './layouts/error/error.component';
// ngx-chips
import {TagInputModule} from 'ngx-chips';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
// ngx-quill rich text
import {QuillConfig, QuillModule} from 'ngx-quill';
// @ts-ignore
import * as Quill from 'quill';
// @ts-ignore
import QuillBetterTable from 'quill-better-table';
// @ts-ignore
import BlotFormatter from 'quill-blot-formatter';
// ngx-text-diff
import {ScrollingModule} from '@angular/cdk/scrolling';
import {NgxTextDiffModule} from 'ngx-text-diff';
import {NgxChartsModule} from '@swimlane/ngx-charts';

Quill.register(
  {
    'modules/better-table': QuillBetterTable,
    'modules/blotFormatter': BlotFormatter
  },
  true
);

const quillConfig: QuillConfig = {
  modules: {
    table: false, // disable table module
    'better-table': {
      operationMenu: {
        items: {
          unmergeCells: {
            text: 'Another unmerge cells name'
          }
        },
        color: {
          colors: ['green', 'red', 'yellow', 'blue', '#ededed', 'grey', 'white'],
          text: 'Background Colors:'
        }
      }
    },
    keyboard: {
      bindings: QuillBetterTable.keyboardBindings
    },
    blotFormatter: {}
  }
};

@NgModule({
  imports: [
    BrowserModule,
    CvsSharedModule,
    CvsCoreModule,
    CvsHomeModule,
    CvsEditorModule,
    CvsAboutModule,
    CvsApiDocsModule,
    CvsUserGuideModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    CvsEntityModule,
    CvsAppRoutingModule,
    TagInputModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    QuillModule.forRoot(quillConfig),
    ScrollingModule,
    NgxTextDiffModule,
    NgxChartsModule
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, ActiveMenuDirective, FooterComponent],
  exports: [],
  bootstrap: [MainComponent]
})
export class CvsAppModule {}
