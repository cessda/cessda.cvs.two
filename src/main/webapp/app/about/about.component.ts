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

import {Component, OnInit} from '@angular/core';
import {EditorService} from 'app/editor/editor.service';
import {MetadataFieldService} from 'app/entities/metadata-field/metadata-field.service';
import {FormBuilder} from '@angular/forms';
import {IMetadataField} from 'app/shared/model/metadata-field.model';
import {IMetadataValue, MetadataValue} from 'app/shared/model/metadata-value.model';
import {HttpResponse} from '@angular/common/http';
import {METADATA_KEY_ABOUT} from 'app/shared/constants/metadata.constants';
import {Observable} from 'rxjs';
import {ObjectType} from 'app/shared/model/enumerations/object-type.model';

@Component({
  selector: 'jhi-about',
  template: '<jhi-custom-page [pageType]="\'about\'"></jhi-custom-page>'
})
export class AboutComponent {
  constructor() {}
}
