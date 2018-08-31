package eu.cessda.cvmanager.ui.layout;

import java.util.Locale;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.RoleService;
import org.gesis.wts.service.UserAgencyService;
import org.gesis.wts.service.UserService;
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
import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.service.MetadataFieldService;
import eu.cessda.cvmanager.service.MetadataValueService;
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
import eu.cessda.cvmanager.ui.view.window.DialogAddLanguageWindowNew;
import eu.cessda.cvmanager.ui.view.window.DialogAgencyManageMember;
import eu.cessda.cvmanager.ui.view.window.DialogAgencyManageProfile;
import eu.cessda.cvmanager.ui.view.window.DialogCVSchemeWindow;
import eu.cessda.cvmanager.ui.view.window.DialogCVSchemeWindowNew;
import eu.cessda.cvmanager.ui.view.window.DialogCreateVersionWindow;
import eu.cessda.cvmanager.ui.view.window.DialogManageStatusWindow;
import eu.cessda.cvmanager.ui.view.window.DialogManageStatusWindowNew;

public class AgencyActionLayout extends ResponsiveBlock{
	private static final long serialVersionUID = 2436346372920594014L;
	
	private final UserService userService;
	private final RoleService roleService;
	private final AgencyService agencyService;
	private final UserAgencyService userAgencyService;
	private final MetadataFieldService metadataFieldService;
	private final MetadataValueService metadataValueService;
	
	private AgencyDTO agency;
	private VocabularyDTO vocabulary;
		
	private I18N i18n;
	private Locale locale = UI.getCurrent().getLocale();
	private final UIEventBus eventBus;
	
	
	private Language selectedLanguage;
	private Language sourceLanguage;
	private VersionDTO currentVersion;
	
	private MButton buttonManageMember = new MButton("Manage Member");
	private MButton buttonManageProfile = new MButton("Manage Profile");
		
	
	public AgencyActionLayout(String titleHeader, String showHeader, I18N i18n, UIEventBus eventBus, 
			AgencyDTO agency, UserService userService, RoleService roleService, AgencyService agencyService,
			MetadataFieldService metadataFieldService, MetadataValueService metadataValueService,
			UserAgencyService userAgencyService) {
		super(titleHeader, showHeader, i18n);
		this.i18n = i18n;
		this.eventBus = eventBus;
		this.agency = agency;
		this.userService = userService;
		this.roleService = roleService;
		this.metadataFieldService = metadataFieldService;
		this.metadataValueService = metadataValueService;
		this.agencyService = agencyService;
		this.userAgencyService = userAgencyService;
		this.i18n = i18n;
		init();
	}

	private void init() {
		buttonManageMember
			.withFullWidth()
			.withStyleName("action-button")
			.withVisible( false )
			.addClickListener( this::doManageMember );
		
		buttonManageProfile
			.withFullWidth()
			.withStyleName("action-button")
			.withVisible( false )
			.addClickListener( this::doManageProfile );
		
		getInnerContainer()
			.add(
				buttonManageMember,
				buttonManageProfile
			);
	}

	private void doManageMember(ClickEvent event ) {
		Window window = new DialogAgencyManageMember(eventBus, agency, userService, roleService, agencyService, userAgencyService, i18n, locale);
		getUI().addWindow(window);
	}
	
	private void doManageProfile(ClickEvent event ) {
		Window window = new DialogAgencyManageProfile(eventBus, agency, agencyService, metadataFieldService, metadataValueService, i18n, locale);
		getUI().addWindow(window);
	}
	
	@Override
	public void updateMessageStrings(Locale locale) {
		buttonManageMember.withCaption( "Manage Member" );
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


	public boolean hasActionRight() {
		
		boolean hasAction = false;
		if( !SecurityUtils.isAuthenticated() || agency == null ) {
			setVisible( false );
		}
		else {
			setVisible( true );
			hasAction = true;
			buttonManageMember.setVisible( true );
			buttonManageProfile.setVisible( true );
			
		}
		
		return hasAction;
	}
}
