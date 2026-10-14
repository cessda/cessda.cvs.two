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
import { FormBuilder } from '@angular/forms';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

describe('DataUtils', () => {
  let dataUtils: DataUtils;
  const form = () => new FormBuilder().group({ logo: [null], logoContentType: [null] });
  const changeEvent = (...files: File[]) => ({ target: { files } }) as unknown as Event;

  beforeEach(() => {
    dataUtils = TestBed.inject(DataUtils);
  });

  it('should load the file into the form as base64 together with its content type', done => {
    const editForm = form();

    dataUtils.loadFileToForm(changeEvent(new File(['logo'], 'logo.png', { type: 'image/png' })), editForm, 'logo', true).subscribe({
      complete: () => {
        expect(editForm.value).toEqual({ logo: btoa('logo'), logoContentType: 'image/png' });
        done();
      },
    });
  });

  it('should refuse a file that is not an image when an image is expected', done => {
    dataUtils.loadFileToForm(changeEvent(new File(['text'], 'notes.txt', { type: 'text/plain' })), form(), 'logo', true).subscribe({
      error: (error: FileLoadError) => {
        expect(error.key).toBe('not.image');
        expect(error.params).toEqual({ fileType: 'text/plain' });
        done();
      },
    });
  });

  it('should report an event without a file', done => {
    dataUtils.loadFileToForm(changeEvent(), form(), 'logo', false).subscribe({
      error: (error: FileLoadError) => {
        expect(error.key).toBe('could.not.extract');
        done();
      },
    });
  });
});
