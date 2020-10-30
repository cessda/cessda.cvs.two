import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {SERVER_API_URL} from 'app/app.constants';
import {createRequestOption} from 'app/shared';
import {IVocabulary} from 'app/shared/model/vocabulary.model';
import {ICvResult} from 'app/shared/model/cv-result.model';

type EntityResponseType = HttpResponse<IVocabulary>;

@Injectable({ providedIn: 'root' })
export class HomeService {
  public resourceUrl = SERVER_API_URL + 'api/vocabulary';
  public vocabularyStaticUrl = SERVER_API_URL + 'content/vocabularies';
  public resourceSearchUrl = SERVER_API_URL + 'v2/search';
  public resourceDownloadUrl = SERVER_API_URL + 'v2/download';
  public resourceCvCompareUrl = SERVER_API_URL + 'v2/compare-vocabulary';

  constructor(protected http: HttpClient) {}

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IVocabulary>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req?: any): Observable<HttpResponse<ICvResult>> {
    const options = createRequestOption(req);
    return this.http.get<ICvResult>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getVocabularyFile(notation: string, req?: any): Observable<HttpResponse<IVocabulary>> {
    const options = createRequestOption(req);
    if (req.v) {
      if (req.v.match(/\./g).length === 2) {
        req.v = req.v.substring(0, req.v.lastIndexOf('.'));
      }
      return this.http.get<IVocabulary>(`${this.vocabularyStaticUrl}/${notation}/${req.v}/${notation}_${req.v}.json`, {
        params: options,
        observe: 'response'
      });
    }
    return this.http.get<IVocabulary>(`${this.vocabularyStaticUrl}/${notation}/${notation}.json`, { params: options, observe: 'response' });
  }

  downloadVocabularyFile(notation: string, slNumber: string, downloadType: string, req?: any): Observable<Blob> {
    const options = createRequestOption(req);
    return this.http.get(`${this.resourceDownloadUrl}/${downloadType}/${notation}/${slNumber}`, {
      params: options,
      responseType: 'blob'
    });
  }

  getVocabularyCompare(notation: string, lv1: string, lv2: string): Observable<HttpResponse<string[]>> {
    return this.http.get<string[]>(`${this.resourceCvCompareUrl}/${notation}/${lv1}/${lv2}`, { observe: 'response' });
  }
}
