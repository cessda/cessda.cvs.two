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
import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

import { Alert, AlertService } from 'app/core/util/alert.service';

describe('AlertService', () => {
  let alertService: AlertService;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [TranslateModule.forRoot()] });
    TestBed.inject(TranslateService).setTranslation('en', { saved: 'Saved {{ name }}' });
    TestBed.inject(TranslateService).use('en');
    alertService = TestBed.inject(AlertService);
  });

  it('should add a translated alert of each type to the shared list', () => {
    alertService.success('saved', { name: 'CV' });
    alertService.error('error.message');
    alertService.warning('warning.message');
    alertService.info('info.message');

    expect(alertService.get().map(alert => [alert.type, alert.msg])).toEqual([
      ['success', 'Saved CV'],
      ['danger', 'error.message'],
      ['warning', 'warning.message'],
      ['info', 'info.message'],
    ]);
    expect(alertService.get().every(alert => alert.position === 'top right' && !alert.toast)).toBe(true);
  });

  it('should strip unsafe markup from the message', () => {
    // Angular warns about the content it strips
    jest.spyOn(console, 'warn').mockImplementation(() => undefined);

    const alert = alertService.success('<img src="x" onerror="alert(1)">');

    expect(alert.msg).not.toContain('onerror');
  });

  it('should close an alert when its timeout has passed', fakeAsync(() => {
    alertService.success('saved');

    tick(4999);
    expect(alertService.get()).toHaveLength(1);
    tick(1);
    expect(alertService.get()).toHaveLength(0);
  }));

  it('should leave a scoped alert to the list of the component that keeps it', fakeAsync(() => {
    const scopedAlerts: Alert[] = [];

    scopedAlerts.push(alertService.addAlert({ type: 'danger', msg: 'error', timeout: 1000, scoped: true }, scopedAlerts));

    expect(alertService.get()).toHaveLength(0);
    expect(scopedAlerts).toHaveLength(1);
    tick(1000);
    expect(scopedAlerts).toHaveLength(0);
  }));

  it('should close an alert through its own close function, and leave the list alone when it is already gone', () => {
    const alert = alertService.addAlert({ type: 'info', msg: 'info' }, []);

    alert.close!(alertService.get());
    alert.close!(alertService.get());

    expect(alertService.get()).toHaveLength(0);
  });

  it('should empty the list when cleared', () => {
    alertService.info('info');

    alertService.clear();

    expect(alertService.get()).toHaveLength(0);
  });

  it('should keep the message untranslated when no translations are available', () => {
    TestBed.resetTestingModule();
    const untranslated = TestBed.inject(AlertService).success('saved');

    expect(untranslated.msg).toBe('saved');
  });
});
