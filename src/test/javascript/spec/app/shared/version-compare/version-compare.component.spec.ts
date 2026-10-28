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
import { HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { DiffContent } from 'ngx-text-diff/lib/ngx-text-diff.model';

import { VersionCompareComponent } from 'app/shared/version-compare/version-compare.component';
import { HomeService } from 'app/home/home.service';
import { EditorService } from 'app/editor/editor.service';
import { EventManager } from 'app/core/util/event-manager.service';

describe('Component Tests', () => {
  describe('Version Compare Component', () => {
    let comp: VersionCompareComponent;
    let fixture: ComponentFixture<VersionCompareComponent>;
    let eventManager: EventManager;
    let emitted: DiffContent[];

    const homeService = { getVocabularyCompare: jest.fn() };
    const editorService = { getVocabularyCompare: jest.fn() };

    const header = (): HTMLElement => fixture.nativeElement.querySelector('a.acc-header');

    beforeEach(waitForAsync(() => {
      homeService.getVocabularyCompare.mockReset().mockReturnValue(of(new HttpResponse({ body: ['published text', 'newer text'] })));
      editorService.getVocabularyCompare.mockReset().mockReturnValue(of(new HttpResponse({ body: ['published text', 'draft text'] })));
      TestBed.configureTestingModule({
        declarations: [VersionCompareComponent],
        providers: [
          { provide: HomeService, useValue: homeService },
          { provide: EditorService, useValue: editorService },
        ],
        // the diff viewer, the collapse directive and fa-icon are not under test here
        schemas: [NO_ERRORS_SCHEMA],
      }).compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(VersionCompareComponent);
      comp = fixture.componentInstance;
      eventManager = TestBed.inject(EventManager);
      comp.notation = 'AnalysisUnit';
      comp.langVersion1 = 'en-2.0.0';
      comp.langVersion2 = 'en-1.0.0';
      comp.dataSource = 'json';
      emitted = [];
      comp.contentObservable$.subscribe(content => emitted.push(content));
    });

    afterEach(() => {
      fixture.destroy();
    });

    it('should start closed without loading anything', () => {
      fixture.detectChanges();

      expect(comp.isOpen).toBe(false);
      expect(header().getAttribute('aria-expanded')).toBe('false');
      expect(homeService.getVocabularyCompare).not.toHaveBeenCalled();
    });

    it.each(['json', 'JSON'])('should compare two published versions when the data source is %s', dataSource => {
      comp.dataSource = dataSource;

      comp.toggleCompareShow();

      expect(comp.isOpen).toBe(true);
      expect(homeService.getVocabularyCompare).toHaveBeenCalledWith('AnalysisUnit', 'en-2.0.0', 'en-1.0.0');
      expect(editorService.getVocabularyCompare).not.toHaveBeenCalled();
      expect(emitted).toEqual([{ leftContent: 'published text', rightContent: 'newer text' }]);
    });

    it('should compare the draft with its previous version when reading from the database', () => {
      comp.dataSource = 'DB';
      comp.langVersion1 = '42';

      comp.toggleCompareShow();

      expect(editorService.getVocabularyCompare).toHaveBeenCalledWith(42);
      expect(homeService.getVocabularyCompare).not.toHaveBeenCalled();
      expect(emitted).toEqual([{ leftContent: 'published text', rightContent: 'draft text' }]);
    });

    it('should not load again when the comparison is closed', () => {
      comp.toggleCompareShow();
      comp.toggleCompareShow();

      expect(comp.isOpen).toBe(false);
      expect(homeService.getVocabularyCompare).toHaveBeenCalledTimes(1);
    });

    it('should open when the header is clicked', () => {
      fixture.detectChanges();

      header().click();
      fixture.detectChanges();

      expect(header().getAttribute('aria-expanded')).toBe('true');
      expect(emitted).toHaveLength(1);
    });

    it('should name both versions in the header of a published comparison', () => {
      fixture.detectChanges();

      expect(header().textContent).toContain('Compare previous version en-1.0.0 with version en-2.0.0');
    });

    it('should describe a database comparison as one against the unpublished version', () => {
      comp.dataSource = 'DB';
      fixture.detectChanges();

      expect(header().textContent).toContain('Compare previous version with current unpublished version');
    });

    it('should close when another part of the page asks for it', () => {
      fixture.detectChanges();
      comp.toggleCompareShow();

      eventManager.broadcast('closeComparison');

      expect(comp.isOpen).toBe(false);
    });

    it('should stop listening once destroyed', () => {
      fixture.detectChanges();
      comp.toggleCompareShow();

      comp.ngOnDestroy();
      eventManager.broadcast('closeComparison');

      expect(comp.isOpen).toBe(true);
    });

    it('should not fail when destroyed before it was initialised', () => {
      expect(() => comp.ngOnDestroy()).not.toThrow();
    });
  });
});
