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
import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { Health, HealthDetails, HealthKey, HealthService, HealthStatus } from './health.service';
import { HealthModalComponent } from './health-modal.component';

@Component({
  selector: 'jhi-health',
  templateUrl: './health.component.html',
})
export class HealthComponent implements OnInit {
  health?: Health;

  constructor(
    private modalService: NgbModal,
    private healthService: HealthService,
  ) {}

  ngOnInit(): void {
    this.refresh();
  }

  getBadgeClass(statusState: HealthStatus): string {
    if (statusState === 'UP') {
      return 'text-bg-success';
    } else {
      return 'text-bg-danger';
    }
  }

  refresh(): void {
    this.healthService.checkHealth().subscribe(
      health => (this.health = health),
      (error: HttpErrorResponse) => {
        if (error.status === 503) {
          this.health = error.error;
        }
      },
    );
  }

  showHealth(health: { key: HealthKey; value: HealthDetails }): void {
    const modalRef = this.modalService.open(HealthModalComponent);
    modalRef.componentInstance.health = health;
  }
}
