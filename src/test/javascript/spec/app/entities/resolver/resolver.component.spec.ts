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
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Data } from '@angular/router';

import { CvsTestModule } from '../../../test.module';
import { ResolverComponent } from 'app/admin/resolver/resolver.component';
import { ResolverService } from 'app/admin/resolver/resolver.service';
import { Resolver } from 'app/shared/model/resolver.model';

describe('Component Tests', () => {
  describe('Resolver Management Component', () => {
    let comp: ResolverComponent;
    let fixture: ComponentFixture<ResolverComponent>;
    let service: ResolverService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [ResolverComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: {
              data: {
                subscribe: (fn: (value: Data) => void) =>
                  fn({
                    pagingParams: {
                      predicate: 'id',
                      reverse: false,
                      page: 0,
                    },
                  }),
              },
            },
          },
        ],
      })
        .overrideTemplate(ResolverComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ResolverComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ResolverService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse<Resolver[]>({
            body: [{ id: 123, resolverURI: '', resourceUrl: '' }],
            headers,
          }),
        ),
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.resolvers && comp.resolvers[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });

    it('should load a page', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse<Resolver[]>({
            body: [{ id: 123, resolverURI: '', resourceUrl: '' }],
            headers,
          }),
        ),
      );

      // WHEN
      comp.loadPage(1);

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.resolvers && comp.resolvers[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });

    it('should calculate the sort attribute for an id', () => {
      // WHEN
      comp.ngOnInit();
      const result = comp.sort();

      // THEN
      expect(result).toEqual(['id,desc']);
    });

    it('should calculate the sort attribute for a non-id attribute', () => {
      // INIT
      comp.ngOnInit();

      // GIVEN
      comp.predicate = 'name';

      // WHEN
      const result = comp.sort();

      // THEN
      expect(result).toEqual(['name,desc', 'id']);
    });
  });
});
