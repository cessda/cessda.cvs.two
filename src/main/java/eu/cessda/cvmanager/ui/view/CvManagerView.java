package eu.cessda.cvmanager.ui.view;

import java.util.Locale;

import javax.annotation.PostConstruct;

import org.gesis.security.SecurityService;
import org.gesis.security.util.LoginSucceedEvent;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.navigator.MView;

import com.vaadin.navigator.View;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.UI;

import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.model.CvItem;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.CvManagerService;

public abstract class CvManagerView extends MVerticalLayout implements MView, Translatable {

	private static final long serialVersionUID = -8769292972079523949L;
	public static enum ActionType{
		SEARCH, BROWSE, DETAIL // this should be similar to view names
	}
	
	protected final I18N i18n;
	protected final EventBus.UIEventBus eventBus;
	protected final ConfigurationService configService;
	protected final CvManagerService cvManagerService;
	protected final SecurityService securityService;
	
	private final ActionType actionType;
	protected CVScheme cvScheme;
	protected CVConcept cvConcept;
	protected CvItem cvItem = new CvItem();
	
	protected MVerticalLayout mainContainer = new MVerticalLayout();
	protected MHorizontalLayout columnContainer = new MHorizontalLayout();
	protected ActionPanel actionPanel;
	protected MVerticalLayout rightContainer = new MVerticalLayout();
	
	public CvManagerView(I18N i, EventBus.UIEventBus eventBus, ConfigurationService configService, 
			CvManagerService cvManagerService, SecurityService securityService, String actionType) {
		this.i18n = i;
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
			.withExpand( actionPanel, 0.2f )
			.withExpand( rightContainer, 0.8f );
		
		mainContainer
			//.withWidth( "1170px" )
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

	public CVConcept getCvConcept() {
		return cvConcept;
	}

	public void setCvConcept(CVConcept cvConcept) {
		this.cvConcept = cvConcept;
	}
}
