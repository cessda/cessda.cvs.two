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

import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';

describe('EventManager', () => {
  let eventManager: EventManager;

  beforeEach(() => {
    eventManager = TestBed.inject(EventManager);
  });

  it('should pass an event with content to the subscribers of its name only', () => {
    const received: EventWithContent<number>[] = [];
    const other = jest.fn();
    eventManager.subscribe('counted', (event: EventWithContent<number>) => received.push(event));
    eventManager.subscribe('other', other);

    eventManager.broadcast(new EventWithContent('counted', 42));

    expect(received).toEqual([new EventWithContent('counted', 42)]);
    expect(other).not.toHaveBeenCalled();
  });

  it('should notify the subscribers of an event broadcast by its name alone, without content', () => {
    const callback = jest.fn();
    eventManager.subscribe('listModification', callback);

    eventManager.broadcast('listModification');

    expect(callback).toHaveBeenCalledWith(undefined);
  });

  it('should accept a plain object carrying a name and content', () => {
    const callback = jest.fn();
    eventManager.subscribe('selectConcept', callback);

    eventManager.broadcast({ name: 'selectConcept', content: 'concept' });

    expect(callback).toHaveBeenCalledWith({ name: 'selectConcept', content: 'concept' });
  });

  it('should stop notifying a destroyed subscription', () => {
    const callback = jest.fn();
    const subscription = eventManager.subscribe('event', callback);

    eventManager.destroy(subscription);
    eventManager.broadcast('event');

    expect(callback).not.toHaveBeenCalled();
  });
});
