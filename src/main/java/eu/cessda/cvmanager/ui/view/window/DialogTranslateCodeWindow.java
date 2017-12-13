package eu.cessda.cvmanager.ui.view.window;

import java.util.List;
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
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.data.Binder;
import com.vaadin.ui.Alignment;
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

public class DialogTranslateCodeWindow extends MWindow {

	private static final Logger log = LoggerFactory.getLogger(DialogTranslateCodeWindow.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 8118228014482059473L;
	private final EventBus.UIEventBus eventBus;

	Binder<CVConcept> binder = new Binder<CVConcept>();
	private MVerticalLayout layout = new MVerticalLayout();
	
	private MLabel lTitle = new MLabel( "Descriptive term" );
	private MLabel lDescription = new MLabel( "Definition" );
	private MLabel lLanguage = new MLabel( "Language" );
	private MLabel lSourceTitle = new MLabel( "Descriptive term (source)" );
	private MLabel lSourceDescription = new MLabel( "Definition (source)" );
	private MLabel lSourceLanguage = new MLabel( "Language (source)" );
	
	private MTextField sourceTitle = new MTextField("Descriptive term (source)");
	private MTextField sourceLanguage = new MTextField("Language (source)");
	private TextArea sourceDescription = new TextArea("Definition en (source)");
	
	private MLabel lCode = new MLabel( "Descriptive terms" );
	private MTextField codeText = new MTextField("Code");

	private TextField preferedLabel = new TextField("Code");
	private TextArea description = new TextArea("Definition");
	private ComboBox<String> languageCb = new ComboBox<>("Language");

	private String selectedLanguage;

	private Button storeCode = new Button("Save");

	private CVScheme cvScheme;
	private CVConcept code;
	
	MHorizontalLayout sourceRowA = new MHorizontalLayout();
	MHorizontalLayout sourceRowB = new MHorizontalLayout();

	public DialogTranslateCodeWindow(EventBus.UIEventBus eventBus, CvManagerService cvManagerService, CVScheme cvScheme, CVConcept cvConcept, String sLanguage) {
		super( "Add Code Translation");
		this.cvScheme = cvScheme;
		this.code = cvConcept;
		
		this.eventBus = eventBus;
		
		codeText.setValue( code.getPrefLabelByLanguage( "en" ));
		codeText.withFullWidth()
			.withReadOnly( true );
		
		sourceTitle.withFullWidth();
		sourceTitle.setValue( code.getPrefLabelByLanguage( "en" ) );
		sourceDescription.setSizeFull();
		
		sourceDescription.setValue( code.getDescriptionByLanguage( "en" ) );
		sourceDescription.setReadOnly( true );
		sourceTitle.setReadOnly( true );
		
		preferedLabel.setWidth("100%");
		description.setSizeFull();
		
		sourceLanguage
			.withReadOnly( true)
			.setValue( "English" );
		
		lTitle.withStyleName( "required" );
		lLanguage.withStyleName( "required" );
		lDescription.withStyleName( "required" );
		
		Set<String> currentCvLanguage = cvScheme.getLanguagesByTitle();
		
		List<String> languages = Language.getFilteredEnumCapitalized( currentCvLanguage );
		languages.remove( "English" );
		
		
		
		languageCb.setItems( languages );
		languageCb.setEmptySelectionAllowed( false );
		languageCb.setTextInputAllowed( false );
		languageCb.setValue(  languages.get(0));
		languageCb.addValueChangeListener( e -> {
			setSelectedLanguage( Language.valueOf( e.getValue().toString().toUpperCase()).getLanguage());
			
			preferedLabel.setCaption( "Code (" + selectedLanguage + ")*");
			description.setCaption( "Definition ("+ selectedLanguage +")*");
			
			preferedLabel.setValue( code.getPrefLabelByLanguage(selectedLanguage));
			description.setValue( code.getDescriptionByLanguage(selectedLanguage) );
			
			if( e.getValue().equals( "en" )) {
				sourceRowA.setVisible( false );
				sourceRowB.setVisible( false );
			} else {
				sourceRowA.setVisible( true );
				sourceRowB.setVisible( true );
			}
		});
		
		this.selectedLanguage = Language.valueOf( languageCb.getValue().toString().toUpperCase()).getLanguage();
		preferedLabel.setCaption( "Code (" + selectedLanguage + ")*");
		description.setCaption( "Definition ("+ selectedLanguage +")*");
		
		if( selectedLanguage.equals( "en" )) {
			sourceRowA.setVisible( false );
			sourceRowB.setVisible( false );
		} else {
			sourceRowA.setVisible( true );
			sourceRowB.setVisible( true );
		}

		binder.setBean(code);

		binder.bind(preferedLabel, concept -> getPrefLabelByLanguage(concept),
				(concept, value) -> setPrefLabelByLanguage(concept, value));

		binder.bind(description, concept -> getDescriptionByLanguage(concept),
				(concept, value) -> setDescriptionByLanguage(concept, value));

		storeCode.addClickListener(event -> {
			// CVConcept cv = binder.getBean();
			log.trace(code.getPrefLabelByLanguage(selectedLanguage));
			code.save();
			DDIStore ddiStore = cvManagerService.saveElement(code.ddiStore, "Peter", "minor edit");
			
			eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_CREATED, ddiStore) );
			
			this.close();

		});

		Button cancelButton = new Button("Cancel", e -> this.close());
		
		layout
			.withHeight("98%")
			.withStyleName("dialog-content")
			.add( 
				sourceRowA
					.withFullWidth()
					.add(
						new MHorizontalLayout()
						.withFullWidth()
						.add(
								lCode, codeText
						).withExpand(lCode, 0.31f).withExpand(codeText, 0.69f),
						new MHorizontalLayout().add(
								lSourceLanguage, sourceLanguage
						)
				),
				sourceRowB
					.withFullWidth()
					.withHeight("290px")
					.add(
						lSourceDescription, sourceDescription
					).withExpand( lSourceDescription, 0.15f).withExpand( sourceDescription, 0.85f),
				new MHorizontalLayout()
					.withFullWidth()
					.add(
						new MHorizontalLayout()
						.withFullWidth()
						.add(
								lTitle, preferedLabel
						).withExpand(lTitle, 0.31f).withExpand(preferedLabel, 0.69f),
						new MHorizontalLayout().add(
								lLanguage, languageCb
						)
				),
				new MHorizontalLayout()
					.withFullWidth()
					.withHeight("290px")
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
			.withExpand(layout.getComponent(1), 0.5f)
			.withExpand(layout.getComponent(2), 0.06f)
			.withExpand(layout.getComponent(3), 0.5f)
			.withAlign(layout.getComponent(4), Alignment.BOTTOM_RIGHT);

		
		this
			.withHeight("800px")
			.withWidth("1024px")
			.withModal( true )
			.withContent(layout);
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
