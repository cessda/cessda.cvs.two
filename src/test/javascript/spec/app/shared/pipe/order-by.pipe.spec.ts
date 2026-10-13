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
import { OrderByPipe } from 'app/shared/pipe/order-by.pipe';

describe('OrderByPipe', () => {
  const pipe = new OrderByPipe();
  const loggers = [{ name: 'b' }, { name: 'c' }, { name: 'a' }, { name: 'a' }];

  it('should sort by the field, leaving the input as it was', () => {
    expect(pipe.transform(loggers, 'name').map(logger => logger.name)).toEqual(['a', 'a', 'b', 'c']);
    expect(loggers.map(logger => logger.name)).toEqual(['b', 'c', 'a', 'a']);
  });

  it('should sort in reverse', () => {
    expect(pipe.transform(loggers, 'name', true).map(logger => logger.name)).toEqual(['c', 'b', 'a', 'a']);
  });

  it('should sort the items themselves without a field', () => {
    expect(pipe.transform([2, 3, 1])).toEqual([1, 2, 3]);
    expect(pipe.transform([2, 3, 1], '', true)).toEqual([3, 2, 1]);
  });

  it('should pass a missing list through', () => {
    expect(pipe.transform(undefined as unknown as string[])).toBeUndefined();
  });
});
