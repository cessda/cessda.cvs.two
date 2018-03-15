/**
 * 
 */
package eu.cessda.cvmanager.ui;

import java.util.Arrays;
import java.util.Locale;

import org.gesis.wts.security.ErrorHandler;
import org.gesis.wts.security.LoginSucceedEvent;
import org.gesis.wts.security.SecurityService;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.security.UserDetails;
import org.gesis.wts.ui.view.AccessDeniedView;
import org.gesis.wts.ui.view.ErrorView;
import org.gesis.wts.ui.view.LoginView;
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
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MMarginInfo;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.MessageByLocaleService;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.LanguageSwitchedEvent;
import eu.cessda.cvmanager.ui.view.AgencyView;
import eu.cessda.cvmanager.ui.view.DetailView;
import eu.cessda.cvmanager.ui.view.EditorView;
import eu.cessda.cvmanager.ui.view.HomeView;
import eu.cessda.cvmanager.ui.view.SearchView;
import eu.cessda.cvmanager.utils.FileUtils;

/**
 * @author klascr
 *
 */

@Theme("mytheme")
@SpringUI
@PreserveOnRefresh
@EnableEventBus

public class CVManagerUI extends TranslatableUI implements Translatable {

	private static final long serialVersionUID = -6435583434844959571L;

	private final MessageByLocaleService messageByLocaleService;
	private final SpringViewProvider viewProvider;
	private final SecurityService securityService;
	private final UIEventBus eventBus;
	private final I18N i18n;

	private MVerticalLayout root = new MVerticalLayout();
	private MCssLayout headerBar = new MCssLayout();
	private MVerticalLayout viewContainer = new MVerticalLayout();
	private CustomLayout headerToplinks = new CustomLayout("headerToplinks");
	private CustomLayout footer = new CustomLayout("footer");

	private MButton home = new MButton("Home", this::gotoHome);

	private MButton searchCVs = new MButton("Search CVs", this::gotoSearchCvs);
	
	private MButton agencyButton = new MButton("Agency", this::goToAgency);
	private MButton adminButton = new MButton("Admin", this::goToAdmin);

	private MButton logIn = new MButton("Login", this::doLogin);
	private MButton logout = new MButton("Logout", this::doLogout);

	private Navigator navigator;

	private String webLanguage = "de";

	private ComboBox countryBox = new ComboBox();
	private UserDetails userDetail;

	public CVManagerUI(MessageByLocaleService messageByLocaleService, SpringViewProvider viewProvider,
			SecurityService securityService, UIEventBus eventBus, I18N i18n) {
		this.messageByLocaleService = messageByLocaleService;
		this.viewProvider = viewProvider;
		this.securityService = securityService;
		this.eventBus = eventBus;
		this.i18n = i18n;
	}

	@Override
	protected void initUI(VaadinRequest request) {
		setLocale(Locale.ENGLISH);

		// to handle the errors of AccessDenied
		this.getUI().setErrorHandler(ErrorHandler::handleError);

		addHeader();

		footer.setStyleName("footer");
		footer.setWidth(100, Unit.PERCENTAGE);

		viewContainer.withFullWidth().withMargin(new MMarginInfo(false, false, true, false)).withStyleName("content");

		root.withResponsive(true).withFullWidth().withMargin(false).withHeightUndefined()
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
		if( !uriQuery.contains( "#!" + DetailView.VIEW_NAME ) && !uriQuery.contains( "#!" + AgencyView.VIEW_NAME ))
			navigator.navigateTo(SearchView.VIEW_NAME);
		navigator.addViewChangeListener(viewChangeListener);

		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			logout.setVisible(true);
			
			if( SecurityUtils.isCurrentUserInRole( "ROLE_ADMIN" ))
				adminButton.setVisible(true);
			logIn.setVisible(false);
		} else {
			logout.setVisible(false);
			adminButton.setVisible(false);
			logIn.setVisible(true);
		}

		eventBus.subscribe(this);
		// updateMessageStrings(UI.getCurrent().getLocale());
	}

	private void addHeader() {

		countryBox.setTextInputAllowed(false);
		countryBox.setItems(Arrays.asList("en", "de", "fi"));
		countryBox.setEmptySelectionAllowed(false);
		countryBox.setWidth("80px");
		countryBox.setValue("en");
		countryBox.addValueChangeListener(e -> {
			setLocale(new Locale(e.getValue().toString().toLowerCase()));
		});
		setLocale(new Locale(countryBox.getValue().toString().toLowerCase()));

		headerToplinks.setSizeFull();
		MLabel logo = new MLabel();
		logo.withContent(FileUtils.getSiteLogo()).withContentMode(ContentMode.HTML).withFullWidth();

		home.withStyleName(ValoTheme.BUTTON_LINK);
		// listAllCv.withStyleName( ValoTheme.BUTTON_LINK );
		searchCVs.withStyleName(ValoTheme.BUTTON_LINK);
		// editorCVs.withStyleName( ValoTheme.BUTTON_LINK );
		agencyButton.withStyleName(ValoTheme.BUTTON_LINK);
		adminButton.withStyleName(ValoTheme.BUTTON_LINK);
		logIn.withStyleName(ValoTheme.BUTTON_LINK);
		logout.withStyleName(ValoTheme.BUTTON_LINK);

		MVerticalLayout logoLayout = new MVerticalLayout();
		logoLayout.withFullWidth().withMargin(new MarginInfo(true, true, true, false)).add(logo,
				new MLabel("CV Manager").withFullWidth().withContentMode(ContentMode.HTML).withStyleName("sublogo"));

		MHorizontalLayout menuLayout = new MHorizontalLayout();

		menuLayout.withFullWidth().withStyleName("menuLayout").withMargin(new MarginInfo(true, true, true, false)).add(
				countryBox, home,
				// signUp,
				// listAllCv,
				searchCVs,
				agencyButton,
				adminButton,
				// editorCVs,
				logIn, logout);

		headerBar.withResponsive(true).withStyleName("headerbar").add(

				headerToplinks,
				new MHorizontalLayout().withStyleName("mid_search").withFullWidth().withMargin(false)
						.add(new MHorizontalLayout().withStyleName("container").add(logoLayout, menuLayout)
								.withExpand(logoLayout, 0.4f).withExpand(menuLayout, 0.6f))

		);
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
	
	public void goToAdmin(ClickEvent event) {
		navigator.navigateTo(ManageUserView.VIEW_NAME);
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
		logout.setVisible(true);
		
		if( SecurityUtils.isCurrentUserInRole( "ROLE_ADMIN" ))
			adminButton.setVisible(true);
		
		logIn.setVisible(false);
		// signUp.setVisible( false );
		// }
	}
	
	public MessageByLocaleService getMessageByLocaleService() {

		return messageByLocaleService;
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

}
