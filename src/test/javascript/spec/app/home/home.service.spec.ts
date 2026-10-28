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

import { HomeService } from 'app/home/home.service';
import { createNewVocabulary } from 'app/shared/model/vocabulary.model';

describe('Service Tests', () => {
  describe('Home Service', () => {
    let service: HomeService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
      TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });

      service = TestBed.inject(HomeService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
      httpMock.verify();
    });

    it('should request a single vocabulary by id', () => {
      const vocabulary = createNewVocabulary({ id: 5 });
      let body;
      service.find(5).subscribe(res => (body = res.body));

      const req = httpMock.expectOne({ method: 'GET', url: 'api/vocabulary/5' });
      req.flush(vocabulary);

      expect(body).toEqual(vocabulary);
    });

    it('should search with the paging parameters and every sort value', () => {
      const result = { totalElements: 0, vocabularies: [], aggrs: [] };
      let body;
      service.search({ page: 2, size: 20, sort: ['relevance', 'code,asc'] }).subscribe(res => (body = res.body));

      const req = httpMock.expectOne(r => r.url === 'v2/search');

      expect(req.request.method).toBe('GET');
      expect(req.request.params.get('page')).toBe('2');
      expect(req.request.params.get('size')).toBe('20');
      expect(req.request.params.getAll('sort')).toEqual(['relevance', 'code,asc']);
      req.flush(result);
      expect(body).toEqual(result);
    });

    it('should search without parameters', () => {
      service.search().subscribe();

      const req = httpMock.expectOne(r => r.url === 'v2/search');

      expect(req.request.params.keys()).toHaveLength(0);
      req.flush({});
    });

    describe('getVocabularyFile', () => {
      it('should request the latest static file when no version is given', () => {
        service.getVocabularyFile('TopicClassification').subscribe();

        const req = httpMock.expectOne(r => r.url === 'content/vocabularies/TopicClassification/TopicClassification.json');

        expect(req.request.method).toBe('GET');
        req.flush({});
      });

      it('should request the latest static file when the version parameter is empty', () => {
        service.getVocabularyFile('TopicClassification', { lang: 'en', v: '' }).subscribe();

        const req = httpMock.expectOne(r => r.url === 'content/vocabularies/TopicClassification/TopicClassification.json');

        expect(req.request.params.get('lang')).toBe('en');
        req.flush({});
      });

      it('should strip the patch number from a three part version', () => {
        service.getVocabularyFile('TopicClassification', { v: '4.2.1' }).subscribe();

        const req = httpMock.expectOne(r => r.url === 'content/vocabularies/TopicClassification/4.2/TopicClassification_4.2.json');

        expect(req.request.params.get('v')).toBe('4.2.1');
        req.flush({});
      });

      it('should keep a two part version unchanged', () => {
        service.getVocabularyFile('TopicClassification', { v: '4.2' }).subscribe();

        httpMock.expectOne(r => r.url === 'content/vocabularies/TopicClassification/4.2/TopicClassification_4.2.json').flush({});
      });

      it('should keep a version without dots unchanged', () => {
        service.getVocabularyFile('TopicClassification', { v: 'draft' }).subscribe();

        httpMock.expectOne(r => r.url === 'content/vocabularies/TopicClassification/draft/TopicClassification_draft.json').flush({});
      });
    });

    describe('getVocabulary', () => {
      it('should request the latest version when no version is given', () => {
        service.getVocabulary('TopicClassification').subscribe();

        const req = httpMock.expectOne(r => r.url === 'v2/vocabularies/TopicClassification/latest');

        expect(req.request.method).toBe('GET');
        req.flush({});
      });

      it('should strip the patch number from a three part version', () => {
        const query = { v: '4.2.1', languageVersion: 'en-4.2.1' };
        service.getVocabulary('TopicClassification', query).subscribe();

        const req = httpMock.expectOne(r => r.url === 'v2/vocabularies/TopicClassification/4.2');

        expect(req.request.params.get('languageVersion')).toBe('en-4.2.1');
        expect(query.v).toBe('4.2');
        req.flush({});
      });

      it('should keep a two part version unchanged', () => {
        service.getVocabulary('TopicClassification', { v: '4.2' }).subscribe();

        httpMock.expectOne(r => r.url === 'v2/vocabularies/TopicClassification/4.2').flush({});
      });
    });

    it('should download a vocabulary file with the requested mime type', () => {
      const blob = new Blob(['<xml/>'], { type: 'application/xml' });
      let downloaded: Blob | undefined;
      service
        .downloadVocabularyFile('TopicClassification', '4.2', 'application/xml', { languageVersion: 'en-4.2.1' })
        .subscribe(res => (downloaded = res));

      const req = httpMock.expectOne(r => r.url === 'v2/vocabularies/TopicClassification/4.2');

      expect(req.request.method).toBe('GET');
      expect(req.request.headers.get('accept')).toBe('application/xml');
      expect(req.request.responseType).toBe('blob');
      expect(req.request.params.get('languageVersion')).toBe('en-4.2.1');
      req.flush(blob);
      expect(downloaded).toBe(blob);
    });

    it('should request a comparison between two language versions', () => {
      let body;
      service.getVocabularyCompare('TopicClassification', 'en-4.1', 'en-4.2').subscribe(res => (body = res.body));

      const req = httpMock.expectOne({ method: 'GET', url: 'v2/compare-vocabulary/TopicClassification/en-4.1/en-4.2' });
      req.flush(['old', 'new']);

      expect(body).toEqual(['old', 'new']);
    });

    it('should request the available languages', () => {
      let body;
      service.getAvailableLanguagesIsos({ agency: 'CESSDA' }).subscribe(res => (body = res.body));

      const req = httpMock.expectOne(r => r.url === '/api/search/languages');

      expect(req.request.params.get('agency')).toBe('CESSDA');
      req.flush(['en', 'de']);
      expect(body).toEqual(['en', 'de']);
    });
  });
});
