package eu.cessda.cvmanager.ui.layout;

import java.util.Locale;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import eu.cessda.cvmanager.domain.Vocabulary;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.model.CvItem;
import eu.cessda.cvmanager.repository.search.VocabularySearchRepository;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyChangeService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.mapper.VocabularyMapper;
import eu.cessda.cvmanager.ui.component.ResponsiveBlock;
import eu.cessda.cvmanager.ui.view.CvView;
import eu.cessda.cvmanager.ui.view.DetailView;
import eu.cessda.cvmanager.ui.view.window.DialogAddLanguageWindow;
import eu.cessda.cvmanager.ui.view.window.DialogCVSchemeWindow;
import eu.cessda.cvmanager.ui.view.window.DialogManageStatusWindow;

public class EditorCvActionLayout extends ResponsiveBlock{
	private static final long serialVersionUID = 2436346372920594014L;
	
	private final StardatDDIService stardatDDIService;
	private final AgencyService agencyService;
	private final VocabularyService vocabularyService;
	private final VocabularySearchRepository vocabularySearchRepository;
	private final VersionService versionService;
	private final VocabularyChangeService vocabularyChangeService;
	
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
	private MButton buttonWithdrawnCv = new MButton();
	
	private MButton buttonNewVersion = new MButton();
	private boolean isCurrentSL;
	
	
	public EditorCvActionLayout(String titleHeader, String showHeader, I18N i18n, StardatDDIService stardatDDIService,
			AgencyService agencyService, VocabularyService vocabularyService, VersionService versionService,
			VocabularySearchRepository vocabularySearchRepository, UIEventBus eventBus, 
			VocabularyChangeService vocabularyChangeService) {
		super(titleHeader, showHeader, i18n);
		this.i18n = i18n;
		this.stardatDDIService = stardatDDIService;
//		this.codeService = codeService;
		this.agencyService = agencyService;
		this.versionService = versionService;
		this.vocabularyService = vocabularyService;
		this.vocabularySearchRepository = vocabularySearchRepository;
		this.eventBus = eventBus;
		this.vocabularyChangeService = vocabularyChangeService;
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
				
		getInnerContainer()
			.add(
				buttonAddCv,
				buttonEditCv,
				buttonReviewInitial,
				buttonReviewFinal,
				buttonPublishCv,
				buttonAddTranslation,
				buttonNewVersion
			);
	}

	private void doCvAdd( ClickEvent event ) {
		
		CVScheme newCvScheme = new CVScheme();
		newCvScheme.loadSkeleton(newCvScheme.getDefaultDialect());
		newCvScheme.createId();
		newCvScheme.setContainerId(newCvScheme.getId());
		newCvScheme.setStatus( Status.DRAFT.toString() );

		Window window = new DialogCVSchemeWindow(stardatDDIService, agencyService, vocabularyService, 
				vocabularySearchRepository, newCvScheme, new VocabularyDTO(), new VersionDTO(), agency, i18n, null, 
				eventBus, vocabularyChangeService);
		getUI().addWindow(window);
	}
	
	private void doCvEdit( ClickEvent event ) {
		Window window = null;
		if( sourceLanguage.equals(selectedLanguage))
			window = new DialogCVSchemeWindow(stardatDDIService, agencyService, vocabularyService, vocabularySearchRepository, 
					cvScheme, vocabulary, currentVersion, agency, i18n, selectedLanguage, eventBus, vocabularyChangeService);
		else
			window = new DialogAddLanguageWindow(stardatDDIService, cvScheme, vocabulary, currentVersion, agency, vocabularyService, 
					versionService, vocabularySearchRepository, eventBus, vocabularyChangeService);
		getUI().addWindow(window);
	}
	
	private void doCvAddTranslation(ClickEvent event ) {
		
		Window window = new DialogAddLanguageWindow(stardatDDIService, cvScheme, vocabulary, new VersionDTO(), agency, 
				vocabularyService, versionService, vocabularySearchRepository, eventBus, vocabularyChangeService);
		getUI().addWindow(window);
	}
	
	public void changeStatus(ClickEvent event ) {
		Window window = new DialogManageStatusWindow(stardatDDIService, vocabularyService, versionService, 
				cvScheme, vocabulary, currentVersion, selectedLanguage, sourceLanguage, 
				agency, eventBus, vocabularyChangeService);
		getUI().addWindow(window);
	}
	
	public void publishCv(ClickEvent event ) {
		ConfirmDialog.show( this.getUI(), "Confirm",
				"Are you sure you want to publish " + vocabulary.getNotation() +" SL?", "yes",
				"cancel",
		
					dialog -> {
						if( dialog.isConfirmed() ) {
							
							currentVersion.setStatus( Status.PUBLISHED.toString() );
							vocabulary.setVersionByLanguage(selectedLanguage, Status.PUBLISHED.toString());
							
							if( isCurrentSL) {
								vocabulary.setStatus( Status.PUBLISHED.toString());
							}
							vocabulary.setStatuses( vocabulary.getLatestStatuses() );
							vocabulary.addLanguagePublished( selectedLanguage.toString());
							
							// save to database
							vocabulary = vocabularyService.save(vocabulary);
							
							// index for editor
							vocabularyService.index(vocabulary);
							
							// index for publication
							vocabularyService.indexPublish(vocabulary, currentVersion);
							
							// save to flatDB
							cvScheme.setStatus(Status.PUBLISHED.toString()  );
							getCvScheme().save();
							DDIStore ddiStore = stardatDDIService.saveElement(getCvScheme().ddiStore, SecurityUtils.getCurrentUserLogin().get(), "Publish Cv");
							
							eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVSCHEME_UPDATED, null) );
						}
					}
				);
	}
	
	/**
	 * Create new version based on the selected language in the Vocabulary
	 * @param event
	 */
	private void createNewVersion(ClickEvent event ) {
		// TODO: check vocabulary language type
		ConfirmDialog.show( this.getUI(), "Confirm",
				"Create new version of  " + vocabulary.getNotation() +" SL?", "yes",
				"cancel",
		
					dialog -> {
						if( dialog.isConfirmed() ) {
							String newCvLink = vocabularyService.createNewVersion(vocabulary, cvItem, selectedLanguage);
							
							UI.getCurrent().getNavigator().navigateTo( DetailView.VIEW_NAME + "/" + newCvLink);
						}
					}
				);
	}
	
	@Override
	public void updateMessageStrings(Locale locale) {
		String buttonSuffix = (isCurrentSL ? " SL " : " TL ") + selectedLanguage.toString();
		buttonAddCv.withCaption(i18n.get("view.action.button.cvscheme.new", locale));
		buttonEditCv.withCaption(i18n.get("view.action.button.cvscheme.edit", locale) + buttonSuffix);
		buttonAddTranslation.withCaption( i18n.get("view.action.button.cvscheme.translation", locale));
		buttonReviewInitial.withCaption( "Initial Review" +  buttonSuffix );
		buttonReviewFinal.withCaption( "Final Review" +  buttonSuffix );
		buttonPublishCv.withCaption( "Publish" + buttonSuffix);
		buttonNewVersion.withCaption( "Create new Version ");
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
		if( !SecurityUtils.isAuthenticated() ) {
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
			
			if( sourceLanguage.equals(selectedLanguage))
				isCurrentSL = true;
			else
				isCurrentSL = false;
			
			updateMessageStrings(locale);
			
			setVisible( true );
			hasAction = true;
			// check add CV button
			if( SecurityUtils.isCurrentUserAllowCreateCvSl() )
				buttonAddCv.setVisible( true );
			
			if( currentVersion != null )  {
			
			// check edit CV button
				
				if(currentVersion.getStatus().equals( Status.PUBLISHED.toString() )) {
					if( SecurityUtils.isCurrentUserAllowCreateCvTl(getAgency()) ) {
						buttonAddTranslation.setVisible( true );
						buttonNewVersion.setVisible( true );
					}
				}
				else {
					if( SecurityUtils.isCurrentUserAllowEditCv( agency , selectedLanguage))
						buttonEditCv.setVisible( true );
					
					if( currentVersion.getStatus().equals( Status.DRAFT.toString() )) {
						if( SecurityUtils.isCurrentUserAllowCreateCvSl( agency ) ) {
							buttonReviewInitial.setVisible( true );
						}
					} else if(currentVersion.getStatus().equals( Status.INITIAL_REVIEW.toString() )) {
						if( SecurityUtils.isCurrentUserAllowCreateCvSl( agency ) ) 
							buttonReviewFinal.setVisible( true );
						
					} else if(currentVersion.getStatus().equals( Status.FINAL_REVIEW.toString() )) {
						if( SecurityUtils.isCurrentUserAllowCreateCvSl( agency ) ) 
							buttonPublishCv.setVisible( true );
					}
				}
			}
			
		}
		
		return hasAction;
	}
}
