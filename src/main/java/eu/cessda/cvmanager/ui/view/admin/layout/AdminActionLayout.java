package eu.cessda.cvmanager.ui.view.admin.layout;

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
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;

import com.vaadin.ui.UI;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button.ClickEvent;

import eu.cessda.cvmanager.service.MetadataFieldService;
import eu.cessda.cvmanager.service.MetadataValueService;
import eu.cessda.cvmanager.ui.component.ResponsiveBlock;
import eu.cessda.cvmanager.ui.view.admin.AdminView;
import eu.cessda.cvmanager.ui.view.admin.AdminView.AdminContent;

public class AdminActionLayout extends ResponsiveBlock{
	private static final long serialVersionUID = 2436346372920594014L;
	
	private final AdminView adminView;

	private I18N i18n;
	private Locale locale = UI.getCurrent().getLocale();
	private final UIEventBus eventBus;

	
	private MButton buttonManageUser = new MButton("Manage User");
	private MButton buttonManageAgency = new MButton("Manage Agency");
	private MButton buttonManageUserAgency = new MButton("Manage agency role");
	private MButton buttonManageUserRole = new MButton("Manage system role");
	private MButton buttonManageLicense = new MButton("Manage license");
	private MButton buttonWitdrawnCvs = new MButton("Withdrawn CVs");
	private MButton buttonManageResolver = new MButton("Manage Resolver");
	
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
		
		buttonManageResolver
			.withFullWidth()
			.withStyleName("action-button")
			.withVisible( false )
			.addClickListener( this::doManageResolver );
		
		getInnerContainer()
			.add(
				buttonManageUser,
				buttonManageAgency,
				buttonManageUserAgency,
				new MLabel("<hr/>").withContentMode( ContentMode.HTML ),
				buttonManageUserRole,
				new MLabel("<hr/>").withContentMode( ContentMode.HTML ),
				buttonManageLicense,
				new MLabel("<hr/>").withContentMode( ContentMode.HTML ),
				buttonWitdrawnCvs,
				new MLabel("<hr/>").withContentMode( ContentMode.HTML ),
				buttonManageResolver
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
	
	private void doManageResolver(ClickEvent event ) {
		adminView.setMainContent( AdminContent.MANAGE_RESOLVER);
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
			buttonManageResolver.setVisible( true );
		}
		
		return hasAction;
	}
}
