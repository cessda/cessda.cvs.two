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
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject, of, throwError } from 'rxjs';

import { Authority } from 'app/shared/constants/authority.constants';
import { CvsTestModule } from '../../../test.module';
import { UserManagementUpdateComponent } from 'app/admin/user-management/user-management-update.component';
import { UserService } from 'app/core/user/user.service';
import { AgencyService } from 'app/agency/agency.service';
import { UserAgency } from 'app/shared/model/user-agency.model';

describe('Component Tests', () => {
  describe('User Management Update Component', () => {
    let comp: UserManagementUpdateComponent;
    let fixture: ComponentFixture<UserManagementUpdateComponent>;
    let service: UserService;
    const existingUser = {
      id: 1,
      login: 'user',
      firstName: 'first',
      lastName: 'last',
      email: 'first@last.com',
      activated: true,
      langKey: 'en',
      authorities: [Authority.USER],
      createdBy: 'admin',
    };
    // the tests push their own user through here, so each one can pick what the resolver returns
    const routeData = new BehaviorSubject<{ user: unknown }>({ user: existingUser });
    const route: ActivatedRoute = { data: routeData } as unknown as ActivatedRoute;

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [UserManagementUpdateComponent],
        providers: [
          FormBuilder,
          {
            provide: ActivatedRoute,
            useValue: route,
          },
        ],
      })
        .overrideTemplate(UserManagementUpdateComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(UserManagementUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(UserService);
    });

    describe('OnInit', () => {
      beforeEach(() => {
        routeData.next({ user: existingUser });
      });

      it('should load the authorities', () => {
        spyOn(service, 'authorities').and.returnValue(of([Authority.ADMIN]));

        comp.ngOnInit();

        expect(comp.authorities).toEqual([Authority.ADMIN]);
      });

      it('should fill the form from the user it is given', () => {
        comp.ngOnInit();

        expect(comp.editForm.controls.login.value).toBe('user');
        expect(comp.editForm.controls.langKey.value).toBe('en');
      });

      it('should sort the agencies by agency, then role, then language', () => {
        routeData.next({
          user: {
            ...existingUser,
            userAgencies: [
              { agencyId: 2, agencyRole: 'ADMIN_TL', language: 'de' },
              { agencyId: 1, agencyRole: 'ADMIN_TL', language: 'sk' },
              { agencyId: 1, agencyRole: 'ADMIN_CONTENT', language: 'de' },
            ],
          },
        });

        comp.ngOnInit();

        expect(comp.user.userAgencies).toEqual([
          { agencyId: 1, agencyRole: 'ADMIN_CONTENT', language: 'de' },
          { agencyId: 1, agencyRole: 'ADMIN_TL', language: 'sk' },
          { agencyId: 2, agencyRole: 'ADMIN_TL', language: 'de' },
        ]);
      });

      it('should place roles without a language before those with one and keep equal roles', () => {
        routeData.next({
          user: {
            ...existingUser,
            userAgencies: [
              { agencyId: 1, agencyRole: 'ADMIN_TL', language: 'de' },
              { agencyId: 1, agencyRole: 'ADMIN_TL' },
              { agencyId: 1, agencyRole: 'ADMIN_TL' },
            ],
          },
        });

        comp.ngOnInit();

        expect(comp.user.userAgencies).toEqual([
          { agencyId: 1, agencyRole: 'ADMIN_TL' },
          { agencyId: 1, agencyRole: 'ADMIN_TL' },
          { agencyId: 1, agencyRole: 'ADMIN_TL', language: 'de' },
        ]);
      });

      it('should leave the form empty when no user is resolved', () => {
        routeData.next({ user: undefined });

        comp.ngOnInit();

        expect(comp.user).toBeUndefined();
        expect(comp.editForm.controls.login.value).toBe('');
      });

      describe('a new user, whom the resolver hands over as an empty object', () => {
        beforeEach(() => {
          routeData.next({ user: {} });
        });

        it('should leave the agencies unset, which is what hides the agency roles table', () => {
          comp.ngOnInit();

          expect(comp.user.userAgencies).toBeUndefined();
        });

        it('should be activated from the start', () => {
          comp.ngOnInit();

          expect(comp.user.activated).toBe(true);
        });
      });
    });

    describe('save', () => {
      beforeEach(() => {
        jest.spyOn(window.history, 'back').mockImplementation(() => undefined);
        comp.editForm.patchValue({
          login: 'changed',
          firstName: 'First',
          lastName: 'Last',
          email: 'first@example.org',
          activated: false,
          langKey: 'de',
          authorities: [Authority.ADMIN],
        });
      });

      afterEach(() => {
        jest.restoreAllMocks();
      });

      it('Should call update service on save for existing user', () => {
        const entity = { id: 123 };
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.user = entity;

        comp.save();

        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.user).toEqual({
          id: 123,
          login: 'changed',
          firstName: 'First',
          lastName: 'Last',
          email: 'first@example.org',
          activated: false,
          langKey: 'de',
          authorities: [Authority.ADMIN],
        });
        expect(comp.isSaving).toBe(false);
        expect(window.history.back).toHaveBeenCalled();
      });

      it('Should call create service on save for new user', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          const entity = {};
          spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
          comp.user = entity;
          // WHEN
          comp.save();
          tick(); // simulate async

          // THEN
          expect(service.create).toHaveBeenCalledWith(entity);
          expect(comp.isSaving).toEqual(false);
        }),
      ));

      it('should default to no authorities when the form holds none', () => {
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: {} })));
        comp.user = {};
        comp.editForm.patchValue({ authorities: null });

        comp.save();

        expect(comp.user.authorities).toEqual([]);
      });

      it('should stay on the page when saving fails', () => {
        spyOn(service, 'update').and.returnValue(throwError(() => new Error('conflict')));
        comp.user = { id: 1 };

        comp.save();

        expect(comp.isSaving).toBe(false);
        expect(window.history.back).not.toHaveBeenCalled();
      });

      it('should stay on the page when creating the user fails', () => {
        spyOn(service, 'create').and.returnValue(throwError(() => new Error('conflict')));
        comp.user = {};

        comp.save();

        expect(comp.isSaving).toBe(false);
        expect(window.history.back).not.toHaveBeenCalled();
      });
    });

    describe('agencies', () => {
      const agencies = [
        { id: 4, name: 'CESSDA' },
        { id: 7, name: 'GESIS' },
      ];

      it('should load the agencies and select the first one', () => {
        const agencyService = TestBed.inject(AgencyService);
        jest.spyOn(agencyService, 'query').mockReturnValue(of(new HttpResponse({ body: agencies })));

        comp.ngOnInit();

        expect(agencyService.query).toHaveBeenCalledWith({ page: 0, size: 1000, sort: ['name,asc'] });
        expect(comp.agencies).toEqual(agencies);
        expect(comp.selectedAgencyId).toBe(4);
      });

      it('should fall back on the first agency id when the first agency has none', () => {
        const agencyService = TestBed.inject(AgencyService);
        jest.spyOn(agencyService, 'query').mockReturnValue(of(new HttpResponse({ body: [{ name: 'Unsaved' }] })));

        comp.ngOnInit();

        expect(comp.selectedAgencyId).toBe(1);
      });

      it('should name an agency by its id while the agencies are not loaded', () => {
        comp.agencies = [];

        expect(comp.getAgencyName(4)).toBe('4');
      });

      it('should name an agency once the agencies are loaded', () => {
        comp.agencies = agencies;

        expect(comp.getAgencyName(7)).toBe('GESIS');
      });

      it('should read the value of a select element', () => {
        const select = document.createElement('select');
        const option = document.createElement('option');
        option.value = 'ADMIN_CONTENT';
        select.appendChild(option);

        expect(comp.getValue({ target: select } as unknown as Event)).toBe('ADMIN_CONTENT');
      });
    });

    describe('agency roles', () => {
      let confirmSpy: jest.SpyInstance;

      beforeEach(() => {
        confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);
        comp.agencies = [
          { id: 4, name: 'CESSDA' },
          { id: 7, name: 'GESIS' },
        ];
      });

      afterEach(() => {
        confirmSpy.mockRestore();
      });

      it('should add a language role to the user once confirmed', () => {
        comp.user = { id: 9, userAgencies: [] };
        comp.selectedAgencyId = 7;
        comp.selectedAgencyRole = 'ADMIN_TL';
        comp.selectedLanguage = 'de';

        comp.saveAgencyRole();

        expect(confirmSpy).toHaveBeenCalledWith(expect.stringContaining('add agency role GESIS: ADMIN_TL-de?'));
        expect(comp.user.userAgencies).toEqual([{ userId: 9, agencyRole: 'ADMIN_TL', agencyId: 7, agencyName: 'GESIS', language: 'de' }]);
      });

      it('should add a role without a language to a user who has no roles yet', () => {
        comp.user = { id: 9 };
        comp.selectedAgencyId = 4;
        comp.selectedAgencyRole = 'ADMIN';
        comp.selectedLanguage = '';

        comp.saveAgencyRole();

        expect(confirmSpy).toHaveBeenCalledWith(expect.stringContaining('add agency role CESSDA: ADMIN?'));
        expect(comp.user.userAgencies).toEqual([
          { userId: 9, agencyRole: 'ADMIN', agencyId: 4, agencyName: 'CESSDA', language: undefined },
        ]);
      });

      it('should not add a role when the addition is cancelled', () => {
        confirmSpy.mockReturnValue(false);
        comp.user = { id: 9 };
        comp.selectedAgencyId = 4;

        comp.saveAgencyRole();

        expect(comp.user.userAgencies).toBeUndefined();
      });

      it('should delete a role once confirmed', () => {
        const kept: UserAgency = { agencyId: 4, agencyRole: 'ADMIN' };
        const deleted: UserAgency = { agencyId: 7, agencyRole: 'ADMIN_TL', language: 'de' };
        comp.user = { id: 9, userAgencies: [kept, deleted] };

        comp.deleteAgencyRole(deleted);

        expect(confirmSpy).toHaveBeenCalledWith(expect.stringContaining('delete agency role GESIS: ADMIN_TL-de?'));
        expect(comp.user.userAgencies).toEqual([kept]);
      });

      it('should leave the roles alone when the role is not among them', () => {
        const kept: UserAgency = { agencyId: 4, agencyRole: 'ADMIN' };
        comp.user = { id: 9, userAgencies: [kept] };

        comp.deleteAgencyRole({ agencyId: 7, agencyRole: 'ADMIN_CONTENT' });

        expect(confirmSpy).toHaveBeenCalledWith(expect.stringContaining('delete agency role GESIS: ADMIN_CONTENT?'));
        expect(comp.user.userAgencies).toEqual([kept]);
      });

      it('should do nothing when the user has no roles', () => {
        comp.user = { id: 9 };

        comp.deleteAgencyRole({ agencyId: 4, agencyRole: 'ADMIN' });

        expect(comp.user.userAgencies).toBeUndefined();
      });

      it('should keep the role when the deletion is cancelled', () => {
        confirmSpy.mockReturnValue(false);
        const role: UserAgency = { agencyId: 4, agencyRole: 'ADMIN' };
        comp.user = { id: 9, userAgencies: [role] };

        comp.deleteAgencyRole(role);

        expect(comp.user.userAgencies).toEqual([role]);
      });
    });
  });
});
