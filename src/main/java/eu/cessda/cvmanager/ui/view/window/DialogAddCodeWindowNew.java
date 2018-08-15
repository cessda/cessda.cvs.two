package eu.cessda.cvmanager.ui.view.window;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.security.UserDetails;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.data.Binder;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.Query;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

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
import eu.cessda.cvmanager.ui.view.DetailsView;
import eu.cessda.cvmanager.utils.CvCodeTreeUtils;

public class DialogAddCodeWindowNew extends MWindow implements Translatable{

	private static final long serialVersionUID = -2960064213533383226L;
	private static final Logger log = LoggerFactory.getLogger(DialogAddCodeWindowNew.class);

	private final EventBus.UIEventBus eventBus;
	private final I18N i18n;
	private final StardatDDIService stardatDDIService;
	private final VocabularyService vocabularyService;
	private final VersionService versionService;
	private final ConceptService conceptService;
	private final CodeService codeService;
	private final VocabularyChangeService vocabularyChangeService;

	Binder<CVConcept> binder = new Binder<CVConcept>();
	
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
	private CVConcept theCode;
	private CVConcept parentCvConcept;
	private CVScheme cvScheme;
	
	private VocabularyDTO vocabulary;
	private VersionDTO version;
	private ConceptDTO concept;
	private CodeDTO code;
	private CodeDTO parentCode;
	private Language language;

	public DialogAddCodeWindowNew(EventBus.UIEventBus eventBus, StardatDDIService stardatDDIService, VocabularyService vocabularyService, 
			VersionService versionService,CodeService codeService, ConceptService conceptService, CVScheme cvSch, CVConcept newCode, CVConcept parentCvConcept, VocabularyDTO vocabularyDTO,
			VersionDTO versionDTO, CodeDTO codeDTO, CodeDTO parentCodeDTO,ConceptDTO conceptDTO, I18N i18n, Locale locale, VocabularyChangeService vocabularyChangeService) {
		super( parentCvConcept == null ? i18n.get( "dialog.detail.code.add.window.title" , locale):i18n.get( "dialog.detail.code.child.window.title" , 
				locale, ( parentCvConcept.getNotation() == null? parentCvConcept.getPrefLabelByLanguage( Language.valueOfEnum( versionDTO.getLanguage()).toString()) : parentCvConcept.getNotation() )));
		
		this.eventBus = eventBus;
		this.cvScheme = cvSch;
		this.parentCvConcept = parentCvConcept;
		this.i18n = i18n;
		this.vocabulary = vocabularyDTO;
		this.version = versionDTO;
		this.code = codeDTO;
		this.parentCode = parentCodeDTO;
		this.concept = conceptDTO;
		this.codeService = codeService;
		this.conceptService = conceptService;
		this.stardatDDIService = stardatDDIService;
		this.vocabularyService = vocabularyService;
		this.versionService = versionService;
		this.vocabularyChangeService = vocabularyChangeService;

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
		

		setTheCode(newCode);

		binder.setBean(getTheCode());

		storeCode.addClickListener(event -> {
			saveCode();
		});
		
		Button cancelButton = new Button("Cancel", e -> this.close());
		MHorizontalLayout row1 = new MHorizontalLayout();
		
		notation.addValueChangeListener( e -> {
			//Only allow letter
			((TextField)e.getComponent()).setValue( e.getValue().replaceAll("[^A-Za-z]", ""));
		});
		
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
		// CVConcept cv = binder.getBean();
		log.trace(getTheCode().getPrefLabelByLanguage( language.toString() ));
		getTheCode().save();
//		DDIStore ddiStore = stardatDDIService.saveElement(getTheCode().ddiStore, SecurityUtils.getCurrentUserLogin().get(), "Add Code");
//		if(parentCvConcept == null ) // root concept
//		{
//			cvScheme.addOrderedMemberList(ddiStore.getElementId());
//			cvScheme.save();
//			DDIStore ddiStoreCv = stardatDDIService.saveElement(cvScheme.ddiStore, SecurityUtils.getCurrentUserLogin().get(), "Update Top Concept");
//		}
//		else // child code, add narrower data in parent
//		{
//			//parentCode.addOrderedNarrowerList("http://lod.gesis.org/thesoz/concept_" + ddiStore.getElementId());
//			parentCvConcept.addOrderedNarrowerList( ddiStore.getElementId());
//			parentCvConcept.save();
//			DDIStore ddiStoreCv = stardatDDIService.saveElement(parentCvConcept.ddiStore, "User", "Add Code");
//		}
		
		
		code.setTitleDefinition( preferedLabel.getValue(), description.getValue(), language);
		
		
		concept.setTitle( preferedLabel.getValue() );
		concept.setDefinition( description.getValue() );
		
		if( !code.isPersisted() ) {
			code.setSourceLanguage( language.toString());
			code.setVocabularyId( vocabulary.getId() );
		}
		
		// save the code
		if( parentCode == null) {
			code.setNotation( notation.getValue() );
			code.setUri( code.getNotation() );
			concept.setNotation( notation.getValue() );
			
			code = codeService.save(code);
		} else {
			code.setNotation( parentCode.getNotation() + "." + notation.getValue());
			code.setUri( code.getNotation() );
			concept.setNotation( parentCode.getNotation() + "." + notation.getValue());
			code.setParent( parentCode.getNotation());
			
			List<CodeDTO> codeDTOs = codeService.findWorkflowCodesByVocabulary( vocabulary.getId());
			// re-save tree structure 
			TreeData<CodeDTO> codeTreeData = CvCodeTreeUtils.getTreeDataByCodes( codeDTOs );
			codeTreeData.addItem(parentCode, code);
			
			List<CodeDTO> newCodeDTOs = CvCodeTreeUtils.getCodeDTOByCodeTree(codeTreeData);
			for( CodeDTO eachCode: newCodeDTOs) {
				if( !eachCode.isPersisted())
					code = codeService.save(eachCode);
				else
					codeService.save(eachCode);
			}
		}
		// save to concept
		if( !concept.isPersisted()) {
			vocabulary.addCode(code);
			concept.setCodeId( code.getId());
			concept.setVersionId( version.getId() );
			concept = conceptService.save(concept);
			version.addConcept(concept);
			version = versionService.save(version);
		}

		// save change log
		VocabularyChangeDTO changeDTO = new VocabularyChangeDTO();
		changeDTO.setVocabularyId( vocabulary.getId());
		changeDTO.setVersionId( version.getId()); 
		changeDTO.setChangeType( "Code added" );
		changeDTO.setDescription( "Code " + concept.getNotation() + " added");
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
		getTheCode().setNotation(notation.getValue());
		getTheCode().setPrefLabelByLanguage(language.toString(), preferedLabel.getValue());
		getTheCode().setDescriptionByLanguage(language.toString(), description.getValue());
		
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

		concept.setPrefLabelByLanguage( language.toString() , value);
		return concept;
	}

	private String getPrefLabelByLanguage(CVConcept concept) {

    		return concept.getPrefLabelByLanguage( language.toString() );

	}

	private Object setDescriptionByLanguage(CVConcept concept, String value) {

		System.out.println("FooBar");
		concept.setDescriptionByLanguage( language.toString() , value);
		return null;
	}

	private String getDescriptionByLanguage(CVConcept concept) {

		return concept.getDescriptionByLanguage( language.toString() );

	}

	public CVConcept getTheCode() {
		return theCode;
	}

	public void setTheCode(CVConcept theCode) {
		this.theCode = theCode;
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		lNotation.withValue( i18n.get( "dialog.detail.code.add.form.notation" , locale)).withStyleName( "required" );
		lTitle.withValue( i18n.get( "dialog.detail.code.add.form.title" , locale)).withStyleName( "required" );
		lDescription.withValue( i18n.get( "dialog.detail.code.add.form.definition" , locale)).withStyleName( "required" );
		lLanguage.withValue( i18n.get( "dialog.detail.code.add.form.language" , locale));
	}
	
}
