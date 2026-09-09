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
import { HttpTestingController } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { JhiEventManager } from 'ng-jhipster';
import Quill from 'quill';

import { CvsTestModule } from '../../../test.module';
import { MockEventManager } from '../../../helpers/mock-event-manager.service';
import { MetadataItemComponent } from 'app/shared/metadata-item/metadata-item.component';
import { EditorService } from 'app/editor/editor.service';
import { MetadataValue } from 'app/shared/model/metadata-value.model';

describe('Component Tests', () => {
  describe('Metadata Item Component', () => {
    let comp: MetadataItemComponent;
    let fixture: ComponentFixture<MetadataItemComponent>;
    let service: EditorService;
    let httpMock: HttpTestingController;
    let mockEventManager: MockEventManager;

    // the content control only refuses what is shorter than thirty characters, so the tests that
    // are not about validation need a body that clears it
    const longEnough = 'A section long enough to be accepted by the form.';

    const setUp = (metadataValue: MetadataValue, position = 1, newTabLink = true): void => {
      comp.metadataField = { metadataKey: 'system.menu.about', metadataValues: [] };
      comp.metadataValue = metadataValue;
      comp.position = position;
      comp.newTabLink = newTabLink;
      comp.isWriting = false;
      comp.ngOnInit();
    };

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [MetadataItemComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(MetadataItemComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(MetadataItemComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(EditorService);
      httpMock = TestBed.inject(HttpTestingController);
      mockEventManager = TestBed.inject(JhiEventManager) as unknown as MockEventManager;
    });

    afterEach(() => {
      httpMock.verify();
    });

    describe('OnInit', () => {
      it('should fill the form from the metadata value it is given', () => {
        setUp({ id: 5, identifier: 'overview', value: longEnough, position: 2 }, 7);

        expect(comp.metadataForm.getRawValue()).toEqual({
          identifier: 'overview',
          position: 2,
          content: longEnough,
          tableRow: 3,
          tableColumn: 3,
        });
      });

      it('should name a section after its position when the value has no identifier', () => {
        setUp({ value: longEnough }, 4);

        expect(comp.metadataForm.controls.identifier.value).toBe('section-4');
      });

      it('should take the position it was given when the value has none', () => {
        setUp({ identifier: 'overview', value: longEnough }, 4);

        expect(comp.metadataForm.controls.position.value).toBe(4);
      });

      it('should start out not saving', () => {
        setUp({ id: 5, value: longEnough });

        expect(comp.isSaving).toBe(false);
      });
    });

    describe('validation', () => {
      beforeEach(() => {
        setUp({ id: 5, identifier: 'overview', value: longEnough });
      });

      // one identifier per validator on the control: required, the two length bounds and the
      // pattern, then the shape that has to survive all four
      const identifiers: { shape: string; value: string; accepted: boolean }[] = [
        { shape: 'nothing at all', value: '', accepted: false },
        { shape: 'a single character', value: 'a', accepted: false },
        { shape: 'more than forty characters', value: 'a'.repeat(41), accepted: false },
        { shape: 'capitals and spaces', value: 'Overview Section', accepted: false },
        { shape: 'lowercase letters, digits and dashes', value: 'about-us-2', accepted: true },
      ];

      identifiers.forEach(identifier => {
        it(`should ${identifier.accepted ? 'accept' : 'refuse'} an identifier of ${identifier.shape}`, () => {
          comp.metadataForm.controls.identifier.setValue(identifier.value);

          expect(comp.metadataForm.controls.identifier.valid).toBe(identifier.accepted);
        });
      });

      it('should refuse content shorter than thirty characters', () => {
        comp.metadataForm.controls.content.setValue('too short to be a section');

        expect(comp.metadataForm.controls.content.valid).toBe(false);
      });

      it('should accept content of thirty characters or more', () => {
        comp.metadataForm.controls.content.setValue('a'.repeat(30));

        expect(comp.metadataForm.controls.content.valid).toBe(true);
      });
    });

    describe('editing', () => {
      it('should start writing on edit', () => {
        setUp({ id: 5, identifier: 'overview', value: longEnough });

        comp.doEditMetadata();

        expect(comp.isWriting).toBe(true);
      });

      it('should stop writing and put the stored content back on cancel', () => {
        setUp({ id: 5, identifier: 'overview', value: longEnough });
        comp.doEditMetadata();
        comp.metadataForm.controls.content.setValue('an edit that is long enough to be kept');

        comp.cancel();

        expect(comp.isWriting).toBe(false);
        expect(comp.metadataForm.controls.content.value).toBe(longEnough);
      });
    });

    describe('deleting', () => {
      it('should delete the value by id and announce the change', () => {
        setUp({ id: 5, identifier: 'overview', value: longEnough });

        comp.doDeleteMetadata();

        const req = httpMock.expectOne({ method: 'DELETE' });
        expect(req.request.url).toBe(service.resourceEditorMetadataUrl + '/5');
        req.flush({});

        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith('metadataListModification');
      });
    });

    describe('saving', () => {
      it('should put a value that already has an id', () => {
        setUp({ id: 5, identifier: 'overview', value: longEnough });

        comp.saveMetadata();

        const req = httpMock.expectOne({ method: 'PUT' });
        expect(req.request.url).toBe(service.resourceEditorMetadataUrl);
        req.flush({});
      });

      it('should post a value that has no id yet', () => {
        setUp({ identifier: 'overview', value: longEnough });

        comp.saveMetadata();

        const req = httpMock.expectOne({ method: 'POST' });
        expect(req.request.url).toBe(service.resourceEditorMetadataUrl);
        req.flush({});
      });

      it('should send the identifier, position, content, object type and the key of its field', () => {
        setUp({ id: 5, identifier: 'overview', value: longEnough, position: 2 }, 7);

        comp.saveMetadata();

        const req = httpMock.expectOne({ method: 'PUT' });
        expect(req.request.body).toEqual({
          id: 5,
          identifier: 'overview',
          position: 2,
          value: longEnough,
          objectType: 'SYSTEM',
          metadataKey: 'system.menu.about',
        });
        req.flush({});
      });

      it('should fall back to the position it was given when the form carries none', () => {
        setUp({ id: 5, identifier: 'overview', value: longEnough }, 7);
        comp.metadataForm.controls.position.setValue(0);

        comp.saveMetadata();

        const req = httpMock.expectOne({ method: 'PUT' });
        expect(req.request.body.position).toBe(7);
        req.flush({});
      });

      it('should mark itself as saving while the request is out', () => {
        setUp({ id: 5, identifier: 'overview', value: longEnough });

        comp.saveMetadata();

        expect(comp.isSaving).toBe(true);
        httpMock.expectOne({ method: 'PUT' }).flush({});
      });

      it('should stop saving, stop writing and announce the change once the server accepts it', () => {
        setUp({ id: 5, identifier: 'overview', value: longEnough });
        comp.doEditMetadata();

        comp.saveMetadata();
        httpMock.expectOne({ method: 'PUT' }).flush({});

        expect(comp.isSaving).toBe(false);
        expect(comp.isWriting).toBe(false);
        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith('metadataListModification');
      });

      it('should stay on the form when the server rejects it', () => {
        setUp({ id: 5, identifier: 'overview', value: longEnough });
        comp.doEditMetadata();

        comp.saveMetadata();
        httpMock.expectOne({ method: 'PUT' }).error(new ProgressEvent('error'), { status: 500, statusText: 'Server Error' });

        expect(comp.isSaving).toBe(false);
        expect(comp.isWriting).toBe(true);
        expect(mockEventManager.broadcastSpy).not.toHaveBeenCalled();
      });

      describe('links that open in a new tab', () => {
        const withNewTab = 'See <a href="https://cessda.eu" rel="noopener noreferrer" target="_blank">CESSDA</a> for more detail.';
        const stripped = 'See <a href="https://cessda.eu">CESSDA</a> for more detail.';

        it('should keep the new tab attributes when they are wanted', () => {
          setUp({ id: 5, identifier: 'overview', value: withNewTab }, 1, true);

          comp.saveMetadata();

          const req = httpMock.expectOne({ method: 'PUT' });
          expect(req.request.body.value).toBe(withNewTab);
          req.flush({});
        });

        it('should strip the new tab attributes when they are not', () => {
          setUp({ id: 5, identifier: 'overview', value: withNewTab }, 1, false);

          comp.saveMetadata();

          const req = httpMock.expectOne({ method: 'PUT' });
          expect(req.request.body.value).toBe(stripped);
          req.flush({});
        });

        it('should put the stripped content back into the form', () => {
          setUp({ id: 5, identifier: 'overview', value: withNewTab }, 1, false);

          comp.saveMetadata();
          httpMock.expectOne({ method: 'PUT' }).flush({});

          expect(comp.metadataForm.controls.content.value).toBe(stripped);
        });
      });
    });

    describe('the rich text editor', () => {
      it('should paste the current content into the editor once it is created', () => {
        setUp({ id: 5, identifier: 'overview', value: longEnough });
        const dangerouslyPasteHTML = jasmine.createSpy('dangerouslyPasteHTML');
        const quill = { clipboard: { dangerouslyPasteHTML } } as unknown as Quill;

        comp.editorCreated(quill);

        expect(comp.quill).toBe(quill);
        expect(dangerouslyPasteHTML).toHaveBeenCalledWith(longEnough);
      });
    });
  });
});
