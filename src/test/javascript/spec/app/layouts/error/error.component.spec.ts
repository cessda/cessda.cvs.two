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
import { ActivatedRoute } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, of } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { ErrorComponent } from 'app/layouts/error/error.component';

describe('Component Tests', () => {
  describe('Error Component', () => {
    let comp: ErrorComponent;
    let fixture: ComponentFixture<ErrorComponent>;
    let translateService: TranslateService;
    let routeData: BehaviorSubject<{ errorMessage?: string }>;

    beforeEach(waitForAsync(() => {
      routeData = new BehaviorSubject<{ errorMessage?: string }>({});

      TestBed.configureTestingModule({
        imports: [CvsTestModule, TranslateModule.forRoot()],
        declarations: [ErrorComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: routeData } as unknown as ActivatedRoute,
          },
        ],
      })
        .overrideTemplate(ErrorComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(ErrorComponent);
      comp = fixture.componentInstance;
      translateService = TestBed.inject(TranslateService);
    });

    it('should show nothing when the route carries no error', () => {
      comp.ngOnInit();

      expect(comp.errorKey).toBeUndefined();
      expect(comp.errorMessage).toBeUndefined();
    });

    it('should translate the error the route carries', () => {
      spyOn(translateService, 'get').and.returnValue(of('The page does not exist'));
      routeData.next({ errorMessage: 'error.http.404' });

      comp.ngOnInit();

      expect(comp.errorKey).toBe('error.http.404');
      expect(comp.errorMessage).toBe('The page does not exist');
    });

    it('should ask for the translation of that very key', () => {
      const get = spyOn(translateService, 'get').and.returnValue(of('Forbidden'));
      routeData.next({ errorMessage: 'error.http.403' });

      comp.ngOnInit();

      expect(get).toHaveBeenCalledWith('error.http.403');
    });

    describe('when the language changes', () => {
      it('should translate the message again', () => {
        const get = spyOn(translateService, 'get').and.returnValue(of('The page does not exist'));
        routeData.next({ errorMessage: 'error.http.404' });
        comp.ngOnInit();

        get.and.returnValue(of('Die Seite existiert nicht'));
        translateService.onLangChange.emit({ lang: 'de', translations: {} });

        expect(comp.errorMessage).toBe('Die Seite existiert nicht');
      });

      it('should stop listening once destroyed', () => {
        spyOn(translateService, 'get').and.returnValue(of('The page does not exist'));
        routeData.next({ errorMessage: 'error.http.404' });
        comp.ngOnInit();

        comp.ngOnDestroy();

        expect(comp.langChangeSubscription?.closed).toBe(true);
      });

      it('should not listen at all when the route carries no error', () => {
        comp.ngOnInit();

        expect(comp.langChangeSubscription).toBeUndefined();
      });

      it('should survive being destroyed without having listened', () => {
        comp.ngOnInit();

        expect(() => comp.ngOnDestroy()).not.toThrow();
      });
    });
  });
});
