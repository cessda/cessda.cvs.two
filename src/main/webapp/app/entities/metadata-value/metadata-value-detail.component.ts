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
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { MetadataValue } from 'app/shared/model/metadata-value.model';

@Component({
  selector: 'jhi-metadata-value-detail',
  templateUrl: './metadata-value-detail.component.html',
})
export class MetadataValueDetailComponent implements OnInit {
  metadataValue: MetadataValue | null = null;

  constructor(
    protected dataUtils: JhiDataUtils,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ metadataValue }) => (this.metadataValue = metadataValue));
  }

  previousState(): void {
    window.history.back();
  }
}
