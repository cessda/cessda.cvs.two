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
import { ComponentFixture, TestBed, async, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { UserManagementComponent } from 'app/admin/user-management/user-management.component';
import { UserService } from 'app/core/user/user.service';
import { User } from 'app/core/user/user.model';

describe('Component Tests', () => {
  describe('User Management Component', () => {
    let comp: UserManagementComponent;
    let fixture: ComponentFixture<UserManagementComponent>;
    let service: UserService;

    beforeEach(async(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [UserManagementComponent],
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
  });
});
