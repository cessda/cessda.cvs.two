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
Object.defineProperty(window, 'getComputedStyle', {
  value: () => ['-webkit-appearance'],
});

/*
 * jsdom implements neither of these, so any component that offers a file for download needs them
 * put in place before it can be tested at all.
 *
 * The stub hands back a fragment rather than a blob URL on purpose. Such a component builds a link
 * and clicks it, and jsdom follows every navigation but a fragment by logging a "Not implemented:
 * navigation" error with a full stack trace, which buries the rest of the CI log. A fragment is
 * the one target it will follow quietly.
 *
 * Both are configurable so that a spec wanting to assert on the blob it was handed can spy on them.
 */
Object.defineProperty(window.URL, 'createObjectURL', {
  value: () => '#blob',
  configurable: true,
  writable: true,
});

Object.defineProperty(window.URL, 'revokeObjectURL', {
  value: () => undefined,
  configurable: true,
  writable: true,
});
