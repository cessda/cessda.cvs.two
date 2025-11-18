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
    name: 'vocabularyLanguageFromKey',
    standalone: false
})
export class VocabularyLanguageFromKeyPipe implements PipeTransform {
  private vocabLanguages: Record<string, { name: string; rtl?: boolean }> = {
    sq: { name: 'Albanian (sq)' },
    bs: { name: 'Bosnian (bs)' },
    bg: { name: 'Bulgarian (bg)' },
    hr: { name: 'Croatian (hr)' },
    cs: { name: 'Czech (cs)' },
    da: { name: 'Danish (da)' },
    nl: { name: 'Dutch (nl)' },
    en: { name: 'English (en)' },
    et: { name: 'Estonian (et)' },
    fi: { name: 'Finnish (fi)' },
    fr: { name: 'French (fr)' },
    de: { name: 'German (de)' },
    el: { name: 'Greek (el)' },
    hu: { name: 'Hungarian (hu)' },
    it: { name: 'Italian (it)' },
    ja: { name: 'Japanese (ja)' },
    lt: { name: 'Lithuanian (lt)' },
    mk: { name: 'Macedonian (mk)' },
    no: { name: 'Norwegian (no)' },
    pl: { name: 'Polish (pl)' },
    pt: { name: 'Portuguese (pt)' },
    ro: { name: 'Romanian (ro)' },
    ru: { name: 'Russian (ru)' },
    sr: { name: 'Serbian (sr)' },
    sk: { name: 'Slovak (sk)' },
    sl: { name: 'Slovenian (sl)' },
    es: { name: 'Spanish (es)' },
    sv: { name: 'Swedish (sv)' },
  };

  transform(lang: string): string {
    const language = this.vocabLanguages[lang];
    if (language) {
      return language.name;
    } else {
      return `Unknown (${lang})`;
    }
  }
}
