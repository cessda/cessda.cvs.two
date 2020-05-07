import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IMetadataField } from 'app/shared/model/metadata-field.model';

@Component({
  selector: 'jhi-metadata-field-detail',
  templateUrl: './metadata-field-detail.component.html'
})
export class MetadataFieldDetailComponent implements OnInit {
  metadataField: IMetadataField | null = null;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ metadataField }) => (this.metadataField = metadataField));
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
  }

  previousState(): void {
    window.history.back();
  }
}
