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

import { TreeReorderComponent } from 'app/shared/tree-reorder/tree-reorder.component';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { Concept } from 'app/shared/model/concept.model';

describe('Component Tests', () => {
  describe('Tree Reorder Component', () => {
    let comp: TreeReorderComponent;
    let fixture: ComponentFixture<TreeReorderComponent>;
    let eventManager: EventManager;
    let broadcastSpy: jest.SpyInstance;

    const concepts: Concept[] = [
      { id: 1, notation: 'A', position: 1, deprecated: false },
      { id: 2, notation: 'A.1', parent: 'A', position: 2, deprecated: false, status: 'TO_BE_INSERTED' },
      { id: 3, notation: 'B', position: 3, deprecated: false, status: 'REORDER' },
      { id: 4, notation: 'C', position: 4, deprecated: true },
    ];

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [TreeReorderComponent],
        // fa-icon is not under test here
        schemas: [NO_ERRORS_SCHEMA],
      }).compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(TreeReorderComponent);
      comp = fixture.componentInstance;
      eventManager = TestBed.inject(EventManager);
      broadcastSpy = jest.spyOn(eventManager, 'broadcast');
      comp.conceptList = concepts.map(c => ({ ...c }));
    });

    afterEach(() => {
      fixture.destroy();
    });

    it('should render the current codes as a nested tree', () => {
      fixture.detectChanges();

      expect(fixture.nativeElement.querySelector('#code_A jhi-tree-reorder #code_A1')).not.toBeNull();
      expect(fixture.nativeElement.querySelector('#code_B')).not.toBeNull();
      expect(fixture.nativeElement.querySelector('#code_C')).toBeNull();
    });

    it('should render only the deprecated codes when asked to', () => {
      comp.deprecated = true;
      fixture.detectChanges();

      expect(fixture.nativeElement.querySelector('#code_C')).not.toBeNull();
      expect(fixture.nativeElement.querySelector('#code_A')).toBeNull();
    });

    it('should style each code by its reorder status', () => {
      fixture.detectChanges();

      expect(fixture.nativeElement.querySelector('#code_B .concept-row').classList).toContain('reorder');
      expect(fixture.nativeElement.querySelector('#code_A1 .concept-row').classList).toContain('to-be-inserted');
    });

    it('should broadcast a selectable code when it is clicked', () => {
      fixture.detectChanges();

      fixture.nativeElement.querySelector('#code_A > .concept-row').click();

      expect(broadcastSpy).toHaveBeenCalledWith({ name: 'selectReorderConcept', content: expect.objectContaining({ notation: 'A' }) });
    });

    it.each(['REORDER', 'UNSELECTABLE', 'PIVOT'])('should ignore a click on a code with status %s', status => {
      comp.selectConcept({ notation: 'X', status });

      expect(broadcastSpy).not.toHaveBeenCalled();
    });

    it('should highlight the code that was selected for reordering', () => {
      fixture.detectChanges();

      eventManager.broadcast(new EventWithContent('selectReorderConcept', concepts[0]));
      fixture.detectChanges();

      expect(comp.activeConceptNotation).toBe('A');
      expect(fixture.nativeElement.querySelector('#code_A > .concept-row').classList).toContain('active');
    });

    it('should stop listening once destroyed', () => {
      comp.ngOnInit();
      comp.ngOnDestroy();

      eventManager.broadcast(new EventWithContent('selectReorderConcept', concepts[0]));

      expect(comp.activeConceptNotation).toBeUndefined();
    });

    it('should not fail when destroyed before it was initialised', () => {
      expect(() => comp.ngOnDestroy()).not.toThrow();
    });

    it('should drop the codes of the current level', () => {
      expect(comp.removeCurrentLevelItems(concepts).map(c => c.notation)).toEqual(['A.1']);
      expect(comp.removeCurrentLevelItems(concepts, 'A').map(c => c.notation)).toEqual(['A', 'B', 'C']);
    });

    it('should tell whether a code has children', () => {
      expect(comp.isConceptHasChildren('A', concepts)).toBe(true);
      expect(comp.isConceptHasChildren('B', concepts)).toBe(false);
    });
  });
});
