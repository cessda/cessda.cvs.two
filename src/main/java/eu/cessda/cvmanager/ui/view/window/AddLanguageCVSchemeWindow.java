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
import eu.cessda.cvmanager.ui.view.DetailView;

public class AddLanguageCVSchemeWindow extends Window {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8116725336044618619L;

	private static final Logger log = LoggerFactory.getLogger(AddLanguageCVSchemeWindow.class);
	
	private final EventBus.UIEventBus eventBus;

	Binder<CVScheme> binder = new Binder<CVScheme>();

	private TextField tfTitle = new TextField("Title*");

	private TextArea description = new TextArea("Description*");
	
	private ComboBox<String> languageCb = new ComboBox<>("Language*");
	
	private String language;

	private Button storeCode = new Button("Save");

	private CVScheme cvScheme;

	//private EditorView theView;

	public AddLanguageCVSchemeWindow(EventBus.UIEventBus eventBus, RestClient client, CVScheme cS, DetailView detailView) {
		super("Add Language");
		this.cvScheme = detailView.getCvScheme();
		this.eventBus = eventBus;
		setWidth("600px");
		setHeight("500px");
		
		Set<String> allLanguages = new LinkedHashSet<>();
		allLanguages.addAll(Language.getAllEnumValue());
		
		Set<String> currentCvLanguage = cvScheme.getLanguagesByTitle();
		
		allLanguages.removeAll(currentCvLanguage);
		
		List<String> availableLanguage = Language.getFilteredEnumCapitalized( allLanguages );
		
		
		languageCb.setItems( availableLanguage );
		languageCb.setEmptySelectionAllowed( false );
		languageCb.setTextInputAllowed( false );
		if( !availableLanguage.isEmpty());
			languageCb.setValue(availableLanguage.get(0));

		setModal(true);
//		setOrginalLanguage(orignalLanguage);
//		setLanguage(language);
		setCvScheme(cvScheme);
		//this.setTheView(theView);

		FormLayout layout = new FormLayout();
		layout.addComponent(tfTitle);

		layout.addComponent(description);
		layout.addComponent(languageCb);

//		binder.setBean(getCvScheme());
//
//		binder.bind(tfTitle, concept -> getTitleByLanguage(concept),
//				(concept, value) -> setTitleByLanguage(concept, value));
//
//		binder.bind(description, concept -> getDescriptionByLanguage(concept),
//				(concept, value) -> setDescriptionByLanguage(concept, value));

		layout.addComponent(storeCode);

		storeCode.addClickListener(event -> {
			Language lang =  Language.valueOf( languageCb.getValue().toString().toUpperCase());
			// CVConcept cv = binder.getBean();
			LanguageLabel titleLangLabel = new LanguageLabel();
			titleLangLabel.setLanguage(lang.toString());
			titleLangLabel.setContent( tfTitle.getValue());
			getCvScheme().addTitle( titleLangLabel);
			
			LanguageLabel descLangLabel = new LanguageLabel();
			descLangLabel.setLanguage(lang.toString());
			descLangLabel.setContent( description.getValue());
			getCvScheme().addTitle( descLangLabel );

			getCvScheme().save();
			DDIStore ddiStore = client.saveElement(getCvScheme().ddiStore, "Peter", "minor edit");
			eventBus.publish( this, ddiStore);
			close();
			UI.getCurrent().getNavigator().navigateTo( DetailView.VIEW_NAME + "/" + getCvScheme().getContainerId());
		});

		setContent(layout);
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
