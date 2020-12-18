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
    it('should get the vocabulary version by selected lang', () => {
      for (const langIso in LanguageIso) {
        if (parseInt(langIso, 10) >= 0) {
          const value = VocabularyUtil.getVocabularyVersionBySelectedLang(vocab, langIso);
          expect( value ).toBe( 'AAA');
        }
      }
    });
    it('should get the vocabulary title by selected lang', () => {
      for (const langIso in LanguageIso) {
        if (parseInt(langIso, 10) >= 0) {
          vocab.selectedLang = langIso;
          const value = VocabularyUtil.getVocabularyTitleBySelectedLang(vocab);
          expect( value ).toBe( 'AAA');
        }
      }
    });
    it('should get the vocabulary definition by selected lang', () => {
      for (const langIso in LanguageIso) {
        if (parseInt(langIso, 10) >= 0) {
          vocab.selectedLang = langIso;
          const value = VocabularyUtil.getVocabularyDefinitionBySelectedLang(vocab);
          expect( value ).toBe( 'AAA');
        }
      }
    });
    it('should get the Code Title by selected lang', () => {
      for (const langIso in LanguageIso) {
        if (parseInt(langIso, 10) >= 0) {
          const value = VocabularyUtil.getCodeTitleBySelectedLang(code, langIso);
          expect( value ).toBe( 'AAA');
        }
      }
    });
    it('should get the Code Definition by selected lang', () => {
      for (const langIso in LanguageIso) {
        if (parseInt(langIso, 10) >= 0) {
          const value = VocabularyUtil.getCodeDefinitionBySelectedLang(code, langIso);
          expect( value ).toBe( 'AAA');
        }
      }
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
