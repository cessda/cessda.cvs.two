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
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TreeEditorComponent } from 'app/shared/tree-editor/tree-editor.component';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { Concept } from 'app/shared/model/concept.model';

describe('Component Tests', () => {
  describe('Tree Editor Component', () => {
    let comp: TreeEditorComponent;
    let fixture: ComponentFixture<TreeEditorComponent>;
    let eventManager: EventManager;

    const concepts: Concept[] = [
      { id: 1, notation: 'A', title: 'Alpha', definition: 'First root', position: 1 },
      { id: 2, notation: 'A.1', parent: 'A', title: 'Alpha one', position: 2 },
      { id: 3, notation: 'B', title: 'Beta', position: 3 },
      { id: 4, notation: 'C', title: 'Gamma', position: 4, deprecated: true },
    ];

    const rowTexts = (): string[] =>
      Array.from<HTMLElement>(fixture.nativeElement.querySelectorAll('.concept-row .column1')).map(el => el.textContent!.trim());

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [TreeEditorComponent],
        // fa-icon is not under test here
        schemas: [NO_ERRORS_SCHEMA],
      }).compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(TreeEditorComponent);
      comp = fixture.componentInstance;
      eventManager = TestBed.inject(EventManager);
      comp.conceptList = concepts.map(c => ({ ...c }));
    });

    afterEach(() => {
      fixture.destroy();
    });

    it('should render every current code once, with children nested below their parent', () => {
      fixture.detectChanges();

      expect(rowTexts()).toEqual(['1 A', '2 A.1', '3 B']);
      const nested = fixture.nativeElement.querySelector('#code_A jhi-tree-editor');
      expect(nested.querySelector('#code_A1')).not.toBeNull();
    });

    it('should leave deprecated codes out', () => {
      fixture.detectChanges();

      expect(fixture.nativeElement.querySelector('#code_C')).toBeNull();
    });

    it('should broadcast the code that is clicked', () => {
      const broadcastSpy = jest.spyOn(eventManager, 'broadcast');
      fixture.detectChanges();

      fixture.nativeElement.querySelector('#code_B .concept-row').click();

      expect(broadcastSpy).toHaveBeenCalledWith({ name: 'selectConcept', content: expect.objectContaining({ notation: 'B' }) });
    });

    it('should highlight the selected code and clear it again on deselection', () => {
      fixture.detectChanges();

      eventManager.broadcast(new EventWithContent('selectConcept', concepts[2]));
      fixture.detectChanges();

      expect(comp.activeConceptNotation).toBe('B');
      expect(fixture.nativeElement.querySelector('#code_B .concept-row').classList).toContain('active');

      eventManager.broadcast('deselectConcept');
      fixture.detectChanges();

      expect(comp.activeConceptNotation).toBeUndefined();
      expect(fixture.nativeElement.querySelector('.concept-row.active')).toBeNull();
    });

    it('should stop listening once destroyed', () => {
      comp.ngOnInit();
      comp.ngOnDestroy();

      eventManager.broadcast(new EventWithContent('selectConcept', concepts[0]));

      expect(comp.activeConceptNotation).toBeUndefined();
    });

    it('should not fail when destroyed before it was initialised', () => {
      expect(() => comp.ngOnDestroy()).not.toThrow();
    });

    it('should drop the codes of the current level', () => {
      expect(comp.removeCurrentLevelItems(concepts, undefined).map(c => c.notation)).toEqual(['A.1']);
      expect(comp.removeCurrentLevelItems(concepts, 'A').map(c => c.notation)).toEqual(['A', 'B', 'C']);
    });

    it('should tell whether a code has children', () => {
      expect(comp.isConceptHasChildren('A', concepts)).toBe(true);
      expect(comp.isConceptHasChildren('B', concepts)).toBe(false);
    });
  });
});
