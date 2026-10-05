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
import { ActivatedRoute, convertToParamMap, Event, NavigationExtras, Params, Router } from '@angular/router';
import { BehaviorSubject, Subject } from 'rxjs';
import { map } from 'rxjs/operators';

/**
 * Stands in for both the router and the route of a component that keeps its state in the query
 * string, such as the search page or the user list.
 *
 * Navigating changes the query parameters the route emits, the way the router does, so a spec
 * follows the whole round trip: the component asks for a page, the URL changes, and the component
 * reacts to the new URL. MockRouter only records the navigation, which is how the pagination bugs
 * of #816 and #820 went unnoticed.
 */
export class QueryParamsNavigation {
  private readonly queryParams = new BehaviorSubject<Params>({});

  readonly route = {
    queryParams: this.queryParams.asObservable(),
    queryParamMap: this.queryParams.pipe(map(params => convertToParamMap(params))),
  } as unknown as ActivatedRoute;

  readonly events = new Subject<Event>();

  readonly navigate = jasmine.createSpy('navigate').and.callFake((_commands: unknown[], extras?: NavigationExtras) => {
    const params: Params = extras?.queryParamsHandling === 'merge' ? { ...this.current } : {};
    Object.entries(extras?.queryParams ?? {}).forEach(([key, value]) => {
      // the router leaves out a parameter given as null or undefined
      if (value === null || value === undefined) {
        delete params[key];
      } else {
        params[key] = value;
      }
    });
    this.queryParams.next(params);
    return Promise.resolve(true);
  });

  readonly router = { events: this.events.asObservable(), navigate: this.navigate } as unknown as Router;

  /** The query parameters in the URL now */
  get current(): Params {
    return this.queryParams.value;
  }

  /** Opens the page at a URL with the given query parameters, as following a link or going back would */
  open(params: Params): void {
    this.queryParams.next(params);
  }

  providers(): { provide: unknown; useValue: unknown }[] {
    return [
      { provide: ActivatedRoute, useValue: this.route },
      { provide: Router, useValue: this.router },
    ];
  }
}
