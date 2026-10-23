/*
 * Copyright © 2017-2026 CESSDA ERIC (support@cessda.eu)
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
import { TestBed } from '@angular/core/testing';

import { CSRFService } from 'app/core/auth/csrf.service';

describe('Service Tests', () => {
  describe('CSRF Service', () => {
    let service: CSRFService;

    const setCookie = (name: string, value: string): void => {
      document.cookie = `${name}=${value};path=/`;
    };

    const clearCookie = (name: string): void => {
      document.cookie = `${name}=;path=/;expires=Thu, 01 Jan 1970 00:00:00 GMT`;
    };

    beforeEach(() => {
      TestBed.configureTestingModule({});
      service = TestBed.inject(CSRFService);
    });

    afterEach(() => {
      clearCookie('XSRF-TOKEN');
      clearCookie('OTHER-TOKEN');
    });

    it('should read the token from the XSRF-TOKEN cookie by default', () => {
      setCookie('XSRF-TOKEN', 'a-token');

      expect(service.getCSRF()).toBe('a-token');
    });

    it('should read the cookie it is asked for', () => {
      setCookie('XSRF-TOKEN', 'a-token');
      setCookie('OTHER-TOKEN', 'another-token');

      expect(service.getCSRF('OTHER-TOKEN')).toBe('another-token');
    });

    it('should hand back nothing when the cookie has not been set', () => {
      expect(service.getCSRF()).toBe('');
    });
  });
});
