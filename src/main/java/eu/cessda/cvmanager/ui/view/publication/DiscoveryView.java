package eu.cessda.cvmanager.ui.view.publication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
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
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.component.CvSchemeComponent;
import eu.cessda.cvmanager.ui.view.CvManagerView;
import eu.cessda.cvmanager.ui.view.GesisPagination;
import eu.cessda.cvmanager.ui.view.HelpWindow;

@UIScope
@SpringView(name = DiscoveryView.VIEW_NAME)
public class DiscoveryView extends CvPublicationView {

	public static final String VIEW_NAME = "discover";
	public static final String FIELD_SORT = "notation";
	private Locale locale = UI.getCurrent().getLocale();
	private final VocabularyService vocabularyService;

	// main container
	private MVerticalLayout mainLayout = new MVerticalLayout();
	private MHorizontalLayout resultLayout = new MHorizontalLayout();
	
	private MGrid<VocabularyDTO> cvGrid = new MGrid<>( VocabularyDTO.class );
	
	private MCssLayout sortButtonLayout = new MCssLayout();
	private MButton sortByRelevence = new MButton( "Relevance" );
	private MButton sortByTitle = new MButton( "A-Z" );
	private MTextField searchTextField = new MTextField();
	private MLabel resultInfo = new MLabel();
	
	private Map<Long, AgencyDTO> agencyMap = new HashMap<>();
	private FiltersLayout filterLayout;
	private PaginationBar paginationBar;
	private EsQueryResultDetail esQueryResultDetail = new EsQueryResultDetail();
	
	private ArrayList<CVScheme> hits = new ArrayList<>();

	private final FiltersLayout.FilterListener filterListener = filterItems -> {
		esQueryResultDetail.resetPaging();
		esQueryResultDetail.setFilterItems(filterItems);
		refreshSearchResult();
	};
	
	private final PaginationBar.PagingListener paggingListener = (page, pagesize) -> {
		esQueryResultDetail.setPage( PageRequest.of( page, pagesize ) );
		refreshSearchResult();
	};
	

	public DiscoveryView(I18N i18n, EventBus.UIEventBus eventBus, ConfigurationService configService,
			StardatDDIService stardatDDIService, SecurityService securityService, AgencyService agencyService,
			VocabularyService vocabularyService, CodeService codeService) {
		super(i18n, eventBus, configService, stardatDDIService, securityService, agencyService, vocabularyService, codeService, DiscoveryView.VIEW_NAME);
		this.vocabularyService = vocabularyService;
		eventBus.subscribe(this, DiscoveryView.VIEW_NAME);
	}

	@PostConstruct
	public void init() {
		LoginView.NAVIGATETO_VIEWNAME = DiscoveryView.VIEW_NAME;
		paginationBar = new PaginationBar( paggingListener , i18n);
		filterLayout = new FiltersLayout( this, filterListener, i18n );
		esQueryResultDetail.setSort( new Sort(Sort.Direction.ASC, FIELD_SORT) );
		
		// button style
		sortByRelevence.setStyleName( "groupButton disable" );
		sortByTitle.setStyleName( "groupButton enable" );

		// refresh initial result
		refreshSearchResult();
		
		resultInfo.withContentMode( ContentMode.HTML );
		cvGrid
			.withStyleName(ValoTheme.TABLE_BORDERLESS, "undefined-height", "search-grid", "no-stripe")
			.withFullSize()
			.setSelectionMode(SelectionMode.NONE);
		
		searchTextField
			.withWidth( "300px" )
			.withValueChangeMode( ValueChangeMode.TIMEOUT)
			.withValueChangeTimeout( 500 )
			.addTextChangeListener( e -> {
				esQueryResultDetail.clear();
				esQueryResultDetail.setSearchTerm( e.getValue() );
				sortByRelevence.setStyleName( "groupButton enable" );
				sortByTitle.setStyleName( "groupButton disable" );
				esQueryResultDetail.setSort( new Sort(Sort.Direction.ASC, "_score") );
				refreshSearchResult();
			});
		
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
		
		sortButtonLayout.add(
				sortByRelevence,
				sortByTitle
			);
		
		filterLayout.setWidth("250px");
		
		resultLayout.add( 
				filterLayout,
				new MVerticalLayout( 
						cvGrid, 
						paginationBar )
				.withMargin( false )
				.withSpacing( false ) 
				)
			.withFullWidth()
			.withSpacing( false )
			.withMargin( false )
			.withExpand( resultLayout.getComponent( 1 ), 1.0f );
		
		mainLayout
		.add( 
			new MVerticalLayout(
				new MHorizontalLayout(
					resultInfo,
					sortButtonLayout,
					searchTextField
					)
				.withAlign( resultInfo, Alignment.TOP_LEFT )
				.withAlign( searchTextField, Alignment.TOP_RIGHT )
				.withFullWidth()
				,resultLayout )
		
		.withMargin( false ) );
		
		rightContainer.add( mainLayout ).withExpand( mainLayout , 1);
			
	}

	@Override
	public void enter(ViewChangeEvent event) {
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
		esQueryResultDetail = vocabularyService.search( esQueryResultDetail );
		
		// set filter and pagination bar
		filterLayout.setFacetFilter(esQueryResultDetail);
		paginationBar.updateState(esQueryResultDetail);
		resultInfo.setValue( "<h3 class=\"result-info\"><strong>" + esQueryResultDetail.getVocabularies().getTotalElements() + " results found</strong></h3>");
		
		// update the result list
		cvGrid.setItems( esQueryResultDetail.getVocabularies().getContent() );
		cvGrid.removeAllColumns();
		cvGrid.setHeaderVisible(false);
		cvGrid.addColumn(voc -> {
			agency = agencyService.findOne( voc.getAgencyId() );
			return new VocabularyGridRow(voc, agency, configService,  searchTextField.getValue());
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
		searchTextField.setPlaceholder(i18n.get("view.search.query.text.search.prompt", locale));
	}

	public ArrayList<CVScheme> getHits() {
		return hits;
	}

	public void setHits(ArrayList<CVScheme> hits) {
		this.hits = hits;
	}

	public EsQueryResultDetail getEsQueryResultDetail() {
		return esQueryResultDetail;
	}

	public void setEsQueryResultDetail(EsQueryResultDetail esQueryResultDetail) {
		this.esQueryResultDetail = esQueryResultDetail;
	}
	
}
