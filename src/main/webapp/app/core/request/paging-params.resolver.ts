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
import { ActivatedRouteSnapshot, ResolveFn } from '@angular/router';

export interface PagingParams {
  page: number;
  predicate: string;
  ascending: boolean;
}

/**
 * Resolves the page and sort order from the `page` and `sort` query parameters (e.g. `?page=2&sort=name,desc`),
 * falling back to page 1 and the route's `defaultSort` data (or `id,asc`).
 * Replaces the resolver formerly provided by ng-jhipster.
 */
export const pagingParamsResolver: ResolveFn<PagingParams> = (route: ActivatedRouteSnapshot) => {
  const page: string = route.queryParams['page'] || '1';
  const sort: string = route.queryParams['sort'] || route.data['defaultSort'] || 'id,asc';
  return {
    page: parseInt(page, 10),
    predicate: parsePredicate(sort),
    ascending: parseAscending(sort),
  };
};

function parsePredicate(sort: string): string {
  return sort.split(',')[0].split('%2C')[0];
}

function parseAscending(sort: string): boolean {
  let sortArray = sort.split(',');
  sortArray = sortArray.length > 1 ? sortArray : sort.split('%2C');
  // default to ascending when no direction is given
  return sortArray.length > 1 ? sortArray[sortArray.length - 1] === 'asc' : true;
}
