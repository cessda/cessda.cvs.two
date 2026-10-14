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
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { SortDirective } from 'app/shared/sort/sort.directive';
import { SortByDirective } from 'app/shared/sort/sort-by.directive';

@Component({
  template: `
    <table>
      <tr jhiSort #sort="jhiSort" [(predicate)]="predicate" [(ascending)]="ascending" [callback]="loadPage.bind(this)">
        <th jhiSortBy="id" id="id">ID <i [class]="sort.iconFor('id')"></i></th>
        <th jhiSortBy="name" id="name">Name <i [class]="sort.iconFor('name')"></i></th>
      </tr>
    </table>
  `,
  standalone: false,
})
class TestHostComponent {
  predicate = 'id';
  ascending = true;
  loadPage = jest.fn();
}

describe('SortDirective and SortByDirective', () => {
  let fixture: ComponentFixture<TestHostComponent>;
  let host: TestHostComponent;
  const click = (id: string): void => {
    fixture.debugElement.query(By.css('#' + id)).nativeElement.click();
    fixture.detectChanges();
  };
  const icon = (id: string): string => fixture.debugElement.query(By.css(`#${id} i`)).nativeElement.className;

  beforeEach(() => {
    TestBed.configureTestingModule({ declarations: [SortDirective, SortByDirective, TestHostComponent] });
    fixture = TestBed.createComponent(TestHostComponent);
    host = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should show the current sort on its column only', () => {
    expect(icon('id')).toBe('sort-up');
    expect(icon('name')).toBe('sort');
  });

  it('should reverse the order when the sorted column is clicked again', () => {
    click('id');

    expect(host.predicate).toBe('id');
    expect(host.ascending).toBe(false);
    expect(icon('id')).toBe('sort-down');
    expect(host.loadPage).toHaveBeenCalledTimes(1);
  });

  it('should sort ascending by another column when it is clicked', () => {
    host.ascending = false;
    fixture.detectChanges();

    click('name');

    expect(host.predicate).toBe('name');
    expect(host.ascending).toBe(true);
    expect(icon('name')).toBe('sort-up');
    expect(icon('id')).toBe('sort');
  });

  it('should not sort a relevance ordered result', () => {
    host.predicate = '_score';
    fixture.detectChanges();

    click('name');

    expect(host.predicate).toBe('_score');
    expect(host.loadPage).not.toHaveBeenCalled();
    expect(icon('name')).toBe('sort');
  });
});
