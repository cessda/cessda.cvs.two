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
import { Component, Input } from '@angular/core';
import { Concept } from 'app/shared/model/concept.model';
import VocabularyUtil from 'app/shared/util/vocabulary-util';

@Component({
  selector: 'jhi-tree',
  templateUrl: './tree.component.html',
  styleUrls: ['./tree.component.scss'],
})
export class TreeComponent {
  @Input() parentNotation?: string;
  @Input() conceptList?: Concept[];
  @Input() level?: number;

  removeCurrentLevelItems: any = (conceptList?: Concept[], parentNotation?: string, level?: number) => {
    return conceptList!.filter(c => c.parent !== parentNotation);
  };

  isConceptHasChildren(notation?: string, conceptList?: Concept[]): boolean {
    return VocabularyUtil.isConceptHasChildren(notation!, conceptList!);
  }
}
