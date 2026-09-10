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
import { HttpEventType, HttpHeaderResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject, of, throwError } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { LicenceUpdateComponent } from 'app/admin/licence/licence-update.component';
import { LicenceService } from 'app/admin/licence/licence.service';
import { FileUploadService } from 'app/shared/upload/file-upload.service';
import { Licence } from 'app/shared/model/licence.model';

describe('Component Tests', () => {
  describe('Licence Update Component', () => {
    let comp: LicenceUpdateComponent;
    let fixture: ComponentFixture<LicenceUpdateComponent>;
    let service: LicenceService;
    let uploadService: FileUploadService;
    let routeData: BehaviorSubject<{ licence: Licence }>;

    const existing: Licence = {
      id: 2,
      name: 'Creative Commons Attribution 4.0',
      abbr: 'CC BY 4.0',
      link: 'https://creativecommons.org',
      logoLink: 'cc-by.png',
    };

    beforeEach(waitForAsync(() => {
      routeData = new BehaviorSubject<{ licence: Licence }>({ licence: existing });

      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [LicenceUpdateComponent],
        // the shared mock route carries paging parameters rather than a licence
        providers: [{ provide: ActivatedRoute, useValue: { data: routeData } }],
      })
        .overrideTemplate(LicenceUpdateComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(LicenceUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(LicenceService);
      uploadService = fixture.debugElement.injector.get(FileUploadService);
    });

    describe('OnInit', () => {
      it('should fill the form from the licence it is given', () => {
        comp.ngOnInit();

        expect(comp.editForm.controls.id.value).toBe(2);
        expect(comp.editForm.controls.name.value).toBe('Creative Commons Attribution 4.0');
        expect(comp.editForm.controls.abbr.value).toBe('CC BY 4.0');
      });

      it('should show the logo the licence already has', () => {
        comp.ngOnInit();

        expect(comp.currentImage).toBe('cc-by.png');
      });
    });

    describe('validation', () => {
      it('should require a name and an abbreviation', () => {
        comp.editForm.patchValue({ name: '', abbr: '' });

        expect(comp.editForm.controls.name.valid).toBe(false);
        expect(comp.editForm.controls.abbr.valid).toBe(false);
      });

      it('should refuse a link that is not a URL', () => {
        comp.editForm.patchValue({ link: 'creativecommons' });

        expect(comp.editForm.controls.link.valid).toBe(false);
      });

      it('should accept a link with and without a scheme, and none at all', () => {
        comp.editForm.patchValue({ link: 'https://creativecommons.org' });
        expect(comp.editForm.controls.link.valid).toBe(true);

        comp.editForm.patchValue({ link: 'www.creativecommons.org' });
        expect(comp.editForm.controls.link.valid).toBe(true);

        comp.editForm.patchValue({ link: null });
        expect(comp.editForm.controls.link.valid).toBe(true);
      });
    });

    describe('saving', () => {
      beforeEach(() => {
        comp.ngOnInit();
      });

      it('should update a licence that already exists', () => {
        const spy = spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: existing })));

        comp.save();

        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ id: 2, abbr: 'CC BY 4.0' }));
      });

      it('should create a licence that has no id yet', () => {
        routeData.next({ licence: { name: 'Open Government Licence', abbr: 'OGL' } });
        comp.ngOnInit();
        const spy = spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: existing })));

        comp.save();

        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ id: undefined, abbr: 'OGL' }));
      });

      it('should carry the logo through as the licence link', () => {
        const spy = spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: existing })));
        comp.currentImage = 'ogl.png';

        comp.save();

        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ logoLink: 'ogl.png' }));
      });

      it('should leave an empty link off the licence', () => {
        const spy = spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: existing })));
        comp.editForm.patchValue({ link: '' });

        comp.save();

        expect(spy.calls.mostRecent().args[0].link).toBeUndefined();
      });

      it('should stop saving and go back once the server accepts it', () => {
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: existing })));
        const back = spyOn(window.history, 'back');

        comp.save();

        expect(comp.isSaving).toBe(false);
        expect(back).toHaveBeenCalled();
      });

      it('should stay on the form when the server rejects it', () => {
        spyOn(service, 'update').and.returnValue(throwError(() => new Error('nope')));
        const back = spyOn(window.history, 'back');

        comp.save();

        expect(comp.isSaving).toBe(false);
        expect(back).not.toHaveBeenCalled();
      });
    });

    describe('the logo', () => {
      // the component reaches for FileList.item, which a plain array does not have
      const fileList = (files: File[]) => ({ length: files.length, item: (i: number) => files[i] ?? null }) as unknown as FileList;
      const fileEvent = (files: FileList | null) => ({ target: { files } }) as unknown as Event;

      it('should report the upload progress', () => {
        spyOn(uploadService, 'uploadLicenseImage').and.returnValue(
          of({ type: HttpEventType.UploadProgress, loaded: 20, total: 80 } as never),
        );

        comp.selectFile(fileEvent(fileList([new File([''], 'cc.png')])));

        expect(comp.progress.percentage).toBe(25);
      });

      it('should take the stored name from the location the server returns', () => {
        spyOn(uploadService, 'uploadLicenseImage').and.returnValue(
          of(new HttpHeaderResponse({ headers: new HttpHeaders({ location: 'https://cvs/licence/cc-by-2.png' }) }) as never),
        );

        comp.selectFile(fileEvent(fileList([new File([''], 'cc.png')])));

        expect(comp.currentImage).toBe('cc-by-2.png');
      });

      it('should upload nothing when no file was chosen', () => {
        const spy = spyOn(uploadService, 'uploadLicenseImage');

        comp.selectFile(fileEvent(null));

        expect(spy).not.toHaveBeenCalled();
      });

      it('should upload nothing when the chosen list is empty', () => {
        const spy = spyOn(uploadService, 'uploadLicenseImage');

        comp.selectFile(fileEvent(fileList([])));

        expect(spy).not.toHaveBeenCalled();
      });

      it('should clear the logo on removal', () => {
        comp.currentImage = 'cc-by.png';

        comp.removePicture();

        expect(comp.currentImage).toBeUndefined();
      });
    });

    it('should go back to the previous page', () => {
      const back = spyOn(window.history, 'back');

      comp.previousState();

      expect(back).toHaveBeenCalled();
    });
  });
});
