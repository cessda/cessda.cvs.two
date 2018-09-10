package eu.cessda.cvmanager.ui.view.window;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.security.UserDetails;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.data.Binder;
import com.vaadin.data.provider.Query;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyChangeService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.view.DetailView;
import eu.cessda.cvmanager.ui.view.DetailsView;
import eu.cessda.cvmanager.ui.view.EditorView;

public class DialogTranslateCodeWindowNew extends MWindow {

	private static final Logger log = LoggerFactory.getLogger(DialogTranslateCodeWindowNew.class);
	private static final long serialVersionUID = 8118228014482059473L;
	
	private final EventBus.UIEventBus eventBus;
	private final I18N i18n;
	private final StardatDDIService stardatDDIService;
	private final VocabularyService vocabularyService;
	private final CodeService codeService;
	private final VersionService versionService;
	private final ConceptService conceptService;
	private final VocabularyChangeService vocabularyChangeService;

	Binder<ConceptDTO> binder = new Binder<ConceptDTO>();
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
	private ComboBox<Language> languageCb = new ComboBox<>("Language");

	private Button storeCode = new Button("Save");
	
	private Language language;
	private Language sourceLang;

	private CVScheme cvScheme;
	private CVConcept cvConcept;
	
	private VocabularyDTO vocabulary;
	private VersionDTO version;
	private CodeDTO code;
	private ConceptDTO concept;
	
	
	MHorizontalLayout sourceRowA = new MHorizontalLayout();
	MHorizontalLayout sourceRowB = new MHorizontalLayout();

	public DialogTranslateCodeWindowNew(EventBus.UIEventBus eventBus, StardatDDIService stardatDDIService, 
			VocabularyService vocabularyService, VersionService versionService, CodeService codeService, ConceptService conceptService,
			CVScheme cvScheme, CVConcept conceptCode, Language sLanguage, Language sourceLang, VocabularyDTO vocabularyDTO, 
			VersionDTO versionDTO, CodeDTO codeDTO, ConceptDTO conceptDTO, VocabularyChangeService vocabularyChangeService, I18N i18n, Locale locale) {
		super( "Add Code Translation");
		this.i18n = i18n;
		this.cvScheme = cvScheme;
		this.cvConcept = conceptCode;
		this.language = sLanguage;
		this.stardatDDIService = stardatDDIService;
		this.vocabularyService = vocabularyService;
		this.versionService = versionService;
		this.codeService = codeService;
		this.conceptService = conceptService;
		this.vocabularyChangeService = vocabularyChangeService;
		
		this.eventBus = eventBus;
		this.vocabulary = vocabularyDTO;
		this.version = versionDTO;
		this.code = codeDTO;
		this.concept = conceptDTO;
		this.sourceLang = sourceLang;
				
		init();
	}

	private void init() {
		codeText.setValue( code.getTitleByLanguage( sourceLang ) );
		codeText.withFullWidth()
			.withReadOnly( true );
		
		sourceTitle.withFullWidth();
		sourceTitle.setValue( code.getTitleByLanguage( sourceLang ) );
		sourceDescription.setSizeFull();
		
		sourceDescription.setValue( code.getDefinitionByLanguage( sourceLang ) );
		sourceDescription.setReadOnly( true );
		sourceTitle.setReadOnly( true );
		
		preferedLabel.setWidth("100%");
		description.setSizeFull();
		
		sourceLanguage
			.withReadOnly( true)
			.setValue( sourceLang.name() );
		
		lTitle.withStyleName( "required" );
		lLanguage.withStyleName( "required" );
		lDescription.withStyleName( "required" );
		
		languageCb.setItems( this.language );
		languageCb.setValue( this.language  );
		
		languageCb.setEmptySelectionAllowed( false );
		languageCb.setTextInputAllowed( false );
		languageCb.setReadOnly( true );
		languageCb.setItemCaptionGenerator( new ItemCaptionGenerator<Language>() {
			private static final long serialVersionUID = 1L;
			@Override
			public String apply(Language item) {
				return item.name() + " (" +item.getLanguage() + ")";
			}
		});

		preferedLabel.setCaption( "Code (" + language.toString() + ")*");
		description.setCaption( "Definition ("+ language.toString() +")*");
		
		if( language.equals( sourceLang )) {
			sourceRowA.setVisible( false );
			sourceRowB.setVisible( false );
		} else {
			sourceRowA.setVisible( true );
			sourceRowB.setVisible( true );
		}

		binder.setBean( concept );

		storeCode.addClickListener(event -> {
			saveCode();
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
					.withHeight("100%")
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
					.withHeight("100%")
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

	private void saveCode() {
		if(!isInputValid())
			return;
		// generate Uri by inserting notation after cvScheme notation
		String uri = version.getUri();
		int lastIndex = uri.lastIndexOf("/");
		if( lastIndex == -1) {
			uri = ConfigurationService.DEFAULT_CV_LINK;
			if(!uri.endsWith("/"))
				uri += "/";
			uri += version.getNotation();
		} else {
			uri = uri.substring(0, lastIndex);
		}
			
		// store the code and index
		code.setTitleDefinition(preferedLabel.getValue(), description.getValue(), language);
		codeService.save(code);
		
		concept.setUri( uri + "#" + code.getNotation() + "/" + language.toString());
		concept.setNotation( code.getNotation() );
		concept.setTitle( preferedLabel.getValue() );
		concept.setDefinition( description.getValue() );
		
		// store concept
		if( !concept.isPersisted()) {
			concept.setCodeId( code.getId());
			concept.setVersionId( version.getId());
			// save vocabulary, version
			concept = conceptService.save(concept);
			version.addConcept(concept);
			version = versionService.save(version);
		} else {
			// save vocabulary, version
			concept = conceptService.save(concept);
		}
		
		// save change log
		VocabularyChangeDTO changeDTO = new VocabularyChangeDTO();
		changeDTO.setVocabularyId( vocabulary.getId());
		changeDTO.setVersionId( version.getId()); 
		changeDTO.setChangeType( "Code added" );
		changeDTO.setDescription( concept.getNotation());
		changeDTO.setDate( LocalDateTime.now() );
		UserDetails loggedUser = SecurityUtils.getLoggedUser();
		changeDTO.setUserId( loggedUser.getId() );
		changeDTO.setUserName( loggedUser.getFirstName() + " " + loggedUser.getLastName());
		vocabularyChangeService.save(changeDTO);

		
		// indexing editor
		vocabularyService.index(vocabulary);
		
		eventBus.publish(EventScope.UI, DetailsView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_CREATED, null) );
		
		this.close();
	}
	
	private boolean isInputValid() {
		concept.setTitle(preferedLabel.getValue());
		concept.setDefinition(description.getValue());
		
		binder
			.forField( preferedLabel )
			.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 250 ))	
			.bind( concept -> concept.getTitle(),
				(concept, value) -> concept.setTitle(value));

		binder
			.forField( description )
			.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 10000 ))	
			.bind(  concept -> concept.getDefinition(),
				(concept, value) -> concept.setDefinition(value));
		
		binder.validate();
		return binder.isValid();
	}

}
