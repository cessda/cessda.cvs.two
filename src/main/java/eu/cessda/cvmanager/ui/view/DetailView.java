package eu.cessda.cvmanager.ui.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.gesis.security.SecurityService;
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
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.data.Binder;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.TreeGridDragSource;
import com.vaadin.ui.components.grid.TreeGridDropTarget;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.export.utils.SaxParserUtils;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.CvManagerService;
import eu.cessda.cvmanager.ui.layout.ExportLayout;
import eu.cessda.cvmanager.ui.view.window.DialogAddCodeWindow;
import eu.cessda.cvmanager.ui.view.window.DialogEditCodeWindow;
import eu.cessda.cvmanager.ui.view.window.DialogMultipleOption;
import eu.cessda.cvmanager.ui.view.window.DialogTranslateCodeWindow;
import eu.cessda.cvmanager.utils.CvCodeTreeUtils;

@UIScope
@SpringView(name = DetailView.VIEW_NAME)
public class DetailView extends CvManagerView {

	private static final long serialVersionUID = 6904286186508174249L;
	public static final String VIEW_NAME = "Detail";
	private Locale locale = UI.getCurrent().getLocale();

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
	
	private VerticalLayout detailLayout = new VerticalLayout();
	private MCssLayout identifyLayout = new MCssLayout().withFullWidth();
	private MCssLayout ddiLayout = new MCssLayout().withFullWidth();
	private MCssLayout licenseLayout = new MCssLayout().withFullWidth();
	private MCssLayout exportLayout = new MCssLayout().withFullWidth();

	private TextField codeEditor = new TextField();
	private TextField prefLanguageEditor = new TextField();
	private TextField prefLabelEditor = new TextField();
	
	private TabSheet detailTab = new TabSheet();
	
	private MLabel lTitle = new MLabel();
	private MLabel lDefinition = new MLabel();
	private MLabel lCode = new MLabel();
	private MLabel lLang = new MLabel();
	private MLabel lVersion = new MLabel();
	private MLabel lDate = new MLabel();
	private MLabel lTitleOl = new MLabel();
	private MLabel lDefinitionOl = new MLabel();
	private MLabel lVersionOl = new MLabel();
	private MLabel lDateOl = new MLabel();
	private boolean enableTreeDragAndDrop;

	private View oldView;
	
	private TreeGrid<CVConcept> detailTreeGrid = new TreeGrid<>(CVConcept.class);
	private TreeGridDragSource<CVConcept> dragSource;
	private TreeGridDropTarget<CVConcept> dropTarget;

	private TreeData<CVConcept> cvCodeTreeData;
	private MCssLayout languageLayout = new MCssLayout();
	private Set<CVConcept> draggedItems;
	private TreeDataProvider<CVConcept> dataProvider;
	
	private ExportLayout exportLayoutContent;

	public DetailView( I18N i18n, EventBus.UIEventBus eventBus, ConfigurationService configService, 
			CvManagerService cvManagerService, SecurityService securityService) {
		super(i18n, eventBus, configService, cvManagerService, securityService, DetailView.VIEW_NAME);
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
		locale = UI.getCurrent().getLocale();
		setOldView(event.getOldView());
		actionPanel.conceptSelectedChange( null );

		if (event.getParameters() != null) {
			try {
				String itemId = event.getParameters();
				LoginView.NAVIGATETO_VIEWNAME = DetailView.VIEW_NAME + "/" + itemId;
				setDetails(itemId);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private void setDetails(String itemId) {
		setFormMode(FormMode.view);
		
		refreshCvScheme(itemId);
//		refreshCvConcepts();

		initTopViewSection();
		initTopEditSection();
		initBottomViewSection();
		//initBottomEditSection();
		updateMessageStrings(UI.getCurrent().getLocale());
		
		// TODO: Workaround so that the translation label viible ~ not efficient need correct solution
		initTopViewSection();
	}



	private void refreshCvScheme(String itemId) {
		languageLayout.removeAllComponents();
		
		List<DDIStore> ddiSchemes = cvManagerService.findByIdAndElementType(itemId, DDIElement.CVSCHEME);
		if (ddiSchemes != null && !ddiSchemes.isEmpty()) {
			cvScheme = new CVScheme(ddiSchemes.get(0));
			cvItem.setCvScheme(cvScheme);
		}
		
		Set<String> languages = cvScheme.getLanguagesByTitle();
		String sourceLanguage = configService.getDefaultSourceLanguage();//cvScheme.getSourceLanguage();

		languages.forEach(item -> {
			MButton langButton = new MButton(item.toUpperCase());
			langButton.withStyleName("langbutton").addClickListener(e -> {
				applyButtonStyle(e.getButton());
				setSelectedLang(e.getButton().getCaption().toLowerCase());
				if (formMode.equals(FormMode.view)) {
					initTopViewSection();
					initBottomViewSection();
				} else {
					initTopEditSection();
					//initBottomEditSection();
					initBottomViewSection();
				}
			});
			languageLayout.add(langButton);
			if( item.equals(sourceLanguage)) {
				langButton.addStyleName("font-bold");
				langButton.setDescription( "source language" );
				langButton.click();
			}
			
		});
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
		titleSmall.withFullWidth().add( lTitle.withWidth("120px").withStyleName("leftPart"),
				new MLabel(cvScheme.getTitleByLanguage("en")).withStyleName("rightPart"));

		MCssLayout description = new MCssLayout();
		description.withFullWidth().add(lDefinition.withWidth("120px").withStyleName("leftPart"),
				new MLabel(cvScheme.getDescriptionByLanguage("en")).withStyleName("rightPart"));

		MCssLayout code = new MCssLayout();
		code.withFullWidth().add(lCode.withWidth("120px").withStyleName("leftPart"),
				new MLabel(cvScheme.getCode()).withStyleName("rightPart"));

		MCssLayout titleSmallOl = new MCssLayout();
		titleSmallOl.withFullWidth().add(
				lTitleOl.withWidth("120px").withStyleName("leftPart"),
				new MLabel(cvScheme.getTitleByLanguage(selectedLang)).withStyleName("rightPart"));

		MCssLayout descriptionOl = new MCssLayout();
		descriptionOl.withFullWidth().add(
				lDefinitionOl.withWidth("120px").withStyleName("leftPart"),
				new MLabel(cvScheme.getDescriptionByLanguage(selectedLang)).withStyleName("rightPart"));

		if (selectedLang.equals(configService.getDefaultSourceLanguage())) {
			titleSmallOl.setVisible(false);
			descriptionOl.setVisible(false);
		}

		MCssLayout langSec = new MCssLayout();
		langSec.withFullWidth().add(
				new MCssLayout().withWidth("33%")
						.add(lLang.withWidth("120px").withStyleName("leftPart"),
								new MLabel(selectedLang).withStyleName("rightPart")),
				new MCssLayout().withWidth("33%").add(
						lVersion.withWidth("180px").withStyleName("leftPart"),
						new MLabel(cvScheme.getVersion().getPublicationVersion() + (selectedLang.equals("en") ? ""
								: "-" + selectedLang)).withStyleName("rightPart")),
				new MCssLayout().withWidth("33%").add(
						lDate.withWidth("140px").withStyleName("leftPart"),
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
		titleSmall.withFullWidth().add( lTitle.withWidth("120px").withStyleName("leftPart"),
				selectedLang.equals("en") ? titleField
						: new MLabel(cvScheme.getTitleByLanguage("en")).withStyleName("rightPart"));

		TextArea descField = new TextArea();
		descField.setStyleName("editField");
		//descField.setValue(cvScheme.getDescriptionByLanguage("en"));

		MCssLayout description = new MCssLayout();
		description.withFullWidth().add(lDefinition.withWidth("120px").withStyleName("leftPart"),
				selectedLang.equals("en") ? descField
						: new MLabel(cvScheme.getDescriptionByLanguage("en")).withStyleName("rightPart"));

		MTextField codeField = new MTextField();
		codeField.withStyleName("editField");
		//.withValue("CODE_TEST");

		MCssLayout code = new MCssLayout();
		code.withFullWidth().add(lCode.withWidth("120px").withStyleName("leftPart"),
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
						lLang.withWidth("120px").withStyleName("leftPart"),
						new MLabel("en").withStyleName("rightPart")),
						new MCssLayout().withWidth("33%").add(
								lVersion.withWidth("180px").withStyleName("leftPart"),
								new MLabel(cvScheme.getVersion().getPublicationVersion()).withStyleName("rightPart")),
						new MCssLayout().withWidth("33%").add(
								lDate.withWidth("140px").withStyleName("leftPart"),
								new MLabel(cvScheme.getVersion().getPublicationDate().toString())
										.withStyleName("rightPart")));

		MTextField titleFieldOl = new MTextField();
		titleFieldOl.withStyleName("editField").withValue(cvScheme.getTitleByLanguage(selectedLang));

		MCssLayout titleSmallOl = new MCssLayout();
		titleSmallOl.withFullWidth().add(
				lTitleOl.withWidth("120px").withStyleName("leftPart"),
				titleFieldOl);

		TextArea descFieldOl = new TextArea();
		descFieldOl.setStyleName("editField");
		descFieldOl.setValue(cvScheme.getDescriptionByLanguage(selectedLang));

		MCssLayout descriptionOl = new MCssLayout();
		descriptionOl.withFullWidth().add(
				lDefinitionOl.withWidth("120px").withStyleName("leftPart"),
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
						.add(lLang.withWidth("120px").withStyleName("leftPart"),
								new MLabel(selectedLang).withStyleName("rightPart")),
				new MCssLayout().withWidth("33%").add(
						lVersion.withWidth("180px").withStyleName("leftPart"),
						new MLabel(cvScheme.getVersion().getPublicationVersion() + (selectedLang.equals("en") ? ""
								: "-" + selectedLang)).withStyleName("rightPart")),
				new MCssLayout().withWidth("33%").add(
						lDate.withWidth("140px").withStyleName("leftPart"),
						new MLabel(cvScheme.getVersion().getPublicationDate().toString()).withStyleName("rightPart")));

		if (selectedLang.equals( configService.getDefaultSourceLanguage())) {
			titleSmallOl.setVisible(false);
			descriptionOl.setVisible(false);
			langSecOl.setVisible(false);
		}

		topEditSection.add(topHead, titleSmall, description, code, langSec, titleSmallOl, descriptionOl, langSecOl);
	}

	private void initBottomViewSection() {
		bottomViewSection.removeAllComponents();
		detailLayout.removeAllComponents();

		detailTab = new TabSheet();
		detailTab.setStyleName("detail-tab");
		detailTab.addTab(detailLayout, i18n.get("view.detail.cvconcept.tab.detail", locale));
		detailTab.addTab(identifyLayout, i18n.get("view.detail.cvconcept.tab.identity", locale));
		detailTab.addTab(ddiLayout, i18n.get("view.detail.cvconcept.tab.ddi", locale));
		detailTab.addTab(licenseLayout, i18n.get("view.detail.cvconcept.tab.license", locale));
		detailTab.addTab(exportLayout, i18n.get("view.detail.cvconcept.tab.export", locale));
		
		detailTreeGrid = new TreeGrid<>(CVConcept.class);
		detailTreeGrid.addStyleNames("undefined-height");
		detailTreeGrid.removeAllColumns();
		
		updateDetailGrid();	
		
		detailTreeGrid.setSelectionMode( SelectionMode.SINGLE );
		
		detailTreeGrid.addColumn(concept -> concept.getNotation())
			.setCaption("Code")
			.setEditorComponent(codeEditor, (concept, value) -> concept.setNotation(value))
			.setExpandRatio(1)
			.setId("code");
	
		detailTreeGrid.addColumn(concept -> concept.getPrefLabelByLanguage("en"))
			.setCaption(i18n.get("view.detail.cvconcept.column.sl.title", locale))
			.setEditorComponent(prefLabelEditor, (concept, value) -> concept.setPrefLabelByLanguage("en", value))
			.setExpandRatio(1)
			.setId("prefLabelSl");

		if( !selectedLang.equals( configService.getDefaultSourceLanguage() ))
			detailTreeGrid.addColumn(concept -> concept.getPrefLabelByLanguage(selectedLang))
				.setCaption(i18n.get("view.detail.cvconcept.column.tl.title", locale, selectedLang ))
				//.setEditorBinding(prefLabelBinding)
				.setEditorComponent(prefLanguageEditor, (concept, value) -> concept.setPrefLabelByLanguage( selectedLang, value))
				.setExpandRatio(1)
				.setId("prefLabelTl");// Component(prefLanguageEditor,
		
		detailTreeGrid.addColumn(concept -> {
					return new Label( concept.getDescriptionByLanguage( "en" ));
				}, new ComponentRenderer())
				.setCaption(i18n.get("view.detail.cvconcept.column.sl.definition", locale))
				.setExpandRatio(3)
				.setId("definitionSl");
		
		if( !selectedLang.equals( configService.getDefaultSourceLanguage() ))
			detailTreeGrid.addColumn(concept -> {
				return new Label( concept.getDescriptionByLanguage(selectedLang));
			}, new ComponentRenderer())
			.setCaption(i18n.get("view.detail.cvconcept.column.tl.definition", locale, selectedLang ))
			.setExpandRatio(3)
			.setId("definitionTl");
		
		detailTreeGrid.setSizeFull();
		detailTreeGrid.addItemClickListener( event -> {
			if( SecurityContextHolder.getContext().getAuthentication() != null && event.getMouseEventDetails().isDoubleClick() ) {
				CVConcept selectedRow = event.getItem();
				Window window = new DialogEditCodeWindow(eventBus, cvManagerService, cvScheme, selectedRow, selectedLang);
				getUI().addWindow(window);
			}
		});
		
		detailTreeGrid.addSelectionListener( event -> {
			cvConcept = event.getAllSelectedItems().size() > 0 ? event.getAllSelectedItems().iterator().next() : null;
			actionPanel.conceptSelectedChange( cvConcept );
		});
		
		if(enableTreeDragAndDrop)
			enableTreeGridDragAndDropSort();
		
		detailTreeGrid.getColumns().stream().forEach( column -> column.setSortable( false ));
				
		detailLayout.addComponents(detailTreeGrid);
		detailLayout.setMargin(false);
		detailLayout.setSpacing(false);
		detailLayout.setSizeFull();
		detailLayout.setExpandRatio(detailTreeGrid, 1);
		
		if(exportLayoutContent == null )
			exportLayoutContent = new ExportLayout(i18n, locale, eventBus, cvItem);
		exportLayout.add(exportLayoutContent);

		bottomViewSection.add(detailTab);
	}
	
	private void enableTreeGridDragAndDropSort() {		
		dragSource = new TreeGridDragSource<>(detailTreeGrid);
		
		// set allowed effects
		dragSource.setEffectAllowed(EffectAllowed.MOVE);
	     
		dragSource.addGridDragStartListener(event ->
			// Keep reference to the dragged items
			draggedItems = event.getDraggedItems()
		);
	  
		dropTarget = new TreeGridDropTarget<>(detailTreeGrid, DropMode.BETWEEN);
		dropTarget.setDropEffect(DropEffect.MOVE);
	     
		dropTarget.addTreeGridDropListener(event -> {
			// Accepting dragged items from another Grid in the same UI
	        event.getDragSourceExtension().ifPresent(source -> {
	            if (source instanceof TreeGridDragSource) {
            		if (event.getDropTargetRow().isPresent()) {
	                	CVConcept targetRow = event.getDropTargetRow().get();
	                	CVConcept draggedRow = draggedItems.iterator().next();
	                	// check if code drag and drop to itself
	                	if( targetRow.equals(draggedRow) ) {
	                		return;
	                	}
	                	List<Button> optionButtons = new ArrayList<>();
	                	optionButtons.add( new Button( "As next sibling" )); // option 0
	                	optionButtons.add( new Button( "As child" ));        // option 1
	                	
	                	getUI().addWindow( new DialogMultipleOption("Code move options", "Move the code <strong>\"" + (draggedRow.getNotation() == null ? draggedRow.getPrefLabelByLanguage("en"): draggedRow.getNotation()) + "\"</strong> as a next sibling or as a child of <strong>\"" + 
        						(targetRow.getNotation() == null ? targetRow.getPrefLabelByLanguage("en"): targetRow.getNotation())+ "\"</strong>?", optionButtons, 
	                			windowoption ->  {
	                				Integer selectedOptionNumber = windowoption.getSelectedOptionNumber();
	                				if(selectedOptionNumber == null )
	                					return;
	                				
                					CVConcept draggedNodeParent = cvCodeTreeData.getParent(draggedRow);
                					CVConcept targetNodeParent = cvCodeTreeData.getParent(targetRow);
                					boolean parentSame = true;
                					
	                				if( selectedOptionNumber == 0 ) { // move as next sibling
	                					// Possibility
	                					// -- within same parent
	                					// move between siblings in root node (top concepts level)  - checked Ok
	                					// move between siblings in x-parent node (parent as child concept level) - checked Ok
	                					// -- different parent
	                					// move from child concept to top concept  - checked Ok
	                					// move from top concept to child concept  - checked Ok
	                					
	                					
	                					// in order to be able to move as next sibling 
	                					// the nodes need to be from the same parent
	                					if( !Objects.equals( draggedNodeParent, targetNodeParent)){
	                						//add code as target parent node first
	                						cvCodeTreeData.setParent(draggedRow, targetNodeParent);
	                						parentSame=false;
	                					}

	                					// update tree in vaadin UI
	                					cvCodeTreeData.moveAfterSibling(draggedRow, targetRow);
	                					dataProvider.refreshAll();
	                					
	                					Integer targetNodeLevel = event.getDropTargetRowDepth().get();
	                					
	                					if( targetNodeLevel == 0 ) { // root concept, reorder
	                						cvManagerService.storeTopConcept(cvScheme, cvCodeTreeData.getRootItems());
	                					} else { // reorder narrower
	                						cvManagerService.storeNarrowerConcept( targetNodeParent, cvCodeTreeData.getChildren( targetNodeParent ));
	                					}
	                					  
	                					// dragged node not from topConcepts, need to reorder the narrower list
	                					// from previous parent
	                					if( !parentSame ) {
		                					if( draggedNodeParent != null ) {
		                						cvManagerService.storeNarrowerConcept( draggedNodeParent, cvCodeTreeData.getChildren( draggedNodeParent ));
		                					} else {
		                						cvManagerService.storeTopConcept(cvScheme, cvCodeTreeData.getRootItems());
		                					}
	                					}
	                				} 
	                				else if (selectedOptionNumber == 1) { //move as child
	                					// Possibility
	                					// as topconcept to child from root/leaf concept
	                					// as child child to  child from root/leaf concept (only concept narrower affected)
            							cvCodeTreeData.setParent(draggedRow, targetRow);
            							dataProvider.refreshAll();
            							
            							// update topconcept, if dragged top concept is null
            							if( draggedNodeParent == null ) { // dragged node was topconcept
            								cvManagerService.storeTopConcept(cvScheme, cvCodeTreeData.getRootItems());
            							} else {
            								cvManagerService.storeNarrowerConcept( draggedNodeParent, cvCodeTreeData.getChildren( draggedNodeParent ));
            							}
            							// update new parent child order
            							cvManagerService.storeNarrowerConcept( targetRow, cvCodeTreeData.getChildren( targetRow ));
	                				}
	                				draggedItems = null;
	                			})
	                	);
	                }
	            }
	        });
	     });
	}

	@SuppressWarnings("unchecked")
	public void updateDetailGrid() {		
		dataProvider = (TreeDataProvider<CVConcept>) detailTreeGrid.getDataProvider();
		cvCodeTreeData = dataProvider.getTreeData();
		cvCodeTreeData.clear();
		// assign the tree structure
		List<DDIStore> ddiConcepts = cvManagerService.findByIdAndElementType(cvScheme.getContainerId(), DDIElement.CVCONCEPT);
		CvCodeTreeUtils.buildCvConceptTree(ddiConcepts, cvScheme, cvCodeTreeData);
		
		cvItem.setCvCodeTreeData(cvCodeTreeData);
		
		dataProvider.refreshAll();
	}
	
	public FormMode getFormMode() {
		return formMode;
	}

	public void setFormMode(FormMode fMode) {
		formMode = fMode;

		switch (formMode) {
		case view:
			topViewSection.setVisible(true);
			topEditSection.setVisible(false);
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
			topViewSection.setVisible(false);
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
		return detailTreeGrid;
	}

	public CVScheme getCvScheme() {
		return cvScheme;
	}
	
	@EventBusListenerMethod( scope = EventScope.UI )
	public void eventHandle( CvManagerEvent.Event event)
	{
		switch(event.getType()) {
			case CVCONCEPT_CREATED:
				updateDetailGrid();
				
				break;
			case CVCONCEPT_ADD_DIALOG:
				CVConcept newCVConcept = new CVConcept();
				newCVConcept.loadSkeleton(newCVConcept.getDefaultDialect());
				newCVConcept.createId();
				newCVConcept.setContainerId( cvScheme.getContainerId());

				DialogAddCodeWindow dialogAddCodeWindow1 = new DialogAddCodeWindow(eventBus, cvManagerService, cvScheme, newCVConcept,null, "en", i18n, UI.getCurrent().getLocale());
				getUI().addWindow(dialogAddCodeWindow1);
				
				break;
			case CVCONCEPT_TRANSLATION_DIALOG:
				if( cvCodeTreeData == null || cvCodeTreeData.getRootItems().isEmpty()) {
					Notification.show("Please add code first");
				} else if( cvScheme.getLanguagesByTitle().size() == 1) {
					Notification.show("Please add CV translation first");
				}
				else {
					Window windowTranslate = new DialogTranslateCodeWindow(eventBus, cvManagerService, cvScheme, cvConcept , selectedLang);
					getUI().addWindow( windowTranslate );
				}
				break;
			case CVCONCEPT_ADDCHILD_DIALOG:
				CVConcept childConcept = new CVConcept();
				childConcept.loadSkeleton(childConcept.getDefaultDialect());
				childConcept.createId();
				childConcept.setContainerId( cvScheme.getContainerId());

				DialogAddCodeWindow dialogAddCodeWindow2 = new DialogAddCodeWindow(eventBus, cvManagerService, cvScheme, childConcept, cvConcept, "en", i18n, UI.getCurrent().getLocale());
				getUI().addWindow( dialogAddCodeWindow2 );
				break;
			case CVCONCEPT_DELETED:
				
				ConfirmDialog.show( this.getUI(), "Confirm",
				"Are you sure you want to delete the concept \"" + cvConcept.getPrefLabelByLanguage( configService.getDefaultSourceLanguage() ) + "\"?", "yes",
				"cancel",
		
					dialog -> {
						if( dialog.isConfirmed() ) {
							cvManagerService.deleteConceptTree(cvCodeTreeData, cvConcept);
							cvCodeTreeData.removeItem( cvConcept );
							detailTreeGrid.getDataProvider().refreshAll();
						}
					}

				);
				break;
			case CVCONCEPT_SORT:
				enableTreeDragAndDrop = (boolean)event.getPayload();
				initBottomViewSection();
				break;
			default:
				break;
		}
	}
	
	private void applyButtonStyle(Button pressedButton) {

		Iterator<Component> iterate = languageLayout.iterator();
		while (iterate.hasNext()) {
			Component c = (Component) iterate.next();
			if( c instanceof  Button) {
				((Button) c).removeStyleName( "button-pressed" );
			}
		}
		pressedButton.addStyleName( "button-pressed" );
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
		lTitle.setValue( i18n.get("view.detail.cvscheme.label.sl.title", locale));
		lDefinition.setValue( i18n.get("view.detail.cvscheme.label.sl.definition", locale));
		lCode.setValue( i18n.get("view.detail.cvscheme.label.sl.code", locale));
		lLang.setValue( i18n.get("view.detail.cvscheme.label.language", locale));
		lVersion.setValue( i18n.get("view.detail.cvscheme.label.sl.version", locale));
		lDate.setValue( i18n.get("view.detail.cvscheme.label.sl.publicationdate", locale));
		lTitleOl.setValue( i18n.get("view.detail.cvscheme.label.tl.title", locale, selectedLang));
		lDefinitionOl.setValue( i18n.get("view.detail.cvscheme.label.tl.definition", locale, selectedLang));
		lVersionOl.setValue( i18n.get("view.detail.cvscheme.label.tl.version", locale));
		lDateOl.setValue( i18n.get("view.detail.cvscheme.label.tl.publicationdate", locale));
		
		detailTab.getTab(0).setCaption( i18n.get("view.detail.cvconcept.tab.detail", locale));
		detailTab.getTab(1).setCaption( i18n.get("view.detail.cvconcept.tab.identity", locale));
		detailTab.getTab(2).setCaption( i18n.get("view.detail.cvconcept.tab.ddi", locale));
		detailTab.getTab(3).setCaption( i18n.get("view.detail.cvconcept.tab.license", locale));
		detailTab.getTab(4).setCaption( i18n.get("view.detail.cvconcept.tab.export", locale));
		
		detailTreeGrid.getColumn("code").setCaption( "Code" );
		detailTreeGrid.getColumn("prefLabelSl").setCaption( i18n.get("view.detail.cvconcept.column.sl.title", locale) );
		if( detailTreeGrid.getColumn("prefLabelTl") != null )
			detailTreeGrid.getColumn("prefLabelTl").setCaption( i18n.get("view.detail.cvconcept.column.tl.title", locale) );
		detailTreeGrid.getColumn("definitionSl").setCaption( i18n.get("view.detail.cvconcept.column.sl.definition", locale) );
		if( detailTreeGrid.getColumn("definitionTl") != null )
			detailTreeGrid.getColumn("definitionTl").setCaption( i18n.get("view.detail.cvconcept.column.tl.definition", locale) );
		
		actionPanel.updateMessageStrings(locale);
	}
}
