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
import { Vocabulary } from 'app/shared/model/vocabulary.model';
import { Code } from 'app/shared/model/code.model';
import { LanguageIso } from 'app/shared/model/enumerations/language-iso.model';
import { Version } from 'app/shared/model/version.model';
import { Concept } from 'app/shared/model/concept.model';
import { AppScope } from 'app/shared/model/enumerations/app-scope.model';
import { VocabularyLanguageFromKeyPipe } from 'app/shared';
import { VersionNumber } from 'app/shared/model/version-number.model';

export default class VocabularyUtil {
  static getTitleDefByLangIso(item: Vocabulary | Code, langIso: string): (string | undefined)[] {
    switch (langIso) {
      case 'sq':
        return [item.titleSq, item.definitionSq, item.versionSq];
      case 'bs':
        return [item.titleBs, item.definitionBs, item.versionBs];
      case 'bg':
        return [item.titleBg, item.definitionBg, item.versionBg];
      case 'hr':
        return [item.titleHr, item.definitionHr, item.versionHr];
      case 'cs':
        return [item.titleCs, item.definitionCs, item.versionCs];
      case 'da':
        return [item.titleDa, item.definitionDa, item.versionDa];
      case 'nl':
        return [item.titleNl, item.definitionNl, item.versionNl];
      case 'en':
        return [item.titleEn, item.definitionEn, item.versionEn];
      case 'et':
        return [item.titleEt, item.definitionEt, item.versionEt];
      case 'fi':
        return [item.titleFi, item.definitionFi, item.versionFi];
      case 'fr':
        return [item.titleFr, item.definitionFr, item.versionFr];
      case 'de':
        return [item.titleDe, item.definitionDe, item.versionDe];
      case 'el':
        return [item.titleEl, item.definitionEl, item.versionEl];
      case 'hu':
        return [item.titleHu, item.definitionHu, item.versionHu];
      case 'it':
        return [item.titleIt, item.definitionIt, item.versionIt];
      case 'ja':
        return [item.titleJa, item.definitionJa, item.versionJa];
      case 'lt':
        return [item.titleLt, item.definitionLt, item.versionLt];
      case 'mk':
        return [item.titleMk, item.definitionMk, item.versionMk];
      case 'no':
        return [item.titleNo, item.definitionNo, item.versionNo];
      case 'pl':
        return [item.titlePl, item.definitionPl, item.versionPl];
      case 'pt':
        return [item.titlePt, item.definitionPt, item.versionPt];
      case 'ro':
        return [item.titleRo, item.definitionRo, item.versionRo];
      case 'ru':
        return [item.titleRu, item.definitionRu, item.versionRu];
      case 'sr':
        return [item.titleSr, item.definitionSr, item.versionSr];
      case 'sk':
        return [item.titleSk, item.definitionSk, item.versionSk];
      case 'sl':
        return [item.titleSl, item.definitionSl, item.versionSl];
      case 'es':
        return [item.titleEs, item.definitionEs, item.versionEs];
      case 'sv':
        return [item.titleSv, item.definitionSv, item.versionSv];
      default:
        return [item.titleEn, item.definitionEn, item.versionEn];
    }
  }

  static getVersionNumberByLangIso(item: Vocabulary | Code, langIso: string): string | undefined {
    switch (langIso) {
      case 'sq':
        return item.versionSq;
      case 'bs':
        return item.versionBs;
      case 'bg':
        return item.versionBg;
      case 'hr':
        return item.versionHr;
      case 'cs':
        return item.versionCs;
      case 'da':
        return item.versionDa;
      case 'nl':
        return item.versionNl;
      case 'en':
        return item.versionEn;
      case 'et':
        return item.versionEt;
      case 'fi':
        return item.versionFi;
      case 'fr':
        return item.versionFr;
      case 'de':
        return item.versionDe;
      case 'el':
        return item.versionEl;
      case 'hu':
        return item.versionHu;
      case 'it':
        return item.versionIt;
      case 'ja':
        return item.versionJa;
      case 'lt':
        return item.versionLt;
      case 'mk':
        return item.versionMk;
      case 'no':
        return item.versionNo;
      case 'pl':
        return item.versionPl;
      case 'pt':
        return item.versionPt;
      case 'ro':
        return item.versionRo;
      case 'ru':
        return item.versionRu;
      case 'sr':
        return item.versionSr;
      case 'sk':
        return item.versionSk;
      case 'sl':
        return item.versionSl;
      case 'es':
        return item.versionEs;
      case 'sv':
        return item.versionSv;
      default:
        throw new TypeError(`Invalid langIso ${langIso}`);
    }
  }

  static getStatus(vocab: Vocabulary): string {
    return vocab.status;
  }

  static getSlVersionOfVocabulary(vocab: Vocabulary): Version {
    return vocab.versions.filter(v => v.itemType === 'SL')[0];
  }

  static parseVersionNumber(vnumber: string): VersionNumber {
    const regex = /^([0-9]+)\.([0-9]+)(?:\.([0-9]+))?/g;
    const matches = regex.exec(vnumber);
    if (matches) {
      return new VersionNumber(Number(matches[1]), Number(matches[2]), Number(matches[3]));
    }
    throw new Error('Invalid version number format');
  }

  static getSlVersionNumber(vnumber: string): string {
    return this.parseVersionNumber(vnumber).toString();
  }

  static getSlMajorMinorVersionNumber(vnumber: string): string {
    const vnumberParsed = this.parseVersionNumber(vnumber);
    return `${vnumberParsed.major}.${vnumberParsed.minor}`;
  }

  static getSlMajorVersionNumber(vnumber: string): number {
    return this.parseVersionNumber(vnumber).major;
  }

  static getSlMinorVersionNumber(vnumber: string): number {
    return this.parseVersionNumber(vnumber).minor;
  }

  static getTlVersionNumber(vnumber: string): number {
    return this.parseVersionNumber(vnumber).patch;
  }

  static getSlVersionNumberOfVocabulary(vocab: Vocabulary): string {
    const slVersion = this.getSlVersionOfVocabulary(vocab);
    if (slVersion.number) {
      return this.getSlVersionNumber(slVersion.number);
    } else {
      return '';
    }
  }

  static getSlMajorVersionNumberOfVersion(version: Version): number {
    if (version.number) {
      return this.parseVersionNumber(version.number).major;
    }
    return -1;
  }

  static getSlMinorVersionNumberOfVersion(version: Version): number {
    if (version.number) {
      return this.parseVersionNumber(version.number).minor;
    }
    return -1;
  }

  static getVersionByLang(vocab: Vocabulary): Version {
    return vocab.versions.filter(v => v.language === vocab.selectedLang)[0];
  }

  static getVersionByLangAndNumber(vocab: Vocabulary, versionNumber?: string): Version {
    if (versionNumber) {
      versionNumber = this.threeDigitVersionNumber(versionNumber);
      return vocab.versions.filter(v => v.language === vocab.selectedLang && this.threeDigitVersionNumber(v.number!) === versionNumber)[0];
    } else if (vocab.selectedVersion) {
      vocab.selectedVersion = this.threeDigitVersionNumber(vocab.selectedVersion);
      return vocab.versions.filter(
        v => v.language === vocab.selectedLang && this.threeDigitVersionNumber(v.number!) === vocab.selectedVersion,
      )[0];
    }
    return VocabularyUtil.getVersionByLang(vocab);
  }

  static isConceptHasChildren(notation: string, concepts: Concept[]): boolean {
    return concepts.filter(c => c.parent === notation).length > 0;
  }

  static hasDeprecatedConcepts(concepts: Concept[]): boolean {
    return concepts.filter(c => c.deprecated === true).length > 0;
  }

  static compareNumbers(n1: number, n2: number): number {
    if (n1 < n2) {
      return -1;
    } else {
      if (n1 === n2) {
        return 0;
      } else {
        return 1;
      }
    }
  }

  static compareArrays(a1: number[], a2: number[]): number {
    while (a1.length < a2.length) a1.push(0);
    while (a2.length < a1.length) a2.push(0);
    let cmpResult = 0;
    for (let i = 0; i < a1.length; i++) {
      cmpResult = this.compareNumbers(a1[i], a2[i]);
      if (cmpResult !== 0) {
        return cmpResult;
      }
    }
    return cmpResult;
  }

  static compareVersionNumbers(v1: string, v2: string): number {
    const v1Parsed = this.parseVersionNumber(this.threeDigitVersionNumber(v1));
    const v2Parsed = this.parseVersionNumber(this.threeDigitVersionNumber(v2));
    const cmpResult = this.compareArrays([v1Parsed.major, v1Parsed.minor], [v2Parsed.major, v2Parsed.minor]);
    if (cmpResult !== 0) {
      return cmpResult;
    }
    if (v1Parsed.patch === 0 || v2Parsed.patch === 0) {
      return 0;
    }
    return this.compareArrays([v1Parsed.patch], [v2Parsed.patch]);
  }

  static compareSlVersionNumbers(v1: string, v2: string): number {
    return this.compareVersionNumbers(this.getSlVersionNumber(v1), this.getSlVersionNumber(v2));
  }

  static getAvailableCvLanguage(versions: Version[]): string[] {
    const availableLanguages = [];
    const takenLanguages = versions.map(v => v.language);
    for (const langIso in LanguageIso) {
      if (parseInt(langIso, 10) >= 0) {
        if (!takenLanguages.includes(LanguageIso[langIso])) {
          availableLanguages.push(LanguageIso[langIso]);
        }
      }
    }
    return availableLanguages;
  }

  static sortLangByEnum(languages: string[], sourceLang: string): string[] {
    const sortedLang: string[] = [sourceLang];
    const sortedLangIsos: LanguageIso[] = [];
    // convert languages to enum so it can be sorted
    languages.forEach(l => {
      if (l !== sourceLang) {
        sortedLangIsos.push(LanguageIso[l as keyof typeof LanguageIso]);
      }
    });
    sortedLangIsos.sort((a, b) => a - b);
    sortedLangIsos.forEach(lIso => sortedLang.push(LanguageIso[lIso]));
    return sortedLang;
  }

  static sortLangByName(languages: string[], sourceLang: string | null): string[] {
    const sortedLang: string[] = sourceLang ? [sourceLang] : [];
    const sortedLangName: string[] = [];
    const vocabLangPipeKey: VocabularyLanguageFromKeyPipe = new VocabularyLanguageFromKeyPipe();
    // add languages for sorting
    languages.forEach(l => {
      if (l !== sourceLang) {
        sortedLangName.push(l);
      }
    });
    sortedLangName.sort((a, b) => vocabLangPipeKey.transform(a).localeCompare(vocabLangPipeKey.transform(b)));
    sortedLangName.forEach(l => sortedLang.push(l));
    return sortedLang;
  }

  static getUniqueVersionLangs(versions: Version[], appScope: AppScope): string[] {
    let uniqueLang: string[] = [];
    versions.forEach(v => {
      if (appScope === AppScope.EDITOR) {
        if (!uniqueLang.some(l => l === v.language)) {
          uniqueLang.push(v.language!);
        }
      } else {
        if (v.number!.startsWith(versions[0].number!) && !uniqueLang.some(l => l === v.language)) {
          uniqueLang.push(v.language!);
        }
      }
    });
    // sort only the SL & TLs in the current version
    uniqueLang = VocabularyUtil.sortLangByEnum(uniqueLang, uniqueLang[0]);

    if (appScope === AppScope.PUBLICATION) {
      versions.forEach(v => {
        if (!v.number!.startsWith(versions[0].number!) && !uniqueLang.some(l => l === v.language)) {
          uniqueLang.push(v.language!);
        }
      });
    }
    return uniqueLang;
  }

  static countMatches(str: string | undefined, sub: string | undefined): number {
    if (!(str && sub)) {
      return 0;
    }
    let count = 0;
    let idx = 0;
    while ((idx = str.indexOf(sub, idx)) >= 0) {
      count++;
      idx += sub.length;
    }
    return count;
  }

  static threeDigitVersionNumber(number: string): string {
    if (this.countMatches(number, '.') === 1) {
      return number + '.0';
    } else {
      return number;
    }
  }

  static convertVocabularyToThreeDigitVersionNumer(vocabulary: Vocabulary): void {
    if (vocabulary.versionNumber) {
      vocabulary.versionNumber = this.threeDigitVersionNumber(vocabulary.versionNumber);
      if (vocabulary.versions) {
        vocabulary.versions.forEach(v => {
          if (v.number) {
            v.number = this.threeDigitVersionNumber(v.number);
          }
        });
      }
    }
  }

  /**
   * Returns true if there's at least one version among versions which belongs to the bundle. False otherwise.
   *
   * @param vocab
   * @param lang
   * @param versions
   * @param bundle
   * @returns
   */
  static isAnyVersionInBundle(versions: Version[], bundle: string): boolean {
    for (const version of versions) {
      if (version.number === bundle) {
        return true;
      }
    }
    return false;
  }
}
