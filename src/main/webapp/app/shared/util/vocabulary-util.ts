import {IVocabulary} from 'app/shared/model/vocabulary.model';
import {ICode} from 'app/shared/model/code.model';
import {LanguageIso} from 'app/shared/model/enumerations/language-iso.model';
import {IVersion} from 'app/shared/model/version.model';
import {IConcept} from 'app/shared/model/concept.model';
import {AppScope} from 'app/shared/model/enumerations/app-scope.model';

export default class VocabularyUtil {
  static getTitleDefByLangIso(item: IVocabulary | ICode, langIso: string): string[] {
    switch (langIso) {
      case 'sq':
        return [item.titleSq, item.definitionSq];
      case 'bs':
        return [item.titleBs, item.definitionBs];
      case 'bg':
        return [item.titleBg, item.definitionBg];
      case 'hr':
        return [item.titleHr, item.definitionHr];
      case 'cs':
        return [item.titleCs, item.definitionCs];
      case 'da':
        return [item.titleDa, item.definitionDa];
      case 'nl':
        return [item.titleNl, item.definitionNl];
      case 'en':
        return [item.titleEn, item.definitionEn];
      case 'et':
        return [item.titleEt, item.definitionEt];
      case 'fi':
        return [item.titleFi, item.definitionFi];
      case 'fr':
        return [item.titleFr, item.definitionFr];
      case 'de':
        return [item.titleDe, item.definitionDe];
      case 'el':
        return [item.titleEl, item.definitionEl];
      case 'hu':
        return [item.titleHu, item.definitionHu];
      case 'it':
        return [item.titleIt, item.definitionIt];
      case 'ja':
        return [item.titleJa, item.definitionJa];
      case 'lt':
        return [item.titleLt, item.definitionLt];
      case 'mk':
        return [item.titleMk, item.definitionMk];
      case 'no':
        return [item.titleNo, item.definitionNo];
      case 'pl':
        return [item.titlePl, item.definitionPl];
      case 'pt':
        return [item.titlePt, item.definitionPt];
      case 'ro':
        return [item.titleRo, item.definitionRo];
      case 'ru':
        return [item.titleRu, item.definitionRu];
      case 'sr':
        return [item.titleSr, item.definitionSr];
      case 'sk':
        return [item.titleSk, item.definitionSk];
      case 'sl':
        return [item.titleSl, item.definitionSl];
      case 'es':
        return [item.titleEs, item.definitionEs];
      case 'sv':
        return [item.titleSv, item.definitionSv];
      default:
        return [item.titleEn, item.definitionEn];
    }
  }

  static getVocabularyVersionBySelectedLang(vocab: IVocabulary, lang: string): string {
    switch (lang) {
      case 'sq':
        return vocab.versionSq;
      case 'bs':
        return vocab.versionBs;
      case 'bg':
        return vocab.versionBg;
      case 'hr':
        return vocab.versionHr;
      case 'cs':
        return vocab.versionCs;
      case 'da':
        return vocab.versionDa;
      case 'nl':
        return vocab.versionNl;
      case 'en':
        return vocab.versionEn;
      case 'et':
        return vocab.versionEt;
      case 'fi':
        return vocab.versionFi;
      case 'fr':
        return vocab.versionFr;
      case 'de':
        return vocab.versionDe;
      case 'el':
        return vocab.versionEl;
      case 'hu':
        return vocab.versionHu;
      case 'it':
        return vocab.versionIt;
      case 'ja':
        return vocab.versionJa;
      case 'lt':
        return vocab.versionLt;
      case 'mk':
        return vocab.versionMk;
      case 'no':
        return vocab.versionNo;
      case 'pl':
        return vocab.versionPl;
      case 'pt':
        return vocab.versionPt;
      case 'ro':
        return vocab.versionRo;
      case 'ru':
        return vocab.versionRu;
      case 'sr':
        return vocab.versionSr;
      case 'sk':
        return vocab.versionSk;
      case 'sl':
        return vocab.versionSl;
      case 'es':
        return vocab.versionEs;
      case 'sv':
        return vocab.versionSv;
      default:
        return vocab.versionEn;
    }
  }

  static getLangIsoByEnum(langIsoEnum: LanguageIso): string {
    switch (langIsoEnum) {
      case LanguageIso.sq:
        return 'sq';
      case LanguageIso.bs:
        return 'bs';
      case LanguageIso.bg:
        return 'bg';
      case LanguageIso.hr:
        return 'hr';
      case LanguageIso.cs:
        return 'cs';
      case LanguageIso.da:
        return 'da';
      case LanguageIso.nl:
        return 'nl';
      case LanguageIso.en:
        return 'en';
      case LanguageIso.et:
        return 'et';
      case LanguageIso.fi:
        return 'fi';
      case LanguageIso.fr:
        return 'fr';
      case LanguageIso.de:
        return 'de';
      case LanguageIso.el:
        return 'el';
      case LanguageIso.hu:
        return 'hu';
      case LanguageIso.it:
        return 'it';
      case LanguageIso.ja:
        return 'ja';
      case LanguageIso.lt:
        return 'lt';
      case LanguageIso.mk:
        return 'mk';
      case LanguageIso.no:
        return 'no';
      case LanguageIso.pl:
        return 'pl';
      case LanguageIso.pt:
        return 'pt';
      case LanguageIso.ro:
        return 'ro';
      case LanguageIso.ru:
        return 'ru';
      case LanguageIso.sr:
        return 'sr';
      case LanguageIso.sk:
        return 'sk';
      case LanguageIso.sl:
        return 'sl';
      case LanguageIso.es:
        return 'es';
      case LanguageIso.sv:
        return 'sv';
      default:
        return 'en';
    }
  }

  static getLangIsoFormatted(lang: string | undefined): string {
    switch (lang) {
      case 'sq':
        return 'Albanian (sq)';
      case 'bs':
        return 'Bosnian (bs)';
      case 'bg':
        return 'Bulgarian (bg)';
      case 'hr':
        return 'Croatian (hr)';
      case 'cs':
        return 'Czech (cs)';
      case 'da':
        return 'Danish (da)';
      case 'nl':
        return 'Dutch (nl)';
      case 'en':
        return 'English (en)';
      case 'et':
        return 'Estonian (et)';
      case 'fi':
        return 'Finnish (fi)';
      case 'fr':
        return 'French (fr)';
      case 'de':
        return 'German (de)';
      case 'el':
        return 'Greek (el)';
      case 'hu':
        return 'Hungarian (hu)';
      case 'it':
        return 'Italian (it)';
      case 'ja':
        return 'Japanese (ja)';
      case 'lt':
        return 'Lithuanian (lt)';
      case 'mk':
        return 'Macedonian (mk)';
      case 'no':
        return 'Norwegian (no)';
      case 'pl':
        return 'Polish (pl)';
      case 'pt':
        return 'Portuguese (pt)';
      case 'ro':
        return 'Romanian (ro)';
      case 'ru':
        return 'Russian (ru)';
      case 'sr':
        return 'Serbian (sr)';
      case 'sk':
        return 'Slovak (sk)';
      case 'sl':
        return 'Slovenian (sl)';
      case 'es':
        return 'Spanish (es)';
      case 'sv':
        return 'Swedish (sv)';
      default:
        return 'Unknown';
    }
  }

  static getSlVersion(vocab: IVocabulary): IVersion {
    return vocab.versions!.filter(v => v.itemType === 'SL')[0];
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
    sortedLangIsos.forEach(lIso => sortedLang.push(VocabularyUtil.getLangIsoByEnum(lIso)));
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
