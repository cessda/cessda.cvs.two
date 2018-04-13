package eu.cessda.cvmanager.ui.view.window;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVEditor;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.security.UserDetails;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.data.Binder;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.provider.Query;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import eu.cessda.cvmanager.domain.Vocabulary;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.view.DetailView;

public class DialogCVSchemeWindow extends MWindow {

	private static final long serialVersionUID = -8116725336044618619L;
	private static final Logger log = LoggerFactory.getLogger(DialogCVSchemeWindow.class);
	
	private final EventBus.UIEventBus eventBus;
	private final I18N i18n;
	private final AgencyService agencyService;
	private final VocabularyService vocabularyService;
	private final StardatDDIService stardatDDIService;
	private Locale locale = UI.getCurrent().getLocale();
	
	private MLabel lAgency = new MLabel( "Agency" );
	private MLabel lCode = new MLabel( "Code" );
	private MLabel lTitle = new MLabel( "Title" );
	private MLabel lDescription = new MLabel( "Definition" );
	private MLabel lLanguage = new MLabel( "Language (source)" );

	private MVerticalLayout layout = new MVerticalLayout();
	private MTextField tfCode = new MTextField("Code");
	private MTextField tfTitle = new MTextField("Title*");
	private TextArea description = new TextArea("Description*");
	private ComboBox<AgencyDTO> editorCb = new ComboBox<>("Agency*");
	private ComboBox<Language> languageCb = new ComboBox<>("Language*");
	private Button storeCode = new Button("Save");
	
	private Binder<CVScheme> binder = new Binder<CVScheme>();
	private Language language;
	private AgencyDTO agency;
//	private String orginalLanguage;
//	private String language;
	private CVScheme cvScheme;
	
	private UserDetails userDetails;

	//private EditorView theView;

	public DialogCVSchemeWindow(EventBus.UIEventBus eventBus, StardatDDIService stardatDDIService, AgencyService agencyService, VocabularyService vocabularyService, CVScheme cvScheme, I18N i18n) {
		super("Add CVScheme");
		this.agencyService = agencyService;
		this.eventBus = eventBus;
		this.stardatDDIService = stardatDDIService;
		this.vocabularyService = vocabularyService;
		this.i18n = i18n;
		
		userDetails = SecurityUtils.getLoggedUser();
				
		lAgency.withStyleName( "required" );
		lCode.withStyleName( "required" );
		lTitle.withStyleName( "required" );
		lDescription.withStyleName( "required" );
		
		if( SecurityUtils.isCurrentUserSystemAdmin()) {
			editorCb.setItems( agencyService.findAll());
		}
		else {
			SecurityUtils.getCurrentUserAgencies().ifPresent( agencies -> {
				editorCb.setItems( agencies );
			});
		}
		
		editorCb.setItemCaptionGenerator(AgencyDTO::getName);
		editorCb.setEmptySelectionAllowed( false );
		editorCb.setTextInputAllowed( false );
		editorCb.addValueChangeListener( e -> {
			if( e.getValue() != null ) {
				languageCb.setReadOnly( false );
				if( SecurityUtils.isCurrentUserAgencyAdmin( e.getValue() )) {
					languageCb.setItems( Language.values() );
				}
				else {
					SecurityUtils.getCurrentUserLanguageSlByAgency( e.getValue() ).ifPresent( languages -> {
						languageCb.setItems( languages );
					});
				}
				languageCb.setValue( languageCb.getDataProvider().fetch( new Query<>()).findFirst().orElse( null ));
				if( languageCb.getValue() != null ) {
					languageCb.setReadOnly( false );
					language = languageCb.getValue();
				}
				else
					languageCb.setReadOnly( true );
				agency = e.getValue();
			} else {
				languageCb.setReadOnly( true );
				languageCb.setValue( null );
				agency = null;
			}
		});
		editorCb.setValue( editorCb.getDataProvider().fetch( new Query<>()).findFirst().orElse( null ));
		
		
		languageCb.setItemCaptionGenerator( new ItemCaptionGenerator<Language>() {
			private static final long serialVersionUID = 1L;
			@Override
			public String apply(Language item) {
				return item.name() + " (" +item.getLanguage() + ")";
			}
		});
		
		languageCb.setEmptySelectionAllowed( false );
		languageCb.setTextInputAllowed( false );
		
		languageCb.addValueChangeListener( e -> {
			if( e.getValue() != null )
				language = e.getValue();
			else
				language = null;
		});
		
		

//		setOrginalLanguage(orignalLanguage);
//		setLanguage(language);
		setCvScheme(cvScheme);	

		binder.setBean(getCvScheme());
		
		tfCode.withFullWidth();
		tfCode.addValueChangeListener( e -> {
			//Only allow letter
			((TextField)e.getComponent()).setValue( e.getValue().replaceAll("[^A-Za-z]", ""));
		});
		
		tfTitle.withFullWidth();
		description.setSizeFull();

		storeCode.addClickListener(event -> {
			saveCV();
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
						lCode, tfCode
					).withExpand( lCode, 0.15f).withExpand( tfCode, 0.85f),
				new MHorizontalLayout()
					.withFullWidth()
					.add(
						lTitle, tfTitle
					).withExpand( lTitle, 0.15f).withExpand( tfTitle, 0.85f),
				new MHorizontalLayout()
				.withFullWidth()
				.withHeight("400px")
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
			.withExpand(layout.getComponent(0), 0.07f)
			.withExpand(layout.getComponent(1), 0.07f)
			.withExpand(layout.getComponent(2), 0.07f)
			.withExpand(layout.getComponent(3), 0.5f)
			.withExpand(layout.getComponent(4), 0.3f)
			.withAlign(layout.getComponent(4), Alignment.BOTTOM_RIGHT);

		
		this
			.withHeight("650px")
			.withWidth("1024px")
			.withModal( true )
			.withContent(layout);
	}

	private void saveCV() {
		if(!isInputValid())
			return;
		//agency
		List<CVEditor> editorSet = getCvScheme().getOwnerAgency();
		if(editorSet ==  null)
			editorSet = new ArrayList<>();
		else
			editorSet.clear();
		CVEditor cvEditor = new CVEditor();
		cvEditor.setName( agency.getName());
		cvEditor.setLogoPath( agency.getLogopath());
		
		editorSet.add( cvEditor );
		// save on flatDB
		getCvScheme().setOwnerAgency((ArrayList<CVEditor>) editorSet);
		getCvScheme().save();
		
		DDIStore ddiStore = stardatDDIService.saveElement(getCvScheme().ddiStore, SecurityUtils.getCurrentUserLogin().get(), "Add new CV");
		
		// save on database
		VocabularyDTO vocabulary = new VocabularyDTO();
		vocabulary.setVersion("1.0");
		vocabulary.setUri( ddiStore.getElementId());
		vocabulary.setNotation( tfCode.getValue() );
		vocabulary.setTitleDefinition(tfTitle.getValue(), description.getValue(), language);
		vocabulary.setAgencyId( agency.getId());
		vocabulary.setAgencyName( agency.getName());
		vocabulary.setSourceLanguage( language.name().toLowerCase());
		vocabularyService.save(vocabulary);
		
		
		eventBus.publish( this, ddiStore);
		close();
		UI.getCurrent().getNavigator().navigateTo( DetailView.VIEW_NAME + "/" + getCvScheme().getContainerId());
	}

	private boolean isInputValid() {
		getCvScheme().setCode(tfCode.getValue());
		getCvScheme().setTitleByLanguage(language.toString(), tfTitle.getValue());
		getCvScheme().setDescriptionByLanguage(language.toString(), description.getValue());
		
		binder
		.forField( tfCode)
		.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 250 ))	
		.bind( concept -> concept.getCode(),
				(concept, value) -> concept.setCode(value));
		
		binder
		.forField( tfTitle)
		.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 250 ))	
		.bind(concept -> getTitleByLanguage(concept),
			(concept, value) -> setTitleByLanguage(concept, value));

		binder
		.forField( description)
		.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 5000 ))
		.bind(concept -> getDescriptionByLanguage(concept),
			(concept, value) -> setDescriptionByLanguage(concept, value));
		
		binder.validate();
		return binder.isValid();
	}

	private CVScheme setTitleByLanguage(CVScheme concept, String value) {
		concept.setTitleByLanguage( language.toString(), value);
		return concept;
	}

	private String getTitleByLanguage(CVScheme concept) {

		return concept.getTitleByLanguage( language.toString());

	}

	private CVScheme setDescriptionByLanguage(CVScheme concept, String value) {
		concept.setDescriptionByLanguage(language.toString(), value);
		return concept;
	}

	private String getDescriptionByLanguage(CVScheme concept) {

		return concept.getDescriptionByLanguage(language.toString());
	}

	public CVScheme getCvScheme() {
		return cvScheme;
	}

	public void setCvScheme(CVScheme cvScheme) {
		this.cvScheme = cvScheme;
	}
}
