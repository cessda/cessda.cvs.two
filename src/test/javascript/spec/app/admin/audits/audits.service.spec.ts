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
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { AuditsService, AuditsQuery } from 'app/admin/audits/audits.service';
import { Audit } from 'app/admin/audits/audit.model';
import { SERVER_API_URL } from 'app/app.constants';
import { AuditData } from 'app/admin/audits/audit-data.model';

describe('Service Tests', () => {
  describe('Audits Service', () => {
    let service: AuditsService;
    let httpMock: HttpTestingController;
    const fakeRequest: AuditsQuery = { page: 0, size: 0, sort: [], fromDate: '', toDate: '' };

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });

      service = TestBed.get(AuditsService);
      httpMock = TestBed.get(HttpTestingController);
    });

    afterEach(() => {
      httpMock.verify();
    });

    describe('Service methods', () => {
      it('should call correct URL', () => {
        service.query(fakeRequest).subscribe();

        const req = httpMock.expectOne({ method: 'GET' });
        const resourceUrl = SERVER_API_URL + 'management/audits';
        expect(req.request.url).toEqual(resourceUrl);
      });

      it('should return Audits', () => {
        let expectedResult: HttpResponse<Audit[]> = new HttpResponse({ body: [] });
        const auditData: AuditData = { key: 'remoteAddress', value: '127.0.0.1' };
        const audit: Audit = { data: auditData, principal: 'user', timestamp: '20140101', type: 'AUTHENTICATION_SUCCESS', expanded: false };

        service.query(fakeRequest).subscribe(received => {
          expectedResult = received;
        });

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([audit]);
        let audits: Audit[] = [];
        if (expectedResult.body !== null) {
          audits = expectedResult.body;
        }
        expect(audits.length).toBe(1);
        expect(audits[0]).toEqual(audit);
      });

      it('should propagate not found response', () => {
        let expectedResult = 0;
        service.query(fakeRequest).subscribe(null, (error: HttpErrorResponse) => {
          expectedResult = error.status;
        });

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush('Invalid request parameters', {
          status: 404,
          statusText: 'Bad Request',
        });
        expect(expectedResult).toEqual(404);
      });
    });
  });
});
