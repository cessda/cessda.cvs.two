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
import { Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
import { Concept } from 'app/shared/model/concept.model';
import VocabularyUtil from 'app/shared/util/vocabulary-util';
import { JhiEventManager, JhiEventWithContent } from 'ng-jhipster';
import { Subscription } from 'rxjs';

@Component({
  selector: 'jhi-tree-reorder',
  templateUrl: './tree-reorder.component.html',
  styleUrls: ['./tree-reorder.component.scss'],
  standalone: false,
})
export class TreeReorderComponent implements OnInit, OnDestroy {
  protected eventManager = inject(JhiEventManager);

  @Input() parentNotation?: string;
  @Input({ required: true }) conceptList!: Concept[];
  @Input() level: number = 0;
  @Input() deprecated: boolean = false;

  eventSubscriber?: Subscription;
  activeConceptNotation?: string;

  removeCurrentLevelItems(conceptList: Concept[], parentNotation?: string) {
    return conceptList.filter(c => c.parent !== parentNotation);
  }

  isConceptHasChildren(notation: string, conceptList: Concept[]): boolean {
    return VocabularyUtil.isConceptHasChildren(notation, conceptList);
  }

  selectConcept(concept: Concept): void {
    if (concept.status !== 'REORDER' && concept.status !== 'UNSELECTABLE' && concept.status !== 'PIVOT') {
      this.eventManager.broadcast({ name: 'selectReorderConcept', content: concept });
    }
  }

  ngOnInit(): void {
    this.eventSubscriber = this.eventManager.subscribe('selectReorderConcept', (response: JhiEventWithContent<Concept>) => {
      this.activeConceptNotation = response.content.notation;
    });
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventSubscriber.unsubscribe();
    }
  }
}
