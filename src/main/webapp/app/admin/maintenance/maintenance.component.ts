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
import { Component } from '@angular/core';
import { MaintenanceService } from 'app/admin/maintenance/maintenance.service';
import { HttpResponse } from '@angular/common/http';
import { Maintenance } from 'app/admin/maintenance/maintenance.model';

@Component({
  selector: 'jhi-maintenance',
  templateUrl: './maintenance.component.html',
})
export class MaintenanceComponent {
  isGeneratingJson: boolean;
  isIndexingAgency: boolean;
  isIndexingAgencyStat: boolean;
  isIndexingVocabularyPublish: boolean;
  isIndexingVocabularyEditor: boolean;
  isCheckingTls: boolean;
  generatingJsonOutput: string | null;
  indexingAgencyOutput: string | null;
  indexingAgencyStatOutput: string | null;
  indexingVocabularyPublishOutput: string | null;
  indexingVocabularyEditorOutput: string | null;

  constructor(protected maintenanceService: MaintenanceService) {
    this.isGeneratingJson = false;
    this.isIndexingAgency = false;
    this.isIndexingAgencyStat = false;
    this.isIndexingVocabularyPublish = false;
    this.isIndexingVocabularyEditor = false;
    this.isCheckingTls = false;
    this.generatingJsonOutput = '';
    this.indexingAgencyOutput = '';
    this.indexingAgencyStatOutput = '';
    this.indexingVocabularyPublishOutput = '';
    this.indexingVocabularyEditorOutput = '';
  }

  performGeneratingJson(): void {
    this.isGeneratingJson = true;
    this.generatingJsonOutput = 'loading...';
    this.maintenanceService.generateJson().subscribe(
      (res: HttpResponse<Maintenance>) => this.onSuccess(res.body!, 'GENERATE_JSON'),
      () => this.onError('GENERATE_JSON'),
    );
  }

  performIndexingAgency(): void {
    this.isIndexingAgency = true;
    this.indexingAgencyOutput = 'loading...';
    this.maintenanceService.indexAgency().subscribe(
      (res: HttpResponse<Maintenance>) => this.onSuccess(res.body!, 'INDEX_AGENCY'),
      () => this.onError('INDEX_AGENCY'),
    );
  }

  performIndexingAgencyStat(): void {
    this.isIndexingAgencyStat = true;
    this.indexingAgencyStatOutput = 'loading...';
    this.maintenanceService.indexAgencyStats().subscribe(
      (res: HttpResponse<Maintenance>) => this.onSuccess(res.body!, 'INDEX_AGENCY_STAT'),
      () => this.onError('INDEX_AGENCY_STAT'),
    );
  }

  performIndexingVocabularyPublish(): void {
    this.isIndexingVocabularyPublish = true;
    this.indexingVocabularyPublishOutput = 'loading...';
    this.maintenanceService.indexVocabularyPublish().subscribe(
      (res: HttpResponse<Maintenance>) => this.onSuccess(res.body!, 'INDEX_CV_PUBLISH'),
      () => this.onError('INDEX_CV_PUBLISH'),
    );
  }

  performIndexingVocabularyEditor(): void {
    this.isIndexingVocabularyEditor = true;
    this.indexingVocabularyEditorOutput = 'loading...';
    this.maintenanceService.indexVocabularyEditor().subscribe(
      (res: HttpResponse<Maintenance>) => this.onSuccess(res.body!, 'INDEX_CV_EDITOR'),
      () => this.onError('INDEX_CV_EDITOR'),
    );
  }

  private onSuccess(data: Maintenance, type: string): void {
    switch (type) {
      case 'GENERATE_JSON':
        this.isGeneratingJson = false;
        this.generatingJsonOutput = data.output;
        break;
      case 'INDEX_AGENCY':
        this.isIndexingAgency = false;
        this.indexingAgencyOutput = data.output;
        break;
      case 'INDEX_AGENCY_STAT':
        this.isIndexingAgencyStat = false;
        this.indexingAgencyStatOutput = data.output;
        break;
      case 'INDEX_CV_PUBLISH':
        this.isIndexingVocabularyPublish = false;
        this.indexingVocabularyPublishOutput = data.output;
        break;
      case 'INDEX_CV_EDITOR':
        this.isIndexingVocabularyEditor = false;
        this.indexingVocabularyEditorOutput = data.output;
        break;
    }
  }

  private onError(type: string): void {
    switch (type) {
      case 'GENERATE_JSON':
        this.isGeneratingJson = false;
        this.generatingJsonOutput = 'Error...';
        break;
      case 'INDEX_AGENCY':
        this.isIndexingAgency = false;
        this.indexingAgencyOutput = 'Error...';
        break;
      case 'INDEX_AGENCY_STAT':
        this.isIndexingAgencyStat = false;
        this.indexingAgencyStatOutput = 'Error...';
        break;
      case 'INDEX_CV_PUBLISH':
        this.isIndexingVocabularyPublish = false;
        this.indexingVocabularyPublishOutput = 'Error...';
        break;
      case 'INDEX_CV_EDITOR':
        this.isIndexingVocabularyEditor = false;
        this.indexingVocabularyEditorOutput = 'Error...';
        break;
    }
  }
}
