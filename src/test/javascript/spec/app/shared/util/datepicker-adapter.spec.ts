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
import moment from 'moment';

import { NgbDateMomentAdapter } from 'app/shared/util/datepicker-adapter';

describe('NgbDateMomentAdapter', () => {
  const adapter = new NgbDateMomentAdapter();

  describe('fromModel', () => {
    it('should turn a moment into a one-based month date struct', () => {
      expect(adapter.fromModel(moment('2026-03-04'))).toEqual({ year: 2026, month: 3, day: 4 });
    });

    it.each([
      { name: 'null', date: null },
      { name: 'undefined', date: undefined },
      { name: 'an invalid moment', date: moment.invalid() },
      { name: 'something that is not a moment', date: new Date('2026-03-04') },
    ])('should return null for $name', ({ date }) => {
      expect(adapter.fromModel(date as never)).toBeNull();
    });
  });

  describe('toModel', () => {
    it('should turn a date struct back into a moment', () => {
      const date = adapter.toModel({ year: 2026, month: 3, day: 4 });

      expect(date.format('YYYY-MM-DD')).toBe('2026-03-04');
    });

    it('should round trip a moment through both directions', () => {
      const original = moment('2026-12-31');

      expect(adapter.toModel(adapter.fromModel(original)).format('YYYY-MM-DD')).toBe('2026-12-31');
    });

    it('should return null when there is no date struct', () => {
      expect(adapter.toModel(null as never)).toBeNull();
    });
  });
});
