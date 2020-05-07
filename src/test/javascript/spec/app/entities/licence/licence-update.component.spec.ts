import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { LicenceUpdateComponent } from 'app/entities/licence/licence-update.component';
import { LicenceService } from 'app/entities/licence/licence.service';
import { Licence } from 'app/shared/model/licence.model';

describe('Component Tests', () => {
  describe('Licence Management Update Component', () => {
    let comp: LicenceUpdateComponent;
    let fixture: ComponentFixture<LicenceUpdateComponent>;
    let service: LicenceService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [LicenceUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(LicenceUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(LicenceUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(LicenceService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Licence(123);
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
        const entity = new Licence();
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
