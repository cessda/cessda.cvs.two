import {Component, OnDestroy, OnInit} from '@angular/core';
import {IMetadataField, MetadataField} from 'app/shared/model/metadata-field.model';
import {EditorService} from 'app/editor/editor.service';
import {MetadataFieldService} from 'app/entities/metadata-field/metadata-field.service';
import {METADATA_KEY_API} from 'app/shared/constants/metadata.constants';
import {HttpResponse} from '@angular/common/http';
import {IMetadataValue, MetadataValue} from 'app/shared/model/metadata-value.model';
import {Subscription} from 'rxjs';
import {JhiEventManager} from 'ng-jhipster';

@Component({
  selector: 'jhi-api-docs',
  templateUrl: './api-docs.component.html'
})
export class ApiDocsComponent implements OnInit, OnDestroy {
  metadataField?: IMetadataField | null;
  metadataValues: IMetadataValue[] = [];
  metadataValueMenu?: IMetadataValue;
  metadataKey = METADATA_KEY_API;

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
        this.metadataValueMenu = this.metadataValues.filter( mv => mv.identifier === 'overview')[0];
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
