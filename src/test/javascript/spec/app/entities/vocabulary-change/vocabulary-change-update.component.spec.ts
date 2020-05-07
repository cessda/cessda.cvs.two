import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
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
        providers: [FormBuilder]
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
        const entity = new VocabularyChange(123);
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
        const entity = new VocabularyChange();
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
