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

@NgModule({
  imports: [CvsSharedLibsModule],
  declarations: [
    FindLanguageFromKeyPipe,
    AlertComponent,
    AlertErrorComponent,
    LoginModalComponent,
    HasAnyAuthorityDirective,
    HasAnyAgencyAuthorityDirective,
    TreeComponent,
    TreeEditorComponent,
    TreeReorderComponent
  ],
  entryComponents: [LoginModalComponent],
  exports: [
    CvsSharedLibsModule,
    FindLanguageFromKeyPipe,
    AlertComponent,
    AlertErrorComponent,
    LoginModalComponent,
    HasAnyAuthorityDirective,
    HasAnyAgencyAuthorityDirective,
    TreeComponent,
    TreeEditorComponent,
    TreeReorderComponent
  ],
  providers: [RouteEventsService]
})
export class CvsSharedModule {}
