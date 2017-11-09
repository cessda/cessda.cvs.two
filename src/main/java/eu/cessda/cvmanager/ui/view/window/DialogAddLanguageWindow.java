package eu.cessda.cvmanager.ui.view.window;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVEditor;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.LanguageLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.fields.MTextField;

import com.vaadin.data.Binder;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import eu.cessda.cvmanager.Language;
import eu.cessda.cvmanager.service.CvManagerService;
import eu.cessda.cvmanager.ui.view.DetailView;

public class DialogAddLanguageWindow extends Window {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8116725336044618619L;

	private static final Logger log = LoggerFactory.getLogger(DialogAddLanguageWindow.class);
	
	private final EventBus.UIEventBus eventBus;

	Binder<CVScheme> binder = new Binder<CVScheme>();
	
	private MTextField sourceTitle = new MTextField("Title (source)");
	private TextArea sourceDescription = new TextArea("Definition (source)");

	private MTextField tfTitle = new MTextField("Title*");

	private TextArea description = new TextArea("Definition*");
	
	private ComboBox<String> languageCb = new ComboBox<>("Language*");
	
	private String language;

	private Button storeCode = new Button("Save");

	private CVScheme cvScheme;
	
	private String selectedLanguage;

	//private EditorView theView;

	public DialogAddLanguageWindow(EventBus.UIEventBus eventBus, CvManagerService cvManagerService,  CVScheme cS) {
		super("Add Language");
		this.eventBus = eventBus;
		this.cvScheme = cS;
		setWidth("600px");
		setHeight("500px");
		
		Set<String> allLanguages = new LinkedHashSet<>();
		allLanguages.addAll(Language.getAllEnumValue());
		
		Set<String> currentCvLanguage = cvScheme.getLanguagesByTitle();
		
		allLanguages.removeAll(currentCvLanguage);
		
		List<String> availableLanguage = Language.getFilteredEnumCapitalized( allLanguages );
		
		sourceTitle
			.withFullWidth()
			.setReadOnly( true );
		sourceTitle.setValue( cvScheme.getTitleByLanguage( "en" ) );
		sourceDescription.setSizeFull();
		sourceDescription.setReadOnly( true );
		sourceDescription.setValue( cvScheme.getDescriptionByLanguage( "en" ) );
		
		tfTitle.withFullWidth();
		description.setSizeFull();
		
		languageCb.setItems( availableLanguage );
		languageCb.setEmptySelectionAllowed( false );
		languageCb.setTextInputAllowed( false );
		if( !availableLanguage.isEmpty()) {
			languageCb.setValue(availableLanguage.get(0));
			selectedLanguage = Language.valueOf( availableLanguage.get(0).toUpperCase()).getLanguage();
			tfTitle.setCaption( "Title (" + selectedLanguage + ")*");
			description.setCaption( "Definition ("+ selectedLanguage +")*");
		} else {
			tfTitle.setVisible( false );
			description.setVisible( false );
			storeCode.setVisible( false );
		}
		languageCb.addValueChangeListener( e -> {
			selectedLanguage = Language.valueOf( e.getValue().toString().toUpperCase()).getLanguage();
			tfTitle.setCaption( "Title (" + selectedLanguage + ")*");
			description.setCaption( "Definition ("+ selectedLanguage +")*");
		});

		setModal(true);
//		setOrginalLanguage(orignalLanguage);
//		setLanguage(language);
		setCvScheme(cvScheme);
		//this.setTheView(theView);

		FormLayout layout = new FormLayout();
		layout.addComponents( sourceTitle, sourceDescription);
		layout.addComponent(tfTitle);

		layout.addComponent(description);
		layout.addComponent(languageCb);

		binder.setBean(getCvScheme());

		binder.bind(tfTitle, concept -> getTitleByLanguage(concept),
				(concept, value) -> setTitleByLanguage(concept, value));

		binder.bind(description, concept -> getDescriptionByLanguage(concept),
				(concept, value) -> setDescriptionByLanguage(concept, value));

		layout.addComponent(storeCode);

		storeCode.addClickListener(event -> {
			// CVConcept cv = binder.getBean();
//			LanguageLabel titleLangLabel = new LanguageLabel();
//			titleLangLabel.setLanguage(selectedLanguage);
//			titleLangLabel.setContent( tfTitle.getValue());
//			getCvScheme().addTitle( titleLangLabel);
//			
//			LanguageLabel descLangLabel = new LanguageLabel();
//			descLangLabel.setLanguage(selectedLanguage);
//			descLangLabel.setContent( description.getValue());
//			getCvScheme().addDescription( descLangLabel );
//
//			getCvScheme().save();
//			//DDIStore ddiStore = client.saveElement(getCvScheme().ddiStore, "Peter", "minor edit");
//			//eventBus.publish( this, ddiStore);
			
			getCvScheme().save();
			DDIStore ddiStore = cvManagerService.saveElement(getCvScheme().ddiStore, "Peter", "minor edit");
			eventBus.publish( this, ddiStore);
			close();
			UI.getCurrent().getNavigator().navigateTo( DetailView.VIEW_NAME + "/" + getCvScheme().getContainerId());
		});

		setContent(layout);
	}
	
	private CVScheme setTitleByLanguage(CVScheme concept, String value) {

		concept.setTitleByLanguage(getSelectedLanguage(), value);
		return concept;
	}

	private String getTitleByLanguage(CVScheme concept) {

		return concept.getTitleByLanguage(getSelectedLanguage());

	}
	
	private Object setDescriptionByLanguage(CVScheme concept, String value) {
		concept.setDescriptionByLanguage(getSelectedLanguage(), value);
		return null;
	}

	private String getDescriptionByLanguage(CVScheme concept) {

		return concept.getDescriptionByLanguage(getSelectedLanguage());

	}
	
	public String getSelectedLanguage() {
		return selectedLanguage;
	}

	public CVScheme getCvScheme() {
		return cvScheme;
	}

	public void setCvScheme(CVScheme cvScheme) {
		this.cvScheme = cvScheme;
	}

//	public EditorView getTheView() {
//		return theView;
//	}
//
//	public void setTheView(EditorView theView) {
//		this.theView = theView;
//	}

}
