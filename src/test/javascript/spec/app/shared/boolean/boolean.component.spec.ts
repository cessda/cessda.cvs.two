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
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BooleanComponent } from 'app/shared/boolean/boolean.component';

describe('BooleanComponent', () => {
  let fixture: ComponentFixture<BooleanComponent>;
  const span = (): HTMLElement => fixture.nativeElement.querySelector('span');

  beforeEach(() => {
    TestBed.configureTestingModule({ declarations: [BooleanComponent] });
    fixture = TestBed.createComponent(BooleanComponent);
  });

  it('should show a badge with the text matching the value', () => {
    fixture.componentRef.setInput('value', true);
    fixture.componentRef.setInput('textTrue', 'Activated');
    fixture.componentRef.setInput('textFalse', 'Deactivated');
    fixture.detectChanges();

    expect(span().className).toBe('badge badge-success');
    expect(span().textContent).toBe('Activated');

    fixture.componentRef.setInput('value', false);
    fixture.detectChanges();

    expect(span().className).toBe('badge badge-danger');
    expect(span().textContent).toBe('Deactivated');
  });

  it('should show an icon when no text is given', () => {
    fixture.componentRef.setInput('value', false);
    fixture.detectChanges();

    expect(span().className).toBe('fa fa-lg fa-times text-danger');
    expect(fixture.componentInstance.classTrue).toBe('fa fa-lg fa-check text-success');
  });

  it('should keep the classes it is given', () => {
    fixture.componentRef.setInput('value', true);
    fixture.componentRef.setInput('classTrue', 'yes');
    fixture.componentRef.setInput('classFalse', 'no');
    fixture.detectChanges();

    expect(span().className).toBe('yes');
  });
});
