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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal, NgbModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { JvmMemoryComponent } from 'app/admin/metrics/blocks/jvm-memory/jvm-memory.component';
import { JvmThreadsComponent } from 'app/admin/metrics/blocks/jvm-threads/jvm-threads.component';
import { ThreadModalComponent } from 'app/admin/metrics/blocks/jvm-threads/thread-modal.component';
import { MetricsSystemComponent } from 'app/admin/metrics/blocks/metrics-system/metrics-system.component';
import { MetricsGarbageCollectorComponent } from 'app/admin/metrics/blocks/metrics-garbagecollector/metrics-garbagecollector.component';
import { MetricsRequestComponent } from 'app/admin/metrics/blocks/metrics-request/metrics-request.component';
import { MetricsEndpointsRequestsComponent } from 'app/admin/metrics/blocks/metrics-endpoints-requests/metrics-endpoints-requests.component';
import { MetricsCacheComponent } from 'app/admin/metrics/blocks/metrics-cache/metrics-cache.component';
import { MetricsDatasourceComponent } from 'app/admin/metrics/blocks/metrics-datasource/metrics-datasource.component';
import { KeysPipe } from 'app/shared/pipe/keys.pipe';
import { PureFilterPipe } from 'app/shared/pipe/pure-filter.pipe';
import { TranslateDirective } from 'app/shared/language/translate.directive';

describe('Metrics blocks', () => {
  const threads = [
    {
      threadId: 1,
      threadName: 'main',
      threadState: 'RUNNABLE',
      lockName: null,
      stackTrace: [{ className: 'Main', methodName: 'run', fileName: 'Main.java', lineNumber: 7 }],
    },
    { threadId: 2, threadName: 'pool-1', threadState: 'WAITING', lockName: 'java.lang.Object@1' },
    { threadId: 3, threadName: 'timer', threadState: 'TIMED_WAITING', lockName: 'java.util.TaskQueue@2' },
    { threadId: 4, threadName: 'worker', threadState: 'BLOCKED', lockName: 'java.lang.Object@1' },
    { threadId: 5, threadName: 'other', threadState: 'NEW', lockName: null },
  ];
  const timer = { count: 4, mean: 2.5, max: 9, '0.0': 1, '0.5': 2, '0.75': 3, '0.95': 4, '0.99': 5 };
  const modalService = { open: jest.fn() };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [NgbModule, FormsModule],
      declarations: [
        JvmMemoryComponent,
        JvmThreadsComponent,
        ThreadModalComponent,
        MetricsSystemComponent,
        MetricsGarbageCollectorComponent,
        MetricsRequestComponent,
        MetricsEndpointsRequestsComponent,
        MetricsCacheComponent,
        MetricsDatasourceComponent,
        KeysPipe,
        PureFilterPipe,
        TranslateDirective,
      ],
      providers: [NgbActiveModal, { provide: NgbModal, useValue: modalService }],
    });
  });

  function render<T>(component: new (...args: never[]) => T, inputs: { [name: string]: unknown }): ComponentFixture<T> {
    const fixture = TestBed.createComponent(component);
    Object.entries(inputs).forEach(([name, value]) => fixture.componentRef.setInput(name, value));
    fixture.detectChanges();
    return fixture;
  }

  const text = (fixture: ComponentFixture<unknown>): string => fixture.nativeElement.textContent.replace(/\s+/g, ' ');
  const cells = (fixture: ComponentFixture<unknown>, row: number): string[] =>
    Array.from<HTMLElement>(fixture.nativeElement.querySelectorAll('tbody tr')[row].querySelectorAll('td')).map(cell =>
      cell.textContent!.trim(),
    );

  it('should show the memory pools, with a bar only for the bounded ones', () => {
    const fixture = render(JvmMemoryComponent, {
      updating: false,
      jvmMemoryMetrics: {
        heap: { used: 104857600, max: 209715200, committed: 157286400 },
        'Code Cache': { used: 10485760, max: -1, committed: 20971520 },
      },
    });

    expect(text(fixture)).toContain('heap (100M / 200M)');
    expect(text(fixture)).toContain('Code Cache 10M');
    expect(text(fixture)).toContain('Committed : 150M');
    expect(fixture.nativeElement.querySelectorAll('ngb-progressbar')).toHaveLength(1);
  });

  it('should count the threads per state and open the dump', () => {
    const fixture = render(JvmThreadsComponent, { threadData: threads });

    expect(fixture.componentInstance.threadStats).toEqual({
      threadDumpAll: 4,
      threadDumpRunnable: 1,
      threadDumpWaiting: 1,
      threadDumpTimedWaiting: 1,
      threadDumpBlocked: 1,
    });

    const modalRef = { componentInstance: {} as ThreadModalComponent };
    modalService.open.mockReturnValue(modalRef);
    fixture.componentInstance.open();

    expect(modalService.open).toHaveBeenCalledWith(ThreadModalComponent);
    expect(modalRef.componentInstance.threadDump).toBe(threads);
  });

  it('should count no threads before the dump has arrived', () => {
    const fixture = render(JvmThreadsComponent, {});

    expect(fixture.componentInstance.threadStats.threadDumpAll).toBe(0);
  });

  it('should list the dumped threads, filtered by state or lock name', () => {
    const fixture = TestBed.createComponent(ThreadModalComponent);
    const modal = fixture.componentInstance;
    modal.threadDump = threads;
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('.pad')).toHaveLength(5);
    expect(text(fixture)).toContain('Main.run(Main.java:7)');
    expect(threads.map(thread => modal.getBadgeClass(thread.threadState))).toEqual([
      'badge-success',
      'badge-info',
      'badge-warning',
      'badge-danger',
      '',
    ]);

    modal.threadDumpFilter = { threadState: 'BLOCKED' };
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelectorAll('.pad')).toHaveLength(1);

    modal.threadDumpFilter = 'object@1';
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelectorAll('.pad')).toHaveLength(2);
  });

  it('should show the system metrics, with the uptime as a duration', () => {
    const fixture = render(MetricsSystemComponent, {
      updating: false,
      systemMetrics: { 'process.uptime': 90061000, 'process.cpu.usage': 0.25, 'system.cpu.usage': 0.5, 'system.cpu.count': 8 },
    });

    expect(text(fixture)).toContain('1 day 1 hour 1 minute 1 second');
    expect(text(fixture)).toContain('25 %');
    expect(fixture.componentInstance.convertMillisecondsToDuration(2 * 86400000 + 2000)).toBe('2 days 2 seconds ');
  });

  it('should show the garbage collector metrics', () => {
    const fixture = render(MetricsGarbageCollectorComponent, {
      updating: false,
      garbageCollectorMetrics: {
        'jvm.gc.live.data.size': 52428800,
        'jvm.gc.max.data.size': 104857600,
        'jvm.gc.memory.promoted': 1048576,
        'jvm.gc.memory.allocated': 4194304,
        classesLoaded: 12000,
        classesUnloaded: 3,
        'jvm.gc.pause': timer,
      },
    });

    expect(text(fixture)).toContain('(50M / 100M)');
    expect(text(fixture)).toContain('12000');
    expect(text(fixture)).toContain('jvm.gc.pause');
  });

  it('should show the HTTP requests per status code', () => {
    const fixture = render(MetricsRequestComponent, {
      updating: false,
      requestMetrics: {
        all: { count: 10 },
        percode: { '200': { count: 9, mean: 12.345, max: 30 }, '404': { count: 1, mean: NaN, max: 0 } },
      },
    });

    expect(fixture.nativeElement.querySelectorAll('tbody tr')).toHaveLength(2);
    expect(text(fixture)).toContain('12.35');
    expect(fixture.componentInstance.filterNaN(NaN)).toBe(0);
  });

  it('should show the requests per endpoint and method', () => {
    const fixture = render(MetricsEndpointsRequestsComponent, {
      updating: false,
      endpointsRequestsMetrics: { '/api/vocabularies': { GET: { count: 5, mean: 1.5 }, POST: { count: 1, mean: 3 } } },
    });

    expect(fixture.nativeElement.querySelectorAll('tbody tr')).toHaveLength(2);
    expect(cells(fixture, 0)).toEqual(['GET', '/api/vocabularies', '5', '1.5']);
  });

  it('should show the cache statistics', () => {
    const fixture = render(MetricsCacheComponent, {
      updating: false,
      cacheMetrics: {
        users: { 'cache.gets.hit': 3, 'cache.gets.miss': 1, 'cache.puts': 1, 'cache.removals': 0, 'cache.evictions': 0 },
        empty: { 'cache.gets.hit': 0, 'cache.gets.miss': 0 },
      },
    });

    expect(cells(fixture, 0)).toEqual(['users', '3', '1', '4', '1', '0', '0', '75', '25']);
    expect(cells(fixture, 1).slice(-2)).toEqual(['0', '0']);
    expect(fixture.componentInstance.filterNaN(0 / 0)).toBe(0);
  });

  it('should show the datasource statistics', () => {
    const fixture = render(MetricsDatasourceComponent, {
      updating: false,
      datasourceMetrics: {
        active: { value: 1 },
        min: { value: 0 },
        max: { value: 10 },
        idle: { value: 4 },
        acquire: timer,
        creation: timer,
        usage: { ...timer, mean: NaN },
      },
    });

    expect(text(fixture)).toContain('(active: 1, min: 0, max: 10, idle: 4)');
    expect(fixture.nativeElement.querySelectorAll('tbody tr')).toHaveLength(3);
  });

  it('should hide the tables while updating', () => {
    const fixture = render(MetricsCacheComponent, { updating: true, cacheMetrics: {} });

    expect(fixture.nativeElement.querySelector('table')).toBeNull();
  });
});
