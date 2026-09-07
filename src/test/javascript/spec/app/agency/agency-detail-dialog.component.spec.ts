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
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import moment from 'moment';

import { CvsTestModule } from '../../test.module';
import { MockActiveModal } from '../../helpers/mock-active-modal.service';
import { AgencyDetailDialogComponent } from 'app/agency/agency-detail-dialog.component';
import { AgencyService } from 'app/agency/agency.service';
import { VocabularyLanguageFromKeyPipe } from 'app/shared/language/vocabulary-language-from-key.pipe';
import { createNewAgency } from 'app/shared/model/agency.model';
import { AgencyStat } from 'app/shared/model/agencystat.model';
import { VocabStat } from 'app/shared/model/vocab-stat.model';
import { VersionStatusStat } from 'app/shared/model/version-status-stat.model';

describe('Component Tests', () => {
  describe('Agency Detail Dialog Component', () => {
    let comp: AgencyDetailDialogComponent;
    let fixture: ComponentFixture<AgencyDetailDialogComponent>;
    let service: AgencyService;
    let mockActiveModal: MockActiveModal;

    const status = (over: Partial<VersionStatusStat>): VersionStatusStat => ({
      language: 'en',
      type: 'SL',
      versionNumber: '2.0',
      status: 'PUBLISHED',
      creationDate: '2021-03-04',
      date: '2021-03-04',
      ...over,
    });

    const vocabStat = (over?: Partial<VocabStat>): VocabStat => ({
      currentVersion: '2.0',
      latestPublishedVersion: '2.0',
      notation: 'AnalysisUnit',
      languages: ['en'],
      sourceLanguage: 'en',
      versionCodeStats: [{ versionNumber: '2.0', codes: ['a', 'b', 'c'] }],
      versionStatusStats: [status({})],
      ...over,
    });

    const statistic = (vocabStats: VocabStat[]) =>
      of(new HttpResponse<AgencyStat>({ body: { id: 4, name: 'CESSDA', url: '', description: '', logo: '', vocabStats } }));

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [AgencyDetailDialogComponent],
        providers: [VocabularyLanguageFromKeyPipe],
      })
        .overrideTemplate(AgencyDetailDialogComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(AgencyDetailDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(AgencyService);
      mockActiveModal = TestBed.inject(NgbActiveModal) as unknown as MockActiveModal;

      comp.agency = createNewAgency({ id: 4, name: 'CESSDA', link: 'https://www.cessda.eu' });
    });

    describe('gathering the statistics', () => {
      it('should ask for nothing when there is no agency to ask about', () => {
        const spy = spyOn(service, 'statistic');
        comp.agency = createNewAgency();

        comp.ngOnInit();

        expect(spy).not.toHaveBeenCalled();
      });

      it('should ask for the statistics of the agency it was opened on', () => {
        const spy = spyOn(service, 'statistic').and.returnValue(statistic([vocabStat()]));

        comp.ngOnInit();

        expect(spy).toHaveBeenCalledWith(4);
      });

      it('should hold no statistics when the server returns no body', () => {
        spyOn(service, 'statistic').and.returnValue(of(new HttpResponse<AgencyStat>({ body: null })));

        comp.ngOnInit();

        expect(comp.vocabStats).toEqual([]);
      });

      it('should count a published source language version and the vocabulary it belongs to', () => {
        spyOn(service, 'statistic').and.returnValue(statistic([vocabStat()]));

        comp.ngOnInit();

        expect(comp.numberCvVersionSlPublished).toBe(1);
        expect(comp.numberCvPublished).toBe(1);
      });

      it('should count published translations apart from source languages', () => {
        spyOn(service, 'statistic').and.returnValue(
          statistic([vocabStat({ versionStatusStats: [status({}), status({ type: 'TL', language: 'de', versionNumber: '2.0.1' })] })]),
        );

        comp.ngOnInit();

        expect(comp.numberCvVersionSlPublished).toBe(1);
        expect(comp.numberCvVersionTlPublished).toBe(1);
      });

      it('should not count a vocabulary whose source language is unpublished', () => {
        spyOn(service, 'statistic').and.returnValue(statistic([vocabStat({ versionStatusStats: [status({ status: 'DRAFT' })] })]));

        comp.ngOnInit();

        expect(comp.numberCvPublished).toBe(0);
        expect(comp.numberCvVersionSlPublished).toBe(0);
      });

      it('should list every version that is not published', () => {
        spyOn(service, 'statistic').and.returnValue(
          statistic([vocabStat({ versionStatusStats: [status({}), status({ status: 'DRAFT', language: 'de', type: 'TL' })] })]),
        );

        comp.ngOnInit();

        expect(comp.unpublishedVersions).toHaveLength(1);
        expect(comp.unpublishedVersions[0]).toEqual(
          jasmine.objectContaining({ cv: 'AnalysisUnit', type: 'TL', language: 'de', status: 'DRAFT' }),
        );
      });

      it('should count the codes of the current version separately from all of them', () => {
        spyOn(service, 'statistic').and.returnValue(
          statistic([
            vocabStat({
              versionCodeStats: [
                { versionNumber: '2.0', codes: ['a', 'b', 'c'] },
                { versionNumber: '1.0', codes: ['a', 'b'] },
              ],
            }),
          ]),
        );

        comp.ngOnInit();

        expect(comp.numberCodePublished).toBe(3);
        expect(comp.numberCodeVersionPublished).toBe(5);
      });

      it('should name the languages it counted rather than key them by code', () => {
        spyOn(service, 'statistic').and.returnValue(
          statistic([vocabStat({ versionStatusStats: [status({}), status({ language: 'de', type: 'TL' })] })]),
        );

        comp.ngOnInit();

        expect(comp.languageComposition).toEqual({ 'English (en)': 1, 'German (de)': 1 });
      });

      it('should add up a language that appears in several vocabularies', () => {
        spyOn(service, 'statistic').and.returnValue(statistic([vocabStat(), vocabStat({ notation: 'TimeMethod' })]));

        comp.ngOnInit();

        expect(comp.languageComposition['English (en)']).toBe(2);
      });
    });

    describe('published versions of a bundle', () => {
      it('should take the published versions whose number starts with the one asked for', () => {
        const vocab = vocabStat({
          versionStatusStats: [
            status({}),
            status({ type: 'TL', language: 'de', versionNumber: '2.0.1' }),
            status({ versionNumber: '1.0' }),
          ],
        });

        expect(comp.getVersionStatus(vocab, '2.0')).toHaveLength(2);
      });

      it('should leave out anything that is not published', () => {
        const vocab = vocabStat({ versionStatusStats: [status({ status: 'DRAFT' })] });

        expect(comp.getVersionStatus(vocab, '2.0')).toEqual([]);
      });
    });

    it('should sort the vocabularies by notation', () => {
      const sorted = comp.sortVocabStat([vocabStat({ notation: 'TimeMethod' }), vocabStat({ notation: 'AnalysisUnit' })]);

      expect(sorted.map(v => v.notation)).toEqual(['AnalysisUnit', 'TimeMethod']);
    });

    it('should render a date as a relative time', () => {
      expect(comp.parseDateAgo(moment().subtract(3, 'days'))).toBe('3 days ago');
    });

    it('should shorten a link to its host', () => {
      expect(comp.urlCleaner('https://www.cessda.eu/about')).toBe('cessda.eu');
    });

    describe('leaving the dialog', () => {
      it('should dismiss it as cancelled', () => {
        comp.clear();

        expect(mockActiveModal.dismissSpy).toHaveBeenCalledWith('cancel');
      });

      it('should hand the chosen vocabulary back on the way out', () => {
        comp.closeAndNavigate('AnalysisUnit', 'en', '2.0', '1.1');

        expect(mockActiveModal.dismissSpy).toHaveBeenCalledWith({ nota: 'AnalysisUnit', lang: 'en', version: '2.0', code: '1.1' });
      });

      it('should leave the version and code out when they were not chosen', () => {
        comp.closeAndNavigate('AnalysisUnit', 'en');

        expect(mockActiveModal.dismissSpy).toHaveBeenCalledWith({
          nota: 'AnalysisUnit',
          lang: 'en',
          version: undefined,
          code: undefined,
        });
      });

      it('should go back to the previous page', () => {
        const back = spyOn(window.history, 'back');

        comp.previousState();

        expect(back).toHaveBeenCalled();
      });
    });
  });
});
