package eu.cessda.cvmanager.ui.view.window;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.*;
import org.gesis.wts.domain.enumeration.Language;
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

import eu.cessda.cvmanager.config.Constants;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.manager.WorkspaceManager;
import eu.cessda.cvmanager.ui.view.EditorDetailsView;
import eu.cessda.cvmanager.utils.ParserUtils;

public class DialogEditCodeWindow extends MWindow {

	private static final Logger log = LoggerFactory.getLogger(DialogEditCodeWindow.class);
	private static final long serialVersionUID = 8118228014482059473L;

	private final transient WorkspaceManager workspaceManager;
	private final transient EventBus.UIEventBus eventBus;
	private final transient I18N i18n;
	private final transient CodeService codeService;

	private Binder<CodeDTO> binder = new Binder<>();
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
	
	private VocabularyDTO vocabulary;
	private VersionDTO version;
	private CodeDTO code;
	private ConceptDTO concept;
	
	private MCssLayout changeBox = new MCssLayout();
	private MLabel lChange = new MLabel( "Change notes:" );
	private MLabel lChangeType = new MLabel( "Type" );
	private MLabel lChangeDesc = new MLabel( "Description" );
	private ComboBox<String> changeCb = new ComboBox<>();
	private MTextField changeDesc = new MTextField();
	
	private MHorizontalLayout sourceRow = new MHorizontalLayout();
	private MHorizontalLayout sourceRowA = new MHorizontalLayout();
	private MHorizontalLayout sourceRowB = new MHorizontalLayout();
	
	private String tempNotation;
	private String tempCompleteNotation;
	
	public DialogEditCodeWindow(WorkspaceManager workspaceManager, I18N i18n, EventBus.UIEventBus eventBus,
								Language sLanguage, VocabularyDTO vocabularyDTO, VersionDTO versionDTO, CodeDTO codeDTO,
								ConceptDTO conceptDTO, CodeService codeService) {
		super( "Edit Code");
		this.workspaceManager = workspaceManager;
		this.i18n = i18n;
		this.language = sLanguage;
		
		this.eventBus = eventBus;
		this.vocabulary = vocabularyDTO;
		this.version = versionDTO;
		this.code = codeDTO;
		this.concept = conceptDTO;
		this.codeService = codeService;
		
		sourceNotation.withWidth("85%");
		sourceNotation.setValue( code.getNotation() );
		sourceNotation.setReadOnly( true );
		
		notation
			.withWidth("85%")
			.addValueChangeListener( e -> ((TextField)e.getComponent()).setValue( e.getValue().replaceAll(Constants.NOTATION_REGEX, "")));
		
		Language sourceLang = Language.valueOfEnum( vocabulary.getSourceLanguage() );
		
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
			.setValue( vocabulary.getSourceLanguage() );
		
		lTitle.withStyleName( "required" );
		lLanguage.withStyleName( "required" );

		preferedLabel.setCaption( "Descriptive term (" + language + ")*");
		description.setCaption( "Definition ("+ language +")*");
				
		tempCompleteNotation = concept.getNotation();
		String actualCodeNotation = getCodeActualNotation( concept.getNotation() );
		notation.setValue( actualCodeNotation );
		tempNotation = actualCodeNotation;
		
		preferedLabel.setValue( concept.getTitle());
		description.setValue( concept.getDefinition() );
		
		
		languageCb.setItems( language );
		languageCb.setEmptySelectionAllowed( false );
		languageCb.setTextInputAllowed( false );
		languageCb.setValue( language );
		languageCb.setReadOnly( true );
		languageCb.addValueChangeListener( e -> {
			setSelectedLanguage( e.getValue());
			
			preferedLabel.setCaption( "Descriptive term (" + language + ")*");
			description.setCaption( "Definition ("+ language +")*");
			
			preferedLabel.setValue( code.getTitleByLanguage(language));
			description.setValue( code.getDefinitionByLanguage(language) );
			
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
		

		binder.setBean(code);

		storeCode.addClickListener(event -> saveEditedCode());

		Button cancelButton = new Button("Cancel", e -> this.close());
		
		lChange
			.withStyleName("change-header");
		changeCb.setWidth("100%");
		changeCb.setItems( Arrays.asList( VocabularyChangeDTO.codeChangeTypes));
		changeCb.setTextInputAllowed(false);

		changeDesc
			.withFullWidth()
			.withValue( this.concept.getTitle() );
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
		
		// check if current version is the initial version
		if( this.concept.getPreviousConcept() == null) {
			changeBox.setVisible( false );
		}
		
		MHorizontalLayout row1 = new MHorizontalLayout();
		
		if( !language.equals( sourceLang) ) {
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
			.withExpand(layout.getComponent(4), 0.3f)
			.withAlign(layout.getComponent(4), Alignment.BOTTOM_RIGHT);
		}
		
		this
			.withHeight("800px")
			.withWidth("1024px")
			.withModal( true )
			.withContent(layout);
	}

	private String getCodeActualNotation( String codeValue) {
		int lastDotIndex = concept.getNotation().lastIndexOf( '.' );
		if( lastDotIndex > 0)
			return codeValue.substring( lastDotIndex + 1 );
		
		return codeValue;
	}

	private void saveEditedCode() {
		if(!isInputValid())
			return;

		log.info( "Preparing to save code and concept with notation {}", notation.getValue());
		
		// store the changes
		String completeNotation = (code.getParent() != null ? code.getParent() + "." : "") + notation.getValue();
		workspaceManager.saveCodeAndConcept(vocabulary, version, code,  null, concept, null,
			completeNotation, preferedLabel.getValue(), ParserUtils.toXHTML( description.getValue()));
		
		if( !tempNotation.equals( notation.getValue()) ) {
			// check children notation change
			List<CodeDTO> workflowCodes = codeService.findWorkflowCodesByVocabulary( vocabulary.getId());
			List<CodeDTO> codesNeedUpdate = workflowCodes.stream()
					.sorted((c1,c2) -> c1.getPosition().compareTo( c2.getPosition()))
					.filter( p -> p.getPosition() > code.getPosition())
					.filter( p -> p.getNotation().contains( tempCompleteNotation ) )
					.collect(Collectors.toList());
			
			if( codesNeedUpdate != null && !codesNeedUpdate.isEmpty()) {
				for(CodeDTO toUpdateCode : codesNeedUpdate) {
					// get corresponding child concepts
					ConceptDTO.getConceptFromCode( version.getConcepts(), toUpdateCode.getId()).ifPresent(
						toUpdateConcept -> {
							String updatedNotation = toUpdateCode.getNotation().replaceFirst(toUpdateCode.getParent(), completeNotation);
							workspaceManager.saveCodeAndConcept(vocabulary, version, toUpdateCode,  null, toUpdateConcept, null,
									updatedNotation, toUpdateConcept.getTitle(), toUpdateConcept.getDefinition());
						}
					);
				}
			}
			 
		}
		
		// save changes log
		if( changeBox.isVisible() && changeCb.getValue() != null)
			workspaceManager.storeChangeLog(vocabulary, version, changeCb.getValue(), changeDesc.getValue() == null ? "": changeDesc.getValue());

		eventBus.publish(EventScope.UI, EditorDetailsView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_CREATED, null) );
		this.close();
	}
	
	private boolean isInputValid() {
		code.setNotation(notation.getValue());
		code.setTitleByLanguage( preferedLabel.getValue(), language);
		code.setDefinitionByLanguage( description.getValue(), language);
		
		binder
			.forField( notation )
			.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 250 ))	
			.bind(CodeDTO::getNotation,
					CodeDTO::setNotation);
		
		binder
			.forField( preferedLabel )
			.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 250 ))	
			.bind( c -> c.getTitleByLanguage( language ),
				(c, value) -> c.setTitleByLanguage(value, language));

		binder
			.forField( description )
			.bind( c -> c.getDefinitionByLanguage( language ),
					(c, value) -> c.setDefinitionByLanguage(value, language));
		
		binder.validate();
		return binder.isValid();
	}

	public void setSelectedLanguage(Language selectedLanguage) {
		this.language = selectedLanguage;
	}
	
	
}
