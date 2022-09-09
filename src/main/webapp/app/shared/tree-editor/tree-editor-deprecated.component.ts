/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { IConcept } from 'app/shared/model/concept.model';
import VocabularyUtil from 'app/shared/util/vocabulary-util';
import { JhiEventManager, JhiEventWithContent } from 'ng-jhipster';
import { Subscription } from 'rxjs';

@Component({
  selector: 'jhi-tree-editor-deprecated',
  templateUrl: './tree-editor-deprecated.component.html',
  styleUrls: ['./tree-editor.component.scss']
})
export class TreeEditorDeprecatedComponent implements OnInit, OnDestroy {
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
