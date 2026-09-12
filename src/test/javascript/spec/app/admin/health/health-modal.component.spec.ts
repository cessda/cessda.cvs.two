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
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { CvsTestModule } from '../../../test.module';
import { MockActiveModal } from '../../../helpers/mock-active-modal.service';
import { HealthModalComponent } from 'app/admin/health/health-modal.component';
import { HealthDetails, HealthKey } from 'app/admin/health/health.service';

describe('Component Tests', () => {
  describe('Health Modal Component', () => {
    let comp: HealthModalComponent;
    let fixture: ComponentFixture<HealthModalComponent>;
    let mockActiveModal: MockActiveModal;

    const GB = 1073741824;
    const MB = 1048576;

    const showing = (key: HealthKey): void => {
      comp.health = { key, value: {} as HealthDetails };
    };

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [HealthModalComponent],
      })
        .overrideTemplate(HealthModalComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(HealthModalComponent);
      comp = fixture.componentInstance;
      mockActiveModal = TestBed.inject(NgbActiveModal) as unknown as MockActiveModal;
    });

    describe('disk space', () => {
      beforeEach(() => {
        showing('diskSpace');
      });

      it('should read a large figure in gigabytes', () => {
        expect(comp.readableValue(2.5 * GB)).toBe('2.50 GB');
      });

      it('should read anything under a gigabyte in megabytes', () => {
        expect(comp.readableValue(512 * MB)).toBe('512.00 MB');
      });

      it('should treat exactly one gigabyte as megabytes, since it is not more than one', () => {
        expect(comp.readableValue(GB)).toBe('1024.00 MB');
      });

      it('should round to two places', () => {
        expect(comp.readableValue(1.23456 * GB)).toBe('1.23 GB');
      });

      it('should leave a value that is not a number alone', () => {
        expect(comp.readableValue('UP')).toBe('UP');
      });
    });

    describe('anything else', () => {
      it('should not read a number as storage under another key', () => {
        showing('db');

        expect(comp.readableValue(2 * GB)).toBe(String(2 * GB));
      });

      it('should not read a number as storage before a health entry is chosen', () => {
        expect(comp.readableValue(2 * GB)).toBe(String(2 * GB));
      });

      it('should spell out an object', () => {
        showing('db');

        expect(comp.readableValue({ database: 'MySQL' })).toBe('{"database":"MySQL"}');
      });

      it('should spell out a plain value', () => {
        showing('db');

        expect(comp.readableValue('UP')).toBe('UP');
      });

      it('should spell out a boolean', () => {
        showing('db');

        expect(comp.readableValue(true)).toBe('true');
      });
    });

    it('should dismiss the dialog', () => {
      comp.dismiss();

      expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
    });
  });
});
