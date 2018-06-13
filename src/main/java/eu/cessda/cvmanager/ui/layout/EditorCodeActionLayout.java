package eu.cessda.cvmanager.ui.layout;

import java.util.Locale;

import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.repository.search.VocabularySearchRepository;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.mapper.VocabularyMapper;
import eu.cessda.cvmanager.ui.component.ResponsiveBlock;
import eu.cessda.cvmanager.ui.view.DetailView;
import eu.cessda.cvmanager.ui.view.window.DialogAddCodeWindow;
import eu.cessda.cvmanager.ui.view.window.DialogCVSchemeWindow;
import eu.cessda.cvmanager.ui.view.window.DialogEditCodeWindow;
import eu.cessda.cvmanager.ui.view.window.DialogTranslateCodeWindow;

public class EditorCodeActionLayout extends ResponsiveBlock{
	private static final long serialVersionUID = 2436346372920594014L;
	
	private final StardatDDIService stardatDDIService;
	private final AgencyService agencyService;
	private final VocabularyService vocabularyService;
	private final VersionService versionService;
	private final CodeService codeService;
	private final ConceptService conceptService;
	
	private I18N i18n;
	private Locale locale = UI.getCurrent().getLocale();
	private final UIEventBus eventBus;
	
	private MButton buttonCodeAdd = new MButton();
	private MButton buttonCodeEdit = new MButton();
	private MButton buttonCodeAddTranslation = new MButton();
	private MButton buttonCodeAddChild = new MButton();
	private MButton buttonCodeDelete = new MButton();
	private MButton buttonCodeSort = new MButton();
	
	private boolean enableSort;
	private boolean isCurrentSL;
	
	private VocabularyDTO vocabulary;
	private CVScheme cvScheme;
	private CVConcept cvConcept;
	private VersionDTO currentVersion;
	private CodeDTO currentCode;
	private ConceptDTO currentConcept;
	private AgencyDTO agency;
	private Language selectedLanguage;
	private Language sourceLanguage;
	
	public EditorCodeActionLayout(String titleHeader, String showHeader, I18N i18n, StardatDDIService stardatDDIService,
			AgencyService agencyService, VocabularyService vocabularyService, VersionService versionService,
			CodeService codeService, ConceptService conceptService, UIEventBus eventBus) {
		super(titleHeader, showHeader, i18n);
		this.i18n = i18n;
		this.stardatDDIService = stardatDDIService;
		this.agencyService = agencyService;
		this.vocabularyService = vocabularyService;
		this.versionService = versionService;
		this.codeService = codeService;
		this.conceptService = conceptService;
		this.eventBus = eventBus;
		init();
	}

	private void init() {
		buttonCodeAdd
			.withFullWidth()
			.withStyleName("action-button");
		buttonCodeEdit
			.withFullWidth()
			.withStyleName("action-button")
			.setVisible( false);
		buttonCodeAddChild
			.withFullWidth()
			.withStyleName("action-button")
			.setVisible( false);
		buttonCodeAddTranslation
			.withFullWidth()
			.withStyleName("action-button")
			.setVisible( false);
		buttonCodeDelete
			.withFullWidth()
			.withStyleName("action-button")
			.setVisible( false);
		buttonCodeSort
			.withFullWidth()
			.withStyleName("action-button")
			.setVisible( false);
		
		buttonCodeAdd.addClickListener( this::doAddCode );
		buttonCodeEdit.addClickListener( this::doEditCode );
		buttonCodeAddTranslation.addClickListener( this::doCodeAddTranslation );
		buttonCodeAddChild.addClickListener( this::doCodeAddChild );
		buttonCodeDelete.addClickListener( this::doDeleteCode );
		buttonCodeSort.addClickListener( this::doSortCode );
		
		updateMessageStrings(locale);
		
		getInnerContainer()
			.add(
				buttonCodeAdd,
				buttonCodeEdit,
				buttonCodeAddChild,
				buttonCodeAddTranslation,
				buttonCodeDelete,
				buttonCodeSort
			);
	}

	private void doAddCode(ClickEvent event ) {
		
		CVConcept newCVConcept = new CVConcept();
		newCVConcept.loadSkeleton(newCVConcept.getDefaultDialect());
		newCVConcept.createId();
		newCVConcept.setContainerId( cvScheme.getContainerId());

		DialogAddCodeWindow dialogAddCodeWindow = new DialogAddCodeWindow(
				eventBus, stardatDDIService, vocabularyService, versionService, codeService, 
				conceptService, cvScheme, newCVConcept, null, vocabulary, currentVersion, 
				new CodeDTO(), null, new ConceptDTO(), i18n, locale);
		getUI().addWindow(dialogAddCodeWindow);
	}
	
	private void doEditCode(ClickEvent event ) {
		if( !SecurityUtils.isCurrentUserAllowEditCv( agency , selectedLanguage) )
			return;
		
		Window window = new DialogEditCodeWindow(
				eventBus, stardatDDIService, vocabularyService, codeService, conceptService, cvScheme, 
				cvConcept, selectedLanguage, vocabulary, currentVersion, currentCode, 
				currentConcept, i18n, locale);
		
		getUI().addWindow(window);
	}
	
	private void doCodeAddTranslation(ClickEvent event ) {
		Window windowTranslate = new DialogTranslateCodeWindow(
				eventBus, stardatDDIService, vocabularyService, versionService, codeService, conceptService, cvScheme, 
				cvConcept, selectedLanguage, sourceLanguage, vocabulary, currentVersion, currentCode, 
				new ConceptDTO(), i18n, locale);
		getUI().addWindow( windowTranslate );
	}
	
	private void doCodeAddChild(ClickEvent event ) {
		CVConcept newCVConcept = new CVConcept();
		newCVConcept.loadSkeleton(newCVConcept.getDefaultDialect());
		newCVConcept.createId();
		newCVConcept.setContainerId( cvScheme.getContainerId());
		
		DialogAddCodeWindow dialogAddChildCodeWindow = new DialogAddCodeWindow(
				eventBus, stardatDDIService, vocabularyService, versionService, codeService, 
				conceptService, cvScheme, newCVConcept, cvConcept, vocabulary, currentVersion, 
				new CodeDTO(), currentCode, new ConceptDTO(), i18n, locale);
		getUI().addWindow( dialogAddChildCodeWindow );
	}
	
	private void doDeleteCode(ClickEvent event ) {
		eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_DELETED, null) );
	}
	
	private void doSortCode(ClickEvent event ) {
		enableSort = !enableSort;
		buttonCodeSort.withCaption( enableSort ? "Disable order code" : "Enable order code" );
		eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_SORT, enableSort) );
	}
	
	public void clearCode() {
		setCvConcept( null );
		setCurrentCode( null );
		setCurrentConcept( null );
	}
	
	@Override
	public void updateMessageStrings(Locale locale) {
		buttonCodeAdd.withCaption( i18n.get("view.action.button.cvconcept.new", locale));
		buttonCodeEdit.withCaption( "Edit Code" );
		buttonCodeAddTranslation.withCaption( i18n.get("view.action.button.cvconcept.translation", locale));
		buttonCodeAddChild.withCaption( "Add Child" );
		buttonCodeDelete.withCaption( i18n.get("view.action.button.cvconcept.delete", locale));
		buttonCodeSort.withCaption( enableSort ? "Disable order code" : "Enable order code" );
	}

	public CVScheme getCvScheme() {
		return cvScheme;
	}

	public void setCvScheme(CVScheme cvScheme) {
		this.cvScheme = cvScheme;
	}

	public CVConcept getCvConcept() {
		return cvConcept;
	}

	public void setCvConcept(CVConcept cvConcept) {
		this.cvConcept = cvConcept;
	}

	public VersionDTO getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(VersionDTO currentVersion) {
		this.currentVersion = currentVersion;
	}

	public ConceptDTO getCurrentConcept() {
		return currentConcept;
	}

	public void setCurrentConcept(ConceptDTO currentConcept) {
		this.currentConcept = currentConcept;
	}
	
	public VocabularyDTO getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(VocabularyDTO vocabulary) {
		this.vocabulary = vocabulary;
	}
	
	public boolean hasActionRight() {
		
		boolean hasAction = false;
		if( !SecurityUtils.isAuthenticated() || currentVersion.getStatus().equals( Status.PUBLISHED.toString() )) {
			setVisible( false );
		} else {
			hasAction = true;

			if( selectedLanguage.equals( sourceLanguage) && SecurityUtils.isCurrentUserAllowCreateCvSl() ) {
				buttonCodeAdd.setVisible( true );
				VersionDTO latestSLVersion = VersionDTO.getLatestSourceVersion( vocabulary.getLatestVersions());
				if( latestSLVersion.getConcepts() != null && latestSLVersion.getConcepts().size() > 1 )
					buttonCodeSort.setVisible( true );
				else
					buttonCodeSort.setVisible( false );
			}
			else {
				buttonCodeAdd.setVisible( false );
				buttonCodeSort.setVisible( false );
			}
			
			if( cvConcept != null && currentCode != null && currentConcept != null) {
				if( SecurityUtils.isCurrentUserAllowEditCv( agency , selectedLanguage)) {
					buttonCodeEdit.setVisible( true );
					buttonCodeAddTranslation.setVisible( false );
				}else {
					buttonCodeEdit.setVisible( false );
				}
				
				if( selectedLanguage.equals( sourceLanguage) ) {
					if( SecurityUtils.isCurrentUserAllowEditCv( agency , selectedLanguage)) {
						buttonCodeAddChild.setVisible( true );
						buttonCodeDelete.setVisible( true );
					}else {
						buttonCodeAddChild.setVisible( false );
						buttonCodeDelete.setVisible( false );
					}
				}
			} else if( cvConcept != null && currentCode != null && currentConcept == null) {
				if( SecurityUtils.isCurrentUserAllowEditCv( agency , selectedLanguage)) {
					buttonCodeAddTranslation.setVisible( true );
					buttonCodeEdit.setVisible( false );
				}else {
					buttonCodeAddTranslation.setVisible( false );
				}
				
				if( selectedLanguage.equals( sourceLanguage) ) {
					if( SecurityUtils.isCurrentUserAllowEditCv( agency , selectedLanguage)) {
						buttonCodeAddChild.setVisible( true );
						buttonCodeDelete.setVisible( true );
					}else {
						buttonCodeAddChild.setVisible( false );
						buttonCodeDelete.setVisible( false );
					}
				}
			} else {
				buttonCodeEdit.setVisible( false );
				buttonCodeAddTranslation.setVisible( false );
				buttonCodeAddChild.setVisible( false );
				buttonCodeDelete.setVisible( false );
			}
		}
		
		
		
		return hasAction;
	}

	public CodeDTO getCurrentCode() {
		return currentCode;
	}

	public void setCurrentCode(CodeDTO currentCode) {
		this.currentCode = currentCode;
	}

	public Language getSelectedLanguage() {
		return selectedLanguage;
	}

	public void setSelectedLanguage(Language selectedLanguage) {
		this.selectedLanguage = selectedLanguage;
	}
	
	public Language getSourceLanguage() {
		return sourceLanguage;
	}

	public void setSourceLanguage(Language sourceLanguage) {
		this.sourceLanguage = sourceLanguage;
	}

	public AgencyDTO getAgency() {
		return agency;
	}

	public void setAgency(AgencyDTO agency) {
		this.agency = agency;
	}
	
}
