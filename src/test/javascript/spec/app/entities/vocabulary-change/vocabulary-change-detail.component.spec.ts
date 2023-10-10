/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
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
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { JhiDataUtils } from 'ng-jhipster';

import { CvsTestModule } from '../../../test.module';
import { VocabularyChangeDetailComponent } from 'app/entities/vocabulary-change/vocabulary-change-detail.component';

describe('Component Tests', () => {
  describe('VocabularyChange Management Detail Component', () => {
    let comp: VocabularyChangeDetailComponent;
    let fixture: ComponentFixture<VocabularyChangeDetailComponent>;
    let dataUtils: JhiDataUtils;
    const route = { data: of({ vocabularyChange: { id: 123 } }) } as unknown as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [VocabularyChangeDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(VocabularyChangeDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(VocabularyChangeDetailComponent);
      comp = fixture.componentInstance;
      dataUtils = fixture.debugElement.injector.get(JhiDataUtils);
    });

    describe('OnInit', () => {
      it('Should load vocabularyChange on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.vocabularyChange).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
