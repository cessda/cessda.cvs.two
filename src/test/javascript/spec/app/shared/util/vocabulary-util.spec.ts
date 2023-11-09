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
import { LanguageIso } from 'app/shared/model/enumerations/language-iso.model';
import moment from 'moment';
import { createNewVocabulary, Status, Vocabulary } from 'app/shared/model/vocabulary.model';
import VocabularyUtil from 'app/shared/util/vocabulary-util';
import { Code, createNewCode } from 'app/shared/model/code.model';

describe('Vocabulary Util Tests', () => {
  let vocab: Vocabulary;
  let code: Code;
  beforeEach(() => {
    vocab = createNewVocabulary({
      id: 0,
      status: Status.DRAFT,
      uri: 'AAA',
      notation: 'AAA',
      versionNumber: 'AAA',
      initialPublication: 0,
      previousPublication: 0,
      archived: false,
      withdrawn: false,
      discoverable: false,
      selectedLang: 'en',
      selectedCode: 'AAA',
      selectedVersion: '1.0',
      agencyId: 0,
      agencyName: 'AAA',
      agencyLogo: 'AAA',
      agencyLink: 'AAA',
      publicationDate: moment(),
      lastModified: moment(),
      notes: 'AAA',
      versionSq: 'AAA',
      titleSq: 'AAA',
      definitionSq: 'AAA',
      versionBs: 'AAA',
      titleBs: 'AAA',
      definitionBs: 'AAA',
      versionBg: 'AAA',
      titleBg: 'AAA',
      definitionBg: 'AAA',
      versionHr: 'AAA',
      titleHr: 'AAA',
      definitionHr: 'AAA',
      versionCs: 'AAA',
      titleCs: 'AAA',
      definitionCs: 'AAA',
      versionDa: 'AAA',
      titleDa: 'AAA',
      definitionDa: 'AAA',
      versionNl: 'AAA',
      titleNl: 'AAA',
      definitionNl: 'AAA',
      versionEn: 'AAA',
      titleEn: 'AAA',
      definitionEn: 'AAA',
      versionEt: 'AAA',
      titleEt: 'AAA',
      definitionEt: 'AAA',
      versionFi: 'AAA',
      titleFi: 'AAA',
      definitionFi: 'AAA',
      versionFr: 'AAA',
      titleFr: 'AAA',
      definitionFr: 'AAA',
      versionDe: 'AAA',
      titleDe: 'AAA',
      definitionDe: 'AAA',
      versionEl: 'AAA',
      titleEl: 'AAA',
      definitionEl: 'AAA',
      versionHu: 'AAA',
      titleHu: 'AAA',
      definitionHu: 'AAA',
      versionIt: 'AAA',
      titleIt: 'AAA',
      definitionIt: 'AAA',
      versionJa: 'AAA',
      titleJa: 'AAA',
      definitionJa: 'AAA',
      versionLt: 'AAA',
      titleLt: 'AAA',
      definitionLt: 'AAA',
      versionMk: 'AAA',
      titleMk: 'AAA',
      definitionMk: 'AAA',
      versionNo: 'AAA',
      titleNo: 'AAA',
      definitionNo: 'AAA',
      versionPl: 'AAA',
      titlePl: 'AAA',
      definitionPl: 'AAA',
      versionPt: 'AAA',
      titlePt: 'AAA',
      definitionPt: 'AAA',
      versionRo: 'AAA',
      titleRo: 'AAA',
      definitionRo: 'AAA',
      versionRu: 'AAA',
      titleRu: 'AAA',
      definitionRu: 'AAA',
      versionSr: 'AAA',
      titleSr: 'AAA',
      definitionSr: 'AAA',
      versionSk: 'AAA',
      titleSk: 'AAA',
      definitionSk: 'AAA',
      versionSl: 'AAA',
      titleSl: 'AAA',
      definitionSl: 'AAA',
      versionEs: 'AAA',
      titleEs: 'AAA',
      definitionEs: 'AAA',
      versionSv: 'AAA',
      titleSv: 'AAA',
      definitionSv: 'AAA',
    });
    code = { ...createNewCode(), ...vocab };
  });
  describe('Vocabulary Util methods', () => {
    it('should get the item title and definition by selected lang', () => {
      for (const langIso in LanguageIso) {
        if (isNaN(Number(langIso))) {
          const vocabTitleDef = VocabularyUtil.getTitleDefByLangIso(vocab, langIso);
          const codeTitleDef = VocabularyUtil.getTitleDefByLangIso(code, langIso);
          expect(vocabTitleDef[0]).toBe('AAA');
          expect(vocabTitleDef[1]).toBe('AAA');
          expect(vocabTitleDef[2]).toBe('AAA');
          expect(codeTitleDef[0]).toBe('AAA');
          expect(codeTitleDef[1]).toBe('AAA');
          expect(codeTitleDef[2]).toBe('AAA');
        }
      }

      // check the default if
      const langIso = 'xx';
      const codeTitleDefDefault = VocabularyUtil.getTitleDefByLangIso(code, langIso);
      const vocabTitleDefDefault = VocabularyUtil.getTitleDefByLangIso(vocab, langIso);
      expect(codeTitleDefDefault[0]).toBe('AAA');
      expect(codeTitleDefDefault[1]).toBe('AAA');
      expect(codeTitleDefDefault[2]).toBe('AAA');
      expect(vocabTitleDefDefault[0]).toBe('AAA');
      expect(vocabTitleDefDefault[1]).toBe('AAA');
      expect(vocabTitleDefDefault[2]).toBe('AAA');
    });

    it('should compare version numbers', () => {
      expect(VocabularyUtil.compareVersionNumbers('1.0', '1.1')).toBe(-1);
      expect(VocabularyUtil.compareVersionNumbers('1.0', '1.0')).toBe(0);
      expect(VocabularyUtil.compareVersionNumbers('1.1', '1.0')).toBe(1);
    });
  });
});
