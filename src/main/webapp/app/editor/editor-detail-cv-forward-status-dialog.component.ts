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
import { Router } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { HttpResponse } from '@angular/common/http';
import { AccountService } from 'app/core/auth/account.service';
import { VocabularyService } from 'app/entities/vocabulary/vocabulary.service';
import { FormBuilder, Validators } from '@angular/forms';
import { Observable, Subject } from 'rxjs';
import { IVocabulary } from 'app/shared/model/vocabulary.model';
import { Account } from 'app/core/user/account.model';
import { EditorService } from 'app/editor/editor.service';
import { VocabularySnippet } from 'app/shared/model/vocabulary-snippet.model';
import { IVersion, Version } from 'app/shared/model/version.model';
import { Licence } from 'app/shared/model/licence.model';
import { LicenceService } from 'app/admin/licence/licence.service';
import VocabularyUtil from 'app/shared/util/vocabulary-util';
import { DiffContent, DiffResults } from 'ngx-text-diff/lib/ngx-text-diff.model';
import { IComment } from 'app/shared/model/comment.model';
import { VocabularyChangeService } from 'app/entities/vocabulary-change/vocabulary-change.service';
import { IVocabularyChange } from 'app/shared/model/vocabulary-change.model';
import { QuillModule } from 'ngx-quill';
import { Quill } from 'quill';
import { ActionType } from 'app/shared/model/enumerations/action-type.model';

@Component({
  selector: 'jhi-editor-detail-cv-forward-status-dialog',
  templateUrl: './editor-detail-cv-forward-status-dialog.component.html',
})
export class EditorDetailCvForwardStatusDialogComponent implements OnInit {
  licences?: Licence[];
  isSaving: boolean;
  isVersionInvalid: boolean;
  account!: Account;
  languages: string[] = [];
  vocabularyParam: IVocabulary = {};
  versionParam: IVersion = new Version();
  isSlForm?: boolean;
  slVersionNumber!: string;
  proposedPatchNumber = 0;
  missingTranslations: string[] = [];
  comments: IComment[] | undefined = [];
  vocabularyChanges: IVocabularyChange[] | null = [];

  public versionNotesEditor: Quill | undefined;
  public versionChangesEditor: Quill | undefined;

  isCommentCollapse = true;
  isTextDiffCollapse = true;
  isVocabularyChangeCollapse = true;

  compareNoOfDifference = 0;
  comparePrevVersion = '';

  left = '';
  right = '';

  // Inside Component define observable
  contentObservable: Subject<DiffContent> = new Subject<DiffContent>();
  contentObservable$: Observable<DiffContent> = this.contentObservable.asObservable();

  quillSimpleModule: QuillModule = {
    toolbar: [['bold', 'italic', 'underline', 'strike'], ['blockquote'], [{ list: 'ordered' }, { list: 'bullet' }], ['link'], ['clean']],
  };

  cvForwardStatusForm = this.fb.group({
    versionNotes: [''],
    versionChanges: [''],
    versionNumberSl: ['', [Validators.required, Validators.pattern('^\\d{1,2}\\.\\d{1,2}$')]],
    licenseId: ['', Validators.required],
  });

  constructor(
    private licenceService: LicenceService,
    private accountService: AccountService,
    protected vocabularyService: VocabularyService,
    protected vocabularyChangeService: VocabularyChangeService,
    protected editorService: EditorService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager,
    private fb: FormBuilder,
    private router: Router,
  ) {
    this.isSaving = false;
    this.isVersionInvalid = false;
  }

  clear(): void {
    this.activeModal.dismiss();
  }

  ngOnInit(): void {
    this.isSaving = false;
    this.comments = this.versionParam.comments;
    this.accountService.identity().subscribe(account => {
      if (account) {
        this.account = account;

        // need to check other states, because this should be probably at the end when we goe from READY_TO_TRANSLATE to PUBLISH state
        // or maybe not as we need to have version numbers for translations as well!!!
        if (this.versionParam.status !== 'REVIEW') {
          this.cvForwardStatusForm.removeControl('versionNotes');
          this.cvForwardStatusForm.removeControl('versionChanges');
          this.cvForwardStatusForm.removeControl('versionNumberSl');
          this.cvForwardStatusForm.removeControl('licenseId');
        } else {
          if (!this.isSlForm) {
            this.cvForwardStatusForm.removeControl('versionNumberSl');
            // get TL proposed number
            this.proposedPatchNumber = VocabularyUtil.getTlVersionNumber(this.versionParam.number!);
          }
          this.licenceService
            .query({
              page: 0,
              size: 50,
              sort: ['id,asc'],
            })
            .subscribe((res: HttpResponse<Licence[]>) => {
              this.licences = res.body!;
              this.fillForm();
            });
        }
        // no need version changes on initial version
        if (this.versionParam.previousVersion === undefined || this.versionParam.previousVersion === null) {
          this.cvForwardStatusForm.removeControl('versionNotes');
          this.cvForwardStatusForm.removeControl('versionChanges');
        } else {
          this.editorService.getVocabularyCompare(this.versionParam.id!).subscribe((res: HttpResponse<string[]>) => {
            const newContent: DiffContent = {
              leftContent: res.body![0],
              rightContent: res.body![1],
            };
            this.contentObservable.next(newContent);
            this.comparePrevVersion = res.headers.get('X-Prev-Cv-Version')!;
          });
          this.vocabularyChangeService.getByVersionId(this.versionParam.id!).subscribe((res: HttpResponse<IVocabularyChange[]>) => {
            this.vocabularyChanges = res.body!;
            this.fillVersionNotes();
            this.fillVersionChanges();
          });
        }
      }
    });
  }

  private fillForm(): void {
    if (this.isSlForm) {
      this.cvForwardStatusForm.patchValue({
        versionNumberSl: VocabularyUtil.getSlMajorMinorVersionNumber(this.versionParam.number!),
      });
    }
    if (this.versionParam.licenseId) {
      this.cvForwardStatusForm.patchValue({
        licenseId: this.versionParam.licenseId,
      });
    } else {
      this.cvForwardStatusForm.patchValue({
        licenseId: this.licences?.length ? this.licences[0].id : null,
      });
    }
  }

  private fillVersionNotes(): void {
    if (this.cvForwardStatusForm.contains('versionNotes') && this.versionParam.versionNotes) {
      this.cvForwardStatusForm.patchValue({
        versionNotes: this.versionParam.versionNotes,
      });
      this.versionNotesEditor?.clipboard.dangerouslyPasteHTML(this.cvForwardStatusForm.get(['versionNotes'])!.value);
    }
  }

  private fillVersionChanges(): void {
    if (
      this.cvForwardStatusForm.contains('versionChanges') &&
      this.versionParam.previousVersion !== undefined &&
      this.versionParam.previousVersion !== 0
    ) {
      if (this.versionParam.versionChanges) {
        this.cvForwardStatusForm.patchValue({
          versionChanges: this.versionParam.versionChanges,
        });
      } else {
        this.cvForwardStatusForm.patchValue({
          versionChanges: this.vocabularyChanges!.map(vc => vc.changeType + ': ' + vc.description).join('<br/>'),
        });
      }
      this.versionChangesEditor?.clipboard.dangerouslyPasteHTML(this.cvForwardStatusForm.get(['versionChanges'])!.value);
    }
  }

  private createFromForm(): VocabularySnippet {
    const vocabularySnippet: VocabularySnippet = {
      actionType: this.isSlForm ? ActionType.FORWARD_CV_SL_STATUS_REVIEW : ActionType.FORWARD_CV_TL_STATUS_REVIEW,
      versionId: this.versionParam.id,
      vocabularyId: this.vocabularyParam.id,
      agencyId: this.vocabularyParam.agencyId,
      language: this.versionParam.language,
      itemType: this.versionParam.itemType,
      status: this.versionParam.status,
    };

    if (this.versionParam.status === 'REVIEW') {
      // recode it, probably we need to add it to the last status!!!
      // or maybe not as we need to specify version right here, because the target languages need some numbers!!!
      vocabularySnippet.actionType = this.isSlForm
        ? ActionType.FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE
        : ActionType.FORWARD_CV_TL_STATUS_READY_TO_PUBLISH;
      vocabularySnippet.licenseId = this.cvForwardStatusForm.get(['licenseId'])!.value;

      if (this.versionParam.previousVersion !== undefined && this.versionParam.previousVersion !== null) {
        vocabularySnippet.versionNotes = this.cvForwardStatusForm.get(['versionNotes'])
          ? this.cvForwardStatusForm.get(['versionNotes'])!.value
          : undefined;
        vocabularySnippet.versionChanges = this.cvForwardStatusForm.get(['versionChanges'])
          ? this.cvForwardStatusForm.get(['versionChanges'])!.value
          : undefined;
      }
      // check validity
      if (this.isSlForm) {
        vocabularySnippet.versionNumber = this.cvForwardStatusForm.get(['versionNumberSl'])!.value + '.' + this.proposedPatchNumber;
        this.isVersionInvalid = VocabularyUtil.compareVersionNumbers(vocabularySnippet.versionNumber, this.versionParam.number!) === -1;
      } else {
        this.missingTranslations = [];
        vocabularySnippet.versionNumber =
          VocabularyUtil.getSlMajorMinorVersionNumber(this.slVersionNumber) + '.' + this.proposedPatchNumber;
        this.versionParam.concepts!.forEach(c => {
          if (!c.deprecated) {
            if (!c.title || c.title === null || c.title === '') {
              this.missingTranslations.push(c.notation!);
            }
          }
        });
      }
    } else if (this.versionParam.status === 'READY_TO_TRANSLATE' || this.versionParam.status === 'PUBLISHED') {
      vocabularySnippet.actionType = ActionType.FORWARD_CV_SL_STATUS_PUBLISH;
    }

    return vocabularySnippet;
  }

  saveVersionInfo(): void {
    this.isSaving = true;

    const versionNotes = this.cvForwardStatusForm.get(['versionNotes']);
    const versionChanges = this.cvForwardStatusForm.get(['versionChanges']);

    const vocabularySnippet: VocabularySnippet = {
      actionType: ActionType.EDIT_VERSION_INFO_CV,
      versionId: this.versionParam.id,
      vocabularyId: this.vocabularyParam.id,
      agencyId: this.vocabularyParam.agencyId,
      language: this.versionParam.language,
      itemType: this.versionParam.itemType,
      status: this.versionParam.status,
      versionNotes: versionNotes ? versionNotes.value : undefined,
      versionChanges: versionChanges ? versionChanges.value : undefined,
      licenseId: this.cvForwardStatusForm.get(['licenseId'])!.value,
    };
    if (this.isSlForm) {
      vocabularySnippet.versionNumber = this.cvForwardStatusForm.get(['versionNumberSl'])!.value;
    } else {
      vocabularySnippet.versionNumber = VocabularyUtil.getSlMajorMinorVersionNumber(this.slVersionNumber) + '.' + this.proposedPatchNumber;
    }
    this.subscribeToSaveResponse(this.editorService.updateVocabulary(vocabularySnippet));
  }

  forwardStatus(): void {
    this.isSaving = true;
    const vocabularySnippet = this.createFromForm();
    if (!this.isVersionInvalid && this.missingTranslations.length === 0) {
      this.subscribeToSaveResponse(this.editorService.forwardStatusVocabulary(vocabularySnippet));
    } else {
      this.isSaving = false;
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVocabulary>>): void {
    result.subscribe(() => this.onSaveSuccess());
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    if (this.isSlForm) {
      this.router.navigate(['/editor/vocabulary/' + this.vocabularyParam.notation]);
    } else {
      this.router.navigate(['/editor/vocabulary/' + this.versionParam.notation], { queryParams: { lang: this.versionParam.language } });
    }
    this.activeModal.dismiss(true);
    this.eventManager.broadcast('deselectConcept');
  }

  onCompareResults(diffResults: DiffResults): void {
    this.compareNoOfDifference = diffResults.diffsCount;
  }

  onVersionNotesEditorCreated(event: Quill): void {
    this.versionNotesEditor = event;
    this.fillVersionNotes();
  }

  onVersionChangesEditorCreated(event: Quill): void {
    this.versionChangesEditor = event;
    this.fillVersionChanges();
  }
}
