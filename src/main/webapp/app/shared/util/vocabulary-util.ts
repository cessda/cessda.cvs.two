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

import {IVocabulary} from 'app/shared/model/vocabulary.model';
import {ICode} from 'app/shared/model/code.model';
import {LanguageIso} from 'app/shared/model/enumerations/language-iso.model';
import {IVersion} from 'app/shared/model/version.model';
import {IConcept} from 'app/shared/model/concept.model';
import {AppScope} from 'app/shared/model/enumerations/app-scope.model';
import {VocabularyLanguageFromKeyPipe} from 'app/shared';

export default class VocabularyUtil {
  static getTitleDefByLangIso(item: IVocabulary | ICode, langIso: string): string[] {
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

  static getStatus(vocab: IVocabulary): String {
    return vocab.status!;
  }

  static getSlVersionOfVocabulary(vocab: IVocabulary): IVersion {
    return vocab.versions!.filter(v => v.itemType === 'SL')[0];
  }

  static parseVersionNumber(vnumber: string): any {
    const regex = /^([0-9]+)\.([0-9]+)(?:\.([0-9]+))?/g;
    const matches = regex.exec(vnumber);
    if (matches) {
        return {
          'sl': matches[1] + '.' + matches[2] + '.' + '0',
          'sl-major': Number(matches[1]),
          'sl-minor': Number(matches[2]),
          'tl': Number(matches[3])
        };
    }
    throw new Error('Invalid version number format');
  }

  static getSlVersionNumber(vnumber: string): string {
    return this.parseVersionNumber(vnumber)['sl'];
  }

  static getSlMajorMinorVersionNumber(vnumber: string): string {
    const vnumberParsed = this.parseVersionNumber(vnumber);
    return vnumberParsed['sl-major'] + '.' + vnumberParsed['sl-minor'];
  }

  static getSlMajorVersionNumber(vnumber: string): number {
    return this.parseVersionNumber(vnumber)['sl-major'];
  }

  static getSlMinorVersionNumber(vnumber: string): number {
    return this.parseVersionNumber(vnumber)['sl-minor'];
  }

  static getTlVersionNumber(vnumber: string): number {
    return this.parseVersionNumber(vnumber)['tl'];
  }

  static getSlVersionNumberOfVocabulary(vocab: IVocabulary): string {
    const slVersion = this.getSlVersionOfVocabulary(vocab);
    return this.getSlVersionNumber(slVersion.number!);
  }

  static getSlMajorVersionNumberOfVersion(version: IVersion): number {
    if (version.number) {
      return this.parseVersionNumber(version.number)['sl-major'];
    }
    return -1;
  }

  static getSlMinorVersionNumberOfVersion(version: IVersion): number {
    if (version.number) {
      return this.parseVersionNumber(version.number)['sl-minor'];
    }
    return -1;
  }

  static getVersionByLang(vocab: IVocabulary): IVersion {
    return vocab.versions!.filter(v => v.language === vocab.selectedLang)[0];
  }

  static getVersionByLangAndNumber(vocab: IVocabulary, versionNumber?: string): IVersion {
    if (versionNumber) {
      versionNumber = this.threeDigitVersionNumber(versionNumber);
      return vocab.versions!.filter(v => v.language === vocab.selectedLang && this.threeDigitVersionNumber(v.number) === versionNumber)[0];
    } else if (vocab.selectedVersion) {
      vocab.selectedVersion = this.threeDigitVersionNumber(vocab.selectedVersion);
      return vocab.versions!.filter(v => v.language === vocab.selectedLang && this.threeDigitVersionNumber(v.number) === vocab.selectedVersion)[0];
    }
    return VocabularyUtil.getVersionByLang(vocab);
  }

  static isConceptHasChildren(notation: string, concepts: IConcept[]): boolean {
    return concepts.filter(c => c.parent === notation).length > 0;
  }

  static hasDeprecatedConcepts(concepts: IConcept[] | undefined): boolean {
    return concepts !== undefined ? concepts.filter(c => c.deprecated === true).length > 0 : false;
  }

  static compareNumbers(n1: Number, n2: Number): number {
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

  static compareArrays(a1:any, a2:any): number {
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

  static compareVersionNumber(v1: string, v2: string): number {
    v1 = this.parseVersionNumber(this.threeDigitVersionNumber(v1)!);
    v2 = this.parseVersionNumber(this.threeDigitVersionNumber(v2)!);
    const cmpResult = this.compareArrays(
      [
        v1['sl-major'],
        v1['sl-minor']
      ],
      [
        v2['sl-major'],
        v2['sl-minor']
      ]
    );
    if (cmpResult !== 0) {
      return cmpResult;
    }
    return this.compareArrays(
      [v1['tl']],
      [v2['tl']]
    );
  }

  static getAvailableCvLanguage(versions: IVersion[] | undefined): string[] {
    const availableLanguages = [];
    const takenLanguages = versions ? versions.map(v => v.language) : [];
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
        sortedLangIsos.push(LanguageIso[l]);
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
    sortedLangName.sort((a,b) => vocabLangPipeKey.transform(a).localeCompare(vocabLangPipeKey.transform(b)));
    sortedLangName.forEach(l => sortedLang.push(l));
    return sortedLang;
  }

  static getUniqueVersionLangs( versions: IVersion[], appScope:AppScope): string[] {
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

  static threeDigitVersionNumber(number: string | undefined): string | undefined {
    if (number && this.countMatches(number, '.') === 1) {
      number += '.0';
    }
    return number;
  }

  static convertVocabularyToThreeDigitVersionNumer(vocabulary: IVocabulary | undefined): void {
    if (vocabulary) {
      vocabulary.versionNumber = this.threeDigitVersionNumber(vocabulary.versionNumber);
      vocabulary.versions!.forEach(v => {
        v.number = this.threeDigitVersionNumber(v.number);
      });
    }
  }
}
