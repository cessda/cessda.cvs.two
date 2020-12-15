import {Component} from '@angular/core';
import {MaintenanceService} from 'app/admin/maintenance/maintenance.service';
import {HttpResponse} from '@angular/common/http';
import {Maintenance} from 'app/admin/maintenance/maintenance.model';

@Component({
  selector: 'jhi-maintenance',
  templateUrl: './maintenance.component.html'
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
      (res: HttpResponse<Maintenance>) => this.onSuccess(res.body, 'GENERATE_JSON'),
      () => this.onError('GENERATE_JSON')
    );
  }

  performIndexingAgency(): void {
    this.isIndexingAgency = true;
    this.indexingAgencyOutput = 'loading...';
    this.maintenanceService.indexAgency().subscribe(
      (res: HttpResponse<Maintenance>) => this.onSuccess(res.body, 'INDEX_AGENCY'),
      () => this.onError('INDEX_AGENCY')
    );
  }

  performIndexingAgencyStat(): void {
    this.isIndexingAgencyStat = true;
    this.indexingAgencyStatOutput = 'loading...';
    this.maintenanceService.indexAgencyStats().subscribe(
      (res: HttpResponse<Maintenance>) => this.onSuccess(res.body, 'INDEX_AGENCY_STAT'),
      () => this.onError('INDEX_AGENCY_STAT')
    );
  }

  performIndexingVocabularyPublish(): void {
    this.isIndexingVocabularyPublish = true;
    this.indexingVocabularyPublishOutput = 'loading...';
    this.maintenanceService.indexVocabularyPublish().subscribe(
      (res: HttpResponse<Maintenance>) => this.onSuccess(res.body, 'INDEX_CV_PUBLISH'),
      () => this.onError('INDEX_CV_PUBLISH')
    );
  }

  performIndexingVocabularyEditor(): void {
    this.isIndexingVocabularyEditor = true;
    this.indexingVocabularyEditorOutput = 'loading...';
    this.maintenanceService.indexVocabularyEditor().subscribe(
      (res: HttpResponse<Maintenance>) => this.onSuccess(res.body, 'INDEX_CV_EDITOR'),
      () => this.onError('INDEX_CV_EDITOR')
    );
  }


  private onSuccess(data: Maintenance | null, type: string): void {
    if (type === 'GENERATE_JSON') {
      this.isGeneratingJson = false;
      this.generatingJsonOutput = data!.output;
    } else if (type === 'INDEX_AGENCY') {
      this.isIndexingAgency = false;
      this.indexingAgencyOutput = data!.output;
    } else if (type === 'INDEX_AGENCY_STAT') {
      this.isIndexingAgencyStat = false;
      this.indexingAgencyStatOutput = data!.output;
    } else if (type === 'INDEX_CV_PUBLISH') {
      this.isIndexingVocabularyPublish = false;
      this.indexingVocabularyPublishOutput = data!.output;
    } else if (type === 'INDEX_CV_EDITOR') {
      this.isIndexingVocabularyEditor = false;
      this.indexingVocabularyEditorOutput = data!.output;
    }
  }

  private onError(type: string): void {
    if (type === 'GENERATE_JSON') {
      this.isGeneratingJson = false;
      this.generatingJsonOutput = 'Error...';
    } else if (type === 'INDEX_AGENCY') {
      this.isIndexingAgency = false;
      this.indexingAgencyOutput = 'Error...';
    } else if (type === 'INDEX_AGENCY_STAT') {
      this.isIndexingAgencyStat = false;
      this.indexingAgencyStatOutput = 'Error...';
    } else if (type === 'INDEX_CV_PUBLISH') {
      this.isIndexingVocabularyPublish = false;
      this.indexingVocabularyPublishOutput = 'Error...';
    } else if (type === 'INDEX_CV_EDITOR') {
      this.isIndexingVocabularyEditor = false;
      this.indexingVocabularyEditorOutput = 'Error...';
    }
  }
}
