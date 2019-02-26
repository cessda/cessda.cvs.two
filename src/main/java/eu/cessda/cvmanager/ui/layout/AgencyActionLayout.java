package eu.cessda.cvmanager.ui.layout;

import java.util.Locale;

import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.RoleService;
import org.gesis.wts.service.UserAgencyService;
import org.gesis.wts.service.UserService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import eu.cessda.cvmanager.service.LicenceService;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.component.ResponsiveBlock;
import eu.cessda.cvmanager.ui.view.window.DialogAgencyManageMember;
import eu.cessda.cvmanager.ui.view.window.DialogAgencyManageProfile;

public class AgencyActionLayout extends ResponsiveBlock{
	private static final long serialVersionUID = 2436346372920594014L;
	
	private final UserService userService;
	private final RoleService roleService;
	private final AgencyService agencyService;
	private final UserAgencyService userAgencyService;
	private final LicenceService licenceService;
	
	private AgencyDTO agency;
	private VocabularyDTO vocabulary;
		
	private I18N i18n;
	private Locale locale = UI.getCurrent().getLocale();
	private final UIEventBus eventBus;
	
	
	private Language selectedLanguage;
	private Language sourceLanguage;
	private VersionDTO currentVersion;
	
	private MButton buttonManageMember = new MButton("Manage member");
	private MButton buttonManageProfile = new MButton("Manage profile");
		
	
	public AgencyActionLayout(String titleHeader, String showHeader, I18N i18n, UIEventBus eventBus, 
			AgencyDTO agency, UserService userService, RoleService roleService, AgencyService agencyService,
			LicenceService licenceService, UserAgencyService userAgencyService) {
		super(titleHeader, showHeader, i18n);
		this.i18n = i18n;
		this.eventBus = eventBus;
		this.agency = agency;
		this.userService = userService;
		this.roleService = roleService;
		this.licenceService = licenceService;
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
		Window window = new DialogAgencyManageProfile(eventBus, agency, agencyService, licenceService, i18n, locale);
		getUI().addWindow(window);
	}
	
	@Override
	public void updateMessageStrings(Locale locale) {
		buttonManageMember.withCaption( i18n.get( "block.action.agency.manageMember" ) );
		buttonManageProfile.withCaption( i18n.get( "block.action.agency.manageProfile" ));
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
			buttonManageMember.setVisible( false );
			buttonManageProfile.setVisible( false );
			
			if( SecurityUtils.isCurrentUserAllowCreateCvSl(agency)) {
				buttonManageMember.setVisible( true );
				buttonManageProfile.setVisible( true );
			}
			
			if( buttonManageMember.isVisible() ) {
				setVisible( true );
				hasAction = true;
			}
		}
		
		return hasAction;
	}
}
