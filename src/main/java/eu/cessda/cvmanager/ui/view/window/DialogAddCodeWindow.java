package eu.cessda.cvmanager.ui.view.window;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.data.Binder;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import eu.cessda.cvmanager.Language;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.CvManagerService;
import eu.cessda.cvmanager.ui.view.DetailView;

public class DialogAddCodeWindow extends MWindow {

	private static final long serialVersionUID = -2960064213533383226L;
	private static final Logger log = LoggerFactory.getLogger(DialogAddCodeWindow.class);

	private final EventBus.UIEventBus eventBus;

	Binder<CVConcept> binder = new Binder<CVConcept>();
	
	private MLabel lNotation = new MLabel( "Code" );
	private MLabel lTitle = new MLabel( "Descriptive term" );
	private MLabel lDescription = new MLabel( "Definition" );
	private MLabel lLanguage = new MLabel( "Language (source)" );

	private MVerticalLayout layout = new MVerticalLayout();
	private TextField notation = new TextField("Code");
	private TextField preferedLabel = new TextField("Descriptive term");
	private TextArea description = new TextArea("Definition*");
	private ComboBox<String> languageCb = new ComboBox<>("Language (source)");
	private String orginalLanguage;

	private Button storeCode = new Button("Save");
	private CVConcept theCode;
	private CVConcept parentCode;
	private CVScheme cvScheme;

	public DialogAddCodeWindow(EventBus.UIEventBus eventBus, CvManagerService cvManagerService, CVScheme cvSch, CVConcept newCode, CVConcept parent, String orignalLanguage) {
		super( parent == null ? "Add Code (Source Language)":"Add Child Code of \"" + parent.getNotation() + "\"");
		
		this.eventBus = eventBus;
		this.cvScheme = cvSch;
		this.parentCode = parent;
		
		setWidth("700px");
		setHeight("500px");
		
		languageCb.setItems( Language.getAllEnumCapitalized());
		languageCb.setEmptySelectionAllowed( false );
		languageCb.setTextInputAllowed( false );
		languageCb.setValue("English");
		languageCb.setReadOnly( true );
		
		preferedLabel.setWidth("100%");
		description.setSizeFull();
		
		lTitle.withStyleName( "required" );
		lDescription.withStyleName( "required" );

		setOrginalLanguage(orignalLanguage);
		setTheCode(newCode);

		binder.setBean(getTheCode());
		
		binder.bind(notation, concept -> concept.getNotation(),
				(concept, value) -> concept.setNotation(value));

		binder.bind(preferedLabel, concept -> getPrefLabelByLanguage(concept),
				(concept, value) -> setPrefLabelByLanguage(concept, value));

		binder.bind(description, concept -> getDescriptionByLanguage(concept),
				(concept, value) -> setDescriptionByLanguage(concept, value));

		storeCode.addClickListener(event -> {
			// CVConcept cv = binder.getBean();
			log.trace(getTheCode().getPrefLabelByLanguage(getOrginalLanguage()));
			getTheCode().save();
			DDIStore ddiStore = cvManagerService.saveElement(getTheCode().ddiStore, "User", "Add Code");
			if(parentCode == null ) // root concept
			{
				cvScheme.addOrderedMemberList(ddiStore.getElementId());
				cvScheme.save();
				DDIStore ddiStoreCv = cvManagerService.saveElement(cvScheme.ddiStore, "User", "Update Top Concept");
			}
			else // child code, add narrower data in parent
			{
				//parentCode.addOrderedNarrowerList("http://lod.gesis.org/thesoz/concept_" + ddiStore.getElementId());
				parentCode.addOrderedNarrowerList( ddiStore.getElementId());
				parentCode.save();
				cvManagerService.saveElement(parentCode.ddiStore, "User", "Add Code");
			}
			
			eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_CREATED, ddiStore) );
			this.close();
		});
		
		Button cancelButton = new Button("Cancel", e -> this.close());
		
		layout
			.withHeight("98%")
			.withStyleName("dialog-content")
			.add( 
				new MHorizontalLayout()
					.withFullWidth()
					.add(
						new MHorizontalLayout()
						.withFullWidth()
						.add(
								lNotation, notation
						).withExpand(lNotation, 0.31f).withExpand(notation, 0.69f),
						new MHorizontalLayout().add(
								lLanguage, languageCb
						)
				),
				new MHorizontalLayout()
					.withFullWidth()
					.add(
						lTitle, preferedLabel
					).withExpand(lTitle, 0.15f).withExpand( preferedLabel, 0.85f),
				new MHorizontalLayout()
				.withFullWidth()
				.withHeight("300px")
				.add(
					lDescription, description
				).withExpand( lDescription, 0.15f).withExpand( description, 0.85f),
				new MHorizontalLayout()
				.withFullWidth()
				.add( storeCode,
					cancelButton
				)
				.withExpand(storeCode, 0.8f)
				.withAlign(storeCode, Alignment.BOTTOM_RIGHT)
				.withExpand(cancelButton, 0.1f)
				.withAlign(cancelButton, Alignment.BOTTOM_RIGHT)
			)
			.withExpand(layout.getComponent(0), 0.06f)
			.withExpand(layout.getComponent(1), 0.06f)
			.withExpand(layout.getComponent(2), 0.4f)
			.withExpand(layout.getComponent(3), 0.4f)
			.withAlign(layout.getComponent(3), Alignment.BOTTOM_RIGHT);

		
		this
			.withHeight("600px")
			.withWidth("700px")
			.withModal( true )
			.withContent(layout);

	}

	private CVConcept setPrefLabelByLanguage(CVConcept concept, String value) {

		concept.setPrefLabelByLanguage(getOrginalLanguage(), value);
		return concept;
	}

	private String getPrefLabelByLanguage(CVConcept concept) {

		return concept.getPrefLabelByLanguage(getOrginalLanguage());

	}

	private Object setDescriptionByLanguage(CVConcept concept, String value) {

		System.out.println("FooBar");
		concept.setDescriptionByLanguage(getOrginalLanguage(), value);
		return null;
	}

	private String getDescriptionByLanguage(CVConcept concept) {

		return concept.getDescriptionByLanguage(getOrginalLanguage());

	}

	public String getOrginalLanguage() {

		return orginalLanguage;
	}

	public void setOrginalLanguage(String orginalLanguage) {

		this.orginalLanguage = orginalLanguage;
	}

	public CVConcept getTheCode() {
		return theCode;
	}

	public void setTheCode(CVConcept theCode) {
		this.theCode = theCode;
	}


}
