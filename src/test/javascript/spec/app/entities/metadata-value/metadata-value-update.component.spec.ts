import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { MetadataValueUpdateComponent } from 'app/entities/metadata-value/metadata-value-update.component';
import { MetadataValueService } from 'app/entities/metadata-value/metadata-value.service';
import { MetadataValue } from 'app/shared/model/metadata-value.model';

describe('Component Tests', () => {
  describe('MetadataValue Management Update Component', () => {
    let comp: MetadataValueUpdateComponent;
    let fixture: ComponentFixture<MetadataValueUpdateComponent>;
    let service: MetadataValueService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [MetadataValueUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(MetadataValueUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(MetadataValueUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(MetadataValueService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new MetadataValue(123);
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
        const entity = new MetadataValue();
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
