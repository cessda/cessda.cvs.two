package eu.cessda.cvmanager.ui.view;

import java.util.Locale;

import javax.annotation.PostConstruct;

import org.gesis.wts.security.SecurityService;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.RoleService;
import org.gesis.wts.service.UserAgencyService;
import org.gesis.wts.service.UserService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.gesis.wts.ui.view.LoginView;
import org.gesis.wts.ui.view.admin.layout.ManageAgencyLayout;
import org.gesis.wts.ui.view.admin.layout.ManageUserLayout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Window;

import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.MetadataFieldService;
import eu.cessda.cvmanager.service.MetadataValueService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.ui.layout.AdminActionLayout;
import eu.cessda.cvmanager.ui.layout.AgencyActionLayout;
import eu.cessda.cvmanager.ui.layout.AgencyDetailLayout;
import eu.cessda.cvmanager.ui.layout.AgencyOwnLayout;
import eu.cessda.cvmanager.ui.layout.AgencySearchLayout;
import eu.cessda.cvmanager.ui.view.publication.DiscoveryView;
import eu.cessda.cvmanager.ui.view.publication.PaginationBar;
import eu.cessda.cvmanager.ui.view.window.DialogAgencyManageMember;

@UIScope
@SpringView(name = AdminView.VIEW_NAME)
public class AdminView extends CvAdminView {

	private static final long serialVersionUID = 2321835584429770141L;
	public static final String VIEW_NAME = "admin";
	public enum AdminContent { USER_MANAGEMENT, AGENCY_MANAGEMENT };
	
//	Autowired
	private final UserService userService;
	private final AgencyService agencyService;
	private final RoleService roleService;
	private final UserAgencyService userAgencyService;
	private final MetadataFieldService metadataFieldService;
	private final MetadataValueService metadataValueService;
	private final BCryptPasswordEncoder encrypt;
	
	private AdminActionLayout adminActionLayout;
	
	public AdminView(I18N i18n, EventBus.UIEventBus eventBus, ConfigurationService configService,
			SecurityService securityService, UserService userService, RoleService roleService, 
			AgencyService agencyService, 
			MetadataFieldService metadataFieldService, MetadataValueService metadataValueService,
			UserAgencyService userAgencyService, BCryptPasswordEncoder encrypt) {
		super(i18n, eventBus, configService, securityService, agencyService, AdminView.VIEW_NAME);
		this.userService = userService;
		this.roleService = roleService;
		this.agencyService = agencyService;
		this.userAgencyService = userAgencyService;
		this.metadataFieldService = metadataFieldService;
		this.metadataValueService = metadataValueService;
		this.encrypt = encrypt;
		
		eventBus.subscribe(this, AdminView.VIEW_NAME);
	}

	@PostConstruct
	public void init() {
		LoginView.NAVIGATETO_VIEWNAME = AdminView.VIEW_NAME;
				
		adminActionLayout = new AdminActionLayout("block.action.admin", "block.action.admin.show", i18n, eventBus, this, agency, userService, 
				roleService, agencyService, metadataFieldService, metadataValueService, userAgencyService);
		
		sidePanel
			.add( 
				adminActionLayout
			);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		if ( event.getParameters() != null )
		{
			try
			{
				String uriPath = event.getParameters();
				String[] path = uriPath.split("\\?")[0].split("/");
				
				if( path.length > 0 && !path[0].isEmpty()) { // if contains agency Id
					setMainContent (path[0]);
				} else {
					setMainContent(AdminContent.USER_MANAGEMENT);
				}
				
				if( uriPath.contains( "?" )) {
					// contains specific querystring
					MultiValueMap<String, String> parameters =
				            UriComponentsBuilder.fromUriString( uriPath ).build().getQueryParams();
				}
			}
			catch (Exception e)
			{
				// UI.getCurrent().getNavigator().navigateTo( ErrorView.VIEW_NAME );
				e.printStackTrace();
			}

		}
				
		updateMessageStrings( getLocale() );
	}


	
	@Override
	public void updateMessageStrings(Locale locale) {

	}

	@Override
	public boolean beforeViewChange(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void afterViewChange(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void setMainContent(String layoutName) {
		setMainContent( AdminContent.valueOf( layoutName.toLowerCase().replace("-", "_")));
	}
	
	public void setMainContent(AdminContent adminContent) {
		adminActionLayout.setVisible( adminActionLayout.hasActionRight() );
		mainContainer.removeAllComponents();
		switch(adminContent) {
			case USER_MANAGEMENT:
				mainContainer.add( new ManageUserLayout(i18n, userService, encrypt) );
				break;
			case AGENCY_MANAGEMENT:
				mainContainer.add( new ManageAgencyLayout(i18n, agencyService) );
				break;
			
		}
	}
	
}
