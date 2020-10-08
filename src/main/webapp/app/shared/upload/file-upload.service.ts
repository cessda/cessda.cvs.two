import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';

@Injectable({ providedIn: 'root' })
export class FileUploadService {
  public resourceUrl = SERVER_API_URL + 'api/upload';

  constructor(protected http: HttpClient) {}

  /**
   * return the  uploaded filename
   * @param file
   */
  uploadAgencyImage(file: File): Observable<HttpEvent<{}>> {
    const formdata: FormData = new FormData();
    formdata.append('file', file);

    const req = new HttpRequest('POST', `${this.resourceUrl}/agency-image`, formdata, {
      reportProgress: true,
      responseType: 'text'
    });

    return this.http.request(req);
  }
  /**
   * return the  uploaded filename
   * @param file
   */
  uploadLicenseImage(file: File): Observable<HttpEvent<{}>> {
    const formdata: FormData = new FormData();
    formdata.append('file', file);

    const req = new HttpRequest('POST', `${this.resourceUrl}/license-image`, formdata, {
      reportProgress: true,
      responseType: 'text'
    });

    return this.http.request(req);
  }
}
