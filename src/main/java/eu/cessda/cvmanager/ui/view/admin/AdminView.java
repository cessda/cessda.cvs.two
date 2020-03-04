package eu.cessda.cvmanager.ui.view.admin;

import java.util.Locale;

import javax.annotation.PostConstruct;

import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityService;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.RoleService;
import org.gesis.wts.service.UserAgencyService;
import org.gesis.wts.service.UserService;
import org.gesis.wts.ui.view.LoginView;
import org.gesis.wts.ui.view.admin.layout.ManageUserAgencyLayout;
import org.gesis.wts.ui.view.admin.layout.ManageUserLayout;
import org.gesis.wts.ui.view.admin.layout.ManageUserRoleLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.i18n.I18N;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;

import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.LicenceService;
import eu.cessda.cvmanager.service.MetadataFieldService;
import eu.cessda.cvmanager.service.MetadataValueService;
import eu.cessda.cvmanager.service.ResolverService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.ui.layout.WithdrawnCvLayout;
import eu.cessda.cvmanager.ui.view.admin.layout.AdminActionLayout;
import eu.cessda.cvmanager.ui.view.admin.layout.ManageAgencyLayout;
import eu.cessda.cvmanager.ui.view.admin.layout.ManageLicenseLayout;
import eu.cessda.cvmanager.ui.view.admin.layout.ManageResolverLayout;
import eu.cessda.cvmanager.ui.view.importing.CsvImportLayout;

@UIScope
@SpringView(name = AdminView.VIEW_NAME)
public class AdminView extends CvAdminView {

	private static final Logger log = LoggerFactory.getLogger(AdminView.class);
	private static final long serialVersionUID = 2321835584429770141L;
	public static final String VIEW_NAME = "admin";
	public enum AdminContent { 
		MANAGE_USER, 
		MANAGE_AGENCY,
		MANAGE_USER_AGENCY,
		MANAGE_USER_ROLE, 
		MANAGE_LICENSE,
		LIST_WITHDRAWN_CV, 
		MANAGE_RESOLVER
	}

	//	Autowired
	private final UserService userService;
	private final AgencyService agencyService;
	private final RoleService roleService;
	private final UserAgencyService userAgencyService;
	private final MetadataFieldService metadataFieldService;
	private final MetadataValueService metadataValueService;
	private final VocabularyService vocabularyService;
	private final LicenceService licenceService;
	private final BCryptPasswordEncoder encrypt;
	private final ResolverService resolverService;
	
	private AdminActionLayout adminActionLayout;
	
	public AdminView(I18N i18n, EventBus.UIEventBus eventBus, ConfigurationService configService,
			SecurityService securityService, UserService userService, RoleService roleService, 
			AgencyService agencyService, LicenceService licenceService, VocabularyService vocabularyService,
			MetadataFieldService metadataFieldService, MetadataValueService metadataValueService,
			UserAgencyService userAgencyService, BCryptPasswordEncoder encrypt,
			ResolverService resolverService) {
		super(i18n, eventBus, configService, securityService, agencyService, AdminView.VIEW_NAME);
		this.userService = userService;
		this.roleService = roleService;
		this.agencyService = agencyService;
		this.licenceService = licenceService;
		this.userAgencyService = userAgencyService;
		this.metadataFieldService = metadataFieldService;
		this.metadataValueService = metadataValueService;
		this.vocabularyService = vocabularyService;
		this.encrypt = encrypt;
		this.resolverService = resolverService;
		
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
		
		topMenuButtonUpdateActive(3);
		
		if ( event.getParameters() != null )
		{
			try
			{
				String uriPath = event.getParameters();
				String[] path = uriPath.split("\\?")[0].split("/");
				
				if( path.length > 0 && !path[0].isEmpty()) { // if contains agency Id
					setMainContent (path[0]);
				} else {
					setMainContent(AdminContent.MANAGE_USER);
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
				log.error(e.getMessage(), e);
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
			case MANAGE_AGENCY:
				mainContainer.add( new ManageAgencyLayout(i18n, agencyService, licenceService) );
				break;
			case MANAGE_USER_AGENCY:
				mainContainer.add( new ManageUserAgencyLayout(i18n, userAgencyService, userService, agencyService) );
				break;
			case MANAGE_USER_ROLE:
				mainContainer.add( new ManageUserRoleLayout(i18n, userService, roleService));
				break;
			case MANAGE_LICENSE:
				mainContainer.add( new ManageLicenseLayout(i18n, licenceService));
				break;
			case LIST_WITHDRAWN_CV:
				mainContainer.add( new WithdrawnCvLayout(i18n, vocabularyService, agencyService, configService));
				break;
			case MANAGE_RESOLVER:
				mainContainer.add( new ManageResolverLayout(i18n, resolverService ));
				break;
			default:
				mainContainer.add( new ManageUserLayout(i18n, userService, encrypt) );
		}
	}
	
}
