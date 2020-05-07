import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { VocabularyUpdateComponent } from 'app/entities/vocabulary/vocabulary-update.component';
import { VocabularyService } from 'app/entities/vocabulary/vocabulary.service';
import { Vocabulary } from 'app/shared/model/vocabulary.model';

describe('Component Tests', () => {
  describe('Vocabulary Management Update Component', () => {
    let comp: VocabularyUpdateComponent;
    let fixture: ComponentFixture<VocabularyUpdateComponent>;
    let service: VocabularyService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [VocabularyUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(VocabularyUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(VocabularyUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(VocabularyService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Vocabulary(123);
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
        const entity = new Vocabulary();
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
