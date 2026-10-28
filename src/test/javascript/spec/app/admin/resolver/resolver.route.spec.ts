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

import { ResolverResolve } from 'app/admin/resolver/resolver.route';
import { ResolverService } from 'app/admin/resolver/resolver.service';
import { Resolver } from 'app/shared/model/resolver.model';

describe('ResolverResolve', () => {
  let resolve: ResolverResolve;
  let service: { find: jest.Mock };
  let router: { navigate: jest.Mock };

  const routeWithId = (id?: string): ActivatedRouteSnapshot => ({ params: id ? { id } : {} }) as unknown as ActivatedRouteSnapshot;

  const collect = (observable: Observable<Resolver>): Resolver[] => {
    const values: Resolver[] = [];
    observable.subscribe(value => values.push(value));
    return values;
  };

  beforeEach(() => {
    service = { find: jest.fn() };
    router = { navigate: jest.fn() };
    TestBed.configureTestingModule({
      providers: [
        { provide: ResolverService, useValue: service },
        { provide: Router, useValue: router },
      ],
    });
    resolve = TestBed.inject(ResolverResolve);
  });

  it('should resolve the resolver found by id', () => {
    const resolver: Resolver = { id: 3, resolverURI: 'https://doi.org/10.1234/cv', resourceUrl: 'https://example.org/cv' };
    service.find.mockReturnValue(of(new HttpResponse({ body: resolver })));

    expect(collect(resolve.resolve(routeWithId('3')))).toEqual([resolver]);
    expect(service.find).toHaveBeenCalledWith('3');
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should resolve an empty resolver when the route has no id', () => {
    expect(collect(resolve.resolve(routeWithId()))).toEqual([{ resolverURI: '', resourceUrl: '' }]);
    expect(service.find).not.toHaveBeenCalled();
  });

  it('should navigate to the not found page when the resolver does not exist', () => {
    service.find.mockReturnValue(of(new HttpResponse<Resolver>({ body: null })));

    expect(collect(resolve.resolve(routeWithId('404')))).toEqual([]);
    expect(router.navigate).toHaveBeenCalledWith(['404'], { skipLocationChange: true });
  });
});
