package eu.cessda.cvmanager.ui.layout;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.ui.component.AgencyGridComponent;
import eu.cessda.cvmanager.ui.view.AgencyView;
import eu.cessda.cvmanager.ui.view.GesisPagination;
import eu.cessda.cvmanager.ui.view.HelpWindow;

public class AgencySearchLayout extends MCssLayout implements Translatable {
	
	private static final long serialVersionUID = -2461005203070668382L;
	private final I18N i18n;
	private final UIEventBus eventBus;
	private final AgencyView agencyView;
	private final AgencyService agencyService;
	private final ConfigurationService configurationService;
	
	private MCssLayout layout = new MCssLayout();
	
	// main container
	private HorizontalLayout globalContainer = new HorizontalLayout();
	private VerticalLayout filtersContainer = new VerticalLayout();
	private VerticalLayout searchGlobalContainer = new VerticalLayout();
	// search box area
	private VerticalLayout searchBoxContainer = new VerticalLayout();
	private TextField searchBox;
	private MButton searchButton = new MButton(VaadinIcons.SEARCH, this::doSearchAgency);
	private Button showAllStudiesButton;

	private MHorizontalLayout searchOption = new MHorizontalLayout();
	private MButton clearButton = new MButton();
	private MButton searchHelpButton = new MButton();

	private MHorizontalLayout filterOption = new MHorizontalLayout();
	private MHorizontalLayout perPageResult = new MHorizontalLayout();
	private ComboBox<String> perPageComboBox = new ComboBox<>();
	private MLabel infoResult = new MLabel();
	private MHorizontalLayout sortResult = new MHorizontalLayout();
	private ComboBox<String> sortComboBox = new ComboBox<>();
	// results area
	private VerticalLayout resultsContainer = new VerticalLayout();
	
	private Grid<AgencyDTO> agencyGrid = new Grid<>(AgencyDTO.class);
	private List<AgencyDTO> agencies;
	
	public AgencySearchLayout(I18N i18n,  UIEventBus eventBus,
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

		// filters zone
		this.filtersContainer.setSpacing(false);
		this.filtersContainer.setMargin(false);
		this.filtersContainer.setSizeFull();
		this.filtersContainer.setWidthUndefined();
		// this.filtersContainer.addComponent(this.initFiltersContainer());

		// the zone of: search box and results
		this.searchGlobalContainer.setSpacing(false);
		this.searchGlobalContainer.setMargin(false);
		this.searchGlobalContainer.setSizeFull();

		// search box zone
		this.searchBoxContainer.setSpacing(false);
		this.searchBoxContainer.setMargin(false);
		this.searchBoxContainer.setSizeFull();
		this.searchBoxContainer.setHeightUndefined();
		this.searchBoxContainer.addComponent(initSearchBoxContainer());

		// results zone
		this.resultsContainer.setSpacing(false);
		this.resultsContainer.setMargin(false);
		this.resultsContainer.setSizeFull();
		this.resultsContainer.addComponent(this.initResultsContainer());

		this.searchGlobalContainer.addComponents(searchBoxContainer, resultsContainer);
		this.searchGlobalContainer.setExpandRatio(resultsContainer, 1);

		this.globalContainer.addComponents(filtersContainer, searchGlobalContainer);
		this.globalContainer.setExpandRatio(searchGlobalContainer, 1);
		
		layout
			.add( globalContainer )
			.withFullSize();
		
		this.add( layout);
	}
	
	/**
	 * Initialize the search box zone, with all necessary components and listeners
	 * 
	 * @return a horizontal layout containing all components of this zone
	 */
	private Component initSearchBoxContainer() {

		VerticalLayout container = new VerticalLayout();

		HorizontalLayout layout = new HorizontalLayout();
		layout.setSizeFull();

		this.searchBox = new TextField();
		this.searchBox.setSizeFull();
		searchBox.addValueChangeListener( e -> updateList());

		this.searchButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
		this.searchButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		this.showAllStudiesButton = new Button("View all CVs");
		this.showAllStudiesButton.setStyleName(ValoTheme.BUTTON_LINK);
		this.showAllStudiesButton.addClickListener(e -> {

			// reinitialize saved query
		});

		clearButton.withCaption("Clear search").withStyleName(ValoTheme.BUTTON_LINK).addClickListener(e -> {
			updateList();
		});

		searchHelpButton.withCaption("Help").withStyleName(ValoTheme.BUTTON_LINK).addClickListener(e -> {
			HelpWindow help = new HelpWindow();
			UI.getCurrent().addWindow(help);
		});

		searchOption.withStyleName("searchoption").withMargin(false).add(clearButton, new Label("|"), searchHelpButton);

		// filter option
		perPageComboBox.setItems("10", "20", "50", "100", "200");
		perPageComboBox.setEmptySelectionAllowed(false);
		perPageComboBox.setWidth("100px");
		perPageComboBox.setValue("10");

		sortComboBox.setItems("Latest", "Relevance", "A-Z");
		sortComboBox.setEmptySelectionAllowed(false);
		sortComboBox.setWidth("120px");
		sortComboBox.setValue("Latest");
		sortComboBox.addValueChangeListener( e -> {
			if( e.getValue().equals("A-Z")) {
				agencies.sort(new Comparator<AgencyDTO>() {
					@Override
					public int compare(AgencyDTO c1, AgencyDTO c2) {
						return c1.getName().compareTo(c2.getName());
					}
				});
				updateList();
			}
		});

		filterOption.withFullWidth().add(
				perPageResult.add(new MLabel(i18n.get("view.search.query.label.perpage",agencyView.getLocale())), perPageComboBox),
				infoResult, sortResult.add(new MLabel(i18n.get("view.search.query.label.sort", agencyView.getLocale())), sortComboBox));

		layout.addComponents(this.searchBox, this.searchButton /* , this.showAllStudiesButton */);
		layout.setExpandRatio(this.searchBox, 1);

		container.addComponents(layout, searchOption, filterOption);

		return container;

	}

	private void doSearchAgency(ClickEvent event) {
		updateList();
	}
	
	public void updateList() {
		agencies = agencyService.findAll(searchBox.getValue());
		agencyGrid.setItems( agencies );
		
		agencyGrid.removeAllColumns();
		agencyGrid.addColumn(agency -> {
			return new AgencyGridComponent( agencyView, agency, configurationService, this.searchBox.getValue());
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
		
		// initialize the results grid
		updateList();

		Label header = new Label("<b>" + agencies.size() + " agencies</b>", ContentMode.HTML);

		agencyGrid.addStyleNames(ValoTheme.TABLE_BORDERLESS, "undefined-height", "search-grid");
		agencyGrid.setHeaderVisible(false);
		agencyGrid.setSelectionMode(SelectionMode.NONE);
		agencyGrid.setSizeFull();

		// add clicking listener to show or hide the detail window of a
		// particular row of the grid
		agencyGrid.addItemClickListener(e -> {

		});
		
		layout.addComponents(header, agencyGrid, new GesisPagination<AgencyDTO>(agencyGrid, agencies, 10, 1));
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();
		layout.setExpandRatio(agencyGrid, 1);

		return layout;

	}

	@Override
	public void updateMessageStrings(Locale locale) {
		searchBox.setPlaceholder(i18n.get("view.search.query.text.search.prompt", locale));
	}
}
