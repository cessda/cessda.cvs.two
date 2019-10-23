package eu.cessda.cvmanager.ui.view.window;

import org.gesis.wts.domain.enumeration.Language;
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
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;

import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.manager.WorkspaceManager;
import eu.cessda.cvmanager.ui.view.EditorDetailsView;

public class DialogTranslateCodeWindow extends MWindow {

	private static final Logger log = LoggerFactory.getLogger(DialogTranslateCodeWindow.class);
	private static final long serialVersionUID = 8118228014482059473L;

	private final transient WorkspaceManager workspaceManager;
	private final transient EventBus.UIEventBus eventBus;
	private final transient I18N i18n;

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
//	private TextArea sourceDescription = new TextArea("Definition en (source)");
	private RichTextArea sourceDescription = new RichTextArea("Definition en (source)");
	
	private MLabel lCode = new MLabel( "Descriptive terms" );
	private MTextField codeText = new MTextField("Code");

	private TextField preferedLabel = new TextField("Code");
//	private TextArea description = new TextArea("Definition");
	private RichTextArea description = new RichTextArea("Definition");
	private ComboBox<Language> languageCb = new ComboBox<>("Language");

	private Button storeCode = new Button("Save");
	
	private Language language;
	private Language sourceLang;
	
	private VocabularyDTO vocabulary;
	private VersionDTO version;
	private CodeDTO code;
	private ConceptDTO concept;
	private ConceptDTO slConcept;
	
	MHorizontalLayout sourceRowA = new MHorizontalLayout();
	MHorizontalLayout sourceRowB = new MHorizontalLayout();

	public DialogTranslateCodeWindow(WorkspaceManager workspaceManager, EventBus.UIEventBus eventBus, Language sLanguage, Language sourceLang, VocabularyDTO vocabularyDTO,
									 VersionDTO versionDTO, CodeDTO codeDTO, ConceptDTO conceptDTO, ConceptDTO slConcept, I18N i18n) {
		super( "Add Code Translation");
		this.workspaceManager = workspaceManager;
		this.i18n = i18n;
		this.language = sLanguage;
		
		this.eventBus = eventBus;
		this.vocabulary = vocabularyDTO;
		this.version = versionDTO;
		this.code = codeDTO;
		this.concept = conceptDTO;
		this.sourceLang = sourceLang;
		this.slConcept = slConcept;
				
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
//		lDescription.withStyleName( "required" );
		
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
		
		// store the code
		workspaceManager.saveCodeAndConcept(vocabulary, version, code, null, concept, slConcept,
				null, preferedLabel.getValue(), description.getValue());

		// save change log
		workspaceManager.storeChangeLog(vocabulary, version, "TL code added", concept.getNotation());
		
		eventBus.publish(EventScope.UI, EditorDetailsView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_CREATED, null) );
		
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
//			.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 10000 ))	
			.bind(  concept -> concept.getDefinition(),
				(concept, value) -> concept.setDefinition(value));
		
		binder.validate();
		return binder.isValid();
	}

}
