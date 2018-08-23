package eu.cessda.cvmanager.ui.layout;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import eu.cessda.cvmanager.export.utils.OnDemandFileDownloader;
import eu.cessda.cvmanager.export.utils.OnDemandFileDownloader.OnDemandStreamResource;
import eu.cessda.cvmanager.export.utils.SaxParserUtils;
import eu.cessda.cvmanager.model.CvItem;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.component.CvSchemeComponent;
import eu.cessda.cvmanager.ui.view.AgencyView;
import eu.cessda.cvmanager.ui.view.GesisPagination;
import eu.cessda.cvmanager.ui.view.HelpWindow;
import eu.cessda.cvmanager.ui.view.AgencyView.ViewMode;

public class AgencyDetailLayout extends MCssLayout implements Translatable {
	
	private static final long serialVersionUID = -2461005203070668382L;
	private final I18N i18n;
	private final UIEventBus eventBus;
	private final AgencyView agencyView;
	private final ConfigurationService configurationService;
	private final VocabularyService vocabularyService;
	private final StardatDDIService stardatDDIService;
	private final ConfigurationService configService;
	
	private AgencyDTO agency;
	
	private Image logo;
	private final String logoPath = "img/noimage.png";
	
	private MCssLayout layout = new MCssLayout();
	private MCssLayout headerLayout = new MCssLayout();
	private MCssLayout logoLayout = new MCssLayout();
	private MCssLayout titleLayout = new MCssLayout();
	private MLabel headTitle = new MLabel();
	
	// main container
	private HorizontalLayout globalContainer = new HorizontalLayout();
	private VerticalLayout filtersContainer = new VerticalLayout();
	private VerticalLayout searchGlobalContainer = new VerticalLayout();


	private MButton backButton = new MButton("Back");

	private MHorizontalLayout filterOption = new MHorizontalLayout();
	private MHorizontalLayout perPageResult = new MHorizontalLayout();
	private ComboBox<String> perPageComboBox = new ComboBox<>();
	private MLabel infoResult = new MLabel();
	private MHorizontalLayout sortResult = new MHorizontalLayout();
	private ComboBox<String> sortComboBox = new ComboBox<>();
	// results area
	private VerticalLayout resultsContainer = new VerticalLayout();
		
	private List<CVScheme> hits = new ArrayList<>();
	
	public AgencyDetailLayout(I18N i18n,  UIEventBus eventBus,
			AgencyView agencyView, AgencyService agencyService,
			 ConfigurationService configurationService, 
			 VocabularyService vocabularyService, StardatDDIService stardatDDIService, 
			 ConfigurationService configService) {
		super();
		this.i18n = i18n;
		this.agencyView = agencyView;
		this.eventBus = eventBus;
		this.configurationService = configurationService;
		this.vocabularyService = vocabularyService;
		this.stardatDDIService = stardatDDIService;
		this.configService = configService;
		
		initLayout();
	
		this.add( layout);
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

		// results zone
		this.resultsContainer.setSpacing(false);
		this.resultsContainer.setMargin(false);
		this.resultsContainer.setSizeFull();

		this.searchGlobalContainer.addComponents(resultsContainer);
		this.searchGlobalContainer.setExpandRatio(resultsContainer, 1);

		this.globalContainer.addComponents(filtersContainer, searchGlobalContainer);
		this.globalContainer.setExpandRatio(searchGlobalContainer, 1);
		
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
				
		layout
			.withFullWidth()
			.add( headerLayout , globalContainer);
		
	}
	
	
	public void setAgency(AgencyDTO agency) {
		this.agency = agency;
		
		setHeaderContent();
		
		Map<String, CVScheme> mapHits = new HashMap<String, CVScheme>();
		
		List<VocabularyDTO> vocabularyDTOs = vocabularyService.findByAgency( agency.getId() );
		
		for(VocabularyDTO vocabularyDto : vocabularyDTOs ) {
			List<DDIStore> ddiStoreScheme = stardatDDIService.findByIdAndElementType(vocabularyDto.getUri(),
					DDIElement.CVSCHEME);
			if(ddiStoreScheme.size() == 1) {
				CVScheme scheme = new CVScheme(ddiStoreScheme.get(0));
				mapHits.put(scheme.ddiStore.getElementId(), scheme);
			}
		}
		
		hits = new ArrayList<CVScheme>(mapHits.values());

		updateResultsContainer();
		
	}

	public void updateResultsContainer() {

		// remove outdated results
		this.resultsContainer.removeAllComponents();

		this.resultsContainer.addComponent(this.initResultsContainer());
	}
	
	private com.vaadin.ui.Component initResultsContainer() {

		VerticalLayout layout = new VerticalLayout();

		if (hits.isEmpty())
			infoResult.setValue("Showing 0 of 0");
		else
			infoResult.setValue("Showing 1 - " + hits.size() + " of " + hits.size());

		Label header = new Label("<b>" + hits.size() + " Vocabularies</b>", ContentMode.HTML);

		// initialize the results grid
		Grid<CVScheme> results = new Grid<>(CVScheme.class);
		results.setItems(hits);

		results.addStyleNames(ValoTheme.TABLE_BORDERLESS, "undefined-height", "search-grid");

		results.removeAllColumns();
		results.setHeaderVisible(false);
		results.addColumn(cvscheme -> {
			return new CvSchemeComponent(cvscheme, configService, "");
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

	private void setHeaderContent() {
		Resource res = new ThemeResource(logoPath);
		if( agency.getLogopath() != null && !agency.getLogopath().isEmpty())
			res = new ThemeResource(agency.getLogopath());
		
		logo = new Image(null, res);
		logo.setWidth("200px");
		
		headTitle
			.withContentMode( ContentMode.HTML )
			.withValue( "<h2 style=\"margin-top:0\">" + agency.getName() + "</h2><strong>" + agency.getDescription() + "</strong>" );
		
		logoLayout.removeAllComponents();
		
		logoLayout
			.withStyleName( "pull-left" )
			.add( logo );
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		
	}
}
