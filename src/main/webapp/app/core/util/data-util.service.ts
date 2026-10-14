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
import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Observable, Observer } from 'rxjs';

export interface FileLoadError {
  message: string;
  key: string;
  params?: Record<string, unknown>;
}

/**
 * File helpers for forms, replacing the service formerly provided by ng-jhipster.
 */
@Injectable({ providedIn: 'root' })
export class DataUtils {
  /**
   * Reads the first file of a file input change event as base64 into `field` of `editForm`,
   * and its content type into `<field>ContentType`.
   * Completes when the form has been updated, errors with a {@link FileLoadError} otherwise.
   */
  loadFileToForm(event: Event, editForm: FormGroup, field: string, isImage: boolean): Observable<void> {
    return new Observable((observer: Observer<void>) => {
      const file = (event.target as HTMLInputElement | null)?.files?.[0];
      if (!file) {
        observer.error({ message: 'Could not extract file', key: 'could.not.extract', params: { event } });
        return;
      }
      if (isImage && !file.type.startsWith('image/')) {
        observer.error({
          message: `File was expected to be an image but was found to be '${file.type}'`,
          key: 'not.image',
          params: { fileType: file.type },
        });
        return;
      }
      this.toBase64(file, base64Data => {
        editForm.patchValue({ [field]: base64Data, [field + 'ContentType']: file.type });
        observer.next();
        observer.complete();
      });
    });
  }

  private toBase64(file: File, callback: (base64Data: string) => void): void {
    const fileReader = new FileReader();
    fileReader.onload = () => {
      const result = fileReader.result as string;
      callback(result.substring(result.indexOf('base64,') + 'base64,'.length));
    };
    fileReader.readAsDataURL(file);
  }
}
