import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { MetadataFieldUpdateComponent } from 'app/entities/metadata-field/metadata-field-update.component';
import { MetadataFieldService } from 'app/entities/metadata-field/metadata-field.service';
import { MetadataField } from 'app/shared/model/metadata-field.model';

describe('Component Tests', () => {
  describe('MetadataField Management Update Component', () => {
    let comp: MetadataFieldUpdateComponent;
    let fixture: ComponentFixture<MetadataFieldUpdateComponent>;
    let service: MetadataFieldService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [MetadataFieldUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(MetadataFieldUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(MetadataFieldUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(MetadataFieldService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new MetadataField(123);
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
        const entity = new MetadataField();
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
