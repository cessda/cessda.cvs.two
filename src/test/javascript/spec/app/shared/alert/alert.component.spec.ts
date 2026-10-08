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
import { JhiAlert, JhiAlertService } from 'ng-jhipster';

import { CvsTestModule } from '../../../test.module';
import { AlertComponent } from 'app/shared/alert/alert.component';

describe('Component Tests', () => {
  describe('Alert Component', () => {
    let comp: AlertComponent;
    let fixture: ComponentFixture<AlertComponent>;

    let held: JhiAlert[];
    let clear: jasmine.Spy;

    beforeEach(waitForAsync(() => {
      held = [];
      clear = jasmine.createSpy('clear');

      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [AlertComponent],
        providers: [{ provide: JhiAlertService, useValue: { get: () => held, clear } }],
      })
        .overrideTemplate(AlertComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(AlertComponent);
      comp = fixture.componentInstance;
    });

    it('should hold no alerts until it has asked', () => {
      expect(comp.alerts).toEqual([]);
    });

    it('should show the alerts the service is holding', () => {
      held = [{ type: 'success', msg: 'Saved' } as JhiAlert];

      comp.ngOnInit();

      expect(comp.alerts).toBe(held);
    });

    it('should clear the alerts when it goes away', () => {
      comp.ngOnDestroy();

      expect(clear).toHaveBeenCalled();
    });

    describe('how an alert is placed', () => {
      it('should mark a toast as one', () => {
        expect(comp.setClasses({ toast: true } as JhiAlert)).toEqual({ 'jhi-toast': true });
      });

      it('should not mark an alert that is not a toast', () => {
        expect(comp.setClasses({ toast: false } as JhiAlert)).toEqual({ 'jhi-toast': false });
      });

      it('should treat a missing toast flag as not a toast', () => {
        expect(comp.setClasses({} as JhiAlert)).toEqual({ 'jhi-toast': false });
      });

      it('should add the position the alert asks for', () => {
        expect(comp.setClasses({ position: 'top right' } as JhiAlert)).toEqual({ 'jhi-toast': false, 'top right': true });
      });

      it('should keep both when a toast asks for a position', () => {
        expect(comp.setClasses({ toast: true, position: 'bottom left' } as JhiAlert)).toEqual({
          'jhi-toast': true,
          'bottom left': true,
        });
      });
    });
  });
});
