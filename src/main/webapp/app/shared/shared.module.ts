import { NgModule } from '@angular/core';
import { CvsSharedLibsModule } from './shared-libs.module';
import { FindLanguageFromKeyPipe } from './language/find-language-from-key.pipe';
import { AlertComponent } from './alert/alert.component';
import { AlertErrorComponent } from './alert/alert-error.component';
import { LoginModalComponent } from './login/login.component';
import { HasAnyAuthorityDirective } from './auth/has-any-authority.directive';
import { HasAnyAgencyAuthorityDirective } from './auth/has-any-agency-authority.directive';
import { TreeComponent } from './tree/tree.component';
import { TreeEditorComponent } from './tree-editor/tree-editor.component';
import { RouteEventsService } from 'app/shared/service/route-events.service';
import { TreeReorderComponent } from 'app/shared/tree-reorder/tree-reorder.component';
import { LinkHttpPipe } from 'app/shared/pipe/link-http-pipe';
import { VersionCompareComponent } from './version-compare/version-compare.component';
import { NgxTextDiffModule } from 'ngx-text-diff';
import { MetadataItemComponent } from 'app/shared/metadata-item/metadata-item.component';
import { QuillModule } from 'ngx-quill';

@NgModule({
  imports: [CvsSharedLibsModule, NgxTextDiffModule, QuillModule],
  declarations: [
    FindLanguageFromKeyPipe,
    LinkHttpPipe,
    AlertComponent,
    AlertErrorComponent,
    LoginModalComponent,
    HasAnyAuthorityDirective,
    HasAnyAgencyAuthorityDirective,
    MetadataItemComponent,
    TreeComponent,
    TreeEditorComponent,
    TreeReorderComponent,
    VersionCompareComponent
  ],
  entryComponents: [LoginModalComponent],
  exports: [
    CvsSharedLibsModule,
    FindLanguageFromKeyPipe,
    LinkHttpPipe,
    AlertComponent,
    AlertErrorComponent,
    LoginModalComponent,
    HasAnyAuthorityDirective,
    HasAnyAgencyAuthorityDirective,
    MetadataItemComponent,
    TreeComponent,
    TreeEditorComponent,
    TreeReorderComponent,
    VersionCompareComponent
  ],
  providers: [RouteEventsService]
})
export class CvsSharedModule {}
