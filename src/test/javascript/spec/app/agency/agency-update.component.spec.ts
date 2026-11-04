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
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { HttpErrorResponse, HttpEventType, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
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
      uri: 'https://vocabularies.cessda.eu/vocabulary/[VOCABULARY]?v=[VERSION]',
      uriCode: 'https://vocabularies.cessda.eu/vocabulary/[VOCABULARY]_[CODE]?v=[VERSION]',
      canonicalUri: 'urn:ddi:int.cessda.cv:',
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

      it('should require the URIs the vocabularies are built from', () => {
        comp.editForm.patchValue({ uri: '', uriCode: null, canonicalUri: '' });

        expect(comp.editForm.controls.uri.errors).toEqual({ required: true });
        expect(comp.editForm.controls.uriCode.errors).toEqual({ required: true });
        expect(comp.editForm.controls.canonicalUri.errors).toEqual({ required: true });
      });

      it('should treat a field of only spaces as missing, as the server does', () => {
        comp.editForm.patchValue({ name: '  ', link: ' ', uri: '   ', uriCode: ' ', canonicalUri: '  ' });

        expect(comp.editForm.controls.name.errors).toEqual({ required: true });
        expect(comp.editForm.controls.link.errors?.required).toBe(true);
        expect(comp.editForm.controls.uri.errors).toEqual({ required: true });
        expect(comp.editForm.controls.uriCode.errors).toEqual({ required: true });
        expect(comp.editForm.controls.canonicalUri.errors).toEqual({ required: true });
      });

      it('should not send a form with a field of only spaces', () => {
        comp.ngOnInit();
        const spy = spyOn(service, 'update');
        comp.editForm.patchValue({ canonicalUri: '  ' });

        comp.save();

        expect(spy).not.toHaveBeenCalled();
        expect(comp.editForm.controls.canonicalUri.touched).toBe(true);
      });

      it('should name the placeholders a URI is missing', () => {
        comp.editForm.patchValue({ uri: 'https://cessda.eu/[VERSION]', uriCode: 'https://cessda.eu/[VERSION]' });

        expect(comp.editForm.controls.uri.errors).toEqual({ placeholders: '[VOCABULARY]' });
        expect(comp.editForm.controls.uriCode.errors).toEqual({ placeholders: '[VOCABULARY], [CODE]' });
      });

      it('should accept URIs with their placeholders', () => {
        comp.updateForm(existing);

        expect(comp.editForm.valid).toBe(true);
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
        routeData.next({ agency: createNewAgency({ ...existing, id: undefined, name: 'GESIS', link: 'https://www.gesis.org' }) });
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

      it('should save without a licence the list does not hold', () => {
        const spy = spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: existing })));
        comp.editForm.patchValue({ licenseId: 99 });

        comp.save();

        expect(spy).toHaveBeenCalled();
        expect(spy.calls.mostRecent().args[0].license).toBeUndefined();
      });

      it('should trim the texts before saving', () => {
        const spy = spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: existing })));
        comp.editForm.patchValue({ name: ' CESSDA ', uri: ' ' + existing.uri, canonicalUri: 'urn:ddi:int.cessda.cv: ' });

        comp.save();

        expect(spy).toHaveBeenCalledWith(
          jasmine.objectContaining({ name: 'CESSDA', uri: existing.uri, canonicalUri: 'urn:ddi:int.cessda.cv:' }),
        );
      });

      it('should show every error instead of saving an incomplete form', () => {
        const spy = spyOn(service, 'update');
        comp.editForm.patchValue({ name: '', uri: '' });

        comp.save();

        expect(spy).not.toHaveBeenCalled();
        expect(comp.isSaving).toBe(false);
        expect(comp.editForm.controls.name.touched).toBe(true);
        expect(comp.editForm.controls.uri.touched).toBe(true);
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
          of(new HttpResponse({ status: 201, headers: new HttpHeaders({ location: 'https://cvs/agency/logo-1.png' }) }) as never),
        );

        comp.selectFile(fileEvent(new File([''], 'logo.png')));

        expect(comp.currentImage).toBe('logo-1.png');
        expect(comp.progress.percentage).toBe(100);
      });

      it('should drop the progress bar and keep the logo when the upload fails', () => {
        comp.currentImage = 'cessda.png';
        spyOn(uploadService, 'uploadAgencyImage').and.returnValue(throwError(() => new HttpErrorResponse({ status: 500 })));

        comp.selectFile(fileEvent(new File([''], 'logo.png')));

        expect(comp.currentFileUpload).toBeNull();
        expect(comp.currentImage).toBe('cessda.png');
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
    describe('the licence select', () => {
      beforeEach(() => {
        TestBed.resetTestingModule();
        TestBed.configureTestingModule({
          imports: [CvsTestModule, ReactiveFormsModule],
          declarations: [AgencyUpdateComponent],
          providers: [{ provide: ActivatedRoute, useValue: { data: routeData } }],
          schemas: [NO_ERRORS_SCHEMA],
        });
        fixture = TestBed.createComponent(AgencyUpdateComponent);
        comp = fixture.componentInstance;
        spyOn(TestBed.inject(LicenceService), 'query').and.returnValue(of(new HttpResponse({ body: licences })));
        fixture.detectChanges();
      });

      it('should hand over the chosen licence as its numeric id', () => {
        const select: HTMLSelectElement = fixture.nativeElement.querySelector('#field_licenseId');

        select.selectedIndex = [...select.options].findIndex(o => o.textContent === 'CC BY 4.0');
        select.dispatchEvent(new Event('change'));

        expect(comp.editForm.controls.licenseId.value).toBe(1);
      });

      it('should offer to remove the licence', () => {
        const select: HTMLSelectElement = fixture.nativeElement.querySelector('#field_licenseId');

        select.selectedIndex = 0;
        select.dispatchEvent(new Event('change'));

        expect(comp.editForm.controls.licenseId.value).toBeNull();
      });
    });
  });
});
