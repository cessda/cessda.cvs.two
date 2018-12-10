package eu.cessda.cvmanager.ui.view;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.gesis.wts.security.LoginSucceedEvent;
import org.gesis.wts.security.SecurityService;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.gesis.wts.ui.view.AccessDeniedView;
import org.gesis.wts.ui.view.LoginView;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.navigator.MView;


import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.UI;

import eu.cessda.cvmanager.model.CvItem;
import eu.cessda.cvmanager.repository.search.VocabularySearchRepository;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.CVManagerUI;
import eu.cessda.cvmanager.ui.component.Breadcrumbs;

public abstract class CvView extends MVerticalLayout implements MView, Translatable {

	private static final long serialVersionUID = -8769292972079523949L;
//	public static enum ActionType{
//		ADMIN, DISCOVER, EDITORSEARCH, DETAIL, DETAILS, AGENCY, WITHDRAWNDETAILS// this should be similar to view names
//	}
	
	protected final I18N i18n;
	protected final EventBus.UIEventBus eventBus;
	protected final ConfigurationService configService;
	protected final StardatDDIService stardatDDIService;
	protected final SecurityService securityService;
	protected final AgencyService agencyService;
	protected final VocabularyService vocabularyService;
	protected final CodeService codeService;
	
	protected Locale locale = UI.getCurrent().getLocale();
	
//	private final ActionType actionType;
	protected CvItem cvItem = new CvItem();
	protected AgencyDTO agency;
	protected VocabularyDTO vocabulary;
	protected CodeDTO code;
	protected Breadcrumbs breadcrumbs;
	protected Map<String, String> breadcrumbItemMap;
	
	protected MCssLayout outerContainer = new MCssLayout();
	protected MCssLayout topPanel = new MCssLayout();
	protected MCssLayout sidePanel = new MCssLayout();
	protected MCssLayout mainContainer = new MCssLayout();
	private String viewName;
	
	public CvView(I18N i, EventBus.UIEventBus eventBus, ConfigurationService configService, 
			StardatDDIService stardatDDIService, SecurityService securityService, AgencyService agencyService,
			VocabularyService vocabularyService, CodeService codeService,
			String actionType ) {
		this.i18n = i;
		this.eventBus = eventBus;
		this.configService = configService;
		this.stardatDDIService = stardatDDIService;
		this.securityService = securityService;
		this.agencyService = agencyService;
		this.vocabularyService = vocabularyService;
		this.codeService = codeService;
		this.viewName = actionType;
//		this.actionType = ActionType.valueOf(actionType.replaceAll("[^A-Za-z]", "").toUpperCase());
		
		this.eventBus.subscribe( this );
	}
	

	@PostConstruct
	public void initview() {
		
		topPanel
			.withStyleName( "top-panel" );

		sidePanel
			.withStyleName( "side-panel" )
			.setId("side-panel");
		
		mainContainer
			.withStyleName( "main-container" );
		
		outerContainer
			//.withWidth( "1170px" )
			.withStyleName( "mainlayout" )
			.add( 
				topPanel,
				sidePanel, 
				mainContainer 
			);
		
		this
			.withUndefinedHeight()
			.withSpacing( false )
			.withMargin( false )
			.add( outerContainer );
		
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		updateBreadcrumb();
	}
	
	public void updateBreadcrumb() {
		breadcrumbItemMap = ((CVManagerUI) getUI()).getBreadcrumbItemMap();
		
		breadcrumbs = ((CVManagerUI) getUI()).getBreadCrumb();
		breadcrumbs
			.clear();
		
		if( !breadcrumbItemMap.isEmpty() ) {
			breadcrumbItemMap.forEach( (k , v ) -> {
				breadcrumbs.addItem( k, v );
			});
		}
	}
	
	public void topMenuButtonUpdateActive(int activeIndex) {
		int i=0;
		for( MButton button : ((CVManagerUI) getUI()).getMenuButtons()) {
			if( activeIndex == i )
				button.addStyleName("active");
			else
				button.removeStyleName("active");
			i++;
		}
	}
	
	protected boolean authorizeViewAccess() {
		if( !SecurityUtils.isAuthenticated()) {
			LoginView.NAVIGATETO_VIEWNAME = viewName;
			UI.getCurrent().getNavigator().navigateTo(AccessDeniedView.NAME);
			return false;
		}
		return true;
	}
	
	@EventBusListenerMethod( scope = EventScope.UI )
	public void onAuthenticate( LoginSucceedEvent event )
	{
		
	}


	public EventBus.UIEventBus getEventBus() {
		return eventBus;
	}

	public StardatDDIService getStardatDDIService() {
		return stardatDDIService;
	}

//	public ActionType getActionType() {
//		return actionType;
//	}

	public CvItem getCvItem() {
		return cvItem;
	}

	public void setCvItem(CvItem cvItem) {
		this.cvItem = cvItem;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public AgencyDTO getAgency() {
		return agency;
	}

	public void setAgency(AgencyDTO agency) {
		this.agency = agency;
	}

	public VocabularyDTO getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(VocabularyDTO vocabulary) {
		this.vocabulary = vocabulary;
	}

	public CodeDTO getCode() {
		return code;
	}

	public void setCode(CodeDTO code) {
		this.code = code;
	}

}
