package eu.cessda.cvmanager.ui.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.gesis.wts.security.SecurityService;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.gesis.wts.ui.view.LoginView;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.repository.search.VocabularySearchRepository;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyChangeService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.layout.EditorSearchActionLayout;
import eu.cessda.cvmanager.ui.view.publication.EsQueryResultDetail;
import eu.cessda.cvmanager.ui.view.publication.FiltersLayout;
import eu.cessda.cvmanager.ui.view.publication.PaginationBar;
import eu.cessda.cvmanager.ui.view.publication.VocabularyGridRow;


@UIScope
@SpringView(name = EditorSearchView.VIEW_NAME)
public class EditorSearchView extends CvView {

	private static final long serialVersionUID = -2479053676589191249L;
	public static final String VIEW_NAME = "editor-search";
	public static final String FIELD_SORT = "notation";
	private Locale locale = UI.getCurrent().getLocale();
	private final VocabularyService vocabularyService;
	private final VersionService versionService;

	// main container
	private MCssLayout resultLayout = new MCssLayout();
	private MCssLayout gridResultLayout = new MCssLayout();
	
	private MGrid<VocabularyDTO> cvGrid = new MGrid<>( VocabularyDTO.class );
	
	private MCssLayout searchTopLayout = new MCssLayout();
	private MCssLayout sortButtonLayout = new MCssLayout();
	private MButton sortByRelevence = new MButton( "Relevance" );
	private MButton sortByTitle = new MButton( "A-Z" );
	private MLabel resultInfo = new MLabel();
	
	private Map<Long, AgencyDTO> agencyMap = new HashMap<>();
	private FiltersLayout filterLayout;
	private EditorSearchActionLayout editorSearchActionLayout;
	
	private PaginationBar paginationBar;
	private EsQueryResultDetail esQueryResultDetail = new EsQueryResultDetail( EditorSearchView.VIEW_NAME );
	
	private final VocabularyChangeService vocabularyChangeService;

	private final FiltersLayout.FilterListener filterListener = ( fieldName, activeFilters) -> {
		esQueryResultDetail.resetPaging();
		esQueryResultDetail.getEsFilterByField( fieldName ).ifPresent( esFilter -> esFilter.setValues( activeFilters ));
		refreshSearchResult();
	};
	
	private final PaginationBar.PagingListener paggingListener = (page, pagesize) -> {
		esQueryResultDetail.setPage( PageRequest.of( page, pagesize ) );
		refreshSearchResult();
	};
	

	public EditorSearchView(I18N i18n, EventBus.UIEventBus eventBus, ConfigurationService configService,
			StardatDDIService stardatDDIService, SecurityService securityService, AgencyService agencyService,
			VocabularyService vocabularyService, VersionService versionService, CodeService codeService, VocabularySearchRepository vocabularySearchRepository,
			VocabularyChangeService vocabularyChangeService) {
		super(i18n, eventBus, configService, stardatDDIService, securityService, agencyService, vocabularyService, codeService, vocabularySearchRepository, EditorSearchView.VIEW_NAME);
		this.vocabularyService = vocabularyService;
		this.vocabularyChangeService = vocabularyChangeService;
		this.versionService = versionService;
		eventBus.subscribe(this, EditorSearchView.VIEW_NAME);
	}

	@PostConstruct
	public void init() {
		LoginView.NAVIGATETO_VIEWNAME = EditorSearchView.VIEW_NAME;
		paginationBar = new PaginationBar( paggingListener , i18n);
		filterLayout = new FiltersLayout( "block.filter", "block.filter.show", null, this, filterListener, i18n );
		editorSearchActionLayout = new EditorSearchActionLayout("block.action", "block.action.show", i18n, stardatDDIService, agencyService, vocabularyService, 
				versionService, vocabularySearchRepository, eventBus, vocabularyChangeService);
		esQueryResultDetail.setSort( new Sort(Sort.Direction.ASC, FIELD_SORT) );
		
		// button style
		sortByRelevence.setStyleName( "groupButton disable" );
		sortByTitle.setStyleName( "groupButton enable" );
		
		resultInfo.withContentMode( ContentMode.HTML );
		cvGrid
			.withStyleName(ValoTheme.TABLE_BORDERLESS, "undefined-height", "search-grid", "no-stripe")
			.withFullSize()
			.setSelectionMode(SelectionMode.NONE);
		
		sortByRelevence.addClickListener( e -> {
			if( esQueryResultDetail.getSearchTerm() != null && !esQueryResultDetail.getSearchTerm().isEmpty() ) {
				sortByRelevence.setStyleName( "groupButton enable" );
				sortByTitle.setStyleName( "groupButton disable" );
				esQueryResultDetail.setSort( new Sort(Sort.Direction.ASC, "_score") );
				refreshSearchResult();
			}
		});

		sortByTitle.addClickListener( e->{
			sortByRelevence.setStyleName( "groupButton disable" );
			sortByTitle.setStyleName( "groupButton enable" );
			esQueryResultDetail.setSort( new Sort(Sort.Direction.ASC, FIELD_SORT) );
			refreshSearchResult();
		});
		
		sortButtonLayout
			.withStyleName("pull-right")
			.add(
				sortByRelevence,
				sortByTitle
			);
		
		filterLayout.setWidth("100%");
				
		gridResultLayout.withStyleName( "result-container" );
		
		searchTopLayout
			.withStyleName("search-option")
			.add( 
				resultInfo,
				sortButtonLayout
			);
		
		resultLayout.add( 
				gridResultLayout
					.add( 
						cvGrid, 
						paginationBar 
					)
				)
			.withFullWidth();
		
		// assign to parent block
		topPanel.add( searchTopLayout );
		sidePanel
			.add( 
				filterLayout,
				editorSearchActionLayout
			);
		mainContainer.add( resultLayout );
			
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		
		if(!authorizeViewAccess())
			return;
		
		// activate home button
		topMenuButtonUpdateActive(1);
		
		locale = UI.getCurrent().getLocale();
		
		updateMessageStrings(locale);
		
		// reset query
		esQueryResultDetail.clear();
		// TODO: set query by query string
		
		refreshSearchResult();
		
		// action block check
		if( !SecurityUtils.isCurrentUserAllowCreateCvSl() )
			editorSearchActionLayout.setVisible( false );
		else
			editorSearchActionLayout.setVisible( true );
	}

	public void refreshSearchResult() {
		// set the query properties from url parameter
		
		// query
		esQueryResultDetail = vocabularyService.search( esQueryResultDetail );
		
		// set Main UI BreadcrumbItemMap
		breadcrumbItemMap.clear();
		breadcrumbItemMap.put("editor-search", EditorSearchView.VIEW_NAME );
		
		// set filter and pagination bar
		filterLayout.setFacetFilter(esQueryResultDetail);
		paginationBar.updateState(esQueryResultDetail);
		resultInfo.setValue( "<h3 class=\"result-info\"><strong>" + esQueryResultDetail.getVocabularies().getTotalElements() + " results found</strong></h3>");
		
		// update the result list
		cvGrid.setItems( esQueryResultDetail.getVocabularies().getContent() );
		cvGrid.removeAllColumns();
		cvGrid.setHeaderVisible(false);
		cvGrid.addColumn(voc -> {
			agency = agencyMap.get( voc.getAgencyId() );
			if( agency == null ) {
				agency = agencyService.findOne( voc.getAgencyId() );
				agencyMap.put( agency.getId(), agency);
			}
			return new VocabularyGridRow(voc, agency, configService);
		}, new ComponentRenderer()).setId("cvColumn");
		// results.setRowHeight( 135.0 );
		cvGrid.getColumn("cvColumn").setExpandRatio(1);
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
//		searchTextField.setPlaceholder(i18n.get("view.search.query.text.search.prompt", locale));
	}

	public EsQueryResultDetail getEsQueryResultDetail() {
		return esQueryResultDetail;
	}

	public void setEsQueryResultDetail(EsQueryResultDetail esQueryResultDetail) {
		this.esQueryResultDetail = esQueryResultDetail;
	}
	
	@EventBusListenerMethod( scope = EventScope.UI )
	public void eventHandle( CvManagerEvent.Event event)
	{
		switch(event.getType()) {
			case VOCABULARY_EDITOR_SEARCH:
				esQueryResultDetail.clear();
				esQueryResultDetail.setSearchTerm( (String) event.getPayload() );
				sortByRelevence.setStyleName( "groupButton enable" );
				sortByTitle.setStyleName( "groupButton disable" );
				esQueryResultDetail.setSort( new Sort(Sort.Direction.ASC, "_score") );
				refreshSearchResult();
				break;
			default:
				break;
		}
	}
}
