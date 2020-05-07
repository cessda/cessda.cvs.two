import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { UserAgencyUpdateComponent } from 'app/entities/user-agency/user-agency-update.component';
import { UserAgencyService } from 'app/entities/user-agency/user-agency.service';
import { UserAgency } from 'app/shared/model/user-agency.model';

describe('Component Tests', () => {
  describe('UserAgency Management Update Component', () => {
    let comp: UserAgencyUpdateComponent;
    let fixture: ComponentFixture<UserAgencyUpdateComponent>;
    let service: UserAgencyService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [UserAgencyUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(UserAgencyUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(UserAgencyUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(UserAgencyService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new UserAgency(123);
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
        const entity = new UserAgency();
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
