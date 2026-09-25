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
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { of, throwError } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { ResolverComponent } from 'app/admin/resolver/resolver.component';
import { ResolverDeleteDialogComponent } from 'app/admin/resolver/resolver-delete-dialog.component';
import { ResolverService } from 'app/admin/resolver/resolver.service';
import { Resolver } from 'app/shared/model/resolver.model';

describe('Component Tests', () => {
  describe('Resolver Component', () => {
    let comp: ResolverComponent;
    let fixture: ComponentFixture<ResolverComponent>;
    let service: ResolverService;
    let modalStub: { componentInstance: { resolver?: Resolver } };
    let modalService: { open: jasmine.Spy };

    const resolver: Resolver = {
      id: 5,
      resourceId: 'AnalysisUnit',
      resourceType: 'VOCABULARY',
      resourceUrl: 'https://vocabularies.cessda.eu/vocabulary/AnalysisUnit',
      resolverType: 'DOI',
      resolverURI: 'doi:10.5281/zenodo.1',
    };

    const page = (body: Resolver[] | null, total = '1') =>
      of(new HttpResponse({ body, headers: new HttpHeaders().append('X-Total-Count', total) }));

    beforeEach(waitForAsync(() => {
      modalStub = { componentInstance: {} };
      modalService = { open: jasmine.createSpy('open').and.returnValue(modalStub) };

      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [ResolverComponent],
        // the test module provides NgbModal as null, and this component opens a dialog
        providers: [{ provide: NgbModal, useValue: modalService }],
      })
        .overrideTemplate(ResolverComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(ResolverComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ResolverService);
    });

    it('should start with no search term when the route carries none', () => {
      expect(comp.currentSearch).toBe('');
    });

    describe('OnInit', () => {
      it('should take the paging parameters from the route', () => {
        spyOn(service, 'query').and.returnValue(page([resolver]));

        comp.ngOnInit();

        expect(comp.page).toBe(10);
        expect(comp.predicate).toBe('id');
        expect(comp.ascending).toBe(false);
      });

      it('should load the resolvers', () => {
        spyOn(service, 'query').and.returnValue(page([resolver]));

        comp.ngOnInit();

        expect(comp.resolvers).toEqual([resolver]);
      });
    });

    describe('loading a page', () => {
      it('should ask for the page the pagination is on, counted from zero', () => {
        const spy = spyOn(service, 'query').and.returnValue(page([resolver]));
        comp.predicate = 'id';
        comp.ascending = true;

        comp.loadPage(3);

        expect(spy).toHaveBeenCalledWith({ page: 2, size: comp.itemsPerPage, sort: ['id,asc'] });
      });

      it('should use the search endpoint once a term is entered', () => {
        const spy = spyOn(service, 'search').and.returnValue(page([resolver]));
        comp.predicate = 'resourceId';
        comp.ascending = true;
        comp.currentSearch = 'AnalysisUnit';

        comp.loadPage(1);

        expect(spy).toHaveBeenCalledWith({ page: 0, query: 'AnalysisUnit', size: comp.itemsPerPage, sort: ['resourceId,asc', 'id'] });
      });

      it('should not ask for the plain list while searching', () => {
        const spy = spyOn(service, 'query');
        spyOn(service, 'search').and.returnValue(page([resolver]));
        comp.currentSearch = 'AnalysisUnit';
        comp.predicate = 'id';

        comp.loadPage(1);

        expect(spy).not.toHaveBeenCalled();
      });

      it('should read the total from the count header', () => {
        spyOn(service, 'query').and.returnValue(page([resolver], '23'));
        comp.predicate = 'id';

        comp.loadPage(1);

        expect(comp.totalItems).toBe(23);
        expect(comp.ngbPaginationPage).toBe(1);
      });

      it('should hold an empty list when the server returns no body', () => {
        spyOn(service, 'query').and.returnValue(page(null));
        comp.predicate = 'id';

        comp.loadPage(1);

        expect(comp.resolvers).toEqual([]);
      });

      it('should keep the pagination on the current page when the request fails', () => {
        spyOn(service, 'query').and.returnValue(throwError(() => new Error('nope')));
        comp.predicate = 'id';
        comp.page = 7;
        comp.ngbPaginationPage = 9;

        comp.loadPage();

        expect(comp.ngbPaginationPage).toBe(7);
      });
    });

    it('should remember a search term and start again from the first page', () => {
      const spy = spyOn(service, 'search').and.returnValue(page([resolver]));
      comp.predicate = 'id';

      comp.search('AnalysisUnit');

      expect(comp.currentSearch).toBe('AnalysisUnit');
      expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ page: 0, query: 'AnalysisUnit' }));
    });

    describe('sorting', () => {
      it('should sort by the predicate and add the id as a tie breaker', () => {
        comp.predicate = 'resourceId';
        comp.ascending = true;

        expect(comp.sort()).toEqual(['resourceId,asc', 'id']);
      });

      it('should not repeat the id when it is the predicate', () => {
        comp.predicate = 'id';
        comp.ascending = false;

        expect(comp.sort()).toEqual(['id,desc']);
      });
    });

    it('should track resolvers by id', () => {
      expect(comp.trackId(0, resolver)).toBe(5);
    });

    it('should open the delete dialog on the resolver', () => {
      comp.delete(resolver);

      expect(modalService.open).toHaveBeenCalledWith(ResolverDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
      expect(modalStub.componentInstance.resolver).toBe(resolver);
    });

    it('should survive being destroyed without a subscription', () => {
      expect(() => comp.ngOnDestroy()).not.toThrow();
    });
  });
});
