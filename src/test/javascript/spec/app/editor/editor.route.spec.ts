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
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of } from 'rxjs';

import { VocabularyResolve } from 'app/editor/editor.route';
import { EditorService } from 'app/editor/editor.service';
import { createNewVocabulary, Vocabulary } from 'app/shared/model/vocabulary.model';

describe('VocabularyResolve', () => {
  let resolve: VocabularyResolve;
  let service: { getVocabulary: jest.Mock };
  let router: { navigate: jest.Mock };

  const routeWithNotation = (notation?: string): ActivatedRouteSnapshot =>
    ({ params: notation ? { notation } : {} }) as unknown as ActivatedRouteSnapshot;

  const collect = (observable: Observable<Vocabulary>): Vocabulary[] => {
    const values: Vocabulary[] = [];
    observable.subscribe(value => values.push(value));
    return values;
  };

  beforeEach(() => {
    service = { getVocabulary: jest.fn() };
    router = { navigate: jest.fn() };
    TestBed.configureTestingModule({
      providers: [
        { provide: EditorService, useValue: service },
        { provide: Router, useValue: router },
      ],
    });
    resolve = TestBed.inject(VocabularyResolve);
  });

  it('should resolve the vocabulary with its source language selected', () => {
    const vocabulary = createNewVocabulary({ notation: 'TopicClassification', sourceLanguage: 'de', selectedLang: 'en' });
    service.getVocabulary.mockReturnValue(of(new HttpResponse({ body: vocabulary })));

    const values = collect(resolve.resolve(routeWithNotation('TopicClassification')));

    expect(service.getVocabulary).toHaveBeenCalledWith('TopicClassification');
    expect(values).toHaveLength(1);
    expect(values[0].notation).toBe('TopicClassification');
    expect(values[0].selectedLang).toBe('de');
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should resolve a new vocabulary when the route has no notation', () => {
    expect(collect(resolve.resolve(routeWithNotation()))).toEqual([createNewVocabulary()]);
    expect(service.getVocabulary).not.toHaveBeenCalled();
  });

  it('should navigate to the not found page when the vocabulary does not exist', () => {
    service.getVocabulary.mockReturnValue(of(new HttpResponse<Vocabulary>({ body: null })));

    expect(collect(resolve.resolve(routeWithNotation('Missing')))).toEqual([]);
    expect(router.navigate).toHaveBeenCalledWith(['404'], { skipLocationChange: true });
  });
});
