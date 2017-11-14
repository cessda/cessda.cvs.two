package eu.cessda.cvmanager.ui.view;

import javax.annotation.PostConstruct;

import org.gesis.security.SecurityService;
import org.gesis.security.util.LoginSucceedEvent;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVScheme;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.navigator.View;
import com.vaadin.shared.ui.MarginInfo;

import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.CvManagerService;

public abstract class CvManagerView extends MVerticalLayout implements View {

	private static final long serialVersionUID = -8769292972079523949L;
	public static enum ActionType{
		SEARCH, BROWSE, DETAIL // this should be similar to view names
	}
	
	public final EventBus.UIEventBus eventBus;
	public final ConfigurationService configService;
	public final CvManagerService cvManagerService;
	public final SecurityService securityService;
	
	private final ActionType actionType;
	public CVScheme cvScheme;
	
	protected MVerticalLayout mainContainer = new MVerticalLayout();
	protected MHorizontalLayout columnContainer = new MHorizontalLayout();
	protected ActionPanel actionPanel;
	protected MVerticalLayout rightContainer = new MVerticalLayout();
	
	public CvManagerView(EventBus.UIEventBus eventBus, ConfigurationService configService, 
			CvManagerService cvManagerService, SecurityService securityService, String actionType) {
		
		this.eventBus = eventBus;
		this.configService = configService;
		this.cvManagerService = cvManagerService;
		this.securityService = securityService;
		
		this.actionType = ActionType.valueOf(actionType.toUpperCase());
		
		actionPanel = new ActionPanel( this );
		
		this.eventBus.subscribe( this );
	}
	

	@PostConstruct
	public void initview() {
		
		rightContainer
			.withMargin( false )
			.withSpacing( false )
			.withStyleName( "right-container" );

		columnContainer
			.withFullWidth()
			.withSpacing( false )
			.withMargin( false )
			.add( actionPanel,
				rightContainer
			)
			.withExpand( actionPanel, 0.15f )
			.withExpand( rightContainer, 0.85f );
		
		mainContainer
			.withWidth( "1170px" )
			.withStyleName( "mainlayout" )
			.withSpacing( true )
			.withMargin( new MarginInfo( false, false, false, false ) )
			.add( columnContainer );
		
		this
			.withHeightUndefined()
			.add( mainContainer );
		
		if( SecurityContextHolder.getContext().getAuthentication() == null && !securityService.rememberMeLogin()) {
			actionPanel.setVisible( false );
		} else {
			actionPanel.setVisible( true );
		}
	}
	
	@EventBusListenerMethod( scope = EventScope.UI )
	public void onAuthenticate( LoginSucceedEvent event )
	{
		actionPanel.setVisible( true );
	}

	public EventBus.UIEventBus getEventBus() {
		return eventBus;
	}

	public CvManagerService getCvManagerService() {
		return cvManagerService;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public CVScheme getCvScheme() {
		return cvScheme;
	}

	public void setCvScheme(CVScheme cvScheme) {
		this.cvScheme = cvScheme;
	}
}
