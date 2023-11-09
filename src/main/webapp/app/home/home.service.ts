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
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { Vocabulary } from 'app/shared/model/vocabulary.model';
import { CvResult } from 'app/shared/model/cv-result.model';

type EntityResponseType = HttpResponse<Vocabulary>;

@Injectable({ providedIn: 'root' })
export class HomeService {
  public resourceUrl = SERVER_API_URL + 'api/vocabulary';
  public vocabularyStaticUrl = SERVER_API_URL + 'content/vocabularies';
  public resourceSearchUrl = SERVER_API_URL + 'v2/search';
  public resourceDownloadUrl = SERVER_API_URL + 'v2/vocabularies';
  public resourceCvCompareUrl = SERVER_API_URL + 'v2/compare-vocabulary';

  constructor(protected http: HttpClient) {}

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<Vocabulary>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req?: Record<string, string>): Observable<HttpResponse<CvResult>> {
    const options = createRequestOption(req);
    return this.http.get<CvResult>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getVocabularyFile(notation: string, req?: Record<string, string>): Observable<HttpResponse<Vocabulary>> {
    const options = createRequestOption(req);
    if (req?.v) {
      if (req.v.match(/\./g)?.length === 2) {
        req.v = req.v.substring(0, req.v.lastIndexOf('.'));
      }
      return this.http.get<Vocabulary>(`${this.vocabularyStaticUrl}/${notation}/${req.v}/${notation}_${req.v}.json`, {
        params: options,
        observe: 'response',
      });
    }
    return this.http.get<Vocabulary>(`${this.vocabularyStaticUrl}/${notation}/${notation}.json`, { params: options, observe: 'response' });
  }

  getVocabulary(notation: string, req?: Record<string, string>): Observable<HttpResponse<Vocabulary>> {
    const options = createRequestOption(req);
    if (req?.v) {
      if (req.v.match(/\./g)?.length === 2) {
        req.v = req.v.substring(0, req.v.lastIndexOf('.'));
      }
      return this.http.get<Vocabulary>(`${this.resourceDownloadUrl}/${notation}/${req.v}`, {
        params: options,
        observe: 'response',
      });
    }
    return this.http.get<Vocabulary>(`${this.resourceDownloadUrl}/${notation}/latest`, { params: options, observe: 'response' });
  }

  downloadVocabularyFile(notation: string, slNumber: string, downloadType: string, req?: Record<string, string>): Observable<Blob> {
    const options = createRequestOption(req);
    return this.http.get(`${this.resourceDownloadUrl}/${downloadType}/${notation}/${slNumber}`, {
      params: options,
      responseType: 'blob',
    });
  }

  getVocabularyCompare(notation: string, lv1: string, lv2: string): Observable<HttpResponse<string[]>> {
    return this.http.get<string[]>(`${this.resourceCvCompareUrl}/${notation}/${lv1}/${lv2}`, { observe: 'response' });
  }

  getAvailableLanguagesIsos(req?: Record<string, string>): Observable<HttpResponse<string[]>> {
    const options = createRequestOption(req);
    return this.http.get<string[]>(`${SERVER_API_URL}/api/search/languages`, { params: options, observe: 'response' });
  }
}
