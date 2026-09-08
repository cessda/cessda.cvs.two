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

import { MaintenanceService } from 'app/admin/maintenance/maintenance.service';
import { Maintenance } from 'app/admin/maintenance/maintenance.model';

describe('Service Tests', () => {
  describe('Maintenance Service', () => {
    let service: MaintenanceService;
    let httpMock: HttpTestingController;

    const result = { output: 'done', type: 'INDEX_AGENCY' };

    beforeEach(() => {
      TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });

      service = TestBed.inject(MaintenanceService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
      httpMock.verify();
    });

    const endpoints: { name: string; call: () => void; path: string }[] = [
      { name: 'generating the published JSON', call: () => service.generateJson().subscribe(), path: '/publication/generate-json' },
      { name: 'indexing the agencies', call: () => service.indexAgency().subscribe(), path: '/index/agency' },
      { name: 'indexing the agency statistics', call: () => service.indexAgencyStats().subscribe(), path: '/index/agency-stats' },
      { name: 'indexing the published CVs', call: () => service.indexVocabularyPublish().subscribe(), path: '/index/vocabulary' },
      { name: 'indexing the CVs in the editor', call: () => service.indexVocabularyEditor().subscribe(), path: '/index/vocabulary/editor' },
    ];

    endpoints.forEach(endpoint => {
      it(`should post to its own endpoint for ${endpoint.name}`, () => {
        endpoint.call();

        const req = httpMock.expectOne({ method: 'POST' });

        expect(req.request.url).toBe(`${service.maintenanceUrl}${endpoint.path}`);
        expect(req.request.body).toBeNull();
        req.flush(result);
      });
    });

    it('should hand the maintenance result back to the caller', () => {
      let body: Maintenance | null = null;
      service.indexAgency().subscribe(res => (body = res.body));

      httpMock.expectOne({ method: 'POST' }).flush({ output: 'indexed 12 agencies', type: 'INDEX_AGENCY' });

      expect(body!.output).toBe('indexed 12 agencies');
      expect(body!.type).toBe('INDEX_AGENCY');
    });

    it('should ask for the response rather than the body alone', () => {
      let status = 0;
      service.generateJson().subscribe(res => (status = res.status));

      httpMock.expectOne({ method: 'POST' }).flush(result);

      expect(status).toBe(200);
    });
  });
});
