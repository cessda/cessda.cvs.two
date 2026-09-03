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
import { existsSync } from 'fs';
import { join } from 'path';

import { FindLanguageFromKeyPipe } from 'app/shared/language/find-language-from-key.pipe';
import { LANGUAGES } from 'app/core/language/language.constants';

describe('Pipe Tests', () => {
  describe('Find Language From Key Pipe', () => {
    let pipe: FindLanguageFromKeyPipe;

    beforeEach(() => {
      pipe = new FindLanguageFromKeyPipe();
    });

    it('should name a language in that same language', () => {
      expect(pipe.transform('en')).toBe('English');
      expect(pipe.transform('sk')).toBe('Slovenský');
    });

    it('should name a language whose key carries a region', () => {
      expect(pipe.transform('pt-pt')).toBe('Português');
    });

    describe('a key the pipe does not know', () => {
      // the settings page pipes every entry of LANGUAGES through here inside a loop, so
      // one unknown key used to throw and take the whole form down with it

      it('should fall back to the key itself', () => {
        expect(pipe.transform('xx')).toBe('xx');
      });

      it('should not throw on a language that is not set at all', () => {
        expect(() => pipe.transform(undefined as unknown as string)).not.toThrow();
      });
    });

    describe('the languages the application offers', () => {
      it('should all have a name in this pipe', () => {
        // an entry without a name now returns its own key, which is what this looks for
        expect(LANGUAGES.filter(lang => pipe.transform(lang) === lang)).toEqual([]);
      });

      it('should all have a translation file', () => {
        // offering a language with no i18n file leaves the interface untranslatable
        const i18n = join(process.cwd(), 'src/main/webapp/i18n');

        expect(LANGUAGES.filter(lang => !existsSync(join(i18n, `${lang}.json`)))).toEqual([]);
      });
    });
  });
});
