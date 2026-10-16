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
import { readFileSync } from 'fs';
import { join } from 'path';

describe('Matomo tracking code', () => {
  // The tracking snippet lives in index.html, outside the Angular application, so it is run here
  // against stubbed browser objects to see where it sends the analytics and which script it loads.

  const MATOMO = 'https://analytics.cessda.eu/';

  const trackingScript = (): string => {
    const html = readFileSync(join(process.cwd(), 'src/main/webapp/index.html'), 'utf8');
    const scripts = new DOMParser().parseFromString(html, 'text/html').querySelectorAll('script');
    const script = Array.from(scripts)
      .map(element => element.textContent ?? '')
      .find(body => body.includes('_paq'));
    if (!script) {
      throw new Error('The Matomo tracking code was not found in index.html');
    }
    return script;
  };

  const runTrackingScript = (): { paq: unknown[][]; loadedScript: { src?: string; async?: boolean } } => {
    const loadedScript: { src?: string; async?: boolean } = {};
    const window: { _paq?: unknown[][] } = {};
    const document = {
      createElement: () => loadedScript,
      getElementsByTagName: () => [{ parentNode: { insertBefore: jest.fn() } }],
    };
    // eslint-disable-next-line @typescript-eslint/no-implied-eval
    new Function('window', 'document', trackingScript())(window, document);
    return { paq: window._paq ?? [], loadedScript };
  };

  it('should send the analytics to the self-hosted Matomo instance', () => {
    const { paq } = runTrackingScript();

    expect(paq).toContainEqual(['setTrackerUrl', `${MATOMO}matomo.php`]);
    expect(paq).toContainEqual(['setSiteId', '3']);
  });

  it('should load the tracker script from the self-hosted Matomo instance', () => {
    const { loadedScript } = runTrackingScript();

    expect(loadedScript.src).toBe(`${MATOMO}matomo.js`);
    expect(loadedScript.async).toBe(true);
  });

  it('should respect Do Not Track', () => {
    const { paq } = runTrackingScript();

    expect(paq).toContainEqual(['setDoNotTrack', true]);
  });

  it('should not refer to Matomo Cloud', () => {
    expect(trackingScript()).not.toContain('matomo.cloud');
  });
});
