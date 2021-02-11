/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

import {Component, OnDestroy, OnInit} from '@angular/core';
import {IMetadataField, MetadataField} from 'app/shared/model/metadata-field.model';
import {EditorService} from 'app/editor/editor.service';
import {MetadataFieldService} from 'app/entities/metadata-field/metadata-field.service';
import {METADATA_KEY_USERGUIDE} from 'app/shared/constants/metadata.constants';
import {HttpResponse} from '@angular/common/http';
import {IMetadataValue, MetadataValue} from 'app/shared/model/metadata-value.model';
import {Subscription} from 'rxjs';
import {JhiEventManager} from 'ng-jhipster';

@Component({
  selector: 'jhi-user-guide',
  templateUrl: './user-guide.component.html'
})
export class UserGuideComponent implements OnInit, OnDestroy {
  metadataField?: IMetadataField | null;
  metadataValues: IMetadataValue[] = [];
  metadataKey = METADATA_KEY_USERGUIDE;

  eventSubscriber?: Subscription;

  constructor(
    protected editorService: EditorService,
    private metadataFieldService: MetadataFieldService,
    protected eventManager: JhiEventManager
  ) {}

  ngOnInit(): void {
    this.refreshContent();
    this.eventSubscriber = this.eventManager.subscribe('metadataListModification', () => this.refreshContent());
  }

  private refreshContent(): void {
    this.metadataFieldService.findByKey(this.metadataKey).subscribe((res: HttpResponse<IMetadataField>) => {
      if (res.body !== null) {
        this.metadataField = res.body;
      } else {
        this.metadataField = { ...new MetadataField(), metadataKey: this.metadataKey, metadataValues: [] };
      }
      if (this.metadataField && this.metadataField.metadataValues) {
        this.metadataValues = this.metadataField.metadataValues;
      }
    });
  }

  addSection(): void {
    this.metadataField!.metadataValues!.push(new MetadataValue());
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventSubscriber.unsubscribe();
    }
  }
}
