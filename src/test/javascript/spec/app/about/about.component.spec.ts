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
import { AboutComponent } from 'app/about/about.component';

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
  describe('About Component', () => {
    let fixture: ComponentFixture<AboutComponent>;

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [AboutComponent, StubCustomPageComponent],
      }).compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(AboutComponent);
    });

    it('should show a custom page', () => {
      fixture.detectChanges();

      expect(fixture.debugElement.query(By.directive(StubCustomPageComponent))).toBeTruthy();
    });

    it('should ask that page for the about content', () => {
      fixture.detectChanges();

      const page = fixture.debugElement.query(By.directive(StubCustomPageComponent)).componentInstance;

      expect(page.pageType).toBe('about');
    });
  });
});
