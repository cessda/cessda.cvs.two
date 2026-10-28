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
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpEvent, HttpEventType, HttpHeaderResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { FileUploadService } from 'app/shared/upload/file-upload.service';

describe('Service Tests', () => {
  describe('FileUpload Service', () => {
    let service: FileUploadService;
    let httpMock: HttpTestingController;

    const file = new File(['content'], 'logo.png', { type: 'image/png' });

    beforeEach(() => {
      TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });

      service = TestBed.inject(FileUploadService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
      httpMock.verify();
    });

    const expectUpload = (upload: Observable<HttpEvent<void>>, url: string): void => {
      upload.subscribe();

      const req = httpMock.expectOne({ method: 'POST', url });

      expect(req.request.reportProgress).toBe(true);
      expect(req.request.responseType).toBe('text');
      expect(req.request.body).toBeInstanceOf(FormData);
      expect((req.request.body as FormData).get('file')).toBeInstanceOf(File);
      req.flush('');
    };

    it('should upload an agency image', () => {
      expectUpload(service.uploadAgencyImage(file), 'api/upload/agency-image');
    });

    it('should upload a licence image', () => {
      expectUpload(service.uploadLicenseImage(file), 'api/upload/license-image');
    });

    it('should upload a file', () => {
      expectUpload(service.uploadFile(file), 'api/upload/file');
    });

    it('should convert an uploaded document to HTML', () => {
      let body;
      service.convertDocsToHtml('upload.docx').subscribe(res => (body = res.body));

      const req = httpMock.expectOne({ method: 'POST', url: 'api/upload/docx2html/upload.docx' });

      expect(req.request.body).toBeNull();
      req.flush({ status: 'OK' });
      expect(body).toEqual({ status: 'OK' });
    });

    it('should fill a metadata section from an HTML file', () => {
      let status;
      service.fillMetadataWithHtmlFile('upload.html', 'about').subscribe(res => (status = res.status));

      const req = httpMock.expectOne({ method: 'POST', url: 'api/upload/html2section/upload.html/about' });

      expect(req.request.body).toBeNull();
      req.flush(null, { status: 204, statusText: 'No Content' });
      expect(status).toBe(204);
    });
  });

  describe('FileUpload Service uploadFileHandler', () => {
    const handle = (...events: HttpEvent<unknown>[]): { values: unknown[]; error?: Error } => {
      const result: { values: unknown[]; error?: Error } = { values: [] };
      of(...events)
        .pipe(FileUploadService.uploadFileHandler)
        .subscribe({
          next: value => result.values.push(value),
          error: (e: Error) => (result.error = e),
        });
      return result;
    };

    it('should report the upload progress as a percentage', () => {
      const result = handle({ type: HttpEventType.UploadProgress, loaded: 1, total: 3 });

      expect(result.values).toEqual([{ progress: 33 }]);
      expect(result.error).toBeUndefined();
    });

    it('should report the location returned in the response headers', () => {
      const headers = new HttpHeaders({ location: '/content/images/agency/logo.png' });
      const result = handle(new HttpHeaderResponse({ headers }));

      expect(result.values).toEqual([{ progress: 100, location: '/content/images/agency/logo.png' }]);
    });

    it('should ignore events other than progress and response headers', () => {
      const result = handle({ type: HttpEventType.Sent }, new HttpResponse({ body: 'done' }));

      expect(result.values).toEqual([]);
      expect(result.error).toBeUndefined();
    });

    it('should fail when the response headers carry no location', () => {
      const result = handle(new HttpHeaderResponse({ headers: new HttpHeaders() }));

      expect(result.values).toEqual([]);
      expect(result.error).toBeInstanceOf(TypeError);
      expect(result.error?.message).toBe('No location returned from server');
    });

    it('should fail on a progress event without a total size', () => {
      const result = handle({ type: HttpEventType.UploadProgress, loaded: 1 });

      expect(result.error).toBeInstanceOf(TypeError);
    });
  });
});
