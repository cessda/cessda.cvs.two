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
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { LoginService } from 'app/core/login/login.service';
import { LoginModalComponent } from 'app/shared/login/login.component';
import { CvsTestModule } from '../../../test.module';
import { MockLoginService } from '../../../helpers/mock-login.service';
import { MockRouter } from '../../../helpers/mock-route.service';
import { MockActiveModal } from '../../../helpers/mock-active-modal.service';

describe('Component Tests', () => {
  describe('LoginComponent', () => {
    let comp: LoginModalComponent;
    let fixture: ComponentFixture<LoginModalComponent>;
    let mockLoginService: MockLoginService;
    let mockRouter: MockRouter;
    let mockActiveModal: MockActiveModal;

    beforeEach(async(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [LoginModalComponent],
        providers: [
          FormBuilder,
          {
            provide: LoginService,
            useClass: MockLoginService,
          },
        ],
      })
        .overrideTemplate(LoginModalComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(LoginModalComponent);
      comp = fixture.componentInstance;
      mockLoginService = TestBed.get(LoginService);
      mockRouter = TestBed.get(Router);
      mockActiveModal = TestBed.get(NgbActiveModal);
    });

    it('should authenticate the user', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        const credentials = {
          username: 'admin',
          password: 'admin',
          rememberMe: true,
        };

        comp.loginForm.patchValue({
          username: 'admin',
          password: 'admin',
          rememberMe: true,
        });
        mockLoginService.setResponse({});
        mockRouter.url = '/admin/metrics';

        // WHEN/
        comp.login();
        tick(); // simulate async

        // THEN
        expect(comp.authenticationError).toEqual(false);
        expect(mockActiveModal.closeSpy).toHaveBeenCalled();
        expect(mockLoginService.loginSpy).toHaveBeenCalledWith(credentials);
      }),
    ));

    it('should empty the credentials upon cancel', () => {
      // GIVEN
      comp.loginForm.patchValue({
        username: 'admin',
        password: 'admin',
      });

      const expected = {
        username: '',
        password: '',
        rememberMe: false,
      };

      // WHEN
      comp.cancel();

      // THEN
      expect(comp.authenticationError).toEqual(false);
      expect(comp.loginForm.controls.username.value).toEqual(expected.username);
      expect(comp.loginForm.controls.password.value).toEqual(expected.password);
      expect(comp.loginForm.controls.rememberMe.value).toEqual(expected.rememberMe);
      expect(mockActiveModal.dismissSpy).toHaveBeenCalledWith('cancel');
    });

    it('should redirect user when register', () => {
      // WHEN
      comp.register();

      // THEN
      expect(mockActiveModal.dismissSpy).toHaveBeenCalledWith('to state register');
      expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['/account/register']);
    });

    it('should redirect user when request password', () => {
      // WHEN
      comp.requestResetPassword();

      // THEN
      expect(mockActiveModal.dismissSpy).toHaveBeenCalledWith('to state requestReset');
      expect(mockRouter.navigateSpy).toHaveBeenCalledWith(['/account/reset', 'request']);
    });
  });
});
