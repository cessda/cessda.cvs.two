package eu.cessda.cvmanager.ui.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.view.publication.PaginationBar;
import eu.cessda.cvmanager.ui.view.publication.VocabularyGridRow;
import eu.cessda.cvmanager.ui.view.publication.VocabularyWithdrawnGridRow;

public class WithdrawnCvLayout extends MCssLayout implements Translatable {
	
	private static final long serialVersionUID = 1L;
	private final I18N i18n;
	private final ConfigurationService configService;
	private final VocabularyService vocabularyService;
	private final AgencyService agencyService;
	
	private AgencyDTO agency;
	
	private Sort sort = Sort.by(Direction.ASC, "notation");
	private Pageable pageable = PageRequest.of( 0, 10, sort );
	
	private MCssLayout searchTopLayout = new MCssLayout();
	private MLabel resultInfo = new MLabel();
	
	private Page<VocabularyDTO> vocabularies;
	private PaginationBar paginationBar;
	private MGrid<VocabularyDTO> cvGrid = new MGrid<>( VocabularyDTO.class );
	
	private Map<Long, AgencyDTO> agencyMap = new HashMap<>();
	
	private final PaginationBar.PagingListener paggingListener = (page, pagesize) -> {
		setPageable( PageRequest.of( page, pagesize, sort ));
		updateList();
	};
	
	public WithdrawnCvLayout(I18N i18n, VocabularyService vocabularyService, 
			AgencyService agencyService, ConfigurationService configService) {
		super();
		this.i18n = i18n;
		this.vocabularyService = vocabularyService;
		this.agencyService = agencyService;
		this.configService = configService;
		
		init();
		updateList();
	}
	
	private void init() {
		paginationBar = new PaginationBar( paggingListener , i18n);
		
		resultInfo
			.withStyleName("search-hit-info")
			.withContentMode( ContentMode.HTML );
		
		searchTopLayout
			.withStyleName("search-option-flex")
			.add( 
				resultInfo
			);
		
		cvGrid
			.withStyleName(ValoTheme.TABLE_BORDERLESS, "undefined-height", "search-grid", "no-stripe")
			.withFullSize()
			.setSelectionMode(SelectionMode.NONE);
		cvGrid.setHeaderVisible(false);
	
		this
			.add( 
				searchTopLayout,
				cvGrid,
				paginationBar
			);
	}
	
	public void updateList() {
		if( agency == null )
			vocabularies = vocabularyService.findAllWithdrawn(pageable);
		else
			vocabularies = vocabularyService.findAllWithdrawn(agency.getId(),pageable);
		
		cvGrid.setItems( vocabularies.getContent() );
		cvGrid.removeAllColumns();
		cvGrid.setHeaderVisible(false);
		cvGrid.addColumn(voc -> {
			agency = agencyMap.get( voc.getAgencyId() );
			if( agency == null ) {
				agency = agencyService.findOne( voc.getAgencyId() );
				agencyMap.put( agency.getId(), agency);
			}
			return new VocabularyWithdrawnGridRow(voc, agency, configService);
		}, new ComponentRenderer()).setId("cvColumn");
		// results.setRowHeight( 135.0 );
		cvGrid.getColumn("cvColumn").setExpandRatio(1);
		
		paginationBar.updateState(pageable, vocabularies.getTotalPages());
		paginationBar.setVisible( true );
		resultInfo.setValue( "<h3 class=\"result-info\"><strong>" + vocabularies.getTotalElements() + " withdrawn CVs</strong></h3>");
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

	public AgencyDTO getAgency() {
		return agency;
	}

	public void setAgency(AgencyDTO agency) {
		this.agency = agency;
	}
	
}
