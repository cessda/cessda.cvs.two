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
import moment, { Moment } from 'moment';
import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';

import { NgbDateMomentAdapter } from 'app/shared/util/datepicker-adapter';

describe('Util Tests', () => {
  describe('Ngb Date Moment Adapter', () => {
    const adapter = new NgbDateMomentAdapter();

    describe('from a moment to the datepicker', () => {
      it('should hand over the year, the month and the day', () => {
        expect(adapter.fromModel(moment('2026-09-11', 'YYYY-MM-DD'))).toEqual({ year: 2026, month: 9, day: 11 });
      });

      it('should count months from one rather than from zero', () => {
        // moment numbers January as 0, the datepicker as 1
        expect(adapter.fromModel(moment('2026-01-31', 'YYYY-MM-DD')).month).toBe(1);
      });

      it('should hand over nothing when there is no date', () => {
        expect(adapter.fromModel(null as unknown as Moment)).toBeNull();
      });

      it('should hand over nothing for a date that is not valid', () => {
        expect(adapter.fromModel(moment('not a date', 'YYYY-MM-DD'))).toBeNull();
      });

      it('should hand over nothing for something that is not a moment at all', () => {
        expect(adapter.fromModel('2026-09-11' as unknown as Moment)).toBeNull();
      });
    });

    describe('from the datepicker to a moment', () => {
      it('should take the year, the month and the day back', () => {
        const date = adapter.toModel({ year: 2026, month: 9, day: 11 });

        expect(date.format('YYYY-MM-DD')).toBe('2026-09-11');
      });

      it('should give back a valid moment', () => {
        expect(adapter.toModel({ year: 2026, month: 9, day: 11 }).isValid()).toBe(true);
      });

      it('should give back nothing when the datepicker holds no date', () => {
        expect(adapter.toModel(null as unknown as NgbDateStruct)).toBeNull();
      });
    });

    it('should survive a round trip through the datepicker', () => {
      const original = moment('2026-02-28', 'YYYY-MM-DD');

      const returned = adapter.toModel(adapter.fromModel(original));

      expect(returned.format('YYYY-MM-DD')).toBe('2026-02-28');
    });
  });
});
