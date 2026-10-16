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
import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { CvsTestModule } from '../../test.module';
import { EditorComponent } from 'app/editor/editor.component';
import { AppScope } from 'app/shared/model/enumerations/app-scope.model';

// the search result is covered by its own spec; this one only pins down the scope it is given
@Component({
  selector: 'jhi-vocabulary-search-result',
  template: '',
  standalone: false,
})
class StubSearchResultComponent {
  @Input() appScope!: AppScope;
}

describe('Component Tests', () => {
  describe('Editor Component', () => {
    let comp: EditorComponent;
    let fixture: ComponentFixture<EditorComponent>;

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [EditorComponent, StubSearchResultComponent],
      }).compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(EditorComponent);
      comp = fixture.componentInstance;
    });

    it('should search the vocabularies in the editor rather than the published ones', () => {
      expect(comp.appScope).toBe(AppScope.EDITOR);
    });

    it('should show the search results', () => {
      fixture.detectChanges();

      expect(fixture.debugElement.query(By.directive(StubSearchResultComponent))).toBeTruthy();
    });

    it('should hand that scope to the results', () => {
      fixture.detectChanges();

      const results = fixture.debugElement.query(By.directive(StubSearchResultComponent)).componentInstance;

      expect(results.appScope).toBe(AppScope.EDITOR);
    });
  });
});
