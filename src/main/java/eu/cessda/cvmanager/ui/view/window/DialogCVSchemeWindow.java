package eu.cessda.cvmanager.ui.view.window;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.vaadin.data.HasValue;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
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
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import eu.cessda.cvmanager.config.Constants;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.manager.WorkspaceManager;
import eu.cessda.cvmanager.ui.view.EditorDetailsView;
import eu.cessda.cvmanager.utils.ParserUtils;

import static eu.cessda.cvmanager.config.Constants.REQUIRED;
import static eu.cessda.cvmanager.config.Constants.REQUIRED_FIELD_INFO;

public class DialogCVSchemeWindow extends MWindow implements Translatable{

	private static final long serialVersionUID = -8116725336044618619L;
	private static final Logger log = LoggerFactory.getLogger(DialogCVSchemeWindow.class);

	private final transient WorkspaceManager workspaceManager;
	private final transient I18N i18n;
	private final transient UIEventBus eventBus;
	private final transient AgencyService agencyService;
	private final transient VocabularyService vocabularyService;
	private Locale locale = UI.getCurrent().getLocale();
	
	private MLabel lAgency = new MLabel( "Agency" );
	private MLabel lCode = new MLabel( "CV short name" );
	private MLabel lTitle = new MLabel( "CV name" );
	private MLabel lDescription = new MLabel( "CV definition" );
	private MLabel lLanguage = new MLabel( "Language (source)" );

	private MVerticalLayout layout = new MVerticalLayout();
	private MTextField tfCode = new MTextField("Code");
	private MTextField tfTitle = new MTextField("Title*");
	private RichTextArea description = new RichTextArea("Description*");
	private ComboBox<AgencyDTO> editorCb = new ComboBox<>("Agency*");
	private ComboBox<Language> languageCb = new ComboBox<>("Language*");
	private Button storeCode = new Button("Save");
	
	private MCssLayout changeBox = new MCssLayout();
	private MLabel lChange = new MLabel( "Change notes:" );
	private MLabel lChangeType = new MLabel( "Type*" );
	private MLabel lChangeDesc = new MLabel( "Description" );
	private ComboBox<String> changeCb = new ComboBox<>();
	private MTextField changeDesc = new MTextField();
	private MLabel notesLabel = new MLabel("Notes");
	private RichTextArea notes = new RichTextArea();
	
	private Binder<VersionDTO> binder = new Binder<>();
	private Language language;
	private AgencyDTO agency;
	private VocabularyDTO vocabulary;
	private VersionDTO version;
	
	private boolean isUpdated = false;

	private HasValue.ValueChangeListener<AgencyDTO> changeListener = e -> {
		if( e.getValue() != null ) {
			languageCb.setReadOnly( false );
			if( SecurityUtils.isCurrentUserAgencyAdmin( e.getValue() )) {
				languageCb.setItems( Language.values() );
				languageCb.setValue( Language.ENGLISH );
			}
			else {
				SecurityUtils.getCurrentUserLanguageSlByAgency( e.getValue() ).ifPresent( languages -> {
					languageCb.setItems( languages );
					if( languages.contains( Language.ENGLISH))
						languageCb.setValue( Language.ENGLISH );
					else // get the first available language
						languageCb.setValue( languageCb.getDataProvider().fetch( new Query<>()).findFirst().orElse( null ));
				});
			}

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
	};

	public DialogCVSchemeWindow(WorkspaceManager workspaceManager, I18N i18n, AgencyService agencyService,
								VocabularyDTO vocabulary, VersionDTO version, AgencyDTO agency, UIEventBus eventBus) {
		super( version.isPersisted() ?"Edit Vocabulary": "Add Vocabulary");
		this.workspaceManager = workspaceManager;
		this.agencyService = agencyService;
		this.vocabularyService = workspaceManager.getVocabularyService();
		this.vocabulary = vocabulary;
		this.version = version;
		this.agency = agency;
		this.i18n = i18n;
		this.eventBus = eventBus;
						
		init();
	}

	private void init() {
		lAgency.withStyleName(REQUIRED);
		lCode.withStyleName(REQUIRED);
		lTitle.withStyleName(REQUIRED);
		lDescription.withStyleName(REQUIRED);
		
		editorCb.setItemCaptionGenerator(AgencyDTO::getName);
		editorCb.setEmptySelectionAllowed( false );
		editorCb.setTextInputAllowed( false );
		editorCb.addValueChangeListener( changeListener );
		
		if( vocabulary.getNotes() != null )
			notes.setValue( vocabulary.getNotes());
		
		if( vocabulary.isPersisted())
			isUpdated = true;
		
		// update process
		if( isUpdated) {
			language = Language.valueOfEnum( vocabulary.getSourceLanguage() );
			// fill with value
			editorCb.setItems( agency );
			editorCb.setValue( agency );
			editorCb.setReadOnly( true );
			
			languageCb.setValue(language);
			languageCb.setReadOnly( true );
			
			tfCode.setValue( vocabulary.getNotation());
			tfCode.setReadOnly( true );
			
			tfTitle.setValue( vocabulary.getTitleByLanguage(language));
			description.setValue( vocabulary.getDefinitionByLanguage(language));
			
		} 
		else // new vocabulary
		{
			if( SecurityUtils.isCurrentUserSystemAdmin()) {
				editorCb.setItems( agencyService.findAll());
			}
			else {
				SecurityUtils.getCurrentUserAgencies().ifPresent( agencies -> editorCb.setItems( agencies ));
			}
			editorCb.setValue( editorCb.getDataProvider().fetch( new Query<>()).findFirst().orElse( null ));
		}
		
		
		
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

		binder.setBean( version );
		
		tfCode.withFullWidth();
		tfCode.addValueChangeListener( e -> ((TextField)e.getComponent()).setValue( e.getValue().replaceAll(Constants.NOTATION_REGEX, "")));
		
		tfTitle.withFullWidth();
		description.setSizeFull();
		notes.setSizeFull();

		storeCode.addClickListener(event -> saveCV());
		
		Button cancelButton = new Button("Cancel", e -> this.close());
		
		// check if current version is the initial version
		List<VersionDTO> versionsByLanguage = vocabulary.getVersionsByLanguage( vocabulary.getSourceLanguage() );
		if( versionsByLanguage.size() == 1) {
			changeBox.setVisible( false );
		}
		
		lChange
			.withStyleName("change-header");
		changeCb.setWidth("100%");
		changeCb.setItems( Arrays.asList( VocabularyChangeDTO.cvChangeTypes));
		changeCb.setTextInputAllowed(false);
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
					.withHeight("100%")
					.add(
						lDescription, description
					).withExpand( lDescription, 0.15f).withExpand( description, 0.85f),
				new MHorizontalLayout()
					.withFullWidth()
					.withHeight("100%")
					.add(
						notesLabel, notes
					).withExpand( notesLabel, 0.15f).withExpand( notes, 0.85f)
				
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
			.withExpand(layout.getComponent(0), 0.07f)
			.withExpand(layout.getComponent(1), 0.07f)
			.withExpand(layout.getComponent(2), 0.07f)
			.withExpand(layout.getComponent(3), 0.4f)
			.withExpand(layout.getComponent(4), 0.3f)
			.withExpand(layout.getComponent(5), 0.2f)
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
				.withExpand(layout.getComponent(0), 0.07f)
				.withExpand(layout.getComponent(1), 0.07f)
				.withExpand(layout.getComponent(2), 0.07f)
				.withExpand(layout.getComponent(3), 0.5f)
				.withExpand(layout.getComponent(4), 0.3f)
				.withAlign(layout.getComponent(4), Alignment.BOTTOM_RIGHT);
		}
		
		this
			.withHeight("800px")
			.withWidth("1024px")
			.withModal( true )
			.withContent(layout);
		updateMessageStrings(locale);
	}

	private void saveCV() {
		if(!isInputValid())
			return;
		log.info( "Create anew CV with notation {}", tfCode.getValue());
		// Store new CV
		version.setNotation( tfCode.getValue() );
		version.setTitleAndDefinition( tfTitle.getValue().trim(), ParserUtils.toXHTML( description.getValue()) );

		workspaceManager.saveSourceCV(agency, language, vocabulary, version, ParserUtils.toXHTML( notes.getValue()));
		
		if( isUpdated && !version.isInitialVersion()) {
			// store log 
			workspaceManager.storeChangeLog(vocabulary, version, changeCb.getValue(), changeDesc.getValue() == null ? "": changeDesc.getValue());
		} 
		
		// use eventbus to update detail view
		if( isUpdated )
			eventBus.publish(EventScope.UI, EditorDetailsView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVSCHEME_UPDATED, null) );
		
		close();
		UI.getCurrent().getNavigator().navigateTo( EditorDetailsView.VIEW_NAME + "/" + vocabulary.getNotation());
	}

	private boolean isInputValid() {
		
		version.setNotation( tfCode.getValue() );
		version.setTitle( tfTitle.getValue() );
		version.setDefinition( description.getValue() );
				
		// if new item check for duplication
		if( !isUpdated) {
			binder
			.forField( tfCode)
			.withValidator( new StringLengthValidator(REQUIRED_FIELD_INFO, 2, 250 ))
			.withValidator(p -> !vocabularyService.existsByNotation( p ), "code is already exist")
			.bind(VersionDTO::getNotation, VersionDTO::setNotation);
		}
		
		binder
			.forField( tfTitle)
			.withValidator( new StringLengthValidator(REQUIRED_FIELD_INFO, 2, 250 ))
			.bind(VersionDTO::getTitle, VersionDTO::setTitle);

		binder
			.forField( description )
			.withValidator( new StringLengthValidator(REQUIRED_FIELD_INFO, 2, 10000 ))
			.bind(VersionDTO::getDefinition, VersionDTO::setDefinition);
		
		binder.validate();
		return binder.isValid();
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		lAgency.withValue( i18n.get("dialog.cv.add.agency") );
		lCode.withValue( i18n.get("dialog.cv.add.code") );
		lTitle.withValue( i18n.get("dialog.cv.add.title") );
		lDescription.withValue( i18n.get("dialog.cv.add.definition") );
	}
}
