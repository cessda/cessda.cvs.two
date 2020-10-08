import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { JhiDataUtils } from 'ng-jhipster';

import { CvsTestModule } from '../../../test.module';
import { AgencyDetailDialogComponent } from 'app/agency/agency-detail-dialog.component';
import { Agency } from 'app/shared/model/agency.model';

describe('Component Tests', () => {
  describe('Agency Management Detail Component', () => {
    let comp: AgencyDetailDialogComponent;
    let fixture: ComponentFixture<AgencyDetailDialogComponent>;
    let dataUtils: JhiDataUtils;
    const route = ({ data: of({ agency: new Agency(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [AgencyDetailDialogComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(AgencyDetailDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(AgencyDetailDialogComponent);
      comp = fixture.componentInstance;
      dataUtils = fixture.debugElement.injector.get(JhiDataUtils);
    });

    // describe('OnInit', () => {
    //   it('Should load agency on init', () => {
    //     // WHEN
    //     comp.ngOnInit();
    //
    //     // THEN
    //     expect(comp.agency).toEqual(jasmine.objectContaining({ id: 123 }));
    //   });
    // });

    describe('byteSize', () => {
      it('Should call byteSize from JhiDataUtils', () => {
        // GIVEN
        spyOn(dataUtils, 'byteSize');
        const fakeBase64 = 'fake base64';

        // WHEN
        comp.byteSize(fakeBase64);

        // THEN
        expect(dataUtils.byteSize).toBeCalledWith(fakeBase64);
      });
    });

    describe('openFile', () => {
      it('Should call openFile from JhiDataUtils', () => {
        // GIVEN
        spyOn(dataUtils, 'openFile');
        const fakeContentType = 'fake content type';
        const fakeBase64 = 'fake base64';

        // WHEN
        comp.openFile(fakeContentType, fakeBase64);

        // THEN
        expect(dataUtils.openFile).toBeCalledWith(fakeContentType, fakeBase64);
      });
    });
  });
});
