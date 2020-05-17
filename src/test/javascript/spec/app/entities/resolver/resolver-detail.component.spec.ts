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
