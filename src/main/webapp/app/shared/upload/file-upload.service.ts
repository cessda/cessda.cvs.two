/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
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
import { HttpClient, HttpEvent, HttpEventType, HttpRequest, HttpResponse } from '@angular/common/http';
import { filter, map, Observable, pipe } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { SimpleResponse } from 'app/shared/model/simple-response.model';

@Injectable({ providedIn: 'root' })
export class FileUploadService {
  public resourceUrl = SERVER_API_URL + 'api/upload';

  constructor(protected http: HttpClient) {}

  /**
   * return the  uploaded filename
   * @param file
   */
  uploadAgencyImage(file: File): Observable<HttpEvent<void>> {
    const formdata: FormData = new FormData();
    formdata.append('file', file);

    const req = new HttpRequest('POST', `${this.resourceUrl}/agency-image`, formdata, {
      reportProgress: true,
      responseType: 'text',
    });

    return this.http.request(req);
  }
  /**
   * return the  uploaded filename
   * @param file
   */
  uploadLicenseImage(file: File): Observable<HttpEvent<void>> {
    const formdata: FormData = new FormData();
    formdata.append('file', file);

    const req = new HttpRequest('POST', `${this.resourceUrl}/license-image`, formdata, {
      reportProgress: true,
      responseType: 'text',
    });

    return this.http.request(req);
  }

  /**
   * return the  uploaded filename
   * @param file
   */
  uploadFile(file: File): Observable<HttpEvent<void>> {
    const formdata: FormData = new FormData();
    formdata.append('file', file);

    const req = new HttpRequest('POST', `${this.resourceUrl}/file`, formdata, {
      reportProgress: true,
      responseType: 'text',
    });

    return this.http.request(req);
  }

  static uploadFileHandler = pipe(
    filter<HttpEvent<unknown>>(event => event.type === HttpEventType.UploadProgress || event.type === HttpEventType.ResponseHeader),
    map(event => {
      if (event.type === HttpEventType.UploadProgress && event.total) {
        // Respond to a progress event
        const progress = Math.round((event.loaded / event.total) * 100);
        return { progress: progress };
      } else if (event.type === HttpEventType.ResponseHeader) {
        const location = event.headers.get('location');
        if (location) {
          return { progress: 100, location: location };
        }
      }

      throw new TypeError('No location returned from server');
    }),
  );

  convertDocsToHtml(fileName: string): Observable<HttpResponse<SimpleResponse>> {
    return this.http.post<SimpleResponse>(`${this.resourceUrl}/docx2html/${fileName}`, null, { observe: 'response' });
  }

  fillMetadataWithHtmlFile(fileName: string, metadataKey: string): Observable<HttpResponse<void>> {
    return this.http.post<void>(`${this.resourceUrl}/html2section/${fileName}/${metadataKey}`, null, { observe: 'response' });
  }
}
