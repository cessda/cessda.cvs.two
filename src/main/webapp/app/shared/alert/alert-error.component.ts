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
import { Component, inject, OnDestroy } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { TranslateService } from '@ngx-translate/core';
import { Alert, AlertService } from 'app/core/util/alert.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { Subscription } from 'rxjs';

import { AlertError } from './alert-error.model';

@Component({
  selector: 'jhi-alert-error',
  template: `
    <div class="alerts" role="alert">
      @for (alert of alerts; track alert) {
        <div [ngClass]="setClasses(alert)">
          @if (alert && alert.type && alert.msg) {
            <ngb-alert [type]="alert.type" (close)="alert.close && alert.close(alerts)">
              <pre [innerHTML]="alert.msg"></pre>
            </ngb-alert>
          }
        </div>
      }
    </div>
  `,
  standalone: false,
})
export class AlertErrorComponent implements OnDestroy {
  private alertService = inject(AlertService);
  private eventManager = inject(EventManager);
  private translateService = inject(TranslateService);

  alerts: Alert[] = [];
  errorListener: Subscription;
  httpErrorListener: Subscription;

  constructor() {
    this.errorListener = this.eventManager.subscribe(
      'cvsApp.error',
      (response: EventWithContent<AlertError>) => {
        const errorResponse = response.content;
        this.addErrorAlert(errorResponse.message, errorResponse.key, errorResponse.params);
      },
    );

    this.httpErrorListener = this.eventManager.subscribe(
      'cvsApp.httpError',
      (response: EventWithContent<HttpErrorResponse>) => {
        this.handleHttpError(response.content);
      },
    );
  }

  private handleHttpError(httpErrorResponse: HttpErrorResponse): void {
    switch (httpErrorResponse.status) {
      // Connection refused, server not reachable
      case 0:
        this.addErrorAlert('Server not reachable', 'error.server.not.reachable');
        break;

      case 400:
        this.handleBadRequest(httpErrorResponse);
        break;

      case 404:
        this.addErrorAlert('Not found', 'error.url.not.found');
        break;

      default:
        this.handleGenericHttpError(httpErrorResponse);
    }
  }

  private handleBadRequest(httpErrorResponse: HttpErrorResponse): void {
    const { errorHeader, entityKey } = this.getErrorHeaders(httpErrorResponse);

    if (errorHeader) {
      const entityName = this.translateService.instant('global.menu.entities.' + entityKey);
      this.addErrorAlert(errorHeader, errorHeader, { entityName });
    } else if (httpErrorResponse.error !== '' && httpErrorResponse.error.fieldErrors) {
      this.handleFieldErrors(httpErrorResponse.error.fieldErrors);
    } else if (httpErrorResponse.error !== '' && httpErrorResponse.error.message) {
      this.addErrorAlert(
        httpErrorResponse.error.message,
        httpErrorResponse.error.message,
        httpErrorResponse.error.params,
      );
    } else {
      this.addErrorAlert(httpErrorResponse.error);
    }
  }

  private getErrorHeaders(httpErrorResponse: HttpErrorResponse): {
    errorHeader: string | null;
    entityKey: string | null;
  } {
    let errorHeader: string | null = null;
    let entityKey: string | null = null;

    httpErrorResponse.headers.keys().forEach(entry => {
      if (entry.toLowerCase().endsWith('app-error')) {
        errorHeader = httpErrorResponse.headers.get(entry);
      } else if (entry.toLowerCase().endsWith('app-params')) {
        entityKey = httpErrorResponse.headers.get(entry);
      }
    });

    return { errorHeader, entityKey };
  }

  private handleFieldErrors(
    fieldErrors: Array<{ message: string; field: string; objectName: string }>,
  ): void {
    for (const fieldError of fieldErrors) {
      if (['Min', 'Max', 'DecimalMin', 'DecimalMax'].includes(fieldError.message)) {
        fieldError.message = 'Size';
      }

      // Convert 'something[14].other[4].id' to 'something[].other[].id'
      // so translations can be written to it.
      const convertedField = fieldError.field.replace(/\[\d*\]/g, '[]');
      const fieldName = this.translateService.instant(
        'cvsApp.' + fieldError.objectName + '.' + convertedField,
      );

      this.addErrorAlert('Error on field "' + fieldName + '"', 'error.' + fieldError.message, {
        fieldName,
      });
    }
  }

  private handleGenericHttpError(httpErrorResponse: HttpErrorResponse): void {
    if (httpErrorResponse.error !== '' && httpErrorResponse.error.message) {
      this.addErrorAlert(httpErrorResponse.error.message);
    } else {
      this.addErrorAlert(httpErrorResponse.error);
    }
  }

  setClasses(alert: Alert): { [key: string]: boolean } {
    const classes = { 'jhi-toast': Boolean(alert.toast) };
    if (alert.position) {
      return { ...classes, [alert.position]: true };
    }
    return classes;
  }

  ngOnDestroy(): void {
    if (this.errorListener) {
      this.eventManager.destroy(this.errorListener);
    }
    if (this.httpErrorListener) {
      this.eventManager.destroy(this.httpErrorListener);
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  addErrorAlert(message: string, key?: string, data?: any): void {
    message = key || message;

    const newAlert: Alert = {
      type: 'danger',
      msg: message,
      params: data,
      timeout: 5000,
      toast: this.alertService.isToast(),
      scoped: true,
    };

    this.alerts.push(this.alertService.addAlert(newAlert, this.alerts));
  }
}
