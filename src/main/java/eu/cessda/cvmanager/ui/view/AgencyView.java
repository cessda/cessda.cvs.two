package eu.cessda.cvmanager.ui.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.gesis.wts.security.SecurityService;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.LanguageRightService;
import org.gesis.wts.service.RoleService;
import org.gesis.wts.service.UserAgencyRoleService;
import org.gesis.wts.service.UserAgencyService;
import org.gesis.wts.service.UserService;
import org.gesis.wts.service.VocabularyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.gesis.wts.ui.view.LoginView;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.I18n;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.CvManagerService;
import eu.cessda.cvmanager.ui.component.AgencyGridComponent;
import eu.cessda.cvmanager.ui.component.CvSchemeComponent;
import eu.cessda.cvmanager.ui.layout.AgencyDetailLayout;
import eu.cessda.cvmanager.ui.layout.AgencyOwnLayout;
import eu.cessda.cvmanager.ui.layout.AgencySearchLayout;
import eu.cessda.cvmanager.ui.view.window.DialogAgencyManageMember;
import eu.cessda.cvmanager.ui.view.window.DialogCVSchemeWindow;

@UIScope
@SpringView(name = AgencyView.VIEW_NAME)
public class AgencyView extends CvManagerView {

	private static final long serialVersionUID = -6014026644497372675L;
	public static final String VIEW_NAME = "agency";
	public enum ViewMode { INITIAL, DETAIL };
	
//	Autowired
	private final UserService userService;
	private final AgencyService agencyService;
	private final RoleService roleService;
	private final UserAgencyService userAgencyService;
	private final UserAgencyRoleService userAgencyRoleService;
	private final LanguageRightService languageRightService;
	private final VocabularyService vocabularyService;
	private final CvManagerService cvManagerService;
//	private final User
	
	private MHorizontalLayout mainContent = new MHorizontalLayout();

//	This view compose of 3 layouts:
//	1. Agency own layout
//	2. Agency search layout
//	3. Agency details layout
	private AgencyOwnLayout aOwnLayout;
	private AgencySearchLayout aSearchLayout;
	private AgencyDetailLayout aDetailLayout;
	
	
	private AgencyDTO agency;
	private ViewMode viewMode;
	

	// The opened search hit at the results grid (null at the begining)
	// private SearchHit selectedItem = null;

	public AgencyView(I18N i18n, EventBus.UIEventBus eventBus, ConfigurationService configService,
			CvManagerService cvManagerService, SecurityService securityService, 
			UserService userService, RoleService roleService, AgencyService agencyService, UserAgencyService userAgencyService,
			UserAgencyRoleService userAgencyRoleService, LanguageRightService languageRightService, VocabularyService vocabularyService) {
		super(i18n, eventBus, configService, cvManagerService, securityService, AgencyView.VIEW_NAME);
		this.userService = userService;
		this.roleService = roleService;
		this.agencyService = agencyService;
		this.userAgencyService = userAgencyService;
		this.userAgencyRoleService = userAgencyRoleService;
		this.languageRightService = languageRightService;
		this.vocabularyService = vocabularyService;
		this.cvManagerService = cvManagerService;
		
		aOwnLayout = new AgencyOwnLayout(i18n, eventBus, this, agencyService, configService); 
		aSearchLayout = new AgencySearchLayout(i18n, eventBus, this, agencyService, configService);
		aDetailLayout = new AgencyDetailLayout(i18n, eventBus, this, agencyService, configService, vocabularyService, cvManagerService, configService); 
		
		eventBus.subscribe(this, AgencyView.VIEW_NAME);
	}

	@PostConstruct
	public void init() {
		LoginView.NAVIGATETO_VIEWNAME = AgencyView.VIEW_NAME;
		
		rightContainer
			.add(
				aOwnLayout,
				aSearchLayout, 
				aDetailLayout
			)
			.withExpand(aOwnLayout, 1)
			.withExpand(aSearchLayout, 1)
			.withExpand(aDetailLayout, 1);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		if ( event.getParameters() != null )
		{
			try
			{
				String uriPath = event.getParameters();
				String[] path = uriPath.split("\\?")[0].split("/");
				
				if( path.length > 0 && !path[0].isEmpty()) { // if contains agency Id
					setAgency(agencyService.findOne(Long.parseLong( path[0])), ViewMode.DETAIL);
				} else {
					setAgency( null , ViewMode.INITIAL);
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


	@EventBusListenerMethod( scope = EventScope.UI )
	public void eventHandle( CvManagerEvent.Event event)
	{
		switch(event.getType()) {
			case AGENCY_MANAGE_MEMBER:
				Window window = new DialogAgencyManageMember(eventBus, agency, userService, roleService, agencyService, userAgencyService, userAgencyRoleService, languageRightService, i18n, locale);
				getUI().addWindow(window);
				break;
			default:
				break;
		}
	}
	
	public void setAgency( AgencyDTO agency, ViewMode viewMode) {
		this.agency = agency;
		this.viewMode = viewMode;
		// check user roles here
		if (viewMode.equals( ViewMode.INITIAL )) {
			actionPanel.getButtonManageMember().setVisible( false );
			aDetailLayout.setVisible( false );
			aSearchLayout.setVisible( true );
			
			if( SecurityUtils.isAuthenticated()) {
				aOwnLayout.updateList();
				aOwnLayout.setVisible( true );
			}
			else {
				aOwnLayout.setVisible( false );
			}
			aSearchLayout.updateList();
			
		} else {
			if( SecurityUtils.isCurrentUserInRole( "ROLE_ADMIN" ) || SecurityUtils.isCurrentUserInRole( "ROLE_ADMIN_AGENCY" ))
				actionPanel.getButtonManageMember().setVisible( true );
			else
				actionPanel.getButtonManageMember().setVisible( false );
			aDetailLayout.setVisible( true );
			aSearchLayout.setVisible( false );
		}
	}
	

	@Override
	public void afterViewChange(ViewChangeEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean beforeViewChange(ViewChangeEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateMessageStrings(Locale locale) {

		actionPanel.updateMessageStrings(locale);
	}

	public AgencyDetailLayout getaDetailLayout() {
		return aDetailLayout;
	}
}
