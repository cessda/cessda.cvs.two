import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';

import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiEventManager} from 'ng-jhipster';
import {HttpResponse} from '@angular/common/http';
import {AccountService} from 'app/core/auth/account.service';
import {VocabularyService} from 'app/entities/vocabulary/vocabulary.service';
import {FormBuilder, Validators} from '@angular/forms';
import {Observable, Subject} from 'rxjs';
import {IVocabulary} from 'app/shared/model/vocabulary.model';
import {Account} from 'app/core/user/account.model';
import {EditorService} from 'app/editor/editor.service';
import {IVocabularySnippet, VocabularySnippet} from 'app/shared/model/vocabulary-snippet.model';
import {IVersion} from 'app/shared/model/version.model';
import {ILicence} from 'app/shared/model/licence.model';
import {LicenceService} from 'app/admin/licence/licence.service';
import VocabularyUtil from 'app/shared/util/vocabulary-util';
import {DiffContent, DiffResults} from 'ngx-text-diff/lib/ngx-text-diff.model';
import {IComment} from 'app/shared/model/comment.model';
import {VocabularyChangeService} from 'app/entities/vocabulary-change/vocabulary-change.service';
import {IVocabularyChange} from 'app/shared/model/vocabulary-change.model';

@Component({
  selector: 'jhi-editor-detail-cv-forward-status-dialog',
  templateUrl: './editor-detail-cv-forward-status-dialog.component.html',
  styleUrls: ['editor.scss']
})
export class EditorDetailCvForwardStatusDialogComponent implements OnInit {
  licences?: ILicence[];
  isSaving: boolean;
  isVersionInvalid: boolean;
  account!: Account;
  languages: string[] = [];
  vocabularyParam?: IVocabulary;
  versionParam?: IVersion;
  isSlForm?: boolean;
  slVersionNumber!: string;
  tlProposedVersionNumber = 1;
  missingTranslations: string[] = [];
  comments: IComment[] | undefined = [];
  vocabularyChanges: IVocabularyChange[] | null = [];

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

  quillSimpleModule: any = {
    toolbar: [['bold', 'italic', 'underline', 'strike'], ['blockquote'], [{ list: 'ordered' }, { list: 'bullet' }], ['link'], ['clean']]
  };

  cvForwardStatusForm = this.fb.group({
    versionNotes: [],
    versionChanges: [],
    versionNumberSl: ['', [Validators.required, Validators.pattern('^\\d{1,2}\\.\\d{1,2}$')]],
    versionNumberTl: ['', [Validators.required, Validators.pattern('^\\d{1,2}$')]],
    licenseId: ['', Validators.required]
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
    private router: Router
  ) {
    this.isSaving = false;
    this.isVersionInvalid = false;
  }

  getLangIsoFormatted(langIso: string): string {
    return VocabularyUtil.getLangIsoFormatted(langIso);
  }

  clear(): void {
    this.activeModal.dismiss();
  }

  ngOnInit(): void {
    this.isSaving = false;
    this.comments = this.versionParam!.comments;
    this.accountService.identity().subscribe(account => {
      if (account) {
        this.account = account;

        if (this.versionParam!.status !== 'REVIEW') {
          this.cvForwardStatusForm.removeControl('versionNotes');
          this.cvForwardStatusForm.removeControl('versionChanges');
          this.cvForwardStatusForm.removeControl('versionNumberSl');
          this.cvForwardStatusForm.removeControl('versionNumberTl');
          this.cvForwardStatusForm.removeControl('licenseId');
        } else {
          if (this.isSlForm) {
            this.cvForwardStatusForm.removeControl('versionNumberTl');
          } else {
            this.cvForwardStatusForm.removeControl('versionNumberSl');
            // get TL proposed number
            this.tlProposedVersionNumber = +this.versionParam!.number!.substring(
              this.slVersionNumber.length + 1,
              this.versionParam!.number!.length
            );
          }
          // no need version changes on initial version
          if (this.versionParam!.previousVersion === undefined || this.versionParam!.previousVersion === null) {
            this.cvForwardStatusForm.removeControl('versionNotes');
            this.cvForwardStatusForm.removeControl('versionChanges');
          }
          this.licenceService
            .query({
              page: 0,
              size: 50,
              sort: ['id,asc']
            })
            .subscribe((res: HttpResponse<ILicence[]>) => {
              this.licences = res.body!;
              this.fillForm();
            });
        }
      }
    });

    // compare version and version changes
    if (this.versionParam!.previousVersion !== undefined && this.versionParam!.previousVersion !== 0) {
      this.editorService.getVocabularyCompare(this.versionParam!.id!).subscribe((res: HttpResponse<string[]>) => {
        const newContent: DiffContent = {
          leftContent: res.body![0],
          rightContent: res.body![1]
        };
        this.contentObservable.next(newContent);
        this.comparePrevVersion = res.headers.get('X-Prev-Cv-Version')!;
      });

      if (this.versionParam!.versionNotes) {
        this.cvForwardStatusForm.patchValue({ versionNotes: this.versionParam!.versionNotes });
      }

      if (this.versionParam!.versionChanges) {
        this.cvForwardStatusForm.patchValue({ versionChanges: this.versionParam!.versionChanges });
      } else {
        this.vocabularyChangeService.getByVersionId(this.versionParam!.id!).subscribe((res: HttpResponse<IVocabularyChange[]>) => {
          this.vocabularyChanges = res.body;
          this.cvForwardStatusForm.patchValue({
            versionChanges: this.vocabularyChanges!.map(vc => vc.changeType + ': ' + vc.description).join('<br/>')
          });
        });
      }
    }
  }

  private fillForm(): void {
    if (this.isSlForm) {
      this.cvForwardStatusForm.patchValue({
        versionNumberSl: this.versionParam!.number
      });
    } else {
      this.cvForwardStatusForm.patchValue({
        versionNumberTl: this.tlProposedVersionNumber
      });
    }
    if (this.versionParam!.licenseId) {
      this.cvForwardStatusForm.patchValue({
        licenseId: this.versionParam!.licenseId
      });
    } else {
      this.cvForwardStatusForm.patchValue({
        licenseId: this.licences?.length ? this.licences[0].id : null
      });
    }
  }

  private createFromForm(): IVocabularySnippet {
    const vocabularySnippet = {
      ...new VocabularySnippet(),
      versionId: this.versionParam!.id,
      vocabularyId: this.vocabularyParam!.id,
      agencyId: this.vocabularyParam!.agencyId,
      language: this.versionParam!.language,
      itemType: this.versionParam!.itemType,
      status: this.versionParam!.status
    };

    if (this.versionParam!.status === 'DRAFT') {
      vocabularySnippet.actionType = this.isSlForm ? 'FORWARD_CV_SL_STATUS_REVIEW' : 'FORWARD_CV_TL_STATUS_REVIEW';
    } else if (this.versionParam!.status === 'REVIEW') {
      vocabularySnippet.actionType = this.isSlForm ? 'FORWARD_CV_SL_STATUS_PUBLISHED' : 'FORWARD_CV_TL_STATUS_PUBLISHED';
      vocabularySnippet.licenseId = this.cvForwardStatusForm.get(['licenseId'])!.value;

      if (this.versionParam!.previousVersion !== undefined && this.versionParam!.previousVersion !== null) {
        vocabularySnippet.versionNotes = this.cvForwardStatusForm.get(['versionNotes'])
          ? this.cvForwardStatusForm.get(['versionNotes'])!.value
          : undefined;
        vocabularySnippet.versionChanges = this.cvForwardStatusForm.get(['versionChanges'])
          ? this.cvForwardStatusForm.get(['versionChanges'])!.value
          : undefined;
      }
      // check validity
      if (this.isSlForm) {
        vocabularySnippet.versionNumber = this.cvForwardStatusForm.get(['versionNumberSl'])!.value;
        this.isVersionInvalid = VocabularyUtil.compareVersionNumber(vocabularySnippet.versionNumber!, this.versionParam!.number!) === -1;
      } else {
        this.missingTranslations = [];
        this.isVersionInvalid = +this.cvForwardStatusForm.get(['versionNumberTl'])!.value < this.tlProposedVersionNumber;
        vocabularySnippet.versionNumber = this.slVersionNumber + '.' + this.cvForwardStatusForm.get(['versionNumberTl'])!.value;
        this.versionParam!.concepts!.forEach(c => {
          if (!c.title || c.title === null || c.title === '') {
            this.missingTranslations.push(c.notation!);
          }
        });
      }
    }

    return vocabularySnippet;
  }

  saveVersionInfo(): void {
    this.isSaving = true;
    const vocabularySnippet = {
      ...new VocabularySnippet(),
      actionType: 'EDIT_VERSION_INFO_CV',
      versionId: this.versionParam!.id,
      vocabularyId: this.vocabularyParam!.id,
      agencyId: this.vocabularyParam!.agencyId,
      language: this.versionParam!.language,
      itemType: this.versionParam!.itemType,
      status: this.versionParam!.status,
      versionNotes: this.cvForwardStatusForm.get(['versionNotes']) ? this.cvForwardStatusForm.get(['versionNotes'])!.value : undefined,
      versionChanges: this.cvForwardStatusForm.get(['versionChanges'])
        ? this.cvForwardStatusForm.get(['versionChanges'])!.value
        : undefined,
      licenseId: this.cvForwardStatusForm.get(['licenseId'])!.value
    };
    if (this.isSlForm) {
      vocabularySnippet.versionNumber = this.cvForwardStatusForm.get(['versionNumberSl'])!.value;
    } else {
      vocabularySnippet.versionNumber = this.slVersionNumber + '.' + this.cvForwardStatusForm.get(['versionNumberTl'])!.value;
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
      this.router.navigate(['/editor/vocabulary/' + this.vocabularyParam!.notation]);
    } else {
      this.router.navigate(['/editor/vocabulary/' + this.versionParam!.notation], { queryParams: { lang: this.versionParam!.language } });
    }
    this.activeModal.dismiss(true);
    this.eventManager.broadcast('deselectConcept');
  }

  onCompareResults(diffResults: DiffResults): void {
    this.compareNoOfDifference = diffResults.diffsCount;
  }
}
