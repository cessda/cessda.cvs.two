import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { LicenceDetailComponent } from 'app/entities/licence/licence-detail.component';
import { Licence } from 'app/shared/model/licence.model';

describe('Component Tests', () => {
  describe('Licence Management Detail Component', () => {
    let comp: LicenceDetailComponent;
    let fixture: ComponentFixture<LicenceDetailComponent>;
    const route = ({ data: of({ licence: new Licence(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [LicenceDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(LicenceDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(LicenceDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load licence on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.licence).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
