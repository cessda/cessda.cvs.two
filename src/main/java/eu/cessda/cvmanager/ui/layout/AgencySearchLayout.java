package eu.cessda.cvmanager.ui.layout;

import java.util.Locale;


import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.ui.component.AgencyGridComponent;
import eu.cessda.cvmanager.ui.view.AgencyView;
import eu.cessda.cvmanager.ui.view.publication.PaginationBar;

public class AgencySearchLayout extends MCssLayout implements Translatable {
	
	private static final long serialVersionUID = -2461005203070668382L;
	private final I18N i18n;
	private final UIEventBus eventBus;
	private final AgencyView agencyView;
	private final AgencyService agencyService;
	private final ConfigurationService configurationService;
	
	private MCssLayout layout = new MCssLayout();

	private MCssLayout gridResultLayout = new MCssLayout();
	private MGrid<AgencyDTO> agencyGrid = new MGrid<>(AgencyDTO.class);
	private PaginationBar paginationBar;
	private Page<AgencyDTO> agencies;
	
	private Sort sort = Sort.by(Direction.ASC, "name");
	private Pageable pageable = PageRequest.of( 0, 10, sort );
	 
	private final PaginationBar.PagingListener paggingListener = (page, pagesize) -> {
		setPageable( PageRequest.of( page, pagesize, sort ));
		updateList();
	};
	
	public AgencySearchLayout(I18N i18n,  UIEventBus eventBus,
			AgencyView agencyView, AgencyService agencyService,
			ConfigurationService configurationService) {
		super();
		this.i18n = i18n;
		this.agencyView = agencyView;
		this.agencyService = agencyService;
		this.eventBus = eventBus;
		this.configurationService = configurationService;
		
		init();
	}

	private void init() {
		paginationBar = new PaginationBar( paggingListener , i18n);
		
		agencyGrid
			.withStyleName(ValoTheme.TABLE_BORDERLESS, "undefined-height", "search-grid")
			.withFullSize()
			.setHeaderVisible(false);
		agencyGrid.setSelectionMode(SelectionMode.NONE);
		
		updateList();

		gridResultLayout.withStyleName( "result-container" );
		gridResultLayout
			.add( 
				agencyGrid, 
				paginationBar 
			);

		layout
			.add( gridResultLayout )
			.withFullSize();
		
		this.add( layout);
	}
	
	public void updateList() {
		if( agencyView.getSearchTf().getValue() == null || agencyView.getSearchTf().getValue().isEmpty())
			agencies = agencyService.findAll(pageable);
		else
			agencies = agencyService.findAllByKeyword(agencyView.getSearchTf().getValue(), pageable);
		agencyGrid.setItems( agencies.getContent() );
		
		agencyGrid.removeAllColumns();
		agencyGrid.addColumn(agency -> {
			return new AgencyGridComponent( agencyView, agency, configurationService, agencyView.getSearchTf().getValue());
		}, new ComponentRenderer()).setId("agencyComp");
		// results.setRowHeight( 135.0 );
		agencyGrid.getColumn("agencyComp").setExpandRatio(1);
		
		paginationBar.updateState(pageable,  agencies.getTotalPages());
		paginationBar.setVisible( true );
		
		agencyView.getResultInfo().setValue( "<h3 class=\"result-info\"><strong>" + agencies.getTotalElements() + " agencies</strong></h3>");
	}

	@Override
	public void updateMessageStrings(Locale locale) {
	}

	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}
	
	
}
