import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { JhiDataUtils } from 'ng-jhipster';

import { CvsTestModule } from '../../../test.module';
import { MetadataFieldDetailComponent } from 'app/entities/metadata-field/metadata-field-detail.component';
import { MetadataField } from 'app/shared/model/metadata-field.model';

describe('Component Tests', () => {
  describe('MetadataField Management Detail Component', () => {
    let comp: MetadataFieldDetailComponent;
    let fixture: ComponentFixture<MetadataFieldDetailComponent>;
    let dataUtils: JhiDataUtils;
    const route = ({ data: of({ metadataField: new MetadataField(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [MetadataFieldDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(MetadataFieldDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(MetadataFieldDetailComponent);
      comp = fixture.componentInstance;
      dataUtils = fixture.debugElement.injector.get(JhiDataUtils);
    });

    describe('OnInit', () => {
      it('Should load metadataField on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.metadataField).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });

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
