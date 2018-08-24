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
import eu.cessda.cvmanager.ui.view.AdminView;
import eu.cessda.cvmanager.ui.view.CvView;
import eu.cessda.cvmanager.ui.view.DetailView;
import eu.cessda.cvmanager.ui.view.AdminView.AdminContent;
import eu.cessda.cvmanager.ui.view.window.DialogAddLanguageWindow;
import eu.cessda.cvmanager.ui.view.window.DialogAddLanguageWindowNew;
import eu.cessda.cvmanager.ui.view.window.DialogAgencyManageMember;
import eu.cessda.cvmanager.ui.view.window.DialogAgencyManageProfile;
import eu.cessda.cvmanager.ui.view.window.DialogCVSchemeWindow;
import eu.cessda.cvmanager.ui.view.window.DialogCVSchemeWindowNew;
import eu.cessda.cvmanager.ui.view.window.DialogCreateVersionWindow;
import eu.cessda.cvmanager.ui.view.window.DialogManageStatusWindow;
import eu.cessda.cvmanager.ui.view.window.DialogManageStatusWindowNew;

public class AdminActionLayout extends ResponsiveBlock{
	private static final long serialVersionUID = 2436346372920594014L;
	
	private final UserService userService;
	private final RoleService roleService;
	private final AgencyService agencyService;
	private final UserAgencyService userAgencyService;
	private final MetadataFieldService metadataFieldService;
	private final MetadataValueService metadataValueService;
	
	private final AdminView adminView;
	

	private I18N i18n;
	private Locale locale = UI.getCurrent().getLocale();
	private final UIEventBus eventBus;

	
	private MButton buttonManageUser = new MButton("Manage User");
	private MButton buttonManageAgency = new MButton("Manage Agency");
		
	
	public AdminActionLayout(String titleHeader, String showHeader, I18N i18n, UIEventBus eventBus, 
			AdminView adminView, AgencyDTO agency, UserService userService, RoleService roleService, AgencyService agencyService,
			MetadataFieldService metadataFieldService, MetadataValueService metadataValueService,
			UserAgencyService userAgencyService) {
		super(titleHeader, showHeader, i18n);
		this.i18n = i18n;
		this.eventBus = eventBus;
		this.adminView = adminView;
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
		buttonManageUser
			.withFullWidth()
			.withStyleName("action-button")
			.withVisible( false )
			.addClickListener( this::doManageUser );
		
		buttonManageAgency
			.withFullWidth()
			.withStyleName("action-button")
			.withVisible( false )
			.addClickListener( this::doManageAgency );
		
		getInnerContainer()
			.add(
				buttonManageUser,
				buttonManageAgency
			);
	}

	private void doManageUser(ClickEvent event ) {
		adminView.setMainContent( AdminContent.USER_MANAGEMENT );
	}
	
	private void doManageAgency(ClickEvent event ) {
		adminView.setMainContent( AdminContent.AGENCY_MANAGEMENT );
	}
	
	@Override
	public void updateMessageStrings(Locale locale) {
		buttonManageUser.withCaption( "Manage Member" );
	}

	public boolean hasActionRight() {
		
		boolean hasAction = false;
		if( !SecurityUtils.isAuthenticated() && !SecurityUtils.isUserAdmin() ) {
			setVisible( false );
		}
		else {
			hasAction = true;
			buttonManageUser.setVisible( true );
			buttonManageAgency.setVisible( true );
		}
		
		return hasAction;
	}
}
