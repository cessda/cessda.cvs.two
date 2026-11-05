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
import { Concept } from 'app/shared/model/concept.model';

describe('Component Tests', () => {
  describe('Tree Component', () => {
    let comp: TreeComponent;
    let fixture: ComponentFixture<TreeComponent>;

    // a two level tree: two roots, and two children under the first of them
    const concepts: Concept[] = [
      { notation: 'A' },
      { notation: 'B' },
      { notation: 'A.1', parent: 'A' },
      { notation: 'A.2', parent: 'A' },
      { notation: 'A.1.1', parent: 'A.1' },
    ];

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [TreeComponent],
      })
        .overrideTemplate(TreeComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(TreeComponent);
      comp = fixture.componentInstance;
      comp.conceptList = concepts;
      comp.level = 0;
    });

    describe('which concepts belong under a level', () => {
      it('should leave out the concepts of the level it is rendering', () => {
        comp.parentNotation = 'A';

        expect(comp.removeCurrentLevelItems().map(c => c.notation)).toEqual(['A', 'B', 'A.1.1']);
      });

      it('should keep every concept that has a parent at the root', () => {
        comp.parentNotation = undefined;

        expect(comp.removeCurrentLevelItems().map(c => c.notation)).toEqual(['A.1', 'A.2', 'A.1.1']);
      });

      it('should keep them all when the level has no concepts of its own', () => {
        comp.parentNotation = 'B';

        expect(comp.removeCurrentLevelItems()).toHaveLength(concepts.length);
      });
    });

    describe('whether a concept has children', () => {
      it('should say so when it does', () => {
        expect(comp.isConceptHasChildren('A')).toBe(true);
      });

      it('should say so for a child that has its own', () => {
        expect(comp.isConceptHasChildren('A.1')).toBe(true);
      });

      it('should say no for a leaf', () => {
        expect(comp.isConceptHasChildren('A.2')).toBe(false);
      });

      it('should say no for a concept that is not in the list', () => {
        expect(comp.isConceptHasChildren('C')).toBe(false);
      });
    });
  });
});
