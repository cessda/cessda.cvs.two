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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { MetadataFieldDetailComponent } from 'app/entities/metadata-field/metadata-field-detail.component';

describe('Component Tests', () => {
  describe('MetadataField Management Detail Component', () => {
    let comp: MetadataFieldDetailComponent;
    let fixture: ComponentFixture<MetadataFieldDetailComponent>;
    const route = { data: of({ metadataField: { id: 123 } }) } as unknown as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [MetadataFieldDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(MetadataFieldDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(MetadataFieldDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load metadataField on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.metadataField).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
