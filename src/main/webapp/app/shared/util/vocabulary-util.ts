import { IVocabulary } from 'app/shared/model/vocabulary.model';
import { ICode } from 'app/shared/model/code.model';
import { LanguageIso } from 'app/shared/model/enumerations/language-iso.model';
import { IVersion } from 'app/shared/model/version.model';
import { IConcept } from 'app/shared/model/concept.model';

export default class VocabularyUtil {
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

  static getVocabularyTitleBySelectedLang(vocab: IVocabulary): string {
    switch (vocab.selectedLang) {
      case 'sq':
        return vocab.titleSq;
      case 'bs':
        return vocab.titleBs;
      case 'bg':
        return vocab.titleBg;
      case 'hr':
        return vocab.titleHr;
      case 'cs':
        return vocab.titleCs;
      case 'da':
        return vocab.titleDa;
      case 'nl':
        return vocab.titleNl;
      case 'en':
        return vocab.titleEn;
      case 'et':
        return vocab.titleEt;
      case 'fi':
        return vocab.titleFi;
      case 'fr':
        return vocab.titleFr;
      case 'de':
        return vocab.titleDe;
      case 'el':
        return vocab.titleEl;
      case 'hu':
        return vocab.titleHu;
      case 'it':
        return vocab.titleIt;
      case 'ja':
        return vocab.titleJa;
      case 'lt':
        return vocab.titleLt;
      case 'mk':
        return vocab.titleMk;
      case 'no':
        return vocab.titleNo;
      case 'pl':
        return vocab.titlePl;
      case 'pt':
        return vocab.titlePt;
      case 'ro':
        return vocab.titleRo;
      case 'ru':
        return vocab.titleRu;
      case 'sr':
        return vocab.titleSr;
      case 'sk':
        return vocab.titleSk;
      case 'sl':
        return vocab.titleSl;
      case 'es':
        return vocab.titleEs;
      case 'sv':
        return vocab.titleSv;
      default:
        return vocab.titleEn;
    }
  }

  static getVocabularyDefinitionBySelectedLang(vocab: IVocabulary): string {
    switch (vocab.selectedLang) {
      case 'sq':
        return vocab.definitionSq;
      case 'bs':
        return vocab.definitionBs;
      case 'bg':
        return vocab.definitionBg;
      case 'hr':
        return vocab.definitionHr;
      case 'cs':
        return vocab.definitionCs;
      case 'da':
        return vocab.definitionDa;
      case 'nl':
        return vocab.definitionNl;
      case 'en':
        return vocab.definitionEn;
      case 'et':
        return vocab.definitionEt;
      case 'fi':
        return vocab.definitionFi;
      case 'fr':
        return vocab.definitionFr;
      case 'de':
        return vocab.definitionDe;
      case 'el':
        return vocab.definitionEl;
      case 'hu':
        return vocab.definitionHu;
      case 'it':
        return vocab.definitionIt;
      case 'ja':
        return vocab.definitionJa;
      case 'lt':
        return vocab.definitionLt;
      case 'mk':
        return vocab.definitionMk;
      case 'no':
        return vocab.definitionNo;
      case 'pl':
        return vocab.definitionPl;
      case 'pt':
        return vocab.definitionPt;
      case 'ro':
        return vocab.definitionRo;
      case 'ru':
        return vocab.definitionRu;
      case 'sr':
        return vocab.definitionSr;
      case 'sk':
        return vocab.definitionSk;
      case 'sl':
        return vocab.definitionSl;
      case 'es':
        return vocab.definitionEs;
      case 'sv':
        return vocab.definitionSv;
      default:
        return vocab.definitionEn;
    }
  }

  static getCodeTitleBySelectedLang(code: ICode, selectedLang: string): string {
    switch (selectedLang) {
      case 'sq':
        return code.titleSq;
      case 'bs':
        return code.titleBs;
      case 'bg':
        return code.titleBg;
      case 'hr':
        return code.titleHr;
      case 'cs':
        return code.titleCs;
      case 'da':
        return code.titleDa;
      case 'nl':
        return code.titleNl;
      case 'en':
        return code.titleEn;
      case 'et':
        return code.titleEt;
      case 'fi':
        return code.titleFi;
      case 'fr':
        return code.titleFr;
      case 'de':
        return code.titleDe;
      case 'el':
        return code.titleEl;
      case 'hu':
        return code.titleHu;
      case 'it':
        return code.titleIt;
      case 'ja':
        return code.titleJa;
      case 'lt':
        return code.titleLt;
      case 'mk':
        return code.titleMk;
      case 'no':
        return code.titleNo;
      case 'pl':
        return code.titlePl;
      case 'pt':
        return code.titlePt;
      case 'ro':
        return code.titleRo;
      case 'ru':
        return code.titleRu;
      case 'sr':
        return code.titleSr;
      case 'sk':
        return code.titleSk;
      case 'sl':
        return code.titleSl;
      case 'es':
        return code.titleEs;
      case 'sv':
        return code.titleSv;
      default:
        return code.titleEn;
    }
  }

  static getCodeDefinitionBySelectedLang(code: ICode, selectedLang: string): string {
    switch (selectedLang) {
      case 'sq':
        return code.definitionSq;
      case 'bs':
        return code.definitionBs;
      case 'bg':
        return code.definitionBg;
      case 'hr':
        return code.definitionHr;
      case 'cs':
        return code.definitionCs;
      case 'da':
        return code.definitionDa;
      case 'nl':
        return code.definitionNl;
      case 'en':
        return code.definitionEn;
      case 'et':
        return code.definitionEt;
      case 'fi':
        return code.definitionFi;
      case 'fr':
        return code.definitionFr;
      case 'de':
        return code.definitionDe;
      case 'el':
        return code.definitionEl;
      case 'hu':
        return code.definitionHu;
      case 'it':
        return code.definitionIt;
      case 'ja':
        return code.definitionJa;
      case 'lt':
        return code.definitionLt;
      case 'mk':
        return code.definitionMk;
      case 'no':
        return code.definitionNo;
      case 'pl':
        return code.definitionPl;
      case 'pt':
        return code.definitionPt;
      case 'ro':
        return code.definitionRo;
      case 'ru':
        return code.definitionRu;
      case 'sr':
        return code.definitionSr;
      case 'sk':
        return code.definitionSk;
      case 'sl':
        return code.definitionSl;
      case 'es':
        return code.definitionEs;
      case 'sv':
        return code.definitionSv;
      default:
        return code.definitionEn;
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
}
