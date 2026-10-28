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

import { LicenceResolve } from 'app/admin/licence/licence.route';
import { LicenceService } from 'app/admin/licence/licence.service';
import { Licence } from 'app/shared/model/licence.model';

describe('LicenceResolve', () => {
  let resolve: LicenceResolve;
  let service: { find: jest.Mock };
  let router: { navigate: jest.Mock };

  const routeWithId = (id?: string): ActivatedRouteSnapshot => ({ params: id ? { id } : {} }) as unknown as ActivatedRouteSnapshot;

  const collect = (observable: Observable<Licence>): Licence[] => {
    const values: Licence[] = [];
    observable.subscribe(value => values.push(value));
    return values;
  };

  beforeEach(() => {
    service = { find: jest.fn() };
    router = { navigate: jest.fn() };
    TestBed.configureTestingModule({
      providers: [
        { provide: LicenceService, useValue: service },
        { provide: Router, useValue: router },
      ],
    });
    resolve = TestBed.inject(LicenceResolve);
  });

  it('should resolve the licence found by id', () => {
    const licence: Licence = { id: 7, name: 'Creative Commons Attribution 4.0' };
    service.find.mockReturnValue(of(new HttpResponse({ body: licence })));

    expect(collect(resolve.resolve(routeWithId('7')))).toEqual([licence]);
    expect(service.find).toHaveBeenCalledWith('7');
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should resolve an empty licence when the route has no id', () => {
    expect(collect(resolve.resolve(routeWithId()))).toEqual([{ name: '' }]);
    expect(service.find).not.toHaveBeenCalled();
  });

  it('should navigate to the not found page when the licence does not exist', () => {
    service.find.mockReturnValue(of(new HttpResponse<Licence>({ body: null })));

    expect(collect(resolve.resolve(routeWithId('404')))).toEqual([]);
    expect(router.navigate).toHaveBeenCalledWith(['404'], { skipLocationChange: true });
  });
});
