/*
 * Copyright © 2017-2026 CESSDA ERIC (support@cessda.eu)
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
import { HttpTestingController } from '@angular/common/http/testing';

import { CvsTestModule } from '../../../test.module';
import { MaintenanceComponent } from 'app/admin/maintenance/maintenance.component';

describe('Component Tests', () => {
  describe('Maintenance Component', () => {
    let comp: MaintenanceComponent;
    let fixture: ComponentFixture<MaintenanceComponent>;
    let httpMock: HttpTestingController;

    /**
     * Each card on the page drives one endpoint, keeps its own busy flag and writes into its own
     * output box. The wiring is what a mistake here would cross, so every action is checked
     * against the flag and the box that belong to it.
     */
    const actions: {
      name: string;
      perform: () => void;
      busy: () => boolean;
      output: () => string | null;
    }[] = [
      {
        name: 'generating the published JSON',
        perform: () => comp.performGeneratingJson(),
        busy: () => comp.isGeneratingJson,
        output: () => comp.generatingJsonOutput,
      },
      {
        name: 'indexing the agencies',
        perform: () => comp.performIndexingAgency(),
        busy: () => comp.isIndexingAgency,
        output: () => comp.indexingAgencyOutput,
      },
      {
        name: 'indexing the agency statistics',
        perform: () => comp.performIndexingAgencyStat(),
        busy: () => comp.isIndexingAgencyStat,
        output: () => comp.indexingAgencyStatOutput,
      },
      {
        name: 'indexing the published CVs',
        perform: () => comp.performIndexingVocabularyPublish(),
        busy: () => comp.isIndexingVocabularyPublish,
        output: () => comp.indexingVocabularyPublishOutput,
      },
      {
        name: 'indexing the CVs in the editor',
        perform: () => comp.performIndexingVocabularyEditor(),
        busy: () => comp.isIndexingVocabularyEditor,
        output: () => comp.indexingVocabularyEditorOutput,
      },
    ];

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [MaintenanceComponent],
      })
        .overrideTemplate(MaintenanceComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(MaintenanceComponent);
      comp = fixture.componentInstance;
      httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
      httpMock.verify();
    });

    it('should start with nothing running and every output empty', () => {
      expect(actions.map(action => action.busy())).toEqual([false, false, false, false, false]);
      expect(actions.map(action => action.output())).toEqual(['', '', '', '', '']);
    });

    actions.forEach(action => {
      describe(action.name, () => {
        it('should say it is loading while the request is still out', () => {
          action.perform();

          expect(action.busy()).toBe(true);
          expect(action.output()).toBe('loading...');

          httpMock.expectOne({ method: 'POST' }).flush({ output: 'done' });
        });

        it('should show the output the server reports and stop being busy', () => {
          action.perform();

          httpMock.expectOne({ method: 'POST' }).flush({ output: 'finished in 12s' });

          expect(action.busy()).toBe(false);
          expect(action.output()).toBe('finished in 12s');
        });

        it('should report an error and stop being busy when the request fails', () => {
          action.perform();

          httpMock.expectOne({ method: 'POST' }).error(new ProgressEvent('error'), { status: 500, statusText: 'Server Error' });

          expect(action.busy()).toBe(false);
          expect(action.output()).toBe('Error...');
        });

        it('should leave the other actions alone', () => {
          action.perform();

          const others = actions.filter(other => other !== action);

          expect(others.map(other => other.busy())).toEqual(others.map(() => false));
          expect(others.map(other => other.output())).toEqual(others.map(() => ''));

          httpMock.expectOne({ method: 'POST' }).flush({ output: 'done' });
        });
      });
    });
  });
});
