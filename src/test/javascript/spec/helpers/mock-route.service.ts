/* eslint-disable @typescript-eslint/no-explicit-any */
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
import Spy = jasmine.Spy;
import { ActivatedRoute, Router, RouterEvent } from '@angular/router';
import { Observable, of } from 'rxjs';

import { SpyObject } from './spyobject';

export class MockActivatedRoute extends ActivatedRoute {
  constructor(parameters?: any) {
    super();
    this.queryParams = of(parameters);
    this.params = of(parameters);
    this.data = of({
      ...parameters,
      pagingParams: {
        page: 10,
        ascending: false,
        predicate: 'id',
      },
    });
  }
}

export class MockRouter extends SpyObject {
  navigateSpy: Spy;
  navigateByUrlSpy: Spy;
  events: Observable<RouterEvent> | null = null;
  routerState: any;
  url = '';

  constructor() {
    super(Router);
    this.navigateSpy = this.spy('navigate');
    this.navigateByUrlSpy = this.spy('navigateByUrl');
  }

  setEvents(events: Observable<RouterEvent>): void {
    this.events = events;
  }

  setRouterState(routerState: any): void {
    this.routerState = routerState;
  }
}
