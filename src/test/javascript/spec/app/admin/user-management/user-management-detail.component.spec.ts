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
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { Authority } from 'app/shared/constants/authority.constants';
import { CvsTestModule } from '../../../test.module';
import { UserManagementDetailComponent } from 'app/admin/user-management/user-management-detail.component';
import { User } from 'app/core/user/user.model';

describe('Component Tests', () => {
  describe('User Management Detail Component', () => {
    let comp: UserManagementDetailComponent;
    let fixture: ComponentFixture<UserManagementDetailComponent>;
    const route: ActivatedRoute = {
      data: of({
        user: {
          id: 1,
          login: 'user',
          firstName: 'first',
          lastName: 'last',
          email: 'first@last.com',
          activated: true,
          langKey: 'en',
          authorities: [Authority.USER],
          createdBy: 'admin',
        },
      }),
    } as any as ActivatedRoute;

    beforeEach(async(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [UserManagementDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: route,
          },
        ],
      })
        .overrideTemplate(UserManagementDetailComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(UserManagementDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.user).toEqual(
          jasmine.objectContaining({
            id: 1,
            login: 'user',
            firstName: 'first',
            lastName: 'last',
            email: 'first@last.com',
            activated: true,
            langKey: 'en',
            authorities: [Authority.USER],
            createdBy: 'admin',
          }),
        );
      });
    });
  });
});
