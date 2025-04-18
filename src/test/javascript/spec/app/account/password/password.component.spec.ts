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
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of, throwError } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { PasswordComponent } from 'app/account/password/password.component';
import { PasswordService } from 'app/account/password/password.service';

describe('Component Tests', () => {
  describe('PasswordComponent', () => {
    let comp: PasswordComponent;
    let fixture: ComponentFixture<PasswordComponent>;
    let service: PasswordService;

    beforeEach(async(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [PasswordComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(PasswordComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(PasswordComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(PasswordService);
    });

    it('should show error if passwords do not match', () => {
      // GIVEN
      comp.passwordForm.patchValue({
        newPassword: 'password1',
        confirmPassword: 'password2',
      });
      // WHEN
      comp.changePassword();
      // THEN
      expect(comp.doNotMatch).toBe(true);
      expect(comp.error).toBe(false);
      expect(comp.success).toBe(false);
    });

    it('should call Auth.changePassword when passwords match', () => {
      // GIVEN
      const passwordValues = {
        currentPassword: 'oldPassword',
        newPassword: 'myPassword',
      };

      spyOn(service, 'save').and.returnValue(of(new HttpResponse({ body: true })));

      comp.passwordForm.patchValue({
        currentPassword: passwordValues.currentPassword,
        newPassword: passwordValues.newPassword,
        confirmPassword: passwordValues.newPassword,
      });

      // WHEN
      comp.changePassword();

      // THEN
      expect(service.save).toHaveBeenCalledWith(passwordValues.newPassword, passwordValues.currentPassword);
    });

    it('should set success to true upon success', () => {
      // GIVEN
      spyOn(service, 'save').and.returnValue(of(new HttpResponse({ body: true })));
      comp.passwordForm.patchValue({
        newPassword: 'myPassword',
        confirmPassword: 'myPassword',
      });

      // WHEN
      comp.changePassword();

      // THEN
      expect(comp.doNotMatch).toBe(false);
      expect(comp.error).toBe(false);
      expect(comp.success).toBe(true);
    });

    it('should notify of error if change password fails', () => {
      // GIVEN
      spyOn(service, 'save').and.returnValue(throwError('ERROR'));
      comp.passwordForm.patchValue({
        newPassword: 'myPassword',
        confirmPassword: 'myPassword',
      });

      // WHEN
      comp.changePassword();

      // THEN
      expect(comp.doNotMatch).toBe(false);
      expect(comp.success).toBe(false);
      expect(comp.error).toBe(true);
    });
  });
});
