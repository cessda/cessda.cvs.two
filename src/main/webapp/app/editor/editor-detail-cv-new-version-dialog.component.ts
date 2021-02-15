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
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {IVocabulary} from 'app/shared/model/vocabulary.model';
import {EditorService} from 'app/editor/editor.service';
import {IVersion} from 'app/shared/model/version.model';
import {Router} from '@angular/router';
import {JhiEventManager} from 'ng-jhipster';
import {FormBuilder, Validators} from '@angular/forms';
import {VocabularyLanguageFromKeyPipe} from 'app/shared/language/vocabulary-language-from-key.pipe';

@Component({
  templateUrl: './editor-detail-cv-new-version-dialog.component.html'
})
export class EditorDetailCvNewVersionDialogComponent implements OnInit {
  vocabularyParam!: IVocabulary;
  versionParam!: IVersion;
  isSaving: boolean;

  unPublishedTls: string;

  newVersionForm = this.fb.group({
    agreeNewVersion: ['', [Validators.required]]
  });

  constructor(
    protected editorService: EditorService,
    public activeModal: NgbActiveModal,
    private router: Router,
    protected eventManager: JhiEventManager,
    private fb: FormBuilder,
    private vocabLangPipeKey: VocabularyLanguageFromKeyPipe
  ) {
    this.isSaving = false;
    this.unPublishedTls = '';
  }

  ngOnInit(): void {
    if (this.versionParam.itemType === 'SL') {
      this.unPublishedTls = this.vocabularyParam
        .versions!.filter(v => v.itemType === 'TL' && v.status !== 'PUBLISHED')
        .map(v => '- ' + this.vocabLangPipeKey.transform(v.language!) + ' ' + v.number + '-' + v.status)
        .join('<br/>');
      if (this.unPublishedTls === '') {
        this.newVersionForm.removeControl('agreeNewVersion');
      }
    } else {
      this.newVersionForm.removeControl('agreeNewVersion');
    }
  }

  clear(): void {
    this.activeModal.dismiss();
  }

  confirmCreateNewVersion(id: number): void {
    this.isSaving = true;
    this.editorService.createNewVersion(id).subscribe(() => {
      if (this.versionParam.itemType === 'SL') {
        this.router.navigate(['/editor/vocabulary/' + this.versionParam.notation]);
      } else {
        this.router.navigate(['/editor/vocabulary/' + this.versionParam.notation], { queryParams: { lang: this.versionParam.language } });
      }
      this.activeModal.dismiss();
      this.eventManager.broadcast('deselectConcept');
      this.isSaving = false;
    });
  }
}
