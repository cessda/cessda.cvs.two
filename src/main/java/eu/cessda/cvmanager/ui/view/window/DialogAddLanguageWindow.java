package eu.cessda.cvmanager.ui.view.window;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.security.UserDetails;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.data.Binder;
import com.vaadin.data.provider.Query;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;

import eu.cessda.cvmanager.domain.Vocabulary;
import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.repository.search.VocabularySearchRepository;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyChangeService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.mapper.VocabularyMapper;
import eu.cessda.cvmanager.ui.view.CvManagerView;
import eu.cessda.cvmanager.ui.view.DetailView;

public class DialogAddLanguageWindow extends MWindow {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8944364070898136792L;
	private static final Logger log = LoggerFactory.getLogger(DialogAddLanguageWindow.class);
	
	private final UIEventBus eventBus;
	private AgencyDTO agency;
	private VocabularyDTO vocabulary;
	private final VocabularySearchRepository vocabularySearchRepository;
	private final VersionService versionService;
	private final VocabularyChangeService vocabularyChangeService;
	private VersionDTO version;
	
	private final StardatDDIService stardatDDIService;
	private final VocabularyService vocabularyService;

	private Binder<CVScheme> binder = new Binder<CVScheme>();
	private MVerticalLayout layout = new MVerticalLayout();
	
	private MLabel lTitle = new MLabel( "Title" );
	private MLabel lDescription = new MLabel( "Definition" );
	private MLabel lLanguage = new MLabel( "Language" );
	private MLabel lSourceTitle = new MLabel( "Title (source)" );
	private MLabel lSourceDescription = new MLabel( "Definition (source)" );
	private MLabel lSourceLanguage = new MLabel( "Language (source)" );
	
	private MTextField sourceTitle = new MTextField("Title (source)");
	private MTextField sourceLanguage = new MTextField("Language (source)");
	private TextArea sourceDescription = new TextArea("Definition (source)");
	private MTextField tfTitle = new MTextField("Title*");
	private TextArea description = new TextArea("Definition*");
	private ComboBox<Language> languageCb = new ComboBox<>("Language*");
	private Button storeCode = new Button("Save");
	
	private MCssLayout changeBox = new MCssLayout();
	private MLabel lChange = new MLabel( "Change notes:" );
	private MLabel lChangeType = new MLabel( "Type*" );
	private MLabel lChangeDesc = new MLabel( "Description" );
	private ComboBox<String> changeCb = new ComboBox<>();
	private MTextField changeDesc = new MTextField();
	
	private CVScheme cvScheme;
	private Language language;

	
	//private EditorView theView;

	public DialogAddLanguageWindow(StardatDDIService stardatDDIService,  CVScheme cvScheme,
			 VocabularyDTO vocabularyDTO, VersionDTO versionDTO, AgencyDTO agencyDTO, 
			 VocabularyService vocabularyService, VersionService versionService,
			 VocabularySearchRepository vocabularySearchRepository, UIEventBus eventBus, VocabularyChangeService vocabularyChangeService) {
		super("Add Language");
		this.cvScheme = cvScheme;
		this.stardatDDIService = stardatDDIService;
		this.agency = agencyDTO;
		this.vocabulary = vocabularyDTO;
		this.version = versionDTO;
		this.vocabularyService = vocabularyService;
		this.versionService = versionService;
		this.vocabularySearchRepository = vocabularySearchRepository;
		this.eventBus = eventBus;
		this.vocabularyChangeService = vocabularyChangeService;
		
		// assign values if updated and not new TL
		if( version.isPersisted()) {
			
			tfTitle.setValue( version.getTitle());
			description.setValue( version.getDefinition());
			
			Language selectedLanguage = Language.getEnumByName( version.getLanguage());
			languageCb.setItems( selectedLanguage );
			languageCb.setValue(selectedLanguage);
			languageCb.setReadOnly( true );
		}
		else {
			// TODO: use list language from vocabulary if possible
			List<Language> availableLanguages = new ArrayList<>();
			Set<Language> userLanguages = new HashSet<>();
			if( SecurityUtils.isCurrentUserAgencyAdmin( agency)) {
				userLanguages.addAll( Arrays.asList( Language.values() ) );
			}
			else {
				SecurityUtils.getCurrentUserLanguageTlByAgency( agency ).ifPresent( languages -> {
					userLanguages.addAll(languages);
				});
			}
			
			if( vocabulary != null ) {
				availableLanguages = Language.getFilteredLanguage(userLanguages, vocabulary.getLanguages());
			} else {
				availableLanguages = Language.getFilteredLanguage(userLanguages, cvScheme.getLanguagesByTitle());
			}
			
			Language sourceLang = Language.getEnumByName( vocabulary.getSourceLanguage() );
			// remove with sourceLanguage option if exist
			availableLanguages.remove( sourceLang );
			
			languageCb.setItems( availableLanguages );
		}
		
		lTitle.withStyleName( "required" );
		lLanguage.withStyleName( "required" );
		lDescription.withStyleName( "required" );
		
		
		
		sourceTitle
			.withFullWidth()
			.withReadOnly( true)
			.setValue( cvScheme.getTitleByLanguage( "en" ) );
		sourceLanguage
			.withReadOnly( true)
			.setValue( "English" );
		sourceDescription.setReadOnly( true );
		sourceDescription.setSizeFull();
		sourceDescription.setValue( cvScheme.getDescriptionByLanguage( "en" ) );
		
		tfTitle.withFullWidth();
		description.setSizeFull();
		
		
		languageCb.setValue( languageCb.getDataProvider().fetch( new Query<>()).findFirst().orElse( null ));
		if( languageCb.getValue() != null )
			language = languageCb.getValue();

		languageCb.setEmptySelectionAllowed( false );
		languageCb.setTextInputAllowed( false );
		languageCb.setItemCaptionGenerator( new ItemCaptionGenerator<Language>() {
			private static final long serialVersionUID = 1L;
			@Override
			public String apply(Language item) {
				return item.name() + " (" +item.getLanguage() + ")";
			}
		});

		languageCb.addValueChangeListener( e -> {
			language = e.getValue();
			lTitle.setValue( "Title (" + language + ")");
			lDescription.setValue( "Definition ("+ language +")");
		});

		setCvScheme(cvScheme);;

		binder.setBean(getCvScheme());

		storeCode.addClickListener(event -> {
			saveCV();
		});

		Button cancelButton = new Button("Cancel", e -> this.close());
		
		lChange
			.withStyleName("change-header");
		changeCb.setWidth("100%");
		changeCb.setItems( Arrays.asList( VocabularyChangeDTO.cvChangeTypes));
		changeCb.setTextInputAllowed(false);
	//	changeCb.setEmptySelectionAllowed(false);
		changeDesc.setWidth("100%");
		changeBox
			.withStyleName("change-block")
			.add( 
				lChange,
				new MHorizontalLayout()
					.withFullWidth()
					.add(
						lChangeType, changeCb
					).withExpand( lChangeType, 0.15f).withExpand( changeCb, 0.85f),
				new MHorizontalLayout()
					.withFullWidth()
					.add(
						lChangeDesc, changeDesc
					).withExpand( lChangeDesc, 0.15f).withExpand( changeDesc, 0.85f)
			);
	
		
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
								lSourceTitle, sourceTitle
						).withExpand(lSourceTitle, 0.31f).withExpand(sourceTitle, 0.69f),
						new MHorizontalLayout().add(
								lSourceLanguage, sourceLanguage
						)
				),
				new MHorizontalLayout()
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
								lTitle, tfTitle
						).withExpand(lTitle, 0.31f).withExpand(tfTitle, 0.69f),
						new MHorizontalLayout().add(
								lLanguage, languageCb
						)
				),
				new MHorizontalLayout()
					.withFullWidth()
					.withHeight("100%")
					.add(
						lDescription, description
					).withExpand( lDescription, 0.15f).withExpand( description, 0.85f)
			);

		if( version.isPersisted() ) {
			layout
			.add(
				changeBox,
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
			.withExpand(layout.getComponent(0), 0.05f)
			.withExpand(layout.getComponent(1), 0.35f)
			.withExpand(layout.getComponent(2), 0.05f)
			.withExpand(layout.getComponent(3), 0.35f)
			.withExpand(layout.getComponent(4), 0.1f)
			.withExpand(layout.getComponent(5), 0.1f)
			.withAlign(layout.getComponent(5), Alignment.BOTTOM_RIGHT);
		} else {
			layout
				.add(
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
		}
		
		this
			.withHeight("800px")
			.withWidth("1024px")
			.withModal( true )
			.withContent(layout);
	}

	private void saveCV() {
		if(!isInputValid())
			return;
		
		if( version.isPersisted()) {
			if( changeCb.getValue() == null ) {
				Notification.show("Please select the change type!");
				return;
			}
		}
		
		getCvScheme().save();
		DDIStore ddiStore = stardatDDIService.saveElement(getCvScheme().ddiStore, SecurityUtils.getCurrentUserLogin().get(), "Add CV translation");
		
		version.setTitle( tfTitle.getValue() );
		version.setDefinition( description.getValue());
		
		// store new version
		if( !version.isPersisted()) {
			version.setUri( vocabulary.getUri() );
			version.setNotation( vocabulary.getNotation());
			version.setNumber("0.0.1");
			version.setStatus( Status.DRAFT.toString() );
			version.setItemType( ItemType.TL.toString());
			version.setLanguage( language.name().toLowerCase() );
			version.setPreviousVersion( 0L );
			version.setInitialVersion( 0L );
			
			version = versionService.save(version);
			
			vocabulary.addVersions(version);
			vocabulary.addVers(version);
		} else {
			VocabularyChangeDTO changeDTO = new VocabularyChangeDTO();
			changeDTO.setVocabularyId( vocabulary.getId());
			changeDTO.setVersionId( version.getId()); 
			changeDTO.setChangeType( changeCb.getValue() );
			changeDTO.setDescription( changeDesc.getValue() == null ? "": changeDesc.getValue() );
			changeDTO.setDate( LocalDateTime.now() );
			UserDetails loggedUser = SecurityUtils.getLoggedUser();
			changeDTO.setUserId( loggedUser.getId() );
			changeDTO.setUserName( loggedUser.getFirstName() + " " + loggedUser.getLastName());
			
			vocabularyChangeService.save(changeDTO);
		}
		// store the variable and index
		vocabulary.setTitleDefinition(tfTitle.getValue(), description.getValue(), language);
		
		// save to database
		vocabulary = vocabularyService.save(vocabulary);
		
		// index
		vocabularyService.index(vocabulary);
		
		// use eventbus to update detail view
		eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVSCHEME_UPDATED, null) );
		
		close();
//		UI.getCurrent().getNavigator().navigateTo( DetailView.VIEW_NAME + "/" + getCvScheme().getContainerId());
	}
	
	private boolean isInputValid() {
		getCvScheme().setTitleByLanguage(language.toString(), tfTitle.getValue());
		getCvScheme().setDescriptionByLanguage(language.toString(), description.getValue());
		
		binder
		.forField( tfTitle)
		.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 250 ))	
		.bind(concept -> getTitleByLanguage(concept),
			(concept, value) -> setTitleByLanguage(concept, value));

		binder
		.forField( description)
		.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 10000 ))
		.bind(concept -> getDescriptionByLanguage(concept),
			(concept, value) -> setDescriptionByLanguage(concept, value));
		
		binder.validate();
		return binder.isValid();
	}
	
	private CVScheme setTitleByLanguage(CVScheme concept, String value) {

		concept.setTitleByLanguage(language.toString(), value);
		return concept;
	}

	private String getTitleByLanguage(CVScheme concept) {
		if( language == null )
			return "";

		return concept.getTitleByLanguage(language.toString());

	}
	
	private Object setDescriptionByLanguage(CVScheme concept, String value) {
		concept.setDescriptionByLanguage(language.toString(), value);
		return null;
	}

	private String getDescriptionByLanguage(CVScheme concept) {
		if( language == null )
			return "";
		
		return concept.getDescriptionByLanguage(language.toString());

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
