package eu.cessda.cvmanager.ui.view;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.gesis.security.util.LoginSucceedEvent;
import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.GridDragSource;
import com.vaadin.ui.components.grid.GridDropTarget;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.ConfigurationService;

@UIScope
@SpringView(name = DetailView.VIEW_NAME)
public class DetailView extends MVerticalLayout implements View {

	private static final long serialVersionUID = 6904286186508174249L;
	public static final String VIEW_NAME = "Detail";

	private final EventBus.UIEventBus eventBus;
	private final ConfigurationService configService;

	private final String ITEM_TITLE = "Title";
	private final String ITEM_DEF = "Definition";
	private final String ITEM_CODE = "Code";
	private final String ITEM_LANG = "Language";
	private final String ITEM_VERSION = "Latest published version";
	private final String ITEM_PUBLICATION = "Date of publication";


	private String selectedLang = "en";
	private FormMode formMode;

	private MCssLayout buttonLayout = new MCssLayout();
	private MButton editButton = new MButton("Edit").withStyleName(ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL,
			"pull-right");
	private MButton saveButton = new MButton("Save").withStyleName(ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL,
			"pull-right");
	private MButton cancelButton = new MButton("Cancel").withStyleName(ValoTheme.BUTTON_SMALL, "pull-right");

	// graphical components
	private MVerticalLayout mainLayout = new MVerticalLayout();
	private MHorizontalLayout detailLayout = new MHorizontalLayout();

	private MCssLayout topSection = new MCssLayout().withFullWidth();
	private MCssLayout topViewSection = new MCssLayout().withFullWidth();
	private MCssLayout topEditSection = new MCssLayout().withFullWidth();
	private MCssLayout bottomSection = new MCssLayout().withFullWidth();
	private MCssLayout bottomViewSection = new MCssLayout().withFullWidth();
	private MCssLayout bottomEditSection = new MCssLayout().withFullWidth();

	private TextField prefLanguageEditor = new TextField();
	private TextField prefLabelEditor = new TextField();
	private TextField definitionEditor = new TextField();

	private View oldView;
	
	private Grid<CVConcept> detailGrid = new Grid<>(CVConcept.class);

	private CVScheme cvScheme;
	private Binder<CVConcept> binder;
	private List<CVConcept> concepts = new ArrayList<CVConcept>();
	private MCssLayout languageLayout = new MCssLayout();
	
	private ActionPanel actionPanel;
	
	private Set<CVConcept> draggedItems;
	private Grid<CVConcept> draggedGrid;

	// The opened search hit at the results grid (null at the begining)
	// private SearchHit selectedItem = null;

	// private List<CvItem> cvItems = new ArrayList<>();
	
	private RestClient client;

	private MHorizontalLayout titleLayout = new MHorizontalLayout();
	private Image logoImage;

	public DetailView(EventBus.UIEventBus eventBus, ConfigurationService configService) {
		this.eventBus = eventBus;
		this.configService = configService;
		
		client = new RestClient( configService.getDdiflatdbRestUrl() );
		
		this.eventBus.subscribe( this );
	}
	// private

	@PostConstruct
	public void init() {
		actionPanel = new ActionPanel( this );
		
		LoginView.NAVIGATETO_VIEWNAME = SearchView.VIEW_NAME;

		MButton backToResults = new MButton(FontAwesome.BACKWARD, this::back);
		backToResults.setCaption("Back");
		backToResults.withStyleName(ValoTheme.BUTTON_FRIENDLY, ValoTheme.BUTTON_SMALL, "pull-right", "marginleft20");

		editButton.addClickListener(e -> setFormMode(FormMode.edit));
		cancelButton.addClickListener(e -> setFormMode(FormMode.view));

		buttonLayout.withFullWidth().withStyleName("alignTextRight").add(backToResults, editButton, cancelButton,
				saveButton);

		languageLayout.withFullWidth();

		topSection.add(topViewSection, topEditSection);

		bottomSection.add(bottomViewSection, bottomEditSection);
		
		detailLayout.add( 
				actionPanel,
				new MVerticalLayout( 
						buttonLayout, 
						topSection, 
						languageLayout, 
						bottomSection )
				.withMargin( false )
				.withSpacing( false ) 
				)
			.withFullWidth()
			.withSpacing( false )
			.withMargin( false );
		
		if( SecurityContextHolder.getContext().getAuthentication() == null ) {
			actionPanel.setVisible( false );
			detailLayout
				.withExpand( actionPanel, 0f )
				.withExpand( detailLayout.getComponent( 1 ), 1f );
		} else {
			actionPanel.setVisible( true );
			detailLayout
				.withExpand( actionPanel, 0.15f )
				.withExpand( detailLayout.getComponent( 1 ), 0.85f );
		}

		mainLayout
			.withWidth( "1170px" )
			.withStyleName( "mainlayout" )
			.withSpacing( true )
			.withMargin( new MarginInfo( false, false, false, false ) )
			.add(
				detailLayout
		);

		this.withHeightUndefined().add(mainLayout);
	}
	
	@EventBusListenerMethod( scope = EventScope.UI )
	public void onAuthenticate( LoginSucceedEvent event )
	{
		actionPanel.setVisible( true );
		detailLayout
			.withExpand( actionPanel, 0.15f )
			.withExpand( detailLayout.getComponent( 1 ), 0.85f );
		
		initTopViewSection();
		initTopEditSection();
		initBottomViewSection();
		initBottomEditSection();
	}

	private void setDetails(String itemId) {
		concepts.clear();
		List<DDIStore> ddiSchemes = client.getElementList(itemId, DDIElement.CVSCHEME);
		if (ddiSchemes != null && !ddiSchemes.isEmpty())
			cvScheme = new CVScheme(ddiSchemes.get(0));

		List<DDIStore> ddiConcepts = client.getElementList(itemId, DDIElement.CVCONCEPT);

		ddiConcepts.forEach(ddiConcept -> concepts.add(new CVConcept(ddiConcept)));

		Set<String> languages = new LinkedHashSet<>();

		concepts.forEach(concept -> languages.addAll(concept.getLanguagesByPrefLabel()));

		languageLayout.removeAllComponents();

		languages.forEach(item -> {
			MButton langBUtton = new MButton(item.toUpperCase());
			langBUtton.withStyleName("langbutton").addClickListener(e -> {
				setSelectedLang(e.getButton().getCaption().toLowerCase());
				if (formMode.equals(FormMode.view)) {
					initTopViewSection();
					initBottomViewSection();
				} else {
					initTopEditSection();
					initBottomEditSection();
				}
			});
			languageLayout.add(langBUtton);
		});

		setFormMode(FormMode.view);
		// cvItem = addDummyData1();

		initTopViewSection();
		initTopEditSection();
		initBottomViewSection();
		initBottomEditSection();
	}

	private void initTopViewSection() {
		topViewSection.removeAllComponents();

		MLabel topTitle = new MLabel();
		topTitle.withStyleName("topTitle").withContentMode(ContentMode.HTML)
				.withValue(cvScheme.getOwnerAgency().get(0).getName() + " Controlled Vocabulary for "
						+ cvScheme.getTitleByLanguage("en") + "</strong>");

		Resource res = new ThemeResource("img/ddi-logo-r.png");
		Image logo = new Image(null, res);
		logo.setWidth("100px");

		MCssLayout topHead = new MCssLayout();
		topHead.withFullWidth().add(logo, topTitle);

		MCssLayout titleSmall = new MCssLayout();
		titleSmall.withFullWidth().add(new MLabel(ITEM_TITLE + ":").withWidth("120px").withStyleName("leftPart"),
				new MLabel(cvScheme.getTitleByLanguage("en")).withStyleName("rightPart"));

		MCssLayout description = new MCssLayout();
		description.withFullWidth().add(new MLabel(ITEM_DEF + ":").withWidth("120px").withStyleName("leftPart"),
				new MLabel(cvScheme.getDescriptionByLanguage("en")).withStyleName("rightPart"));

		MCssLayout code = new MCssLayout();
		code.withFullWidth().add(new MLabel(ITEM_CODE + ":").withWidth("120px").withStyleName("leftPart"),
				new MLabel(cvScheme.getCode()).withStyleName("rightPart"));

		MCssLayout titleSmallOl = new MCssLayout();
		titleSmallOl.withFullWidth().add(
				new MLabel(selectedLang + " " + ITEM_TITLE + ":").withWidth("120px").withStyleName("leftPart"),
				new MLabel(cvScheme.getTitleByLanguage(selectedLang)).withStyleName("rightPart"));

		MCssLayout descriptionOl = new MCssLayout();
		descriptionOl.withFullWidth().add(
				new MLabel(selectedLang + " " + ITEM_DEF + ":").withWidth("120px").withStyleName("leftPart"),
				new MLabel(cvScheme.getDescriptionByLanguage(selectedLang)).withStyleName("rightPart"));

		if (selectedLang.equals("en")) {
			titleSmallOl.setVisible(false);
			descriptionOl.setVisible(false);
		}

		MCssLayout langSec = new MCssLayout();
		langSec.withFullWidth().add(
				new MCssLayout().withWidth("33%")
						.add(new MLabel(ITEM_LANG + ":").withWidth("120px").withStyleName("leftPart"),
								new MLabel(selectedLang).withStyleName("rightPart")),
				new MCssLayout().withWidth("33%").add(
						new MLabel(ITEM_VERSION + ":").withWidth("180px").withStyleName("leftPart"),
						new MLabel(cvScheme.getVersion().getPublicationVersion() + (selectedLang.equals("en") ? ""
								: "-" + selectedLang)).withStyleName("rightPart")),
				new MCssLayout().withWidth("33%").add(
						new MLabel(ITEM_PUBLICATION + ":").withWidth("140px").withStyleName("leftPart"),
						new MLabel(cvScheme.getVersion().getPublicationDate().toString()).withStyleName("rightPart")));

		topViewSection.add(topHead, titleSmall, description, code, titleSmallOl, descriptionOl, langSec);
	}

	private void initTopEditSection() {
		topEditSection.removeAllComponents();

		MLabel topTitle = new MLabel();
		topTitle.withStyleName("topTitle").withContentMode(ContentMode.HTML)
				.withValue(cvScheme.getOwnerAgency().get(0).getName() + " Controlled Vocabulary for "
						+ cvScheme.getTitleByLanguage("en") + "</strong>");

		Resource res = new ThemeResource("img/ddi-logo-r.png");
		Image logo = new Image(null, res);
		logo.setWidth("100px");

		MCssLayout topHead = new MCssLayout();
		topHead.withFullWidth().add(logo, topTitle);

		MTextField titleField = new MTextField();
		titleField.withStyleName("editField").withValue(cvScheme.getTitleByLanguage("en"));

		MCssLayout titleSmall = new MCssLayout();
		titleSmall.withFullWidth().add(new MLabel(ITEM_TITLE + ":").withWidth("120px").withStyleName("leftPart"),
				selectedLang.equals("en") ? titleField
						: new MLabel(cvScheme.getTitleByLanguage("en")).withStyleName("rightPart"));

		TextArea descField = new TextArea();
		descField.setStyleName("editField");
		descField.setValue(cvScheme.getDescriptionByLanguage("en"));

		MCssLayout description = new MCssLayout();
		description.withFullWidth().add(new MLabel(ITEM_DEF + ":").withWidth("120px").withStyleName("leftPart"),
				selectedLang.equals("en") ? descField
						: new MLabel(cvScheme.getDescriptionByLanguage("en")).withStyleName("rightPart"));

		MTextField codeField = new MTextField();
		codeField.withStyleName("editField").withValue("CODE_TEST");

		MCssLayout code = new MCssLayout();
		code.withFullWidth().add(new MLabel(ITEM_CODE + ":").withWidth("120px").withStyleName("leftPart"),
				selectedLang.equals("en") ? codeField : new MLabel(cvScheme.getCode()).withStyleName("rightPart"));

		MCssLayout langSec = new MCssLayout();
		langSec.withFullWidth()
				.add(new MCssLayout().withWidth("33%").add(
						new MLabel(ITEM_LANG + ":").withWidth("120px").withStyleName("leftPart"),
						new MLabel("en").withStyleName("rightPart")),
						new MCssLayout().withWidth("33%").add(
								new MLabel(ITEM_VERSION + ":").withWidth("180px").withStyleName("leftPart"),
								new MLabel(cvScheme.getVersion().getPublicationVersion()).withStyleName("rightPart")),
						new MCssLayout().withWidth("33%").add(
								new MLabel(ITEM_PUBLICATION + ":").withWidth("140px").withStyleName("leftPart"),
								new MLabel(cvScheme.getVersion().getPublicationDate().toString())
										.withStyleName("rightPart")));

		MTextField titleFieldOl = new MTextField();
		titleFieldOl.withStyleName("editField").withValue(cvScheme.getTitleByLanguage(selectedLang));

		MCssLayout titleSmallOl = new MCssLayout();
		titleSmallOl.withFullWidth().add(
				new MLabel(selectedLang + " " + ITEM_TITLE + ":").withWidth("120px").withStyleName("leftPart"),
				titleFieldOl);

		TextArea descFieldOl = new TextArea();
		descFieldOl.setStyleName("editField");
		descFieldOl.setValue(cvScheme.getDescriptionByLanguage(selectedLang));

		MCssLayout descriptionOl = new MCssLayout();
		descriptionOl.withFullWidth().add(
				new MLabel(selectedLang + " " + ITEM_DEF + ":").withWidth("120px").withStyleName("leftPart"),
				descFieldOl);

		MCssLayout langSecOl = new MCssLayout();
		langSecOl.withFullWidth().add(
				new MCssLayout().withWidth("33%")
						.add(new MLabel(ITEM_LANG + ":").withWidth("120px").withStyleName("leftPart"),
								new MLabel(selectedLang).withStyleName("rightPart")),
				new MCssLayout().withWidth("33%").add(
						new MLabel(ITEM_VERSION + ":").withWidth("180px").withStyleName("leftPart"),
						new MLabel(cvScheme.getVersion().getPublicationVersion() + (selectedLang.equals("en") ? ""
								: "-" + selectedLang)).withStyleName("rightPart")),
				new MCssLayout().withWidth("33%").add(
						new MLabel(ITEM_PUBLICATION + ":").withWidth("140px").withStyleName("leftPart"),
						new MLabel(cvScheme.getVersion().getPublicationDate().toString()).withStyleName("rightPart")));

		if (selectedLang.equals("en")) {
			titleSmallOl.setVisible(false);
			descriptionOl.setVisible(false);
			langSecOl.setVisible(false);
		}

		topEditSection.add(topHead, titleSmall, description, code, langSec, titleSmallOl, descriptionOl, langSecOl);
	}

	private void initBottomViewSection() {
		bottomViewSection.removeAllComponents();

		TabSheet detailTab = new TabSheet();
		VerticalLayout detailLayout = new VerticalLayout();
		MCssLayout identifyLayout = new MCssLayout().withFullWidth();
		MCssLayout ddiLayout = new MCssLayout().withFullWidth();
		MCssLayout licenseLayout = new MCssLayout().withFullWidth();
		MCssLayout exportLayout = new MCssLayout().withFullWidth();

		detailTab.addTab(detailLayout, "Details");
		detailTab.addTab(identifyLayout, "Identity, versions and general");
		detailTab.addTab(ddiLayout, "DDI usage");
		detailTab.addTab(licenseLayout, "License and copyright");
		detailTab.addTab(exportLayout, "Export/download");
				
		binder = detailGrid.getEditor().getBinder();
		//binder.addValueChangeListener(event -> Notification.show("Binder Event"));
		detailGrid.setItems(concepts);
		// detailGrid.setItems( cvItem.getCvElements() );
		// detailGrid.addStyleName(ValoTheme.TABLE_BORDERLESS);

		// SourceAsString column
		// results.addColumn(SearchHit::getSourceAsString).setCaption("Study").setId("study");

		updateDetailGrid();

		detailLayout.addComponents(detailGrid);
		detailLayout.setMargin(true);
		detailLayout.setSpacing(true);
		detailLayout.setSizeFull();
		detailLayout.setExpandRatio(detailGrid, 1);

		bottomViewSection.add(detailTab);
	}
	
	
	public void updateDetailGrid() {
		detailGrid.removeAllColumns();
		// detailGrid.addColumn(CVConcept::getId).setCaption("URI").setExpandRatio(1);

		detailGrid.addColumn(concept -> concept.getPrefLabelByLanguage("en")).setCaption("en")
				.setEditorComponent(prefLabelEditor, (concept, value) -> concept.setPrefLabelByLanguage("en", value))
				.setExpandRatio(1);

		Binding<CVConcept, String> prefLabelBinding = binder.bind(prefLanguageEditor,
				concept -> concept.getPrefLabelByLanguage(selectedLang),
				(concept, label) -> concept.setPrefLabelByLanguage(selectedLang, label));

		detailGrid.addColumn(concept -> concept.getPrefLabelByLanguage(selectedLang)).setCaption(selectedLang)
				.setEditorBinding(prefLabelBinding).setExpandRatio(1);// Component(prefLanguageEditor,
		// (concept, value) ->
		// updateConcept(concept,
		// value, "en"));

		detailGrid.addColumn(concept -> concept.getDescriptionByLanguage(selectedLang)).setCaption("Definition")
				.setEditorComponent(definitionEditor, (concept, value) -> concept.setDescriptionByLanguage( selectedLang, value))
				.setExpandRatio(2);

		detailGrid.setSizeFull();
		detailGrid.getEditor().setEnabled(true);
		
		addDragSourceExtension(detailGrid, concepts);
		addDropTargetExtension(detailGrid, DropMode.BETWEEN, concepts);
	}

	private void initBottomEditSection() {
		bottomEditSection.removeAllComponents();

		TabSheet detailTab = new TabSheet();
		MCssLayout detailLayout = new MCssLayout().withFullWidth();
		MCssLayout identifyLayout = new MCssLayout().withFullWidth();
		MCssLayout ddiLayout = new MCssLayout().withFullWidth();
		MCssLayout licenseLayout = new MCssLayout().withFullWidth();
		MCssLayout exportLayout = new MCssLayout().withFullWidth();

		detailTab.addTab(detailLayout, "Details");
		detailTab.addTab(identifyLayout, "Identity, versions and general");
		detailTab.addTab(ddiLayout, "DDI usage");
		detailTab.addTab(licenseLayout, "License and copyright");
		detailTab.addTab(exportLayout, "Export/download");

		bottomEditSection.add(detailTab);
	}
	
    private GridDragSource<CVConcept> addDragSourceExtension(Grid<CVConcept> source,
            List<CVConcept> items) {
        // Create and attach extension
        GridDragSource<CVConcept> dragSource = new GridDragSource<>(source);
        dragSource.setEffectAllowed(EffectAllowed.MOVE);
 
		// Add drag start listener
        dragSource.addGridDragStartListener(event -> {
            // Keep reference to the dragged items,
            // note that there can be only one drag at a time
            draggedItems = event.getDraggedItems();
            draggedGrid = source;
        });
 
        // Add drag end listener
        dragSource.addGridDragEndListener(event -> {
            // verify that drop effect was the desired -> drop happened
            if (event.getDropEffect() == DropEffect.MOVE) {
                // inside grid reordering is handled on drop event listener,
                // which is always fired before drag end
                if (draggedGrid == null) {
                    return;
                }
                // remove items from this grid
                items.removeAll(draggedItems);
                source.getDataProvider().refreshAll();
 
                // Remove reference to dragged items
                draggedItems = null;
                draggedGrid = null;
            }
        });
 
        return dragSource;
    }
 
    private GridDropTarget<CVConcept> addDropTargetExtension(Grid<CVConcept> target,
            DropMode dropMode, List<CVConcept> items) {
        // Create and attach extension
        GridDropTarget<CVConcept> dropTarget = new GridDropTarget<>(target,
                dropMode);
        dropTarget.setDropEffect(DropEffect.MOVE);
 
        // Add listener
        dropTarget.addGridDropListener(event -> {
            // Calculate the target row's index
            int index = items.size();
            if (event.getDropTargetRow().isPresent()) {
                index = items.indexOf(event.getDropTargetRow().get())
                        + (event.getDropLocation() == DropLocation.BELOW ? 1
                                : 0);
            }
            if (draggedGrid == target) {
                // The index needs to be offset by the number of dragged items
                // above the drop location
                final int finalIndex = index;
                int offset = (int) draggedItems.stream()
                        .filter(d -> items.indexOf(d) < finalIndex).count();
 
                // Reordering rows in this grid, first remove and then add
                items.removeAll(draggedItems);
                items.addAll(index - offset, draggedItems);
                draggedItems = null;
                draggedGrid = null;
            } else {
                // Add dragged items to this Grid
            	if( draggedItems != null)
            		items.addAll(index, draggedItems);
            }
            target.getDataProvider().refreshAll();
        });
 
        return dropTarget;
    }

	@Override
	public void enter(ViewChangeEvent event) {
		setOldView(event.getOldView());

		if (event.getParameters() != null) {
			try {
				String itemId = event.getParameters();
				setDetails(itemId);
			} catch (Exception e) {
				// UI.getCurrent().getNavigator().navigateTo(
				// ErrorView.VIEW_NAME );
				e.printStackTrace();
			}

		}
	}

	public FormMode getFormMode() {
		return formMode;
	}

	public void setFormMode(FormMode fMode) {
		formMode = fMode;

		switch (formMode) {
		case view:
			topViewSection.setVisible(true);
			bottomViewSection.setVisible(true);
			topEditSection.setVisible(false);
			bottomEditSection.setVisible(false);
			saveButton.setVisible(false);
			cancelButton.setVisible(false);
			editButton.setVisible(true);
			break;
		case edit:
			topEditSection.setVisible(true);
			bottomEditSection.setVisible(true);
			topViewSection.setVisible(false);
			bottomViewSection.setVisible(false);
			saveButton.setVisible(true);
			cancelButton.setVisible(true);
			editButton.setVisible(false);

			break;
		default:
			break;
		}
	}

	public String getSelectedLang() {
		return selectedLang;
	}

	public void setSelectedLang(String selectedLang) {
		this.selectedLang = selectedLang;
	}

	private void setOldView(View oldView) {
		this.oldView = oldView;
	}

	private View getOldView() {
		return this.oldView;
	}

	private void back(ClickEvent clickEvent) {

		UI.getCurrent().getNavigator().navigateTo(SearchView.VIEW_NAME);
		if (getOldView().getClass().getSimpleName().equals("HomeView")) {
			UI.getCurrent().getNavigator().navigateTo(SearchView.VIEW_NAME);
		} else {
			UI.getCurrent().getNavigator().navigateTo(SearchView.VIEW_NAME);
		}

	}

	public Grid<CVConcept> getDetailGrid() {
		return detailGrid;
	}

	public List<CVConcept> getConcepts() {
		return concepts;
	}

	public EventBus.UIEventBus getEventBus() {
		return eventBus;
	}

	public RestClient getClient() {
		return client;
	}
	
	public CVScheme getCvScheme() {
		return cvScheme;
	}
}
