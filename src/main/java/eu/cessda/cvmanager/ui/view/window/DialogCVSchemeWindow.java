package eu.cessda.cvmanager.ui.view.window;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVEditor;
import org.gesis.stardat.entity.CVScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.data.Binder;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;

import eu.cessda.cvmanager.Language;
import eu.cessda.cvmanager.service.CvManagerService;
import eu.cessda.cvmanager.ui.view.DetailView;

public class DialogCVSchemeWindow extends MWindow {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8116725336044618619L;
	private static final Logger log = LoggerFactory.getLogger(DialogCVSchemeWindow.class);
	
	private static final CVEditor[] cvEditors = new CVEditor[2];
	private final EventBus.UIEventBus eventBus;
	
	private MLabel lAgency = new MLabel( "Agency" );
	private MLabel lTitle = new MLabel( "Title" );
	private MLabel lDescription = new MLabel( "Definition" );
	private MLabel lLanguage = new MLabel( "Language (source)" );

	private MVerticalLayout layout = new MVerticalLayout();
	private MTextField tfTitle = new MTextField("Title*");
	private TextArea description = new TextArea("Description*");
	private ComboBox<CVEditor> editorCb = new ComboBox<>("Agency*");
	private ComboBox<String> languageCb = new ComboBox<>("Language*");
	private Button storeCode = new Button("Save");
	
	private Binder<CVScheme> binder = new Binder<CVScheme>();
	private String orginalLanguage;
	private String language;
	private CVScheme cvScheme;

	//private EditorView theView;

	public DialogCVSchemeWindow(EventBus.UIEventBus eventBus, CvManagerService cvManagerService, CVScheme cvScheme, String orignalLanguage, String language) {
		super("Add CVScheme");
		
		this.eventBus = eventBus;
				
		lAgency.withStyleName( "required" );
		lTitle.withStyleName( "required" );
		lDescription.withStyleName( "required" );
		
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

		setOrginalLanguage(orignalLanguage);
		setLanguage(language);
		setCvScheme(cvScheme);	

		binder.setBean(getCvScheme());

		binder.bind(tfTitle, concept -> getTitleByLanguage(concept),
				(concept, value) -> setTitleByLanguage(concept, value));

		binder.bind(description, concept -> getDescriptionByLanguage(concept),
				(concept, value) -> setDescriptionByLanguage(concept, value));
		
		tfTitle
			.withFullWidth()
			.withValue( "" );
		
		description.setSizeFull();
		description.setValue( "" );

		storeCode.addClickListener(event -> {
			log.trace(getCvScheme().getTitleByLanguage(getLanguage()));
			getCvScheme().addEditor( editorCb.getValue());
			getCvScheme().save();
			DDIStore ddiStore = cvManagerService.saveElement(getCvScheme().ddiStore, "Peter", "minor edit");
			eventBus.publish( this, ddiStore);
			close();
			UI.getCurrent().getNavigator().navigateTo( DetailView.VIEW_NAME + "/" + getCvScheme().getContainerId());
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
								lAgency, editorCb
						).withExpand(lAgency, 0.31f).withExpand(editorCb, 0.69f),
						new MHorizontalLayout().add(
								lLanguage, languageCb
						)
				),
				new MHorizontalLayout()
					.withFullWidth()
					.add(
						lTitle, tfTitle
					).withExpand( lTitle, 0.15f).withExpand( tfTitle, 0.85f),
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
