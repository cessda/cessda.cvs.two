/**
 * 
 */
package eu.cessda.cvmanager.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.ErrorHandler;
import org.gesis.wts.security.LoginSucceedEvent;
import org.gesis.wts.security.SecurityService;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.security.UserDetails;
import org.gesis.wts.ui.view.AccessDeniedView;
import org.gesis.wts.ui.view.ErrorView;
import org.gesis.wts.ui.view.LoginView;
import org.gesis.wts.ui.view.admin.ManageUserAgencyView;
import org.gesis.wts.ui.view.admin.ManageUserView;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EnableEventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.spring.i18n.support.TranslatableUI;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MMarginInfo;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.LanguageSwitchedEvent;
import eu.cessda.cvmanager.ui.component.Breadcrumbs;
import eu.cessda.cvmanager.ui.view.AboutView;
import eu.cessda.cvmanager.ui.view.AgencyView;
import eu.cessda.cvmanager.ui.view.DetailView;
import eu.cessda.cvmanager.ui.view.DetailsView;
import eu.cessda.cvmanager.ui.view.EditorSearchView;
import eu.cessda.cvmanager.ui.view.EditorView;
import eu.cessda.cvmanager.ui.view.HomeView;
import eu.cessda.cvmanager.ui.view.SearchView;
import eu.cessda.cvmanager.ui.view.admin.AdminView;
import eu.cessda.cvmanager.ui.view.publication.DiscoveryView;
import eu.cessda.cvmanager.utils.FileUtils;

/**
 * @author klascr
 *
 */

@Viewport("initial-scale=1, maximum-scale=1")
//@StyleSheet({"http://fonts.googleapis.com/css?family=Roboto:100,200,300,400,500,600,700,800,900"})
@Theme("mytheme")
@Title("CESSDA Vocabularies")
@SpringUI
//@PreserveOnRefresh
@EnableEventBus

public class CVManagerUI extends TranslatableUI implements Translatable {

	private static final long serialVersionUID = -6435583434844959571L;

	private final SpringViewProvider viewProvider;
	private final SecurityService securityService;
	private final ConfigurationService configurationService;
	private final UIEventBus eventBus;
	private final I18N i18n;

	private MVerticalLayout root = new MVerticalLayout();
	private MCssLayout headerBar = new MCssLayout();
	private MVerticalLayout viewContainer = new MVerticalLayout();
	private MCssLayout headerTop = new MCssLayout();
	private MCssLayout headerMiddle = new MCssLayout();
	private MCssLayout headerBottom = new MCssLayout();
	private CustomLayout footer = new CustomLayout("footer");

	private MButton home = new MButton("Home", this::goToDiscovery);
	private MButton about = new MButton("About", this::goToAbout);

	private List<MButton> menuButtons = new ArrayList<>();
	private MButton searchCVs = new MButton("Editor Search", this::gotoSearchCvs);
	
	private MButton agencyButton = new MButton("Agency", this::goToAgency);
	private MButton adminButton = new MButton("Admin", this::goToAdmin);
	private MButton discoverButton = new MButton("Published", this::goToDiscovery);

	private MButton logIn = new MButton("Login", this::doLogin);
	private MButton logout = new MButton("Logout", this::doLogout);
	private MButton clearSearchButton = new MButton();

	private Navigator navigator;

	private String webLanguage = "de";

	private ComboBox<Language> countryBox = new ComboBox<>();
	private UserDetails userDetail;	
	
	private MTextField searchTf = new MTextField();
	private MCssLayout userSubMenu = new MCssLayout();
	private MLabel usernameLbl = new MLabel();
	private MCssLayout userInfoLayout = new MCssLayout();
	private Breadcrumbs breadCrumb = new Breadcrumbs();
	private Map<String, String> breadcrumbItemMap = new LinkedHashMap<>();

	public CVManagerUI(SpringViewProvider viewProvider,
			SecurityService securityService, ConfigurationService configurationService, 
			UIEventBus eventBus, I18N i18n) {
		this.viewProvider = viewProvider;
		this.securityService = securityService;
		this.configurationService = configurationService;
		this.eventBus = eventBus;
		this.i18n = i18n;
	}

	@SuppressWarnings("static-access")
	@Override
	protected void initUI(VaadinRequest request) {
		setLocale(Locale.ENGLISH);
		
		
		breadCrumb
			.withBaseUrl( configurationService.getServerContextPath() )
			.withBasePageName("Home")
			.withBasePageUrl( DiscoveryView.VIEW_NAME);

		// to handle the errors of AccessDenied
		this.getUI().setErrorHandler(ErrorHandler::handleError);
		this.getUI().getCurrent().setPollInterval( 5000 );

		addHeader();

		footer.setStyleName("footer");
		footer.setWidth(100, Unit.PERCENTAGE);

		viewContainer
			.withFullWidth()
			.withMargin( false )
			.withStyleName("content");

		root
			.withResponsive(true)
			.withFullWidth()
			.withMargin(false)
			.withSpacing( false )
			.withUndefinedSize()
			.withDefaultComponentAlignment(Alignment.TOP_CENTER)
			.with(
				headerBar,
				// searchBox,
				viewContainer, 
				footer
			)
			.withExpand(viewContainer, 1.0f);

		// main UI properties
		setStyleName("mainlayout");
		setId("main-container");
		setContent(root);

		// navigator properties
		navigator = new Navigator(this, viewContainer);
		navigator.addProvider(viewProvider);

		// by Karam
		navigator.setErrorView(ErrorView.class);
		this.viewProvider.setAccessDeniedViewClass(AccessDeniedView.class);

		String uriQuery = Page.getCurrent().getLocation().toString();
		if( !uriQuery.contains( "#!" + DetailView.VIEW_NAME ) && !uriQuery.contains( "#!" + AgencyView.VIEW_NAME ) && 
				!uriQuery.contains( "#!" + DiscoveryView.VIEW_NAME ) && !uriQuery.contains( "#!" + DetailsView.VIEW_NAME )  && 
				!uriQuery.contains( "#!" + AdminView.VIEW_NAME ) && !uriQuery.contains( "#!" + EditorSearchView.VIEW_NAME )) {
			navigator.navigateTo(DiscoveryView.VIEW_NAME);
		}
		navigator.addViewChangeListener(viewChangeListener);

		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			userInfoLayout.setVisible(true);
			searchCVs.setVisible(true);
			agencyButton.setVisible( true );
			
			if( SecurityUtils.isCurrentUserAgencyAdmin())
				adminButton.setVisible(true);
			logIn.setVisible(false);
		} else {
			userInfoLayout.setVisible(false);
			adminButton.setVisible(false);
			logIn.setVisible(true);
			searchCVs.setVisible(false);
			agencyButton.setVisible( false );
		}

		eventBus.subscribe(this);
		// updateMessageStrings(UI.getCurrent().getLocale());
		
		// remember me login
		if (securityService.rememberMeLogin()) {

			// Now when the session is reinitialized, we can enable
			// websocket communication. Or we could have just
			// used WEBSOCKET_XHR and skipped this step completely.
			this.getUI().getPushConfiguration().setTransport(Transport.WEBSOCKET);

			// throw an event when the login is succeeded
			eventBus.publish(this, new LoginSucceedEvent());

//			this.getUI().getNavigator().navigateTo(LoginView.NAVIGATETO_VIEWNAME);

		}
	}

	private void addHeader() {

		countryBox.setTextInputAllowed(false);
		countryBox.setItems( Language.ENGLISH, Language.GERMAN, Language.FINNISH);
		countryBox.setEmptySelectionAllowed(false);
		countryBox.setStyleName( "language-option" );
		countryBox.setValue( Language.ENGLISH );
		countryBox.setWidth("100px");
		countryBox.addValueChangeListener(e -> {
			setLocale(new Locale(e.getValue().toString()));
		});
		countryBox.setItemCaptionGenerator( new ItemCaptionGenerator<Language>() {
			private static final long serialVersionUID = 1L;
			@Override
			public String apply(Language item) {
				return item.name().substring(0, 1) + item.name().substring(1).toLowerCase();
			}
		});
		
		//  Remove this line if select language is activated
		countryBox.setVisible( false );
		
		
		setLocale(new Locale(countryBox.getValue().toString().toLowerCase()));
		
		userSubMenu
			.withId("user-submenu")
			.add(
				logout
			);

		userInfoLayout
			.withId("user-menu")
			.add(
				new MLabel().withContentMode( ContentMode.HTML ).withValue( "<i class=\"icon-header fa fa-user\"></i>" ),
				usernameLbl,
				userSubMenu
			);
		
		
		
		MCssLayout headerTopContainer = new MCssLayout();
		headerTopContainer
			.withStyleName( "row" )
			.withFullWidth()
			.add(
				new MLabel()
					.withContentMode( ContentMode.HTML)
					.withStyleName( "col-md-6 social text-center" )
					.withValue(  "<div class=\"email\"><span>Consortium of European Social Science Data Archives</span></div>" ),
				new MCssLayout()
					.withStyleName( "col-md-6 log-in pull-right" )
					.add(
//						new MLabel().withContentMode( ContentMode.HTML ).withValue( "<i class=\"icon-header fa fa-globe\"></i>" ),
						countryBox,
						new MCssLayout()
							.withStyleName( "login" )
							.add( 
								logIn, 
								userInfoLayout
							)
					)
			);
		
		MLabel logo = new MLabel();
		logo.withContent(FileUtils.getSiteLogo()).withContentMode(ContentMode.HTML).withFullWidth();
			
		MCssLayout headerMiddleContent = new MCssLayout();
		headerMiddleContent
			.withStyleName( "row header-content" )
			.withFullWidth()
			.add(
				new MCssLayout()
					.withStyleName("col-md-4")
					.add(
							logo
						),
					new MCssLayout()
					.withStyleName("col-md-8 text-center")
					.add(
							searchBox()
						)
					);

		home.withStyleName(ValoTheme.BUTTON_LINK + " pull-left");
		about.withStyleName(ValoTheme.BUTTON_LINK + " pull-left");
		// listAllCv.withStyleName( ValoTheme.BUTTON_LINK + " pull-left");
		searchCVs.withStyleName(ValoTheme.BUTTON_LINK + " pull-left");
		// editorCVs.withStyleName( ValoTheme.BUTTON_LINK + " pull-left");
		agencyButton.withStyleName(ValoTheme.BUTTON_LINK + " pull-left");
		discoverButton.withStyleName(ValoTheme.BUTTON_LINK + " pull-left");
		adminButton.withStyleName(ValoTheme.BUTTON_LINK + " pull-left");
		logIn.withStyleName(ValoTheme.BUTTON_LINK + " pull-left");
		logout.withStyleName(ValoTheme.BUTTON_LINK + " pull-left");
		
		if( SecurityUtils.isAuthenticated() )
			usernameLbl.setValue( SecurityUtils.getLoggedUser().getLastName() );
		
		searchTf
			.withPlaceholder("Find Controlled Vocabulary")
			.withFullWidth()
			.withValueChangeMode( ValueChangeMode.LAZY)
			.withValueChangeTimeout( 200 )
			.addTextChangeListener( e -> {
				System.out.println( EditorSearchView.class.getName() );
				if( navigator.getCurrentView().toString().indexOf( EditorSearchView.class.getSimpleName() ) > 0 || 
						navigator.getCurrentView().toString().indexOf( DetailsView.class.getSimpleName() ) > 0 ) {
					if( navigator.getCurrentView().toString().indexOf( EditorSearchView.class.getSimpleName() ) < 0 && !e.getValue().isEmpty())
						navigator.navigateTo(EditorSearchView.VIEW_NAME);
					eventBus.publish(EventScope.UI, EditorSearchView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.VOCABULARY_EDITOR_SEARCH, e.getValue()) );
				} else {
					if( navigator.getCurrentView().toString().indexOf( DiscoveryView.class.getSimpleName() ) < 0 && !e.getValue().isEmpty())
						navigator.navigateTo(DiscoveryView.VIEW_NAME);
					eventBus.publish(EventScope.UI, DiscoveryView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.VOCABULARY_SEARCH, e.getValue()) );
				}
				if( e.getValue() != null && e.getValue().length() > 0)
					clearSearchButton.setVisible( true );
				else
					clearSearchButton.setVisible( false );
			});
		
		headerTop
			.withFullWidth()
			.withStyleName( "primary-header" )
			.add(
					new MCssLayout()
					.withStyleName( "container" )
					.add( headerTopContainer )
			);
		
		headerMiddle
			.withFullWidth()
			.withStyleName( "common-header header-bg" )
			.add(
					new MCssLayout()
					.withStyleName( "container" )
					.add( headerMiddleContent )
			);
		
		menuButtons.clear();
		menuButtons.add( home );
		menuButtons.add( searchCVs );
		menuButtons.add(agencyButton);
		menuButtons.add( adminButton );
		menuButtons.add( about );

		headerBottom
			.withFullWidth()
			.withStyleName("menu-bg")
			.add(
					new MCssLayout()
					.withStyleName( "container" )
					.add(
						home,
						searchCVs,
						agencyButton,
//						discoverButton,
						adminButton,
						about
					)
			);
		
		headerBar
			.withResponsive(true)
			.withStyleName("header-cessda")
			.add(
				headerTop,
				headerMiddle,
				headerBottom,
				breadCrumb
//				new MHorizontalLayout().withStyleName("mid_search").withFullWidth().withMargin(false)
//						.add(new MHorizontalLayout().withStyleName("container").add(logoLayout, menuLayout)
//								.withExpand(logoLayout, 0.4f).withExpand(menuLayout, 0.6f))

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
			.withStyleName("search-box")
			.add( 
				new MLabel().withContentMode( ContentMode.HTML ).withValue( "<i class=\"icon-search fa fa-search\"></i>" ),
				searchTf,
				clearSearchButton
			);
		return searchContainer;
	}

	public void gotoHome(ClickEvent event) {
		navigator.navigateTo(EditorSearchView.VIEW_NAME);
	}

	public void gotoListAllCvs(ClickEvent event) {
		navigator.navigateTo(EditorSearchView.VIEW_NAME);
	}

	public void gotoSearchCvs(ClickEvent event) {
		navigator.navigateTo(EditorSearchView.VIEW_NAME);
	}

	public void gotoEditorCvs(ClickEvent event) {
		navigator.navigateTo(EditorView.VIEW_NAME);
	}

	public void gotoBrowse(ClickEvent event) {
		navigator.navigateTo(EditorSearchView.VIEW_NAME);
	}

	public void gotoProfile(ClickEvent event) {

	}
	
	public void goToAgency(ClickEvent event) {
		navigator.navigateTo(AgencyView.VIEW_NAME);
		eventBus.publish(EventScope.UI, AgencyView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.AGENCY_SEARCH_MODE, null) );
	}
	
	public void goToDiscovery(ClickEvent event) {
		navigator.navigateTo(DiscoveryView.VIEW_NAME);
	}
	
	public void goToAbout(ClickEvent event) {
		navigator.navigateTo(AboutView.VIEW_NAME);
	}
	
	public void goToAdmin(ClickEvent event) {
		navigator.navigateTo(AdminView.VIEW_NAME);
	}

	public void doLogin(ClickEvent event) {
		getNavigator().navigateTo(LoginView.NAME);
	}

	public void doLogout(ClickEvent event) {
		securityService.logout(LoginView.NAME);
	}

	// notify the view menu about view changes so that it can display which view
	// is currently active
	ViewChangeListener viewChangeListener = new ViewChangeListener() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public boolean beforeViewChange(ViewChangeEvent event) {

			return true;
		}

		@Override
		public void afterViewChange(ViewChangeEvent event) {

			// menu.setActiveView(event.getViewName());
		}

	};

	public String getWebLanguage() {

		return webLanguage;
	}

	@EventBusListenerMethod(scope = EventScope.UI)
	public void onPersonModified(LoginSucceedEvent event) {
		
		setUserDetail( SecurityUtils.getCurrentUserDetails().get());
		// if( securityService.isAuthenticate()){
		System.out.println("User logged in!");
		userInfoLayout.setVisible(true);
		searchCVs.setVisible(true);
		agencyButton.setVisible( true );
		usernameLbl.setValue( SecurityUtils.getLoggedUser().getLastName() );
		
		System.out.println(SecurityUtils.isCurrentUserAgencyAdmin());
		
		if( SecurityUtils.isCurrentUserAgencyAdmin())
			adminButton.setVisible(true);
		
		logIn.setVisible(false);
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		home.withCaption(i18n.get("view.home.menu.home", locale));
		searchCVs.withCaption(i18n.get("view.home.menu.browse", locale));
		logIn.withCaption(i18n.get("view.home.menu.login", locale));
		logout.withCaption(i18n.get("view.home.menu.logout", locale));
	}

	public UserDetails getUserDetail() {
		return userDetail;
	}

	public void setUserDetail(UserDetails userDetail) {
		this.userDetail = userDetail;
	}

	public Breadcrumbs getBreadCrumb() {
//		clearSearch();
		return breadCrumb;
	}

	public Map<String, String> getBreadcrumbItemMap() {
		return breadcrumbItemMap;
	}
	
	public void clearSearch() {
		searchTf.setValue("");
	}

	public List<MButton> getMenuButtons() {
		return menuButtons;
	}
	
	
}
