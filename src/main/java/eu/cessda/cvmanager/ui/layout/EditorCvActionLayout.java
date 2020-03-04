package eu.cessda.cvmanager.ui.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import eu.cessda.cvmanager.service.manager.WorkflowManager;
import eu.cessda.cvmanager.service.manager.WorkspaceManager;
import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.model.CvItem;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.LicenceService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyChangeService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.component.ResponsiveBlock;
import eu.cessda.cvmanager.ui.view.PublicationDetailsView;
import eu.cessda.cvmanager.ui.view.EditorDetailsView;
import eu.cessda.cvmanager.ui.view.EditorSearchView;
import eu.cessda.cvmanager.ui.view.window.DialogAddLanguageWindow;
import eu.cessda.cvmanager.ui.view.window.DialogCVSchemeWindow;
import eu.cessda.cvmanager.ui.view.window.DialogCreateVersionWindow;
import eu.cessda.cvmanager.ui.view.window.DialogManageStatusWindow;
import eu.cessda.cvmanager.utils.CvManagerSecurityUtils;

public class EditorCvActionLayout extends ResponsiveBlock{
	private static final long serialVersionUID = 2436346372920594014L;

	private final transient WorkspaceManager workspaceManager;
	private final transient WorkflowManager workflowManager;
	private final AgencyService agencyService;
	private final CodeService codeService;
	private final ConceptService conceptService;
	private final ConfigurationService configService;
	private final VocabularyService vocabularyService;
	private final VersionService versionService;
	private final VocabularyChangeService vocabularyChangeService;
	private final LicenceService licenceService;
	
	private AgencyDTO agency;
	private VocabularyDTO vocabulary;
		
	private I18N i18n;
	private Locale locale = UI.getCurrent().getLocale();
	private final UIEventBus eventBus;
	
	
	private Language selectedLanguage;
	private Language sourceLanguage;
	private VersionDTO currentVersion;
	
	private CVScheme cvScheme;
	private CvItem cvItem;
	
	private MButton buttonAddCv = new MButton();
	private MButton buttonEditCv = new MButton();
	private MButton buttonAddTranslation = new MButton();
	
	private MButton buttonReviewInitial= new MButton();
	private MButton buttonReviewFinal = new MButton("Final Review");
	private MButton buttonPublishCv = new MButton();
	private MButton buttonWithdrawCv = new MButton();
	
	private MLabel separatorLabel = new MLabel("<hr style=\"margin:5px\"/>").withContentMode( ContentMode.HTML );
	
	private MButton buttonNewVersion = new MButton();
	private MButton buttonDropVersion = new MButton();
	private boolean isCurrentSL;
	
	
	public EditorCvActionLayout(String titleHeader, String showHeader, WorkspaceManager workspaceManager, WorkflowManager workflowManager, I18N i18n,
								AgencyService agencyService, VocabularyService vocabularyService, VersionService versionService,
								ConceptService conceptService, CodeService codeService, ConfigurationService configService,
								UIEventBus eventBus, VocabularyChangeService vocabularyChangeService, LicenceService licenceService) {
		super(titleHeader, showHeader, i18n);
		this.workspaceManager = workspaceManager;
		this.workflowManager = workflowManager;
		this.i18n = i18n;
		this.codeService = codeService;
		this.conceptService = conceptService;
		this.configService = configService;
		this.agencyService = agencyService;
		this.versionService = versionService;
		this.vocabularyService = vocabularyService;
		this.eventBus = eventBus;
		this.vocabularyChangeService = vocabularyChangeService;
		this.licenceService = licenceService;
		init();
	}

	private void init() {
		buttonAddCv
			.withFullWidth()
			.withStyleName("action-button")
			.withVisible( false )
			.addClickListener( this::doCvAdd );
		
		buttonEditCv
			.withFullWidth()
			.withStyleName("action-button")
			.withVisible( false )
			.addClickListener( this::doCvEdit );
		
		buttonAddTranslation
			.withFullWidth()
			.withStyleName("action-button")
			.withVisible( false )
			.addClickListener( this::doCvAddTranslation );
		
		buttonReviewInitial
			.withFullWidth()
			.withStyleName("action-button")
			.withVisible( false )
			.addClickListener( this::changeStatus );
		
		buttonReviewFinal
			.withFullWidth()
			.withStyleName("action-button")
			.withVisible( false )
			.addClickListener( this::changeStatus );
		
		buttonPublishCv
			.withFullWidth()
			.withStyleName("action-button")
			.withVisible( false )
			.addClickListener( this::changeStatus );
		
		buttonNewVersion
			.withFullWidth()
			.withStyleName("action-button")
			.withVisible( false )
			.addClickListener( this::createNewVersion );
		
		buttonDropVersion
			.withFullWidth()
			.withStyleName("action-button", "danger-button")
			.withVisible( false )
			.addClickListener( this::dropVersion );
		
		buttonWithdrawCv
			.withFullWidth()
			.withStyleName("action-button", "danger-button")
			.withVisible( false )
			.addClickListener( this::withdrawVocabulary );
		
		separatorLabel.withFullWidth().setVisible( false );
				
		getInnerContainer()
			.add(
				buttonAddCv,
				buttonEditCv,
				buttonReviewInitial,
				buttonReviewFinal,
				buttonPublishCv,
				buttonAddTranslation,
				buttonNewVersion,
				separatorLabel,
				buttonDropVersion,
				buttonWithdrawCv
			);
		
	}

	private void doCvAdd( ClickEvent event ) {
		Window window = new DialogCVSchemeWindow(workspaceManager, i18n, agencyService, VocabularyDTO.createDraft(),
				VersionDTO.createDraft(), agency, eventBus
		);
		getUI().addWindow(window);
	}
	
	private void doCvEdit( ClickEvent event ) {
		Window window = null;
		if( sourceLanguage.equals(selectedLanguage))
			window = new DialogCVSchemeWindow(workspaceManager, i18n, agencyService, vocabulary, currentVersion, agency, eventBus
			);
		else
			window = new DialogAddLanguageWindow(workspaceManager, agency, vocabulary, currentVersion, eventBus);
		getUI().addWindow(window);
	}
	
	private void doCvAddTranslation(ClickEvent event ) {
		
		Window window = new DialogAddLanguageWindow(workspaceManager, agency, vocabulary, VersionDTO.createDraft(), eventBus);
		getUI().addWindow(window);
	}
	
	public void changeStatus(ClickEvent event ) {
		Window window = new DialogManageStatusWindow(conceptService, versionService, 
				vocabulary, currentVersion, selectedLanguage, sourceLanguage,
				agency, eventBus, vocabularyChangeService, licenceService, workflowManager);
		getUI().addWindow(window);
	}
	
	/**
	 * Create new version based on the selected language in the Vocabulary
	 * @param event
	 */
	private void createNewVersion(ClickEvent event ) {
		Window window = new DialogCreateVersionWindow(codeService, conceptService, vocabularyService, versionService, cvScheme,
				vocabulary, currentVersion, selectedLanguage, sourceLanguage, agency, eventBus, vocabularyChangeService);
		getUI().addWindow(window);
	}
	
	//TODO: review drop version
	/**
	 * Drop the current workflow (unpublish) version
	 * @param event
	 */
	private void dropVersion(ClickEvent event ) {
		ConfirmDialog.show( this.getUI(), "Confirm",
				i18n.get( "dialog.version.drop.content", 
						"\"" + currentVersion.getNotation() + "\" (" + currentVersion.getLanguage().toUpperCase() + ") ("+ currentVersion.getStatus() +")"),
				i18n.get("dialog.button.yes"),
				i18n.get("dialog.button.cancel"),
				
			dialog -> {
				if( dialog.isConfirmed() ) {
					// normalize the code
					if( !currentVersion.isInitialVersion()) { // there is already exist published version
						removeNonInitialVersion();
					} else { // initial version
						if( currentVersion.getItemType().equals( ItemType.SL.toString())) {
							// remove codes on vocabulary
							for( CodeDTO code : codeService.findByVocabulary( vocabulary.getId()))
								codeService.delete(code);
							
							// remove all concepts
							for(ConceptDTO c : currentVersion.getConcepts()) {
								conceptService.delete( c.getId());
							}
							// remove version
							vocabulary.removeVersion(currentVersion);
							versionService.delete( currentVersion.getId());

							// remove  vocabulary in DB
							vocabularyService.delete( vocabulary.getId());
							UI.getCurrent().getNavigator().navigateTo( EditorSearchView.VIEW_NAME );
						} else {
							// clear workflow codes in certain language
							List<CodeDTO> workflowCodes = codeService.findWorkflowCodesByVocabulary( vocabulary.getId());
							for(CodeDTO wfc : workflowCodes) {
								wfc.setTitleDefinition(null, null, Language.getByIso( currentVersion.getLanguage()), true);
								codeService.save(wfc);
							}
							
							// remove all concepts
							for(ConceptDTO c : currentVersion.getConcepts()) {
								conceptService.delete( c.getId());
							}
							// remove version
							vocabulary.removeVersion(currentVersion);
							versionService.delete( currentVersion.getId());
							
							vocabulary.getLanguages().remove( currentVersion.getLanguage());
							vocabulary.getLanguagesPublished().remove( currentVersion.getLanguage());
							vocabulary = vocabularyService.save(vocabulary);
							
							// reindex editor search
							vocabularyService.index(vocabulary);
							
							eventBus.publish(EventScope.UI, EditorDetailsView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVSCHEME_UPDATED, null) );
							UI.getCurrent().getNavigator().navigateTo( PublicationDetailsView.VIEW_NAME + "/" + vocabulary.getNotation());
						}
					}
					
					
				}
			}

		);
	}

	private void removeNonInitialVersion() {
		List<CodeDTO> workflowCodes = codeService.findWorkflowCodesByVocabulary( vocabulary.getId());
		boolean reindexPublication = false;
		String currentLanguage = currentVersion.getLanguage();

		// if deleted code is SL
		if( currentVersion.getItemType().equals( ItemType.SL.toString())) {
			reindexPublication = true;
			// delete TLs
			removeAllTlVersions();
			//replace current workflow codes with new ones created from previous version codes
			replaceWorkflowCodes(workflowCodes);


			// remove all concepts
			for(ConceptDTO c : currentVersion.getConcepts()) {
				conceptService.delete( c.getId());
			}
			// remove version
			vocabulary.removeVersion(currentVersion);
			versionService.delete( currentVersion.getId());

			// get latest SL and update vocabulary URI
			vocabulary.getLatestSlVersion(true)
					.ifPresent( v -> {
						vocabulary.setUri( v.getUri());
						vocabulary.setVersionNumber( v.getNumber() );
					});

			// update published language
			updatePublishedLanguages();

			// update editor langauge
			vocabulary.getLanguages().clear();
			vocabulary.addLanguage( currentLanguage );
			vocabulary = vocabularyService.save(vocabulary);

			// reindex editor search
			vocabularyService.index(vocabulary);

			if(reindexPublication)
				vocabularyService.indexPublish(vocabulary, null);

			eventBus.publish(EventScope.UI, EditorDetailsView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVSCHEME_UPDATED, null) );
			UI.getCurrent().getNavigator().navigateTo( PublicationDetailsView.VIEW_NAME + "/" + vocabulary.getNotation());
		}
		else // TL version which is not initial version
		{
			// reindex publication if to be deleted code has already published
			if( currentVersion.getStatus().equals(Status.PUBLISHED.toString()))
				reindexPublication = true;

			// remove all concepts
			for(ConceptDTO c : currentVersion.getConcepts()) {
				conceptService.delete( c.getId());
			}
			// remove version
			vocabulary.removeVersion(currentVersion);
			versionService.delete( currentVersion.getId());


			// clear the code in certain language
			for(CodeDTO eachCode : workflowCodes) {
				eachCode.setTitleDefinition( null, null, currentLanguage);
				codeService.save( eachCode );
			}

			// update published language
			updatePublishedLanguages();

			// replace the content of workflowCodes in certain language
			Optional<VersionDTO> latestVersionByLanguage = vocabulary.getLatestVersionByLanguage(currentLanguage);
			Map<String, CodeDTO> codeMap = CodeDTO.getCodeAsMap(workflowCodes);
			if( latestVersionByLanguage.isPresent() ) {
				for (ConceptDTO concept : latestVersionByLanguage.get().getConcepts()) {
					CodeDTO code = codeMap.get(concept.getNotation());
					if (code == null)
						continue;
					code.setTitleDefinition(concept.getTitle(), concept.getDefinition(), currentVersion.getLanguage());
					codeService.save(code);
				}
			}

			// reindex editor search
			vocabularyService.index(vocabulary);
			if(reindexPublication)
				vocabularyService.indexPublish(vocabulary, null);

			eventBus.publish(EventScope.UI, EditorDetailsView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVSCHEME_UPDATED, null) );
			UI.getCurrent().getNavigator().navigateTo( PublicationDetailsView.VIEW_NAME + "/" + vocabulary.getNotation());
		}
	}

	private void updatePublishedLanguages() {
		Set<String> publishedLangs = vocabulary.getLatestVersions( Status.PUBLISHED.toString() )
			.stream()
			.map( VersionDTO::getLanguage )
			.collect( Collectors.toSet());
		vocabulary.setLanguagesPublished(publishedLangs);
		vocabulary = vocabularyService.save(vocabulary);
	}

	private void removeAllTlVersions() {
		for(VersionDTO eachTLversion: versionService.getTLVersionBySLVersion(currentVersion)) {
			// remove all concepts
			for(ConceptDTO c : eachTLversion.getConcepts()) {
				conceptService.delete( c.getId());
			}
			// remove version
			versionService.delete( eachTLversion.getId() );
			vocabulary.removeVersion(eachTLversion);
		}
	}

	private void replaceWorkflowCodes(List<CodeDTO> workflowCodes) {
		// remove workflow codes on vocabulary
		for( CodeDTO code : workflowCodes )
			codeService.delete(code);
		// if SL is already published set of published code need to be removed as well
		if( currentVersion.getStatus().equals( Status.PUBLISHED.toString() )){
			List<CodeDTO> toDeletePublishedCodes = codeService.findByVocabularyAndVersion( vocabulary.getId(), currentVersion.getId());
			for( CodeDTO code : toDeletePublishedCodes )
				codeService.delete(code);
		}
		// create new workflow code from previous version
		List<CodeDTO> latestSLcodes = codeService.findByVocabularyAndVersion( vocabulary.getId(), currentVersion.getPreviousVersion());
		Set<CodeDTO> newWorkflowCodes = new LinkedHashSet<>();
		for(CodeDTO eachCode : latestSLcodes) {
			CodeDTO newCode = CodeDTO.clone(eachCode);
			newCode.setArchived( false );
			newCode = codeService.save( newCode);
			newWorkflowCodes.add(newCode);
		}
		vocabulary.setCodes(newWorkflowCodes);
	}

	private void withdrawVocabulary(ClickEvent event ) {
		ConfirmDialog.show( this.getUI(), "Confirm",
		"Are you sure you want to withdraw the CV \"" + currentVersion.getNotation() + "\" ("+ currentVersion.getNumber() +")" +"?", "yes",
		"cancel",
				
			dialog -> {
				if( dialog.isConfirmed() ) {
					vocabularyService.withdraw(vocabulary);
					UI.getCurrent().getNavigator().navigateTo( EditorSearchView.VIEW_NAME );
				}
			}

		);
	}
	
	@Override
	public void updateMessageStrings(Locale locale) {
		String buttonSuffix = (isCurrentSL ? " SL " : " TL ") + selectedLanguage.getIso();
		buttonAddCv.withCaption(i18n.get("view.action.button.cvscheme.new", locale));
		buttonEditCv.withCaption(i18n.get("view.action.button.cvscheme.edit", locale) + buttonSuffix);
		buttonAddTranslation.withCaption( i18n.get("view.action.button.cvscheme.translation", locale));
		buttonReviewInitial.withCaption( "Initial review" +  buttonSuffix );
		buttonReviewFinal.withCaption( "Final review" +  buttonSuffix );
		buttonPublishCv.withCaption( "Publish" + buttonSuffix);
		buttonNewVersion.withCaption( "Create new version ");
		buttonDropVersion.withCaption( "Drop version" );
		buttonWithdrawCv.withCaption( "Withdraw vocabulary" );
	}

	public AgencyDTO getAgency() {
		return agency;
	}

	public void setAgency(AgencyDTO agency) {
		this.agency = agency;
	}

	public VocabularyDTO getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(VocabularyDTO vocabulary) {
		this.vocabulary = vocabulary;
	}

	public Language getSelectedLanguage() {
		return selectedLanguage;
	}

	public void setSelectedLanguage(Language selectedLanguage) {
		this.selectedLanguage = selectedLanguage;
	}

	public CVScheme getCvScheme() {
		return cvScheme;
	}

	public void setCvScheme(CVScheme cvScheme) {
		this.cvScheme = cvScheme;
	}

	public MButton getButtonAddCv() {
		return buttonAddCv;
	}

	public MButton getButtonEditCv() {
		return buttonEditCv;
	}

	public MButton getButtonAddTranslation() {
		return buttonAddTranslation;
	}
	
	public Language getSourceLanguage() {
		return sourceLanguage;
	}

	public void setSourceLanguage(Language sourceLanguage) {
		this.sourceLanguage = sourceLanguage;
	}
	
	public VersionDTO getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(VersionDTO currentVersion) {
		this.currentVersion = currentVersion;
	}
	
	public CvItem getCvItem() {
		return cvItem;
	}

	public void setCvItem(CvItem cvItem) {
		this.cvItem = cvItem;
	}

	public boolean hasActionRight() {
		
		boolean hasAction = false;
		if( !CvManagerSecurityUtils.isAuthenticated() ) {
			setVisible( false );
		}
		else {
			buttonAddCv.setVisible( false );
			buttonEditCv.setVisible( false );
			buttonReviewInitial.setVisible( false );
			buttonReviewFinal.setVisible( false );
			buttonPublishCv.setVisible( false );
			buttonAddTranslation.setVisible( false );
			buttonNewVersion.setVisible( false );
			separatorLabel.setVisible( false );
			buttonDropVersion.setVisible( false );
			buttonWithdrawCv.setVisible( false );

			isCurrentSL = sourceLanguage.equals( selectedLanguage );
			
			updateMessageStrings(locale);
			
			// check add CV button
			if( CvManagerSecurityUtils.isCurrentUserAllowCreateCvSl() )
				buttonAddCv.setVisible( true );
			
			if( currentVersion == null )
				return false;
			
			// check edit CV button
			if(currentVersion.getStatus().equals( Status.PUBLISHED.toString() )) {
				List<Language> availableLanguages = new ArrayList<>();
				Set<Language> userLanguages = new HashSet<>();
				if( CvManagerSecurityUtils.isCurrentUserAllowCreateCvSl( getAgency())) {
					buttonAddCv.setVisible( true );
					if( currentVersion.getItemType().equals(ItemType.SL.toString())) {
						buttonNewVersion.setVisible( true );
						separatorLabel.setVisible( true );
						buttonWithdrawCv.setVisible( true );
					}
					if( SecurityUtils.isCurrentUserAgencyAdmin( agency)) {
						userLanguages.addAll( Arrays.asList( Language.values() ) );
						
						availableLanguages = Language.getFilteredLanguage(userLanguages, vocabulary.getLanguages());
						
						Language sourceLang = Language.getByIso( vocabulary.getSourceLanguage() );
						// remove with sourceLanguage option if exist
						availableLanguages.remove( sourceLang );
						
						if(!availableLanguages.isEmpty() && currentVersion.getItemType().equals(ItemType.SL.toString()))
							buttonAddTranslation.setVisible( true );
						
						buttonDropVersion.setVisible( true );
					}
					
				} 
				
				if( CvManagerSecurityUtils.isCurrentUserAllowCreateCvTl(getAgency()) ) {
					

					SecurityUtils.getCurrentUserLanguageTlByAgency( agency ).ifPresent( userLanguages::addAll );
					availableLanguages = Language.getFilteredLanguage( new HashSet<>( userLanguages ), vocabulary.getLanguages());
					
					Language sourceLang = Language.getByIso( vocabulary.getSourceLanguage() );
					// remove with sourceLanguage option if exist
					availableLanguages.remove( sourceLang );
					
					if(!availableLanguages.isEmpty() && currentVersion.getItemType().equals(ItemType.SL.toString()))
						buttonAddTranslation.setVisible( true );
					
					if( SecurityUtils.isCurrentUserAllowEditCv(agency, Language.getByIso( currentVersion.getLanguage())))
						buttonNewVersion.setVisible( true );
				}
			}
			else {						
				if( CvManagerSecurityUtils.isCurrentUserAllowToManageCv(agency, currentVersion )) {
					separatorLabel.setVisible( true );
					buttonEditCv.setVisible( true );
					buttonDropVersion.setVisible( true );
					if( currentVersion.getStatus().equals( Status.DRAFT.toString() ))
						buttonReviewInitial.setVisible( true );
					else if(currentVersion.getStatus().equals( Status.INITIAL_REVIEW.toString() ))
						buttonReviewFinal.setVisible( true );
					else if(currentVersion.getStatus().equals( Status.FINAL_REVIEW.toString() ))
						buttonPublishCv.setVisible( true );
				}
			}
			
		}
		
		// check if any component visible
		if(buttonAddCv.isVisible() || buttonEditCv.isVisible() ||
				buttonReviewInitial.isVisible() || buttonReviewFinal.isVisible() ||
				buttonPublishCv.isVisible() || buttonAddTranslation.isVisible() ||
				buttonNewVersion.isVisible() || buttonDropVersion.isVisible() ||
				buttonWithdrawCv.isVisible() ) {
			setVisible( true );
			hasAction = true;
		} else {
			setVisible( false );
			hasAction = false;
		}
		
		return hasAction;
	}
}
