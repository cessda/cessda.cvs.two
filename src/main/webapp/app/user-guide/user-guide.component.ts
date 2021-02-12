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
  template: '<jhi-custom-page [pageType]="\'user-guide\'"></jhi-custom-page>'
})
export class UserGuideComponent {
  constructor() {}
}
