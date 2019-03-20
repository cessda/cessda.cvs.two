package eu.cessda.cvmanager.ui.view.window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.manager.WorkspaceManager;
import eu.cessda.cvmanager.ui.view.EditorDetailsView;

public class DialogAddLanguageWindow extends MWindow {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8944364070898136792L;
	private static final Logger log = LoggerFactory.getLogger(DialogAddLanguageWindow.class);
	
	private final UIEventBus eventBus;
	private AgencyDTO agency;
	private VocabularyDTO vocabulary;
	private VersionDTO version;
	
	private Binder<VersionDTO> binder = new Binder<VersionDTO>();
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
	
	private MCssLayout translatorAgencyBox = new MCssLayout();
	private MLabel lTranslatorAgency = new MLabel( "Translator Agency" );
	private MLabel lTranslatorAgencyLink = new MLabel( "Translator Agency Link" );
	private MTextField translatorAgency = new MTextField();
	private MTextField translatorAgencyLink = new MTextField();
	
	private Language language;

	
	//private EditorView theView;

	public DialogAddLanguageWindow(
			AgencyDTO agencyDTO, VocabularyDTO vocabularyDTO, VersionDTO versionDTO, UIEventBus eventBus) {
		super("Add Language");
		this.agency = agencyDTO;
		this.vocabulary = vocabularyDTO;
		this.version = versionDTO;
		this.eventBus = eventBus;
		
		// assign values if updated and not new TL
		if( version.isPersisted()) {
			
			tfTitle.setValue( version.getTitle());
			description.setValue( version.getDefinition());
			
			Language selectedLanguage = Language.valueOfEnum( version.getLanguage());
			languageCb.setItems( selectedLanguage );
			languageCb.setValue(selectedLanguage);
			languageCb.setReadOnly( true );
		}
		else {
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
			} 
//			else {
//				availableLanguages = Language.getFilteredLanguage(userLanguages, cvScheme.getLanguagesByTitle());
//			}
			
			Language sourceLang = Language.valueOfEnum( vocabulary.getSourceLanguage() );
			// remove with sourceLanguage option if exist
			availableLanguages.remove( sourceLang );
			
			languageCb.setItems( availableLanguages );
		}
		
		lTitle.withStyleName( "required" );
		lLanguage.withStyleName( "required" );
		lDescription.withStyleName( "required" );
		
		
		// get version SL
		VersionDTO slVersion = null;
		
		Optional<VersionDTO> latestSlVersion = vocabulary.getLatestSlVersion( true );
		if(latestSlVersion.isPresent()) {
			slVersion = latestSlVersion.get();
			// copy license and ddi usage
			version.setDdiUsage( slVersion.getDdiUsage());
			version.setLicense( slVersion.getLicense());
			version.setLicenseId( slVersion.getLicenseId() );
		}
		
		sourceTitle
			.withFullWidth()
			.withReadOnly( true)
			.setValue( slVersion.getTitle() );
		sourceLanguage
			.withReadOnly( true)
			.setValue( "English" );
		sourceDescription.setReadOnly( true );
		sourceDescription.setSizeFull();
		sourceDescription.setValue( slVersion.getDefinition() );
		
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

		binder.setBean( version );

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
				.withExpand(layout.getComponent(1), 0.35f)
				.withExpand(layout.getComponent(2), 0.05f)
				.withExpand(layout.getComponent(3), 0.35f)
				.withExpand(layout.getComponent(4), 0.2f)
				.withAlign(layout.getComponent(4), Alignment.BOTTOM_RIGHT);
			} else {
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
				.withExpand(layout.getComponent(1), 0.4f)
				.withExpand(layout.getComponent(2), 0.05f)
				.withExpand(layout.getComponent(3), 0.4f)
				.withExpand(layout.getComponent(4), 0.1f)
				.withExpand(layout.getComponent(5), 0.1f)
				.withAlign(layout.getComponent(5), Alignment.BOTTOM_RIGHT);
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
		
		
		// save TL Vocabulary
		WorkspaceManager.saveTargetCV(agency, language, vocabulary, version, 
				tfTitle.getValue(), description.getValue(), translatorAgency.getValue(), translatorAgencyLink.getValue());

		// save log if not initial version
		if( !version.isInitialVersion())
			WorkspaceManager.storeChangeLog(vocabulary, version, changeCb.getValue(), changeDesc.getValue() == null ? "": changeDesc.getValue());
		
		close();
		UI.getCurrent().getNavigator().navigateTo( EditorDetailsView.VIEW_NAME + "/" + vocabulary.getNotation() + "?lang=" + language.toString());
	}
	
	private boolean isInputValid() {
		version.setTitle( tfTitle.getValue() );
		version.setDefinition( description.getValue() );
		
		binder
			.forField( tfTitle)
			.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 250 ))	
			.bind(v -> v.getTitle(),
				(v, value) -> v.setTitle( value));

		binder
			.forField( description)
			.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 10000 ))
			.bind(v -> v.getDefinition(),
				(v, value) -> v.setDefinition( value ));
		
		binder.validate();
		return binder.isValid();
	}
}
