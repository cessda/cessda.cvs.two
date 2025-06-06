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
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { advanceTo } from 'jest-date-mock';

import { CvsTestModule } from '../../../test.module';
import { AuditsComponent } from 'app/admin/audits/audits.component';
import { AuditsService } from 'app/admin/audits/audits.service';
import { Audit } from 'app/admin/audits/audit.model';
import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { AuditData } from 'app/admin/audits/audit-data.model';

function build2DigitsDatePart(datePart: number): string {
  return `0${datePart}`.slice(-2);
}

function getDate(isToday = true): string {
  let date: Date = new Date();
  if (isToday) {
    // Today + 1 day - needed if the current day must be included
    date.setDate(date.getDate() + 1);
  } else {
    // get last month
    if (date.getMonth() === 0) {
      date = new Date(date.getFullYear() - 1, 11, date.getDate());
    } else {
      date = new Date(date.getFullYear(), date.getMonth() - 1, date.getDate());
    }
  }
  const monthString = build2DigitsDatePart(date.getMonth() + 1);
  const dateString = build2DigitsDatePart(date.getDate());
  return `${date.getFullYear()}-${monthString}-${dateString}`;
}

describe('Component Tests', () => {
  describe('AuditsComponent', () => {
    let comp: AuditsComponent;
    let fixture: ComponentFixture<AuditsComponent>;
    let service: AuditsService;

    beforeEach(async(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [AuditsComponent],
        providers: [AuditsService],
      })
        .overrideTemplate(AuditsComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(AuditsComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(AuditsService);
    });

    describe('today function', () => {
      it('should set toDate to current date', () => {
        comp.ngOnInit();
        expect(comp.toDate).toBe(getDate());
      });

      it('if current day is last day of month then should set toDate to first day of next month', () => {
        advanceTo(new Date(2019, 0, 31, 0, 0, 0));
        comp.ngOnInit();
        expect(comp.toDate).toBe('2019-02-01');
      });

      it('if current day is not last day of month then should set toDate to next day of current month', () => {
        advanceTo(new Date(2019, 0, 27, 0, 0, 0));
        comp.ngOnInit();
        expect(comp.toDate).toBe('2019-01-28');
      });
    });

    describe('previousMonth function', () => {
      it('should set fromDate to previous month', () => {
        comp.ngOnInit();
        expect(comp.fromDate).toBe(getDate(false));
      });

      it('if current month is January then should set fromDate to previous year last month', () => {
        advanceTo(new Date(2019, 0, 20, 0, 0, 0));
        comp.ngOnInit();
        expect(comp.fromDate).toBe('2018-12-20');
      });

      it('if current month is not January then should set fromDate to current year previous month', () => {
        advanceTo(new Date(2019, 1, 20, 0, 0, 0));
        comp.ngOnInit();
        expect(comp.fromDate).toBe('2019-01-20');
      });
    });

    describe('By default, on init', () => {
      it('should set all default values correctly', () => {
        fixture.detectChanges();
        expect(comp.toDate).toBe(getDate());
        expect(comp.fromDate).toBe(getDate(false));
        expect(comp.itemsPerPage).toBe(ITEMS_PER_PAGE);
        expect(comp.page).toBe(10);
        expect(comp.ascending).toBe(false);
        expect(comp.predicate).toBe('id');
      });
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN
        const headers = new HttpHeaders().append('X-Total-Count', '1');
        const auditData: AuditData = { key: 'remoteAddress', value: '127.0.0.1' };
        const audit: Audit = { data: auditData, principal: 'user', timestamp: '20140101', type: 'AUTHENTICATION_SUCCESS', expanded: false };
        spyOn(service, 'query').and.returnValue(
          of(
            new HttpResponse({
              body: [audit],
              headers,
            }),
          ),
        );

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(service.query).toHaveBeenCalled();
        expect(comp.audits && comp.audits[0]).toEqual(jasmine.objectContaining(audit));
        expect(comp.totalItems).toBe(1);
      });
    });

    describe('Create sort object', () => {
      beforeEach(() => {
        comp.toDate = getDate();
        comp.fromDate = getDate(false);
        spyOn(service, 'query').and.returnValue(of(new HttpResponse({ body: null })));
      });

      it('Should sort only by id asc', () => {
        // GIVEN
        comp.predicate = 'id';
        comp.ascending = false;

        // WHEN
        comp.transition();

        // THEN
        expect(service.query).toBeCalledWith(
          expect.objectContaining({
            sort: ['id,desc'],
          }),
        );
      });

      it('Should sort by timestamp asc then by id', () => {
        // GIVEN
        comp.predicate = 'timestamp';
        comp.ascending = true;

        // WHEN
        comp.transition();

        // THEN
        expect(service.query).toBeCalledWith(
          expect.objectContaining({
            sort: ['timestamp,asc', 'id'],
          }),
        );
      });
    });

    describe('loadPage', () => {
      beforeEach(() => {
        comp.toDate = getDate();
        comp.fromDate = getDate(false);
        comp.previousPage = 1;
        spyOn(comp, 'transition');
      });

      it('Should not reload page already shown', () => {
        // WHEN
        comp.loadPage(1);

        // THEN
        expect(comp.transition).not.toBeCalled();
      });

      it('Should load new page', () => {
        // WHEN
        comp.loadPage(2);

        // THEN
        expect(comp.previousPage).toBe(2);
        expect(comp.transition).toBeCalled();
      });
    });

    describe('transition', () => {
      beforeEach(() => {
        spyOn(service, 'query').and.returnValue(of(new HttpResponse({ body: null })));
      });

      it('Should not query data if fromDate and toDate are empty', () => {
        // WHEN
        comp.transition();

        // THEN
        expect(comp.canLoad()).toBe(false);
        expect(service.query).not.toBeCalled();
      });

      it('Should query data if fromDate and toDate are not empty', () => {
        // GIVEN
        comp.toDate = getDate();
        comp.fromDate = getDate(false);

        // WHEN
        comp.transition();

        // THEN
        expect(comp.canLoad()).toBe(true);
        expect(service.query).toBeCalled();
      });
    });
  });
});
