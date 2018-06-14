package eu.cessda.cvmanager.ui.view.window;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.security.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.data.Binder;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConceptService;
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
import eu.cessda.cvmanager.ui.view.EditorView;

public class DialogEditCodeWindow extends MWindow {

	private static final Logger log = LoggerFactory.getLogger(DialogEditCodeWindow.class);
	private static final long serialVersionUID = 8118228014482059473L;
	
	private final EventBus.UIEventBus eventBus;
	private final I18N i18n;
	private final StardatDDIService stardatDDIService;
	private final VocabularyService vocabularyService;
	private final CodeService codeService;
	private final ConceptService conceptService;
	private final VocabularyChangeService vocabularyChangeService;

	Binder<CVConcept> binder = new Binder<CVConcept>();
	private MVerticalLayout layout = new MVerticalLayout();
	
	private MLabel lSourceNotation = new MLabel( "Code" );
	private MLabel lTitle = new MLabel( "Descriptive term" );
	private MLabel lDescription = new MLabel( "Definition" );
	private MLabel lLanguage = new MLabel( "Language" );
	private MLabel lNotation = new MLabel( "Code" );
	private MLabel lSourceTitle = new MLabel( "Descriptive term (source)" );
	private MLabel lSourceDescription = new MLabel( "Definition (source)" );
	private MLabel lSourceLanguage = new MLabel( "Language (source)" );
	
	private MTextField sourceNotation = new MTextField("Code");
	private MTextField sourceTitle = new MTextField("Descriptive term (source)");
	private MTextField sourceLanguage = new MTextField("Language (source)");
	private TextArea sourceDescription = new TextArea("Definition en (source)");

	private MTextField notation = new MTextField("Code");
	private TextField preferedLabel = new TextField("Descriptive term*");
	private TextArea description = new TextArea("Definition*");
	private ComboBox<Language> languageCb = new ComboBox<>("Language*");

	private Language language;

	private Button storeCode = new Button("Save");

	private CVScheme cvScheme;
	private CVConcept cvConcept;
	
	private VocabularyDTO vocabulary;
	private VersionDTO version;
	private CodeDTO code;
	private ConceptDTO concept;
	
	private MCssLayout changeBox = new MCssLayout();
	private MLabel lChange = new MLabel( "Change notes:" );
	private MLabel lChangeType = new MLabel( "Type*" );
	private MLabel lChangeDesc = new MLabel( "Description" );
	private ComboBox<String> changeCb = new ComboBox<>();
	private MTextField changeDesc = new MTextField();
	
	MHorizontalLayout sourceRow = new MHorizontalLayout();
	MHorizontalLayout sourceRowA = new MHorizontalLayout();
	MHorizontalLayout sourceRowB = new MHorizontalLayout();

	public DialogEditCodeWindow(EventBus.UIEventBus eventBus, StardatDDIService stardatDDIService, 
			VocabularyService vocabularyService, CodeService codeService, ConceptService conceptService,
			CVScheme cvScheme, CVConcept conceptCode, Language sLanguage, VocabularyDTO vocabularyDTO, 
			VersionDTO versionDTO, CodeDTO codeDTO, ConceptDTO conceptDTO, I18N i18n, Locale locale, 
			VocabularyChangeService vocabularyChangeService) {
		super( "Edit Code");
		this.i18n = i18n;
		this.cvScheme = cvScheme;
		this.cvConcept = conceptCode;
		this.language = sLanguage;
		this.stardatDDIService = stardatDDIService;
		this.vocabularyService = vocabularyService;
		this.codeService = codeService;
		this.conceptService = conceptService;
		this.vocabularyChangeService = vocabularyChangeService;
		
		this.eventBus = eventBus;
		this.vocabulary = vocabularyDTO;
		this.version = versionDTO;
		this.code = codeDTO;
		this.concept = conceptDTO;
		
		sourceNotation.withWidth("85%");
		sourceNotation.setValue( cvConcept.getNotation() == null ? "" : cvConcept.getNotation() );
		sourceNotation.setReadOnly( true );
		
		notation.withWidth("85%");
		
		String sourceLang = Language.getEnumByName( vocabulary.getSourceLanguage() ).toString();
		
		sourceTitle.withFullWidth();
		sourceTitle.setValue( cvConcept.getPrefLabelByLanguage( sourceLang ) );
		sourceDescription.setSizeFull();
		
		sourceDescription.setValue( cvConcept.getDescriptionByLanguage( sourceLang ) );
		sourceDescription.setReadOnly( true );
		sourceTitle.setReadOnly( true );
		
		preferedLabel.setWidth("100%");
		description.setSizeFull();
		
		sourceLanguage
			.withReadOnly( true)
			.setValue( vocabulary.getSourceLanguage() );
		
		lTitle.withStyleName( "required" );
		lLanguage.withStyleName( "required" );
		lDescription.withStyleName( "required" );
		
		preferedLabel.setCaption( "Descriptive term (" + language + ")*");
		description.setCaption( "Definition ("+ language +")*");
		
		notation.setValue( cvConcept.getNotation() );
		preferedLabel.setValue( cvConcept.getPrefLabelByLanguage(language.toString()));
		description.setValue( cvConcept.getDescriptionByLanguage(language.toString()) );
		
		
		languageCb.setItems( language );
		languageCb.setEmptySelectionAllowed( false );
		languageCb.setTextInputAllowed( false );
		languageCb.setValue( language );
		languageCb.setReadOnly( true );
		languageCb.addValueChangeListener( e -> {
			setSelectedLanguage( e.getValue());
			
			preferedLabel.setCaption( "Descriptive term (" + language + ")*");
			description.setCaption( "Definition ("+ language +")*");
			
			preferedLabel.setValue( cvConcept.getPrefLabelByLanguage(language.toString()));
			description.setValue( cvConcept.getDescriptionByLanguage(language.toString()) );
			
			if( e.getValue().equals( sourceLang )) {
				sourceRow.setVisible( false );
				sourceRowA.setVisible( false );
				sourceRowB.setVisible( false );
			} else {
				sourceRow.setVisible( false );
				sourceRowA.setVisible( true );
				sourceRowB.setVisible( true );
			}
		});
		languageCb.setItemCaptionGenerator( new ItemCaptionGenerator<Language>() {
			private static final long serialVersionUID = 1L;
			@Override
			public String apply(Language item) {
				return item.name() + " (" +item.getLanguage() + ")";
			}
		});
		

		binder.setBean(cvConcept);

		storeCode.addClickListener(event -> {
			saveCode();
		});

		Button cancelButton = new Button("Cancel", e -> this.close());
		
		lChange
			.withStyleName("change-header");
		changeCb.setWidth("100%");
		changeCb.setItems( Arrays.asList( VocabularyChangeDTO.codeChangeTypes));
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
		
		MHorizontalLayout row1 = new MHorizontalLayout();
		
		if( !language.equals( Language.getEnum(sourceLang) )) {
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
									lSourceNotation, sourceNotation
							).withExpand(lSourceNotation, 0.215f).withExpand(sourceNotation, 0.785f),
							new MHorizontalLayout().add(
									lSourceLanguage, sourceLanguage
							)
					).withExpand( sourceRowA.getComponent(0), 0.7f)
					 .withExpand( sourceRowA.getComponent(1), 0.3f),
					sourceRow
						.withFullWidth()
						.add(
								lSourceTitle, sourceTitle
							).withExpand(lSourceTitle, 0.15f).withExpand( sourceTitle, 0.85f),
					sourceRowB
						.withFullWidth()
						.withFullHeight()
//						.withHeight("270px")
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
						.withFullHeight()
//						.withHeight("290px")
						.add(
							lDescription, description
						).withExpand( lDescription, 0.15f).withExpand( description, 0.85f),
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
					.withExpand(layout.getComponent(0), 0.03f)
					.withExpand(layout.getComponent(1), 0.03f)
					.withExpand(layout.getComponent(2), 0.25f)
					.withExpand(layout.getComponent(3), 0.03f)
					.withExpand(layout.getComponent(4), 0.25f)
					.withExpand(layout.getComponent(5), 0.1f)
					.withAlign(layout.getComponent(6), Alignment.BOTTOM_RIGHT);
			
		} else {
			layout
			.withHeight("98%")
			.withStyleName("dialog-content")
			.add( 
				row1
					.withFullWidth()
					.add(
						new MHorizontalLayout()
						.withFullWidth()
						.add(
								lNotation, notation
						).withExpand(lNotation, 0.215f).withExpand(notation, 0.785f),
						new MHorizontalLayout().add(
								lLanguage, languageCb
						)
				).withExpand( row1.getComponent(0), 0.7f)
				 .withExpand( row1.getComponent(1), 0.3f),
				new MHorizontalLayout()
					.withFullWidth()
					.add(
						lTitle, preferedLabel
					).withExpand(lTitle, 0.15f).withExpand( preferedLabel, 0.85f),
				new MHorizontalLayout()
					.withFullWidth()
					.withFullHeight()
	//				.withHeight("300px")
					.add(
						lDescription, description
				).withExpand( lDescription, 0.15f).withExpand( description, 0.85f),
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
			.withExpand(layout.getComponent(0), 0.06f)
			.withExpand(layout.getComponent(1), 0.06f)
			.withExpand(layout.getComponent(2), 0.4f)
			.withExpand(layout.getComponent(3), 0.2f)
			.withAlign(layout.getComponent(4), Alignment.BOTTOM_RIGHT);
		}
		
		this
			.withHeight("800px")
			.withWidth("1024px")
			.withModal( true )
			.withContent(layout);
	}

	private void saveCode() {
		if(!isInputValid())
			return;
		
		if( changeCb.getValue() == null ) {
			Notification.show("Please select the change type!");
			return;
		}
		
		// CVConcept cv = binder.getBean();
		log.trace(cvConcept.getPrefLabelByLanguage(language.toString()));
		cvConcept.save();
		DDIStore ddiStore = stardatDDIService.saveElement(cvConcept.ddiStore, SecurityUtils.getCurrentUserLogin().get(), "Update code");
		// store the code and index
		code.setTitleDefinition(preferedLabel.getValue(), description.getValue(), language);
		codeService.save(code);
		// store concept
		concept.setTitle( preferedLabel.getValue() );
		concept.setDefinition( description.getValue() );
		conceptService.save(concept);
		
		// save changes log
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
		
		
		// indexing editor
		vocabularyService.index(vocabulary);
		
		eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_CREATED, ddiStore) );
		
		this.close();
	}
	
	private boolean isInputValid() {
		cvConcept.setNotation(notation.getValue());
		cvConcept.setPrefLabelByLanguage(language.toString(), preferedLabel.getValue());
		cvConcept.setDescriptionByLanguage(language.toString(), description.getValue());
		
		binder
			.forField( notation )
			.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 250 ))	
			.bind( concept -> concept.getNotation(),
				(concept, value) -> concept.setNotation(value));
		
		binder
			.forField( preferedLabel )
			.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 250 ))	
			.bind( concept -> getPrefLabelByLanguage(concept),
				(concept, value) -> setPrefLabelByLanguage(concept, value));

		binder
			.forField( description )
			.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 10000 ))	
			.bind( concept -> getDescriptionByLanguage(concept),
				(concept, value) -> setDescriptionByLanguage(concept, value));
		
		binder.validate();
		return binder.isValid();
	}

	private CVConcept setPrefLabelByLanguage(CVConcept concept, String value) {

		concept.setPrefLabelByLanguage(language.toString(), value);
		return concept;
	}

	private String getPrefLabelByLanguage(CVConcept concept) {

		return concept.getPrefLabelByLanguage(language.toString());

	}

	private Object setDescriptionByLanguage(CVConcept concept, String value) {

		System.out.println("FooBar");
		concept.setDescriptionByLanguage(language.toString(), value);
		return null;
	}

	private String getDescriptionByLanguage(CVConcept concept) {

		return concept.getDescriptionByLanguage(language.toString());

	}

	public void setSelectedLanguage(Language selectedLanguage) {
		this.language = selectedLanguage;
	}
	
	
}
