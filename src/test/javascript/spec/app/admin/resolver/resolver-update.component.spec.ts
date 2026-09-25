/*
 * Copyright © 2017-2026 CESSDA ERIC (support@cessda.eu)
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
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject, of, throwError } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { ResolverUpdateComponent } from 'app/admin/resolver/resolver-update.component';
import { ResolverService } from 'app/admin/resolver/resolver.service';
import { Resolver } from 'app/shared/model/resolver.model';

describe('Component Tests', () => {
  describe('Resolver Update Component', () => {
    let comp: ResolverUpdateComponent;
    let fixture: ComponentFixture<ResolverUpdateComponent>;
    let service: ResolverService;
    let routeData: BehaviorSubject<{ resolver: Resolver }>;

    const existing: Resolver = {
      id: 5,
      resourceId: 'AnalysisUnit',
      resourceType: 'VOCABULARY',
      resourceUrl: 'https://vocabularies.cessda.eu/vocabulary/AnalysisUnit',
      resolverType: 'DOI',
      resolverURI: 'doi:10.5281/zenodo.1',
    };

    const saved = of(new HttpResponse({ body: existing }));

    beforeEach(waitForAsync(() => {
      routeData = new BehaviorSubject<{ resolver: Resolver }>({ resolver: existing });

      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [ResolverUpdateComponent],
        // the shared mock route carries paging parameters rather than a resolver
        providers: [{ provide: ActivatedRoute, useValue: { data: routeData } }],
      })
        .overrideTemplate(ResolverUpdateComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(ResolverUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ResolverService);
    });

    it('should fill the form from the resolver it is given', () => {
      comp.ngOnInit();

      expect(comp.editForm.controls.id.value).toBe(5);
      expect(comp.editForm.controls.resourceId.value).toBe('AnalysisUnit');
      expect(comp.editForm.controls.resourceType.value).toBe('VOCABULARY');
      expect(comp.editForm.controls.resolverType.value).toBe('DOI');
      expect(comp.editForm.controls.resolverURI.value).toBe('doi:10.5281/zenodo.1');
    });

    describe('validation', () => {
      it('should require the resource URL and the resolver URI', () => {
        comp.editForm.patchValue({ resourceUrl: '', resolverURI: '' });

        expect(comp.editForm.controls.resourceUrl.valid).toBe(false);
        expect(comp.editForm.controls.resolverURI.valid).toBe(false);
      });

      it('should leave the resource and resolver type optional', () => {
        comp.editForm.patchValue({ resourceType: '', resolverType: '' });

        expect(comp.editForm.controls.resourceType.valid).toBe(true);
        expect(comp.editForm.controls.resolverType.valid).toBe(true);
      });
    });

    describe('saving', () => {
      beforeEach(() => {
        comp.ngOnInit();
      });

      it('should update a resolver that already exists', () => {
        const spy = spyOn(service, 'update').and.returnValue(saved);

        comp.save();

        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ id: 5, resourceId: 'AnalysisUnit' }));
      });

      it('should create a resolver that has no id yet', () => {
        routeData.next({ resolver: { resourceUrl: 'https://vocabularies.cessda.eu', resolverURI: 'urn:cessda:1' } });
        comp.ngOnInit();
        const spy = spyOn(service, 'create').and.returnValue(saved);

        comp.save();

        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ id: undefined, resolverURI: 'urn:cessda:1' }));
      });

      it('should keep a resource type it recognises', () => {
        const spy = spyOn(service, 'update').and.returnValue(saved);

        comp.save();

        expect(spy.calls.mostRecent().args[0].resourceType).toBe('VOCABULARY');
      });

      it('should drop a resource type it does not recognise', () => {
        const spy = spyOn(service, 'update').and.returnValue(saved);
        comp.editForm.patchValue({ resourceType: 'AGENCY' });

        comp.save();

        expect(spy.calls.mostRecent().args[0].resourceType).toBeUndefined();
      });

      it('should keep either resolver type it recognises', () => {
        const spy = spyOn(service, 'update').and.returnValue(saved);

        comp.editForm.patchValue({ resolverType: 'URN' });
        comp.save();
        expect(spy.calls.mostRecent().args[0].resolverType).toBe('URN');

        comp.editForm.patchValue({ resolverType: 'DOI' });
        comp.save();
        expect(spy.calls.mostRecent().args[0].resolverType).toBe('DOI');
      });

      it('should drop a resolver type it does not recognise', () => {
        const spy = spyOn(service, 'update').and.returnValue(saved);
        comp.editForm.patchValue({ resolverType: 'HANDLE' });

        comp.save();

        expect(spy.calls.mostRecent().args[0].resolverType).toBeUndefined();
      });

      it('should leave an empty resource id off the resolver', () => {
        const spy = spyOn(service, 'update').and.returnValue(saved);
        comp.editForm.patchValue({ resourceId: '' });

        comp.save();

        expect(spy.calls.mostRecent().args[0].resourceId).toBeUndefined();
      });

      it('should stop saving and go back once the server accepts it', () => {
        spyOn(service, 'update').and.returnValue(saved);
        const back = spyOn(window.history, 'back');

        comp.save();

        expect(comp.isSaving).toBe(false);
        expect(back).toHaveBeenCalled();
      });

      it('should stay on the form when the server rejects it', () => {
        spyOn(service, 'update').and.returnValue(throwError(() => new Error('nope')));
        const back = spyOn(window.history, 'back');

        comp.save();

        expect(comp.isSaving).toBe(false);
        expect(back).not.toHaveBeenCalled();
      });
    });

    it('should go back to the previous page', () => {
      const back = spyOn(window.history, 'back');

      comp.previousState();

      expect(back).toHaveBeenCalled();
    });
  });
});
