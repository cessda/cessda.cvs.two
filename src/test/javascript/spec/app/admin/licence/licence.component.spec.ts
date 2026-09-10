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
import { LicenceComponent } from 'app/admin/licence/licence.component';
import { LicenceDeleteDialogComponent } from 'app/admin/licence/licence-delete-dialog.component';
import { LicenceService } from 'app/admin/licence/licence.service';
import { Licence } from 'app/shared/model/licence.model';

describe('Component Tests', () => {
  describe('Licence Component', () => {
    let comp: LicenceComponent;
    let fixture: ComponentFixture<LicenceComponent>;
    let service: LicenceService;
    let modalStub: { componentInstance: { licence?: Licence } };
    let modalService: { open: jasmine.Spy };

    const licence: Licence = { id: 2, name: 'Creative Commons Attribution 4.0', abbr: 'CC BY 4.0' };

    const page = (body: Licence[] | null, total = '1') =>
      of(new HttpResponse({ body, headers: new HttpHeaders().append('X-Total-Count', total) }));

    beforeEach(waitForAsync(() => {
      modalStub = { componentInstance: {} };
      modalService = { open: jasmine.createSpy('open').and.returnValue(modalStub) };

      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [LicenceComponent],
        // the test module provides NgbModal as null, and this component opens a dialog
        providers: [{ provide: NgbModal, useValue: modalService }],
      })
        .overrideTemplate(LicenceComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(LicenceComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(LicenceService);
    });

    it('should start with no search term when the route carries none', () => {
      expect(comp.currentSearch).toBe('');
    });

    describe('OnInit', () => {
      it('should take the paging parameters from the route', () => {
        spyOn(service, 'query').and.returnValue(page([licence]));

        comp.ngOnInit();

        expect(comp.page).toBe(10);
        expect(comp.predicate).toBe('id');
        expect(comp.ascending).toBe(false);
      });

      it('should load the licences', () => {
        spyOn(service, 'query').and.returnValue(page([licence]));

        comp.ngOnInit();

        expect(comp.licences).toEqual([licence]);
      });
    });

    describe('loading a page', () => {
      it('should ask for the page the pagination is on, counted from zero', () => {
        const spy = spyOn(service, 'query').and.returnValue(page([licence]));
        comp.predicate = 'id';
        comp.ascending = true;

        comp.loadPage(3);

        expect(spy).toHaveBeenCalledWith({ page: 2, size: comp.itemsPerPage, sort: ['id,asc'] });
      });

      it('should use the search endpoint once a term is entered', () => {
        const spy = spyOn(service, 'search').and.returnValue(page([licence]));
        comp.predicate = 'name';
        comp.ascending = true;
        comp.currentSearch = 'attribution';

        comp.loadPage(1);

        expect(spy).toHaveBeenCalledWith({ page: 0, query: 'attribution', size: comp.itemsPerPage, sort: ['name,asc', 'id'] });
      });

      it('should not ask for the plain list while searching', () => {
        const spy = spyOn(service, 'query');
        spyOn(service, 'search').and.returnValue(page([licence]));
        comp.currentSearch = 'attribution';
        comp.predicate = 'id';

        comp.loadPage(1);

        expect(spy).not.toHaveBeenCalled();
      });

      it('should fall back to the current page when given none', () => {
        const spy = spyOn(service, 'query').and.returnValue(page([licence]));
        comp.page = 4;
        comp.predicate = 'id';

        comp.loadPage();

        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ page: 3 }));
      });

      it('should read the total from the count header', () => {
        spyOn(service, 'query').and.returnValue(page([licence], '17'));
        comp.predicate = 'id';

        comp.loadPage(1);

        expect(comp.totalItems).toBe(17);
        expect(comp.ngbPaginationPage).toBe(1);
      });

      it('should hold an empty list when the server returns no body', () => {
        spyOn(service, 'query').and.returnValue(page(null));
        comp.predicate = 'id';

        comp.loadPage(1);

        expect(comp.licences).toEqual([]);
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
      const spy = spyOn(service, 'search').and.returnValue(page([licence]));
      comp.predicate = 'id';

      comp.search('attribution');

      expect(comp.currentSearch).toBe('attribution');
      expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ page: 0, query: 'attribution' }));
    });

    describe('sorting', () => {
      it('should sort by the predicate and add the id as a tie breaker', () => {
        comp.predicate = 'name';
        comp.ascending = true;

        expect(comp.sort()).toEqual(['name,asc', 'id']);
      });

      it('should not repeat the id when it is the predicate', () => {
        comp.predicate = 'id';
        comp.ascending = false;

        expect(comp.sort()).toEqual(['id,desc']);
      });
    });

    it('should track licences by id', () => {
      expect(comp.trackId(0, licence)).toBe(2);
    });

    it('should open the delete dialog on the licence', () => {
      comp.delete(licence);

      expect(modalService.open).toHaveBeenCalledWith(LicenceDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
      expect(modalStub.componentInstance.licence).toBe(licence);
    });

    it('should survive being destroyed without a subscription', () => {
      expect(() => comp.ngOnDestroy()).not.toThrow();
    });
  });
});
