import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { JhiDataUtils } from 'ng-jhipster';

import { CvsTestModule } from '../../../test.module';
import { VocabularyChangeDetailComponent } from 'app/entities/vocabulary-change/vocabulary-change-detail.component';
import { VocabularyChange } from 'app/shared/model/vocabulary-change.model';

describe('Component Tests', () => {
  describe('VocabularyChange Management Detail Component', () => {
    let comp: VocabularyChangeDetailComponent;
    let fixture: ComponentFixture<VocabularyChangeDetailComponent>;
    let dataUtils: JhiDataUtils;
    const route = ({ data: of({ vocabularyChange: new VocabularyChange(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [VocabularyChangeDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(VocabularyChangeDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(VocabularyChangeDetailComponent);
      comp = fixture.componentInstance;
      dataUtils = fixture.debugElement.injector.get(JhiDataUtils);
    });

    describe('OnInit', () => {
      it('Should load vocabularyChange on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.vocabularyChange).toEqual(jasmine.objectContaining({ id: 123 }));
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
