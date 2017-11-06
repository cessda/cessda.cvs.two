package eu.cessda.cvmanager.ui.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.gesis.security.util.LoginSucceedEvent;
import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
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
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.GridDragSource;
import com.vaadin.ui.components.grid.GridDropTarget;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.CvManagerService;
import eu.cessda.cvmanager.ui.view.window.DialogCodeWindow;

@UIScope
@SpringView(name = DetailView.VIEW_NAME)
public class DetailView extends CvManagerView {

	private static final long serialVersionUID = 6904286186508174249L;
	public static final String VIEW_NAME = "Detail";

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
		
	private Set<CVConcept> draggedItems;
	private Grid<CVConcept> draggedGrid;

	public DetailView( EventBus.UIEventBus eventBus, ConfigurationService configService, CvManagerService cvManagerService) {
		super(eventBus, configService, cvManagerService, DetailView.VIEW_NAME);
		eventBus.subscribe( this, DetailView.VIEW_NAME );
	}

	@PostConstruct
	public void init() {
		MButton backToResults = new MButton(FontAwesome.BACKWARD, this::back);
		backToResults.setCaption("Back");
		backToResults.withStyleName(ValoTheme.BUTTON_FRIENDLY, ValoTheme.BUTTON_SMALL, "pull-right", "marginleft20");

		editButton.addClickListener(e -> setFormMode(FormMode.edit));
		cancelButton.addClickListener(e -> setFormMode(FormMode.view));
		saveButton.addClickListener( this::doSaveConcept );

		buttonLayout
			.withFullWidth()
			.withStyleName("alignTextRight")
			.add(
				backToResults, 
				editButton, 
				cancelButton,
				saveButton
			);

		languageLayout.withFullWidth();

		topSection.add(topViewSection, topEditSection);

		bottomSection.add(bottomViewSection, bottomEditSection);
		
		rightContainer
			.add(
				buttonLayout, 
				topSection, 
				languageLayout, 
				bottomSection 
			);

	}
	
	@EventBusListenerMethod( scope = EventScope.UI )
	public void onAuthenticate( LoginSucceedEvent event )
	{
		editButton.setVisible( true );
		actionPanel.setVisible( true );
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		setOldView(event.getOldView());

		if (event.getParameters() != null) {
			try {
				String itemId = event.getParameters();
				LoginView.NAVIGATETO_VIEWNAME = DetailView.VIEW_NAME + "/" + itemId;
				setDetails(itemId);
			} catch (Exception e) {
				// UI.getCurrent().getNavigator().navigateTo(
				// ErrorView.VIEW_NAME );
				e.printStackTrace();
			}

		}
	}

	private void setDetails(String itemId) {
		refreshCvScheme(itemId);
		refreshCvConcepts();
		
		setFormMode(FormMode.view);

		initTopViewSection();
		initTopEditSection();
		initBottomViewSection();
		initBottomEditSection();
	}



	private void refreshCvScheme(String itemId) {
		languageLayout.removeAllComponents();
		
		List<DDIStore> ddiSchemes = cvManagerService.findByIdAndElementType(itemId, DDIElement.CVSCHEME);
		if (ddiSchemes != null && !ddiSchemes.isEmpty())
			cvScheme = new CVScheme(ddiSchemes.get(0));

		Set<String> languages = cvScheme.getLanguagesByTitle();

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
	}
	
	private void refreshCvConcepts() {
		concepts.clear();
		List<DDIStore> ddiConcepts = cvManagerService.findByIdAndElementType(cvScheme.getContainerId(), DDIElement.CVCONCEPT);
		ddiConcepts.forEach(ddiConcept -> concepts.add(new CVConcept(ddiConcept)));
	}
	
	private void doSaveConcept() {
		cvScheme.save();
		DDIStore ddiStore = cvManagerService.saveElement(cvScheme.ddiStore, "Peter", "minor edit");
		Notification.show("All changes saved");
		setFormMode(FormMode.view);
		initTopViewSection();
		// emit Event for searchView
		eventBus.publish(EventScope.UI, SearchView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVSCHEME_UPDATED, ddiStore) );
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
		titleField.withStyleName("editField");//.withValue(cvScheme.getTitleByLanguage("en"));

		MCssLayout titleSmall = new MCssLayout();
		titleSmall.withFullWidth().add(new MLabel(ITEM_TITLE + ":").withWidth("120px").withStyleName("leftPart"),
				selectedLang.equals("en") ? titleField
						: new MLabel(cvScheme.getTitleByLanguage("en")).withStyleName("rightPart"));

		TextArea descField = new TextArea();
		descField.setStyleName("editField");
		//descField.setValue(cvScheme.getDescriptionByLanguage("en"));

		MCssLayout description = new MCssLayout();
		description.withFullWidth().add(new MLabel(ITEM_DEF + ":").withWidth("120px").withStyleName("leftPart"),
				selectedLang.equals("en") ? descField
						: new MLabel(cvScheme.getDescriptionByLanguage("en")).withStyleName("rightPart"));

		MTextField codeField = new MTextField();
		codeField.withStyleName("editField");
		//.withValue("CODE_TEST");

		MCssLayout code = new MCssLayout();
		code.withFullWidth().add(new MLabel(ITEM_CODE + ":").withWidth("120px").withStyleName("leftPart"),
				selectedLang.equals("en") ? codeField : new MLabel(cvScheme.getCode()).withStyleName("rightPart"));
		
		Binder<CVScheme> cvSchemeBinder = new Binder<>();
		cvSchemeBinder.setBean(cvScheme);
		
		// Binder original language
		cvSchemeBinder.bind( titleField, 
				cScheme -> cScheme.getTitleByLanguage("en"),
				(cScheme, value) -> cScheme.setTitleByLanguage( "en" , value) );
		
		cvSchemeBinder.bind( descField, 
				cScheme -> cScheme.getDescriptionByLanguage("en"),
				(cScheme, value) -> cScheme.setDescriptionByLanguage( "en" , value) );

		cvSchemeBinder.bind( codeField, 
				cScheme -> cScheme.getCode(),
				(cScheme, value) -> cScheme.setCode( value) );

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
		
		// Binder other language
		cvSchemeBinder.bind( titleFieldOl, 
				cScheme -> cScheme.getTitleByLanguage( getSelectedLang()),
				(cScheme, value) -> cScheme.setTitleByLanguage( getSelectedLang() , value) );
		
		cvSchemeBinder.bind( descFieldOl, 
				cScheme -> cScheme.getDescriptionByLanguage( getSelectedLang() ),
				(cScheme, value) -> cScheme.setDescriptionByLanguage( getSelectedLang() , value) );

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
		
		binder = detailGrid.getEditor().getBinder();
		//binder.addValueChangeListener(event -> Notification.show("Binder Event"));
		detailGrid.setItems(concepts);
		// detailGrid.setItems( cvItem.getCvElements() );
		// detailGrid.addStyleName(ValoTheme.TABLE_BORDERLESS);

		// SourceAsString column
		// results.addColumn(SearchHit::getSourceAsString).setCaption("Study").setId("study");


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
		//detailGrid.getEditor().setEnabled(true);
		detailGrid.addItemClickListener( event -> {
			if( event.getMouseEventDetails().isDoubleClick() ) {
				CVConcept selectedRow = event.getItem();
				Window window = new DialogCodeWindow(eventBus, cvManagerService, selectedRow, "en", selectedLang);
				getUI().addWindow(window);
			}
		});
		
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
			if( SecurityContextHolder.getContext().getAuthentication() == null ) {
				editButton.setVisible( false );
			} else {
				editButton.setVisible( true );
			}
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
	
	@EventBusListenerMethod( scope = EventScope.UI )
	public void resetGrid( DDIStore ddiStore ) {
		setDetails( ddiStore.getParentIdentifier());
	}

	public Grid<CVConcept> getDetailGrid() {
		return detailGrid;
	}

	public List<CVConcept> getConcepts() {
		return concepts;
	}

	public CVScheme getCvScheme() {
		return cvScheme;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@EventBusListenerMethod( scope = EventScope.UI )
	public void eventHandle( CvManagerEvent.Event event)
	{
		switch(event.getType()) {
			case CVCONCEPT_CREATED:
				refreshCvConcepts();
				updateDetailGrid();
				
				break;
			case CVCONCEPT_ADD_DIALOG:
				CVConcept newCVConcept = new CVConcept();
				newCVConcept.loadSkeleton(newCVConcept.getDefaultDialect());
				newCVConcept.createId();
				newCVConcept.setContainerId( cvScheme.getContainerId());

				Window window = new DialogCodeWindow(eventBus, cvManagerService, newCVConcept, "en", "en");
				getUI().addWindow(window);
				
				break;
			case CVCONCEPT_EDIT_MODE:
				if( detailGrid.getColumn("cvConceptRemove") == null ) {		
					detailGrid
							.addColumn( cvconcept -> "x",
								new ButtonRenderer(clickEvent -> {
									CVConcept targetConcept = (CVConcept) clickEvent.getItem();
//									ConfirmDialog.show( this.getUI(), "Confirm",
//											"Are you sure you want to delete the concept \"" + targetConcept.getDescriptionByLanguage( configService.getOriginalLanguage() ) + "\"?", "yes",
//											"cancel",
//											new ConfirmDialog.Listener() {
//												private static final long serialVersionUID = 4111198501798071357L;
//
//												@Override
//												public void onClose( ConfirmDialog dialog )
//												{
//													if ( dialog.isConfirmed() ) {
//														cvManagerService.deleteById(targetConcept.getId(), DDIElement.CVCONCEPT, "peter", "delete concept");
//														concepts.remove( targetConcept );
//													}
//												}
//											}
									
//											dialog -> {
//												if( dialog.isConfirmed() ) {
//													cvManagerService.deleteById(targetConcept.getId(), DDIElement.CVCONCEPT, "peter", "delete concept");
//													concepts.remove( targetConcept );
//												}
//											}

//									);
									cvManagerService.deleteById(targetConcept.ddiStore.getPrimaryKey(), "peter", "delete concept");
									concepts.remove( targetConcept );
									detailGrid.setItems( concepts );
						    })).setId("cvConceptRemove");
				} else {
					if( !detailGrid.getColumn("cvConceptRemove").isHidden()) {
						detailGrid.getColumn("cvConceptRemove").setHidden( true );
					} else {
						detailGrid.getColumn("cvConceptRemove").setHidden( false );
					}
				}
				break;
			default:
				break;
		}
	}
}
