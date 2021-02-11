/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { UserAgencyDetailComponent } from 'app/entities/user-agency/user-agency-detail.component';
import { UserAgency } from 'app/shared/model/user-agency.model';

describe('Component Tests', () => {
  describe('UserAgency Management Detail Component', () => {
    let comp: UserAgencyDetailComponent;
    let fixture: ComponentFixture<UserAgencyDetailComponent>;
    const route = ({ data: of({ userAgency: new UserAgency(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [UserAgencyDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(UserAgencyDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(UserAgencyDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load userAgency on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.userAgency).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
