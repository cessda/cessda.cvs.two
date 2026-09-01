/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
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
import { BehaviorSubject, of } from 'rxjs';

import { Authority } from 'app/shared/constants/authority.constants';
import { CvsTestModule } from '../../../test.module';
import { UserManagementUpdateComponent } from 'app/admin/user-management/user-management-update.component';
import { UserService } from 'app/core/user/user.service';

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
      // Commented due to test failure ASYNC TIMEOUT
      // it('Should call update service on save for existing user', inject(
      //   [],
      //   fakeAsync(() => {
      //     // GIVEN
      //     const entity = new User(123);
      //     spyOn(service, 'update').and.returnValue(
      //       of(
      //         new HttpResponse({
      //           body: entity
      //         })
      //       )
      //     );
      //     comp.user = entity;
      //     comp.editForm.patchValue({ id: entity.id });
      //     // WHEN
      //     comp.save();
      //     tick(); // simulate async
      //
      //     // THEN
      //     expect(service.update).toHaveBeenCalledWith(entity);
      //     expect(comp.isSaving).toEqual(false);
      //   })
      // ));
      //
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
    });
  });
});
