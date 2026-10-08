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
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ProfileService } from 'app/layouts/profiles/profile.service';
import { InfoResponse, ProfileInfo } from 'app/layouts/profiles/profile-info.model';

describe('Service Tests', () => {
  describe('Profile Service', () => {
    let service: ProfileService;
    let httpMock: HttpTestingController;

    const load = (response: InfoResponse): ProfileInfo => {
      let info!: ProfileInfo;
      service.getProfileInfo().subscribe(profileInfo => (info = profileInfo));
      httpMock.expectOne({ method: 'GET' }).flush(response);
      return info;
    };

    beforeEach(() => {
      TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });

      service = TestBed.inject(ProfileService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
      httpMock.verify();
    });

    it('should ask the management info endpoint', () => {
      service.getProfileInfo().subscribe();

      const req = httpMock.expectOne({ method: 'GET' });

      expect(req.request.url).toContain('management/info');
      req.flush({});
    });

    it('should keep the profiles the server reports', () => {
      expect(load({ activeProfiles: ['prod', 'swagger'] }).activeProfiles).toEqual(['prod', 'swagger']);
    });

    it('should hold an empty list when the server names no profiles', () => {
      expect(load({}).activeProfiles).toEqual([]);
    });

    describe('what the profiles turn on', () => {
      it('should be in production on the prod profile', () => {
        expect(load({ activeProfiles: ['prod'] }).inProduction).toBe(true);
      });

      it('should not be in production without it', () => {
        expect(load({ activeProfiles: ['dev'] }).inProduction).toBe(false);
      });

      it('should enable Swagger on the swagger profile', () => {
        expect(load({ activeProfiles: ['swagger'] }).swaggerEnabled).toBe(true);
      });

      it('should not enable Swagger without it', () => {
        expect(load({ activeProfiles: ['dev'] }).swaggerEnabled).toBe(false);
      });
    });

    describe('the ribbon', () => {
      it('should show it on a profile the server asks for', () => {
        expect(load({ activeProfiles: ['dev'], 'display-ribbon-on-profiles': 'dev' }).ribbonEnv).toBe('dev');
      });

      it('should take the first profile that is actually active', () => {
        const info = load({ activeProfiles: ['staging'], 'display-ribbon-on-profiles': 'dev,staging' });

        expect(info.ribbonEnv).toBe('staging');
      });

      it('should stay unset when no requested profile is active', () => {
        expect(load({ activeProfiles: ['prod'], 'display-ribbon-on-profiles': 'dev' }).ribbonEnv).toBeUndefined();
      });

      it('should stay unset when the server asks for none', () => {
        expect(load({ activeProfiles: ['dev'] }).ribbonEnv).toBeUndefined();
      });
    });

    it('should ask the server only once and replay the answer', () => {
      const first = load({ activeProfiles: ['dev'] });

      let second!: ProfileInfo;
      service.getProfileInfo().subscribe(profileInfo => (second = profileInfo));

      // a second request would be left unmatched, and the verify in afterEach would fail
      httpMock.expectNone({ method: 'GET' });
      expect(second).toBe(first);
    });
  });
});
