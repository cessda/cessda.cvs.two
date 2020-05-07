import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { IConcept } from 'app/shared/model/concept.model';
import VocabularyUtil from 'app/shared/util/vocabulary-util';
import { JhiEventManager, JhiEventWithContent } from 'ng-jhipster';
import { Subscription } from 'rxjs';

@Component({
  selector: 'jhi-tree-reorder',
  templateUrl: './tree-reorder.component.html',
  styleUrls: ['./tree-reorder.component.scss']
})
export class TreeReorderComponent implements OnInit, OnDestroy {
  @Input() parentNotation?: string;
  @Input() conceptList?: IConcept[];
  @Input() level?: number;

  eventSubscriber?: Subscription;
  activeConceptNotation?: string;

  constructor(protected eventManager: JhiEventManager) {}

  removeCurrentLevelItems: any = (conceptList?: IConcept[], parentNotation?: string, level?: number) => {
    return conceptList!.filter(c => c.parent !== parentNotation);
  };

  isConceptHasChildren(notation?: string, conceptList?: IConcept[]): boolean {
    return VocabularyUtil.isConceptHasChildren(notation!, conceptList!);
  }

  selectConcept(concept: IConcept): void {
    if (concept.status !== 'REORDER') {
      this.eventManager.broadcast({ name: 'selectReorderConcept', content: concept });
    }
  }

  ngOnInit(): void {
    this.eventSubscriber = this.eventManager.subscribe('selectReorderConcept', (response: JhiEventWithContent<IConcept>) => {
      this.activeConceptNotation = response.content.notation;
    });
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventSubscriber.unsubscribe();
    }
  }
}
