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
import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { MetadataField } from 'app/shared/model/metadata-field.model';
import { EditorService } from 'app/editor/editor.service';
import { MetadataFieldService } from 'app/entities/metadata-field/metadata-field.service';
import { METADATA_KEY_ABOUT, METADATA_KEY_API } from 'app/shared/constants/metadata.constants';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { MetadataValue } from 'app/shared/model/metadata-value.model';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { ActivatedRoute } from '@angular/router';
import { FileUploadService } from 'app/shared/upload/file-upload.service';
import { FileFormat } from 'app/shared/vocabulary-download/FileFormat';

@Component({
  selector: 'jhi-custom-page',
  templateUrl: './custom-page.component.html',
})
export class CustomPageComponent implements OnInit, OnDestroy {
  @Input() pageType!: string;

  metadataKey = METADATA_KEY_API;

  metadataField: MetadataField | null = null;
  metadataValues: MetadataValue[] = [];
  metadataValueMenu: MetadataValue | undefined;

  enableDocxExport = false;
  generatingFile = false;

  eventSubscriber: Subscription | undefined;

  progress: { percentage: number } = { percentage: 0 };
  currentFileUpload: File | null = null;

  uploadFileStatus = '';
  inDocxProgress = false;
  uploadFileName = '';

  constructor(
    protected editorService: EditorService,
    private metadataFieldService: MetadataFieldService,
    protected eventManager: JhiEventManager,
    protected activatedRoute: ActivatedRoute,
    protected fileUploadService: FileUploadService,
  ) {
    this.activatedRoute.queryParams.subscribe(params => {
      if (params['docx-export'] && params['docx-export'] === 'true') {
        this.enableDocxExport = true;
      }
    });
  }

  ngOnInit(): void {
    if (this.pageType === 'about') {
      this.metadataKey = METADATA_KEY_ABOUT;
    }
    this.refreshContent();
    this.eventSubscriber = this.eventManager.subscribe('metadataListModification', () => this.refreshContent());
  }

  selectFile(selectFileEvent: Event): void {
    const selectedFiles = (selectFileEvent.target as HTMLInputElement).files;
    if (!selectedFiles) {
      return;
    }

    this.currentFileUpload = selectedFiles.item(0);
    if (!this.currentFileUpload) {
      return;
    }

    this.inDocxProgress = true;
    this.uploadFileStatus = 'uploading DOCX file...';
    this.progress.percentage = 0;
    this.fileUploadService.uploadFile(this.currentFileUpload).subscribe(event => {
      if (event.type === HttpEventType.UploadProgress) {
        this.progress.percentage = Math.round((100 * event.loaded) / (event.total || 0));
      } else if (event instanceof HttpResponse) {
        this.uploadFileStatus = 'Uploading complete, extracting DOCX to HTML... Please wait!';

        const location = event.headers.get('location') || '';
        const fileName = location.split('/').pop();
        if (!fileName) {
          throw new TypeError('File name not returned from server!');
        }

        this.fileUploadService.convertDocsToHtml(fileName).subscribe(
          response => {
            this.uploadFileStatus = 'Docx contents is extracted. See the results:';
            this.uploadFileName = response.body?.message || fileName;
          },
          () => {
            this.uploadFileStatus = 'There is a problem!. Please try again later';
          },
        );
      }
    });
  }

  private refreshContent(): void {
    this.metadataFieldService.findByKey(this.metadataKey).subscribe((res: HttpResponse<MetadataField>) => {
      if (res.body !== null) {
        this.metadataField = res.body;
      } else {
        this.metadataField = { metadataKey: this.metadataKey, metadataValues: [] };
      }
      if (this.metadataField && this.metadataField.metadataValues) {
        this.metadataValues = this.metadataField.metadataValues;
        this.metadataValueMenu = this.metadataValues.filter(mv => mv.identifier === 'overview')[0];
      }
    });
  }

  addSection(): void {
    this.metadataField!.metadataValues.push({});
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventSubscriber.unsubscribe();
    }
  }

  fillSections(): void {
    this.fileUploadService.fillMetadataWithHtmlFile(this.uploadFileName, this.metadataKey).subscribe({
      next: () => {
        this.refreshContent();
        location.reload();
      },
      error: () => {
        this.uploadFileStatus = 'There is a problem!. Please try again later';
      },
    });
  }

  downloadAsFile(format: FileFormat): void {
    if (this.generatingFile) {
      return;
    }
    this.generatingFile = true;
    this.metadataFieldService.downloadMetadataFile(this.metadataKey, format.mimeType).subscribe((res: Blob) => {
      this.generateDownloadFile(res, format.mimeType, format.extension);
    });
  }

  private generateDownloadFile(res: Blob, mimeType: string, fileFormat: string): void {
    const newBlob = new Blob([res], { type: mimeType });
    const data = window.URL.createObjectURL(newBlob);
    const link = document.createElement('a');
    link.href = data;
    let fileTitle = 'CESSDA_API-Docs';
    if (this.pageType === 'about') {
      fileTitle = 'CESSDA_CVS_About';
    }
    link.download = fileTitle + '.' + fileFormat;
    link.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, view: window }));
    setTimeout(() => {
      window.URL.revokeObjectURL(data);
      link.remove();
    }, 100);
    this.generatingFile = false;
  }
}
