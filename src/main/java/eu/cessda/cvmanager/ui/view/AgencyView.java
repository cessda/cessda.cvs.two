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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
import eu.cessda.cvmanager.ui.layout.AgencyActionLayout;
import eu.cessda.cvmanager.ui.layout.AgencyDetailLayout;
import eu.cessda.cvmanager.ui.layout.AgencyOwnLayout;
import eu.cessda.cvmanager.ui.layout.AgencySearchLayout;
import eu.cessda.cvmanager.ui.view.publication.DiscoveryView;
import eu.cessda.cvmanager.ui.view.publication.PaginationBar;
import eu.cessda.cvmanager.ui.view.window.DialogAgencyManageMember;

@UIScope
@SpringView(name = AgencyView.VIEW_NAME)
public class AgencyView extends CvView {

	private static final long serialVersionUID = -6014026644497372675L;
	public static final String VIEW_NAME = "agency";
	public enum ViewMode { INITIAL, DETAIL };
	
//	Autowired
	private final UserService userService;
	private final AgencyService agencyService;
	private final RoleService roleService;
	private final UserAgencyService userAgencyService;
	private final MetadataFieldService metadataFieldService;
	private final MetadataValueService metadataValueService;
	
	private MCssLayout searchTopLayout = new MCssLayout();
	private MLabel resultInfo = new MLabel();
	
	private MTextField searchTf = new MTextField();
	private MButton clearSearchButton = new MButton();
	
	private MHorizontalLayout mainContent = new MHorizontalLayout();

//	This view compose of 3 layouts:
//	1. Agency own layout
//	2. Agency search layout
//	3. Agency details layout
	private AgencyOwnLayout aOwnLayout;
	private AgencySearchLayout aSearchLayout;
	private AgencyDetailLayout aDetailLayout;
	private AgencyActionLayout aActionLayout;
	
	private ViewMode viewMode;

	// The opened search hit at the results grid (null at the beginning)

	public AgencyView(I18N i18n, EventBus.UIEventBus eventBus, ConfigurationService configService,
			StardatDDIService stardatDDIService, SecurityService securityService, 
			UserService userService, RoleService roleService, AgencyService agencyService, 
			MetadataFieldService metadataFieldService, MetadataValueService metadataValueService,
			UserAgencyService userAgencyService, VocabularyService vocabularyService, CodeService codeService) {
		super(i18n, eventBus, configService, stardatDDIService, securityService, agencyService, vocabularyService, codeService, null, AgencyView.VIEW_NAME);
		this.userService = userService;
		this.roleService = roleService;
		this.agencyService = agencyService;
		this.userAgencyService = userAgencyService;
		this.metadataFieldService = metadataFieldService;
		this.metadataValueService = metadataValueService;
		
		eventBus.subscribe(this, AgencyView.VIEW_NAME);
	}

	@PostConstruct
	public void init() {
		LoginView.NAVIGATETO_VIEWNAME = AgencyView.VIEW_NAME;
				
		aActionLayout = new AgencyActionLayout( "block.action.agency" , "block.action.agency.show" , 
				i18n, eventBus, agency, userService, roleService, agencyService, metadataFieldService, 
				metadataValueService, userAgencyService);
		aOwnLayout = new AgencyOwnLayout(i18n, eventBus, this, agencyService, configService); 
		aSearchLayout = new AgencySearchLayout(i18n, eventBus, this, agencyService, configService);
		aDetailLayout = new AgencyDetailLayout(i18n, eventBus, this, agencyService, configService, vocabularyService, stardatDDIService, configService); 
		
		
		resultInfo
			.withStyleName("search-hit-info")
			.withContentMode( ContentMode.HTML );
		
		searchTf
			.withPlaceholder("Find Agency")
			.withFullWidth()
			.withValueChangeMode( ValueChangeMode.LAZY)
			.withValueChangeTimeout( 200 )
			.addTextChangeListener( e -> {
				aSearchLayout.updateList();
				if( e.getValue() != null && e.getValue().length() > 0)
					clearSearchButton.setVisible( true );
				else
					clearSearchButton.setVisible( false );
			});
		
		searchTopLayout
			.withStyleName("search-option-flex")
			.add( 
				resultInfo,
				searchBox()
			);
		
		topPanel.add( searchTopLayout );
		sidePanel
			.add( 
				aActionLayout
			);
		
		mainContainer
			.add(
				aOwnLayout,
				aSearchLayout, 
				aDetailLayout
			);
	}
	
	private MCssLayout searchBox() {
		MCssLayout searchContainer = new MCssLayout();
		clearSearchButton
			.withIcon(FontAwesome.TIMES)
			.withStyleName("clear-search-button")
			.withVisible( false )
			.addClickListener( e -> {
				clearSearch();
			});
		searchContainer
			.withStyleName("search-box-secondary")
			.add( 
				new MLabel().withContentMode( ContentMode.HTML ).withValue( "<i class=\"icon-search fa fa-search\"></i>" ),
				searchTf,
				clearSearchButton
			);
		return searchContainer;
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
				Window window = new DialogAgencyManageMember(eventBus, agency, userService, roleService, agencyService, userAgencyService, i18n, locale);
				getUI().addWindow(window);
				break;
			case AGENCY_SEARCH_MODE:
				setAgency(null, ViewMode.INITIAL);
				break;
			default:
				break;
		}
	}
	
	public void setAgency( AgencyDTO agency, ViewMode viewMode) {
		this.agency = agency;
		this.viewMode = viewMode;
		
		aActionLayout.setAgency(agency);
		aActionLayout.hasActionRight();
		// check user roles here
		if (viewMode.equals( ViewMode.INITIAL )) {
			aDetailLayout.setVisible( false );
			sidePanel.setVisible( false );
			aSearchLayout.setVisible( true );
			topPanel.setVisible( true );
			
			if( SecurityUtils.isAuthenticated()) {
				aOwnLayout.updateList();
				aOwnLayout.setVisible( true );
			}
			else {
				aOwnLayout.setVisible( false );
			}
			aSearchLayout.updateList();
			
		} else {
			aDetailLayout.setVisible( true );
			sidePanel.setVisible( true );
			aSearchLayout.setVisible( false );
			topPanel.setVisible( false );
			aDetailLayout.setAgency(agency);
		}
	}
	
	public void refreshSearchResult() {
		aSearchLayout.updateList();
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

	}

	public AgencyDetailLayout getaDetailLayout() {
		return aDetailLayout;
	}
	
	public void clearSearch() {
		searchTf.setValue("");
	}

	public MLabel getResultInfo() {
		return resultInfo;
	}

	public void setResultInfo(MLabel resultInfo) {
		this.resultInfo = resultInfo;
	}

	public MTextField getSearchTf() {
		return searchTf;
	}

	public void setSearchTf(MTextField searchTf) {
		this.searchTf = searchTf;
	}
	
	

}
