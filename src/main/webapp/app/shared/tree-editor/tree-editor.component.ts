import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { IConcept } from 'app/shared/model/concept.model';
import VocabularyUtil from 'app/shared/util/vocabulary-util';
import { JhiEventManager, JhiEventWithContent } from 'ng-jhipster';
import { Subscription } from 'rxjs';

@Component({
  selector: 'jhi-tree-editor',
  templateUrl: './tree-editor.component.html',
  styleUrls: ['./tree-editor.component.scss']
})
export class TreeEditorComponent implements OnInit, OnDestroy {
  @Input() parentNotation?: string;
  @Input() conceptList?: IConcept[];
  @Input() level?: number;

  eventSubscriber?: Subscription;
  eventSubscriber2?: Subscription;
  activeConceptNotation?: string;

  constructor(protected eventManager: JhiEventManager) {}

  removeCurrentLevelItems: any = (conceptList?: IConcept[], parentNotation?: string, level?: number) => {
    return conceptList!.filter(c => c.parent !== parentNotation);
  };

  isConceptHasChildren(notation?: string, conceptList?: IConcept[]): boolean {
    return VocabularyUtil.isConceptHasChildren(notation!, conceptList!);
  }

  selectConcept(concept: IConcept): void {
    this.eventManager.broadcast({ name: 'selectConcept', content: concept });
  }

  ngOnInit(): void {
    this.eventSubscriber = this.eventManager.subscribe('selectConcept', (response: JhiEventWithContent<IConcept>) => {
      this.activeConceptNotation = response.content.notation;
    });
    this.eventSubscriber2 = this.eventManager.subscribe('deselectConcept', () => {
      this.activeConceptNotation = undefined;
    });
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventSubscriber.unsubscribe();
    }
    if (this.eventSubscriber2) {
      this.eventSubscriber2.unsubscribe();
    }
  }
}
