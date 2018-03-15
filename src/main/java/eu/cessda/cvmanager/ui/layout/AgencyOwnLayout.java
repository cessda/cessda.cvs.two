package eu.cessda.cvmanager.ui.layout;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.gesis.wts.domain.Agency;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.security.UserDetails;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.ui.CVManagerUI;
import eu.cessda.cvmanager.ui.component.AgencyGridComponent;
import eu.cessda.cvmanager.ui.view.AgencyView;
import eu.cessda.cvmanager.ui.view.GesisPagination;

public class AgencyOwnLayout extends MCssLayout implements Translatable {
	
	private static final long serialVersionUID = -2461005203070668382L;
	private final I18N i18n;
	private final UIEventBus eventBus;
	private final AgencyView agencyView;
	private final AgencyService agencyService;
	private final ConfigurationService configurationService;
	
	private MCssLayout layout = new MCssLayout();
	private Label header = new Label("<h3>My Agencies</h3>", ContentMode.HTML);
	
	// main container
	private HorizontalLayout globalContainer = new HorizontalLayout();
	private VerticalLayout searchGlobalContainer = new VerticalLayout();
	private MLabel infoResult = new MLabel();
	// results area
	private VerticalLayout resultsContainer = new VerticalLayout();
	
	private Grid<AgencyDTO> agencyGrid = new Grid<>(AgencyDTO.class);
	private List<AgencyDTO> agencies;
	
	public AgencyOwnLayout(I18N i18n,  UIEventBus eventBus,
			AgencyView agencyView, AgencyService agencyService,
			ConfigurationService configurationService) {
		super();
		this.i18n = i18n;
		this.agencyView = agencyView;
		this.agencyService = agencyService;
		this.eventBus = eventBus;
		this.configurationService = configurationService;
				
		initLayout();
	}

	private void initLayout() {
		// the layout that contains the three zones: filters, search box, and
		// results
		this.globalContainer.setSpacing(false);
		this.globalContainer.setMargin(false);
		this.globalContainer.setSizeFull();

		// the zone of: search box and results
		this.searchGlobalContainer.setSpacing(false);
		this.searchGlobalContainer.setMargin(false);
		this.searchGlobalContainer.setSizeFull();

		// results zone
		this.resultsContainer.setSpacing(false);
		this.resultsContainer.setMargin(false);
		this.resultsContainer.setSizeFull();
		this.resultsContainer.addComponent(this.initResultsContainer());

		this.searchGlobalContainer.addComponents( resultsContainer);
		this.searchGlobalContainer.setExpandRatio(resultsContainer, 1);

		this.globalContainer.addComponents(searchGlobalContainer);
		this.globalContainer.setExpandRatio(searchGlobalContainer, 1);
		
		layout
			.add( globalContainer )
			.withFullSize();
		
		this.add( layout);
	}
	
	public void updateList() {
		agencyGrid.removeAllColumns();
		
		agencies = agencyService.findByUserId( SecurityUtils.getLoggedUser().getId() );
		agencyGrid.setItems( agencies );
		
		agencyGrid.addColumn(agency -> {
			return new AgencyGridComponent(agencyView, agency, configurationService, null);
		}, new ComponentRenderer()).setId("agencyComp");
		// results.setRowHeight( 135.0 );
		agencyGrid.getColumn("agencyComp").setExpandRatio(1);
		
		if (agencies.isEmpty())
			infoResult.setValue("Showing 0 of 0");
		else
			infoResult.setValue("Showing 1 - " + agencies.size() + " of " + agencies.size());
	}

	/**
	 * Initialize the results zone, with all necessary components and listeners.
	 * Especially: results grid + pagination
	 * 
	 * @param hits:
	 *            a list of results
	 * @return a vertical layout containing all components of this zone
	 */
	private Component initResultsContainer() {

		VerticalLayout layout = new VerticalLayout();
		
		agencyGrid.addStyleNames(ValoTheme.TABLE_BORDERLESS, "undefined-height", "search-grid");
		agencyGrid.setHeaderVisible(false);
		agencyGrid.setSelectionMode(SelectionMode.NONE);
		agencyGrid.setSizeFull();

		// add clicking listener to show or hide the detail window of a
		// particular row of the grid
		agencyGrid.addItemClickListener(e -> {

		});
		
		layout.addComponents(header, agencyGrid);
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();
		layout.setExpandRatio(agencyGrid, 1);

		return layout;

	}

	@Override
	public void updateMessageStrings(Locale locale) {
		// TODO Auto-generated method stub
		
	}
}
