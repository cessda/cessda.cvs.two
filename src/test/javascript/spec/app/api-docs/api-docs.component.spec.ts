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
import { ApiDocsComponent } from 'app/api-docs/api-docs.component';

// the page itself is covered by its own spec; this one only pins down what it is asked to show
@Component({
  selector: 'jhi-custom-page',
  template: '',
  standalone: false,
})
class StubCustomPageComponent {
  @Input() pageType!: string;
}

describe('Component Tests', () => {
  describe('Api Docs Component', () => {
    let fixture: ComponentFixture<ApiDocsComponent>;

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [ApiDocsComponent, StubCustomPageComponent],
      }).compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(ApiDocsComponent);
    });

    it('should show a custom page', () => {
      fixture.detectChanges();

      expect(fixture.debugElement.query(By.directive(StubCustomPageComponent))).toBeTruthy();
    });

    it('should ask that page for the API documentation', () => {
      fixture.detectChanges();

      const page = fixture.debugElement.query(By.directive(StubCustomPageComponent)).componentInstance;

      expect(page.pageType).toBe('api-docs');
    });
  });
});
