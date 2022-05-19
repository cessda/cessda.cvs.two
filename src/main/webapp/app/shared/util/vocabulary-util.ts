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

  static getSlVersion(vocab: IVocabulary): IVersion {
    return vocab.versions!.filter(v => v.itemType === 'SL')[0];
  }

  static getMajorVersionNumber(version: IVersion): number {
    if (version.number) {
      const regex = /^([0-9]+)\./g;
      const matches = regex.exec(version.number);
      if (matches) {
          return Number(matches[1]);
      }
    }
    return -1;
  }

  static getMinorVersionNumber(version: IVersion): number {
    if (version.number) {
      const regex = /^([0-9]+)\.([0-9]+)/g;
      const matches = regex.exec(version.number);
      if (matches) {
          return Number(matches[2]);
      }
    }
    return -1;
  }

  static getVersionByLang(vocab: IVocabulary): IVersion {
    return vocab.versions!.filter(v => v.language === vocab.selectedLang)[0];
  }

  static getVersionByLangAndNumber(vocab: IVocabulary, versionNumber?: string): IVersion {
    if (versionNumber) {
      return vocab.versions!.filter(v => v.language === vocab.selectedLang && v.number === versionNumber)[0];
    } else if (vocab.selectedVersion) {
      return vocab.versions!.filter(v => v.language === vocab.selectedLang && v.number === vocab.selectedVersion)[0];
    }
    return VocabularyUtil.getVersionByLang(vocab);
  }

  static isConceptHasChildren(notation: string, concepts: IConcept[]): boolean {
    return concepts.filter(c => c.parent === notation).length > 0;
  }

  static getSlVersionByVersionNumber(vnumber: string): string {
    if (vnumber.match(/\./g)!.length === 2) {
      const index = vnumber.lastIndexOf('.');
      return vnumber.substring(0, index);
    }
    return vnumber;
  }

  static compareVersionNumber(v1: string, v2: string): number {
    const v1parts = v1.split('.'),
      v2parts = v2.split('.');

    while (v1parts.length < v2parts.length) v1parts.push('0');
    while (v2parts.length < v1parts.length) v2parts.push('0');

    for (let i = 0; i < v1parts.length; ++i) {
      if (v2parts.length === i) {
        return 1;
      }

      if (v1parts[i] === v2parts[i]) {
        continue;
      } else if (v1parts[i] > v2parts[i]) {
        return 1;
      } else {
        return -1;
      }
    }

    if (v1parts.length !== v2parts.length) {
      return -1;
    }

    return 0;
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
}
