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
import { TestBed } from '@angular/core/testing';
import { DomSanitizer } from '@angular/platform-browser';
import { SecurityContext } from '@angular/core';

import { SafeHtmlPipe } from 'app/shared/pipe/safe-html-pipe';

describe('SafeHtmlPipe', () => {
  let pipe: SafeHtmlPipe;
  let sanitizer: DomSanitizer;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [SafeHtmlPipe] });
    pipe = TestBed.inject(SafeHtmlPipe);
    sanitizer = TestBed.inject(DomSanitizer);
  });

  it('should hand the markup to the sanitizer as trusted HTML', () => {
    const spy = jest.spyOn(sanitizer, 'bypassSecurityTrustHtml');

    pipe.transform('<b>bold</b>');

    expect(spy).toHaveBeenCalledWith('<b>bold</b>');
  });

  it('should keep the style attribute that plain sanitising would drop', () => {
    const safe = pipe.transform('<p style="color: red">styled</p>');

    expect(sanitizer.sanitize(SecurityContext.HTML, safe)).toContain('style="color: red"');
  });
});
