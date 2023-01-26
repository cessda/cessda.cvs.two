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
import { ResolverDetailComponent } from 'app/admin/resolver/resolver-detail.component';
import { Resolver } from 'app/shared/model/resolver.model';

describe('Component Tests', () => {
  describe('Resolver Management Detail Component', () => {
    let comp: ResolverDetailComponent;
    let fixture: ComponentFixture<ResolverDetailComponent>;
    const route = ({ data: of({ resolver: new Resolver(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [ResolverDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(ResolverDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ResolverDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load resolver on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.resolver).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
