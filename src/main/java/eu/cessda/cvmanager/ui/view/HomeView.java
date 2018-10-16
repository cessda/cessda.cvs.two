package eu.cessda.cvmanager.ui.view;

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
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.ui.view.LoginView;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
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
import eu.cessda.cvmanager.ui.component.CvSchemeComponent;

@UIScope
@SpringView(name = HomeView.VIEW_NAME)
public class HomeView extends CvManagerView {

	private static final long serialVersionUID = 6904286186508174249L;
	public static final String VIEW_NAME = "Home";
	private Locale locale = UI.getCurrent().getLocale();

	// main container
	private HorizontalLayout globalContainer = new HorizontalLayout();
	private VerticalLayout filtersContainer = new VerticalLayout();
	private VerticalLayout searchGlobalContainer = new VerticalLayout();
	// search box area
	private VerticalLayout searchBoxContainer = new VerticalLayout();
	private TextField searchBox;
	private MButton searchButton = new MButton(VaadinIcons.SEARCH, this::doSearchCv);
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
	
	private ArrayList<CVScheme> hits = new ArrayList<>();

	// The opened search hit at the results grid (null at the begining)
	// private SearchHit selectedItem = null;

	public HomeView(I18N i18n, EventBus.UIEventBus eventBus, ConfigurationService configService,
			StardatDDIService stardatDDIService, SecurityService securityService, AgencyService agencyService, 
			VocabularyService vocabularyService, CodeService codeService) {
		super(i18n, eventBus, configService, stardatDDIService, securityService, agencyService, vocabularyService, codeService, HomeView.VIEW_NAME);
		eventBus.subscribe(this, HomeView.VIEW_NAME);
	}

	@PostConstruct
	public void init() {
		LoginView.NAVIGATETO_VIEWNAME = HomeView.VIEW_NAME;

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
		this.searchBoxContainer.addComponent(this.initSearchBoxContainer());

		// results zone
		this.resultsContainer.setSpacing(false);
		this.resultsContainer.setMargin(false);
		this.resultsContainer.setSizeFull();
		this.resultsContainer.addComponent(this.initResultsContainer());

		this.searchGlobalContainer.addComponents(searchBoxContainer, resultsContainer);
		this.searchGlobalContainer.setExpandRatio(resultsContainer, 1);

		this.globalContainer.addComponents(filtersContainer, searchGlobalContainer);
		this.globalContainer.setExpandRatio(searchGlobalContainer, 1);

		rightContainer.add(globalContainer).withExpand(globalContainer, 1);

		resetSearch();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		locale = UI.getCurrent().getLocale();
		updateMessageStrings(locale);
	}

	/**
	 * Initialize the search box zone, with all necessary components and listeners
	 * 
	 * @return a horizontal layout containing all components of this zone
	 */
	private com.vaadin.ui.Component initSearchBoxContainer() {

		VerticalLayout container = new VerticalLayout();

		HorizontalLayout layout = new HorizontalLayout();
		layout.setSizeFull();

		this.searchBox = new TextField();
		this.searchBox.setSizeFull();

		this.searchButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
		this.searchButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		this.showAllStudiesButton = new Button("View all CVs");
		this.showAllStudiesButton.setStyleName(ValoTheme.BUTTON_LINK);
		this.showAllStudiesButton.addClickListener(e -> {

			// reinitialize saved query
		});

		clearButton.withCaption("Clear search").withStyleName(ValoTheme.BUTTON_LINK).addClickListener(e -> {
			resetSearch();
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
				hits.sort(new Comparator<CVScheme>() {
					@Override
					public int compare(CVScheme c1, CVScheme c2) {
						return c1.getCode().compareTo(c2.getCode());
					}
				});
				updateResultsContainer();
			}
		});

		filterOption.withFullWidth().add(
				perPageResult.add(new MLabel(i18n.get("view.search.query.label.perpage", locale)), perPageComboBox),
				infoResult, sortResult.add(new MLabel(i18n.get("view.search.query.label.sort", locale)), sortComboBox));

		layout.addComponents(this.searchBox, this.searchButton /* , this.showAllStudiesButton */);
		layout.setExpandRatio(this.searchBox, 1);

		container.addComponents(layout, searchOption, filterOption);

		return container;

	}

	private void doSearchCv(ClickEvent event) {
		if (!searchBox.getValue().trim().equalsIgnoreCase("")) {

			Map<String, CVScheme> mapHits = new HashMap<String, CVScheme>();

			// initialize the new query
			List<DDIStore> searchResult = stardatDDIService.findByContentAndElementType(this.searchBox.getValue(),
					DDIElement.CVSCHEME);

			List<DDIStore> searchResult2 = stardatDDIService.findByContentAndElementType(this.searchBox.getValue(),
					DDIElement.CVCONCEPT);

			for (DDIStore store : searchResult2) {
				CVConcept concept = new CVConcept(store);

				List<DDIStore> ddiStoreScheme = stardatDDIService.findByIdAndElementType(concept.getContainerId(),
						DDIElement.CVSCHEME);

				if (ddiStoreScheme.size() == 1) {

					if (mapHits.containsKey(ddiStoreScheme.get(0).getElementId())) {
						CVScheme scheme = mapHits.get(ddiStoreScheme.get(0).getElementId());
						scheme.addConcept(concept);
					} else {
						CVScheme scheme = new CVScheme(ddiStoreScheme.get(0));
						scheme.addConcept(concept);
						mapHits.put(scheme.ddiStore.getElementId(), scheme);
					}

				}
			}

			for (DDIStore store : searchResult) {

				if (!mapHits.containsKey(store.getElementId())) {

					CVScheme scheme = new CVScheme(store);

					mapHits.put(scheme.ddiStore.getElementId(), scheme);
				}

			}

			hits = new ArrayList<CVScheme>(mapHits.values());

			updateResultsContainer();
		} else
			resetSearch();
	}

	@EventBusListenerMethod(scope = EventScope.UI)
	public void resetSearch(DDIStore ddiStore) {
		resetSearch();
	}

	private void resetSearch() {
		searchBox.setValue("");
		List<DDIStore> searchResult = stardatDDIService.findStudyByElementType(DDIElement.CVSCHEME);

		hits = new ArrayList<CVScheme>();
		for (DDIStore store : searchResult) {
			CVScheme scheme = new CVScheme(store);

			hits.add(scheme);
		}
		updateResultsContainer();
	}

	/**
	 * Initialize the results zone, with all necessary components and listeners.
	 * Especially: results grid + pagination
	 * 
	 * @param hits:
	 *            a list of results
	 * @return a vertical layout containing all components of this zone
	 */
	private com.vaadin.ui.Component initResultsContainer() {

		VerticalLayout layout = new VerticalLayout();

		if (hits.isEmpty())
			infoResult.setValue("Showing 0 of 0");
		else
			infoResult.setValue("Showing 1 - " + hits.size() + " of " + hits.size());

		Label header = new Label("<b>" + hits.size() + " CVs retrieved</b>", ContentMode.HTML);

		// initialize the results grid
		Grid<CVScheme> results = new Grid<>(CVScheme.class);
		results.setItems(hits);

		results.addStyleNames(ValoTheme.TABLE_BORDERLESS, "undefined-height", "search-grid");

		results.removeAllColumns();
		results.setHeaderVisible(false);
		results.addColumn(cvscheme -> {
			return new CvSchemeComponent(cvscheme, configService, this.searchBox.getValue());
		}, new ComponentRenderer()).setId("cvScemeComp");
		// results.setRowHeight( 135.0 );
		results.getColumn("cvScemeComp").setExpandRatio(1);

		results.setSelectionMode(SelectionMode.NONE);
		results.setSizeFull();

		// add clicking listener to show or hide the detail window of a
		// particular row of the grid
		results.addItemClickListener(e -> {

		});

		layout.addComponents(header, results, new GesisPagination<CVScheme>(results, hits, 10, 1));
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();
		layout.setExpandRatio(results, 1);

		return layout;

	}

	/**
	 * Update the results list
	 * 
	 * @param hits:
	 *            a hit list to be shown (the results list)
	 */

	public void updateResultsContainer() {

		// remove outdated results
		this.resultsContainer.removeAllComponents();

		this.resultsContainer.addComponent(this.initResultsContainer());

	}

	@EventBusListenerMethod(scope = EventScope.UI)
	public void eventHandle(CvManagerEvent.Event event) {
		switch (event.getType()) {
		case CVSCHEME_UPDATED:
			resetSearch();

			break;
		default:
			break;
		}
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
		searchBox.setPlaceholder(i18n.get("view.search.query.text.search.prompt", locale));

//		actionPanel.updateMessageStrings(locale);
	}

	public ArrayList<CVScheme> getHits() {
		return hits;
	}

	public void setHits(ArrayList<CVScheme> hits) {
		this.hits = hits;
	}
	
}
