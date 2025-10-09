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
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'findLanguageFromKey',
    standalone: false
})
export class FindLanguageFromKeyPipe implements PipeTransform {
  private languages: Record<string, { name: string; rtl?: boolean }> = {
    al: { name: 'Shqip' },
    cs: { name: 'Český' },
    da: { name: 'Dansk' },
    nl: { name: 'Nederlands' },
    en: { name: 'English' },
    et: { name: 'Eesti' },
    fi: { name: 'Suomi' },
    fr: { name: 'Français' },
    de: { name: 'Deutsch' },
    hu: { name: 'Magyar' },
    it: { name: 'Italiano' },
    ja: { name: '日本語' },
    pl: { name: 'Polski' },
    'pt-pt': { name: 'Português' },
    ro: { name: 'Română' },
    ru: { name: 'Русский' },
    sk: { name: 'Slovenský' },
    sr: { name: 'Srpski' },
    es: { name: 'Español' },
    sv: { name: 'Svenska' },
    // jhipster-needle-i18n-language-key-pipe - JHipster will add/remove languages in this object
  };

  transform(lang: string): string {
    return this.languages[lang].name;
  }
}
