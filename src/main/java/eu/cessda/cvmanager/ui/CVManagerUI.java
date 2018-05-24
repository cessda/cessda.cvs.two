/**
 * 
 */
package eu.cessda.cvmanager.ui;

import java.util.Arrays;
import java.util.Locale;

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
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Viewport;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.ValueChangeMode;
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
import eu.cessda.cvmanager.ui.view.AgencyView;
import eu.cessda.cvmanager.ui.view.DetailView;
import eu.cessda.cvmanager.ui.view.EditorView;
import eu.cessda.cvmanager.ui.view.HomeView;
import eu.cessda.cvmanager.ui.view.SearchView;
import eu.cessda.cvmanager.ui.view.publication.DiscoveryView;
import eu.cessda.cvmanager.utils.FileUtils;

/**
 * @author klascr
 *
 */

@Viewport("initial-scale=1, maximum-scale=1")
@Theme("mytheme")
@SpringUI
@PreserveOnRefresh
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

	private MButton home = new MButton("Home", this::gotoHome);

	private MButton searchCVs = new MButton("Search CVs", this::gotoSearchCvs);
	
	private MButton agencyButton = new MButton("Agency", this::goToAgency);
	private MButton adminButton = new MButton("Admin", this::goToAdmin);
	private MButton discoverButton = new MButton("Published", this::goToDiscovery);

	private MButton logIn = new MButton("Login", this::doLogin);
	private MButton logout = new MButton("Logout", this::doLogout);

	private Navigator navigator;

	private String webLanguage = "de";

	private ComboBox<Language> countryBox = new ComboBox<>();
	private UserDetails userDetail;	
	
	private MTextField searchTf = new MTextField();
	private MCssLayout userSubMenu = new MCssLayout();
	private MLabel usernameTf = new MLabel();
	private MCssLayout userInfoLayout = new MCssLayout();
	private Breadcrumbs breadCrumb = new Breadcrumbs();

	public CVManagerUI(SpringViewProvider viewProvider,
			SecurityService securityService, ConfigurationService configurationService, 
			UIEventBus eventBus, I18N i18n) {
		this.viewProvider = viewProvider;
		this.securityService = securityService;
		this.configurationService = configurationService;
		this.eventBus = eventBus;
		this.i18n = i18n;
	}

	@Override
	protected void initUI(VaadinRequest request) {
		setLocale(Locale.ENGLISH);
		
		breadCrumb
			.withBaseUrl( configurationService.getServerContextPath() )
			.withBasePageName("Home")
			.withBasePageUrl( SearchView.VIEW_NAME);

		// to handle the errors of AccessDenied
		this.getUI().setErrorHandler(ErrorHandler::handleError);

		addHeader();

		footer.setStyleName("footer");
		footer.setWidth(100, Unit.PERCENTAGE);

		viewContainer.withFullWidth().withMargin(new MMarginInfo(false, false, true, false)).withStyleName("content");

		root.withResponsive(true).withFullWidth().withMargin(false).withUndefinedSize()
				.withDefaultComponentAlignment(Alignment.TOP_CENTER).with(headerBar,
						// searchBox,
						viewContainer, footer)
				.withExpand(viewContainer, 1.0f);

		// main UI properties

		setStyleName("mainlayout");
		setContent(root);

		// navigator properties
		navigator = new Navigator(this, viewContainer);
		navigator.addProvider(viewProvider);

		// by Karam
		navigator.setErrorView(ErrorView.class);
		this.viewProvider.setAccessDeniedViewClass(AccessDeniedView.class);

		String uriQuery = Page.getCurrent().getLocation().toString();
		if( !uriQuery.contains( "#!" + DetailView.VIEW_NAME ) && !uriQuery.contains( "#!" + AgencyView.VIEW_NAME ) && 
				!uriQuery.contains( "#!" + DiscoveryView.VIEW_NAME ))
			navigator.navigateTo(SearchView.VIEW_NAME);
		navigator.addViewChangeListener(viewChangeListener);

		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			userInfoLayout.setVisible(true);
			
			if( SecurityUtils.isCurrentUserAgencyAdmin())
				adminButton.setVisible(true);
			logIn.setVisible(false);
		} else {
			userInfoLayout.setVisible(false);
			adminButton.setVisible(false);
			logIn.setVisible(true);
		}

		eventBus.subscribe(this);
		// updateMessageStrings(UI.getCurrent().getLocale());
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
		setLocale(new Locale(countryBox.getValue().toString().toLowerCase()));
		
		userSubMenu
			.withId("user-submenu")
			.add(
				adminButton,
				logout
			);

		userInfoLayout
			.withId("user-menu")
			.add(
				new MLabel().withContentMode( ContentMode.HTML ).withValue( "<i class=\"icon-header fa fa-user\"></i>" ),
				usernameTf,
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
					.withValue(  "		<div class=\"email\">\n" + 
							"			<a href=\"mailto:cessda@cessda.net\"><i class=\"fa fa-envelope\"></i> cessda@cessda.net</a>\n" + 
							"		</div>" +
							"	<ul>\n" + 
							"		<li><a href=\"https://www.facebook.com/Cessda-463858013634628/\" target=\"_blank\"><i class=\"fa fa-facebook\"></i></a></li>\n" + 
							"		<li><a href=\"https://twitter.com/CESSDA_Data/\" target=\"_blank\"><i class=\"fa fa-twitter\"></i></a></li>\n" + 
							"		<li><a href=\"https://www.linkedin.com/company/9392869\" target=\"_blank\"><i class=\"fa fa-linkedin\"></i></a></li>\n" + 
							"		<li><a href=\"https://www.instagram.com/cessda_data/\" target=\"_blank\"><i class=\"fa fa-instagram\"></i></a></li>\n" + 
							"		<li><a href=\"https://plus.google.com/112779581489694492154\" target=\"_blank\"><i class=\"fa fa-google-plus\"></i></a></li>\n" + 
							"	</ul>" ),
				new MCssLayout()
					.withStyleName( "col-md-6 log-in pull-right" )
					.add(
						new MLabel().withContentMode( ContentMode.HTML ).withValue( "<i class=\"icon-header fa fa-globe\"></i>" ),
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
							logo,
							new MLabel("CV Manager").withFullWidth().withContentMode(ContentMode.HTML).withStyleName("sublogo")
						),
					new MCssLayout()
					.withStyleName("col-md-8 text-center")
					.add(
							searchBox()
						)
					);

		home.withStyleName(ValoTheme.BUTTON_LINK + " pull-left");
		// listAllCv.withStyleName( ValoTheme.BUTTON_LINK + " pull-left");
		searchCVs.withStyleName(ValoTheme.BUTTON_LINK + " pull-left");
		// editorCVs.withStyleName( ValoTheme.BUTTON_LINK + " pull-left");
		agencyButton.withStyleName(ValoTheme.BUTTON_LINK + " pull-left");
		discoverButton.withStyleName(ValoTheme.BUTTON_LINK + " pull-left");
		adminButton.withStyleName(ValoTheme.BUTTON_LINK + " pull-left");
		logIn.withStyleName(ValoTheme.BUTTON_LINK + " pull-left");
		logout.withStyleName(ValoTheme.BUTTON_LINK + " pull-left");
		
		searchTf
			.withPlaceholder("Find Controlled Vocabulary")
			.withFullWidth()
			.withValueChangeMode( ValueChangeMode.LAZY)
			.withValueChangeTimeout( 200 )
			.addTextChangeListener( e -> {
				navigator.navigateTo(DiscoveryView.VIEW_NAME);
				eventBus.publish(EventScope.UI, DiscoveryView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.VOCABULARY_SEARCH, e.getValue()) );
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

		headerBottom
			.withFullWidth()
			.withStyleName("menu-bg")
			.add(
					new MCssLayout()
					.withStyleName( "container" )
					.add(
						home,
						// signUp,
						// listAllCv,
						searchCVs,
						agencyButton,
						discoverButton
						
						// editorCVs,
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
		searchContainer
			.withStyleName("search-box")
			.add( 
				new MLabel().withContentMode( ContentMode.HTML ).withValue( "<i class=\"icon-search fa fa-search\"></i>" ),
				searchTf 
			);
		return searchContainer;
	}

	public void gotoHome(ClickEvent event) {
		navigator.navigateTo(SearchView.VIEW_NAME);
	}

	public void gotoListAllCvs(ClickEvent event) {
		navigator.navigateTo(SearchView.VIEW_NAME);
	}

	public void gotoSearchCvs(ClickEvent event) {
		navigator.navigateTo(SearchView.VIEW_NAME);
	}

	public void gotoEditorCvs(ClickEvent event) {
		navigator.navigateTo(EditorView.VIEW_NAME);
	}

	public void gotoBrowse(ClickEvent event) {
		navigator.navigateTo(SearchView.VIEW_NAME);
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
	
	public void goToAdmin(ClickEvent event) {
		if( SecurityUtils.isCurrentUserSystemAdmin() )
			navigator.navigateTo(ManageUserView.VIEW_NAME);
		else
			navigator.navigateTo(ManageUserAgencyView.VIEW_NAME);
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
		// newDataset.setVisible( true );
		// browse.setVisible( true );
		userInfoLayout.setVisible(true);
		usernameTf.setValue( SecurityUtils.getLoggedUser().getLastName() );
		
		System.out.println(SecurityUtils.isCurrentUserAgencyAdmin());
		
		if( SecurityUtils.isCurrentUserAgencyAdmin())
			adminButton.setVisible(true);
		
		logIn.setVisible(false);
		// signUp.setVisible( false );
		// }
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
		return breadCrumb;
	}

}
