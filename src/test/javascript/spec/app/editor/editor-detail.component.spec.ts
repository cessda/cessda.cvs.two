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
import { ElementRef } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import { ActivatedRoute, Data, NavigationEnd, NavigationStart, Params, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { advanceTo, clear } from 'jest-date-mock';
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs';

import { CvsTestModule } from '../../test.module';
import { renderQuillEditor, toolbarControls } from '../../helpers/quill-editor';
import { MockAccountService } from '../../helpers/mock-account.service';
import { MockEventManager } from '../../helpers/mock-event-manager.service';
import { MockRouter } from '../../helpers/mock-route.service';
import { EditorDetailComponent } from 'app/editor/editor-detail.component';
import { EditorService } from 'app/editor/editor.service';
import { EditorDetailCvAddEditDialogComponent } from 'app/editor/editor-detail-cv-add-edit-dialog.component';
import { EditorDetailCvCommentDialogComponent } from 'app/editor/editor-detail-cv-comment-dialog.component';
import { EditorDetailCvDeleteDialogComponent } from 'app/editor/editor-detail-cv-delete-dialog.component';
import { EditorDetailCvForwardStatusDialogComponent } from 'app/editor/editor-detail-cv-forward-status-dialog.component';
import { EditorDetailCvNewVersionDialogComponent } from 'app/editor/editor-detail-cv-new-version-dialog.component';
import { EditorDetailCodeAddEditDialogComponent } from 'app/editor/editor-detail-code-add-edit-dialog.component';
import { EditorDetailCodeCsvImportDialogComponent } from 'app/editor/editor-detail-code-csv-import-dialog.component';
import { EditorDetailCodeDeleteDialogComponent } from 'app/editor/editor-detail-code-delete-dialog.component';
import { EditorDetailCodeDeprecateDialogComponent } from 'app/editor/editor-detail-code-deprecate-dialog.component';
import { EditorDetailCodeReorderDialogComponent } from 'app/editor/editor-detail-code-reorder-dialog.component';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { VocabularyLanguageFromKeyPipe } from 'app/shared/language/vocabulary-language-from-key.pipe';
import { RouteEventsService } from 'app/shared/service/route-events.service';
import { createNewVocabulary, Vocabulary } from 'app/shared/model/vocabulary.model';
import { createNewVersion, Version } from 'app/shared/model/version.model';
import { Concept } from 'app/shared/model/concept.model';
import { ActionType } from 'app/shared/model/enumerations/action-type.model';
import { UserAgency } from 'app/shared/model/user-agency.model';
import Quill from 'quill';

describe('Component Tests', () => {
  describe('Editor Detail Component', () => {
    let comp: EditorDetailComponent;
    let fixture: ComponentFixture<EditorDetailComponent>;
    let service: EditorService;
    let modalStub: { componentInstance: Record<string, unknown> };
    let modalService: { open: jasmine.Spy };
    let previousRoutePath: BehaviorSubject<string>;
    // the component reads the route in its constructor, so each test sets these before creating it
    const route: { queryParams: Observable<Params>; data: Observable<Data> } = { queryParams: of({}), data: of({}) };

    const slVersion: Version = {
      ...createNewVersion(1),
      itemType: 'SL',
      language: 'en',
      number: '2.0.0',
      status: 'PUBLISHED',
      notation: 'AnalysisUnit',
    };
    const tlVersion: Version = {
      ...createNewVersion(2),
      itemType: 'TL',
      language: 'de',
      number: '2.0.1',
      status: 'DRAFT',
      notation: 'AnalysisUnit',
    };

    const vocabularyWith = (versions: Version[]): Vocabulary =>
      createNewVocabulary({
        id: 3,
        agencyId: 5,
        notation: 'AnalysisUnit',
        sourceLanguage: 'en',
        versionNumber: '2.0.0',
        selectedLang: 'en',
        versions,
      });

    // built anew for each test, since the component writes onto the vocabulary and its versions
    const freshVocabulary = (): Vocabulary =>
      createNewVocabulary({
        id: 3,
        agencyId: 5,
        notation: 'AnalysisUnit',
        sourceLanguage: 'en',
        versionNumber: '2.0',
        selectedLang: 'en',
        versions: [
          {
            ...createNewVersion(1),
            itemType: 'SL',
            language: 'en',
            number: '2.0',
            status: 'PUBLISHED',
            notation: 'AnalysisUnit',
            title: 'Analysis unit',
            definition: 'The unit of analysis',
            notes: 'SL notes',
            versionNotes: 'Version notes',
            versionChanges: 'Version changes',
            ddiUsage: 'DDI',
            translateAgency: 'CESSDA',
            translateAgencyLink: 'https://www.cessda.eu',
            comments: [{}, {}] as Version['comments'],
            concepts: [
              { id: 11, notation: 'A', position: 0, title: 'Alpha', definition: 'First' },
              { id: 12, notation: 'B', position: 1, parent: 'A', title: 'Beta', definition: 'Second', deprecated: true },
            ],
          },
          {
            ...createNewVersion(2),
            itemType: 'TL',
            language: 'de',
            number: '2.0.1',
            status: 'DRAFT',
            notation: 'AnalysisUnit',
            concepts: [{ id: 21, notation: 'B', title: 'Beta DE' }],
          },
        ],
      });

    const createComponent = (queryParams: Params = {}): void => {
      route.queryParams = of(queryParams);
      fixture = TestBed.createComponent(EditorDetailComponent);
      comp = fixture.componentInstance;
    };

    beforeEach(waitForAsync(() => {
      modalStub = { componentInstance: {} };
      modalService = { open: jasmine.createSpy('open').and.returnValue(modalStub) };
      previousRoutePath = new BehaviorSubject<string>('');
      route.queryParams = of({});
      route.data = of({});

      TestBed.configureTestingModule({
        imports: [CvsTestModule],
        declarations: [EditorDetailComponent],
        providers: [
          VocabularyLanguageFromKeyPipe,
          // the real service subscribes to router events, which the mock router does not emit
          { provide: RouteEventsService, useValue: { previousRoutePath } },
          // the test module provides NgbModal as null, and this component opens dialogs
          { provide: NgbModal, useValue: modalService },
          { provide: ActivatedRoute, useValue: route },
        ],
      })
        .overrideTemplate(EditorDetailComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      createComponent();
      service = fixture.debugElement.injector.get(EditorService);

      comp.vocabulary = vocabularyWith([slVersion, tlVersion]);
      comp.version = slVersion;
    });

    describe('reading the source language version', () => {
      it('should pick the SL version out of the vocabulary', () => {
        expect(comp.getSlVersion()).toBe(slVersion);
      });

      it('should return the SL version number of the vocabulary when given nothing', () => {
        expect(comp.getSlVersionNumber()).toBe('2.0.0');
      });

      it('should normalise a version number it is given', () => {
        expect(comp.getSlVersionNumber('4.2.7')).toBe('4.2.7');
      });

      it('should expose the major and minor parts', () => {
        expect(comp.getSlMajorMinorVersionNumber()).toBe('2.0');
        expect(comp.getSlMajorVersionNumber()).toBe(2);
        expect(comp.getSlMinorVersionNumber()).toBe(0);
      });
    });

    describe('listing languages and versions', () => {
      it('should list each language once', () => {
        expect(comp.getUniqueVersionLang()).toEqual(['en', 'de']);
      });

      it('should return nothing when the vocabulary has no versions', () => {
        comp.vocabulary = createNewVocabulary({});

        expect(comp.getUniqueVersionLang()).toEqual([]);
        expect(comp.getVersionsByLanguage('en')).toEqual([]);
      });

      it('should return nothing when the vocabulary carries no version list at all', () => {
        comp.vocabulary = { ...createNewVocabulary({}), versions: undefined as unknown as Version[] };

        expect(comp.getUniqueVersionLang()).toEqual([]);
        expect(comp.getVersionsByLanguage('en')).toEqual([]);
      });

      it('should filter the versions by language', () => {
        expect(comp.getVersionsByLanguage('de')).toEqual([tlVersion]);
        expect(comp.getVersionsByLanguage('fr')).toEqual([]);
      });
    });

    describe('describing a version', () => {
      it('should leave the status out of a published version', () => {
        expect(comp.getFormattedVersionTooltip(slVersion)).toBe('English (en) v.2.0.0');
      });

      it('should prefix an unpublished version with its status', () => {
        expect(comp.getFormattedVersionTooltip(tlVersion)).toBe('German (de) v.DRAFT-2.0.1');
      });

      it('should mark the source language', () => {
        expect(comp.getFormattedVersionTooltip(slVersion, 'en')).toBe('English (en) v.2.0.0 SOURCE');
      });

      it('should report the status of the first version in a language', () => {
        expect(comp.isVersionStatus('en', 'PUBLISHED')).toBe(true);
        expect(comp.isVersionStatus('de', 'PUBLISHED')).toBe(false);
      });
    });

    describe('bundle membership', () => {
      it('should fall back to the vocabulary version number', () => {
        expect(comp.isAnyLangVersionInBundle(comp.vocabulary, 'en')).toBe(true);
        expect(comp.isAnyLangVersionInBundle(comp.vocabulary, 'de')).toBe(false);
      });

      it('should honour an explicitly given bundle', () => {
        expect(comp.isAnyLangVersionInBundle(comp.vocabulary, 'de', '2.0.1')).toBe(true);
        expect(comp.isAnyLangVersionInBundle(comp.vocabulary, 'de', '9.9.9')).toBe(false);
      });
    });

    it('should report deprecated concepts', () => {
      const concepts: Concept[] = [{ notation: 'a' }, { notation: 'b', deprecated: true }];

      expect(comp.hasDeprecatedConcepts(concepts)).toBe(true);
      expect(comp.hasDeprecatedConcepts([concepts[0]])).toBe(false);
    });

    it('should expose the server origin', () => {
      expect(comp.getServerUrl()).toBe(window.location.origin);
    });

    describe('saving parts of the version', () => {
      let spy: jasmine.Spy;

      beforeEach(() => {
        spy = spyOn(service, 'updateVocabulary').and.returnValue(of({} as never));
      });

      it('should save the DDI usage on its own action', fakeAsync(() => {
        comp.editorDetailForm.patchValue({ ddiUsage: 'Some usage' });

        comp.saveDdiUsage();
        tick();

        expect(spy).toHaveBeenCalledWith({
          actionType: ActionType.EDIT_DDI_CV,
          agencyId: 5,
          vocabularyId: 3,
          versionId: 1,
          language: 'en',
          itemType: 'SL',
          ddiUsage: 'Some usage',
        });
      }));

      it('should write the saved DDI usage back onto the version', fakeAsync(() => {
        comp.editorDetailForm.patchValue({ ddiUsage: 'Some usage' });

        comp.saveDdiUsage();
        tick();

        expect(comp.version?.ddiUsage).toBe('Some usage');
      }));

      it('should save the notes on their own action', fakeAsync(() => {
        comp.editorDetailForm.patchValue({ notes: 'A note' });

        comp.saveNotes();
        tick();

        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ actionType: ActionType.EDIT_NOTE_CV, notes: 'A note' }));
      }));

      it('should leave empty notes off the snippet', fakeAsync(() => {
        comp.editorDetailForm.patchValue({ notes: '' });

        comp.saveNotes();
        tick();

        expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ notes: undefined }));
      }));

      it('should save the version notes and changes together', fakeAsync(() => {
        comp.editorDetailForm.patchValue({ versionNotes: 'Notes', versionChanges: 'Changes' });

        comp.saveCurrentVersionInfo();
        tick();

        expect(spy).toHaveBeenCalledWith(
          jasmine.objectContaining({
            actionType: ActionType.EDIT_VERSION_INFO_CV,
            versionNotes: 'Notes',
            versionChanges: 'Changes',
          }),
        );
      }));

      it('should save the translating agency once the link is a URL', fakeAsync(() => {
        comp.editorDetailForm.patchValue({
          translateAgency: 'GESIS',
          translateAgencyLink: 'https://www.gesis.org',
        });

        comp.saveTranslateIdentity();
        tick();

        expect(spy).toHaveBeenCalledWith(
          jasmine.objectContaining({
            actionType: ActionType.EDIT_IDENTITY_CV,
            translateAgency: 'GESIS',
            translateAgencyLink: 'https://www.gesis.org',
          }),
        );
      }));

      it('should refuse to save a translating agency link that is not a URL', () => {
        comp.editorDetailForm.patchValue({ translateAgency: 'GESIS', translateAgencyLink: 'not a url' });

        comp.saveTranslateIdentity();

        expect(spy).not.toHaveBeenCalled();
      });
    });

    describe('closing an edit', () => {
      it('should restore the notes from the version', () => {
        comp.version = { ...slVersion, notes: 'Stored note' };
        comp.editorDetailForm.patchValue({ notes: 'Unsaved edit' });

        comp.closeNotes();

        expect(comp.isNotesEdit).toBe(false);
        expect(comp.editorDetailForm.controls.notes.value).toBe('Stored note');
      });
    });

    describe('escaping CSV content', () => {
      it('should strip trailing newlines', () => {
        expect(comp.escapeCsvContent('a\n\n\n')).toBe('a');
      });

      it('should keep an interior newline and quote the cell', () => {
        expect(comp.escapeCsvContent('a\nb')).toBe('"a\nb"');
      });

      it('should double an embedded quote and quote the cell', () => {
        expect(comp.escapeCsvContent('say "hi"')).toBe('"say ""hi"""');
      });

      it('should quote a cell containing a comma', () => {
        expect(comp.escapeCsvContent('a,b')).toBe('"a,b"');
      });

      it('should return an empty string for undefined content', () => {
        expect(comp.escapeCsvContent(undefined)).toBe('');
      });

      it('should leave the carriage return of a CRLF line ending', () => {
        expect(comp.escapeCsvContent('a\r\n')).toBe('a\r');
      });
    });
    describe('reading the query parameters', () => {
      it('should open on the detail tab without a code or popup by default', () => {
        expect(comp.initialTabSelected).toBe('detail');
        expect(comp.initialLangSelect).toBeNull();
        expect(comp.currentSelectedCode).toBe('');
        expect(comp.activePopup).toBe('');
        expect(comp.enableDocxExport).toBe(false);
      });

      it('should take the language and tab', () => {
        createComponent({ lang: 'de', tab: 'version' });

        expect(comp.initialLangSelect).toBe('de');
        expect(comp.initialTabSelected).toBe('version');
      });

      it('should jump to the detail tab for a code', () => {
        createComponent({ tab: 'version', code: 'B' });

        expect(comp.initialTabSelected).toBe('detail');
        expect(comp.currentSelectedCode).toBe('B');
      });

      it('should remember the popup to open', () => {
        createComponent({ popup: 'edit-cv' });

        expect(comp.activePopup).toBe('edit-cv');
      });

      it('should switch to the export tab and enable the DOCX export', () => {
        createComponent({ tab: 'version', 'docx-export': 'true' });

        expect(comp.initialTabSelected).toBe('export');
        expect(comp.enableDocxExport).toBe(true);
      });

      it('should switch to the export tab without enabling the DOCX export for any other value', () => {
        createComponent({ 'docx-export': 'false' });

        expect(comp.initialTabSelected).toBe('export');
        expect(comp.enableDocxExport).toBe(false);
      });
    });

    describe('activating a version', () => {
      let eventManager: MockEventManager;
      let vocabulary: Vocabulary;

      beforeEach(() => {
        eventManager = TestBed.inject(EventManager) as unknown as MockEventManager;
        vocabulary = freshVocabulary();
        comp.vocabulary = vocabulary;
      });

      it('should select the version of the language and fill the form from it', () => {
        comp.setActiveVersion('en');

        expect(comp.version).toBe(vocabulary.versions[0]);
        expect(vocabulary.selectedLang).toBe('en');
        expect(vocabulary.selectedVersion).toBeUndefined();
        expect(comp.editorDetailForm.getRawValue()).toEqual(
          jasmine.objectContaining({
            ddiUsage: 'DDI',
            translateAgency: 'CESSDA',
            translateAgencyLink: 'https://www.cessda.eu',
            notes: 'SL notes',
            versionNotes: 'Version notes',
            versionChanges: 'Version changes',
          }),
        );
        expect(comp.noOfComments).toBe(2);
      });

      it('should pick a version by its number', () => {
        vocabulary.versions.push({ ...createNewVersion(3), itemType: 'SL', language: 'en', number: '1.0.0', status: 'PUBLISHED' });

        comp.setActiveVersion('en', '1.0');

        expect(comp.version?.id).toBe(3);
        expect(vocabulary.selectedVersion).toBe('1.0');
      });

      it('should close every open edit', () => {
        comp.isNotesEdit = true;
        comp.isCurrentVersionInfoEdit = true;
        comp.isDdiUsageEdit = true;
        comp.isIdentityEdit = true;

        comp.setActiveVersion('en');

        expect(comp.isNotesEdit).toBe(false);
        expect(comp.isCurrentVersionInfoEdit).toBe(false);
        expect(comp.isDdiUsageEdit).toBe(false);
        expect(comp.isIdentityEdit).toBe(false);
      });

      it('should count no comments when the version has none', () => {
        vocabulary.versions[0].comments = undefined as unknown as Version['comments'];

        comp.setActiveVersion('en');

        expect(comp.noOfComments).toBe(0);
      });

      it('should drop the selected concept', () => {
        comp.setActiveVersion('en');

        expect(eventManager.broadcastSpy).toHaveBeenCalledWith('deselectConcept');
      });

      it('should allow adding a translation to a published source version', () => {
        comp.setActiveVersion('en');

        expect(comp.enableAddTl).toBe(true);
        expect(comp.enablePublishTl).toBe(false);
      });

      it('should allow adding a translation to a source version ready to translate', () => {
        vocabulary.versions[0].status = 'READY_TO_TRANSLATE';

        comp.setActiveVersion('en');

        expect(comp.enableAddTl).toBe(true);
      });

      it('should not allow adding a translation to a draft source version', () => {
        vocabulary.versions[0].status = 'DRAFT';

        comp.setActiveVersion('en');

        expect(comp.enableAddTl).toBe(false);
      });

      it('should allow publishing translations once one of them is ready to publish', () => {
        vocabulary.versions[1].status = 'READY_TO_PUBLISH';

        comp.setActiveVersion('en');

        expect(comp.enablePublishTl).toBe(true);
      });

      it('should neither add nor publish translations from a target version', () => {
        vocabulary.versions[1].status = 'READY_TO_PUBLISH';
        comp.enableAddTl = true;
        comp.enablePublishTl = true;

        comp.setActiveVersion('de');

        expect(comp.version).toBe(vocabulary.versions[1]);
        expect(comp.version?.languageSl).toBe('en');
        expect(comp.enableAddTl).toBe(false);
        expect(comp.enablePublishTl).toBe(false);
      });

      it('should align the target codes with the source codes', () => {
        comp.setActiveVersion('de');

        const beta = comp.version!.concepts.find(c => c.notation === 'B');
        expect(beta).toEqual(
          jasmine.objectContaining({ id: 21, title: 'Beta DE', position: 1, parent: 'A', titleSl: 'Beta', definitionSl: 'Second' }),
        );
      });

      it('should add a placeholder for a source code that has no translation yet', () => {
        comp.setActiveVersion('de');

        expect(comp.version!.concepts.map(c => c.notation)).toEqual(['A', 'B']);
        expect(comp.version!.concepts[0]).toEqual({
          visible: true,
          position: 0,
          parent: undefined,
          notation: 'A',
          titleSl: 'Alpha',
          definitionSl: 'First',
          slConcept: 11,
        });
      });
    });

    describe('the collapsible panels', () => {
      const panels: {
        panel: 'detailEPanel' | 'versionEPanel' | 'identityEPanel' | 'usageEPanel' | 'licenseEPanel' | 'exportEPanel';
        flag:
          'isDetailCollapse' | 'isVersionCollapse' | 'isIdentityCollapse' | 'isUsageCollapse' | 'isLicenseCollapse' | 'isExportCollapse';
        toggle: (c: EditorDetailComponent) => void;
      }[] = [
        { panel: 'detailEPanel', flag: 'isDetailCollapse', toggle: c => c.toggleDetailEPanel() },
        { panel: 'versionEPanel', flag: 'isVersionCollapse', toggle: c => c.toggleVersionEPanel() },
        { panel: 'identityEPanel', flag: 'isIdentityCollapse', toggle: c => c.toggleIdentityEPanel() },
        { panel: 'usageEPanel', flag: 'isUsageCollapse', toggle: c => c.toggleUsageEPanel() },
        { panel: 'licenseEPanel', flag: 'isLicenseCollapse', toggle: c => c.toggleLicenseEPanel() },
        { panel: 'exportEPanel', flag: 'isExportCollapse', toggle: c => c.toggleExportEPanel() },
      ];

      panels.forEach(({ panel, flag, toggle }) => {
        describe(`for ${panel}`, () => {
          let element: HTMLElement;

          beforeEach(() => {
            element = document.createElement('div');
            comp[panel] = new ElementRef(element);
          });

          it('should open a panel that has not been shown yet', () => {
            toggle(comp);

            expect(element.style.display).toBe('block');
            expect(comp[flag]).toBe(false);
          });

          it('should open a hidden panel', () => {
            element.style.display = 'none';

            toggle(comp);

            expect(element.style.display).toBe('block');
            expect(comp[flag]).toBe(false);
          });

          it('should close an open panel', () => {
            toggle(comp);
            toggle(comp);

            expect(element.style.display).toBe('none');
            expect(comp[flag]).toBe(true);
          });
        });
      });
    });

    it('should toggle showing the deprecated codes', () => {
      comp.toggleDeprecatedCodesDisplay();
      expect(comp.isShowingDeprecatedCodes).toBe(true);

      comp.toggleDeprecatedCodesDisplay();
      expect(comp.isShowingDeprecatedCodes).toBe(false);
    });

    describe('OnInit', () => {
      let router: MockRouter;
      let accountService: MockAccountService;
      let scrollTo: jasmine.Spy;
      let vocabulary: Vocabulary;

      const account = (userAgencies: UserAgency[]): Account =>
        new Account(1, true, [], 'editor@example.org', 'Ed', 'en', 'Itor', 'editor', '', userAgencies);

      const init = (queryParams: Params = {}): void => {
        createComponent(queryParams);
        comp.ngOnInit();
      };

      beforeEach(() => {
        router = TestBed.inject(Router) as unknown as MockRouter;
        router.setEvents(of(new NavigationStart(1, '/editor')));
        accountService = TestBed.inject(AccountService) as unknown as MockAccountService;
        scrollTo = spyOn(window, 'scrollTo');
        vocabulary = freshVocabulary();
        route.data = of({ vocabulary });
      });

      it('should scroll to the top once a navigation ends', () => {
        router.setEvents(of(new NavigationStart(1, '/editor'), new NavigationEnd(1, '/editor', '/editor')));

        init();

        expect(scrollTo).toHaveBeenCalledTimes(1);
        expect(scrollTo).toHaveBeenCalledWith(0, 0);
      });

      it('should not scroll on any other router event', () => {
        init();

        expect(scrollTo).not.toHaveBeenCalled();
      });

      it('should take the vocabulary from the route with three digit version numbers', () => {
        init();

        expect(comp.vocabulary).toBe(vocabulary);
        expect(vocabulary.versionNumber).toBe('2.0.0');
        expect(vocabulary.versions[0].number).toBe('2.0.0');
      });

      it('should offer only the languages that have no version yet', () => {
        init();

        expect(comp.availableLanguages).not.toContain('en');
        expect(comp.availableLanguages).not.toContain('de');
        expect(comp.availableLanguages).toContain('fr');
      });

      it('should select the tab from the query parameters', () => {
        init({ tab: 'usage' });

        expect(comp.editorDetailForm.controls.tabSelected.value).toBe('usage');
      });

      it('should activate the version of the selected language', () => {
        init();

        expect(comp.version).toBe(vocabulary.versions[0]);
      });

      it('should activate the version of the language asked for', () => {
        init({ lang: 'de' });

        expect(comp.version).toBe(vocabulary.versions[1]);
      });

      it('should fall back to the source language when no version is in the language asked for', () => {
        vocabulary.selectedLang = 'de';

        init({ lang: 'fr' });

        expect(comp.version).toBe(vocabulary.versions[0]);
        expect(vocabulary.selectedLang).toBe('en');
      });

      it('should leave the translator languages empty without an account', () => {
        init();

        expect(comp.account).toBeUndefined();
        expect(comp.availableTlLanguages).toEqual([]);
        expect(comp.tlLanguageStatus).toBe('none');
      });

      it('should collect the languages the user may translate into for this agency', () => {
        const user = account([
          { agencyRole: 'ADMIN_TL', agencyId: 5, language: 'fr' },
          { agencyRole: 'ADMIN_TL', agencyId: 5, language: 'de' },
          { agencyRole: 'ADMIN_TL', agencyId: 6, language: 'cs' },
          { agencyRole: 'ADMIN_CONTENT', agencyId: 5, language: 'sk' },
          { agencyRole: 'ADMIN_TL', agencyId: 5 },
        ]);
        accountService.setIdentityResponse(user);

        init();

        expect(comp.account).toBe(user);
        expect(comp.availableTlLanguages).toEqual(['fr']);
        expect(comp.tlLanguageStatus).toBe('any');
      });

      it('should report no translator languages when the user has none for this agency', () => {
        accountService.setIdentityResponse(account([{ agencyRole: 'ADMIN', agencyId: 5 }]));

        init();

        expect(comp.availableTlLanguages).toEqual([]);
        expect(comp.tlLanguageStatus).toBe('none');
      });

      describe('opening a popup from the query parameters', () => {
        const popups: { popup: string; isNew: boolean; isSlForm: boolean }[] = [
          { popup: 'add-cv', isNew: true, isSlForm: true },
          { popup: 'edit-cv', isNew: false, isSlForm: true },
          { popup: 'add-tl-cv', isNew: true, isSlForm: false },
          { popup: 'edit-tl-cv', isNew: false, isSlForm: false },
        ];

        popups.forEach(({ popup, isNew, isSlForm }) => {
          it(`should open the CV dialog for ${popup}`, () => {
            init({ popup });

            expect(modalService.open).toHaveBeenCalledWith(EditorDetailCvAddEditDialogComponent, { size: 'xl', backdrop: 'static' });
            expect(modalStub.componentInstance).toEqual(jasmine.objectContaining({ vocabularyParam: vocabulary, isNew, isSlForm }));
          });
        });

        it('should open nothing for a popup it does not know', () => {
          init({ popup: 'unknown' });

          expect(modalService.open).not.toHaveBeenCalled();
        });

        it('should open nothing without a popup', () => {
          init();

          expect(modalService.open).not.toHaveBeenCalled();
        });
      });

      describe('jumping to a code', () => {
        let element: HTMLElement;

        beforeEach(() => {
          element = document.createElement('div');
          element.id = 'code_B';
          element.scrollIntoView = jasmine.createSpy('scrollIntoView');
          document.body.appendChild(element);
        });

        afterEach(() => {
          element.remove();
        });

        it('should show the deprecated codes when the version has some', fakeAsync(() => {
          init({ code: 'B' });
          tick(6500);

          expect(comp.isShowingDeprecatedCodes).toBe(true);
        }));

        it('should keep the deprecated codes hidden when the version has none', fakeAsync(() => {
          vocabulary.versions[0].concepts.forEach(c => (c.deprecated = false));

          init({ code: 'B' });
          tick(6500);

          expect(comp.isShowingDeprecatedCodes).toBe(false);
        }));

        it('should scroll to the code and highlight it for a while', fakeAsync(() => {
          init({ code: 'B' });

          tick(1499);
          expect(element.scrollIntoView).not.toHaveBeenCalled();

          tick(1);
          expect(element.scrollIntoView).toHaveBeenCalledWith({ behavior: 'smooth' });
          expect(element.classList).toContain('highlight');

          tick(5000);
          expect(element.classList).not.toContain('highlight');
        }));

        it('should do nothing when the code is not on the page', fakeAsync(() => {
          init({ code: 'Z' });
          tick(6500);

          expect(element.scrollIntoView).not.toHaveBeenCalled();
          expect(element.classList).not.toContain('highlight');
        }));

        it('should not look for a code when none was asked for', fakeAsync(() => {
          init();
          tick(6500);

          expect(element.scrollIntoView).not.toHaveBeenCalled();
          expect(comp.isShowingDeprecatedCodes).toBe(false);
        }));
      });

      describe('selecting a concept', () => {
        let handlers: Record<string, (event?: EventWithContent<Concept>) => void>;
        let subscriptions: Subscription[];

        beforeEach(() => {
          handlers = {};
          subscriptions = [];
          const eventManager = TestBed.inject(EventManager);
          spyOn(eventManager, 'subscribe').and.callFake((name: string, callback: (event?: EventWithContent<Concept>) => void) => {
            handlers[name] = callback;
            const subscription = new Subscription();
            subscriptions.push(subscription);
            return subscription;
          });
          init();
        });

        it('should keep the concept that was selected', () => {
          const concept: Concept = { notation: 'A' };

          handlers['selectConcept'](new EventWithContent('selectConcept', concept));

          expect(comp.concept).toBe(concept);
        });

        it('should forget the concept once it is deselected', () => {
          comp.concept = { notation: 'A' };

          handlers['deselectConcept']();

          expect(comp.concept).toBeNull();
        });

        it('should stop listening when destroyed', () => {
          comp.ngOnDestroy();

          expect(subscriptions).toHaveLength(2);
          expect(subscriptions.every(s => s.closed)).toBe(true);
        });
      });
    });

    it('should be destroyed safely before it was initialised', () => {
      expect(() => comp.ngOnDestroy()).not.toThrow();
    });

    it('should go back in the browser history', () => {
      const back = spyOn(window.history, 'back');

      comp.previousState();

      expect(back).toHaveBeenCalled();
    });

    describe('going back to the CV search', () => {
      let router: MockRouter;

      beforeEach(() => {
        router = TestBed.inject(Router) as unknown as MockRouter;
      });

      it('should return to the search it came from', () => {
        previousRoutePath.next('/?q=unit&page=2');

        comp.goToCvSearch();

        expect(router.navigateByUrlSpy).toHaveBeenCalledWith('/?q=unit&page=2');
        expect(router.navigateSpy).not.toHaveBeenCalled();
      });

      it('should return to the plain search page', () => {
        previousRoutePath.next('/');

        comp.goToCvSearch();

        expect(router.navigateByUrlSpy).toHaveBeenCalledWith('/');
      });

      it('should go to the search page when it came from elsewhere', () => {
        previousRoutePath.next('/editor/vocabulary/AnalysisUnit');

        comp.goToCvSearch();

        expect(router.navigateSpy).toHaveBeenCalledWith(['']);
        expect(router.navigateByUrlSpy).not.toHaveBeenCalled();
      });
    });

    describe('opening dialogs', () => {
      const xl = { size: 'xl', backdrop: 'static' };
      let vocabulary: Vocabulary;
      let sl: Version;
      let tl: Version;
      const concept: Concept = { id: 11, notation: 'A' };

      beforeEach(() => {
        vocabulary = freshVocabulary();
        [sl, tl] = vocabulary.versions;
        comp.vocabulary = vocabulary;
        comp.version = sl;
        comp.concept = concept;
      });

      it('should open the source CV dialog without a source version', () => {
        comp.openAddEditCvPopup(false, true);

        expect(modalService.open).toHaveBeenCalledWith(EditorDetailCvAddEditDialogComponent, xl);
        expect(modalStub.componentInstance).toEqual({ vocabularyParam: vocabulary, isNew: false, isSlForm: true, versionParam: sl });
      });

      it('should hand the target CV dialog the source version and copy its texts over', () => {
        comp.version = tl;

        comp.openAddEditCvPopup(true, false);

        expect(modalStub.componentInstance).toEqual({
          vocabularyParam: vocabulary,
          isNew: true,
          isSlForm: false,
          versionSlParam: sl,
          versionParam: tl,
        });
        expect(tl.titleSl).toBe('Analysis unit');
        expect(tl.definitionSl).toBe('The unit of analysis');
        expect(tl.notesSl).toBe('SL notes');
      });

      it('should forward the status of a source version', () => {
        comp.openForwardStatusCvPopup();

        expect(modalService.open).toHaveBeenCalledWith(EditorDetailCvForwardStatusDialogComponent, xl);
        expect(modalStub.componentInstance).toEqual({
          vocabularyParam: vocabulary,
          versionParam: sl,
          isSlForm: true,
          slVersionNumber: '2.0',
        });
      });

      it('should forward the status of a target version', () => {
        comp.version = tl;

        comp.openForwardStatusCvPopup();

        expect(modalStub.componentInstance).toEqual(
          jasmine.objectContaining({ versionParam: tl, isSlForm: false, slVersionNumber: '2.0' }),
        );
      });

      it('should open the new version dialog', () => {
        comp.openCreateNewCvVersionPopup();

        expect(modalService.open).toHaveBeenCalledWith(EditorDetailCvNewVersionDialogComponent, xl);
        expect(modalStub.componentInstance).toEqual({ vocabularyParam: vocabulary, versionParam: sl });
      });

      it('should open the CV delete dialog', () => {
        comp.openDeleteCvPopup();

        expect(modalService.open).toHaveBeenCalledWith(EditorDetailCvDeleteDialogComponent, xl);
        expect(modalStub.componentInstance).toEqual({ vocabularyParam: vocabulary, versionParam: sl });
      });

      it('should open the comment dialog', () => {
        comp.openCvCommentPopup();

        expect(modalService.open).toHaveBeenCalledWith(EditorDetailCvCommentDialogComponent, xl);
        expect(modalStub.componentInstance).toEqual({ vocabularyParam: vocabulary, versionParam: sl });
      });

      it('should open the code dialog for a new source code', () => {
        comp.openAddCodePopup();

        expect(modalService.open).toHaveBeenCalledWith(EditorDetailCodeAddEditDialogComponent, xl);
        expect(modalStub.componentInstance).toEqual({
          conceptParam: concept,
          versionParam: sl,
          isNew: true,
          codeInsertMode: undefined,
          isSlForm: true,
        });
      });

      it('should pass on where a new code goes', () => {
        comp.openAddEditCodePopup(true, 'INSERT_AS_CHILD');

        expect(modalStub.componentInstance).toEqual(jasmine.objectContaining({ isNew: true, codeInsertMode: 'INSERT_AS_CHILD' }));
      });

      it('should open the code dialog to edit a source code', () => {
        comp.openEditCodePopup();

        expect(modalStub.componentInstance).toEqual(jasmine.objectContaining({ isNew: false, isSlForm: true }));
      });

      it('should open the code dialog to add a translated code as an edit', () => {
        comp.version = tl;

        comp.openAddTlCode();

        expect(modalService.open).toHaveBeenCalledWith(EditorDetailCodeAddEditDialogComponent, xl);
        expect(modalStub.componentInstance).toEqual(jasmine.objectContaining({ versionParam: tl, isNew: false, isSlForm: false }));
      });

      it('should open the code dialog to edit a translated code', () => {
        comp.version = tl;

        comp.openEditTlCode();

        expect(modalStub.componentInstance).toEqual(jasmine.objectContaining({ versionParam: tl, isNew: false, isSlForm: false }));
      });

      it('should refuse to open the code dialog without a version', () => {
        comp.version = null;

        expect(() => comp.openAddCodePopup()).toThrow(new TypeError('version was null'));
        expect(modalService.open).not.toHaveBeenCalled();
      });

      it('should open the deprecate dialog with every code of the version', () => {
        comp.openDeprecateCodePopup();

        expect(modalService.open).toHaveBeenCalledWith(EditorDetailCodeDeprecateDialogComponent, xl);
        expect(modalStub.componentInstance).toEqual({
          versionParam: sl,
          conceptParam: concept,
          isSlForm: true,
          conceptList: sl.concepts,
        });
      });

      it('should open the code delete dialog', () => {
        comp.openDeleteCodePopup();

        expect(modalService.open).toHaveBeenCalledWith(EditorDetailCodeDeleteDialogComponent, xl);
        expect(modalStub.componentInstance).toEqual({ versionParam: sl, conceptParam: concept, isSlForm: true });
      });

      it('should remove a translated code through the delete dialog', () => {
        comp.version = tl;

        comp.openRemoveTlCode();

        expect(modalService.open).toHaveBeenCalledWith(EditorDetailCodeDeleteDialogComponent, xl);
        expect(modalStub.componentInstance).toEqual({ versionParam: tl, conceptParam: concept, isSlForm: false });
      });

      it('should open the CSV import dialog', () => {
        comp.openCsvImportCodeWindow();

        expect(modalService.open).toHaveBeenCalledWith(EditorDetailCodeCsvImportDialogComponent, xl);
        expect(modalStub.componentInstance).toEqual({ vocabularyParam: vocabulary, versionParam: sl, isSlForm: true });
      });

      it('should open the reorder dialog', () => {
        comp.openReorderCode();

        expect(modalService.open).toHaveBeenCalledWith(EditorDetailCodeReorderDialogComponent, xl);
        expect(modalStub.componentInstance).toEqual({ versionParam: sl, conceptParam: concept });
      });
    });

    describe('writing a save back onto the version', () => {
      let version: Version;

      beforeEach(() => {
        version = freshVocabulary().versions[0];
        comp.version = version;
      });

      it('should store the translating agency and leave the identity edit', () => {
        comp.isIdentityEdit = true;

        comp.subscribeToSaveResponse(of({} as never), {
          actionType: ActionType.EDIT_IDENTITY_CV,
          translateAgency: 'GESIS',
          translateAgencyLink: 'https://www.gesis.org',
        });

        expect(comp.isIdentityEdit).toBe(false);
        expect(version.translateAgency).toBe('GESIS');
        expect(version.translateAgencyLink).toBe('https://www.gesis.org');
      });

      it('should store the notes and leave the notes edit', () => {
        comp.isNotesEdit = true;

        comp.subscribeToSaveResponse(of({} as never), { actionType: ActionType.EDIT_NOTE_CV, notes: 'New notes' });

        expect(comp.isNotesEdit).toBe(false);
        expect(version.notes).toBe('New notes');
      });

      it('should store the version notes and changes and leave the version info edit', () => {
        comp.isCurrentVersionInfoEdit = true;

        comp.subscribeToSaveResponse(of({} as never), {
          actionType: ActionType.EDIT_VERSION_INFO_CV,
          versionNotes: 'New version notes',
          versionChanges: 'New version changes',
        });

        expect(comp.isCurrentVersionInfoEdit).toBe(false);
        expect(version.versionNotes).toBe('New version notes');
        expect(version.versionChanges).toBe('New version changes');
      });

      it('should leave the version alone for any other action', () => {
        const before = { ...version };

        comp.subscribeToSaveResponse(of({} as never), { actionType: ActionType.EDIT_CV, notes: 'Ignored' });

        expect(version).toEqual(before);
        expect(comp.isNotesEdit).toBe(false);
      });

      it('should change nothing before the save has answered', () => {
        comp.isNotesEdit = true;

        comp.subscribeToSaveResponse(new Observable<never>(), { actionType: ActionType.EDIT_NOTE_CV, notes: 'Pending' });

        expect(comp.isNotesEdit).toBe(true);
        expect(version.notes).toBe('SL notes');
      });
    });

    describe('closing the other edits', () => {
      beforeEach(() => {
        comp.version = freshVocabulary().versions[0];
      });

      it('should restore the DDI usage from the version', () => {
        comp.isDdiUsageEdit = true;
        comp.editorDetailForm.patchValue({ ddiUsage: 'Unsaved edit' });

        comp.closeDdiUsage();

        expect(comp.isDdiUsageEdit).toBe(false);
        expect(comp.editorDetailForm.controls.ddiUsage.value).toBe('DDI');
      });

      it('should restore the translating agency from the version', () => {
        comp.isIdentityEdit = true;
        comp.editorDetailForm.patchValue({ translateAgency: 'Unsaved', translateAgencyLink: 'https://unsaved.example.org' });

        comp.closeTranslateIdentity();

        expect(comp.isIdentityEdit).toBe(false);
        expect(comp.editorDetailForm.controls.translateAgency.value).toBe('CESSDA');
        expect(comp.editorDetailForm.controls.translateAgencyLink.value).toBe('https://www.cessda.eu');
      });

      it('should restore the version notes and changes from the version', () => {
        comp.isCurrentVersionInfoEdit = true;
        comp.editorDetailForm.patchValue({ versionNotes: 'Unsaved', versionChanges: 'Unsaved' });

        comp.closeCurrentVersionInfo();

        expect(comp.isCurrentVersionInfoEdit).toBe(false);
        expect(comp.editorDetailForm.controls.versionNotes.value).toBe('Version notes');
        expect(comp.editorDetailForm.controls.versionChanges.value).toBe('Version changes');
      });
    });

    describe('exporting the codes as CSV', () => {
      let createObjectURL: jasmine.Spy;
      let revokeObjectURL: jasmine.Spy;
      let downloads: string[];

      beforeEach(() => {
        advanceTo(new Date(2026, 2, 14, 12, 0, 0));
        createObjectURL = spyOn(window.URL, 'createObjectURL').and.returnValue('#csv');
        revokeObjectURL = spyOn(window.URL, 'revokeObjectURL');
        downloads = [];
        spyOn(HTMLAnchorElement.prototype, 'click').and.callFake(function (this: HTMLAnchorElement) {
          downloads.push(this.download);
        });

        comp.version = {
          ...freshVocabulary().versions[0],
          number: '2.0.0',
          concepts: [
            { notation: 'A', title: 'Alpha', definition: 'The first, "best" one' },
            { notation: 'B', title: 'Beta' },
          ],
        };
      });

      afterEach(() => {
        clear();
      });

      it('should name the file after the version and the day', () => {
        comp.exportAsCsv();

        expect(downloads).toEqual(['AnalysisUnit_en_2.0.0(PUBLISHED)_2026-03-14.csv']);
      });

      it('should write a CSV with a byte order mark, a header and one escaped row per code', async () => {
        comp.exportAsCsv();

        const blob = createObjectURL.calls.mostRecent().args[0] as Blob;
        expect(blob.type).toBe('text/csv');
        // read as bytes, since decoding as text would swallow the byte order mark; the jsdom Blob has no arrayBuffer()
        const buffer = await new Promise<ArrayBuffer>(resolve => {
          const reader = new FileReader();
          reader.onload = () => resolve(reader.result as ArrayBuffer);
          reader.readAsArrayBuffer(blob);
        });
        const bytes = new Uint8Array(buffer);
        expect(Array.from(bytes.slice(0, 3))).toEqual([0xef, 0xbb, 0xbf]);
        expect(new TextDecoder().decode(bytes.slice(3))).toBe(
          'Code value,Code term,Code definition\r\nA,Alpha,"The first, ""best"" one"\r\nB,Beta,',
        );
      });

      it('should release the object URL afterwards', () => {
        comp.exportAsCsv();

        expect(revokeObjectURL).toHaveBeenCalledWith('#csv');
      });
    });

    describe('switching the language', () => {
      let eventManager: MockEventManager;

      beforeEach(() => {
        eventManager = TestBed.inject(EventManager) as unknown as MockEventManager;
        comp.vocabulary = freshVocabulary();
      });

      it('should select the language and number of the version and close the comparison', () => {
        comp.version = comp.vocabulary.versions[1];

        comp.switchLang();

        expect(comp.vocabulary.selectedLang).toBe('de');
        expect(comp.vocabulary.selectedVersion).toBe('2.0.1');
        expect(eventManager.broadcastSpy).toHaveBeenCalledWith({ name: 'closeComparison', content: true });
      });

      it('should do nothing without a version', () => {
        comp.version = null;

        comp.switchLang();

        expect(comp.vocabulary.selectedLang).toBe('en');
        expect(eventManager.broadcastSpy).not.toHaveBeenCalled();
      });

      it('should do nothing for a version without a language', () => {
        comp.version = { ...createNewVersion(9), number: '9.9.9' };

        comp.switchLang();

        expect(comp.vocabulary.selectedVersion).toBeUndefined();
        expect(eventManager.broadcastSpy).not.toHaveBeenCalled();
      });
    });

    describe('naming the missing translation version', () => {
      beforeEach(() => {
        const vocabulary = freshVocabulary();
        vocabulary.versions[0].number = '3.0.0';
        vocabulary.versions[0].versionHistories = [
          { id: 3, version: '3.0', date: '', note: '', changes: '' },
          { id: 2, version: '2.1', date: '', note: '', changes: '' },
          { id: 1, version: '2.0', date: '', note: '', changes: '' },
          { id: 0, version: '1.0', date: '', note: '', changes: '' },
        ];
        comp.vocabulary = vocabulary;
      });

      it('should name the latest bundle for a version of the current source version', () => {
        expect(comp.getMissingTlVersion('3.0.0.1')).toBe('3.0.x');
      });

      it('should name the bundle after the one the version belongs to', () => {
        expect(comp.getMissingTlVersion('2.1.1')).toBe('2.0.x');
      });

      it('should name nothing for a version outside every bundle', () => {
        expect(comp.getMissingTlVersion('7.0.1')).toBe('');
      });
    });

    describe('the rich text editors', () => {
      const editors: {
        field: 'versionNotes' | 'versionChanges' | 'ddiUsage';
        onCreated: (c: EditorDetailComponent) => (q: Quill) => void;
      }[] = [
        { field: 'versionNotes', onCreated: c => q => c.onVersionNotesEditorCreated(q) },
        { field: 'versionChanges', onCreated: c => q => c.onVersionChangesEditorCreated(q) },
        { field: 'ddiUsage', onCreated: c => q => c.onDdiUsageEditorCreated(q) },
      ];

      editors.forEach(({ field, onCreated }) => {
        describe(`for ${field}`, () => {
          const render = (): ReturnType<typeof renderQuillEditor> =>
            renderQuillEditor({ control: comp.editorDetailForm.controls[field], modules: comp.quillModules, onCreated: onCreated(comp) });

          it('should come up with the full toolbar', async () => {
            const editor = await render();

            expect(toolbarControls(editor)).toEqual([
              'bold',
              'italic',
              'underline',
              'strike',
              'blockquote',
              'list:ordered',
              'list:bullet',
              'header',
              'color',
              'background',
              'link',
              'clean',
            ]);
          });

          it('should show the saved text with its formatting rather than coming up blank', async () => {
            comp.editorDetailForm.controls[field].setValue('<p>First <strong>release</strong></p>');

            const editor = await render();

            expect(editor.quill.getText()).toBe('First release\n');
            expect(editor.content.querySelector('strong')?.textContent).toBe('release');
          });

          it('should start empty when nothing has been saved', async () => {
            const editor = await render();

            expect(editor.quill.getText()).toBe('\n');
          });

          it('should write what is typed back to the form', async () => {
            const editor = await render();

            editor.type('Revised');

            expect(comp.editorDetailForm.controls[field].value).toBe('<p>Revised</p>');
          });

          it('should apply a header picked from the toolbar', async () => {
            const editor = await render();
            editor.type('Revised');

            editor.pick('ql-header', '2', 0, 0);

            expect(comp.editorDetailForm.controls[field].value).toBe('<h2>Revised</h2>');
          });
        });
      });
    });
  });
});
