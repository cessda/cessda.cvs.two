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
import { HttpHandler, HttpHeaders, HttpRequest, HttpResponse, HttpSentEvent } from '@angular/common/http';
import { JhiAlertService } from 'ng-jhipster';
import { of } from 'rxjs';

import { NotificationInterceptor } from 'app/blocks/interceptor/notification.interceptor';

describe('NotificationInterceptor', () => {
  let interceptor: NotificationInterceptor;
  let success: jest.Mock;

  // JHipster's HeaderUtil emits X-<clientApp>-alert and X-<clientApp>-params; the interceptor
  // matches on the suffix, so the application name in front of it does not matter
  const respondWith = (headers: Record<string, string>): HttpHandler =>
    ({ handle: () => of(new HttpResponse({ headers: new HttpHeaders(headers) })) }) as unknown as HttpHandler;

  const request = new HttpRequest('GET', '/api/licences');

  beforeEach(() => {
    success = jest.fn();
    TestBed.configureTestingModule({
      providers: [NotificationInterceptor, { provide: JhiAlertService, useValue: { success } }],
    });
    interceptor = TestBed.inject(NotificationInterceptor);
  });

  it('should raise the alert the server asked for', () => {
    interceptor.intercept(request, respondWith({ 'X-cvsApp-alert': 'cvsApp.licence.created' })).subscribe();

    expect(success).toHaveBeenCalledWith('cvsApp.licence.created', { param: null });
  });

  it('should pass the alert parameters through, decoded', () => {
    interceptor.intercept(request, respondWith({ 'X-cvsApp-alert': 'cvsApp.licence.created', 'X-cvsApp-params': 'CC+BY+4.0' })).subscribe();

    expect(success).toHaveBeenCalledWith('cvsApp.licence.created', { param: 'CC BY 4.0' });
  });

  it('should decode percent escapes in the parameters as well', () => {
    interceptor.intercept(request, respondWith({ 'X-cvsApp-alert': 'a', 'X-cvsApp-params': 'Analysis%20Unit' })).subscribe();

    expect(success).toHaveBeenCalledWith('a', { param: 'Analysis Unit' });
  });

  it('should match the alert header whatever its case', () => {
    interceptor.intercept(request, respondWith({ 'x-CVSAPP-ALERT': 'shouted' })).subscribe();

    expect(success).toHaveBeenCalledWith('shouted', { param: null });
  });

  it.each([
    { name: 'the response carries no alert header', headers: {} },
    { name: 'only parameters arrive, with no alert to raise', headers: { 'X-cvsApp-params': 'orphaned' } },
  ])('should stay quiet when $name', ({ headers }) => {
    interceptor.intercept(request, respondWith(headers)).subscribe();

    expect(success).not.toHaveBeenCalled();
  });

  it('should ignore events that are not responses', () => {
    const sent = { type: 0 } as HttpSentEvent;

    interceptor.intercept(request, { handle: () => of(sent) } as unknown as HttpHandler).subscribe();

    expect(success).not.toHaveBeenCalled();
  });

  it('should pass the response through untouched', () => {
    let seen: HttpResponse<unknown> | undefined;

    interceptor.intercept(request, respondWith({ 'X-cvsApp-alert': 'kept' })).subscribe(event => (seen = event as HttpResponse<unknown>));

    expect(seen?.headers.get('X-cvsApp-alert')).toBe('kept');
  });
});
