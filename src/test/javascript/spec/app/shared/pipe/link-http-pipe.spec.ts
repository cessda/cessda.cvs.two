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
import { LinkHttpPipe } from 'app/shared/pipe/link-http-pipe';

describe('LinkHttpPipe', () => {
  const pipe = new LinkHttpPipe();

  it.each([
    { name: 'leave an http link alone', href: 'http://cessda.eu', expected: 'http://cessda.eu' },
    { name: 'leave an https link alone', href: 'https://cessda.eu', expected: 'https://cessda.eu' },
    { name: 'prefix a bare host', href: 'cessda.eu', expected: 'http://cessda.eu' },
    { name: 'prefix a protocol it does not know', href: 'ftp://cessda.eu', expected: 'http://ftp://cessda.eu' },
    { name: 'prefix an empty string', href: '', expected: 'http://' },
  ])('should $name', ({ href, expected }) => {
    expect(pipe.transform(href)).toBe(expected);
  });

  it('should be case sensitive about the scheme, as startsWith is', () => {
    expect(pipe.transform('HTTP://cessda.eu')).toBe('http://HTTP://cessda.eu');
  });
});
