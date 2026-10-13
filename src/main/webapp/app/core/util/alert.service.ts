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
import { Injectable, NgZone, SecurityContext, inject } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';

export type AlertType = 'success' | 'danger' | 'warning' | 'info';

export interface Alert {
  id?: number;
  type: AlertType;
  msg: string;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  params?: any;
  timeout?: number;
  toast?: boolean;
  position?: string;
  scoped?: boolean;
  close?: (alerts: Alert[]) => void;
}

const ALERT_TIMEOUT = 5000;

/**
 * Keeps the list of alerts shown by the alert components, replacing the one formerly provided by ng-jhipster.
 * Messages are translation keys, translated when the alert is added.
 */
@Injectable({ providedIn: 'root' })
export class AlertService {
  private sanitizer = inject(DomSanitizer);
  private ngZone = inject(NgZone);
  private translateService = inject(TranslateService, { optional: true });

  private alertId = 0;
  private alerts: Alert[] = [];

  clear(): void {
    this.alerts.splice(0, this.alerts.length);
  }

  get(): Alert[] {
    return this.alerts;
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  success(msg: string, params?: any, position?: string): Alert {
    return this.addAlert({ type: 'success', msg, params, timeout: ALERT_TIMEOUT, toast: this.isToast(), position }, []);
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  error(msg: string, params?: any, position?: string): Alert {
    return this.addAlert({ type: 'danger', msg, params, timeout: ALERT_TIMEOUT, toast: this.isToast(), position }, []);
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  warning(msg: string, params?: any, position?: string): Alert {
    return this.addAlert({ type: 'warning', msg, params, timeout: ALERT_TIMEOUT, toast: this.isToast(), position }, []);
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  info(msg: string, params?: any, position?: string): Alert {
    return this.addAlert({ type: 'info', msg, params, timeout: ALERT_TIMEOUT, toast: this.isToast(), position }, []);
  }

  addAlert(alertOptions: Alert, extAlerts: Alert[]): Alert {
    const id = this.alertId++;
    const msg =
      this.translateService && alertOptions.msg ? this.translateService.instant(alertOptions.msg, alertOptions.params) : alertOptions.msg;
    const alert: Alert = {
      type: alertOptions.type,
      msg: this.sanitizer.sanitize(SecurityContext.HTML, msg) ?? '',
      id,
      timeout: alertOptions.timeout,
      toast: alertOptions.toast,
      position: alertOptions.position ?? 'top right',
      scoped: alertOptions.scoped,
      close: (alerts: Alert[]) => this.closeAlert(id, alerts),
    };
    if (!alert.scoped) {
      this.alerts.push(alert);
    }
    if (alertOptions.timeout && alertOptions.timeout > 0) {
      // keep the pending timeout out of Angular's zone so it does not hold up stability checks
      this.ngZone.runOutsideAngular(() => {
        setTimeout(() => this.ngZone.run(() => this.closeAlert(id, extAlerts)), alertOptions.timeout);
      });
    }
    return alert;
  }

  closeAlert(id: number, extAlerts?: Alert[]): Alert[] {
    const alerts = extAlerts && extAlerts.length > 0 ? extAlerts : this.alerts;
    const index = alerts.findIndex(alert => alert.id === id);
    return index === -1 ? [] : alerts.splice(index, 1);
  }

  isToast(): boolean {
    return false;
  }
}
