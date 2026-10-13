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
import { Injectable } from '@angular/core';
import { Subject, Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';

export class EventWithContent<T> {
  constructor(
    public name: string,
    public content: T,
  ) {}
}

/**
 * An application wide event bus, replacing the one formerly provided by ng-jhipster.
 * Events are either plain names or {@link EventWithContent} instances; subscribers receive the event as broadcast.
 */
@Injectable({ providedIn: 'root' })
export class EventManager {
  private readonly events = new Subject<EventWithContent<unknown> | string>();

  broadcast(event: EventWithContent<unknown> | string): void {
    this.events.next(event);
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  subscribe(eventName: string, callback: (event: any) => void): Subscription {
    return this.events
      .pipe(filter(event => (typeof event === 'string' ? event : event.name) === eventName))
      .subscribe(event => callback(typeof event === 'string' ? undefined : event));
  }

  destroy(subscriber: Subscription): void {
    subscriber.unsubscribe();
  }
}
