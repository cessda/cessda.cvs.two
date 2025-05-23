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
import { ElementRef } from '@angular/core';
import { ComponentFixture, TestBed, inject, tick, fakeAsync } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';

import { CvsTestModule } from '../../../../test.module';
import { PasswordResetFinishComponent } from 'app/account/password-reset/finish/password-reset-finish.component';
import { PasswordResetFinishService } from 'app/account/password-reset/finish/password-reset-finish.service';
import { MockActivatedRoute } from '../../../../helpers/mock-route.service';

describe('Component Tests', () => {
  describe('PasswordResetFinishComponent', () => {
    let fixture: ComponentFixture<PasswordResetFinishComponent>;
    let comp: PasswordResetFinishComponent;

    beforeEach(() => {
      fixture = TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [PasswordResetFinishComponent],
        providers: [
          FormBuilder,
          {
            provide: ActivatedRoute,
            useValue: new MockActivatedRoute({ key: 'XYZPDQ' }),
          },
        ],
      })
        .overrideTemplate(PasswordResetFinishComponent, '')
        .createComponent(PasswordResetFinishComponent);
    });

    beforeEach(() => {
      fixture = TestBed.createComponent(PasswordResetFinishComponent);
      comp = fixture.componentInstance;
      comp.ngOnInit();
    });

    it('should define its initial state', () => {
      expect(comp.initialized).toBe(true);
      expect(comp.key).toEqual('XYZPDQ');
    });

    it('sets focus after the view has been initialized', () => {
      const node = {
        // eslint-disable-next-line @typescript-eslint/no-empty-function
        focus(): void {},
      };
      comp.newPassword = new ElementRef(node);
      spyOn(node, 'focus');

      comp.ngAfterViewInit();

      expect(node.focus).toHaveBeenCalled();
    });

    it('should ensure the two passwords entered match', () => {
      comp.passwordForm.patchValue({
        newPassword: 'password',
        confirmPassword: 'non-matching',
      });

      comp.finishReset();

      expect(comp.doNotMatch).toBe(true);
    });

    it('should update success to true after resetting password', inject(
      [PasswordResetFinishService],
      fakeAsync((service: PasswordResetFinishService) => {
        spyOn(service, 'save').and.returnValue(of({}));
        comp.passwordForm.patchValue({
          newPassword: 'password',
          confirmPassword: 'password',
        });

        comp.finishReset();
        tick();

        expect(service.save).toHaveBeenCalledWith('XYZPDQ', 'password');
        expect(comp.success).toBe(true);
      }),
    ));

    it('should notify of generic error', inject(
      [PasswordResetFinishService],
      fakeAsync((service: PasswordResetFinishService) => {
        spyOn(service, 'save').and.returnValue(throwError('ERROR'));
        comp.passwordForm.patchValue({
          newPassword: 'password',
          confirmPassword: 'password',
        });

        comp.finishReset();
        tick();

        expect(service.save).toHaveBeenCalledWith('XYZPDQ', 'password');
        expect(comp.success).toBe(false);
        expect(comp.error).toBe(true);
      }),
    ));
  });
});
