import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IVocabularyChange } from 'app/shared/model/vocabulary-change.model';

@Component({
  selector: 'jhi-vocabulary-change-detail',
  templateUrl: './vocabulary-change-detail.component.html'
})
export class VocabularyChangeDetailComponent implements OnInit {
  vocabularyChange: IVocabularyChange | null = null;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ vocabularyChange }) => (this.vocabularyChange = vocabularyChange));
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
