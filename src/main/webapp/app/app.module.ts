import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { CvsSharedModule } from 'app/shared/shared.module';
import { CvsCoreModule } from 'app/core/core.module';
import { CvsAppRoutingModule } from './app-routing.module';
import { CvsHomeModule } from './home/home.module';
import { CvsEditorModule } from './editor/editor.module';
import { CvsEntityModule } from './entities/entity.module';
import { CvsAboutModule } from 'app/about/about.module';
import { CvsUserGuideModule } from 'app/user-guide/user-guide.module';
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

@NgModule({
  imports: [
    BrowserModule,
    CvsSharedModule,
    CvsCoreModule,
    CvsHomeModule,
    CvsEditorModule,
    CvsAboutModule,
    CvsUserGuideModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    CvsEntityModule,
    CvsAppRoutingModule,
    TagInputModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    QuillModule.forRoot(),
    ScrollingModule,
    NgxTextDiffModule
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, ActiveMenuDirective, FooterComponent],
  exports: [],
  bootstrap: [MainComponent]
})
export class CvsAppModule {}
