package eu.cessda.cvmanager.ui.view.window;

import java.util.Set;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.viritin.fields.MTextField;
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

public class DialogEditCodeWindow extends Window {

	private static final Logger log = LoggerFactory.getLogger(DialogEditCodeWindow.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 8118228014482059473L;
	private final EventBus.UIEventBus eventBus;

	Binder<CVConcept> binder = new Binder<CVConcept>();
	
	private MTextField sourceTitle = new MTextField("Code en (source)");
	private TextArea sourceDescription = new TextArea("Definition en (source)");

	private TextField preferedLabel = new TextField("Code*");

	private TextArea description = new TextArea("Definition*");
	
	private ComboBox<String> languageCb = new ComboBox<>("Language*");

	private String selectedLanguage;

	private Button storeCode = new Button("Save");

	private CVScheme cvScheme;
	private CVConcept code;


	public DialogEditCodeWindow(EventBus.UIEventBus eventBus, CvManagerService cvManagerService, CVScheme cvScheme, CVConcept code, String sLanguage) {
		super( "Edit Code");
		this.cvScheme = cvScheme;
		this.code = code;
		this.selectedLanguage = sLanguage;
		
		this.eventBus = eventBus;
		setWidth("600px");
		setHeight("500px");
		setModal(true);
		
		sourceTitle.withFullWidth();
		sourceTitle.setValue( code.getPrefLabelByLanguage( "en" ) );
		sourceDescription.setSizeFull();
		
		sourceDescription.setValue( code.getDescriptionByLanguage( "en" ) );
		sourceDescription.setReadOnly( true );
		sourceTitle.setReadOnly( true );
		
		preferedLabel.setSizeFull();
		description.setSizeFull();
		
		preferedLabel.setCaption( "Code (" + selectedLanguage + ")*");
		description.setCaption( "Definition ("+ selectedLanguage +")*");
		
		Set<String> currentCvLanguage = cvScheme.getLanguagesByTitle();
		
		languageCb.setItems( Language.getFilteredEnumCapitalized( currentCvLanguage ));
		languageCb.setEmptySelectionAllowed( false );
		languageCb.setTextInputAllowed( false );
		languageCb.setValue( Language.getEnumCapitalized( selectedLanguage ));
		languageCb.addValueChangeListener( e -> {
			setSelectedLanguage( Language.valueOf( e.getValue().toString().toUpperCase()).getLanguage());
			
			preferedLabel.setCaption( "Code (" + selectedLanguage + ")*");
			description.setCaption( "Definition ("+ selectedLanguage +")*");
			
			preferedLabel.setValue( code.getPrefLabelByLanguage(selectedLanguage));
			description.setValue( code.getDescriptionByLanguage(selectedLanguage) );
			
			if( e.getValue().equals( "en" )) {
				sourceTitle.setVisible( false );
				sourceDescription.setVisible( false );
			} else {
				sourceTitle.setVisible( true );
				sourceDescription.setVisible( true );
			}
		});
		
		if( selectedLanguage.equals( "en" )) {
			sourceTitle.setVisible( false );
			sourceDescription.setVisible( false );
		} else {
			sourceTitle.setVisible( true );
			sourceDescription.setVisible( true );
		}

		FormLayout layout = new FormLayout();

		layout.addComponent(sourceTitle);
		layout.addComponent(sourceDescription);
		layout.addComponent(preferedLabel);
		layout.addComponent(description);
		layout.addComponent(languageCb);

		binder.setBean(code);

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
			log.trace(code.getPrefLabelByLanguage(selectedLanguage));
			code.save();
			DDIStore ddiStore = cvManagerService.saveElement(code.ddiStore, "Peter", "minor edit");
			
			eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_CREATED, ddiStore) );
			
			this.close();

		});

		setContent(layout);
	}

	private CVConcept setPrefLabelByLanguage(CVConcept concept, String value) {

		concept.setPrefLabelByLanguage(selectedLanguage, value);
		return concept;
	}

	private String getPrefLabelByLanguage(CVConcept concept) {

		return concept.getPrefLabelByLanguage(selectedLanguage);

	}

	private Object setDescriptionByLanguage(CVConcept concept, String value) {

		System.out.println("FooBar");
		concept.setDescriptionByLanguage(selectedLanguage, value);
		return null;
	}

	private String getDescriptionByLanguage(CVConcept concept) {

		return concept.getDescriptionByLanguage(selectedLanguage);

	}

	public void setSelectedLanguage(String selectedLanguage) {
		this.selectedLanguage = selectedLanguage;
	}
	
	
}
