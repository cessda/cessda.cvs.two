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

describe('Pipe Tests', () => {
  describe('Link Http Pipe', () => {
    const pipe = new LinkHttpPipe();

    const links: { shape: string; href: string; expected: string }[] = [
      { shape: 'an http link', href: 'http://cessda.eu', expected: 'http://cessda.eu' },
      { shape: 'an https link', href: 'https://cessda.eu', expected: 'https://cessda.eu' },
      { shape: 'a bare host', href: 'cessda.eu', expected: 'http://cessda.eu' },
      { shape: 'a host with a path', href: 'cessda.eu/vocabularies', expected: 'http://cessda.eu/vocabularies' },
      { shape: 'nothing at all', href: '', expected: 'http://' },
    ];

    links.forEach(link => {
      it(`should leave ${link.shape} alone or give it a scheme`, () => {
        expect(pipe.transform(link.href)).toBe(link.expected);
      });
    });

    it('should not mistake a host that merely starts with http for a scheme', () => {
      expect(pipe.transform('httpbin.org')).toBe('http://httpbin.org');
    });
  });
});
