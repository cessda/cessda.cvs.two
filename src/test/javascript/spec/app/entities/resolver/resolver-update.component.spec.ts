import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { ResolverUpdateComponent } from 'app/admin/resolver/resolver-update.component';
import { ResolverService } from 'app/admin/resolver/resolver.service';
import { Resolver } from 'app/shared/model/resolver.model';

describe('Component Tests', () => {
  describe('Resolver Management Update Component', () => {
    let comp: ResolverUpdateComponent;
    let fixture: ComponentFixture<ResolverUpdateComponent>;
    let service: ResolverService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [ResolverUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(ResolverUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ResolverUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ResolverService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Resolver(123);
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
        const entity = new Resolver();
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
