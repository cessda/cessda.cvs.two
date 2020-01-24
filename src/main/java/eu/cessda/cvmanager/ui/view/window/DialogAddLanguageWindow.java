package eu.cessda.cvmanager.ui.view.window;

import com.vaadin.data.Binder;
import com.vaadin.data.provider.Query;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.*;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.manager.WorkspaceManager;
import eu.cessda.cvmanager.ui.view.EditorDetailsView;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import java.util.*;

import static eu.cessda.cvmanager.config.Constants.REQUIRED;

public class DialogAddLanguageWindow extends MWindow {

	private static final long serialVersionUID = -8944364070898136792L;
	private static final Logger log = LoggerFactory.getLogger(DialogAddLanguageWindow.class);

	private final transient WorkspaceManager workspaceManager;
	private final transient UIEventBus eventBus;
	private AgencyDTO agency;
	private VocabularyDTO vocabulary;
	private VersionDTO version;
	
	private Binder<VersionDTO> binder = new Binder<>();
	private MVerticalLayout layout = new MVerticalLayout();
	
	private MLabel lTitle = new MLabel( "CV name" );
	private MLabel lDescription = new MLabel( "CV definition" );
	private MLabel lLanguage = new MLabel( "CV language" );
	private MLabel lNotes = new MLabel( "CV notes" );
	private MLabel lSourceTitle = new MLabel( "CV name (source)" );
	private MLabel lSourceDescription = new MLabel( "CV definition (source)" );
	private MLabel lSourceLanguage = new MLabel( "CV language (source)" );
	private MLabel lSourceNotes = new MLabel( "CV notes (source)" );
	
	private MTextField sourceTitle = new MTextField("CV name (source)");
	private MTextField sourceLanguage = new MTextField("CV language (source)");
	private TextArea sourceDescription = new TextArea("CV definition (source)");
	private TextArea sourceNotes = new TextArea("CV notes (source)");
	private MTextField tfTitle = new MTextField("CV name*");
	private TextArea description = new TextArea("CV definition*");
	private TextArea notes = new TextArea("CV notes");
	private ComboBox<Language> languageCb = new ComboBox<>("CV language*");
	private Button storeCode = new Button("Save");
	
	private MCssLayout changeBox = new MCssLayout();
	private MLabel lChange = new MLabel( "Change notes:" );
	private MLabel lChangeType = new MLabel( "Type*" );
	private MLabel lChangeDesc = new MLabel( "Description" );
	private ComboBox<String> changeCb = new ComboBox<>();
	private MTextField changeDesc = new MTextField();
	
	private MCssLayout translatorAgencyBox = new MCssLayout();
	private MLabel lTranslatorAgency = new MLabel( "Translator Agency" );
	private MLabel lTranslatorAgencyLink = new MLabel( "Translator Agency Link" );
	private MTextField translatorAgency = new MTextField();
	private MTextField translatorAgencyLink = new MTextField();
	
	private Language language;
	
	public DialogAddLanguageWindow( WorkspaceManager workspaceManager, AgencyDTO agencyDTO, VocabularyDTO vocabularyDTO,
									VersionDTO versionDTO, UIEventBus eventBus) {
		super(versionDTO.isPersisted()?"Edit CV Translation (" + versionDTO.getLanguage() + ")":"Add CV Translation");

		if( vocabularyDTO == null )
			throw new IllegalArgumentException( "Vocabulary can not be null" );

		this.workspaceManager = workspaceManager;
		this.agency = agencyDTO;
		this.vocabulary = vocabularyDTO;
		this.version = versionDTO;
		this.eventBus = eventBus;

		init();
	}

	private void init() {
		if( version.isPersisted()) {
			assignExistingTargetVersion();
		}
		else {
			assignNewTargetVersion();
		}

		lTitle.withStyleName( REQUIRED );
		lLanguage.withStyleName( REQUIRED );
		lDescription.withStyleName( REQUIRED );

		// get version SL
		VersionDTO slVersion = null;

		Optional<VersionDTO> latestSlVersion = vocabulary.getLatestSlVersion( true );
		if(latestSlVersion.isPresent()) {
			slVersion = latestSlVersion.get();
			// copy license and ddi usage
			version.setDdiUsage( slVersion.getDdiUsage());
			version.setLicense( slVersion.getLicense());
			version.setLicenseId( slVersion.getLicenseId() );
		} else {
			throw new IllegalArgumentException("SL version for notation {} can not be null" + vocabulary.getNotation());
		}

		sourceTitle
			.withFullWidth()
			.withReadOnly( true)
			.setValue( slVersion.getTitle() );
		sourceLanguage
			.withReadOnly( true)
			.setValue( Language.getByIso( slVersion.getLanguage() ).getFormatted() );
		sourceDescription.setReadOnly( true );
		sourceDescription.setSizeFull();
		sourceDescription.setValue( slVersion.getDefinition() );

		sourceNotes.setReadOnly( true );
		sourceNotes.setSizeFull();
		sourceNotes.setValue( slVersion.getNotes() == null ? "": slVersion.getNotes());

		tfTitle.withFullWidth();
		description.setSizeFull();
		notes.setSizeFull();

		languageCb.setValue( languageCb.getDataProvider().fetch( new Query<>()).findFirst().orElse( null ));
		if( languageCb.getValue() != null )
			language = languageCb.getValue();

		languageCb.setEmptySelectionAllowed( false );
		languageCb.setTextInputAllowed( false );
		languageCb.setItemCaptionGenerator( new ItemCaptionGenerator<Language>() {
			private static final long serialVersionUID = 1L;
			@Override
			public String apply(Language item) {
				return item.getFormatted();
			}
		});

		languageCb.addValueChangeListener( e -> {
			language = e.getValue();
			lTitle.setValue( "Title (" + language + ")");
			lDescription.setValue( "Definition ("+ language +")");
		});

		binder.setBean( version );

		storeCode.addClickListener(event ->  saveTargetCV() );

		Button cancelButton = new Button("Cancel", e -> this.close());

		lChange
			.withStyleName("change-header");
		changeCb.setWidth("100%");
		changeCb.setItems(Arrays.asList(VocabularyChangeDTO.getCvChangeTypes()));
		changeCb.setTextInputAllowed(false);

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

		translatorAgencyBox
			.withFullWidth()
			.add(
				new MHorizontalLayout()
					.withFullWidth()
					.add(
						lTranslatorAgency, translatorAgency.withFullWidth()
					).withExpand( lTranslatorAgency, 0.15f).withExpand( translatorAgency, 0.85f),
				new MHorizontalLayout()
					.withFullWidth()
					.add(
						lTranslatorAgencyLink, translatorAgencyLink.withFullWidth()
					).withExpand( lTranslatorAgencyLink, 0.15f).withExpand( translatorAgencyLink, 0.85f)
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
						.withHeight("100%")
						.add(
								lSourceNotes, sourceNotes
						).withExpand( lSourceNotes, 0.15f).withExpand( sourceNotes, 0.85f),
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
					).withExpand( lDescription, 0.15f).withExpand( description, 0.85f),
				new MHorizontalLayout()
						.withFullWidth()
						.withHeight("100%")
						.add(
								lNotes, notes
						).withExpand( lNotes, 0.15f).withExpand( notes, 0.85f)
			);

		if( version.isPersisted() ) {
			if( version.isInitialVersion() ) {
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
				.withExpand(layout.getComponent(0), 0.05f)
				.withExpand(layout.getComponent(1), 0.15f)
				.withExpand(layout.getComponent(2), 0.1f)
				.withExpand(layout.getComponent(3), 0.05f)
				.withExpand(layout.getComponent(4), 0.3f)
				.withExpand(layout.getComponent(5), 0.2f)
				.withExpand(layout.getComponent(6), 0.15f)
				.withAlign(layout.getComponent(6), Alignment.BOTTOM_RIGHT);
			} else {
				changeDesc
					.withWidth("100%")
					.withValue( version.getTitle());
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
				.withExpand(layout.getComponent(1), 0.15f)
				.withExpand(layout.getComponent(2), 0.1f)
				.withExpand(layout.getComponent(3), 0.05f)
				.withExpand(layout.getComponent(4), 0.3f)
				.withExpand(layout.getComponent(5), 0.2f)
				.withExpand(layout.getComponent(6), 0.05f)
				.withExpand(layout.getComponent(7), 0.05f)
				.withAlign(layout.getComponent(7), Alignment.BOTTOM_RIGHT);
			}

		} else {
			layout
				.add(
					translatorAgencyBox,
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
				.withExpand(layout.getComponent(1), 0.15f)
				.withExpand(layout.getComponent(2), 0.1f)
				.withExpand(layout.getComponent(3), 0.05f)
				.withExpand(layout.getComponent(4), 0.35f)
				.withExpand(layout.getComponent(5), 0.17f)
				.withExpand(layout.getComponent(6), 0.05f)
				.withExpand(layout.getComponent(7), 0.1f)
				.withAlign(layout.getComponent(7), Alignment.BOTTOM_RIGHT);
		}

		this
			.withHeight("100%")
			.withWidth("1024px")
			.withModal( true )
			.withContent(layout);
	}

	private void assignNewTargetVersion() {
		List<Language> availableLanguages = null;
		Set<Language> userLanguages = new HashSet<>();
		if( SecurityUtils.isCurrentUserAgencyAdmin( agency)) {
			userLanguages.addAll( Arrays.asList( Language.values() ) );
		}
		else {
			SecurityUtils.getCurrentUserLanguageTlByAgency( agency ).ifPresent(userLanguages::addAll);
		}

		availableLanguages = Language.getFilteredLanguage(userLanguages, vocabulary.getLanguages());

		Language sourceLang = Language.ENGLISH;
		if( vocabulary.getSourceLanguage() != null)
			sourceLang = Language.getByIso( vocabulary.getSourceLanguage() );
		// remove with sourceLanguage option if exist
		availableLanguages.remove( sourceLang );

		languageCb.setItems( availableLanguages );
		languageCb.addValueChangeListener( e -> {
			Optional<VersionDTO> latestTlVersion = VersionDTO.getLatestVersion( vocabulary.getVersions(), e.getValue().toString(), null);
			if(latestTlVersion.isPresent()) {
				tfTitle.setValue( latestTlVersion.get().getTitle());
				description.setValue( latestTlVersion.get().getDefinition());
				notes.setValue( latestTlVersion.get().getNotes() == null ? "":latestTlVersion.get().getNotes());
				translatorAgency.setValue( latestTlVersion.get().getTranslateAgency());
				translatorAgencyLink.setValue( latestTlVersion.get().getTranslateAgencyLink());
			}else {
				tfTitle.setValue( "" );
				description.setValue( "" );
				notes.setValue( "" );
				translatorAgency.setValue( "" );
				translatorAgencyLink.setValue( "" );
			}
		});
	}

	private void assignExistingTargetVersion() {
		tfTitle.setValue( version.getTitle());
		description.setValue( version.getDefinition());
		notes.setValue( version.getNotes() == null ? "":version.getNotes());
		Language selectedLanguage = Language.getByIso( version.getLanguage());
		languageCb.setItems( selectedLanguage );
		languageCb.setValue(selectedLanguage);
		languageCb.setReadOnly( true );
	}

	private void saveTargetCV() {
		if(!isInputValid())
			return;
		
		if( !version.isInitialVersion() && version.isPersisted() && changeCb.getValue() == null ) {
			Notification.show("Please select the change type!");
			return;
		}
		log.info( "Preparint to add new CV TL with notation {}", version.getNotation());

		// save TL Vocabulary
		version.setTitleAndDefinition( tfTitle.getValue(), description.getValue() );
		version.setNotes( notes.getValue());
		workspaceManager.saveTargetCV(agency, language, vocabulary, version, translatorAgency.getValue(), translatorAgencyLink.getValue());

		// save log if not initial version
		if( !version.isInitialVersion())
			workspaceManager.storeChangeLog(vocabulary, version, changeCb.getValue(), changeDesc.getValue() == null ? "": changeDesc.getValue());

		eventBus.publish(EventScope.UI, EditorDetailsView.VIEW_NAME, this, new CvManagerEvent.Event( CvManagerEvent.EventType.CVSCHEME_UPDATED, null) );
		close();
		UI.getCurrent().getNavigator().navigateTo( EditorDetailsView.VIEW_NAME + "/" + vocabulary.getNotation() + "?lang=" + language.toString());
	}
	
	private boolean isInputValid() {
		version.setTitle( tfTitle.getValue() );
		version.setDefinition( description.getValue() );
		version.setNotes( notes.getValue() );
		
		binder
			.forField( tfTitle)
			.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 250 ))	
			.bind(VersionDTO::getTitle,
					VersionDTO::setTitle);

		binder
			.forField( description)
			.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 10000 ))
			.bind(VersionDTO::getDefinition,
					VersionDTO::setDefinition);

		binder
				.forField( notes)
				.bind(VersionDTO::getNotes,
						VersionDTO::setNotes);
		
		binder.validate();
		return binder.isValid();
	}
}
