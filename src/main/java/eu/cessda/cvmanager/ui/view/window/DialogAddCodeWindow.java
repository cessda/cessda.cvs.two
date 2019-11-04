package eu.cessda.cvmanager.ui.view.window;

import java.util.Locale;

import com.vaadin.ui.*;
import org.gesis.wts.domain.enumeration.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.data.Binder;
import com.vaadin.data.validator.StringLengthValidator;

import eu.cessda.cvmanager.config.Constants;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.manager.WorkspaceManager;
import eu.cessda.cvmanager.ui.view.EditorDetailsView;
import eu.cessda.cvmanager.utils.ParserUtils;

public class DialogAddCodeWindow extends MWindow implements Translatable{

	private static final long serialVersionUID = -2960064213533383226L;
	private static final Logger log = LoggerFactory.getLogger(DialogAddCodeWindow.class);

	private final transient WorkspaceManager workspaceManager;
	private final transient EventBus.UIEventBus eventBus;
	private final transient I18N i18n;

	private Locale locale = UI.getCurrent().getLocale();

	private Binder<ConceptDTO> binder = new Binder<>();
	
	private MLabel lNotation = new MLabel( "Code" );
	private MLabel lTitle = new MLabel( "Descriptive term" );
	private MLabel lDescription = new MLabel( "Definition" );
	private MLabel lLanguage = new MLabel( "Language (source)" );

	private MVerticalLayout layout = new MVerticalLayout();
	private MTextField parentNotation = new MTextField();
	private MTextField notation = new MTextField();
	private TextField preferedLabel = new TextField();
	private TextArea description = new TextArea();
	private ComboBox<Language> languageCb = new ComboBox<>();

	private Button storeCode = new Button("Save");
	
	private VocabularyDTO vocabulary;
	private VersionDTO version;
	private ConceptDTO concept;
	private CodeDTO code;
	private CodeDTO parentCode;
	private Language language;

	public DialogAddCodeWindow(WorkspaceManager workspaceManager, I18N i18n, EventBus.UIEventBus eventBus, VocabularyDTO vocabularyDTO,
							   VersionDTO versionDTO, CodeDTO codeDTO, CodeDTO parentCodeDTO, ConceptDTO conceptDTO) {
		super( parentCodeDTO == null ? i18n.get( "dialog.detail.code.add.window.title" ):i18n.get( "dialog.detail.code.child.window.title", parentCodeDTO.getNotation() ));
		this.workspaceManager = workspaceManager;
		this.eventBus = eventBus;
		this.i18n = i18n;
		this.vocabulary = vocabularyDTO;
		this.version = versionDTO;
		this.code = codeDTO;
		this.parentCode = parentCodeDTO;
		this.concept = conceptDTO;

		language = Language.valueOfEnum( this.version.getLanguage());
		
		languageCb.setItems( language);
		languageCb.setValue( language );

	
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
		
		languageCb.addValueChangeListener( e -> {
			if( e.getValue() != null )
				language = e.getValue();
			else
				language = null;
		});
		
		
		preferedLabel.setWidth("100%");
		description.setSizeFull();
		

		binder.setBean( concept);

		storeCode.addClickListener(event -> saveCode());
		
		Button cancelButton = new Button("Cancel", e -> this.close());
		MHorizontalLayout row1 = new MHorizontalLayout();
		
		notation.addValueChangeListener( e -> ((TextField)e.getComponent()).setValue( e.getValue().replaceAll(Constants.NOTATION_REGEX, "")));
		
		if( parentCode != null )  {
			notation.withWidth("80%");
			parentNotation
				.withReadOnly( true )
				.withWidth("100%")
				.withValue( parentCode.getNotation() + ".");
			row1
				.withFullWidth()
				.add(
					new MHorizontalLayout()
					.withFullWidth()
					.add(
							lNotation, parentNotation, notation
					)
					.withExpand(lNotation, 0.215f)
					.withExpand(parentNotation, 0.290f)
					.withExpand(notation, 0.490f),
					new MHorizontalLayout().add(
							lLanguage, languageCb
					)
			).withExpand( row1.getComponent(0), 0.7f)
			 .withExpand( row1.getComponent(1), 0.3f);
		} else {
			notation.withWidth("85%");
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
			 .withExpand( row1.getComponent(1), 0.3f);
		}
		
		layout
			.withHeight("98%")
			.withStyleName("dialog-content")
			.add( 
				row1,
				new MHorizontalLayout()
					.withFullWidth()
					.add(
						lTitle, preferedLabel
					).withExpand(lTitle, 0.15f).withExpand( preferedLabel, 0.85f),
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
			.withExpand(layout.getComponent(2), 0.5f)
			.withExpand(layout.getComponent(3), 0.3f)
			.withAlign(layout.getComponent(3), Alignment.BOTTOM_RIGHT);

		
		this
			.withHeight("650px")
			.withWidth("1024px")
			.withModal( true )
			.withContent(layout);

		updateMessageStrings(locale);
	}

	private void saveCode() {
		if(!isInputValid())
			return;
		log.info("Preparing to save code {} ", concept.getNotation());

		// store the code
		workspaceManager.saveCodeAndConcept(vocabulary, version, code, parentCode, concept, null,
				notation.getValue(), preferedLabel.getValue(), ParserUtils.toXHTML(description.getValue()));

		// save change log
		workspaceManager.storeChangeLog(vocabulary, version, "Code added", concept.getNotation());

		eventBus.publish(EventScope.UI, EditorDetailsView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_CREATED, null) );
		this.close();
	}

	private boolean isInputValid() {
		concept.setNotation(notation.getValue());
		concept.setTitle( preferedLabel.getValue());
		concept.setDefinition( description.getValue());
		
		binder
			.forField( notation )
			.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 250 ))	
			.withValidator( p -> !version.isConceptExist(parentCode == null? p: parentCode.getNotation() + "." + p), "code is already exist")
			.bind(ConceptDTO::getNotation,
					ConceptDTO::setNotation);

		binder
			.forField( preferedLabel )
			.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 250 ))	
			.bind(ConceptDTO::getTitle,
					ConceptDTO::setTitle);

		binder
			.forField( description )
//			.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 10000 ))	
			.bind(ConceptDTO::getDefinition,
					ConceptDTO::setDefinition);
		
		binder.validate();
		return binder.isValid();
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		lNotation.withValue( i18n.get( "dialog.detail.code.add.form.notation" , locale)).withStyleName( "required" );
		lTitle.withValue( i18n.get( "dialog.detail.code.add.form.title" , locale)).withStyleName( "required" );
		lDescription.withValue( i18n.get( "dialog.detail.code.add.form.definition" , locale));
		lLanguage.withValue( i18n.get( "dialog.detail.code.add.form.language" , locale));
	}
	
}
