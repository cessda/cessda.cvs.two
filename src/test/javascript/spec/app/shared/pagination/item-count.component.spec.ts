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
import { TranslateModule, TranslateService } from '@ngx-translate/core';

import { ItemCountComponent } from 'app/shared/pagination/item-count.component';
import { TranslateDirective } from 'app/shared/language/translate.directive';

describe('ItemCountComponent', () => {
  let fixture: ComponentFixture<ItemCountComponent>;
  let component: ItemCountComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [TranslateModule.forRoot()], declarations: [ItemCountComponent, TranslateDirective] });
    const translateService = TestBed.inject(TranslateService);
    translateService.setTranslation('en', { global: { 'item-count': 'Showing {{first}} - {{second}} of {{total}} items.' } });
    translateService.use('en');
    fixture = TestBed.createComponent(ItemCountComponent);
    component = fixture.componentInstance;
  });

  it('should count the items of a full page', () => {
    component.page = 2;
    component.itemsPerPage = 20;
    component.total = 95;
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toBe('Showing 21 - 40 of 95 items.');
  });

  it('should stop at the total on the last page', () => {
    component.page = 5;
    component.itemsPerPage = 20;
    component.total = 95;

    expect(component.i18nValues()).toEqual({ first: 81, second: 95, total: 95 });
  });

  it('should start counting at one on the first page', () => {
    component.page = 1;
    component.itemsPerPage = 20;
    component.total = 5;

    expect(component.i18nValues()).toEqual({ first: 1, second: 5, total: 5 });
  });
});
