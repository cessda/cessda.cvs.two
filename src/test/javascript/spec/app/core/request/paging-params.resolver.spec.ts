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
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

import { PagingParams, pagingParamsResolver } from 'app/core/request/paging-params.resolver';

describe('pagingParamsResolver', () => {
  const resolve = (queryParams: { [key: string]: string }, data: { [key: string]: string } = {}): PagingParams =>
    pagingParamsResolver({ queryParams, data } as unknown as ActivatedRouteSnapshot, {} as RouterStateSnapshot) as PagingParams;

  it('should default to the first page sorted by id ascending', () => {
    expect(resolve({})).toEqual({ page: 1, predicate: 'id', ascending: true });
  });

  it("should fall back on the route's default sort", () => {
    expect(resolve({}, { defaultSort: 'auditEventDate,desc' })).toEqual({ page: 1, predicate: 'auditEventDate', ascending: false });
  });

  it('should take the page and sort from the query parameters', () => {
    expect(resolve({ page: '3', sort: 'name,desc' }, { defaultSort: 'id,asc' })).toEqual({ page: 3, predicate: 'name', ascending: false });
  });

  it('should read a sort whose comma is URL encoded', () => {
    expect(resolve({ sort: 'name%2Cdesc' })).toEqual({ page: 1, predicate: 'name', ascending: false });
  });

  it('should sort ascending when no direction is given', () => {
    expect(resolve({ sort: 'name' })).toEqual({ page: 1, predicate: 'name', ascending: true });
  });
});
