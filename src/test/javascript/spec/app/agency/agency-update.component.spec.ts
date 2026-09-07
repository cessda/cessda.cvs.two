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
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { HttpEventType, HttpHeaderResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject, of, throwError } from 'rxjs';

import { CvsTestModule } from '../../test.module';
import { AgencyUpdateComponent } from 'app/agency/agency-update.component';
import { AgencyService } from 'app/agency/agency.service';
import { LicenceService } from 'app/admin/licence/licence.service';
import { FileUploadService } from 'app/shared/upload/file-upload.service';
import { Agency, createNewAgency } from 'app/shared/model/agency.model';
import { Licence } from 'app/shared/model/licence.model';

describe('Component Tests', () => {
  describe('Agency Update Component', () => {
    let comp: AgencyUpdateComponent;
    let fixture: ComponentFixture<AgencyUpdateComponent>;
    let service: AgencyService;
    let licenceService: LicenceService;
    let uploadService: FileUploadService;
    let routeData: BehaviorSubject<{ agency: Agency }>;

    const licences = [
      { id: 1, name: 'CC BY 4.0' },
      { id: 2, name: 'CC BY-SA 4.0' },
    ] as Licence[];

    const existing = createNewAgency({
      id: 4,
      name: 'CESSDA',
      link: 'https://www.cessda.eu',
      description: 'The consortium',
      licenseId: 2,
      uri: 'https://vocabularies.cessda.eu',
      uriCode: 'urn:code',
      canonicalUri: 'https://vocabularies.cessda.eu/canonical',
      logopath: 'cessda.png',
    });

    beforeEach(waitForAsync(() => {
      routeData = new BehaviorSubject<{ agency: Agency }>({ agency: existing });

      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [AgencyUpdateComponent],
        // the shared mock route carries paging parameters rather than an agency
        providers: [{ provide: ActivatedRoute, useValue: { data: routeData } }],
      })
        .overrideTemplate(AgencyUpdateComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(AgencyUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(AgencyService);
      licenceService = fixture.debugElement.injector.get(LicenceService);
      uploadService = fixture.debugElement.injector.get(FileUploadService);

      spyOn(licenceService, 'query').and.returnValue(of(new HttpResponse({ body: licences })));
    });

    describe('OnInit', () => {
      it('should offer the licences the server knows', () => {
        comp.ngOnInit();

        expect(comp.licences).toEqual(licences);
      });

      it('should fill the form from the agency it is given', () => {
        comp.ngOnInit();

        expect(comp.editForm.controls.id.value).toBe(4);
        expect(comp.editForm.controls.name.value).toBe('CESSDA');
        expect(comp.editForm.controls.link.value).toBe('https://www.cessda.eu');
        expect(comp.editForm.controls.licenseId.value).toBe(2);
      });

      it('should show the logo the agency already has', () => {
        comp.ngOnInit();

        expect(comp.currentImage).toBe('cessda.png');
      });

      it('should hold an empty licence list when the server returns no body', () => {
        (licenceService.query as jasmine.Spy).and.returnValue(of(new HttpResponse<Licence[]>({ body: null })));

        comp.ngOnInit();

        expect(comp.licences).toEqual([]);
      });
    });

    describe('validation', () => {
      it('should require a name and a link', () => {
        comp.editForm.patchValue({ name: '', link: '' });

        expect(comp.editForm.controls.name.valid).toBe(false);
        expect(comp.editForm.controls.link.valid).toBe(false);
      });

      it('should refuse a link that is not a URL', () => {
        comp.editForm.patchValue({ link: 'cessda' });

        expect(comp.editForm.controls.link.valid).toBe(false);
      });

      it('should accept a URL with and without a scheme', () => {
        comp.editForm.patchValue({ link: 'https://www.cessda.eu' });
        expect(comp.editForm.controls.link.valid).toBe(true);

        comp.editForm.patchValue({ link: 'www.cessda.eu' });
        expect(comp.editForm.controls.link.valid).toBe(true);
      });
    });

    describe('saving', () => {
      beforeEach(() => {
        comp.ngOnInit();
      });

      it('should update an agency that already exists', () => {
        const spy = spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: existing })));

        comp.save();

        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ id: 4, name: 'CESSDA' }));
      });

      it('should create an agency that has no id yet', () => {
        routeData.next({ agency: createNewAgency({ name: 'GESIS', link: 'https://www.gesis.org' }) });
        comp.ngOnInit();
        const spy = spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: existing })));

        comp.save();

        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ id: undefined, name: 'GESIS' }));
      });

      it('should carry the logo through as the agency path', () => {
        const spy = spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: existing })));
        comp.currentImage = 'gesis.png';

        comp.save();

        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ logopath: 'gesis.png' }));
      });

      it('should name the licence that the chosen id stands for', () => {
        const spy = spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: existing })));

        comp.save();

        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ license: 'CC BY-SA 4.0' }));
      });

      it('should leave the licence out when none is chosen', () => {
        const spy = spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: existing })));
        comp.editForm.patchValue({ licenseId: null });

        comp.save();

        // the name is never looked up, so the property does not appear on the agency at all
        expect(spy.calls.mostRecent().args[0].license).toBeUndefined();
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
      const fileEvent = (file: File) => ({ target: { files: [file] } }) as unknown as Event;

      it('should report the upload progress', () => {
        spyOn(uploadService, 'uploadAgencyImage').and.returnValue(
          of({ type: HttpEventType.UploadProgress, loaded: 30, total: 60 } as never),
        );

        comp.selectFile(fileEvent(new File([''], 'logo.png')));

        expect(comp.progress.percentage).toBe(50);
      });

      it('should take the stored name from the location the server returns', () => {
        spyOn(uploadService, 'uploadAgencyImage').and.returnValue(
          of(new HttpHeaderResponse({ headers: new HttpHeaders({ location: 'https://cvs/agency/logo-1.png' }) }) as never),
        );

        comp.selectFile(fileEvent(new File([''], 'logo.png')));

        expect(comp.currentImage).toBe('logo-1.png');
        expect(comp.progress.percentage).toBe(100);
      });

      it('should remember the file it is uploading', () => {
        const file = new File([''], 'logo.png');
        spyOn(uploadService, 'uploadAgencyImage').and.returnValue(of());

        comp.selectFile(fileEvent(file));

        expect(comp.currentFileUpload).toBe(file);
      });

      it('should clear the logo on removal', () => {
        comp.currentImage = 'cessda.png';

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
