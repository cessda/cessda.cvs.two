/**
 * 
 */
package eu.cessda.cvmanager.ui;

import java.util.Locale;

import org.gesis.security.SecurityService;
import org.gesis.security.util.ErrorHandler;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.vaadin.spring.events.EventBus;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

import eu.cessda.cvmanager.MessageByLocaleService;
import eu.cessda.cvmanager.service.LanguageSwitchedEvent;
import eu.cessda.cvmanager.ui.view.AboutView;
import eu.cessda.cvmanager.ui.view.DetailView;
import eu.cessda.cvmanager.ui.view.EditorView;
import eu.cessda.cvmanager.ui.view.ErrorView;
import eu.cessda.cvmanager.ui.view.SearchView;

/**
 * @author klascr
 *
 */

@Theme("mytheme")
@SpringUI
@PreserveOnRefresh
public class CVManagerUI extends UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6435583434844959571L;

	// we can use either constructor autowiring or field autowiring
	@Autowired
	private SpringViewProvider viewProvider;

	@Autowired
	SecurityService securityService;

	@Autowired
	EventBus.UIEventBus eventBus;

	@Autowired
	private MessageByLocaleService messageByLocaleService;

	private Navigator navigator;

	private Menu menu;

	private RestClient ddiFlatDBRestClient;

	private String webLanguage = "de";

	@Override
	protected void init(VaadinRequest request) {

		// to handle the errors of AccessDenied
		this.getUI().setErrorHandler(ErrorHandler::handleError);

		final HorizontalLayout root = new HorizontalLayout();
		root.setSizeFull();
		root.setMargin(true);
		root.setSpacing(true);
		setContent(root);

		final Panel viewContainer = new Panel();
		viewContainer.setSizeFull();

		navigator = new Navigator(this, viewContainer);
		navigator.addProvider(viewProvider);

		menu = new Menu(navigator, securityService);
		menu.addView(new SearchView(), SearchView.VIEW_NAME, "CV Search", VaadinIcons.EDIT);
		menu.addView(new EditorView(), EditorView.VIEW_NAME, "CV Editor", VaadinIcons.EDIT);

		menu.addView(new AboutView(), AboutView.VIEW_NAME, AboutView.VIEW_NAME, VaadinIcons.INFO_CIRCLE);

		root.addComponent(menu);
		root.addComponent(viewContainer);
		root.setExpandRatio(viewContainer, 1.0f);

		// by Karam
		navigator.setErrorView(ErrorView.class);
		this.viewProvider.setAccessDeniedViewClass(org.gesis.security.views.AccessDeniedView.class);

		navigator.navigateTo(DetailView.VIEW_NAME);
		navigator.addViewChangeListener(viewChangeListener);
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

			menu.setActiveView(event.getViewName());
		}

	};

	public String getWebLanguage() {

		return webLanguage;
	}

	/**
	 * Accesses language of the passed event class --> EventBus transmits or
	 * publishes the LanguageSwitchedEvent-object. Review EventBus instructions
	 * for more information
	 * 
	 * @param event
	 */

	public Locale accessLang(LanguageSwitchedEvent event) {

		System.out.println(" MainView Lang:" + getWebLanguage());
		if (event.getWebLanguage().equals("de")) {
			LocaleContextHolder.setLocale(Locale.GERMAN);
			return LocaleContextHolder.getLocale();
		} else {
			LocaleContextHolder.setLocale(Locale.ENGLISH);
			return LocaleContextHolder.getLocale();
		}

	}

	public MessageByLocaleService getMessageByLocaleService() {

		return messageByLocaleService;
	}

	public void setMessageByLocaleService(MessageByLocaleService messageByLocaleService) {

		this.messageByLocaleService = messageByLocaleService;
	}

}
