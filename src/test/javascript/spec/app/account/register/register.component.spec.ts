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
import { ComponentFixture, TestBed, async, inject, tick, fakeAsync } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { JhiLanguageService } from 'ng-jhipster';

import { MockLanguageService } from '../../../helpers/mock-language.service';
import { CvsTestModule } from '../../../test.module';
import { EMAIL_ALREADY_USED_TYPE, LOGIN_ALREADY_USED_TYPE } from 'app/shared/constants/error.constants';
import { RegisterService } from 'app/account/register/register.service';
import { RegisterComponent } from 'app/account/register/register.component';

describe('Component Tests', () => {
  describe('RegisterComponent', () => {
    let fixture: ComponentFixture<RegisterComponent>;
    let comp: RegisterComponent;

    beforeEach(async(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [RegisterComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(RegisterComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(RegisterComponent);
      comp = fixture.componentInstance;
    });

    it('should ensure the two passwords entered match', () => {
      comp.registerForm.patchValue({
        password: 'password',
        confirmPassword: 'non-matching',
      });

      comp.register();

      expect(comp.doNotMatch).toBe(true);
    });

    it('should update success to true after creating an account', inject(
      [RegisterService, JhiLanguageService],
      fakeAsync((service: RegisterService, mockTranslate: MockLanguageService) => {
        spyOn(service, 'save').and.returnValue(of({}));
        comp.registerForm.patchValue({
          password: 'password',
          confirmPassword: 'password',
        });

        comp.register();
        tick();

        expect(service.save).toHaveBeenCalledWith({
          email: '',
          password: 'password',
          login: '',
          langKey: 'en',
        });
        expect(comp.success).toBe(true);
        expect(mockTranslate.getCurrentLanguageSpy).toHaveBeenCalled();
        expect(comp.errorUserExists).toBe(false);
        expect(comp.errorEmailExists).toBe(false);
        expect(comp.error).toBe(false);
      }),
    ));

    it('should notify of user existence upon 400/login already in use', inject(
      [RegisterService],
      fakeAsync((service: RegisterService) => {
        spyOn(service, 'save').and.returnValue(
          throwError({
            status: 400,
            error: { type: LOGIN_ALREADY_USED_TYPE },
          }),
        );
        comp.registerForm.patchValue({
          password: 'password',
          confirmPassword: 'password',
        });

        comp.register();
        tick();

        expect(comp.errorUserExists).toBe(true);
        expect(comp.errorEmailExists).toBe(false);
        expect(comp.error).toBe(false);
      }),
    ));

    it('should notify of email existence upon 400/email address already in use', inject(
      [RegisterService],
      fakeAsync((service: RegisterService) => {
        spyOn(service, 'save').and.returnValue(
          throwError({
            status: 400,
            error: { type: EMAIL_ALREADY_USED_TYPE },
          }),
        );
        comp.registerForm.patchValue({
          password: 'password',
          confirmPassword: 'password',
        });

        comp.register();
        tick();

        expect(comp.errorEmailExists).toBe(true);
        expect(comp.errorUserExists).toBe(false);
        expect(comp.error).toBe(false);
      }),
    ));

    it('should notify of generic error', inject(
      [RegisterService],
      fakeAsync((service: RegisterService) => {
        spyOn(service, 'save').and.returnValue(
          throwError({
            status: 503,
          }),
        );
        comp.registerForm.patchValue({
          password: 'password',
          confirmPassword: 'password',
        });

        comp.register();
        tick();

        expect(comp.errorUserExists).toBe(false);
        expect(comp.errorEmailExists).toBe(false);
        expect(comp.error).toBe(true);
      }),
    ));
  });
});
