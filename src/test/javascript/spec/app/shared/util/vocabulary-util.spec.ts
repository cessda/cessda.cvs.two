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
import { createNewVersion, Version } from 'app/shared/model/version.model';
import { Concept } from 'app/shared/model/concept.model';

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

describe('VocabularyUtil version number parsing', () => {
  it('should parse a three part version number', () => {
    const parsed = VocabularyUtil.parseVersionNumber('2.1.3');
    expect(parsed.major).toBe(2);
    expect(parsed.minor).toBe(1);
    expect(parsed.patch).toBe(3);
  });

  it('should reject a string that is not a version number', () => {
    expect(() => VocabularyUtil.parseVersionNumber('not-a-version')).toThrow('Invalid version number format');
  });

  it('should pad a two part version number to three parts', () => {
    expect(VocabularyUtil.threeDigitVersionNumber('1.0')).toBe('1.0.0');
    expect(VocabularyUtil.threeDigitVersionNumber('1.0.0')).toBe('1.0.0');
    // only a single dot triggers padding
    expect(VocabularyUtil.threeDigitVersionNumber('1')).toBe('1');
  });

  it('should extract the individual parts of a version number', () => {
    expect(VocabularyUtil.getSlMajorVersionNumber('4.2.7')).toBe(4);
    expect(VocabularyUtil.getSlMinorVersionNumber('4.2.7')).toBe(2);
    expect(VocabularyUtil.getTlVersionNumber('4.2.7')).toBe(7);
    expect(VocabularyUtil.getSlMajorMinorVersionNumber('4.2.7')).toBe('4.2');
  });

  it('should count non overlapping occurrences of a substring', () => {
    expect(VocabularyUtil.countMatches('1.2.3', '.')).toBe(2);
    expect(VocabularyUtil.countMatches('aaa', 'aa')).toBe(1);
    expect(VocabularyUtil.countMatches('abc', 'x')).toBe(0);
    expect(VocabularyUtil.countMatches(undefined, '.')).toBe(0);
    expect(VocabularyUtil.countMatches('abc', undefined)).toBe(0);
  });
});

describe('VocabularyUtil comparison', () => {
  it('should compare two numbers', () => {
    expect(VocabularyUtil.compareNumbers(1, 2)).toBe(-1);
    expect(VocabularyUtil.compareNumbers(2, 2)).toBe(0);
    expect(VocabularyUtil.compareNumbers(3, 2)).toBe(1);
  });

  it('should pad the shorter array before comparing', () => {
    expect(VocabularyUtil.compareArrays([1], [1, 0])).toBe(0);
    expect(VocabularyUtil.compareArrays([1, 2], [1, 3])).toBe(-1);
    expect(VocabularyUtil.compareArrays([2], [1, 9])).toBe(1);
  });

  it('should compare major and minor before patch', () => {
    expect(VocabularyUtil.compareVersionNumbers('2.1', '1.9')).toBe(1);
    expect(VocabularyUtil.compareVersionNumbers('1.9', '2.1')).toBe(-1);
  });

  it('should treat versions as equal when either patch is zero', () => {
    // a zero patch marks an SL version, which compares equal to any TL sharing its major.minor
    expect(VocabularyUtil.compareVersionNumbers('2.0.0', '2.0.5')).toBe(0);
    expect(VocabularyUtil.compareVersionNumbers('2.0.5', '2.0.0')).toBe(0);
    // with both patches set, the patch decides
    expect(VocabularyUtil.compareVersionNumbers('2.0.1', '2.0.2')).toBe(-1);
  });
});

describe('VocabularyUtil vocabulary versions', () => {
  const slVersion: Version = { ...createNewVersion(1), itemType: 'SL', language: 'en', number: '2.0.0' };
  const tlVersion: Version = { ...createNewVersion(2), itemType: 'TL', language: 'de', number: '2.0.1' };

  const vocabWithVersions = (): Vocabulary =>
    createNewVocabulary({
      status: Status.PUBLISHED,
      selectedLang: 'en',
      versions: [slVersion, tlVersion],
    });

  it('should return the status of the vocabulary', () => {
    expect(VocabularyUtil.getStatus(vocabWithVersions())).toBe(Status.PUBLISHED);
  });

  it('should pick the SL version out of the versions', () => {
    expect(VocabularyUtil.getSlVersionOfVocabulary(vocabWithVersions())).toBe(slVersion);
  });

  it('should return the SL version number of the vocabulary', () => {
    expect(VocabularyUtil.getSlVersionNumberOfVocabulary(vocabWithVersions())).toBe('2.0.0');
  });

  it('should return an empty SL version number when the SL version has none', () => {
    const vocab = createNewVocabulary({ versions: [{ ...createNewVersion(1), itemType: 'SL', number: undefined }] });
    expect(VocabularyUtil.getSlVersionNumberOfVocabulary(vocab)).toBe('');
  });

  it('should return the major and minor parts of a version', () => {
    expect(VocabularyUtil.getSlMajorVersionNumberOfVersion(tlVersion)).toBe(2);
    expect(VocabularyUtil.getSlMinorVersionNumberOfVersion(tlVersion)).toBe(0);
  });

  it('should report -1 for a version without a number', () => {
    const numberless: Version = { ...createNewVersion(3), number: undefined };
    expect(VocabularyUtil.getSlMajorVersionNumberOfVersion(numberless)).toBe(-1);
    expect(VocabularyUtil.getSlMinorVersionNumberOfVersion(numberless)).toBe(-1);
  });

  it('should find the version matching the selected language', () => {
    expect(VocabularyUtil.getVersionByLang(vocabWithVersions())).toBe(slVersion);
  });

  it('should find a version by language and an explicit version number', () => {
    // the requested number is padded to three digits before matching
    expect(VocabularyUtil.getVersionByLangAndNumber(vocabWithVersions(), '2.0')).toBe(slVersion);
  });

  it('should fall back to the selected version when no number is given', () => {
    const vocab = createNewVocabulary({ selectedLang: 'de', selectedVersion: '2.0.1', versions: [slVersion, tlVersion] });
    expect(VocabularyUtil.getVersionByLangAndNumber(vocab)).toBe(tlVersion);
  });

  it('should pad every version number of a vocabulary to three digits', () => {
    const vocab = createNewVocabulary({
      versionNumber: '3.1',
      versions: [{ ...createNewVersion(1), itemType: 'SL', number: '3.1' }],
    });
    VocabularyUtil.convertVocabularyToThreeDigitVersionNumer(vocab);
    expect(vocab.versionNumber).toBe('3.1.0');
    expect(vocab.versions[0].number).toBe('3.1.0');
  });

  it('should detect whether a version belongs to a bundle', () => {
    expect(VocabularyUtil.isAnyVersionInBundle([slVersion, tlVersion], '2.0.1')).toBe(true);
    expect(VocabularyUtil.isAnyVersionInBundle([slVersion, tlVersion], '9.9.9')).toBe(false);
    expect(VocabularyUtil.isAnyVersionInBundle([], '2.0.0')).toBe(false);
  });
});

describe('VocabularyUtil concepts', () => {
  const concepts: Concept[] = [
    { notation: 'root', deprecated: false },
    { notation: 'child', parent: 'root', deprecated: false },
    { notation: 'gone', deprecated: true },
  ];

  it('should report whether a concept has children', () => {
    expect(VocabularyUtil.isConceptHasChildren('root', concepts)).toBe(true);
    expect(VocabularyUtil.isConceptHasChildren('child', concepts)).toBe(false);
  });

  it('should report whether any concept is deprecated', () => {
    expect(VocabularyUtil.hasDeprecatedConcepts(concepts)).toBe(true);
    expect(VocabularyUtil.hasDeprecatedConcepts([concepts[0], concepts[1]])).toBe(false);
  });
});

describe('VocabularyUtil languages', () => {
  it('should put the source language first and sort the rest by ISO order', () => {
    // bg precedes de in the LanguageIso enum
    expect(VocabularyUtil.sortLangByEnum(['de', 'en', 'bg'], 'en')).toEqual(['en', 'bg', 'de']);
  });

  it('should exclude languages already taken by a version', () => {
    const versions: Version[] = [{ ...createNewVersion(1), language: 'en' }];
    const available = VocabularyUtil.getAvailableCvLanguage(versions);
    expect(available).not.toContain('en');
    expect(available).toContain('de');
  });

  it('should return the version number for a language', () => {
    const vocab = createNewVocabulary({ versionEn: '1.2.0', versionDe: '1.2.1' });
    expect(VocabularyUtil.getVersionNumberByLangIso(vocab, 'en')).toBe('1.2.0');
    expect(VocabularyUtil.getVersionNumberByLangIso(vocab, 'de')).toBe('1.2.1');
  });

  it('should reject an unknown language code', () => {
    const vocab = createNewVocabulary({});
    expect(() => VocabularyUtil.getVersionNumberByLangIso(vocab, 'xx')).toThrow(TypeError);
  });
});
