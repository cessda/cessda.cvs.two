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
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { UntypedFormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { VocabularyChangeUpdateComponent } from 'app/entities/vocabulary-change/vocabulary-change-update.component';
import { VocabularyChangeService } from 'app/entities/vocabulary-change/vocabulary-change.service';
import { VocabularyChange } from 'app/shared/model/vocabulary-change.model';

describe('Component Tests', () => {
  describe('VocabularyChange Management Update Component', () => {
    let comp: VocabularyChangeUpdateComponent;
    let fixture: ComponentFixture<VocabularyChangeUpdateComponent>;
    let service: VocabularyChangeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [VocabularyChangeUpdateComponent],
        providers: [UntypedFormBuilder],
      })
        .overrideTemplate(VocabularyChangeUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(VocabularyChangeUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(VocabularyChangeService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity: VocabularyChange = { id: 123, changeType: '' };
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity: VocabularyChange = { changeType: '' };
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
