/* eslint-disable @typescript-eslint/no-explicit-any */
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
import Spy = jasmine.Spy;
import { of } from 'rxjs';

import { SpyObject } from './spyobject';
import { LoginService } from 'app/core/login/login.service';

export class MockLoginService extends SpyObject {
  loginSpy: Spy;
  logoutSpy: Spy;
  registerSpy: Spy;
  requestResetPasswordSpy: Spy;
  cancelSpy: Spy;

  constructor() {
    super(LoginService);

    this.loginSpy = this.spy('login').andReturn(of({}));
    this.logoutSpy = this.spy('logout').andReturn(this);
    this.registerSpy = this.spy('register').andReturn(this);
    this.requestResetPasswordSpy = this.spy('requestResetPassword').andReturn(this);
    this.cancelSpy = this.spy('cancel').andReturn(this);
  }

  setResponse(json: any): void {
    this.loginSpy = this.spy('login').andReturn(of(json));
  }
}
