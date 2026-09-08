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
import { JhiEventManager } from 'ng-jhipster';
import { BehaviorSubject, of, throwError } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { MockEventManager } from '../../../helpers/mock-event-manager.service';
import { CustomPageComponent } from 'app/shared/custom-page/custom-page.component';
import { MetadataFieldService } from 'app/entities/metadata-field/metadata-field.service';
import { FileUploadService } from 'app/shared/upload/file-upload.service';
import { METADATA_KEY_ABOUT, METADATA_KEY_API } from 'app/shared/constants/metadata.constants';
import { MetadataField } from 'app/shared/model/metadata-field.model';
import { FileFormat } from 'app/shared/vocabulary-download/FileFormat';

describe('Component Tests', () => {
  describe('Custom Page Component', () => {
    let comp: CustomPageComponent;
    let fixture: ComponentFixture<CustomPageComponent>;
    let metadataFieldService: MetadataFieldService;
    let uploadService: FileUploadService;
    let mockEventManager: MockEventManager;
    let queryParams: BehaviorSubject<Record<string, string>>;

    let findByKey: jasmine.Spy;
    let createObjectURL: jasmine.Spy;
    let revokeObjectURL: jasmine.Spy;

    const docx: FileFormat = { extension: 'docx', mimeType: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' };

    const field = (): MetadataField => ({
      id: 3,
      metadataKey: METADATA_KEY_API,
      metadataValues: [
        { id: 1, identifier: 'overview', value: 'The overview' },
        { id: 2, identifier: 'endpoints', value: 'The endpoints' },
      ],
    });

    // jsdom implements neither of these, so the download path needs them put in place by hand. The
    // stub hands back a fragment rather than a blob URL because the component clicks the link it
    // builds, and a fragment is the one navigation jsdom will follow without complaining.
    const stubObjectUrls = (): void => {
      createObjectURL = jasmine.createSpy('createObjectURL').and.returnValue('#download');
      revokeObjectURL = jasmine.createSpy('revokeObjectURL');
      Object.defineProperty(window.URL, 'createObjectURL', { value: createObjectURL, configurable: true, writable: true });
      Object.defineProperty(window.URL, 'revokeObjectURL', { value: revokeObjectURL, configurable: true, writable: true });
    };

    /**
     * The component reads the query parameters in its constructor, so a test that cares about them
     * pushes its own before asking for the component.
     */
    const createComponent = (pageType = 'api'): void => {
      fixture = TestBed.createComponent(CustomPageComponent);
      comp = fixture.componentInstance;
      comp.pageType = pageType;
    };

    // a real FileList cannot be built here, and the component reaches for item(0) rather than [0]
    const fileEvent = (...files: File[]): Event =>
      ({ target: { files: { length: files.length, item: (index: number) => files[index] ?? null } } }) as unknown as Event;

    beforeEach(waitForAsync(() => {
      queryParams = new BehaviorSubject<Record<string, string>>({});

      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [CustomPageComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { queryParams } as unknown as ActivatedRoute,
          },
        ],
      })
        .overrideTemplate(CustomPageComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      metadataFieldService = TestBed.inject(MetadataFieldService);
      uploadService = TestBed.inject(FileUploadService);
      mockEventManager = TestBed.inject(JhiEventManager) as unknown as MockEventManager;

      findByKey = spyOn(metadataFieldService, 'findByKey').and.returnValue(of(new HttpResponse({ body: field() })));
      stubObjectUrls();

      createComponent();
    });

    describe('which page it shows', () => {
      it('should ask for the API metadata by default', () => {
        comp.ngOnInit();

        expect(comp.metadataKey).toBe(METADATA_KEY_API);
        expect(findByKey).toHaveBeenCalledWith(METADATA_KEY_API);
      });

      it('should ask for the about metadata on the about page', () => {
        createComponent('about');

        comp.ngOnInit();

        expect(comp.metadataKey).toBe(METADATA_KEY_ABOUT);
        expect(findByKey).toHaveBeenCalledWith(METADATA_KEY_ABOUT);
      });
    });

    describe('the DOCX export', () => {
      it('should stay off when the query says nothing', () => {
        expect(comp.enableDocxExport).toBe(false);
      });

      it('should turn on when the query asks for it', () => {
        queryParams.next({ 'docx-export': 'true' });

        createComponent();

        expect(comp.enableDocxExport).toBe(true);
      });

      it('should stay off when the query asks for anything else', () => {
        queryParams.next({ 'docx-export': 'false' });

        createComponent();

        expect(comp.enableDocxExport).toBe(false);
      });
    });

    describe('loading the content', () => {
      it('should take the sections from the field the server returns', () => {
        comp.ngOnInit();

        expect(comp.metadataField).toEqual(field());
        expect(comp.metadataValues.map(value => value.identifier)).toEqual(['overview', 'endpoints']);
      });

      it('should start from an empty field when the server has none', () => {
        findByKey.and.returnValue(of(new HttpResponse<MetadataField>({ body: null })));

        comp.ngOnInit();

        expect(comp.metadataField).toEqual({ metadataKey: METADATA_KEY_API, metadataValues: [] });
        expect(comp.metadataValues).toEqual([]);
      });

      it('should single out the overview section for the menu', () => {
        comp.ngOnInit();

        expect(comp.metadataValueMenu?.id).toBe(1);
      });

      it('should leave the menu section unset when there is no overview', () => {
        const withoutOverview = field();
        withoutOverview.metadataValues = [{ id: 2, identifier: 'endpoints' }];
        findByKey.and.returnValue(of(new HttpResponse({ body: withoutOverview })));

        comp.ngOnInit();

        expect(comp.metadataValueMenu).toBeUndefined();
      });

      it('should ask for the content again when a section is changed', () => {
        const subscribe = spyOn(mockEventManager, 'subscribe');
        comp.ngOnInit();

        expect(subscribe).toHaveBeenCalledWith('metadataListModification', jasmine.any(Function));

        findByKey.calls.reset();
        subscribe.calls.mostRecent().args[1]();

        expect(findByKey).toHaveBeenCalledWith(METADATA_KEY_API);
      });

      it('should stop listening once destroyed', () => {
        const unsubscribe = jasmine.createSpy('unsubscribe');
        spyOn(mockEventManager, 'subscribe').and.returnValue({ unsubscribe } as never);
        comp.ngOnInit();

        comp.ngOnDestroy();

        expect(unsubscribe).toHaveBeenCalled();
      });

      it('should survive being destroyed without a subscription', () => {
        expect(() => comp.ngOnDestroy()).not.toThrow();
      });
    });

    describe('adding a section', () => {
      it('should append an empty section to the field', () => {
        comp.ngOnInit();

        comp.addSection();

        expect(comp.metadataField!.metadataValues).toHaveLength(3);
        expect(comp.metadataField!.metadataValues[2]).toEqual({});
      });
    });

    describe('uploading a DOCX', () => {
      it('should upload nothing when the event carries no file list', () => {
        const upload = spyOn(uploadService, 'uploadFile');

        comp.selectFile({ target: { files: null } } as unknown as Event);

        expect(upload).not.toHaveBeenCalled();
        expect(comp.inDocxProgress).toBe(false);
      });

      it('should upload nothing when the chosen list is empty', () => {
        const upload = spyOn(uploadService, 'uploadFile');

        comp.selectFile(fileEvent());

        expect(upload).not.toHaveBeenCalled();
        expect(comp.inDocxProgress).toBe(false);
      });

      it('should report the upload progress', () => {
        spyOn(uploadService, 'uploadFile').and.returnValue(of({ type: HttpEventType.UploadProgress, loaded: 30, total: 60 } as never));

        comp.selectFile(fileEvent(new File([''], 'about.docx')));

        expect(comp.inDocxProgress).toBe(true);
        expect(comp.progress.percentage).toBe(50);
      });

      it('should convert the file the server stored, taken from the location it returns', () => {
        spyOn(uploadService, 'uploadFile').and.returnValue(
          of(new HttpHeaderResponse({ headers: new HttpHeaders({ location: '/api/upload/file/about.docx' }) }) as never),
        );
        const convert = spyOn(uploadService, 'convertDocsToHtml').and.returnValue(
          of(new HttpResponse({ body: { message: 'about.html' } })),
        );

        comp.selectFile(fileEvent(new File([''], 'about.docx')));

        expect(convert).toHaveBeenCalledWith('about.docx');
        expect(comp.progress.percentage).toBe(100);
      });

      it('should take the extracted name from the response', () => {
        spyOn(uploadService, 'uploadFile').and.returnValue(
          of(new HttpHeaderResponse({ headers: new HttpHeaders({ location: '/api/upload/file/about.docx' }) }) as never),
        );
        spyOn(uploadService, 'convertDocsToHtml').and.returnValue(of(new HttpResponse({ body: { message: 'extracted.html' } })));

        comp.selectFile(fileEvent(new File([''], 'about.docx')));

        expect(comp.uploadFileName).toBe('extracted.html');
        expect(comp.uploadFileStatus).toBe('Docx contents is extracted. See the results:');
      });

      it('should fall back to the uploaded name when the response carries none', () => {
        spyOn(uploadService, 'uploadFile').and.returnValue(
          of(new HttpHeaderResponse({ headers: new HttpHeaders({ location: '/api/upload/file/about.docx' }) }) as never),
        );
        spyOn(uploadService, 'convertDocsToHtml').and.returnValue(of(new HttpResponse<{ message: string }>({ body: null })));

        comp.selectFile(fileEvent(new File([''], 'about.docx')));

        expect(comp.uploadFileName).toBe('about.docx');
      });

      it('should report a problem when the conversion fails', () => {
        spyOn(uploadService, 'uploadFile').and.returnValue(
          of(new HttpHeaderResponse({ headers: new HttpHeaders({ location: '/api/upload/file/about.docx' }) }) as never),
        );
        spyOn(uploadService, 'convertDocsToHtml').and.returnValue(throwError(() => new Error('nope')));

        comp.selectFile(fileEvent(new File([''], 'about.docx')));

        expect(comp.uploadFileStatus).toBe('There is a problem!. Please try again later');
      });
    });

    describe('filling the sections from the upload', () => {
      it('should hand the extracted file and the page key to the server', () => {
        const fill = spyOn(uploadService, 'fillMetadataWithHtmlFile').and.returnValue(of(new HttpResponse<void>({ body: null })));
        comp.ngOnInit();
        comp.uploadFileName = 'extracted.html';

        comp.fillSections();

        expect(fill).toHaveBeenCalledWith('extracted.html', METADATA_KEY_API);
      });

      it('should load the content again once the sections are filled', () => {
        spyOn(uploadService, 'fillMetadataWithHtmlFile').and.returnValue(of(new HttpResponse<void>({ body: null })));
        comp.ngOnInit();
        findByKey.calls.reset();

        // the component also reloads the page here, which jsdom will not do; the content being
        // asked for again is the part that belongs to the component
        comp.fillSections();

        expect(findByKey).toHaveBeenCalledWith(METADATA_KEY_API);
      });

      it('should report a problem when the server refuses', () => {
        spyOn(uploadService, 'fillMetadataWithHtmlFile').and.returnValue(throwError(() => new Error('nope')));
        comp.ngOnInit();

        comp.fillSections();

        expect(comp.uploadFileStatus).toBe('There is a problem!. Please try again later');
      });
    });

    describe('downloading the page', () => {
      let download: jasmine.Spy;
      let anchor: HTMLAnchorElement;

      beforeEach(() => {
        download = spyOn(metadataFieldService, 'downloadMetadataFile').and.returnValue(of(new Blob(['a page'])));

        const create = document.createElement.bind(document);
        anchor = create('a');
        spyOn(document, 'createElement').and.callFake((tag: string) => (tag === 'a' ? anchor : create(tag)));
      });

      it('should ask for the page in the format it was given', () => {
        comp.ngOnInit();

        comp.downloadAsFile(docx);

        expect(download).toHaveBeenCalledWith(METADATA_KEY_API, docx.mimeType);
      });

      it('should hand the download a blob of that format', () => {
        comp.ngOnInit();

        comp.downloadAsFile(docx);

        expect(createObjectURL).toHaveBeenCalled();
        expect((createObjectURL.calls.mostRecent().args[0] as Blob).type).toBe(docx.mimeType);
      });

      it('should name the API download after the API docs', () => {
        comp.ngOnInit();

        comp.downloadAsFile(docx);

        expect(anchor.download).toBe('CESSDA_API-Docs.docx');
      });

      it('should name the about download after the CVS about page', () => {
        createComponent('about');
        comp.ngOnInit();

        comp.downloadAsFile(docx);

        expect(anchor.download).toBe('CESSDA_CVS_About.docx');
      });

      it('should not start a second download while one is still running', () => {
        comp.ngOnInit();
        comp.generatingFile = true;

        comp.downloadAsFile(docx);

        expect(download).not.toHaveBeenCalled();
      });

      it('should be ready for another download once one finishes', () => {
        comp.ngOnInit();

        comp.downloadAsFile(docx);

        expect(comp.generatingFile).toBe(false);
      });
    });
  });
});
