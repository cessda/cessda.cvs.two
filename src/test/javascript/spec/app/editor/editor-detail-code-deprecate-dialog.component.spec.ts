import { ComponentFixture, fakeAsync, inject, TestBed } from "@angular/core/testing";
import { NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";
import { EditorDetailCodeDeprecateDialogComponent } from "app/editor";
import { EditorService } from "app/editor/editor.service";
import { JhiEventManager } from "ng-jhipster";
import { of } from "rxjs";
import { MockActiveModal } from "../../helpers/mock-active-modal.service";
import { MockEventManager } from "../../helpers/mock-event-manager.service";
import { CvsTestModule } from "../../test.module";

describe('Component Tests', () => {
    describe('Editor Detail Code Deprecation Dialog Component', () => {
        let comp: EditorDetailCodeDeprecateDialogComponent;
        let fixture: ComponentFixture<EditorDetailCodeDeprecateDialogComponent>;
        let service: EditorService;
        let mockEventManager: MockEventManager;
        let mockActiveModal: MockActiveModal;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CvsTestModule],
                declarations: [EditorDetailCodeDeprecateDialogComponent]
            })
                .overrideTemplate(EditorDetailCodeDeprecateDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(EditorDetailCodeDeprecateDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(EditorService);
            mockEventManager = TestBed.get(JhiEventManager);
            mockActiveModal = TestBed.get(NgbActiveModal);
        });

        describe('submit deprecation', () => {
            it('Should call code deprecation service on submit', inject(
                [],
                fakeAsync(() => {
                    // GIVEN
                    spyOn(service, 'deprecateCode').and.returnValue(of({}));
                    // WHEN
                    comp.save();
                    // THEN
                    expect(service.deprecateCode).not.toHaveBeenCalled();
                    expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                })
            ));
        });
    });
});