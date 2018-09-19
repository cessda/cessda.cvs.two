package eu.cessda.cvmanager.ui.view.admin;

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
import org.vaadin.viritin.label.MLabel;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.shared.ui.ContentMode;
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
import eu.cessda.cvmanager.ui.view.admin.AdminView.AdminContent;
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
	
	private final AdminView adminView;

	private I18N i18n;
	private Locale locale = UI.getCurrent().getLocale();
	private final UIEventBus eventBus;

	
	private MButton buttonManageUser = new MButton("Manage User");
	private MButton buttonManageAgency = new MButton("Manage Agency");
	private MButton buttonManageUserAgency = new MButton("Manage User Agency");
	private MButton buttonManageUserRole = new MButton("Manage User Role");
	private MButton buttonManageLicense = new MButton("Manage License");
	private MButton buttonWitdrawnCvs = new MButton("Withdrawn CVs");
	
	public AdminActionLayout(String titleHeader, String showHeader, I18N i18n, UIEventBus eventBus, 
			AdminView adminView, AgencyDTO agency, UserService userService, RoleService roleService, AgencyService agencyService,
			MetadataFieldService metadataFieldService, MetadataValueService metadataValueService,
			UserAgencyService userAgencyService) {
		super(titleHeader, showHeader, i18n);
		this.i18n = i18n;
		this.eventBus = eventBus;
		this.adminView = adminView;
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
		
		buttonManageUserAgency
			.withFullWidth()
			.withStyleName("action-button")
			.withVisible( false )
			.addClickListener( this::doManageUserAgency );
		
		buttonManageUserRole
			.withFullWidth()
			.withStyleName("action-button")
			.withVisible( false )
			.addClickListener( this::doManageUserRole );
		
		buttonManageLicense
			.withFullWidth()
			.withStyleName("action-button")
			.withVisible( false )
			.addClickListener( this::doManageLicense );
		
		buttonWitdrawnCvs
			.withFullWidth()
			.withStyleName("action-button")
			.withVisible( false )
			.addClickListener( this::doWithdrawnList );
		
		getInnerContainer()
			.add(
				buttonManageUser,
				buttonManageAgency,
				buttonManageUserAgency,
				new MLabel("<hr/>").withContentMode( ContentMode.HTML ),
				buttonManageUserRole,
				new MLabel("<hr/>").withContentMode( ContentMode.HTML ),
				buttonManageLicense,
				buttonWitdrawnCvs
			);
	}

	private void doManageUser(ClickEvent event ) {
		adminView.setMainContent( AdminContent.MANAGE_USER );
	}
	
	private void doManageAgency(ClickEvent event ) {
		adminView.setMainContent( AdminContent.MANAGE_AGENCY );
	}
	
	private void doManageUserAgency(ClickEvent event ) {
		adminView.setMainContent( AdminContent.MANAGE_USER_AGENCY );
	}
	
	private void doManageUserRole(ClickEvent event ) {
		adminView.setMainContent( AdminContent.MANAGE_USER_ROLE );
	}
	
	private void doManageLicense(ClickEvent event ) {
		adminView.setMainContent( AdminContent.MANAGE_LICENSE );
	}
	
	private void doWithdrawnList(ClickEvent event ) {
		adminView.setMainContent( AdminContent.LIST_WITHDRAWN_CV );
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
			buttonManageUserAgency.setVisible( true );
			buttonManageUserRole.setVisible( true );
			buttonManageLicense.setVisible( true );
			buttonWitdrawnCvs.setVisible( true );
		}
		
		return hasAction;
	}
}
