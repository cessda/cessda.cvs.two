/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

import {LanguageIso} from 'app/shared/model/enumerations/language-iso.model';
import * as moment from 'moment';
import {IVocabulary, Vocabulary} from 'app/shared/model/vocabulary.model';
import VocabularyUtil from 'app/shared/util/vocabulary-util';
import {Code} from 'app/shared/model/code.model';


describe('Vocabulary Util Tests', () => {
  let vocab: IVocabulary;
  let code: any;
  beforeEach(() => {
    vocab = new Vocabulary(
      0,
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      0,
      0,
      false,
      false,
      false,
      'AAA',
      'en',
      'AAA',
      '1.0',
      0,
      'AAA',
      'AAA',
      'AAA',
      moment(),
      moment(),
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA',
      'AAA'
    );
    code = {...new Code(), ...vocab};
  });
  describe('Vocabulary Util methods', () => {
    it('should get the item title and definition by selected lang', () => {
      for (const langIso in LanguageIso) {
        if ( isNaN(Number(langIso))) {
          const vocabTitleDef = VocabularyUtil.getTitleDefByLangIso(vocab, langIso);
          const codeTitleDef = VocabularyUtil.getTitleDefByLangIso(code, langIso);
          const langIsoFormatted = VocabularyUtil.getLangIsoFormatted( langIso );
          expect( vocabTitleDef[0] ).toBe( 'AAA');
          expect( vocabTitleDef[1] ).toBe( 'AAA');
          expect( vocabTitleDef[2] ).toBe( 'AAA');
          expect( codeTitleDef[0] ).toBe( 'AAA');
          expect( codeTitleDef[1] ).toBe( 'AAA');
          expect( vocabTitleDef[2] ).toBe( 'AAA');

          expect( langIsoFormatted ).toContain( '(' + langIso + ')' );
        }
      }

      // check the default if
      const langIso = 'xx';
      const vocabTitleDef = VocabularyUtil.getTitleDefByLangIso(vocab, langIso);
      const codeTitleDef = VocabularyUtil.getTitleDefByLangIso(code, langIso);
      const langIsoFormatted = VocabularyUtil.getLangIsoFormatted( langIso );
      expect( vocabTitleDef[0] ).toBe( 'AAA');
      expect( vocabTitleDef[1] ).toBe( 'AAA');
      expect( vocabTitleDef[2] ).toBe( 'AAA');
      expect( codeTitleDef[0] ).toBe( 'AAA');
      expect( codeTitleDef[1] ).toBe( 'AAA');
      expect( vocabTitleDef[2] ).toBe( 'AAA');

      expect( langIsoFormatted ).toContain( 'Unknown' );
    });
    it('should get langIso By Enum', () => {
      for (const langIso in LanguageIso) {
        if ( isNaN(Number(langIso))) {
          const lang: string = langIso;
          const value = VocabularyUtil.getLangIsoByEnum(LanguageIso[lang]);
          expect( value ).toBe( lang );
        }
      }
    });
    it('should compare version number', () => {
      expect( VocabularyUtil.compareVersionNumber('1.0','1.1') ).toBe( -1 );
      expect( VocabularyUtil.compareVersionNumber('1.0','1.0') ).toBe( 0 );
      expect( VocabularyUtil.compareVersionNumber('1.1','1.0') ).toBe( 1 );
    });
  });
});
