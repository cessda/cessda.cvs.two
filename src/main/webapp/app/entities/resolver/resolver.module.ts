import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CvsSharedModule } from 'app/shared/shared.module';
import { ResolverComponent } from './resolver.component';
import { ResolverDetailComponent } from './resolver-detail.component';
import { ResolverUpdateComponent } from './resolver-update.component';
import { ResolverDeleteDialogComponent } from './resolver-delete-dialog.component';
import { resolverRoute } from './resolver.route';

@NgModule({
  imports: [CvsSharedModule, RouterModule.forChild(resolverRoute)],
  declarations: [ResolverComponent, ResolverDetailComponent, ResolverUpdateComponent, ResolverDeleteDialogComponent],
  entryComponents: [ResolverDeleteDialogComponent]
})
export class CvsResolverModule {}
