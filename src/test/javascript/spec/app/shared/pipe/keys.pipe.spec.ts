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
import { KeysPipe } from 'app/shared/pipe/keys.pipe';

describe('KeysPipe', () => {
  const pipe = new KeysPipe();

  it('should list the entries of an object', () => {
    expect(pipe.transform({ db: { status: 'UP' }, ping: 'UP' })).toEqual([
      { key: 'db', value: { status: 'UP' } },
      { key: 'ping', value: 'UP' },
    ]);
  });

  it('should list nothing for a missing object', () => {
    expect(pipe.transform(undefined)).toEqual([]);
    expect(pipe.transform(null)).toEqual([]);
  });
});
