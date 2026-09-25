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
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { ResolverDetailComponent } from 'app/admin/resolver/resolver-detail.component';
import { Resolver } from 'app/shared/model/resolver.model';

describe('Component Tests', () => {
  describe('Resolver Detail Component', () => {
    let comp: ResolverDetailComponent;
    let fixture: ComponentFixture<ResolverDetailComponent>;

    const resolver: Resolver = { id: 5, resourceUrl: 'https://vocabularies.cessda.eu', resolverURI: 'doi:10.5281/zenodo.1' };

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [ResolverDetailComponent],
        // the shared mock route carries paging parameters rather than a resolver
        providers: [{ provide: ActivatedRoute, useValue: { data: of({ resolver }) } }],
      })
        .overrideTemplate(ResolverDetailComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(ResolverDetailComponent);
      comp = fixture.componentInstance;
    });

    it('should hold no resolver until the route resolves one', () => {
      expect(comp.resolver).toBeNull();
    });

    it('should show the resolver the route resolved', () => {
      comp.ngOnInit();

      expect(comp.resolver).toEqual(resolver);
    });

    it('should go back to the previous page', () => {
      const back = spyOn(window.history, 'back');

      comp.previousState();

      expect(back).toHaveBeenCalled();
    });
  });
});
