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
/* eslint-disable @typescript-eslint/no-explicit-any */
import { Component, OnInit, inject } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { Thread } from '../../metrics.service';
import { ThreadStats, countThreadStates } from './thread-stats';

@Component({
  selector: 'jhi-thread-modal',
  templateUrl: './thread-modal.component.html',
  standalone: false,
})
export class ThreadModalComponent implements OnInit {
  activeModal = inject(NgbActiveModal);

  threadDump: Thread[] = [];
  threadDumpFilter: any = {};
  threadStats: ThreadStats = countThreadStates([]);

  ngOnInit(): void {
    this.threadStats = countThreadStates(this.threadDump);
  }

  getBadgeClass(threadState: string): string {
    switch (threadState) {
      case 'RUNNABLE':
        return 'badge-success';
      case 'WAITING':
        return 'badge-info';
      case 'TIMED_WAITING':
        return 'badge-warning';
      case 'BLOCKED':
        return 'badge-danger';
      default:
        return '';
    }
  }
}
