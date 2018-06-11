package eu.cessda.cvmanager.ui.layout;

import java.util.Locale;

import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.AgencyService;
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

public class EditorCodeActionLayout extends ResponsiveBlock{
	private static final long serialVersionUID = 2436346372920594014L;
	
	private final StardatDDIService stardatDDIService;
	private final AgencyService agencyService;
	private final VocabularyService vocabularyService;
	private final VersionService versionService;
	private final CodeService codeService;
	
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
	private ConceptDTO currentConcept;
	
	public EditorCodeActionLayout(String titleHeader, String showHeader, I18N i18n, StardatDDIService stardatDDIService,
			AgencyService agencyService, VocabularyService vocabularyService, VersionService versionService,
			CodeService codeService, UIEventBus eventBus) {
		super(titleHeader, showHeader, i18n);
		this.i18n = i18n;
		this.stardatDDIService = stardatDDIService;
		this.agencyService = agencyService;
		this.vocabularyService = vocabularyService;
		this.versionService = versionService;
		this.codeService = codeService;
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

		DialogAddCodeWindow dialogAddCodeWindow1 = new DialogAddCodeWindow(
				eventBus, stardatDDIService, vocabularyService, versionService, codeService, 
				cvScheme, newCVConcept, null, vocabulary, currentVersion, new CodeDTO(), new ConceptDTO(), i18n, locale);
		getUI().addWindow(dialogAddCodeWindow1);
		
//		DialogAddCodeWindow dialogAddCodeWindow1 = new DialogAddCodeWindow(eventBus, stardatDDIService, versionService, cvScheme, newCVConcept, null, getVocabulary(), getAgency(), i18n, UI.getCurrent().getLocale());
//		getUI().addWindow(dialogAddCodeWindow1);
//		eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_ADD_DIALOG, null) );
	}
	
	private void doEditCode(ClickEvent event ) {
		if( currentVersion.getItemType().equals( ItemType.SL.toString())) {
			
		} else {
			// 
		}
//		eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_ADD_DIALOG, null) );
	}
	
	private void doCodeAddTranslation(ClickEvent event ) {
		eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_TRANSLATION_DIALOG, null) );
	}
	
	private void doCodeAddChild(ClickEvent event ) {
		eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_ADDCHILD_DIALOG, null) );
	}
	
	private void doDeleteCode(ClickEvent event ) {
		eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_DELETED, null) );
	}
	
	private void doSortCode(ClickEvent event ) {
		enableSort = !enableSort;
		buttonCodeSort.withCaption( enableSort ? "Disable order code" : "Enable order code" );
		eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_SORT, enableSort) );
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
		if( !SecurityUtils.isAuthenticated() ) {
			setVisible( false );
		}
		
		if( currentVersion.getStatus().equals( Status.PUBLISHED.toString()) ) {
			setVisible( false );
		} else {
			hasAction = true;
		}
		
		return hasAction;
	}
	
}
