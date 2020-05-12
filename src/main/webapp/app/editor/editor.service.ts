import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, DATE_FORMAT } from 'app/shared';
import { IVocabulary } from 'app/shared/model/vocabulary.model';
import { ICvResult } from 'app/shared/model/cv-result.model';
import { map } from 'rxjs/operators';
import * as moment from 'moment';
import { IVocabularySnippet } from 'app/shared/model/vocabulary-snippet.model';
import { ICodeSnippet } from 'app/shared/model/code-snippet.model';
import { IConcept } from 'app/shared/model/concept.model';
import { IVersion } from 'app/shared/model/version.model';

type EntityResponseVocabularyType = HttpResponse<IVocabulary>;
type EntityResponseVersionType = HttpResponse<IVersion>;
type EntityResponseConceptType = HttpResponse<IConcept>;

@Injectable({ providedIn: 'root' })
export class EditorService {
  public resourceVocabularyUrl = SERVER_API_URL + 'api/vocabularies';
  public resourceEditorSearchUrl = SERVER_API_URL + 'api/editor/search';
  public resourceEditorVocabularyUrl = SERVER_API_URL + 'api/editors/vocabularies';
  public resourceEditorCodeUrl = SERVER_API_URL + 'api/editors/codes';
  public resourceDownloadUrl = SERVER_API_URL + 'api/download';

  constructor(protected http: HttpClient) {}

  createVocabulary(vocabularySnippet: IVocabularySnippet): Observable<EntityResponseVocabularyType> {
    return this.http
      .post<IVocabulary>(this.resourceEditorVocabularyUrl, vocabularySnippet, { observe: 'response' })
      .pipe(map((res: EntityResponseVocabularyType) => this.convertDateFromServer(res)));
  }

  createNewVersion(id: number): Observable<EntityResponseVersionType> {
    return this.http.post<IVersion>(this.resourceEditorVocabularyUrl + '/new-version/' + id, null, { observe: 'response' });
  }

  updateVocabulary(vocabularySnippet: IVocabularySnippet): Observable<EntityResponseVocabularyType> {
    return this.http
      .put<IVocabulary>(this.resourceEditorVocabularyUrl, vocabularySnippet, { observe: 'response' })
      .pipe(map((res: EntityResponseVocabularyType) => this.convertDateFromServer(res)));
  }

  forwardStatusVocabulary(vocabularySnippet: IVocabularySnippet): Observable<EntityResponseVersionType> {
    return this.http.put<IVersion>(this.resourceEditorVocabularyUrl + '/forward-status', vocabularySnippet, { observe: 'response' });
  }

  deleteVocabulary(versionId: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceEditorVocabularyUrl}/${versionId}`, { observe: 'response' });
  }

  createCode(codeSnippet: ICodeSnippet): Observable<EntityResponseConceptType> {
    return this.http
      .post<IConcept>(this.resourceEditorCodeUrl, codeSnippet, { observe: 'response' })
      .pipe(map((res: EntityResponseConceptType) => this.convertDateFromServer(res)));
  }

  updateCode(codeSnippet: ICodeSnippet): Observable<EntityResponseConceptType> {
    return this.http.put<IConcept>(this.resourceEditorCodeUrl, codeSnippet, { observe: 'response' });
  }

  reorderCode(codeSnippet: ICodeSnippet): Observable<EntityResponseVersionType> {
    return this.http.post<IVersion>(this.resourceEditorCodeUrl + '/reorder', codeSnippet, { observe: 'response' });
  }

  deleteCode(conceptId: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceEditorCodeUrl}/${conceptId}`, { observe: 'response' });
  }

  protected convertDateFromClient(vocabulary: IVocabulary): IVocabulary {
    const copy: IVocabulary = Object.assign({}, vocabulary, {
      publicationDate:
        vocabulary.publicationDate && vocabulary.publicationDate.isValid() ? vocabulary.publicationDate.format(DATE_FORMAT) : undefined,
      lastModified: vocabulary.lastModified && vocabulary.lastModified.isValid() ? vocabulary.lastModified.toJSON() : undefined
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseVocabularyType): EntityResponseVocabularyType {
    if (res.body) {
      res.body.publicationDate = res.body.publicationDate ? moment(res.body.publicationDate) : undefined;
      res.body.lastModified = res.body.lastModified ? moment(res.body.lastModified) : undefined;
    }
    return res;
  }

  search(req?: any): Observable<HttpResponse<ICvResult>> {
    const options = createRequestOption(req);
    return this.http.get<ICvResult>(this.resourceEditorSearchUrl, { params: options, observe: 'response' });
  }

  getVocabulary(notation: string): Observable<HttpResponse<IVocabulary>> {
    return this.http.get<IVocabulary>(`${this.resourceVocabularyUrl}/${notation}/latest`, { observe: 'response' });
  }

  getVocabularyCompare(id: number): Observable<HttpResponse<string[]>> {
    return this.http.get<string[]>(`${this.resourceEditorVocabularyUrl}/compare-prev/${id}`, { observe: 'response' });
  }

  downloadVocabularyFile(notation: string, slNumber: string, downloadType: string, req?: any): Observable<Blob> {
    const options = createRequestOption(req);
    return this.http.get(`${this.resourceDownloadUrl}/${downloadType}/${notation}/${slNumber}`, {
      params: options,
      responseType: 'blob'
    });
  }
}
