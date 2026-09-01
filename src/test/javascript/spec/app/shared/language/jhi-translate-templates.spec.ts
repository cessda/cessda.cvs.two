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
import { readFileSync, readdirSync, statSync } from 'fs';
import { join, relative } from 'path';

describe('Template Tests', () => {
  describe('Elements carrying jhiTranslate', () => {
    // JhiTranslateDirective assigns the translation to innerHTML, which discards whatever
    // the element already contained. A form control nested inside such an element is
    // therefore taken out of the DOM as soon as the translation arrives, leaving a field
    // that cannot be typed into. This is checked against the templates as written rather
    // than against a rendered component, because the directive does nothing while i18n is
    // disabled, as it is in the test configuration, and a rendered test would pass either way.

    const root = join(process.cwd(), 'src/main/webapp/app');

    const templates = (dir: string): string[] =>
      readdirSync(dir).flatMap(entry => {
        const path = join(dir, entry);
        if (statSync(path).isDirectory()) {
          return templates(path);
        }
        return path.endsWith('.html') ? [path] : [];
      });

    const translatedLabels = /<label\b[^>]*\bjhiTranslate\b[^>]*>[\s\S]*?<\/label>/g;

    it('should find the templates to check in the first place', () => {
      expect(templates(root).length).toBeGreaterThan(0);
    });

    it('should not wrap a form control in a translated label', () => {
      const offenders = templates(root)
        .filter(path => (readFileSync(path, 'utf8').match(translatedLabels) ?? []).some(label => /<(input|select|textarea)\b/.test(label)))
        .map(path => relative(process.cwd(), path));

      expect(offenders).toEqual([]);
    });
  });
});
