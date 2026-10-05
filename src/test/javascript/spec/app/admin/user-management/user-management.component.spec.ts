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
import { ComponentFixture, TestBed, waitForAsync, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { of, Subject } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { QueryParamsNavigation } from '../../../helpers/query-params-navigation';
import { UserManagementComponent } from 'app/admin/user-management/user-management.component';
import { UserService } from 'app/core/user/user.service';
import { User } from 'app/core/user/user.model';
import { AgencyService } from 'app/agency/agency.service';

describe('Component Tests', () => {
  describe('User Management Component', () => {
    let comp: UserManagementComponent;
    let fixture: ComponentFixture<UserManagementComponent>;
    let service: UserService;
    let navigation: QueryParamsNavigation;

    beforeEach(waitForAsync(() => {
      navigation = new QueryParamsNavigation();
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [UserManagementComponent],
        providers: navigation.providers(),
      })
        .overrideTemplate(UserManagementComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(UserManagementComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(UserService);
    });

    describe('OnInit', () => {
      it('Should call load all on init', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          const headers = new HttpHeaders().append('link', 'link;link');
          spyOn(service, 'query').and.returnValue(
            of(
              new HttpResponse({
                body: [{ id: 123 }],
                headers,
              }),
            ),
          );

          // WHEN
          comp.ngOnInit();
          tick(); // simulate async

          // THEN
          expect(service.query).toHaveBeenCalled();
          expect(comp.users && comp.users[0]).toEqual(jasmine.objectContaining({ id: 123 }));
        }),
      ));
    });

    describe('deleteUser', () => {
      let closed: Subject<string>;
      let query: jasmine.Spy;
      let open: jasmine.Spy;
      let dialog: { componentInstance: { user?: User }; closed: Subject<string> };

      beforeEach(() => {
        closed = new Subject<string>();
        dialog = { componentInstance: {}, closed };
        open = jasmine.createSpy('open').and.returnValue(dialog);
        // CvsTestModule provides NgbModal as null, so the component is given a stub of its own
        (comp as unknown as { modalService: { open: jasmine.Spy } }).modalService = { open };
        query = spyOn(service, 'query').and.returnValue(of(new HttpResponse({ body: [{ id: 123 }] })));
      });

      it('should hand the user to the dialog it opens', () => {
        const user = { id: 7, login: 'jdoe' } as User;

        comp.deleteUser(user);

        expect(open).toHaveBeenCalled();
        expect(dialog.componentInstance.user).toBe(user);
      });

      it('should reload the users once the dialog reports a deletion', () => {
        comp.deleteUser({ id: 7, login: 'jdoe' } as User);

        closed.next('deleted');

        expect(query).toHaveBeenCalled();
      });

      it('should leave the list alone when the dialog closes for any other reason', () => {
        comp.deleteUser({ id: 7, login: 'jdoe' } as User);

        closed.next('cancel');

        expect(query).not.toHaveBeenCalled();
      });
    });

    describe('setActive', () => {
      it('Should update user and call load all', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          const headers = new HttpHeaders().append('link', 'link;link');
          const user = { id: 123 };
          spyOn(service, 'query').and.returnValue(
            of(
              new HttpResponse({
                body: [user],
                headers,
              }),
            ),
          );
          spyOn(service, 'update').and.returnValue(of(new HttpResponse({ status: 200 })));

          // WHEN
          comp.setActive(user, true);
          tick(); // simulate async

          // THEN
          expect(service.update).toHaveBeenCalledWith({ ...user, activated: true });
          expect(service.query).toHaveBeenCalled();
          expect(comp.users && comp.users[0]).toEqual(jasmine.objectContaining({ id: 123 }));
        }),
      ));
    });

    describe('paging through the users', () => {
      let query: jasmine.Spy;

      const page = (users: User[], total?: number): HttpResponse<User[]> =>
        new HttpResponse({
          body: users,
          headers: total === undefined ? new HttpHeaders() : new HttpHeaders({ 'X-Total-Count': String(total) }),
        });

      const lastRequest = (): { page: number; size: number; sort: string[] } => query.calls.mostRecent().args[0];

      beforeEach(() => {
        query = spyOn(service, 'query').and.returnValue(of(page([{ id: 1 }], 87)));
        spyOn(TestBed.inject(AgencyService), 'query').and.returnValue(of(new HttpResponse({ body: [{ id: 5, name: 'CESSDA' }] })));
      });

      it('should ask for the first page ordered by id when the URL carries nothing', () => {
        comp.ngOnInit();

        expect(lastRequest()).toEqual({ page: 0, size: 30, sort: ['id,asc'] });
        expect(comp.page).toBe(1);
      });

      it('should ask the server for the page in the URL, counting pages from zero', () => {
        navigation.open({ page: '3' });

        comp.ngOnInit();

        expect(lastRequest().page).toBe(2);
        expect(comp.page).toBe(3);
      });

      it('should move to a clicked page through the URL alone', () => {
        comp.ngOnInit();
        query.calls.reset();

        comp.loadPage(2);

        expect(navigation.current).toEqual({ page: 2 });
        expect(query).toHaveBeenCalledTimes(1);
        expect(lastRequest().page).toBe(1);
        expect(comp.page).toBe(2);
      });

      it('should come back to the first page with the URL', () => {
        navigation.open({ page: '2' });
        comp.ngOnInit();

        navigation.open({});

        expect(lastRequest().page).toBe(0);
        expect(comp.page).toBe(1);
      });

      it('should keep the order when moving to another page', () => {
        navigation.open({ sort: 'login,desc' });
        comp.ngOnInit();

        comp.loadPage(2);

        expect(lastRequest()).toEqual({ page: 1, size: 30, sort: ['login,desc', 'id'] });
      });

      it('should put the order chosen into the URL and break ties by id', () => {
        comp.ngOnInit();

        comp.predicate = 'email';
        comp.ascending = false;
        comp.updateSort();

        expect(navigation.current).toEqual({ sort: 'email,desc' });
        expect(lastRequest().sort).toEqual(['email,desc', 'id']);
      });

      it('should not navigate by itself while loading', () => {
        navigation.open({ page: '2', sort: 'login,asc' });

        comp.ngOnInit();

        expect(navigation.navigate).not.toHaveBeenCalled();
        expect(query).toHaveBeenCalledTimes(1);
      });

      it('should take the number of users in all from the X-Total-Count header', () => {
        comp.ngOnInit();

        expect(comp.totalItems).toBe(87);
      });

      it('should count the users returned when the server gives no total', () => {
        query.and.returnValue(of(page([{ id: 1 }, { id: 2 }])));

        comp.ngOnInit();

        expect(comp.totalItems).toBe(2);
      });

      it("should list a user's agencies in a stable order", () => {
        query.and.returnValue(
          of(
            page([
              {
                id: 1,
                userAgencies: [
                  { agencyId: 5, agencyRole: 'ADMIN_TL', language: 'de' },
                  { agencyId: 2, agencyRole: 'ADMIN' },
                  { agencyId: 5, agencyRole: 'ADMIN_TL', language: 'cs' },
                ],
              },
            ]),
          ),
        );

        comp.ngOnInit();

        expect(comp.users[0].userAgencies!.map(ua => comp.userAgencyToCompare(ua))).toEqual(['2ADMIN', '5ADMIN_TLcs', '5ADMIN_TLde']);
      });

      it('should name the agencies of the users', () => {
        comp.ngOnInit();

        expect(comp.getAgencyName(5)).toBe('CESSDA');
        expect(comp.getAgencyName(9)).toBe('Unknown agency (9)');
      });
    });
  });
});
