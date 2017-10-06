package eu.cessda.cvmanager.ui.view;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.gesis.security.util.LoginSucceedEvent;
import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.data.ValueProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.ui.component.CvSchemeComponent;

@UIScope
@SpringView(name = SearchView.VIEW_NAME)
public class SearchView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6904286186508174249L;
	public static final String VIEW_NAME = "Browse";
	
	private final ConfigurationService configService;

	// graphical components
	private VerticalLayout mainLayout = new VerticalLayout();
	// private Image headerImage = new Image();
	private Image footerImage = new Image();
	// main container
	private HorizontalLayout globalContainer = new HorizontalLayout();
	private VerticalLayout filtersContainer = new VerticalLayout();
	private VerticalLayout searchGlobalContainer = new VerticalLayout();
	// search box area
	private VerticalLayout searchBoxContainer = new VerticalLayout();
	private TextField searchBox;
	private Button searchButton;
	private Button showAllStudiesButton;
	
	private MHorizontalLayout searchOption = new MHorizontalLayout();
	private MButton clearButton = new MButton();
	private MButton searchHelpButton = new MButton();
	
	private MHorizontalLayout filterOption = new MHorizontalLayout();
	private MHorizontalLayout perPageResult = new MHorizontalLayout();
	private ComboBox perPageComboBox = new ComboBox();
	private MLabel infoResult = new MLabel();
	private MHorizontalLayout sortResult = new MHorizontalLayout();
	private ComboBox sortComboBox = new ComboBox();
	// results area
	private VerticalLayout resultsContainer = new VerticalLayout();

	// The opened search hit at the results grid (null at the begining)
	// private SearchHit selectedItem = null;

	private RestClient client = new RestClient("http://localhost:8080/stardat-ddiflatdb");
	
	public SearchView(ConfigurationService configService) {
		this.configService = configService;
	}
	
	@PostConstruct
	public void init() {

		this.setHeightUndefined();

		// the layout that contains all
//		this.mainLayout.setSpacing(true);
//		this.mainLayout.setMargin(true);
//		this.mainLayout.setSizeFull();
		mainLayout.setWidth( "1170px" );
		mainLayout.setStyleName( "mainlayout" );

		mainLayout.setMargin( new MarginInfo( false, false, false, false ) );
		mainLayout.setSpacing( true );

		// // header image
		// this.headerImage = new Image(null,
		// new
		// ExternalResource("http://www.gesis.org/fileadmin/styles/img/gs_home_logo_de.svg"));
		// this.headerImage.setHeight(50, Unit.PIXELS);
		//
		// // footer image
		// this.footerImage = new Image(null,
		// new
		// ExternalResource("http://www.gesis.org/fileadmin/styles/img/leibniz_logo_de_white.svg"));
		// this.footerImage.setHeight(50, Unit.PIXELS);

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
		this.resultsContainer.addComponent(this.initResultsContainer(new ArrayList<>()));
		
		this.searchGlobalContainer.addComponents(searchBoxContainer, resultsContainer);
		this.searchGlobalContainer.setExpandRatio(resultsContainer, 1);

		this.globalContainer.addComponents(filtersContainer, searchGlobalContainer);
		this.globalContainer.setExpandRatio(searchGlobalContainer, 1);

		this.mainLayout.addComponents(globalContainer, footerImage);
		this.mainLayout.setExpandRatio(globalContainer, 1);
		addComponent(this.mainLayout);
		
		resetSearch();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Initialize the search box zone, with all necessary components and
	 * listeners
	 * 
	 * @return a horizontal layout containing all components of this zone
	 */
	private com.vaadin.ui.Component initSearchBoxContainer() {

		VerticalLayout container = new VerticalLayout();

		HorizontalLayout layout = new HorizontalLayout();
		layout.setSizeFull();

		this.searchBox = new TextField();
		this.searchBox.setSizeFull();
		this.searchBox.setPlaceholder("Enter your query here ...");

		this.searchButton = new Button(VaadinIcons.SEARCH);
		this.searchButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
		this.searchButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		this.searchButton.addClickListener(e -> {

			if (!searchBox.getValue().trim().equalsIgnoreCase("")) {
				// initialize the new query
				List<DDIStore> searchResult = client.getElementListByContent(DDIElement.CVSCHEME,
						this.searchBox.getValue());

				ArrayList<CVScheme> hits = new ArrayList<CVScheme>();
				for (DDIStore store : searchResult) {
					CVScheme scheme = new CVScheme(store);

					hits.add(scheme);
				}
				updateResultsContainer(hits);
			} else
				resetSearch();

		});

		
		this.showAllStudiesButton = new Button("View all CVs");
		this.showAllStudiesButton.setStyleName(ValoTheme.BUTTON_LINK);
		this.showAllStudiesButton.addClickListener(e -> {

			// reinitialize saved query
		});
		
		clearButton
			.withCaption("Clear search")
			.withStyleName(ValoTheme.BUTTON_LINK)
			.addClickListener( e -> {
				resetSearch();
			 });

		searchHelpButton
			.withCaption("Help")
			.withStyleName(ValoTheme.BUTTON_LINK)
			.addClickListener( e -> {
				HelpWindow help = new HelpWindow();
				UI.getCurrent().addWindow(help);
			 });
		
		searchOption
			.withStyleName( "searchoption" )
			.withMargin( false )
			.add(
				clearButton,
				new Label("|"),
				searchHelpButton
			);
		
		// filter option
		perPageComboBox.setItems("10","20","50","100","200");
		perPageComboBox.setEmptySelectionAllowed( false );
		perPageComboBox.setWidth("100px");
		perPageComboBox.setValue("10");
		
		sortComboBox.setItems("Relevance","A-Z");
		sortComboBox.setEmptySelectionAllowed( false );
		sortComboBox.setWidth("120px");
		sortComboBox.setValue("A-Z");
				
		filterOption
			.withFullWidth()
			.add(
				perPageResult
					.add( 
						new MLabel("Result per page"),
						perPageComboBox
					),
				infoResult,
				sortResult
				.add( 
						new MLabel("Result per page"),
						sortComboBox
					)
			);

		layout.addComponents(this.searchBox, this.searchButton /*, this.showAllStudiesButton*/);
		layout.setExpandRatio(this.searchBox, 1);

		container.addComponents(layout, searchOption, filterOption);

		return container;

	}
	
	private void resetSearch() {
		searchBox.setValue( "" );
		List<DDIStore> searchResult = client.getStudyList(DDIElement.CVSCHEME);

		ArrayList<CVScheme> hits = new ArrayList<CVScheme>();
		for (DDIStore store : searchResult) {
			CVScheme scheme = new CVScheme(store);

			hits.add(scheme);
		}
		updateResultsContainer(hits);
	}

	/**
	 * Initialize the results zone, with all necessary components and listeners.
	 * Especially: results grid + pagination
	 * 
	 * @param hits:
	 *            a list of results
	 * @return a vertical layout containing all components of this zone
	 */
	private com.vaadin.ui.Component initResultsContainer(ArrayList<CVScheme> hits) {

		VerticalLayout layout = new VerticalLayout();
		
		if( hits.isEmpty())
			infoResult.setValue( "Showing 0 of 0" );
		else
			infoResult.setValue( "Showing 1 - " + hits.size()+ " of " + hits.size());

		Label header = new Label("<b>" + hits.size() + " CVs retrieved</b>", ContentMode.HTML);

		// initialize the results grid
		Grid<CVScheme> results = new Grid<>(CVScheme.class);
		results.setItems(hits);
		results.addStyleName(ValoTheme.TABLE_BORDERLESS);
		
//		results.addComponentColumn( scheme -> {
//			return cvItems.get(0);
//		} );
		results.removeAllColumns();
		results.setHeaderVisible( false );
		results.addColumn( cvscheme -> {
		      return new CvSchemeComponent( cvscheme, configService );
		      }, new ComponentRenderer())
			.setId("cvScemeComp");
		results.setRowHeight( 135.0 );
		results.getColumn("cvScemeComp").setExpandRatio( 1 );
		
		results.setSelectionMode(SelectionMode.NONE);
		
		// SourceAsString column
		// results.addColumn(SearchHit::getSourceAsString).setCaption("Study").setId("study");

//		// add study title column to the grid
//		results.addColumn(new ValueProvider<CVScheme, String>() {
//
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public String apply(CVScheme source) {
//				return source.getTitleByLanguage("en");
//			}
//		}).setCaption("Title").setId("titleS");
//
//		// add study rank column to the grid
//		results.addColumn(new ValueProvider<CVScheme, String>() {
//
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public String apply(CVScheme source) {
//				return source.getDescriptionByLanguage("en");
//			}
//		}).setCaption("Description").setId("descriptionS");
//
//		// add study rank column to the grid
//		results.addColumn(new ValueProvider<CVScheme, String>() {
//
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public String apply(CVScheme source) {
//				return source.getOwnerAgency().get(0).getName();
//			}
//		}).setCaption("Owner Agency").setId("ownerS");
//
//		results.setColumns("ownerS", "titleS", "descriptionS");
		results.setSizeFull();
//		results.getColumn("titleS").setExpandRatio(1);

		// define the panel that should be opened when clicking on a row of the
		// grid
		// results.setDetailsGenerator(new DetailsGenerator<SearchHit>() {
		//
		// private static final long serialVersionUID = 1L;
		//
		// @Override
		// public com.vaadin.ui.Component apply(SearchHit arg0) {
		//
		// return new DetailsPanel(arg0);
		// }
		// });

		// add clicking listener to show or hide the detail window of a
		// particular row of the grid
		results.addItemClickListener(e -> {

			// close the previously opened item if it is not the same as the
			// currently selected item
			// if ((this.selectedItem != null) && (this.selectedItem !=
			// e.getItem()))
			// results.setDetailsVisible(this.selectedItem, false);
			//
			// boolean opened = results.isDetailsVisible(e.getItem());
			// if (opened)
			// this.selectedItem = null;
			// else
			// this.selectedItem = e.getItem();
			//
			// results.setDetailsVisible(e.getItem(), !opened);

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
	 *            a hit list to be showen (the results list)
	 */
	public void updateResultsContainer(ArrayList<CVScheme> hits) {

		// remove outdated results
		this.resultsContainer.removeAllComponents();

		this.resultsContainer.addComponent(this.initResultsContainer(hits));

	}

}
