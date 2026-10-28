/*
 * Copyright © 2017-2026 CESSDA ERIC (support@cessda.eu)
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
import { MissingTranslationHandler, MissingTranslationHandlerParams, TranslateLoader } from '@ngx-translate/core';
import { from, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export const translationNotFoundMessage = 'translation-not-found';

export class MissingTranslationHandlerImpl implements MissingTranslationHandler {
  handle(params: MissingTranslationHandlerParams): string {
    return `${translationNotFoundMessage}[${params.key}]`;
  }
}

/**
 * Loads the translations from lazy chunks of the bundle. The chunks are named after a hash of
 * their content, so a changed translation gets a new URL and is never served from a stale cache.
 */
export class BundledTranslateLoader implements TranslateLoader {
  getTranslation(lang: string): Observable<Record<string, unknown>> {
    return from(import(`../../../i18n/${lang}.json`)).pipe(
      map((translations: { default: Record<string, unknown> }) => translations.default),
    );
  }
}

export function translatePartialLoader(): TranslateLoader {
  return new BundledTranslateLoader();
}

export function missingTranslationHandler(): MissingTranslationHandler {
  return new MissingTranslationHandlerImpl();
}
