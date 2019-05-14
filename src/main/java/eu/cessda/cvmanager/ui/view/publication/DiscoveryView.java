package eu.cessda.cvmanager.ui.view.publication;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.gesis.wts.security.SecurityService;
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

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.view.CvView;

@UIScope
@SpringView(name = DiscoveryView.VIEW_NAME)
public class DiscoveryView extends CvView {

	private static final long serialVersionUID = -2479053676589191249L;
	public static final String VIEW_NAME = "discover";
	public static final String FIELD_SORT = "notation";
	private Locale locale = UI.getCurrent().getLocale();
	private final VocabularyService vocabularyService;

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
	private PaginationBar paginationBar;
	private EsQueryResultDetail esQueryResultDetail = new EsQueryResultDetail( DiscoveryView.VIEW_NAME );
	
	private final FiltersLayout.FilterListener filterListener = ( fieldName, activeFilters) -> {
		esQueryResultDetail.resetPaging();
		esQueryResultDetail.getEsFilterByField( fieldName ).ifPresent( esFilter -> esFilter.setValues( activeFilters ));
		refreshSearchResult();
	};
	
	private final PaginationBar.PagingListener paggingListener = (page, pagesize) -> {
		esQueryResultDetail.setPage( PageRequest.of( page, pagesize ) );
		refreshSearchResult();
	};

	public DiscoveryView(I18N i18n, EventBus.UIEventBus eventBus, ConfigurationService configService,
			StardatDDIService stardatDDIService, SecurityService securityService, AgencyService agencyService,
			VocabularyService vocabularyService, CodeService codeService) {
		super(i18n, eventBus, configService, stardatDDIService, securityService, agencyService, vocabularyService,  codeService, DiscoveryView.VIEW_NAME);
		this.vocabularyService = vocabularyService;
		eventBus.subscribe(this, DiscoveryView.VIEW_NAME);
	}

	@PostConstruct
	public void init() {
		LoginView.NAVIGATETO_VIEWNAME = DiscoveryView.VIEW_NAME;
		paginationBar = new PaginationBar( paggingListener , i18n);
		filterLayout = new FiltersLayout( "block.filter", "block.filter.show", this, null, filterListener, i18n );
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
		cvGrid.removeAllColumns();
		cvGrid.setHeaderVisible(false);
		cvGrid.addColumn(voc -> {
			agency = agencyMap.get( voc.getAgencyId() );
			if( agency == null ) {
				agency = agencyService.findOne( voc.getAgencyId() );
				agencyMap.put( agency.getId(), agency);
			}
			return new VocabularyGridRowPublish(voc, agency, configService);
		}, new ComponentRenderer()).setId("cvColumn");
		cvGrid.getColumn("cvColumn").setExpandRatio(1);
		
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
			.withStyleName("discover-page")
			.add( filterLayout );
		mainContainer.add( resultLayout );
			
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// activate home button
		topMenuButtonUpdateActive(0);
				
		super.enter(event);
		
		locale = UI.getCurrent().getLocale();
		
		updateMessageStrings(locale);
		
		// reset query
		esQueryResultDetail.clear();
		// TODO: set query by query string
		
		refreshSearchResult();
	}

	public void refreshSearchResult() {
		// set the query properties from url parameter
		
		// query
		esQueryResultDetail = vocabularyService.searchPublished( esQueryResultDetail );
		
		// set Main UI BreadcrumbItemMap
		breadcrumbItemMap.clear();
		breadcrumbItemMap.put("search", DiscoveryView.VIEW_NAME );
		
		// set filter and pagination bar
		filterLayout.setFacetFilter(esQueryResultDetail);
		paginationBar.updateState(esQueryResultDetail);
		resultInfo.withStyleName("result-info").setValue( esQueryResultDetail.getVocabularies().getTotalElements() + " results found" );
		
		// update the result list
		cvGrid.setDataProvider( new ListDataProvider<>( esQueryResultDetail.getVocabularies().getContent() ));
		cvGrid.getDataProvider().refreshAll();
		

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
			case VOCABULARY_SEARCH:
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
