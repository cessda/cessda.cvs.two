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
