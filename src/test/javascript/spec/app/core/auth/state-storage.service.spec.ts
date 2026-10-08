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
import { SessionStorageService } from 'ngx-webstorage';

import { StateStorageService } from 'app/core/auth/state-storage.service';

describe('Service Tests', () => {
  describe('State Storage Service', () => {
    let service: StateStorageService;

    let store: jasmine.Spy;
    let retrieve: jasmine.Spy;
    let clear: jasmine.Spy;

    // the guard and the expired-session interceptor both lean on this key
    const KEY = 'previousUrl';

    beforeEach(() => {
      store = jasmine.createSpy('store');
      retrieve = jasmine.createSpy('retrieve').and.returnValue(null);
      clear = jasmine.createSpy('clear');

      TestBed.configureTestingModule({
        providers: [{ provide: SessionStorageService, useValue: { store, retrieve, clear } }],
      });

      service = TestBed.inject(StateStorageService);
    });

    it('should keep the url in session storage under its own key', () => {
      service.storeUrl('/editor/vocabulary');

      expect(store).toHaveBeenCalledWith(KEY, '/editor/vocabulary');
    });

    it('should read the url back from that same key', () => {
      retrieve.and.returnValue('/admin/licence');

      expect(service.getUrl()).toBe('/admin/licence');
      expect(retrieve).toHaveBeenCalledWith(KEY);
    });

    it('should hand back nothing when no url was kept', () => {
      retrieve.and.returnValue(null);

      expect(service.getUrl()).toBeNull();
    });

    it('should forget the url on request', () => {
      service.clearUrl();

      expect(clear).toHaveBeenCalledWith(KEY);
    });

    it('should not touch session storage merely by existing', () => {
      expect(store).not.toHaveBeenCalled();
      expect(clear).not.toHaveBeenCalled();
    });
  });
});
