package eu.cessda.cvmanager.ui.layout;

import java.util.Arrays;
import java.util.Locale;

import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Image;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.view.AgencyView;
import eu.cessda.cvmanager.ui.view.AgencyView.ViewMode;
import eu.cessda.cvmanager.ui.view.publication.DiscoveryView;
import eu.cessda.cvmanager.ui.view.publication.EsFilter;
import eu.cessda.cvmanager.ui.view.publication.EsQueryResultDetail;
import eu.cessda.cvmanager.ui.view.publication.FiltersLayout;
import eu.cessda.cvmanager.ui.view.publication.PaginationBar;
import eu.cessda.cvmanager.ui.view.publication.VocabularyGridRowPublish;

public class AgencyDetailLayout extends MCssLayout implements Translatable {
	
	private static final long serialVersionUID = -2461005203070668382L;
	public static final String FIELD_SORT = "notation";
	private final I18N i18n;
	private final AgencyView agencyView;
	private final VocabularyService vocabularyService;
	private final ConfigurationService configService;
	
	private AgencyDTO agency;
	
	private final String logoPath = "img/noimage.png";
	
	private MCssLayout layout = new MCssLayout();
	private MCssLayout headerLayout = new MCssLayout();
	private MCssLayout logoLayout = new MCssLayout();
	private MCssLayout titleLayout = new MCssLayout();
	private MCssLayout searchTopLayout = new MCssLayout();
	private MLabel headTitle = new MLabel();
	private MButton backButton = new MButton("Back");
	
	// main container
	private MCssLayout resultLayout = new MCssLayout();
	private MCssLayout gridResultLayout = new MCssLayout();
	private MLabel resultInfo = new MLabel();
	private MGrid<VocabularyDTO> cvGrid = new MGrid<>( VocabularyDTO.class );

	private PaginationBar paginationBar;
	private EsQueryResultDetail esQueryResultDetail = new EsQueryResultDetail( DiscoveryView.VIEW_NAME );
		
	private final PaginationBar.PagingListener paggingListener = (page, pagesize) -> {
		esQueryResultDetail.setPage( PageRequest.of( page, pagesize ) );
		refreshSearchResult();
	};
	
	public AgencyDetailLayout(I18N i18n,  UIEventBus eventBus,
			AgencyView agencyView, AgencyService agencyService,
			 ConfigurationService configurationService, 
			 VocabularyService vocabularyService, StardatDDIService stardatDDIService, 
			 ConfigurationService configService) {
		super();
		this.i18n = i18n;
		this.agencyView = agencyView;
		this.vocabularyService = vocabularyService;
		this.configService = configService;
		
		initLayout();
	
		this.add( layout);
	}
	

	private void initLayout() {
		
		paginationBar = new PaginationBar( paggingListener , i18n);
		esQueryResultDetail.setSort( new Sort(Sort.Direction.ASC, FIELD_SORT) );
		
		cvGrid
			.withStyleName(ValoTheme.TABLE_BORDERLESS, "undefined-height", "search-grid", "no-stripe")
			.withFullSize()
			.setSelectionMode(SelectionMode.NONE);
		
		resultInfo.withContentMode( ContentMode.HTML );
		
		backButton
			.withStyleName( "pull-right" )
			.addClickListener( e -> {
				agencyView.setAgency( null , ViewMode.INITIAL);
			});
		
		titleLayout
			.withStyleName( "pull-left" )
			.add( headTitle );
		
		headerLayout
			.withFullWidth()
			.add(logoLayout, 
				titleLayout,
				backButton);
		
		searchTopLayout
			.withStyleName("search-option")
			.add( 
				resultInfo
			);
		
		resultLayout.add( 
				gridResultLayout
					.add( 
						searchTopLayout,
						cvGrid, 
						paginationBar 
					)
				)
			.withFullWidth();
				
		layout
			.withFullWidth()
			.add( headerLayout , resultLayout);
		
	}
	
	
	public void setAgency(AgencyDTO agency) {
		this.agency = agency;
		
		// update breadcrumb
		agencyView.setBreadcrumb(agency);

		
		setHeaderContent();
		
		// filter to current agency
		esQueryResultDetail.getEsFilterByField( FiltersLayout.AGENCY_AGG ).ifPresent( esFilter -> esFilter.setValues(Arrays.asList( agency.getName())));

		refreshSearchResult();
		
	}
	
	private void refreshSearchResult() {
		
		// query
		esQueryResultDetail = vocabularyService.searchPublished( esQueryResultDetail );
		
		// set filter and pagination bar
		paginationBar.updateState(esQueryResultDetail);
		resultInfo.setValue( "<h3 class=\"result-info\"><strong>" + esQueryResultDetail.getVocabularies().getTotalElements() + " CVs</strong></h3>");
		
		// update the result list
		cvGrid.setItems( esQueryResultDetail.getVocabularies().getContent() );
		cvGrid.removeAllColumns();
		cvGrid.setHeaderVisible(false);
		cvGrid.addColumn(voc -> {
			return new VocabularyGridRowPublish(voc, agency, configService);
		}, new ComponentRenderer()).setId("cvColumn");
		// results.setRowHeight( 135.0 );
		cvGrid.getColumn("cvColumn").setExpandRatio(1);
	}

	private void setHeaderContent() {
		MLabel logoLabel = new MLabel()
				.withContentMode( ContentMode.HTML )
				.withWidth("200px");
		
		if( agency.getLogo() != null && !agency.getLogo().isEmpty())
			logoLabel.setValue(  "<img style=\"max-width:200px;max-height:100px\" alt=\"" + agency.getName() + " logo\" src='" + agency.getLogo() + "'>");
		
		headTitle
			.withContentMode( ContentMode.HTML )
			.withValue( "<h2 style=\"margin-top:0\">" + agency.getName() + "</h2><strong>" + agency.getDescription() + "</strong>" );
		
		logoLayout.removeAllComponents();
		
		logoLayout
			.withStyleName( "pull-left", "margin-right15px" )
			.add( logoLabel );
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		
	}
}
