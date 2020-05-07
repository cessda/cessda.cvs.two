import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { VersionUpdateComponent } from 'app/entities/version/version-update.component';
import { VersionService } from 'app/entities/version/version.service';
import { Version } from 'app/shared/model/version.model';

describe('Component Tests', () => {
  describe('Version Management Update Component', () => {
    let comp: VersionUpdateComponent;
    let fixture: ComponentFixture<VersionUpdateComponent>;
    let service: VersionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [VersionUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(VersionUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(VersionUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(VersionService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Version(123);
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
        const entity = new Version();
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
