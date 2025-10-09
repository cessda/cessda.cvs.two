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
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { Agency, createNewAgency } from 'app/shared/model/agency.model';
import { AgencyService } from 'app/agency/agency.service';
import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import moment, { Moment } from 'moment';
import { VocabularyLanguageFromKeyPipe } from 'app/shared';
import { VocabStat } from 'app/shared/model/vocab-stat.model';
import { VersionStatusStat } from 'app/shared/model/version-status-stat.model';

interface UnpublishedVersions {
  cv: string;
  type: string;
  language: string;
  versionNumber: string;
  status: string;
  date: Moment;
}

@Component({
    selector: 'jhi-agency-detail-dialog',
    templateUrl: './agency-detail-dialog.component.html',
    standalone: false
})
export class AgencyDetailDialogComponent implements OnInit {
  agency: Agency = createNewAgency();
  vocabStats: VocabStat[] = [];
  unpublishedVersions: UnpublishedVersions[] = [];
  languageComposition: Record<string, number> = {};
  numberCvPublished: number;
  numberCvVersionSlPublished: number;
  numberCvVersionTlPublished: number;
  numberCodePublished: number;
  numberCodeVersionPublished: number;

  constructor(
    protected dataUtils: JhiDataUtils,
    protected agencyService: AgencyService,
    public activeModal: NgbActiveModal,
    private router: Router,
    private vocabLangPipeKey: VocabularyLanguageFromKeyPipe,
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
    if (!this.agency.id) {
      return;
    }

    this.agencyService.statistic(this.agency.id).subscribe(res => {
      const agencyStatistic = res.body;
      this.vocabStats = agencyStatistic?.vocabStats || [];

      const langComposition: Record<string, number> = {};
      for (const vocabStat of this.vocabStats) {
        let hasAnySlPublished = false;
        for (const versionStatusStat of vocabStat.versionStatusStats) {
          langComposition[versionStatusStat.language] = (langComposition[versionStatusStat.language] || 0) + 1;
          if (versionStatusStat.status === 'PUBLISHED') {
            if (versionStatusStat.type === 'SL') {
              hasAnySlPublished = true;
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
              date: moment(versionStatusStat.date),
            });
          }
        }
        if (hasAnySlPublished) {
          this.numberCvPublished++;
        }
        for (const versionCodeStat of vocabStat.versionCodeStats) {
          if (versionCodeStat.versionNumber === vocabStat.currentVersion) {
            this.numberCodePublished += versionCodeStat.codes.length;
          }
          this.numberCodeVersionPublished += versionCodeStat.codes.length;
        }

        Object.keys(langComposition).forEach(key => {
          this.languageComposition[this.vocabLangPipeKey.transform(key)] = langComposition[key];
        });
      }
    });
  }

  clear(): void {
    this.activeModal.dismiss('cancel');
  }

  previousState(): void {
    window.history.back();
  }

  closeAndNavigate(notation: string, language: string, vnumber?: string, cvcode?: string): void {
    this.activeModal.dismiss({ nota: notation, lang: language, version: vnumber, code: cvcode });
  }
  parseDateAgo(dateTime: Moment): string {
    return moment(dateTime).fromNow();
  }

  getVersionStatus(vocab: VocabStat, versionNumber: string): VersionStatusStat[] {
    return vocab.versionStatusStats.filter(vss => (vss.versionNumber as string).startsWith(versionNumber) && vss.status === 'PUBLISHED');
  }

  sortVocabStat(vocabStats: VocabStat[]): VocabStat[] {
    return vocabStats.sort((a, b) => (a.notation < b.notation ? -1 : a.notation > b.notation ? 1 : 0));
  }
}

@Component({
    selector: 'jhi-agency-detail-popup',
    template: '',
    standalone: false
})
export class AgencyDetailPopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef?: NgbModalRef | null;

  constructor(
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal,
  ) {}

  ngOnInit(): void {
    this.ngbModalRef = this.modalService.open(AgencyDetailDialogComponent as Component, { size: 'xl', backdrop: 'static' });
    this.activatedRoute.data.subscribe(({ agency }) => (this.ngbModalRef!.componentInstance.agency = agency));
    this.ngbModalRef.result.then(
      () => {
        this.router.navigate(['/agency', { outlets: { popup: null } }]);
        this.ngbModalRef = null;
      },
      reason => {
        if (reason.nota === undefined) {
          this.router.navigate(['/agency', { outlets: { popup: null } }]);
        } else {
          if (reason.version === undefined) {
            this.router.navigate(['/editor/vocabulary/' + reason.nota, { outlets: { popup: null } }], {
              queryParams: { lang: reason.lang },
            });
          } else {
            this.router.navigate(['/vocabulary/' + reason.nota, { outlets: { popup: null } }], {
              queryParams: {
                lang: reason.lang,
                v: reason.version ? reason.version : null,
                code: reason.code ? reason.code.split('.').join('') : null,
              },
            });
          }
        }
        this.ngbModalRef = null;
      },
    );
  }

  ngOnDestroy(): void {
    this.ngbModalRef = null;
  }
}
