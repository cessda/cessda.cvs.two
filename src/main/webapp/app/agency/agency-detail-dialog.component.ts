import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IAgency } from 'app/shared/model/agency.model';
import { AgencyService } from 'app/agency/agency.service';
import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import VocabularyUtil from 'app/shared/util/vocabulary-util';

@Component({
  selector: 'jhi-agency-detail-dialog',
  templateUrl: './agency-detail-dialog.component.html'
})
export class AgencyDetailDialogComponent implements OnInit {
  agency!: IAgency;
  agencyStatistic?: any;
  vocabStats?: any[];
  unpublishedVersions: any[] = [];
  languageComposition: any[] = [];
  numberCvPublished: number;
  numberCvVersionSlPublished: number;
  numberCvVersionTlPublished: number;
  numberCodePublished: number;
  numberCodeVersionPublished: number;

  constructor(
    protected dataUtils: JhiDataUtils,
    protected agencyService: AgencyService,
    public activeModal: NgbActiveModal,
    private router: Router
  ) {
    this.numberCvPublished = 0;
    this.numberCvVersionSlPublished = 0;
    this.numberCvVersionTlPublished = 0;
    this.numberCodePublished = 0;
    this.numberCodeVersionPublished = 0;
  }

  urlCleaner(url: string): string {
    return url.replace(/^(?:https?:\/\/)?(?:www\.)?/i, '').split('/')[0];
  }

  ngOnInit(): void {
    this.agencyService.statistic(this.agency.id!).subscribe(res => {
      this.agencyStatistic = res.body;
      this.vocabStats = this.agencyStatistic.vocabStats;

      if (this.vocabStats !== undefined) {
        const langComposition = {};
        for (const vocabStat of this.vocabStats) {
          this.numberCvPublished++;
          for (const versionStatusStat of vocabStat.versionStatusStats) {
            langComposition[versionStatusStat.language] = (langComposition[versionStatusStat.language] || 0) + 1;
            if (versionStatusStat.status === 'PUBLISHED') {
              if (versionStatusStat.type === 'SL') {
                this.numberCvVersionSlPublished++;
              } else {
                this.numberCvVersionTlPublished++;
              }
            } else {
              this.unpublishedVersions.push({
                cv: vocabStat.notation,
                type: versionStatusStat.type,
                language: versionStatusStat.language,
                versionNumber: versionStatusStat.versionNumber,
                status: versionStatusStat.status,
                date: versionStatusStat.date
              });
            }
          }

          for (const versionCodeStat of vocabStat.versionCodeStats) {
            if (versionCodeStat.versionNumber === vocabStat.currentVersion) {
              this.numberCodePublished += versionCodeStat.codes.length;
            }
            this.numberCodeVersionPublished += versionCodeStat.codes.length;
          }
        }

        Object.keys(langComposition).forEach(key => {
          this.languageComposition.push({ name: VocabularyUtil.getLangIsoFormatted(key), value: langComposition[key] });
        });
      }
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
  }

  clear(): void {
    this.activeModal.dismiss('cancel');
  }

  previousState(): void {
    window.history.back();
  }

  closeAndNavigate(notation: string, language: string): void {
    this.activeModal.dismiss({ nota: notation, lang: language });
  }
}

@Component({
  selector: 'jhi-agency-detail-popup',
  template: ''
})
export class AgencyDetailPopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef?: NgbModalRef | null;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit(): void {
    this.ngbModalRef = this.modalService.open(AgencyDetailDialogComponent as Component, { size: 'xl', backdrop: 'static' });
    this.activatedRoute.data.subscribe(({ agency }) => (this.ngbModalRef!.componentInstance.agency = agency));
    this.ngbModalRef.result.then(
      result => {
        this.router.navigate(['/agency', { outlets: { popup: null } }]);
        this.ngbModalRef = null;
      },
      reason => {
        if (reason.nota === undefined) {
          this.router.navigate(['/agency', { outlets: { popup: null } }]);
        } else {
          this.router.navigate(['/editor/vocabulary/' + reason.nota, { outlets: { popup: null } }], { queryParams: { lang: reason.lang } });
        }
        this.ngbModalRef = null;
      }
    );
  }

  ngOnDestroy(): void {
    this.ngbModalRef = null;
  }
}