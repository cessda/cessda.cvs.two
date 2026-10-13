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
import { PureFilterPipe } from 'app/shared/pipe/pure-filter.pipe';

describe('PureFilterPipe', () => {
  const pipe = new PureFilterPipe();
  const loggers = [
    { name: 'eu.cessda.cvs', level: 'DEBUG', count: 2, active: true },
    { name: 'org.springframework', level: 'INFO', count: 3, active: false },
  ];

  it('should keep everything for an empty filter', () => {
    expect(pipe.transform(loggers, '', 'name')).toBe(loggers);
    expect(pipe.transform(loggers, undefined, 'name')).toBe(loggers);
  });

  it('should match a string case-insensitively within the field', () => {
    expect(pipe.transform(loggers, 'CESSDA', 'name')).toEqual([loggers[0]]);
  });

  it('should match a number or a boolean exactly', () => {
    expect(pipe.transform(loggers, 3, 'count')).toEqual([loggers[1]]);
    expect(pipe.transform(loggers, true, 'active')).toEqual([loggers[0]]);
  });

  it('should match every field of an object filter', () => {
    expect(pipe.transform(loggers, { level: 'info', active: false })).toEqual([loggers[1]]);
    expect(pipe.transform(loggers, {})).toEqual(loggers);
  });

  it('should compute a filter given as a function', () => {
    expect(pipe.transform(loggers, () => 'spring', 'name')).toEqual([loggers[1]]);
  });

  it('should compare the items themselves without a field', () => {
    expect(pipe.transform(['alpha', 'beta'], 'ALP')).toEqual(['alpha']);
  });
});
