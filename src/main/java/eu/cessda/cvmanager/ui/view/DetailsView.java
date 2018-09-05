package eu.cessda.cvmanager.ui.view;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.xml.utils.URI;
import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVEditor;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.LoginSucceedEvent;
import org.gesis.wts.security.SecurityService;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.security.UserDetails;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.gesis.wts.ui.view.LoginView;
import org.springframework.security.core.context.SecurityContextHolder;
import org.thymeleaf.TemplateEngine;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
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

import eu.cessda.cvmanager.domain.Version;
import eu.cessda.cvmanager.domain.VocabularyChange;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.export.utils.SaxParserUtils;
import eu.cessda.cvmanager.repository.search.VocabularySearchRepository;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.LicenseService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyChangeService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.mapper.VocabularyMapper;
import eu.cessda.cvmanager.ui.CVManagerUI;
import eu.cessda.cvmanager.ui.layout.DdiUsageLayout;
import eu.cessda.cvmanager.ui.layout.EditorCodeActionLayout;
import eu.cessda.cvmanager.ui.layout.EditorCodeActionLayoutNew;
import eu.cessda.cvmanager.ui.layout.EditorCvActionLayout;
import eu.cessda.cvmanager.ui.layout.EditorCvActionLayoutNew;
import eu.cessda.cvmanager.ui.layout.ExportLayout;
import eu.cessda.cvmanager.ui.layout.IdentityLayout;
import eu.cessda.cvmanager.ui.layout.LicenseLayout;
import eu.cessda.cvmanager.ui.layout.VersionLayout;
import eu.cessda.cvmanager.ui.view.window.DialogAddCodeWindow;
import eu.cessda.cvmanager.ui.view.window.DialogAddCodeWindow2;
import eu.cessda.cvmanager.ui.view.window.DialogEditCodeWindow;
import eu.cessda.cvmanager.ui.view.window.DialogMultipleOption;
import eu.cessda.cvmanager.ui.view.window.DialogTranslateCodeWindow;
import eu.cessda.cvmanager.utils.CvCodeTreeUtils;

@UIScope
@SpringView(name = DetailsView.VIEW_NAME)
public class DetailsView extends CvView {

	private static final long serialVersionUID = -1095312295254197091L;
	public static final String VIEW_NAME = "details";
	private Locale locale = UI.getCurrent().getLocale();
	
	private final TemplateEngine templateEngine;
	private final AgencyService agencyService;
	private final VocabularyService vocabularyService;
	private final VersionService versionService;
	private final CodeService codeService;
	private final ConceptService conceptService;
	private final VocabularyChangeService vocabularyChangeService;
	private final LicenseService licenseService;

	private Language selectedLang = Language.ENGLISH;

	private MCssLayout topSection = new MCssLayout().withFullWidth();
	private MCssLayout topViewSection = new MCssLayout().withFullWidth();
	private MCssLayout bottomSection = new MCssLayout().withFullWidth();
	private MCssLayout bottomViewSection = new MCssLayout().withFullWidth();
	
	// the tabs
	private TabSheet detailTab = new TabSheet();
	private MCssLayout detailLayout = new MCssLayout().withFullWidth();
	private MCssLayout versionContentLayout = new MCssLayout().withFullWidth();
	private MCssLayout identifyLayout = new MCssLayout().withFullWidth();
	private MCssLayout ddiLayout = new MCssLayout().withFullWidth();
	private MCssLayout licenseLayout = new MCssLayout().withFullWidth();
	private MCssLayout exportLayout = new MCssLayout().withFullWidth();

	private TextField codeEditor = new TextField();
	private TextField prefLanguageEditor = new TextField();
	private TextField prefLabelEditor = new TextField();
	
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
	
	private MLabel lTitle2 = new MLabel();
	private MLabel lDefinition2 = new MLabel();
	private MLabel lCode2 = new MLabel();
	private MLabel lLang2 = new MLabel();
	private MLabel lVersion2 = new MLabel();
	private MLabel lDate2 = new MLabel();
	private MLabel lTitleOl2 = new MLabel();
	private MLabel lDefinitionOl2 = new MLabel();
	private MLabel lVersionOl2 = new MLabel();
	private MLabel lDateOl2 = new MLabel();
	
	private MLabel lLang3 = new MLabel();
	private MLabel lVersion3 = new MLabel();
	private MLabel lDate3 = new MLabel();
	
	private boolean enableTreeDragAndDrop;
	
	private VersionDTO currentVersion;
	private VersionDTO currentSLVersion;
	private ConceptDTO currentConcept;
	
	private MLabel versionLabel = new MLabel();

//	private View oldView;
	
//	private TreeGrid<CVConcept> detailTreeGrid = new TreeGrid<>(CVConcept.class);
//	private TreeGridDragSource<CVConcept> dragSource;
//	private TreeGridDropTarget<CVConcept> dropTarget;
	
	private TreeGrid<CodeDTO> detailTreeGridNew = new TreeGrid<>(CodeDTO.class);
	private TreeGridDragSource<CodeDTO> dragSourceNew;
	private TreeGridDropTarget<CodeDTO> dropTargetNew;

//	private TreeData<CVConcept> cvCodeTreeData;
	private TreeData<CodeDTO> cvCodeTreeDataNew;
	private MCssLayout languageLayout = new MCssLayout();
//	private List<CVConcept> draggedItems;
	private List<CodeDTO> draggedItemsNew;
//	private TreeDataProvider<CVConcept> dataProvider;
	private TreeDataProvider<CodeDTO> dataProviderNew;
	
	private VersionLayout versionLayout;
	private ExportLayout exportLayoutContent;
	private IdentityLayout identityLayout;
	private DdiUsageLayout ddiUsageLayout;
	private LicenseLayout licenseLayoutContent;
	
	private Map<String, List<VersionDTO>> orderedLanguageVersionMap;
	private Language sourceLanguage;
	private String activeTab;
	private String versionNumber;
	
	private EditorCvActionLayoutNew editorCvActionLayout;
	private EditorCodeActionLayoutNew editorCodeActionLayout;

	public DetailsView(I18N i18n, EventBus.UIEventBus eventBus, ConfigurationService configService,
			StardatDDIService stardatDDIService, SecurityService securityService, AgencyService agencyService,
			VocabularyService vocabularyService, VersionService versionService, CodeService codeService, ConceptService conceptService,
			VocabularySearchRepository vocabularySearchRepository, TemplateEngine templateEngine,
			VocabularyChangeService vocabularyChangeService, LicenseService licenseService) {
		super(i18n, eventBus, configService, stardatDDIService, securityService, agencyService, vocabularyService, codeService, vocabularySearchRepository, DetailsView.VIEW_NAME);
		this.templateEngine = templateEngine;
		this.agencyService = agencyService;
		this.vocabularyService = vocabularyService;
		this.versionService = versionService;
		this.codeService = codeService;
		this.conceptService = conceptService;
		this.vocabularyChangeService = vocabularyChangeService;
		this.licenseService = licenseService;
		eventBus.subscribe( this, DetailsView.VIEW_NAME );
	}

	@PostConstruct
	public void init() {
		
		editorCvActionLayout = new EditorCvActionLayoutNew("block.action.cv", "block.action.cv.show", i18n, 
				stardatDDIService, agencyService, vocabularyService, versionService, conceptService, codeService, 
				vocabularySearchRepository, eventBus, vocabularyChangeService);
		
		editorCodeActionLayout = new EditorCodeActionLayoutNew("block.action.code", "block.action.code.show", i18n,
				stardatDDIService, agencyService, vocabularyService, versionService, codeService, conceptService, eventBus,
				vocabularyChangeService);
		

		languageLayout.withFullWidth();

		topSection.add(topViewSection/*, topEditSection*/);

		bottomSection.add(bottomViewSection/*, bottomEditSection*/);
		
		sidePanel
			.add( 
				editorCvActionLayout,
				editorCodeActionLayout
			);
		
		mainContainer
			.add(
				topSection, 
				languageLayout, 
				bottomSection
			);

	}
	
	@EventBusListenerMethod( scope = EventScope.UI )
	public void onAuthenticate( LoginSucceedEvent event )
	{
//		if( SecurityUtils.isCurrentUserAllowEditCv( agency , selectedLang))
//			editButton.setVisible( true );
//		else
//			editButton.setVisible( false );
//		actionPanel.setVisible( true );
	}
	
	/**
	 * The CV detail-page URL will be 
	 * [BASE_URL]/#!detail/[CV_Code_Name]
	 * In this method, the following query string parameters are checked:
	 * - uid - The ID of CVScheme ~ all published CV also stored in CVSCheme DDI flatDB
	 * - link - The Link of CV
	 * - lang - the selected language
	 * - concept - the selected concept
	 * - tab - the active tab
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		
		// set the default value for parameters
		selectedLang = null;
		activeTab = "detail";
		
		// set UI-locale on page load
		locale = UI.getCurrent().getLocale();

		// check for CV_Code_Name and query string parameters
		if (event.getParameters() != null) {
			try {
				// split between URI and query-string
				String[] itemPath = event.getParameters().split("\\?");
				// split the URI
				String[] itemPathPart = itemPath[0].split("/");
				
				// if contains query string
				if(itemPath.length > 1) {
					// get query string params as pairs
					List<NameValuePair> params = URLEncodedUtils.parse( itemPath[1],  Charset.forName("UTF-8"));
					Map<String, String> mappedParams = params.stream().collect(
					        Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
					
					// set lang
					String selectedLanguage = mappedParams.get("lang");
					if( selectedLanguage != null ) {
						cvItem.setCurrentLanguage( mappedParams.get("lang") );
						selectedLang = Language.getEnum( selectedLanguage );
					}
					if(  mappedParams.get("tab") != null )
						activeTab = mappedParams.get("tab");
					else
						activeTab = "detail";
					if(  mappedParams.get("vers") != null )
						versionNumber = mappedParams.get("vers");
					else
						versionNumber = null;
					
					
				}
				
				
				LoginView.NAVIGATETO_VIEWNAME = DetailsView.VIEW_NAME + "/" + itemPathPart[0];
				cvItem.setCurrentCvId(itemPathPart[0]);
				cvItem.setCurrentNotation(itemPathPart[0]);
				if( itemPathPart.length > 1 )
					cvItem.setCurrentConceptId(itemPathPart[1]);
				
				setDetails() ;

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			Notification.show("Unable to get the vocabulary detail");
		}
		
	}
	
	/**
	 * 
	 */
	private void setDetails() {
		
		refreshCvScheme();
//		refreshCvConcepts();
		
		// update breadcrumb
		breadcrumbs
			.addItem(getAgency().getName(), "agency")
			.addItem( currentVersion.getNotation() + " " + (currentVersion.getNumber() == null ? "":currentVersion.getNumber()) + " (" + currentVersion.getStatus() + ")", null)
			.build();

//		initTopViewSection();
//		initTopEditSection();
//		initBottomViewSection();
		//initBottomEditSection();
		updateMessageStrings(UI.getCurrent().getLocale());
		
		// top panes is the layout container for search, just hide here
		topPanel.setVisible( false );
		
		refreshCvActionButton();
		refreshCodeActionButton();
	}

	private void refreshCvActionButton() {
		if( editorCvActionLayout.hasActionRight() ) {
			mainContainer.removeStyleName("margin-left10");
			sidePanel.setVisible( true );
		}
		else {
			mainContainer.addStyleName("margin-left10");
			sidePanel.setVisible( false );
		}
	}

	private void refreshCodeActionButton() {
		if( editorCodeActionLayout.hasActionRight() ) {
			editorCodeActionLayout.setVisible( true );
		}
		else {
			editorCodeActionLayout.setVisible( false );
		}
	}

	private void refreshCvScheme() {
		languageLayout.removeAllComponents();
		
		vocabulary = vocabularyService.getByNotation(cvItem.getCurrentNotation());
		
		if( vocabulary == null ) {
			Notification.show("Unable to find vocabulary");
			return;
		}
		
		agency = agencyService.findByName( getVocabulary().getAgencyName());
				
		Set<String> languages = vocabulary.getLanguages();
		
		editorCvActionLayout.setVocabulary( vocabulary );
		editorCodeActionLayout.setVocabulary( vocabulary );
		
		editorCvActionLayout.setAgency( agency );
		editorCodeActionLayout.setAgency( agency );
		
		
		// determine source langauge
		sourceLanguage = Language.valueOfEnum( vocabulary.getSourceLanguage());
		if(selectedLang == null )
			selectedLang = sourceLanguage;
		
		// set source language to action layouts
		editorCvActionLayout.setSourceLanguage( sourceLanguage );
		editorCodeActionLayout.setSourceLanguage( sourceLanguage );
				
		currentSLVersion = VersionDTO.getLatestSourceVersion( vocabulary.getVersions());
		
		if ( currentSLVersion.getUri() != null && !currentSLVersion.getUri().isEmpty()) {
			List<DDIStore> ddiSchemes = stardatDDIService.findByIdAndElementType(currentSLVersion.getUri(), DDIElement.CVSCHEME);
			
			if (ddiSchemes != null && !ddiSchemes.isEmpty()) {
				cvItem.setCvScheme( new CVScheme(ddiSchemes.get(0)) );
				editorCvActionLayout.setCvScheme( cvItem.getCvScheme() );
			}
		}
		
		currentVersion = currentSLVersion;
		
		// TODO change this implementation 
		languages.forEach(item -> {
			Language eachLanguage = Language.getEnum(item);
			
			MButton langButton = new MButton(eachLanguage.toString().toUpperCase());
			langButton.withStyleName("langbutton");
			
			if( item.equalsIgnoreCase( sourceLanguage.toString() ))
				langButton.addStyleName( "button-source-language" );
			
			if( eachLanguage.equals( selectedLang ))
				langButton.addStyleName( "button-language-selected" );
			
			// determine the status
			vocabulary.getLatestVersionByLanguage( eachLanguage.toString(), versionNumber)
			.ifPresent( versionDTO -> {
				if( versionDTO.getStatus().equals( Status.DRAFT.toString())) {
					// TODO: check detail for editor or publication page
//					if( !breadcrumbItemMap.isEmpty() && breadcrumbItemMap.get("editor-search") != null )
						langButton.addStyleName( "status-draft" );
//					else
//						langButton.setVisible( false );
				}
				else if( versionDTO.getStatus().equals( Status.INITIAL_REVIEW.toString())) {
//					if( !breadcrumbItemMap.isEmpty() && breadcrumbItemMap.get("editor-search") != null )
						langButton.addStyleName( "status-review-initial" );
//					else
//						langButton.setVisible( false );
				}
				else if( versionDTO.getStatus().equals( Status.FINAL_REVIEW.toString())) {
					langButton.addStyleName( "status-review-final" );
				}
			});
			
			langButton.addClickListener(e -> {
				applyButtonStyle(e.getButton());
				
				cvItem.setCurrentLanguage(e.getButton().getCaption().toLowerCase());
				setSelectedLang( Language.getEnum( e.getButton().getCaption().toLowerCase()) );
//				actionPanel.languageSelectionChange( configService.getDefaultSourceLanguage(), cvItem.getCurrentLanguage());
				
//				setFormMode(FormMode.view);
							
				editorCvActionLayout.setSelectedLanguage(selectedLang);
				editorCodeActionLayout.setSelectedLanguage(selectedLang);
				
				currentVersion = null;
				editorCvActionLayout.setCurrentVersion( null );
				editorCodeActionLayout.setCurrentVersion( null );
				vocabulary.getLatestVersionByLanguage(selectedLang, versionNumber)
					.ifPresent( v -> {
						editorCvActionLayout.setCurrentVersion(v);
						editorCodeActionLayout.setCurrentVersion(v);
						currentVersion = v;
						
						versionLabel.setValue( (currentVersion.getNumber() == null ? "":currentVersion.getNumber()) + 
								(selectedLang.equals( sourceLanguage ) ? "": "-" + selectedLang.toString())  + 
								( currentVersion.getStatus().equals( Status.PUBLISHED.toString() ) ? "":" (" + currentVersion.getStatus() + ")"));
					});
				
				
				initTopViewSection();
//				initTopEditSection();
				initBottomViewSection();
				
//				if( SecurityUtils.isCurrentUserAllowEditCv( agency , selectedLang))
//					editButton.setVisible( true );
//				else
//					editButton.setVisible( false );
				
//				actionPanel.conceptSelectedChange( null );
				setCode( null );
				updateMessageStrings(locale);
				refreshCvActionButton();
				
				// clear cvConcept selection and button
//				detailTreeGrid.asSingleSelect().clear();
				detailTreeGridNew.asSingleSelect().clear();
				editorCodeActionLayout.clearCode();
				refreshCodeActionButton();
			});
			
			languageLayout.add(langButton);
			
			
			if( eachLanguage.equals(sourceLanguage)) {
				langButton.addStyleName("font-bold");
				langButton.setDescription( "source language" );
			}
			if( eachLanguage.equals( selectedLang ) ) {
//				editorCvActionLayout.setSelectedLanguage( Language.getEnum( item) );
				langButton.click();
			}
			
			
		});
	}

	private void initTopViewSection() {
		topViewSection.removeAllComponents();

		// TODO: better to use version
		MLabel topTitle = new MLabel();
		topTitle.withStyleName("topTitle").withContentMode(ContentMode.HTML)
				.withValue( agency.getName() + " Controlled Vocabulary for "
						+ currentVersion.getTitle() + "</strong>");
		
		MLabel logoLabel = new MLabel()
			.withContentMode( ContentMode.HTML )
			.withWidth("120px");
			
		if( agency.getLogo() != null && !agency.getLogo().isEmpty())
			logoLabel.setValue(  "<img style=\"width:120px\" alt=\"" + agency.getName() + " logo\" src='" + agency.getLogo() + "'>");
			
		MCssLayout topHead = new MCssLayout();
		topHead.withFullWidth().add( logoLabel, topTitle);

		MCssLayout titleSmall = new MCssLayout();
		titleSmall.withFullWidth().add( lTitle.withWidth("140px").withStyleName("leftPart"),
				new MLabel( currentSLVersion.getTitle() ).withStyleName("rightPart"));

		MCssLayout description = new MCssLayout();
		description.withFullWidth().add(lDefinition.withWidth("140px").withStyleName("leftPart"),
				new MLabel( currentSLVersion.getDefinition() ).withStyleName("rightPart"));

		MCssLayout code = new MCssLayout();
		code.withFullWidth().add(lCode.withWidth("140px").withStyleName("leftPart"),
				new MLabel( vocabulary.getNotation() ).withStyleName("rightPart"));

		MCssLayout titleSmallOl = new MCssLayout();
		titleSmallOl.withFullWidth().add(
				lTitleOl.withWidth("140px").withStyleName("leftPart"),
				new MLabel(currentVersion.getTitle() ).withStyleName("rightPart"));

		MCssLayout descriptionOl = new MCssLayout();
		descriptionOl.withFullWidth().add(
				lDefinitionOl.withWidth("140px").withStyleName("leftPart"),
				new MLabel( currentVersion.getDefinition() ).withStyleName("rightPart"));

		if (selectedLang.toString().equals(configService.getDefaultSourceLanguage())) {
			titleSmallOl.setVisible(false);
			descriptionOl.setVisible(false);
		}
		
		versionLabel.withStyleName("rightPart");

		MCssLayout langVersDateLayout = new MCssLayout();
		langVersDateLayout.withFullWidth().add(
				new MCssLayout().withStyleName("col-des-4")
						.add(lLang.withWidth("140px").withStyleName("leftPart"),
								new MLabel(selectedLang.toString()).withStyleName("rightPart")),
				new MCssLayout()
						.withStyleName("col-des-4")
						.add(
								lVersion.withWidth("140px").withStyleName("leftPart"),
								versionLabel
						)
				);
		
		if( vocabulary.getStatus().equals( Status.PUBLISHED.toString()) && currentVersion.getPublicationDate() != null)
			langVersDateLayout.add(
					new MCssLayout()
						.withStyleName("col-des-4")
						.add(
								lDate.withWidth("140px").withStyleName("leftPart"),
								new MLabel(currentVersion.getPublicationDate().toString()).withStyleName("rightPart"))
					);

		topViewSection.add(topHead, titleSmall, description, code, titleSmallOl, descriptionOl, langVersDateLayout);
	}

	private void initBottomViewSection() {
		bottomViewSection.removeAllComponents();
		detailLayout.removeAllComponents();
		exportLayout.removeAllComponents();
		identifyLayout.removeAllComponents();
		ddiLayout.removeAllComponents();
		licenseLayout.removeAllComponents();
		versionContentLayout.removeAllComponents();
		
		exportLayout.withHeight("450px");
		detailLayout.setHeight("800px");

		detailTab = new TabSheet();
		detailTab.setStyleName("detail-tab");
		detailTab.setHeightUndefined();
		detailTab.setWidth("100%");
	
		detailTab.addTab(detailLayout, i18n.get("view.detail.cvconcept.tab.detail", locale)).setId("detail");
		detailTab.addTab(versionContentLayout, i18n.get("view.detail.cvconcept.tab.version", locale)).setId("version");
		detailTab.addTab(identifyLayout, i18n.get("view.detail.cvconcept.tab.identity", locale)).setId("identify");
		detailTab.addTab(ddiLayout, i18n.get("view.detail.cvconcept.tab.ddi", locale)).setId("identify");
		detailTab.addTab(licenseLayout, i18n.get("view.detail.cvconcept.tab.license", locale)).setId("license");
		detailTab.addTab(exportLayout, i18n.get("view.detail.cvconcept.tab.export", locale)).setId("export");
		
		
		setActiveTab();
			
		detailTreeGridNew = new TreeGrid<>(CodeDTO.class);
		detailTreeGridNew.addStyleNames("undefined-height");
		detailTreeGridNew.removeAllColumns();
		detailTreeGridNew.setHeight("800px");
		detailTreeGridNew.setWidthUndefined();
		
		updateDetailGrid();	
		
		detailTreeGridNew.setSelectionMode( SelectionMode.SINGLE );
		
		detailTreeGridNew.addColumn(code -> code.getNotation())
			.setCaption("Code")
			.setExpandRatio(1)
			.setId("code");
	
		detailTreeGridNew.addColumn(code -> code.getTitleByLanguage( sourceLanguage ))
			.setCaption(i18n.get("view.detail.cvconcept.column.sl.title", locale))
			.setExpandRatio(1)
			.setId("prefLabelSl");

		if( !selectedLang.equals( Language.valueOfEnum( vocabulary.getSourceLanguage() ) ))
			detailTreeGridNew.addColumn(code -> code.getTitleByLanguage(selectedLang))
				.setCaption(i18n.get("view.detail.cvconcept.column.tl.title", locale, selectedLang.toString() ))
				.setExpandRatio(1)
				.setId("prefLabelTl");// Component(prefLanguageEditor,
		
		detailTreeGridNew.addColumn(code -> {
					return new MLabel( code.getDefinitionByLanguage( sourceLanguage )).withStyleName( "word-brake-normal" );
				}, new ComponentRenderer())
				.setCaption(i18n.get("view.detail.cvconcept.column.sl.definition", locale))
				.setExpandRatio(3)
				.setId("definitionSl");
		
		if( !selectedLang.equals( Language.valueOfEnum( vocabulary.getSourceLanguage() ) ))
			detailTreeGridNew.addColumn(code -> {
				return new MLabel( code.getDefinitionByLanguage(selectedLang)).withStyleName( "word-brake-normal" );
			}, new ComponentRenderer())
			.setCaption(i18n.get("view.detail.cvconcept.column.tl.definition", locale, selectedLang.toString() ))
			.setExpandRatio(3)
			.setId("definitionTl");
		
		detailTreeGridNew.setSizeFull();
		
		
		detailTreeGridNew.asSingleSelect().addValueChangeListener( event -> {		
			if (event.getValue() != null) {
				code = event.getValue();
				
//				editorCodeActionLayout.setCvConcept( cvItem.getCvConcept() );
				editorCodeActionLayout.setCurrentCode(code);
				editorCodeActionLayout.setCurrentConcept(null);
				
				// get concept
				ConceptDTO.getConceptFromCode(currentVersion.getConcepts(), code.getId()).ifPresent( conceptDTO -> {
					if( conceptDTO.isPersisted()) {
						currentConcept = conceptDTO;
						editorCodeActionLayout.setCurrentConcept(conceptDTO);
					} else {
						// query in database for updated one
						ConceptDTO conceptFromDb = conceptService.findOneByCodeNotationAndId( code.getNotation(), code.getId() );
						if( conceptFromDb != null ) {
							currentConcept = conceptFromDb;
							editorCodeActionLayout.setCurrentConcept(conceptFromDb);
						}
					}
				});
								
				refreshCodeActionButton();
				
            } else {
            	cvItem.setCvConcept(  null );
            	editorCodeActionLayout.clearCode();
				refreshCodeActionButton();
            }
		});
		
		if(enableTreeDragAndDrop)
			enableTreeGridDragAndDropSort();
		
		// select row programatically
		if(code != null ) {
			detailTreeGridNew.select( code);
			//detailTreeGridNew.scrollTo( 13 );

		}
		
		detailTreeGridNew.getColumns().stream().forEach( column -> column.setSortable( false ));
				
		detailLayout.addComponents(detailTreeGridNew);
		
		
		
		
		
		//detailLayout.setMargin(false);
		//detailLayout.setSpacing(false);
		detailLayout.setSizeFull();
		//detailLayout.setExpandRatio(detailTreeGrid, 1);
		
		versionLayout = new VersionLayout(i18n, locale, eventBus, agency, vocabulary, vocabularyChangeService, configService, currentSLVersion.getNumber());
		versionContentLayout.add( versionLayout );
		
		identityLayout = new IdentityLayout(i18n, locale, eventBus, agency, currentVersion, versionService, configService, false);
		identifyLayout.add( identityLayout );
		
		ddiUsageLayout = new DdiUsageLayout(i18n, locale, eventBus, agency, currentVersion, versionService, false);
		ddiLayout.add(ddiUsageLayout);
		
		licenseLayoutContent = new LicenseLayout(i18n, locale, eventBus, agency, currentVersion, versionService, licenseService.findAll(), false);
		licenseLayout.add( licenseLayoutContent );
		
		exportLayoutContent = new ExportLayout(i18n, locale, eventBus, cvItem, vocabulary, agency, versionService, configService, templateEngine, false);
		exportLayout.add(exportLayoutContent);
		exportLayout.setSizeFull();
		

		detailTab.addSelectedTabChangeListener( e -> {
			TabSheet tabsheet = e.getTabSheet();
			 // Find the tab (here we know it's a layout)
	        Layout tab = (Layout) tabsheet.getSelectedTab();
	        
			if( tabsheet.getTab(tab).getId().equals("export")) {
				vocabulary = vocabularyService.getByNotation(cvItem.getCurrentNotation());
				// get all version put it on the map
				orderedLanguageVersionMap = versionService.getOrderedLanguageVersionMap(vocabulary.getId());
				exportLayoutContent.updateGrid(currentVersion, orderedLanguageVersionMap);
			}
		});
		
		bottomViewSection.add(detailTab);
	}

	private void setActiveTab() {
		if( activeTab != null) {
			switch( activeTab) {
				case "download":
					detailTab.setSelectedTab(4);
					break;
				default:
					detailTab.setSelectedTab(0);
			}
		} else
			detailTab.setSelectedTab(0);
	}
	
	private void enableTreeGridDragAndDropSort() {		
//		dragSource = new TreeGridDragSource<>(detailTreeGrid);
		dragSourceNew = new TreeGridDragSource<>(detailTreeGridNew);
		
		// set allowed effects
//		dragSource.setEffectAllowed(EffectAllowed.MOVE);
		dragSourceNew.setEffectAllowed(EffectAllowed.MOVE);
	     
//		dragSource.addGridDragStartListener(event ->
//			// Keep reference to the dragged items
//			draggedItems = event.getDraggedItems()
//		);
		dragSourceNew.addGridDragStartListener(event ->
			// Keep reference to the dragged items
			draggedItemsNew = event.getDraggedItems()
		);
	  
//		dropTarget = new TreeGridDropTarget<>(detailTreeGrid, DropMode.BETWEEN);
//		dropTarget.setDropEffect(DropEffect.MOVE);
		
		dropTargetNew = new TreeGridDropTarget<>(detailTreeGridNew, DropMode.BETWEEN);
		dropTargetNew.setDropEffect(DropEffect.MOVE);
	     
//		dropTarget.addTreeGridDropListener(event -> {
		dropTargetNew.addTreeGridDropListener(event -> {
			// Accepting dragged items from another Grid in the same UI
	        event.getDragSourceExtension().ifPresent(source -> {
	            if (source instanceof TreeGridDragSource) {
            		if (event.getDropTargetRow().isPresent()) {
//	                	CVConcept targetRow = event.getDropTargetRow().get();
//	                	CVConcept draggedRow = draggedItems.iterator().next();
            			
            			CodeDTO targetRow = event.getDropTargetRow().get();
            			CodeDTO draggedRow = draggedItemsNew.iterator().next();
	                	
	                	// check if code drag and drop to itself
	                	if( targetRow.equals(draggedRow) ) {
	                		return;
	                	}
	                	List<Button> optionButtons = new ArrayList<>();
	                	optionButtons.add( new Button( "As next sibling" )); // option 0
	                	optionButtons.add( new Button( "As child" ));        // option 1
	                	
	                	getUI().addWindow( new DialogMultipleOption("Code move options", "Move the code <strong>\"" + 
	                	(draggedRow.getNotation() == null ? draggedRow.getTitleByLanguage( sourceLanguage ): draggedRow.getNotation()) + "\"</strong> as a next sibling or as a child of <strong>\"" + 
        						(targetRow.getNotation() == null ? targetRow.getTitleByLanguage( sourceLanguage ): targetRow.getNotation())+ "\"</strong>?", optionButtons, 
	                			windowoption ->  {
	                				Integer selectedOptionNumber = windowoption.getSelectedOptionNumber();
	                				if(selectedOptionNumber == null )
	                					return;
	                				
//                					CVConcept draggedNodeParent = cvCodeTreeData.getParent(draggedRow);
//                					CVConcept targetNodeParent = cvCodeTreeData.getParent(targetRow);
	                				CodeDTO draggedNodeParent =  cvCodeTreeDataNew.getParent( draggedRow );
	                				CodeDTO targetNodeParent = cvCodeTreeDataNew.getParent(targetRow);
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
	                						cvCodeTreeDataNew.setParent(draggedRow, targetNodeParent);
	                						parentSame=false;
	                					}

	                					// update tree in vaadin UI
	                					cvCodeTreeDataNew.moveAfterSibling(draggedRow, targetRow);
	                					dataProviderNew.refreshAll();
	                					
	                					// save on DB
            							codeService.storeCodeTree(cvCodeTreeDataNew);
	                					
//	                					Integer targetNodeLevel = event.getDropTargetRowDepth().get();
//	                					
//	                					if( targetNodeLevel == 0 ) { // root concept, reorder
//	                						
//	                						stardatDDIService.storeTopConcept(cvItem.getCvScheme(), cvCodeTreeDataNew.getRootItems());
//	                					} else { // reorder narrower
//	                						
//	                						stardatDDIService.storeNarrowerConcept( targetNodeParent, cvCodeTreeDataNew.getChildren( targetNodeParent ));
//	                					}
//	                					  
//	                					// dragged node not from topConcepts, need to reorder the narrower list
//	                					// from previous parent
//	                					if( !parentSame ) {
//		                					if( draggedNodeParent != null ) {
//		                						stardatDDIService.storeNarrowerConcept( draggedNodeParent, cvCodeTreeDataNew.getChildren( draggedNodeParent ));
//		                					} else {
//		                						stardatDDIService.storeTopConcept(cvItem.getCvScheme(), cvCodeTreeDataNew.getRootItems());
//		                					}
//	                					}
	                				} 
	                				else if (selectedOptionNumber == 1) { //move as child
	                					// Possibility
	                					// as topconcept to child from root/leaf concept
	                					// as child child to  child from root/leaf concept (only concept narrower affected)
	                					
	                					// force child notation follow its parent
	                					int lastDotIndex = draggedRow.getNotation().lastIndexOf( '.' );
	                					if( lastDotIndex >= 0) {
	                						draggedRow.setNotation( targetRow.getNotation() + "." + draggedRow.getNotation().substring( lastDotIndex + 1));
	                					} else {
	                						draggedRow.setNotation( targetRow.getNotation() + "." + draggedRow.getNotation());
	                					}
	                					
            							cvCodeTreeDataNew.setParent(draggedRow, targetRow);
            							dataProviderNew.refreshAll();
            							detailTreeGridNew.expand(draggedRow, targetRow);
            							
            							// save on DB
            							codeService.storeCodeTree(cvCodeTreeDataNew);
            							
//            							// update topconcept, if dragged top concept is null
//            							if( draggedNodeParent == null ) { // dragged node was topconcept
//            								stardatDDIService.storeTopConcept(cvItem.getCvScheme(), cvCodeTreeDataNew.getRootItems());
//            							} else {
//            								stardatDDIService.storeNarrowerConcept( draggedNodeParent, cvCodeTreeDataNew.getChildren( draggedNodeParent ));
//            							}
//            							// update new parent child order
//            							stardatDDIService.storeNarrowerConcept( targetRow, cvCodeTreeDataNew.getChildren( targetRow ));
	                				}
	                				draggedItemsNew = null;
	                			})
	                	);
	                }
	            }
	        });
	     });
	}

	@SuppressWarnings("unchecked")
	public void updateDetailGrid() {		
//		dataProvider = (TreeDataProvider<CVConcept>) detailTreeGrid.getDataProvider();
//		cvCodeTreeData = dataProvider.getTreeData();
//		cvCodeTreeData.clear();
//		// assign the tree structure
//		List<DDIStore> ddiConcepts = stardatDDIService.findByIdAndElementType(cvItem.getCvScheme().getContainerId(), DDIElement.CVCONCEPT);
//		CvCodeTreeUtils.buildCvConceptTree(ddiConcepts, cvItem.getCvScheme() , cvCodeTreeData);
//		
//		cvItem.setCvConceptTreeData(cvCodeTreeData);
//		// refresh tree
//		dataProvider.refreshAll();
//		// expand all nodes
//		detailTreeGrid.expand( cvItem.getFlattenedCvConceptStreams().collect(Collectors.toList()));
//		// auto select nodes 
//		CVConcept currentCvConcept = cvItem.getCVConceptMap().get( cvItem.getCurrentConceptId());
//		if( currentCvConcept != null ) {
//			cvItem.setCvConcept(currentCvConcept);
//		}
//		
		/*--------------------------*/
		detailTreeGridNew.asSingleSelect().clear();
		editorCodeActionLayout.setCurrentConcept(null);
		refreshCodeActionButton();
		
		
		dataProviderNew  = (TreeDataProvider<CodeDTO>) detailTreeGridNew.getDataProvider();
		cvCodeTreeDataNew = dataProviderNew.getTreeData();
		cvCodeTreeDataNew.clear();
		// assign the tree structure
		List<CodeDTO> codeDTOs = codeService.findWorkflowCodesByVocabulary( vocabulary.getId() );
		CvCodeTreeUtils.buildCvConceptTree( codeDTOs , cvCodeTreeDataNew);
		cvItem.setCvCodeTreeData(cvCodeTreeDataNew);
		// refresh tree
		dataProviderNew.refreshAll();
		// expand all nodes
		detailTreeGridNew.expand( codeDTOs );
		
		// TODO: check missing
	}


	public Language getSelectedLang() {
		return selectedLang;
	}

	public void setSelectedLang(Language selectedLang) {
		this.selectedLang = selectedLang;
	}
	
	@EventBusListenerMethod( scope = EventScope.UI )
	public void resetGrid( DDIStore ddiStore ) {
		cvItem.setCurrentCvId( ddiStore.getParentIdentifier() );
		setDetails();
	}

	public Grid<CodeDTO> getDetailGrid() {
		return detailTreeGridNew;
	}
	
	@EventBusListenerMethod( scope = EventScope.UI )
	public void eventHandle( CvManagerEvent.Event event)
	{
		switch(event.getType()) {
			case CVSCHEME_UPDATED:
				super.updateBreadcrumb();
				setDetails();
				break;
			case CVCONCEPT_CREATED:
				// refresh vocabulary
				vocabulary = vocabularyService.findOne( vocabulary.getId());
				updateDetailGrid();
				
				break;
				
			case CVSCHEME_NEWVERSION:
				super.updateBreadcrumb();
				setDetails();
				break;

//			case CVCONCEPT_TRANSLATION_DIALOG:
//				if( cvCodeTreeData == null || cvCodeTreeData.getRootItems().isEmpty()) {
//					Notification.show("Please add code first");
//				} else if( cvItem.getCvScheme().getLanguagesByTitle().size() == 1) {
//					Notification.show("Please add CV translation first");
//				}
//				else {
//					Window windowTranslate = new DialogTranslateCodeWindow(eventBus, stardatDDIService, vocabularyService, codeService, cvItem.getCvScheme(), cvItem.getCvConcept(), getVocabulary(), getAgency(), code, i18n, UI.getCurrent().getLocale());
//					getUI().addWindow( windowTranslate );
//				}
//				break;
			case CVCONCEPT_ADDCHILD_DIALOG:
				CVConcept childConcept = new CVConcept();
				childConcept.loadSkeleton(childConcept.getDefaultDialect());
				childConcept.createId();
				childConcept.setContainerId( cvItem.getCvScheme().getContainerId());

				DialogAddCodeWindow2 dialogAddCodeWindow2 = new DialogAddCodeWindow2(eventBus, stardatDDIService, vocabularyService, codeService, cvItem.getCvScheme(), childConcept, cvItem.getCvConcept(), getVocabulary(), getAgency(),  i18n, UI.getCurrent().getLocale());
				getUI().addWindow( dialogAddCodeWindow2 );
				break;
			case CVCONCEPT_DELETED:
				
				ConfirmDialog.show( this.getUI(), "Confirm",
				"Are you sure you want to delete the concept \"" + code.getNotation() + "\"?", "yes",
				"cancel",
		
					dialog -> {
						if( dialog.isConfirmed() ) {
							
							// delete Code and Concepts
							codeService.deleteCodeTree(cvCodeTreeDataNew, code);
							cvCodeTreeDataNew.removeItem( code );

							
							detailTreeGridNew.getDataProvider().refreshAll();
//							actionPanel.conceptSelectedChange( null );
							
							// save change log
							VocabularyChangeDTO changeDTO = new VocabularyChangeDTO();
							changeDTO.setVocabularyId( vocabulary.getId());
							changeDTO.setVersionId( editorCodeActionLayout.getCurrentVersion().getId()); 
							changeDTO.setChangeType( "Code deleted" );
							changeDTO.setDescription( "Code " + code.getNotation() + " deleted");
							changeDTO.setDate( LocalDateTime.now() );
							UserDetails loggedUser = SecurityUtils.getLoggedUser();
							changeDTO.setUserId( loggedUser.getId() );
							changeDTO.setUserName( loggedUser.getFirstName() + " " + loggedUser.getLastName());
							vocabularyChangeService.save(changeDTO);
							
							// reindex
							vocabularyService.index(vocabulary);
							
							setCode( null );
							currentConcept = null;
							editorCodeActionLayout.clearCode();
							
							
							refreshCodeActionButton();
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
				((Button) c).removeStyleName("button-language-selected");
			}
		}
		pressedButton.addStyleName("button-language-selected");
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
		
		lTitle2.setValue( i18n.get("view.detail.cvscheme.label.sl.title", locale));
		lDefinition2.setValue( i18n.get("view.detail.cvscheme.label.sl.definition", locale));
		lCode2.setValue( i18n.get("view.detail.cvscheme.label.sl.code", locale));
		lLang2.setValue( i18n.get("view.detail.cvscheme.label.language", locale));
		lVersion2.setValue( i18n.get("view.detail.cvscheme.label.sl.version", locale));
		lDate2.setValue( i18n.get("view.detail.cvscheme.label.sl.publicationdate", locale));
		lTitleOl2.setValue( i18n.get("view.detail.cvscheme.label.tl.title", locale, selectedLang));
		lDefinitionOl2.setValue( i18n.get("view.detail.cvscheme.label.tl.definition", locale, selectedLang));
		lVersionOl2.setValue( i18n.get("view.detail.cvscheme.label.tl.version", locale));
		lDateOl2.setValue( i18n.get("view.detail.cvscheme.label.tl.publicationdate", locale));
		
		lLang3.setValue( i18n.get("view.detail.cvscheme.label.language", locale));
		lVersion3.setValue( i18n.get("view.detail.cvscheme.label.sl.version", locale));
		lDate3.setValue( i18n.get("view.detail.cvscheme.label.sl.publicationdate", locale));
		
		detailTab.getTab(0).setCaption( i18n.get("view.detail.cvconcept.tab.detail", locale));
		detailTab.getTab(1).setCaption( i18n.get("view.detail.cvconcept.tab.version", locale));
		detailTab.getTab(2).setCaption( i18n.get("view.detail.cvconcept.tab.identity", locale));
		detailTab.getTab(3).setCaption( i18n.get("view.detail.cvconcept.tab.ddi", locale));
		detailTab.getTab(4).setCaption( i18n.get("view.detail.cvconcept.tab.license", locale));
		detailTab.getTab(5).setCaption( i18n.get("view.detail.cvconcept.tab.export", locale));
		
//		detailTreeGrid.getColumn("code").setCaption( "Code" );
//		detailTreeGrid.getColumn("prefLabelSl").setCaption( i18n.get("view.detail.cvconcept.column.sl.title", locale) );
//		if( detailTreeGrid.getColumn("prefLabelTl") != null )
//			detailTreeGrid.getColumn("prefLabelTl").setCaption( i18n.get("view.detail.cvconcept.column.tl.title", locale, selectedLang) );
//		detailTreeGrid.getColumn("definitionSl").setCaption( i18n.get("view.detail.cvconcept.column.sl.definition", locale) );
//		if( detailTreeGrid.getColumn("definitionTl") != null )
//			detailTreeGrid.getColumn("definitionTl").setCaption( i18n.get("view.detail.cvconcept.column.tl.definition", locale, selectedLang) );
//		
		detailTreeGridNew.getColumn("code").setCaption( "Code" );
		detailTreeGridNew.getColumn("prefLabelSl").setCaption( i18n.get("view.detail.cvconcept.column.sl.title", locale) );
		if( detailTreeGridNew.getColumn("prefLabelTl") != null )
			detailTreeGridNew.getColumn("prefLabelTl").setCaption( i18n.get("view.detail.cvconcept.column.tl.title", locale, selectedLang) );
		detailTreeGridNew.getColumn("definitionSl").setCaption( i18n.get("view.detail.cvconcept.column.sl.definition", locale) );
		if( detailTreeGridNew.getColumn("definitionTl") != null )
			detailTreeGridNew.getColumn("definitionTl").setCaption( i18n.get("view.detail.cvconcept.column.tl.definition", locale, selectedLang) );
		
//		actionPanel.updateMessageStrings(locale);
	}
}
