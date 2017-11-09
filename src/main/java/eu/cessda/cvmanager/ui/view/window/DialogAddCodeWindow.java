package eu.cessda.cvmanager.ui.view.window;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVConcept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import com.vaadin.data.Binder;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import eu.cessda.cvmanager.Language;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.CvManagerService;
import eu.cessda.cvmanager.ui.view.DetailView;
import eu.cessda.cvmanager.ui.view.EditorView;

public class DialogAddCodeWindow extends Window {

	private static final Logger log = LoggerFactory.getLogger(DialogAddCodeWindow.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 8118228014482059473L;
	private final EventBus.UIEventBus eventBus;

	Binder<CVConcept> binder = new Binder<CVConcept>();

	private TextField preferedLabel = new TextField("Code*");

	private TextArea description = new TextArea("Definition*");
	
	private ComboBox<String> languageCb = new ComboBox<>("Language (source)");

	private String orginalLanguage;

	private String language;

	private Button storeCode = new Button("Save");

	private CVConcept theCode;


	public DialogAddCodeWindow(EventBus.UIEventBus eventBus, CvManagerService cvManagerService, CVConcept code, String orignalLanguage, String language) {
		super( "Add Code");
		
		this.eventBus = eventBus;
		setWidth("600px");
		setHeight("500px");
		
		languageCb.setItems( Language.getAllEnumCapitalized());
		languageCb.setEmptySelectionAllowed( false );
		languageCb.setTextInputAllowed( false );
		languageCb.setValue("English");
		languageCb.setReadOnly( true );
		
		preferedLabel.setSizeFull();
		description.setSizeFull();

		setModal(true);
		setOrginalLanguage(orignalLanguage);
		setLanguage(language);
		setTheCode(code);

		FormLayout layout = new FormLayout();

		layout.addComponent(preferedLabel);

		layout.addComponent(description);
		layout.addComponent(languageCb);

		binder.setBean(getTheCode());

		binder.bind(preferedLabel, concept -> getPrefLabelByLanguage(concept),
				(concept, value) -> setPrefLabelByLanguage(concept, value));

		binder.bind(description, concept -> getDescriptionByLanguage(concept),
				(concept, value) -> setDescriptionByLanguage(concept, value));

		layout.addComponent( new MHorizontalLayout().add(
				storeCode,
				new Button("Cancel", e -> this.close())
			));

		storeCode.addClickListener(event -> {
			// CVConcept cv = binder.getBean();
			log.trace(getTheCode().getPrefLabelByLanguage(getLanguage()));
			getTheCode().save();
			DDIStore ddiStore = cvManagerService.saveElement(getTheCode().ddiStore, "Peter", "minor edit");
			
			eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_CREATED, ddiStore) );
			
			this.close();

		});

		setContent(layout);
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

		return concept.getDescriptionByLanguage(getLanguage());

	}

	public String getOrginalLanguage() {

		return orginalLanguage;
	}

	public void setOrginalLanguage(String orginalLanguage) {

		this.orginalLanguage = orginalLanguage;
	}

	public String getLanguage() {

		return language;
	}

	public void setLanguage(String language) {

		this.language = language;
	}

	public CVConcept getTheCode() {
		return theCode;
	}

	public void setTheCode(CVConcept theCode) {
		this.theCode = theCode;
	}


}
