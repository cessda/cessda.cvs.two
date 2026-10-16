/*
 * Copyright © 2017-2026 CESSDA ERIC (support@cessda.eu)
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
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { CvsTestModule } from '../../../test.module';
import { TreeComponent } from 'app/shared/tree/tree.component';
import { TreeDeprecatedComponent } from 'app/shared/tree/tree-deprecated.component';
import { Concept } from 'app/shared/model/concept.model';

describe('Component Tests', () => {
  describe('Tree Deprecated Component', () => {
    let comp: TreeDeprecatedComponent;
    let fixture: ComponentFixture<TreeDeprecatedComponent>;

    // the deprecated tree is shown beside the ordinary one and walks the same concepts
    const concepts: Concept[] = [{ notation: 'A' }, { notation: 'A.1', parent: 'A', deprecated: true }, { notation: 'A.2', parent: 'A' }];

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [TreeDeprecatedComponent],
      })
        .overrideTemplate(TreeDeprecatedComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(TreeDeprecatedComponent);
      comp = fixture.componentInstance;
      comp.conceptList = concepts;
      comp.level = 0;
    });

    it('should be a tree of its own, rendered from its own template', () => {
      expect(comp).toBeInstanceOf(TreeComponent);
      expect(comp.constructor).not.toBe(TreeComponent);
    });

    it('should walk the levels the way the ordinary tree does', () => {
      comp.parentNotation = 'A';

      expect(comp.removeCurrentLevelItems().map(c => c.notation)).toEqual(['A']);
    });

    it('should find the children of a concept the way the ordinary tree does', () => {
      expect(comp.isConceptHasChildren('A')).toBe(true);
      expect(comp.isConceptHasChildren('A.1')).toBe(false);
    });
  });
});
