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
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { of } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { PageRibbonComponent } from 'app/layouts/profiles/page-ribbon.component';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { ProfileInfo } from 'app/layouts/profiles/profile-info.model';

describe('Component Tests', () => {
  describe('Page Ribbon Component', () => {
    let comp: PageRibbonComponent;
    let fixture: ComponentFixture<PageRibbonComponent>;
    let profileService: ProfileService;

    const profileInfo = (ribbonEnv?: string): ProfileInfo => ({
      activeProfiles: ['dev'],
      inProduction: false,
      swaggerEnabled: false,
      ribbonEnv,
    });

    const shownRibbon = (): string | undefined => {
      let env: string | undefined;
      comp.ribbonEnv$?.subscribe(value => (env = value));
      return env;
    };

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [PageRibbonComponent],
      })
        .overrideTemplate(PageRibbonComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(PageRibbonComponent);
      comp = fixture.componentInstance;
      profileService = TestBed.inject(ProfileService);
    });

    it('should hold no ribbon until it has asked', () => {
      expect(comp.ribbonEnv$).toBeUndefined();
    });

    it('should show the environment the profile names', () => {
      spyOn(profileService, 'getProfileInfo').and.returnValue(of(profileInfo('dev')));

      comp.ngOnInit();

      expect(shownRibbon()).toBe('dev');
    });

    it('should show nothing when the profile names no environment', () => {
      spyOn(profileService, 'getProfileInfo').and.returnValue(of(profileInfo(undefined)));

      comp.ngOnInit();

      expect(shownRibbon()).toBeUndefined();
    });
  });
});
