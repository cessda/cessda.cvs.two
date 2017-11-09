package eu.cessda.cvmanager.ui.view.window;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVEditor;
import org.gesis.stardat.entity.CVScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.v7.fields.MTextArea;

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

public class DialogCVSchemeWindow extends Window {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8116725336044618619L;

	private static final Logger log = LoggerFactory.getLogger(DialogCVSchemeWindow.class);
	
	private static final CVEditor[] cvEditors = new CVEditor[2];
	private final EventBus.UIEventBus eventBus;

	Binder<CVScheme> binder = new Binder<CVScheme>();

	private MTextField tfTitle = new MTextField("Title*");

	private TextArea description = new TextArea("Description*");

	private ComboBox<CVEditor> editorCb = new ComboBox<>("Agency*");
	
	private ComboBox<String> languageCb = new ComboBox<>("Language*");
	
	private String orginalLanguage;

	private String language;

	private Button storeCode = new Button("Save");

	private CVScheme cvScheme;

	//private EditorView theView;

	public DialogCVSchemeWindow(EventBus.UIEventBus eventBus, CvManagerService cvManagerService, CVScheme cvScheme, String orignalLanguage, String language) {
		super("Add CVScheme");
		
		this.eventBus = eventBus;
		setWidth("600px");
		setHeight("500px");
		
		tfTitle.withFullWidth();
		
		description.setSizeFull();
		
		cvEditors[0] = new CVEditor("DDI", "DDI");
		cvEditors[0].setLogoPath("img/ddi-logo-r.png");
		
		cvEditors[1] = new CVEditor("CESSDA", "CESSDA");
		cvEditors[1].setLogoPath("img/cessda.png");
		
		editorCb.setItems(cvEditors);
		editorCb.setItemCaptionGenerator(CVEditor::getName);
		editorCb.setEmptySelectionAllowed( false );
		editorCb.setTextInputAllowed( false );
		editorCb.setValue(cvEditors[0]);
		
		languageCb.setItems( Language.getAllEnumCapitalized());
		languageCb.setEmptySelectionAllowed( false );
		languageCb.setTextInputAllowed( false );
		languageCb.setValue("English");
		languageCb.setReadOnly( true );

		setModal(true);
		setOrginalLanguage(orignalLanguage);
		setLanguage(language);
		setCvScheme(cvScheme);
		//this.setTheView(theView);

		FormLayout layout = new FormLayout();
		layout.addComponent( editorCb  );
		layout.addComponent(tfTitle);

		layout.addComponent(description);
		layout.addComponent(languageCb);

		binder.setBean(getCvScheme());

		binder.bind(tfTitle, concept -> getTitleByLanguage(concept),
				(concept, value) -> setTitleByLanguage(concept, value));

		binder.bind(description, concept -> getDescriptionByLanguage(concept),
				(concept, value) -> setDescriptionByLanguage(concept, value));

		layout.addComponent( new MHorizontalLayout().add(
				storeCode,
				new Button("Cancel", e -> this.close())
			));

		storeCode.addClickListener(event -> {
			// CVConcept cv = binder.getBean();
			log.trace(getCvScheme().getTitleByLanguage(getLanguage()));
			getCvScheme().addEditor( editorCb.getValue());
			getCvScheme().save();
			DDIStore ddiStore = cvManagerService.saveElement(getCvScheme().ddiStore, "Peter", "minor edit");
			eventBus.publish( this, ddiStore);
			close();
			UI.getCurrent().getNavigator().navigateTo( DetailView.VIEW_NAME + "/" + getCvScheme().getContainerId());
		});

		setContent(layout);
	}

	private CVScheme setTitleByLanguage(CVScheme concept, String value) {

		concept.setTitleByLanguage(getOrginalLanguage(), value);
		return concept;
	}

	private String getTitleByLanguage(CVScheme concept) {

		return concept.getTitleByLanguage(getOrginalLanguage());

	}

	private Object setDescriptionByLanguage(CVScheme concept, String value) {
		concept.setDescriptionByLanguage(getOrginalLanguage(), value);
		return null;
	}

	private String getDescriptionByLanguage(CVScheme concept) {

		return concept.getDescriptionByLanguage(getOrginalLanguage());
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
