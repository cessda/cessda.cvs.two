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
import { FormBuilder } from '@angular/forms';
import { throwError, of } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { SettingsComponent } from 'app/account/settings/settings.component';
import { MockAccountService } from '../../../helpers/mock-account.service';

describe('Component Tests', () => {
  describe('SettingsComponent', () => {
    let comp: SettingsComponent;
    let fixture: ComponentFixture<SettingsComponent>;
    let mockAuth: MockAccountService;
    const accountValues: Account = {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      activated: true,
      email: 'john.doe@mail.com',
      langKey: 'en',
      login: 'john',
      authorities: [],
      imageUrl: '',
      userAgencies: [],
    };

    beforeEach(async(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [SettingsComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(SettingsComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(SettingsComponent);
      comp = fixture.componentInstance;
      mockAuth = TestBed.get(AccountService);
      mockAuth.setIdentityResponse(accountValues);
    });

    it('should send the current identity upon save', () => {
      // GIVEN
      mockAuth.saveSpy.and.returnValue(of({}));
      const settingsFormValues = {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@mail.com',
        langKey: 'en',
      };

      // WHEN
      comp.ngOnInit();
      comp.save();

      // THEN
      expect(mockAuth.identitySpy).toHaveBeenCalled();
      expect(mockAuth.saveSpy).toHaveBeenCalledWith(accountValues);
      expect(mockAuth.authenticateSpy).toHaveBeenCalledWith(accountValues);
      expect(comp.settingsForm.value).toEqual(settingsFormValues);
    });

    it('should notify of success upon successful save', () => {
      // GIVEN
      mockAuth.saveSpy.and.returnValue(of({}));

      // WHEN
      comp.ngOnInit();
      comp.save();

      // THEN
      expect(comp.success).toBe(true);
    });

    it('should notify of error upon failed save', () => {
      // GIVEN
      mockAuth.saveSpy.and.returnValue(throwError('ERROR'));

      // WHEN
      comp.ngOnInit();
      comp.save();

      // THEN
      expect(comp.success).toBe(false);
    });
  });
});
