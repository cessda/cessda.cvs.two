import {Component, OnDestroy, OnInit} from '@angular/core';
import {IMetadataField, MetadataField} from 'app/shared/model/metadata-field.model';
import {EditorService} from 'app/editor/editor.service';
import {MetadataFieldService} from 'app/entities/metadata-field/metadata-field.service';
import {METADATA_KEY_API} from 'app/shared/constants/metadata.constants';
import {HttpEventType, HttpResponse} from '@angular/common/http';
import {IMetadataValue, MetadataValue} from 'app/shared/model/metadata-value.model';
import {Subscription} from 'rxjs';
import {JhiEventManager} from 'ng-jhipster';
import {ActivatedRoute} from '@angular/router';
import {FileUploadService} from 'app/shared/upload/file-upload.service';
import {SimpleResponse} from 'app/shared/model/simple-response.model';

@Component({
  selector: 'jhi-api-docs',
  templateUrl: './api-docs.component.html'
})
export class ApiDocsComponent implements OnInit, OnDestroy {
  metadataField?: IMetadataField | null;
  metadataValues: IMetadataValue[] = [];
  metadataValueMenu?: IMetadataValue;
  metadataKey = METADATA_KEY_API;

  enableDocxExport = false;
  generatingFile = false;

  eventSubscriber?: Subscription;

  selectedFiles?: FileList;
  progress: { percentage: number; } = { percentage: 0};
  currentFileUpload?: File | null;

  uploadFileStatus = '';
  inDocxProgress = false;
  uploadFileName = '';

  constructor(
    protected editorService: EditorService,
    private metadataFieldService: MetadataFieldService,
    protected eventManager: JhiEventManager,
    protected activatedRoute: ActivatedRoute,
    protected fileUploadService: FileUploadService
  ) {
    this.activatedRoute.queryParams.subscribe(params => {
      if (params['docx-export'] && params['docx-export'] === 'true') {
          this.enableDocxExport = true;
      }
    });
  }

  ngOnInit(): void {
    this.refreshContent();
    this.eventSubscriber = this.eventManager.subscribe('metadataListModification', () => this.refreshContent());
  }

  selectFile(selectFileEvent: { target: { files: FileList | undefined } }): void {
    this.inDocxProgress = true;
    this.uploadFileStatus = 'uploading DOCX file...'
    this.selectedFiles = selectFileEvent.target.files;
    this.progress.percentage = 0;
    this.currentFileUpload = this.selectedFiles!.item(0);
    this.fileUploadService.uploadFile(this.currentFileUpload!).subscribe(event => {
      if (event.type === HttpEventType.UploadProgress) {
        this.progress.percentage = Math.round((100 * event.loaded) / event.total!);
      } else if (event instanceof HttpResponse) {
        this.uploadFileStatus = 'Uploading complete, extracting DOCX to HTML... Please wait!'
        const fileName = event.body!.toString();
        this.fileUploadService.convertDocsToHtml( fileName ).subscribe(
          (res: HttpResponse<SimpleResponse>) => {
            this.uploadFileStatus = 'Docx contents is extracted. See the results:'
            this.uploadFileName = fileName;
          }, error => {
            this.uploadFileStatus = 'There is a problem!. Please try again later'
          }
        );
      }
    });

    this.selectedFiles = undefined;
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
        this.metadataValueMenu = this.metadataValues.filter(mv => mv.identifier === 'overview')[0];
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

  fillSections(): void {
    this.fileUploadService.fillMetadataWithHtmlFile( this.uploadFileName, this.metadataKey ).subscribe(
      (res: HttpResponse<SimpleResponse>) => {
        this.refreshContent();
        location.reload();
      }, error => {
        this.uploadFileStatus = 'There is a problem!. Please try again later'
      }
    );
  }

  downloadAsFile(format: string): void {
    if ( this.generatingFile ) {
      return;
    }
    this.generatingFile = true;
    this.metadataFieldService
      .downloadMetadataFile(this.metadataKey, format)
      .subscribe((res: Blob) => {
        this.generateDownloadFile(
          res,
          format === 'pdf' ? 'application/pdf':'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
          format === 'pdf' ? 'pdf':'docx');
      });
  }

  private generateDownloadFile(res: Blob, mimeType: string, fileFormat: string): void {
    const newBlob = new Blob([res], {type: mimeType});
    if (window.navigator && window.navigator.msSaveOrOpenBlob) {
      window.navigator.msSaveOrOpenBlob(newBlob);
      return;
    }
    const data = window.URL.createObjectURL(newBlob);
    const link = document.createElement('a');
    link.href = data;
    link.download = 'CESSDA_API-Docs.' + fileFormat;
    link.dispatchEvent(new MouseEvent('click', {bubbles: true, cancelable: true, view: window}));
    setTimeout(function (): void {
      window.URL.revokeObjectURL(data);
      link.remove();
    }, 100);
    this.generatingFile = false;
  }
}
