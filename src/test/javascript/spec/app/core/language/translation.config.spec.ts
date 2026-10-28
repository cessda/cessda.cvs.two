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
import { readFileSync } from 'fs';
import { join } from 'path';
import { firstValueFrom } from 'rxjs';

import { translatePartialLoader } from 'app/core/language/translation.config';

const en = JSON.parse(readFileSync(join(process.cwd(), 'src/main/webapp/i18n/en.json'), 'utf-8'));

describe('translatePartialLoader', () => {
  it('should load the translations of a language from the bundle', async () => {
    const translations = await firstValueFrom(translatePartialLoader().getTranslation('en'));

    expect(translations).toEqual(en);
  });

  it('should load each language from its own file', async () => {
    const loader = translatePartialLoader();

    const de = await firstValueFrom(loader.getTranslation('de'));

    expect(de).not.toEqual(en);
    expect(de['global']).toBeDefined();
  });
});
