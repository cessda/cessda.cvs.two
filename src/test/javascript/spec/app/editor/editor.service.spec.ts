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
import { getTestBed, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import moment from 'moment';

import { EditorService } from 'app/editor/editor.service';
import { VocabularySnippet } from 'app/shared/model/vocabulary-snippet.model';
import { CodeSnippet } from 'app/shared/model/code-snippet.model';
import { Comment } from 'app/shared/model/comment.model';
import { MetadataValue } from 'app/shared/model/metadata-value.model';

describe('Service Tests', () => {
  describe('Editor Service', () => {
    let injector: TestBed;
    let service: EditorService;
    let httpMock: HttpTestingController;

    const vocabularySnippet = { vocabularyId: 1, notation: 'AnalysisUnit' } as VocabularySnippet;
    const codeSnippet = { conceptId: 7, notation: 'Individual' } as CodeSnippet;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      injector = getTestBed();
      service = injector.inject(EditorService);
      httpMock = injector.inject(HttpTestingController);
    });

    afterEach(() => {
      // fails the test if any call went to an unexpected URL
      httpMock.verify();
    });

    describe('Vocabularies', () => {
      it('should post a new vocabulary', () => {
        service.createVocabulary(vocabularySnippet).subscribe();

        const req = httpMock.expectOne({ method: 'POST', url: service.resourceEditorVocabularyUrl });
        expect(req.request.body).toEqual(vocabularySnippet);
        req.flush({});
      });

      it('should put an updated vocabulary', () => {
        service.updateVocabulary(vocabularySnippet).subscribe();

        httpMock.expectOne({ method: 'PUT', url: service.resourceEditorVocabularyUrl }).flush({});
      });

      it('should post a new version for a vocabulary id', () => {
        service.createNewVersion(42).subscribe();

        httpMock.expectOne({ method: 'POST', url: `${service.resourceEditorVocabularyUrl}/new-version/42` }).flush({});
      });

      it('should put a status change on its own endpoint', () => {
        service.forwardStatusVocabulary(vocabularySnippet).subscribe();

        httpMock.expectOne({ method: 'PUT', url: `${service.resourceEditorVocabularyUrl}/forward-status` }).flush({});
      });

      it('should delete a vocabulary by version id', () => {
        service.deleteVocabulary(9).subscribe();

        httpMock.expectOne({ method: 'DELETE', url: `${service.resourceEditorVocabularyUrl}/9` }).flush({});
      });

      it('should request the latest version by notation', () => {
        service.getVocabulary('AnalysisUnit').subscribe();

        httpMock.expectOne({ method: 'GET', url: `${service.resourceEditorVocabularyUrl}/AnalysisUnit/latest` }).flush({});
      });

      it('should request a comparison against the previous version', () => {
        service.getVocabularyCompare(5).subscribe();

        httpMock.expectOne({ method: 'GET', url: `${service.resourceEditorVocabularyUrl}/compare-prev/5` }).flush([]);
      });

      it('should convert vocabulary dates coming back from the server into moments', () => {
        let body;
        service.createVocabulary(vocabularySnippet).subscribe(res => (body = res.body));

        httpMock.expectOne({ method: 'POST' }).flush({ publicationDate: '2021-03-04', lastModified: '2021-03-05T10:00:00Z' });

        expect(moment.isMoment(body!.publicationDate)).toBe(true);
        expect(body!.publicationDate!.format('YYYY-MM-DD')).toBe('2021-03-04');
        expect(moment.isMoment(body!.lastModified)).toBe(true);
      });

      it('should leave absent vocabulary dates undefined', () => {
        let body;
        service.createVocabulary(vocabularySnippet).subscribe(res => (body = res.body));

        httpMock.expectOne({ method: 'POST' }).flush({ notation: 'AnalysisUnit' });

        expect(body!.publicationDate).toBeUndefined();
        expect(body!.lastModified).toBeUndefined();
      });
    });

    describe('Codes', () => {
      it('should post a single code', () => {
        service.createCode(codeSnippet).subscribe();

        httpMock.expectOne({ method: 'POST', url: service.resourceEditorCodeUrl }).flush({});
      });

      it('should post a batch of codes to the batch endpoint', () => {
        service.createBatchCode([codeSnippet, codeSnippet]).subscribe();

        const req = httpMock.expectOne({ method: 'POST', url: `${service.resourceEditorCodeUrl}/batch` });
        expect(req.request.body).toHaveLength(2);
        req.flush([]);
      });

      it('should put an updated code', () => {
        service.updateCode(codeSnippet).subscribe();

        httpMock.expectOne({ method: 'PUT', url: service.resourceEditorCodeUrl }).flush({});
      });

      it('should post a reorder to its own endpoint', () => {
        service.reorderCode(codeSnippet).subscribe();

        httpMock.expectOne({ method: 'POST', url: `${service.resourceEditorCodeUrl}/reorder` }).flush({});
      });

      it('should post a deprecation to its own endpoint', () => {
        service.deprecateCode(codeSnippet).subscribe();

        httpMock.expectOne({ method: 'POST', url: `${service.resourceEditorCodeUrl}/deprecate` }).flush({});
      });

      it('should delete a code by concept id', () => {
        service.deleteCode(3).subscribe();

        httpMock.expectOne({ method: 'DELETE', url: `${service.resourceEditorCodeUrl}/3` }).flush({});
      });
    });

    describe('Comments', () => {
      const comment = { id: 1, content: 'Looks good' } as Comment;

      it('should post a comment', () => {
        service.createComment(comment).subscribe();

        httpMock.expectOne({ method: 'POST', url: service.resourceEditorCommentUrl }).flush({});
      });

      it('should convert the comment timestamp coming back from the server into a moment', () => {
        let body;
        service.createComment(comment).subscribe(res => (body = res.body));

        httpMock.expectOne({ method: 'POST' }).flush({ id: 1, dateTime: '2022-06-01T08:30:00Z' });

        expect(moment.isMoment(body!.dateTime)).toBe(true);
      });

      it('should put an updated comment', () => {
        service.updateComment(comment).subscribe();

        httpMock.expectOne({ method: 'PUT', url: service.resourceEditorCommentUrl }).flush({});
      });

      it('should delete a comment by id', () => {
        service.deleteComment(11).subscribe();

        httpMock.expectOne({ method: 'DELETE', url: `${service.resourceEditorCommentUrl}/11` }).flush({});
      });
    });

    describe('Application metadata', () => {
      const metadataValue = { id: 2, value: 'AAA' } as MetadataValue;

      it('should post application metadata', () => {
        service.createAppMetadata(metadataValue).subscribe();

        httpMock.expectOne({ method: 'POST', url: service.resourceEditorMetadataUrl }).flush({});
      });

      it('should put application metadata', () => {
        service.updateAppMetadata(metadataValue).subscribe();

        httpMock.expectOne({ method: 'PUT', url: service.resourceEditorMetadataUrl }).flush({});
      });

      it('should delete application metadata by id', () => {
        service.deleteAppMetadata(4).subscribe();

        httpMock.expectOne({ method: 'DELETE', url: `${service.resourceEditorMetadataUrl}/4` }).flush({});
      });
    });

    describe('Search and download', () => {
      it('should pass the search request through as query parameters', () => {
        service.search({ q: 'unit', size: 10, page: 0, sort: ['notation,asc'] }).subscribe();

        const req = httpMock.expectOne(r => r.url === service.resourceEditorSearchUrl);
        expect(req.request.params.get('q')).toBe('unit');
        expect(req.request.params.get('size')).toBe('10');
        expect(req.request.params.get('page')).toBe('0');
        expect(req.request.params.getAll('sort')).toEqual(['notation,asc']);
        req.flush({});
      });

      it('should search without parameters when no request is given', () => {
        service.search().subscribe();

        const req = httpMock.expectOne({ method: 'GET', url: service.resourceEditorSearchUrl });
        expect(req.request.params.keys()).toHaveLength(0);
        req.flush({});
      });

      it('should request a download as a blob with the requested mime type', () => {
        service.downloadVocabularyFile('AnalysisUnit', '2.0', 'application/pdf', { lv: 'en' }).subscribe();

        const req = httpMock.expectOne(r => r.url === `${service.resourceDownloadUrl}/AnalysisUnit/2.0`);
        expect(req.request.headers.get('accept')).toBe('application/pdf');
        expect(req.request.responseType).toBe('blob');
        expect(req.request.params.get('lv')).toBe('en');
        req.flush(new Blob());
      });
    });
  });
});
