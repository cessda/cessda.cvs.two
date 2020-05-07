import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IResolver } from 'app/shared/model/resolver.model';
import { ResolverService } from './resolver.service';

@Component({
  templateUrl: './resolver-delete-dialog.component.html'
})
export class ResolverDeleteDialogComponent {
  resolver?: IResolver;

  constructor(protected resolverService: ResolverService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.resolverService.delete(id).subscribe(() => {
      this.eventManager.broadcast('resolverListModification');
      this.activeModal.close();
    });
  }
}
