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

import { SafeHtmlPipe } from 'app/shared/pipe/safe-html-pipe';

describe('Pipe Tests', () => {
  describe('Safe Html Pipe', () => {
    let pipe: SafeHtmlPipe;
    let sanitizer: DomSanitizer;

    beforeEach(() => {
      TestBed.configureTestingModule({ providers: [SafeHtmlPipe] });

      pipe = TestBed.inject(SafeHtmlPipe);
      sanitizer = TestBed.inject(DomSanitizer);
    });

    it('should hand the markup to the sanitizer as trusted HTML', () => {
      const bypass = spyOn(sanitizer, 'bypassSecurityTrustHtml').and.callThrough();

      pipe.transform('<p>A definition</p>');

      expect(bypass).toHaveBeenCalledWith('<p>A definition</p>');
    });

    it('should give back what the sanitizer produced', () => {
      const trusted = {} as ReturnType<DomSanitizer['bypassSecurityTrustHtml']>;
      spyOn(sanitizer, 'bypassSecurityTrustHtml').and.returnValue(trusted);

      expect(pipe.transform('<p>A definition</p>')).toBe(trusted);
    });

    it('should keep markup that Angular would otherwise strip', () => {
      const result = pipe.transform('<p style="color: red">A definition</p>');

      expect(sanitizer.sanitize(1, result)).toContain('style="color: red"');
    });

    it('should pass an empty string through', () => {
      expect(sanitizer.sanitize(1, pipe.transform(''))).toBe('');
    });
  });
});
