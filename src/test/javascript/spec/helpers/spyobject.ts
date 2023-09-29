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
export interface GuinessCompatibleSpy extends jasmine.Spy {
  /** By chaining the spy with and.returnValue, all calls to the function will return a specific
   * value. */
  andReturn(val: any): jasmine.Spy;
  /** By chaining the spy with and.callFake, all calls to the spy will delegate to the supplied
   * function. */
  andCallFake(fn: (...args: any[]) => any): jasmine.Spy;
  /** removes all recorded calls */
  reset(): void;
}

export class SpyObject {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any, @typescript-eslint/explicit-module-boundary-types
  constructor(type?: any) {
    if (type) {
      Object.keys(type.prototype).forEach(prop => {
        let m = null;
        try {
          m = type.prototype[prop];
        } catch (e) {
          // As we are creating spys for abstract classes,
          // these classes might have getters that throw when they are accessed.
          // As we are only auto creating spys for methods, this
          // should not matter.
        }
        if (typeof m === 'function') {
          this.spy(prop);
        }
      });
    }
  }

  spy(name: string): GuinessCompatibleSpy {
    //@ts-expect-error - not expressible in TypeScript
    if (!this[name]) {
      //@ts-expect-error - not expressible in TypeScript
      this[name] = this.createGuinnessCompatibleSpy(name);
    }
    //@ts-expect-error - not expressible in TypeScript
    return this[name];
  }

  private createGuinnessCompatibleSpy(name: string): GuinessCompatibleSpy {
    const newSpy: GuinessCompatibleSpy = jasmine.createSpy(name) as GuinessCompatibleSpy;
    newSpy.andCallFake = newSpy.and.callFake;
    newSpy.andReturn = newSpy.and.returnValue;
    newSpy.reset = newSpy.calls.reset;
    // revisit return null here (previously needed for rtts_assert).
    newSpy.and.returnValue(null);
    return newSpy;
  }
}
