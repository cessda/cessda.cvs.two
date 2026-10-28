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

import { TreeEditorDeprecatedComponent } from 'app/shared/tree-editor/tree-editor-deprecated.component';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { Concept } from 'app/shared/model/concept.model';

describe('Component Tests', () => {
  describe('Tree Editor Deprecated Component', () => {
    let comp: TreeEditorDeprecatedComponent;
    let fixture: ComponentFixture<TreeEditorDeprecatedComponent>;
    let eventManager: EventManager;

    const concepts: Concept[] = [
      { id: 1, notation: 'A', title: 'Alpha', position: 1 },
      {
        id: 2,
        notation: 'D',
        title: 'Delta',
        position: 2,
        deprecated: true,
        uri: 'https://vocabularies.cessda.eu/D',
        replacedByNotation: 'A',
      },
      { id: 3, notation: 'D.1', parent: 'D', title: 'Delta one', position: 3, deprecated: true },
    ];

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [TreeEditorDeprecatedComponent],
        // fa-icon is not under test here
        schemas: [NO_ERRORS_SCHEMA],
      }).compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(TreeEditorDeprecatedComponent);
      comp = fixture.componentInstance;
      eventManager = TestBed.inject(EventManager);
      comp.conceptList = concepts.map(c => ({ ...c }));
    });

    afterEach(() => {
      fixture.destroy();
    });

    it('should only render deprecated codes, nesting their children', () => {
      fixture.detectChanges();

      expect(fixture.nativeElement.querySelector('#code_A')).toBeNull();
      expect(fixture.nativeElement.querySelector('#code_D jhi-tree-editor-deprecated #code_D1')).not.toBeNull();
    });

    it('should link to the uri of a deprecated code and name its replacement', () => {
      fixture.detectChanges();

      const row: HTMLElement = fixture.nativeElement.querySelector('#code_D .concept-row');
      expect(row.querySelector('a')?.getAttribute('href')).toBe('https://vocabularies.cessda.eu/D');
      const columns = Array.from<HTMLElement>(row.querySelectorAll('.column2')).map(el => el.textContent!.trim());
      expect(columns).toContain('A');
    });

    it('should broadcast the selected code', () => {
      const broadcastSpy = jest.spyOn(eventManager, 'broadcast');

      comp.selectConcept(concepts[1]);

      expect(broadcastSpy).toHaveBeenCalledWith({ name: 'selectConcept', content: concepts[1] });
    });

    it('should follow selection and deselection of codes', () => {
      fixture.detectChanges();

      eventManager.broadcast(new EventWithContent('selectConcept', concepts[1]));
      fixture.detectChanges();

      expect(comp.activeConceptNotation).toBe('D');
      expect(fixture.nativeElement.querySelector('#code_D .concept-row').classList).toContain('active');

      eventManager.broadcast('deselectConcept');

      expect(comp.activeConceptNotation).toBeUndefined();
    });

    it('should stop listening once destroyed', () => {
      comp.ngOnInit();
      comp.ngOnDestroy();

      eventManager.broadcast(new EventWithContent('selectConcept', concepts[1]));

      expect(comp.activeConceptNotation).toBeUndefined();
    });

    it('should not fail when destroyed before it was initialised', () => {
      expect(() => comp.ngOnDestroy()).not.toThrow();
    });

    it('should drop the codes of the given level from its own list', () => {
      expect(comp.removeCurrentLevelItems(undefined).map(c => c.notation)).toEqual(['D.1']);
      expect(comp.removeCurrentLevelItems('D').map(c => c.notation)).toEqual(['A', 'D']);
    });

    it('should tell whether a code has children', () => {
      expect(comp.isConceptHasChildren('D', concepts)).toBe(true);
      expect(comp.isConceptHasChildren('A', concepts)).toBe(false);
    });
  });
});
