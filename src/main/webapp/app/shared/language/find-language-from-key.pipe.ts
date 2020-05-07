import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'findLanguageFromKey' })
export class FindLanguageFromKeyPipe implements PipeTransform {
  private languages: { [key: string]: { name: string; rtl?: boolean } } = {
    al: { name: 'Shqip' },
    cs: { name: 'Český' },
    da: { name: 'Dansk' },
    nl: { name: 'Nederlands' },
    en: { name: 'English' },
    et: { name: 'Eesti' },
    fi: { name: 'Suomi' },
    fr: { name: 'Français' },
    de: { name: 'Deutsch' },
    hu: { name: 'Magyar' },
    it: { name: 'Italiano' },
    ja: { name: '日本語' },
    pl: { name: 'Polski' },
    'pt-pt': { name: 'Português' },
    ro: { name: 'Română' },
    ru: { name: 'Русский' },
    sk: { name: 'Slovenský' },
    sr: { name: 'Srpski' },
    es: { name: 'Español' },
    sv: { name: 'Svenska' }
    // jhipster-needle-i18n-language-key-pipe - JHipster will add/remove languages in this object
  };

  transform(lang: string): string {
    return this.languages[lang].name;
  }
}
