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

import { CvsTestModule } from '../../test.module';
import { AgencyComponent } from 'app/agency/agency.component';
import { AgencyDeleteDialogComponent } from 'app/agency/agency-delete-dialog.component';
import { AgencyService } from 'app/agency/agency.service';
import { Agency, createNewAgency } from 'app/shared/model/agency.model';

describe('Component Tests', () => {
  describe('Agency Component', () => {
    let comp: AgencyComponent;
    let fixture: ComponentFixture<AgencyComponent>;
    let service: AgencyService;
    let modalStub: { componentInstance: { agency?: Agency } };
    let modalService: { open: jasmine.Spy };

    const agency = createNewAgency({ id: 4, name: 'CESSDA', link: 'https://www.cessda.eu' });

    const page = (body: Agency[] | null, total = '1') =>
      of(new HttpResponse({ body, headers: new HttpHeaders().append('X-Total-Count', total) }));

    beforeEach(waitForAsync(() => {
      modalStub = { componentInstance: {} };
      modalService = { open: jasmine.createSpy('open').and.returnValue(modalStub) };

      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [AgencyComponent],
        // the test module provides NgbModal as null, and this component opens a dialog
        providers: [{ provide: NgbModal, useValue: modalService }],
      })
        .overrideTemplate(AgencyComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(AgencyComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(AgencyService);
    });

    it('should start with no search term when the route carries none', () => {
      expect(comp.currentSearch).toBe('');
    });

    describe('OnInit', () => {
      it('should take the paging parameters from the route', () => {
        spyOn(service, 'query').and.returnValue(page([agency]));

        comp.ngOnInit();

        expect(comp.page).toBe(10);
        expect(comp.ascending).toBe(false);
        expect(comp.predicate).toBe('id');
      });

      it('should load the agencies', () => {
        spyOn(service, 'query').and.returnValue(page([agency]));

        comp.ngOnInit();

        expect(comp.agencies).toEqual([agency]);
      });
    });

    describe('loading a page', () => {
      it('should ask for every agency by name when nothing is searched for', () => {
        const spy = spyOn(service, 'query').and.returnValue(page([agency]));

        comp.loadPage(1);

        expect(spy).toHaveBeenCalledWith({ page: 0, size: 200, sort: ['name,asc'] });
      });

      it('should use the search endpoint once a term is entered', () => {
        const spy = spyOn(service, 'search').and.returnValue(page([agency]));
        comp.predicate = 'name';
        comp.ascending = true;
        comp.currentSearch = 'cessda';

        comp.loadPage(3);

        expect(spy).toHaveBeenCalledWith({ page: 2, query: 'cessda', size: comp.itemsPerPage, sort: ['name,asc', 'id'] });
      });

      it('should not ask for every agency while searching', () => {
        const spy = spyOn(service, 'query');
        spyOn(service, 'search').and.returnValue(page([agency]));
        comp.currentSearch = 'cessda';
        comp.predicate = 'id';

        comp.loadPage(1);

        expect(spy).not.toHaveBeenCalled();
      });

      it('should fall back to the current page when given none', () => {
        const spy = spyOn(service, 'search').and.returnValue(page([agency]));
        comp.page = 4;
        comp.predicate = 'id';
        comp.currentSearch = 'cessda';

        comp.loadPage();

        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ page: 3 }));
      });

      it('should read the total from the count header', () => {
        spyOn(service, 'query').and.returnValue(page([agency], '42'));

        comp.loadPage(1);

        expect(comp.totalItems).toBe(42);
        expect(comp.ngbPaginationPage).toBe(1);
      });

      it('should hold an empty list when the server returns no body', () => {
        spyOn(service, 'query').and.returnValue(page(null));

        comp.loadPage(1);

        expect(comp.agencies).toEqual([]);
      });

      it('should keep the pagination on the current page when the request fails', () => {
        spyOn(service, 'query').and.returnValue(throwError(() => new Error('nope')));
        comp.page = 7;
        comp.ngbPaginationPage = 9;

        comp.loadPage();

        expect(comp.ngbPaginationPage).toBe(7);
      });
    });

    describe('searching', () => {
      it('should remember the term and start again from the first page', () => {
        const spy = spyOn(service, 'search').and.returnValue(page([agency]));
        comp.predicate = 'id';

        comp.search('cessda');

        expect(comp.currentSearch).toBe('cessda');
        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ page: 0, query: 'cessda' }));
      });
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

    describe('showing a link', () => {
      it('should drop the scheme and the www prefix', () => {
        expect(comp.urlCleaner('https://www.cessda.eu')).toBe('cessda.eu');
        expect(comp.urlCleaner('http://cessda.eu')).toBe('cessda.eu');
      });

      it('should keep the host only', () => {
        expect(comp.urlCleaner('https://www.cessda.eu/about/us')).toBe('cessda.eu');
      });

      it('should leave a bare host alone', () => {
        expect(comp.urlCleaner('cessda.eu')).toBe('cessda.eu');
      });
    });

    it('should track agencies by id, and fall back for one without', () => {
      expect(comp.trackId(0, agency)).toBe(4);
      expect(comp.trackId(0, createNewAgency())).toBe(-1);
    });

    describe('deleting', () => {
      it('should open the delete dialog on the agency', () => {
        comp.delete(agency);

        expect(modalService.open).toHaveBeenCalledWith(AgencyDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
        expect(modalStub.componentInstance.agency).toBe(agency);
      });
    });

    it('should survive being destroyed without a subscription', () => {
      expect(() => comp.ngOnDestroy()).not.toThrow();
    });
  });
});
