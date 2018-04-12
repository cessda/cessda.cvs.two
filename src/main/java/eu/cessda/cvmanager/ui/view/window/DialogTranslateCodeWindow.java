package eu.cessda.cvmanager.ui.view.window;

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
import eu.cessda.cvmanager.service.CvManagerService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.view.DetailView;
import eu.cessda.cvmanager.ui.view.EditorView;

public class DialogTranslateCodeWindow extends MWindow {

	private static final Logger log = LoggerFactory.getLogger(DialogTranslateCodeWindow.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 8118228014482059473L;
	private final EventBus.UIEventBus eventBus;
	private final I18N i18n;
	private final CvManagerService cvManagerService;
	private final VocabularyService vocabularyService;
	private final CodeService codeService;

	Binder<CVConcept> binder = new Binder<CVConcept>();
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

	private CVScheme cvScheme;
	private CVConcept code;
	
	private AgencyDTO agency;
	private VocabularyDTO vocabulary;
	private Language language;
	
	MHorizontalLayout sourceRowA = new MHorizontalLayout();
	MHorizontalLayout sourceRowB = new MHorizontalLayout();

	public DialogTranslateCodeWindow(EventBus.UIEventBus eventBus, CvManagerService cvManagerService, VocabularyService vocabularyService, CodeService codeService,
			CVScheme cvScheme, CVConcept cvConcept, VocabularyDTO vocabularyDTO, AgencyDTO agencyDTO, I18N i18n, Locale locale) {
		super( "Add Code Translation");
		this.cvScheme = cvScheme;
		this.code = cvConcept;
		
		this.eventBus = eventBus;
		this.i18n = i18n;
		this.vocabulary = vocabularyDTO;
		this.agency = agencyDTO;
		this.cvManagerService = cvManagerService;
		this.vocabularyService = vocabularyService;
		this.codeService = codeService;
		
		Language sourceLang = Language.getEnumByName( vocabulary.getSourceLanguage() );
		
		codeText.setValue( code.getPrefLabelByLanguage( sourceLang.toString() ));
		codeText.withFullWidth()
			.withReadOnly( true );
		
		sourceTitle.withFullWidth();
		sourceTitle.setValue( code.getPrefLabelByLanguage( sourceLang.toString() ) );
		sourceDescription.setSizeFull();
		
		sourceDescription.setValue( code.getDescriptionByLanguage( sourceLang.toString() ) );
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
		
		// TODO: use list language from vocabulary if possible
		List<Language> availableLanguages = new ArrayList<>();
		Set<Language> userLanguages = new HashSet<>();
		if( SecurityUtils.isCurrentUserAgencyAdmin( this.agency )) {
			userLanguages.addAll( Arrays.asList( Language.values() ) );
		}
		else {
			SecurityUtils.getCurrentUserLanguageTlByAgency(  this.agency ).ifPresent( languages -> {
				userLanguages.addAll(languages);
			});
		}
		
		if( vocabulary != null ) {
			availableLanguages = Language.getFilteredLanguage(userLanguages, vocabulary.getLanguages());
		} else {
			availableLanguages = Language.getFilteredLanguage(userLanguages, cvScheme.getLanguagesByTitle());
		}
		// remove with sourceLanguage option if exist
		availableLanguages.remove( sourceLang );
		
		languageCb.setItems( availableLanguages );
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
			
			preferedLabel.setCaption( "Code (" + language + ")*");
			description.setCaption( "Definition ("+ language +")*");
			
			preferedLabel.setValue( code.getPrefLabelByLanguage(language.toString()));
			description.setValue( code.getDescriptionByLanguage(language.toString()) );
			
			if( e.getValue().equals( sourceLang )) {
				sourceRowA.setVisible( false );
				sourceRowB.setVisible( false );
			} else {
				sourceRowA.setVisible( true );
				sourceRowB.setVisible( true );
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

		binder.setBean(code);

		binder.bind(preferedLabel, concept -> getPrefLabelByLanguage(concept),
				(concept, value) -> setPrefLabelByLanguage(concept, value));

		binder.bind(description, concept -> getDescriptionByLanguage(concept),
				(concept, value) -> setDescriptionByLanguage(concept, value));

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
					.withHeight("290px")
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
					.withHeight("290px")
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
		// CVConcept cv = binder.getBean();
		log.trace(code.getPrefLabelByLanguage(language.toString()));
		code.save();
		DDIStore ddiStore = cvManagerService.saveElement(code.ddiStore, "Peter", "minor edit");
		
		eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_CREATED, ddiStore) );
		
		this.close();
	}

	private CVConcept setPrefLabelByLanguage(CVConcept concept, String value) {
		concept.setPrefLabelByLanguage(language.toString(), value);
		return concept;
	}

	private String getPrefLabelByLanguage(CVConcept concept) {
		return concept.getPrefLabelByLanguage(language.toString());

	}

	private Object setDescriptionByLanguage(CVConcept concept, String value) {
		concept.setDescriptionByLanguage(language.toString(), value);
		return null;
	}

	private String getDescriptionByLanguage(CVConcept concept) {

		return concept.getDescriptionByLanguage(language.toString());

	}
}
