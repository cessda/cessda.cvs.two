package eu.cessda.cvmanager.ui.view.publication;

import java.util.Iterator;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.security.LoginSucceedEvent;
import org.gesis.wts.security.SecurityService;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.gesis.wts.ui.view.admin.CvManagerAdminView.ActionType;
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
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.model.CvItem;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;

public abstract class CvPublicationView extends MVerticalLayout implements MView, Translatable {

	private static final long serialVersionUID = -8769292972079523949L;
	public static enum ActionType{
		SEARCH, BROWSE, DETAIL, AGENCY, AGENCYDISCOVER// this should be similar to view names
	}
	
	protected final I18N i18n;
	protected final EventBus.UIEventBus eventBus;
	protected final ConfigurationService configService;
	protected final StardatDDIService stardatDDIService;
	protected final SecurityService securityService;
	protected final AgencyService agencyService;
	protected final VocabularyService vocabularyService;
	protected final CodeService codeService;
	protected Locale locale = UI.getCurrent().getLocale();
	
	private final ActionType actionType;
	protected CvItem cvItem = new CvItem();
	protected AgencyDTO agency;
	protected VocabularyDTO vocabulary;
	protected CodeDTO code;
	
	protected MVerticalLayout mainContainer = new MVerticalLayout();
	protected MHorizontalLayout columnContainer = new MHorizontalLayout();
	protected MVerticalLayout rightContainer = new MVerticalLayout();
	
	public CvPublicationView(I18N i, EventBus.UIEventBus eventBus, ConfigurationService configService, 
			StardatDDIService stardatDDIService, SecurityService securityService, AgencyService agencyService,
			VocabularyService vocabularyService,  CodeService codeService,	String actionType) {
		this.i18n = i;
		this.eventBus = eventBus;
		this.configService = configService;
		this.stardatDDIService = stardatDDIService;
		this.securityService = securityService;
		this.agencyService = agencyService;
		this.vocabularyService = vocabularyService;
		this.codeService = codeService;
		
		this.actionType = ActionType.valueOf(actionType.replaceAll("[^A-Za-z]", "").toUpperCase());
		
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
			.add(
				rightContainer
			)
			.withExpand( rightContainer, 1f );
		
		mainContainer
			//.withWidth( "1170px" )
			.withStyleName( "mainlayout" )
			.withSpacing( true )
			.withMargin( new MarginInfo( false, false, false, false ) )
			.add( columnContainer );
		
		this
			.withUndefinedHeight()
			.add( mainContainer );
		
	}
	
	@EventBusListenerMethod( scope = EventScope.UI )
	public void onAuthenticate( LoginSucceedEvent event )
	{
		
	}


	public EventBus.UIEventBus getEventBus() {
		return eventBus;
	}

	public StardatDDIService getCvManagerService() {
		return stardatDDIService;
	}

	public ActionType getActionType() {
		return actionType;
	}

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
