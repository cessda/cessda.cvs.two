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
import { HTTP_INTERCEPTORS, HttpClient } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { JhiAlertService } from 'ng-jhipster';

import { NotificationInterceptor } from 'app/blocks/interceptor/notification.interceptor';

describe('Interceptor Tests', () => {
  describe('Notification Interceptor', () => {
    let http: HttpClient;
    let httpMock: HttpTestingController;
    let success: jasmine.Spy;

    const respondWith = (headers: Record<string, string>): void => {
      http.get('api/vocabularies').subscribe();
      httpMock.expectOne('api/vocabularies').flush({}, { headers });
    };

    beforeEach(() => {
      success = jasmine.createSpy('success');

      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [
          { provide: JhiAlertService, useValue: { success } },
          { provide: HTTP_INTERCEPTORS, useClass: NotificationInterceptor, multi: true },
        ],
      });

      http = TestBed.inject(HttpClient);
      httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
      httpMock.verify();
    });

    it('should raise the alert the server asks for', () => {
      respondWith({ 'X-cvsApp-alert': 'cvsApp.agency.created' });

      expect(success).toHaveBeenCalledWith('cvsApp.agency.created', { param: null });
    });

    it('should pass the parameter along with it', () => {
      respondWith({ 'X-cvsApp-alert': 'cvsApp.agency.created', 'X-cvsApp-params': 'CESSDA' });

      expect(success).toHaveBeenCalledWith('cvsApp.agency.created', { param: 'CESSDA' });
    });

    it('should read a parameter whose spaces were sent as plus signs', () => {
      respondWith({ 'X-cvsApp-alert': 'cvsApp.agency.created', 'X-cvsApp-params': 'Data+Archive' });

      expect(success).toHaveBeenCalledWith('cvsApp.agency.created', { param: 'Data Archive' });
    });

    it('should decode a parameter that was percent encoded', () => {
      respondWith({ 'X-cvsApp-alert': 'cvsApp.agency.created', 'X-cvsApp-params': 'Analysis%20Unit' });

      expect(success).toHaveBeenCalledWith('cvsApp.agency.created', { param: 'Analysis Unit' });
    });

    it('should find the headers whatever their prefix', () => {
      respondWith({ 'X-otherApp-app-alert': 'other.created', 'X-otherApp-app-params': 'GESIS' });

      expect(success).toHaveBeenCalledWith('other.created', { param: 'GESIS' });
    });

    it('should stay quiet when the response carries no alert', () => {
      respondWith({});

      expect(success).not.toHaveBeenCalled();
    });

    it('should stay quiet when only a parameter is sent', () => {
      respondWith({ 'X-cvsApp-params': 'CESSDA' });

      expect(success).not.toHaveBeenCalled();
    });

    it('should stay quiet when the request fails', () => {
      http.get('api/vocabularies').subscribe({ error: () => undefined });
      httpMock.expectOne('api/vocabularies').error(new ProgressEvent('error'), { status: 500, statusText: 'Server Error' });

      expect(success).not.toHaveBeenCalled();
    });
  });
});
