package eu.cessda.cvmanager.ui.view;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.LoginSucceedEvent;
import org.gesis.wts.security.SecurityService;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.security.UserDetails;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.ui.view.LoginView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.grid.TreeGridDragSource;
import com.vaadin.ui.components.grid.TreeGridDropTarget;
import com.vaadin.ui.renderers.ComponentRenderer;

import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.LicenceService;
import eu.cessda.cvmanager.service.ResolverService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyChangeService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.LicenceDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;
import eu.cessda.cvmanager.service.mapper.CsvRowToConceptDTOMapper;
import eu.cessda.cvmanager.ui.layout.DdiUsageLayout;
import eu.cessda.cvmanager.ui.layout.EditorCodeActionLayout;
import eu.cessda.cvmanager.ui.layout.EditorCvActionLayout;
import eu.cessda.cvmanager.ui.layout.ExportLayout;
import eu.cessda.cvmanager.ui.layout.IdentityLayout;
import eu.cessda.cvmanager.ui.layout.LicenseLayout;
import eu.cessda.cvmanager.ui.layout.VersionLayout;
import eu.cessda.cvmanager.ui.view.publication.DiscoveryView;
import eu.cessda.cvmanager.ui.view.window.DialogMultipleOption;
import eu.cessda.cvmanager.utils.CvCodeTreeUtils;
import eu.cessda.cvmanager.utils.CvManagerSecurityUtils;

@UIScope
@SpringView(name = EditorDetailsView.VIEW_NAME)
public class EditorDetailsView extends CvView {

	private static final Logger log = LoggerFactory.getLogger(EditorDetailsView.class);
	private static final long serialVersionUID = -1095312295254197091L;
	public static final String VIEW_NAME = "details";
	private Locale locale = UI.getCurrent().getLocale();
	
	private final SpringTemplateEngine templateEngine;
	private final AgencyService agencyService;
	private final VocabularyService vocabularyService;
	private final VersionService versionService;
	private final CodeService codeService;
	private final ConceptService conceptService;
	private final ConfigurationService configService;
	private final VocabularyChangeService vocabularyChangeService;
	private final LicenceService licenceService;
	private final CsvRowToConceptDTOMapper csvRowToConceptDTOMapper;
	private final ResolverService resolverService;

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

	private MLabel lTitle = new MLabel();
	private MLabel lDefinition = new MLabel();
	private MLabel lNotes = new MLabel();
	private MLabel lCode = new MLabel();
	private MLabel lLang = new MLabel();
	private MLabel lVersion = new MLabel();
	private MLabel lDate = new MLabel();
	private MLabel lTitleOl = new MLabel();
	private MLabel lDefinitionOl = new MLabel();
	private MLabel lVersionOl = new MLabel();
	private MLabel lDateOl = new MLabel();

	
	private boolean enableTreeDragAndDrop;
	
	private VersionDTO currentVersion;
	private VersionDTO currentSLVersion;
	private ConceptDTO currentConcept;
	
	private MLabel versionLabel = new MLabel();
	
	private List<LicenceDTO> licenses;
	
	private TreeGrid<CodeDTO> detailTreeGrid = new TreeGrid<>(CodeDTO.class);
	private TreeGridDragSource<CodeDTO> dragSource;
	private TreeGridDropTarget<CodeDTO> dropTarget;

	private TreeData<CodeDTO> cvCodeTreeData;
	private MCssLayout languageLayout = new MCssLayout();
	private List<CodeDTO> draggedItems;
	private TreeDataProvider<CodeDTO> dataProvider;
	
	private VersionLayout versionLayout;
	private ExportLayout exportLayoutContent;
	private IdentityLayout identityLayout;
	private DdiUsageLayout ddiUsageLayout;
	private LicenseLayout licenseLayoutContent;
	
	private Map<String, List<VersionDTO>> orderedLanguageVersionMap;
	private Language sourceLanguage;
	private String activeTab;
	private String versionNumber;
	
	private String highlightCode;
	
	private MCssLayout vocabularyIsWithdrawn = new MCssLayout();
	
	private EditorCvActionLayout editorCvActionLayout;
	private EditorCodeActionLayout editorCodeActionLayout;

	public EditorDetailsView(I18N i18n, EventBus.UIEventBus eventBus, ConfigurationService configService,
			StardatDDIService stardatDDIService, SecurityService securityService, AgencyService agencyService,
			VocabularyService vocabularyService, VersionService versionService, CodeService codeService, ConceptService conceptService,
			SpringTemplateEngine templateEngine, VocabularyChangeService vocabularyChangeService, LicenceService licenceService,
			ResolverService resolverService, CsvRowToConceptDTOMapper csvRowToConceptDTOMapper) {
		super(i18n, eventBus, configService, stardatDDIService, securityService, agencyService, vocabularyService, codeService, EditorDetailsView.VIEW_NAME);
		this.templateEngine = templateEngine;
		this.agencyService = agencyService;
		this.vocabularyService = vocabularyService;
		this.versionService = versionService;
		this.codeService = codeService;
		this.conceptService = conceptService;
		this.configService = configService;
		this.vocabularyChangeService = vocabularyChangeService;
		this.licenceService = licenceService;
		this.resolverService = resolverService;
		this.csvRowToConceptDTOMapper = csvRowToConceptDTOMapper;
		eventBus.subscribe( this, EditorDetailsView.VIEW_NAME );
	}

	@PostConstruct
	public void init() {
		
		editorCvActionLayout = new EditorCvActionLayout("block.action.cv", "block.action.cv.show", i18n, 
				stardatDDIService, agencyService, vocabularyService, versionService, conceptService, codeService, 
				configService, eventBus, vocabularyChangeService, licenceService);
		
		editorCodeActionLayout = new EditorCodeActionLayout("block.action.code", "block.action.code.show", i18n,
				stardatDDIService, agencyService, vocabularyService, versionService, codeService, conceptService, eventBus,
				vocabularyChangeService, csvRowToConceptDTOMapper);
		

		languageLayout.withFullWidth();

		topSection.add(topViewSection/*, topEditSection*/);

		bottomSection.add(bottomViewSection/*, bottomEditSection*/);
		
		sidePanel
			.add( 
				editorCvActionLayout,
				editorCodeActionLayout
			)
			.addStyleName("side-panel-action");
		
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
		
		if(!authorizeViewAccess())
			return;
		
		// activate home button
		topMenuButtonUpdateActive(1);
		
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
						try {
							selectedLang = Language.getEnum( selectedLanguage );
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}
					}
					if(  mappedParams.get("tab") != null )
						activeTab = mappedParams.get("tab");
					else
						activeTab = "detail";
					if(  mappedParams.get("vers") != null )
						versionNumber = mappedParams.get("vers");
					else
						versionNumber = null;
					
					if(  mappedParams.get("code") != null )
						highlightCode = mappedParams.get("code");
				}
				
				
				LoginView.NAVIGATETO_VIEWNAME = EditorDetailsView.VIEW_NAME + "/" + itemPathPart[0];
				cvItem.setCurrentCvId(itemPathPart[0]);
				cvItem.setCurrentNotation(itemPathPart[0]);
				if( itemPathPart.length > 1 )
					cvItem.setCurrentConceptId(itemPathPart[1]);
				
				setDetails() ;

			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

		} else {
			Notification.show("Unable to get the vocabulary detail");
			if( !CvManagerSecurityUtils.isAuthenticated() )
				UI.getCurrent().getNavigator().navigateTo( DiscoveryView.VIEW_NAME);
			else
				UI.getCurrent().getNavigator().navigateTo( EditorSearchView.VIEW_NAME);
			return;
		}
		
	}
	
	/**
	 * 
	 */
	private void setDetails() {
		
		refreshCvScheme();
		
		if( vocabulary == null ) {
			UI.getCurrent().getNavigator().navigateTo( DiscoveryView.VIEW_NAME);
			return;
		}
		
		if( vocabulary.isWithdrawn()) {
			return;
		}
		
		// update breadcrumb
		breadcrumbs
			.addItem(getAgency().getName(), "agency/" + agency.getName())
			.addItem( currentVersion.getNotation() + " " + (currentVersion.getNumber() == null ? "":currentVersion.getNumber()) + " (" + currentVersion.getStatus() + ")", null)
			.build();

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
//			Notification.show("Unable to find vocabulary");
			return;
		}
		
		// check for withdrawn vocabulary
		if( vocabulary.isWithdrawn()) {
			vocabularyIsWithdrawn.removeAllComponents();
			vocabularyIsWithdrawn
				.withWidth("100%")
				.withStyleName( "alert alert-danger" )
				.add(
					new MLabel()
						.withContentMode( ContentMode.HTML)
						.withValue(
							"Unable to access withdrawn CV \"" + vocabulary.getNotation() + "\""
						)
				);
			
			topSection.add( vocabularyIsWithdrawn );
			sidePanel.setVisible( false );
			return;
		}
		
		agency = agencyService.findByName( getVocabulary().getAgencyName());
		
		// get all available licenses
			licenses = licenceService.findAll();
						
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
		
//		if ( currentSLVersion.getUri() != null && !currentSLVersion.getUri().isEmpty()) {
//			List<DDIStore> ddiSchemes = stardatDDIService.findByIdAndElementType(currentSLVersion.getUri(), DDIElement.CVSCHEME);
//
//			if (ddiSchemes != null && !ddiSchemes.isEmpty()) {
//				cvItem.setCvScheme( new CVScheme(ddiSchemes.get(0)) );
//				editorCvActionLayout.setCvScheme( cvItem.getCvScheme() );
//			}
//		}
		
		// find correct version by selected language
		if( selectedLang != null && !vocabulary.getSourceLanguage().equals( selectedLang.getLanguage())) {
			vocabulary.getVersionByUriSlAndLangauge( cvItem.getCurrentCvId(), selectedLang.getLanguage())
			.ifPresent( ver -> currentVersion = ver);
		} else {
			currentVersion = currentSLVersion;
		}
		
		List<String> languages = new ArrayList<>();
		// add tls if exist
		if( vocabulary.getLanguages().size() > 1) {
			languages.addAll( 
				vocabulary.getLanguages().stream()
					.filter( p -> !p.equals( vocabulary.getSourceLanguage()))
					.sorted( (v1, v2) -> v2.compareTo( v1 ))
					.collect( Collectors.toList()) );
		}
		// add source language
		languages.add( vocabulary.getSourceLanguage() );
		
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
				initBottomViewSection();
				executeJavascriptFunction();
				
				setCode( null );
				updateMessageStrings(locale);
				refreshCvActionButton();
				
				// clear cvConcept selection and button
				detailTreeGrid.asSingleSelect().clear();
				editorCodeActionLayout.clearCode();
				refreshCodeActionButton();
			});
			
			languageLayout.add(langButton);
			
			
			if( eachLanguage.equals(sourceLanguage)) {
				langButton.addStyleName("font-bold");
				langButton.setDescription( "source language" );
			}
			if( eachLanguage.equals( selectedLang ) ) {
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
			logoLabel.setValue(  "<img style=\"max-width:120px;max-height:80px\" alt=\"" + agency.getName() + " logo\" src='" + agency.getLogo() + "'>");
			
		MCssLayout topHead = new MCssLayout();
		topHead.withFullWidth().add( logoLabel, topTitle);

		MCssLayout titleSmall = new MCssLayout();
		titleSmall.withFullWidth().add( lTitle.withWidth("140px").withStyleName("leftPart"),
				new MLabel( currentSLVersion.getTitle() ).withStyleName("rightPart"));

		MCssLayout description = new MCssLayout();
		description.withFullWidth().add(lDefinition.withWidth("140px").withStyleName("leftPart"),
				new MLabel( currentSLVersion.getDefinition() ).withStyleName("rightPart").withContentMode( ContentMode.HTML));
		
		MCssLayout notes = new MCssLayout();
		notes.withFullWidth().add(lNotes.withWidth("140px").withStyleName("leftPart"),
				new MLabel( vocabulary.getNotes() ).withStyleName("rightPart").withContentMode( ContentMode.HTML));
		if( vocabulary.getNotes() == null || vocabulary.getNotes().isEmpty() )
			notes.setVisible( false );

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
				new MLabel( currentVersion.getDefinition() ).withStyleName("rightPart").withContentMode( ContentMode.HTML));

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
								lVersion.withWidth("110px").withStyleName("leftPart"),
								versionLabel
						)
				);
		
		if( vocabulary.getStatus().equals( Status.PUBLISHED.toString()) && currentVersion.getPublicationDate() != null)
			langVersDateLayout.add(
					new MCssLayout()
						.withStyleName("col-des-4")
						.add(
								lDate.withWidth("160px").withStyleName("leftPart"),
								new MLabel(currentVersion.getPublicationDate().toString()).withStyleName("rightPart"))
					);

		topViewSection.add(topHead, titleSmall, description, notes, code, titleSmallOl, descriptionOl, langVersDateLayout);
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
			
		detailTreeGrid = new TreeGrid<>(CodeDTO.class);
		detailTreeGrid.addStyleNames("undefined-height");
		detailTreeGrid.removeAllColumns();
		detailTreeGrid.setHeight("800px");
		detailTreeGrid.setWidthUndefined();
		
		updateDetailGrid();	
		
		detailTreeGrid.setSelectionMode( SelectionMode.SINGLE );
		
		detailTreeGrid.addColumn(code -> code.getNotation())
			.setCaption("Code")
			.setExpandRatio(1)
			.setId("code");
	
		detailTreeGrid.addColumn(code -> code.getTitleByLanguage( sourceLanguage ))
			.setCaption(i18n.get("view.detail.cvconcept.column.sl.title", locale))
			.setExpandRatio(1)
			.setId("prefLabelSl");

		if( !selectedLang.equals( Language.valueOfEnum( vocabulary.getSourceLanguage() ) ))
			detailTreeGrid.addColumn(code -> code.getTitleByLanguage(selectedLang))
				.setCaption(i18n.get("view.detail.cvconcept.column.tl.title", locale, selectedLang.toString() ))
				.setExpandRatio(1)
				.setId("prefLabelTl");
		
		detailTreeGrid.addColumn(code -> {
					MLabel definitionLabel = new MLabel( code.getDefinitionByLanguage( sourceLanguage ))
							.withStyleName( "word-brake-normal" ).withContentMode( ContentMode.HTML);
					definitionLabel.setId( "code-" + code.getNotation().replace(".", "-"));
					return definitionLabel;
				}, new ComponentRenderer())
				.setCaption(i18n.get("view.detail.cvconcept.column.sl.definition", locale))
				.setExpandRatio(3)
				.setId("definitionSl");
		
		if( !selectedLang.equals( Language.valueOfEnum( vocabulary.getSourceLanguage() ) ))
			detailTreeGrid.addColumn(code -> {
				return new MLabel( code.getDefinitionByLanguage(selectedLang)).withStyleName( "word-brake-normal" );
			}, new ComponentRenderer())
			.setCaption(i18n.get("view.detail.cvconcept.column.tl.definition", locale, selectedLang.toString() ))
			.setExpandRatio(3)
			.setId("definitionTl");
		
		detailTreeGrid.setSizeFull();
		
		
		detailTreeGrid.asSingleSelect().addValueChangeListener( event -> {		
			if (event.getValue() != null) {
				code = event.getValue();
				
				editorCodeActionLayout.setCurrentCode(code);
				editorCodeActionLayout.setCurrentConcept(null);
				currentConcept = null;
				
				// get concept
				ConceptDTO.getConceptFromCode(currentVersion.getConcepts(), code.getId()).ifPresent( conceptDTO -> {
					if( conceptDTO.isPersisted()) {
						editorCodeActionLayout.setCurrentConcept(conceptDTO);
						currentConcept = conceptDTO;
					} else {
						// query in database for updated one
						ConceptDTO conceptFromDb = conceptService.findOneByCodeNotationAndId( code.getNotation(), code.getId() );
						if( conceptFromDb != null ) {
							editorCodeActionLayout.setCurrentConcept(conceptFromDb);
							currentConcept = conceptFromDb;
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
			detailTreeGrid.select( code);
		}
		
		detailTreeGrid.getColumns().stream().forEach( column -> column.setSortable( false ));
				
		detailLayout.addComponents(detailTreeGrid);
		detailLayout.setSizeFull();
		
		versionLayout = new VersionLayout(i18n, locale, eventBus, agency, vocabulary, vocabularyChangeService, configService, conceptService, versionService, false);
		versionContentLayout.add( versionLayout );
		
		identityLayout = new IdentityLayout(i18n, locale, eventBus, agency, currentVersion, versionService, configService, resolverService, false);
		identifyLayout.add( identityLayout );
		
		ddiUsageLayout = new DdiUsageLayout(i18n, locale, eventBus, agency, currentVersion, versionService, false);
		ddiLayout.add(ddiUsageLayout);
		
		licenseLayoutContent = new LicenseLayout(i18n, locale, eventBus, agency, currentVersion, configService, versionService, licenses, false);
		licenseLayout.add( licenseLayoutContent );
		
		exportLayoutContent = new ExportLayout(i18n, locale, eventBus, cvItem, vocabulary, agency, versionService, codeService, configService, stardatDDIService, licenses, templateEngine, false);
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
			else if (tabsheet.getTab(tab).getId().equals("version")) {
				versionLayout.refreshContent(currentVersion);
			}
			
			if( SecurityUtils.isAuthenticated()) {
				if (tabsheet.getTab(tab).getId().equals("detail")) {
					editorCodeActionLayout.setVisible( true );
				}
				else {
					editorCodeActionLayout.setVisible( false );
				}
			}
		});
		
		bottomViewSection.add(detailTab);
		
		setActiveTab();
	}

	private void executeJavascriptFunction() {
		// scroll code, if code available in url
		if( highlightCode != null ) {
			JavaScript.getCurrent().execute(
				"function offset(el) {" + 
				"    var rect = el.getBoundingClientRect()," + 
				"    scrollLeft = window.pageXOffset || document.documentElement.scrollLeft," + 
				"    scrollTop = window.pageYOffset || document.documentElement.scrollTop;" + 
				"    return { top: rect.top + scrollTop, left: rect.left + scrollLeft }" + 
				"}" + 
				"var mainContainer=document.getElementById('main-container'); " +
				"mainContainer.scrollTop = 0;" +
				"var sidePanel = document.getElementById('side-panel');" + 
				"var sidePanelOffset = offset(sidePanel);" + 
				"mainContainer.addEventListener('scroll', function() { " +
					"if( mainContainer.scrollTop > sidePanelOffset.top && Math.max(document.documentElement.clientWidth, window.innerWidth || 0) > 792){" +
						"sidePanel.style.position='fixed';sidePanel.style.top='0';" +
					"}else{" +
						"sidePanel.style.position='static';" +
					"}" +
				"});" +
				"setTimeout( scrollPage, 1000);" +
				"function scrollPage(){" +
					"var codeRow = document.getElementById('code-"+ highlightCode +"'); " +
					"codeRow.parentNode.parentNode.parentNode.classList.add('code-highlight');" +
					"setTimeout( function(){ codeRow.parentNode.parentNode.parentNode.classList.remove('code-highlight');}, 2000);" +
					"var codeRowOffset = offset(codeRow);" + 
					"console.log(codeRowOffset.top);" + 
					"mainContainer.scrollTop = codeRowOffset.top;" +
				"}"
				);
			highlightCode = null;
		} else {
			JavaScript.getCurrent().execute(
					"function offset(el) {" + 
					"    var rect = el.getBoundingClientRect()," + 
					"    scrollLeft = window.pageXOffset || document.documentElement.scrollLeft," + 
					"    scrollTop = window.pageYOffset || document.documentElement.scrollTop;" + 
					"    return { top: rect.top + scrollTop, left: rect.left + scrollLeft }" + 
					"}" + 
					"var mainContainer=document.getElementById('main-container'); " +
					"mainContainer.scrollTop = 0;" +
					"var sidePanel = document.getElementById('side-panel');" + 
					"var sidePanelOffset = offset(sidePanel);" + 
					"mainContainer.addEventListener('scroll', function() { " +
						"if( mainContainer.scrollTop > sidePanelOffset.top && Math.max(document.documentElement.clientWidth, window.innerWidth || 0) > 792){" +
							"sidePanel.style.position='fixed';sidePanel.style.top='0';" +
						"}else{" +
							"sidePanel.style.position='static';" +
						"}" +
					"});"
					);
		}
	}
	
	private void setActiveTab() {
		if( activeTab != null) {
			switch( activeTab) {
				case "download":
					vocabulary = vocabularyService.getByNotation(cvItem.getCurrentNotation());
					// get all version put it on the map
					orderedLanguageVersionMap = versionService.getOrderedLanguageVersionMap(vocabulary.getId());
					exportLayoutContent.updateGrid(currentVersion, orderedLanguageVersionMap);
					detailTab.setSelectedTab(5);
					break;
				default:
					detailTab.setSelectedTab(0);
			}
		} else
			detailTab.setSelectedTab(0);
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
            			CodeDTO targetRow = event.getDropTargetRow().get();
            			CodeDTO draggedRow = draggedItems.iterator().next();
	                	
	                	// check if code drag and drop to itself
	                	if( targetRow.equals(draggedRow) ) {
	                		return;
	                	}
	                	List<Button> optionButtons = new ArrayList<>();
	                	optionButtons.add( new Button( "As previous sibling" )); // option 0
	                	optionButtons.add( new Button( "As next sibling" )); // option 1
	                	optionButtons.add( new Button( "As child" ));        // option 2
	                	
	                	getUI().addWindow( new DialogMultipleOption(
	                			i18n.get( "dialog.order.code.header" ), 
	                			i18n.get("dialog.order.code.content", 
	                					(draggedRow.getNotation() == null ? draggedRow.getTitleByLanguage( sourceLanguage ): draggedRow.getNotation()), 
	                					(targetRow.getNotation() == null ? targetRow.getTitleByLanguage( sourceLanguage ): targetRow.getNotation())),
	                			optionButtons, 
	                			windowoption ->  {
	                				Integer selectedOptionNumber = windowoption.getSelectedOptionNumber();
	                				if(selectedOptionNumber == null )
	                					return;
	                				
	                				CodeDTO draggedNodeParent = cvCodeTreeData.getParent( draggedRow );
	                				CodeDTO targetNodeParent = cvCodeTreeData.getParent(targetRow);
	                				if( selectedOptionNumber == 0 || selectedOptionNumber == 1) { // move as previous or next sibling
	                					// in order to be able to move as previous sibling 
	                					// the nodes need to be from the same parent
	                					if( !Objects.equals( draggedNodeParent, targetNodeParent)){
	                						// add code as target parent node first
	                						cvCodeTreeData.setParent(draggedRow, targetNodeParent);
	                					}
	                					
	                					// now after the node target and node dragged from same parent,
	                					// set the node position
	                					if( draggedRow.getParent() != null ) {
	                						draggedRow.setNotation( draggedRow.getNotation().substring(draggedRow.getParent().length() + 1));
	                						draggedRow.setParent( null );
	                					}
	                					if( targetNodeParent != null && targetNodeParent.getParent() != null ) {
	                						draggedRow.setParent( targetNodeParent.getParent() );
	                						draggedRow.setNotation( targetNodeParent.getParent() + "." + draggedRow.getNotation());
	                					} 
	                					draggedRow.setUri( draggedRow.getNotation() );

	                					// update tree in vaadin UI
	                					cvCodeTreeData.moveAfterSibling(draggedRow, targetRow);
	                					
	                					// if move to previous sibling, need to do moveAfterSibling twice
	                					// to swap the position between target and dragged node
	                					if( selectedOptionNumber == 0 ) {
	                						cvCodeTreeData.moveAfterSibling(targetRow, draggedRow);
	                					}
	                					dataProvider.refreshAll();
	                					
	                					// save on DB
            							codeService.storeCodeTree(cvCodeTreeData, currentVersion.getConcepts());
	                				} 
	                				else if (selectedOptionNumber == 2) { //move as child
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
	                					draggedRow.setUri( draggedRow.getNotation() );
	                					
            							cvCodeTreeData.setParent(draggedRow, targetRow);
            							dataProvider.refreshAll();
            							detailTreeGrid.expand(draggedRow, targetRow);
            							
            							// save on DB
            							codeService.storeCodeTree(cvCodeTreeData, currentVersion.getConcepts());
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
		detailTreeGrid.asSingleSelect().clear();
		editorCodeActionLayout.setCurrentConcept(null);
		currentConcept = null;
		
		refreshCodeActionButton();
		
		dataProvider  = (TreeDataProvider<CodeDTO>) detailTreeGrid.getDataProvider();
		cvCodeTreeData = dataProvider.getTreeData();
		cvCodeTreeData.clear();
		// assign the tree structure
		List<CodeDTO> codeDTOs = codeService.findWorkflowCodesByVocabulary( vocabulary.getId() );
		CvCodeTreeUtils.buildCvConceptTree( codeDTOs , cvCodeTreeData);
		cvItem.setCvCodeTreeData(cvCodeTreeData);
		// refresh tree
		dataProvider.refreshAll();
		// expand all nodes
		detailTreeGrid.expand( codeDTOs );
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
		return detailTreeGrid;
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

			case CVCONCEPT_DELETED:
				
				// get the editor's codes
				List<CodeDTO> wCodeDTOs = codeService.findWorkflowCodesByVocabulary( vocabulary.getId() );
				// Find if code has children
				List<CodeDTO> childWCodeDTOs = wCodeDTOs
						.stream()
						.filter( p -> p.getParent() != null )
						.filter( p -> p.getParent().equals( code.getNotation()))
						.collect( Collectors.toList());
				String popUpDialogMessageKey = null;
				if( childWCodeDTOs.isEmpty()) {
					if( currentVersion.getItemType().equals(ItemType.SL.toString()))
						popUpDialogMessageKey = "dialog.confirm.delete.code";
					else
						popUpDialogMessageKey = "dialog.confirm.delete.code.tl";
				}else {
					if( currentVersion.getItemType().equals(ItemType.SL.toString()))
						popUpDialogMessageKey = "dialog.confirm.delete.code.and.child";
					else
						popUpDialogMessageKey = "dialog.confirm.delete.code.tl.and.child";
				}
					
				ConfirmDialog.show( this.getUI(), "Confirm",
				i18n.get( popUpDialogMessageKey, "\"" + code.getNotation() + "\" - \"" + currentConcept.getTitle() + "\" ("+ currentVersion.getLanguage() +")"),
				i18n.get("dialog.button.yes"),
				i18n.get("dialog.button.cancel"),
						
					dialog -> {
						if( dialog.isConfirmed() ) {
							String notationCode = code.getNotation();
							
							if( currentVersion.getItemType().equals(ItemType.SL.toString())) {
								// delete Code and Concepts
								codeService.deleteCodeTree( cvCodeTreeData, code, currentVersion);
								cvCodeTreeData.removeItem( code );
								setCode( null );
							} else {
								// clear Code on specific TL and remove TL Concepts
								codeService.deleteCodeTreeTl( cvCodeTreeData, code, currentVersion);
							}
							currentConcept = null;
							editorCodeActionLayout.clearCode();
							
							detailTreeGrid.getDataProvider().refreshAll();
							
							// save change log
							VocabularyChangeDTO changeDTO = new VocabularyChangeDTO();
							changeDTO.setVocabularyId( vocabulary.getId());
							changeDTO.setVersionId( editorCodeActionLayout.getCurrentVersion().getId()); 
							changeDTO.setChangeType( "Code deleted" );
							changeDTO.setDescription( "Code " + notationCode + " deleted");
							changeDTO.setDate( LocalDateTime.now() );
							UserDetails loggedUser = SecurityUtils.getLoggedUser();
							changeDTO.setUserId( loggedUser.getId() );
							changeDTO.setUserName( loggedUser.getFirstName() + " " + loggedUser.getLastName());
							vocabularyChangeService.save(changeDTO);
							
							// reindex
							vocabularyService.index(vocabulary);
							
							refreshCodeActionButton();
							
							// there is no way to update existing tree without add/delete,
							// refresh entire block
							if( currentVersion.getItemType().equals(ItemType.TL.toString())) {
								initBottomViewSection();
							}
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
		lNotes.setValue( i18n.get("view.detail.cvscheme.label.sl.note", locale));
		lLang.setValue( i18n.get("view.detail.cvscheme.label.language", locale));
		lVersion.setValue( i18n.get("view.detail.cvscheme.label.sl.version", locale));
		lDate.setValue( i18n.get("view.detail.cvscheme.label.sl.publicationdate", locale));
		lTitleOl.setValue( i18n.get("view.detail.cvscheme.label.tl.title", locale, selectedLang));
		lDefinitionOl.setValue( i18n.get("view.detail.cvscheme.label.tl.definition", locale, selectedLang));
		lVersionOl.setValue( i18n.get("view.detail.cvscheme.label.tl.version", locale));
		lDateOl.setValue( i18n.get("view.detail.cvscheme.label.tl.publicationdate", locale));

		detailTab.getTab(0).setCaption( i18n.get("view.detail.cvconcept.tab.detail", locale));
		detailTab.getTab(1).setCaption( i18n.get("view.detail.cvconcept.tab.version", locale));
		detailTab.getTab(2).setCaption( i18n.get("view.detail.cvconcept.tab.identity", locale));
		detailTab.getTab(3).setCaption( i18n.get("view.detail.cvconcept.tab.ddi", locale));
		detailTab.getTab(4).setCaption( i18n.get("view.detail.cvconcept.tab.license", locale));
		detailTab.getTab(5).setCaption( i18n.get("view.detail.cvconcept.tab.export", locale));
		
		detailTreeGrid.getColumn("code").setCaption( i18n.get("view.detail.cvconcept.column.sl.code", locale)  );
		detailTreeGrid.getColumn("prefLabelSl").setCaption( i18n.get("view.detail.cvconcept.column.sl.title", locale) );
		if( detailTreeGrid.getColumn("prefLabelTl") != null )
			detailTreeGrid.getColumn("prefLabelTl").setCaption( i18n.get("view.detail.cvconcept.column.tl.title", locale, selectedLang) );
		detailTreeGrid.getColumn("definitionSl").setCaption( i18n.get("view.detail.cvconcept.column.sl.definition", locale) );
		if( detailTreeGrid.getColumn("definitionTl") != null )
			detailTreeGrid.getColumn("definitionTl").setCaption( i18n.get("view.detail.cvconcept.column.tl.definition", locale, selectedLang) );
		
//		actionPanel.updateMessageStrings(locale);
	}
}
