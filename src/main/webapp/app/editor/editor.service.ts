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
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { Vocabulary } from 'app/shared/model/vocabulary.model';
import { CvResult } from 'app/shared/model/cv-result.model';
import { map } from 'rxjs/operators';
import moment from 'moment';
import { VocabularySnippet } from 'app/shared/model/vocabulary-snippet.model';
import { CodeSnippet } from 'app/shared/model/code-snippet.model';
import { Concept } from 'app/shared/model/concept.model';
import { Version } from 'app/shared/model/version.model';
import { Comment } from 'app/shared/model/comment.model';
import { MetadataValue } from 'app/shared/model/metadata-value.model';

@Injectable({ providedIn: 'root' })
export class EditorService {
  protected http = inject(HttpClient);

  public resourceEditorSearchUrl = SERVER_API_URL + 'api/editors/search';
  public resourceEditorVocabularyUrl = SERVER_API_URL + 'api/editors/vocabularies';
  public resourceEditorCodeUrl = SERVER_API_URL + 'api/editors/codes';
  public resourceEditorCommentUrl = SERVER_API_URL + 'api/editors/comments';
  public resourceEditorMetadataUrl = SERVER_API_URL + 'api/editors/metadatas';
  public resourceDownloadUrl = SERVER_API_URL + 'api/editors/download';

  createVocabulary(vocabularySnippet: VocabularySnippet): Observable<HttpResponse<Vocabulary>> {
    return this.http
      .post<Vocabulary>(this.resourceEditorVocabularyUrl, vocabularySnippet, { observe: 'response' })
      .pipe(map((res: HttpResponse<Vocabulary>) => this.convertVocabularyDateFromServer(res)));
  }

  createNewVersion(id: number): Observable<HttpResponse<Version>> {
    return this.http.post<Version>(this.resourceEditorVocabularyUrl + '/new-version/' + id, null, { observe: 'response' });
  }

  updateVocabulary(vocabularySnippet: VocabularySnippet): Observable<HttpResponse<Vocabulary>> {
    return this.http
      .put<Vocabulary>(this.resourceEditorVocabularyUrl, vocabularySnippet, { observe: 'response' })
      .pipe(map(res => this.convertVocabularyDateFromServer(res)));
  }

  forwardStatusVocabulary(vocabularySnippet: VocabularySnippet): Observable<HttpResponse<Vocabulary>> {
    return this.http.put<Vocabulary>(this.resourceEditorVocabularyUrl + '/forward-status', vocabularySnippet, { observe: 'response' });
  }

  // eslint-disable-next-line @typescript-eslint/ban-types
  deleteVocabulary(versionId: number): Observable<HttpResponse<Object>> {
    return this.http.delete(`${this.resourceEditorVocabularyUrl}/${versionId}`, { observe: 'response' });
  }

  createCode(codeSnippet: CodeSnippet): Observable<HttpResponse<Concept>> {
    return this.http.post<Concept>(this.resourceEditorCodeUrl, codeSnippet, { observe: 'response' });
  }

  createBatchCode(codeSnippets: CodeSnippet[]): Observable<HttpResponse<Concept[]>> {
    return this.http.post<Concept[]>(`${this.resourceEditorCodeUrl}/batch`, codeSnippets, { observe: 'response' });
  }

  updateCode(codeSnippet: CodeSnippet): Observable<HttpResponse<Concept>> {
    return this.http.put<Concept>(this.resourceEditorCodeUrl, codeSnippet, { observe: 'response' });
  }

  reorderCode(codeSnippet: CodeSnippet): Observable<HttpResponse<Version>> {
    return this.http.post<Version>(this.resourceEditorCodeUrl + '/reorder', codeSnippet, { observe: 'response' });
  }

  deprecateCode(codeSnippet: CodeSnippet): Observable<HttpResponse<Concept>> {
    return this.http.post<Concept>(this.resourceEditorCodeUrl + '/deprecate', codeSnippet, { observe: 'response' });
  }

  // eslint-disable-next-line @typescript-eslint/ban-types
  deleteCode(conceptId: number): Observable<HttpResponse<Object>> {
    return this.http.delete(`${this.resourceEditorCodeUrl}/${conceptId}`, { observe: 'response' });
  }

  protected convertDateFromClient(vocabulary: Vocabulary): Vocabulary {
    const copy: Vocabulary = Object.assign({}, vocabulary, {
      publicationDate:
        vocabulary.publicationDate && vocabulary.publicationDate.isValid() ? vocabulary.publicationDate.format(DATE_FORMAT) : undefined,
      lastModified: vocabulary.lastModified && vocabulary.lastModified.isValid() ? vocabulary.lastModified.toJSON() : undefined,
    });
    return copy;
  }

  search(req?: any): Observable<HttpResponse<CvResult>> {
    const options = createRequestOption(req);
    return this.http.get<CvResult>(this.resourceEditorSearchUrl, { params: options, observe: 'response' });
  }

  getVocabulary(notation: string): Observable<HttpResponse<Vocabulary>> {
    return this.http.get<Vocabulary>(`${this.resourceEditorVocabularyUrl}/${notation}/latest`, { observe: 'response' });
  }

  getVocabularyCompare(id: number): Observable<HttpResponse<string[]>> {
    return this.http.get<string[]>(`${this.resourceEditorVocabularyUrl}/compare-prev/${id}`, { observe: 'response' });
  }

  downloadVocabularyFile(notation: string, slNumber: string, mimeType: string, req?: any): Observable<Blob> {
    const options = createRequestOption(req);
    return this.http.get(`${this.resourceDownloadUrl}/${notation}/${slNumber}`, {
      params: options,
      headers: new HttpHeaders({ accept: mimeType }),
      responseType: 'blob',
    });
  }

  createComment(comment: Comment): Observable<HttpResponse<Comment>> {
    return this.http
      .post<Comment>(this.resourceEditorCommentUrl, comment, { observe: 'response' })
      .pipe(map((res: HttpResponse<Comment>) => this.convertCommentDateFromServer(res)));
  }

  updateComment(comment: Comment): Observable<HttpResponse<Comment>> {
    return this.http.put<Comment>(this.resourceEditorCommentUrl, comment, { observe: 'response' });
  }

  deleteComment(commentId: number): Observable<HttpResponse<void>> {
    return this.http.delete<void>(`${this.resourceEditorCommentUrl}/${commentId}`, { observe: 'response' });
  }

  protected convertVocabularyDateFromServer(res: HttpResponse<Vocabulary>): HttpResponse<Vocabulary> {
    if (res.body) {
      res.body.publicationDate = res.body.publicationDate ? moment(res.body.publicationDate) : undefined;
      res.body.lastModified = res.body.lastModified ? moment(res.body.lastModified) : undefined;
    }
    return res;
  }

  protected convertCommentDateFromServer(res: HttpResponse<Comment>): HttpResponse<Comment> {
    if (res.body) {
      res.body.dateTime = res.body.dateTime ? moment(res.body.dateTime) : undefined;
    }
    return res;
  }

  createAppMetadata(metadataValue: MetadataValue): Observable<HttpResponse<MetadataValue>> {
    return this.http.post<MetadataValue>(this.resourceEditorMetadataUrl, metadataValue, { observe: 'response' });
  }

  updateAppMetadata(metadataValue: MetadataValue): Observable<HttpResponse<MetadataValue>> {
    return this.http.put<MetadataValue>(this.resourceEditorMetadataUrl, metadataValue, { observe: 'response' });
  }

  // eslint-disable-next-line @typescript-eslint/ban-types
  deleteAppMetadata(metadataValueId: number): Observable<HttpResponse<Object>> {
    return this.http.delete(`${this.resourceEditorMetadataUrl}/${metadataValueId}`, { observe: 'response' });
  }
}
