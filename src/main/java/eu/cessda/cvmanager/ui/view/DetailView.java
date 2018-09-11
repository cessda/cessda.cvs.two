package eu.cessda.cvmanager.ui.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import eu.cessda.cvmanager.service.dto.LicenseDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.mapper.VocabularyMapper;
import eu.cessda.cvmanager.ui.CVManagerUI;
import eu.cessda.cvmanager.ui.layout.DdiUsageLayout;
import eu.cessda.cvmanager.ui.layout.EditorCodeActionLayout;
import eu.cessda.cvmanager.ui.layout.EditorCvActionLayout;
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
@SpringView(name = DetailView.VIEW_NAME)
public class DetailView extends CvView {

	private static final long serialVersionUID = 6904286186508174249L;
	public static final String VIEW_NAME = "detail";
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
	private MCssLayout newerVersionAvailable = new MCssLayout();
	
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
	private static final CVEditor[] cvEditors = new CVEditor[2];
	
	
	private MLabel lAgency = new MLabel("Agency");
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
	
	
	private VersionDTO currentVersion;
	private ConceptDTO currentConcept;
	private VersionDTO latestSlVersion;
	
	private MLabel versionLabel = new MLabel();

	
	private TreeGrid<CVConcept> detailTreeGrid = new TreeGrid<>(CVConcept.class);
	private TreeGridDragSource<CVConcept> dragSource;
	private TreeGridDropTarget<CVConcept> dropTarget;

	private TreeData<CVConcept> cvCodeTreeData;
	private MCssLayout languageLayout = new MCssLayout();
	private List<CVConcept> draggedItems;
	private TreeDataProvider<CVConcept> dataProvider;
	
	private VersionLayout versionLayout;
	private ExportLayout exportLayoutContent;
	private IdentityLayout identityLayout;
	private DdiUsageLayout ddiUsageLayout;
	private LicenseLayout licenseLayoutContent;
	
	private Map<String, List<VersionDTO>> orderedLanguageVersionMap;
	private List<LicenseDTO> licenses;
	private Language sourceLanguage;
	private String activeTab;
	

	public DetailView(I18N i18n, EventBus.UIEventBus eventBus, ConfigurationService configService,
			StardatDDIService stardatDDIService, SecurityService securityService, AgencyService agencyService,
			VocabularyService vocabularyService, VersionService versionService, CodeService codeService, ConceptService conceptService,
			VocabularySearchRepository vocabularySearchRepository, TemplateEngine templateEngine,
			VocabularyChangeService vocabularyChangeService, LicenseService licenseService) {
		super(i18n, eventBus, configService, stardatDDIService, securityService, agencyService, vocabularyService, 
				codeService, vocabularySearchRepository, DetailView.VIEW_NAME);
		this.templateEngine = templateEngine;
		this.agencyService = agencyService;
		this.vocabularyService = vocabularyService;
		this.versionService = versionService;
		this.codeService = codeService;
		this.conceptService = conceptService;
		this.vocabularyChangeService = vocabularyChangeService;
		this.licenseService = licenseService;
		eventBus.subscribe( this, DetailView.VIEW_NAME );
	}

	@PostConstruct
	public void init() {
		languageLayout.withFullWidth();

		topSection.add(topViewSection/*, topEditSection*/);

		bottomSection.add(bottomViewSection/*, bottomEditSection*/);

		mainContainer
		.withStyleName("margin-left10")
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
	
	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		
		locale = UI.getCurrent().getLocale();

		if (event.getParameters() != null) {
			try {
				Map<String, String> mappedParams = new HashMap<>();
				String[] itemPath = event.getParameters().split("\\?");
				String[] itemPathPart = itemPath[0].split("/");
				if(itemPath.length > 1) {
					List<NameValuePair> params = URLEncodedUtils.parse( itemPath[1],  Charset.forName("UTF-8"));
					mappedParams = params.stream().collect(
					        Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
					String selectedLanguage = mappedParams.get("lang");
					if( selectedLanguage != null ) {
						cvItem.setCurrentLanguage( mappedParams.get("lang") );
						selectedLang = Language.getEnum( selectedLanguage );
						
//						// set selected language and version
//						editorCvActionLayout.setSelectedLanguage(selectedLang);
					} else {
						selectedLang = null;
//						editorCvActionLayout.setSelectedLanguage(null);
					}
					if(  mappedParams.get("tab") != null )
						activeTab = mappedParams.get("tab");
					else
						activeTab = "detail";
				} else {
					selectedLang = null;
					activeTab = "detail";
				}
				LoginView.NAVIGATETO_VIEWNAME = DetailView.VIEW_NAME + "/" + itemPathPart[0];
				if( itemPathPart.length > 0 )
					cvItem.setCurrentNotation(itemPathPart[0]);
				if( mappedParams.get("url") != null)
					cvItem.setCurrentCvId( mappedParams.get("url") );

				
				setDetails() ;

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
	}
	
	
	private void setDetails() {
//		setFormMode(FormMode.view);
		
		refreshCvScheme();
//		refreshCvConcepts();
		
		// update breadcrumb
		breadcrumbs
			.addItem(getAgency().getName(), "agency")
			.addItem( vocabulary.getNotation() + " " + currentVersion.getNumber(), null)
			.build();

//		initTopViewSection();
//		initTopEditSection();
//		initBottomViewSection();
		//initBottomEditSection();
		updateMessageStrings(UI.getCurrent().getLocale());
		
		// TODO: Workaround so that the translation label visible ~ not efficient need correct solution
//		initTopViewSection();
		
		topPanel.setVisible( false );
		
//		refreshCvActionButton();
//		refreshCodeActionButton();
	}

//	private void refreshCvActionButton() {
//		if( editorCvActionLayout.hasActionRight() ) {
//			mainContainer.removeStyleName("margin-left10");
//			sidePanel.setVisible( true );
//		}
//		else {
//			mainContainer.addStyleName("margin-left10");
//			sidePanel.setVisible( false );
//		}
//	}
//
//	private void refreshCodeActionButton() {
//		if( editorCodeActionLayout.hasActionRight() ) {
//			editorCodeActionLayout.setVisible( true );
//		}
//		else {
//			editorCodeActionLayout.setVisible( false );
//		}
//	}

	private void refreshCvScheme() {
		languageLayout.removeAllComponents();
		List<DDIStore> ddiSchemes = null;
		if( cvItem.getCurrentCvId() != null ) {
			ddiSchemes = stardatDDIService.findByIdAndElementType(cvItem.getCurrentCvId(), DDIElement.CVSCHEME);
		}
		
		if( ddiSchemes == null ) {
			if( cvItem.getCurrentNotation() != null ) {
				vocabulary = vocabularyService.getByNotation(cvItem.getCurrentNotation());
				
				if(vocabulary != null ) {
					ddiSchemes = stardatDDIService.findByIdAndElementType( vocabulary.getUri(), DDIElement.CVSCHEME);
					currentVersion = versionService.getByUri( vocabulary.getUri() );
				}
				
			}
		} else {
			// find in Vocabulary entity as well
			currentVersion = versionService.getByUri( cvItem.getCurrentCvId() );
			vocabulary = vocabularyService.findOne( currentVersion.getVocabularyId() );
		}
		
		if (ddiSchemes != null && !ddiSchemes.isEmpty()) {
			cvItem.setCvScheme( new CVScheme(ddiSchemes.get(0)) );
		}
		
		String owner = cvItem.getCvScheme().getOwnerAgency().get(0).getName();
		if( owner != null && !owner.isEmpty() )
			setAgency( agencyService.findByName( owner));
		
		if( getAgency() == null)
			setAgency( agencyService.findOne(1L) );
		
		// get all available licenses
		licenses = licenseService.findAll();
		
		
//		Set<String> languages = cvItem.getCvScheme().getLanguagesByTitle();
		Set<String> languages = vocabulary.getLanguagesPublished();
		
		sourceLanguage = Language.valueOfEnum( vocabulary.getSourceLanguage());
		selectedLang = Language.valueOfEnum( currentVersion.getLanguage());
		
//		editorCvActionLayout.setSourceLanguage( sourceLanguage );
//		editorCvActionLayout.setCvScheme( cvItem.getCvScheme() );
//		editorCvActionLayout.setAgency( agency );
//		editorCvActionLayout.setVocabulary( vocabulary );
//		
//		editorCodeActionLayout.setSourceLanguage( sourceLanguage );
//		editorCodeActionLayout.setCvScheme( cvItem.getCvScheme() );
//		editorCodeActionLayout.setAgency( agency );
//		editorCodeActionLayout.setVocabulary(vocabulary);
		
		languages.forEach(item -> {
			
			MButton langButton = new MButton(item.toUpperCase());
			langButton.withStyleName("langbutton");
			
			if( item.equalsIgnoreCase( sourceLanguage.toString() ))
				langButton.addStyleName( "button-source-language" );
			
			if( item.equalsIgnoreCase( selectedLang.toString() ))
				langButton.addStyleName( "button-language-selected" );
			
			langButton.addClickListener(e -> {
				applyButtonStyle(e.getButton());
				
				cvItem.setCurrentLanguage(e.getButton().getCaption().toLowerCase());
				setSelectedLang( Language.getEnum( e.getButton().getCaption().toLowerCase()) );
//				actionPanel.languageSelectionChange( configService.getDefaultSourceLanguage(), cvItem.getCurrentLanguage());
				
//				setFormMode(FormMode.view);
				

				
//				editorCvActionLayout.setSelectedLanguage(selectedLang);
//				editorCodeActionLayout.setSelectedLanguage(selectedLang);
				
//				editorCvActionLayout.setCurrentVersion( null );
//				editorCodeActionLayout.setCurrentVersion( null );

				versionLabel.setValue( currentVersion.getNumber() + (selectedLang.equals( sourceLanguage ) ? ""
						: "-" + selectedLang.toString())  + 
						( currentVersion.getStatus().equals( Status.PUBLISHED.toString() ) ? "":" (" + currentVersion.getStatus() + ")"));
				
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
//				refreshCvActionButton();
				
				// clear cvConcept selection and button
				detailTreeGrid.asSingleSelect().clear();
//				editorCodeActionLayout.clearCode();
//				refreshCodeActionButton();
			});
			languageLayout.add(langButton);
			if( item.equals(sourceLanguage.toString())) {
				langButton.addStyleName("font-bold");
				langButton.setDescription( "source language" );
			}
			if( item.equals( selectedLang.toString() ) ) {
//				editorCvActionLayout.setSelectedLanguage( Language.getEnum( item) );
				langButton.click();
			}
		});
	}

	private void initTopViewSection() {
		topViewSection.removeAllComponents();
		
		
		if( SecurityUtils.isAuthenticated()) {
			String baseUrl = configService.getServerContextPath() + "/#!" + DetailsView.VIEW_NAME + "/" + vocabulary.getNotation();
			topViewSection.add( new MLabel()
					.withContentMode( ContentMode.HTML)
					.withStyleName("pull-right")
					.withValue(
						" " +
								"<a href='" + baseUrl + "'> View in Editor</a> "
					) );
		}
		
		vocabulary
		.getLatestVersionByLanguage( sourceLanguage.toString(), null, Status.PUBLISHED.toString())
		.ifPresent( slVersion -> {
			String baseUrl = configService.getServerContextPath() + "/#!" + DetailView.VIEW_NAME + "/";
			latestSlVersion = slVersion;
			// warning about newer version
			if( latestSlVersion.getId() > currentVersion.getId()) {
				
				String cvUrl = null;
				try {
					cvUrl = baseUrl +  vocabulary.getNotation() + "?url=" +URLEncoder.encode(latestSlVersion.getUri(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					cvUrl = baseUrl  + vocabulary.getNotation() + "?url=" + latestSlVersion.getUri();
					e.printStackTrace();
				}
				
				newerVersionAvailable.removeAllComponents();
				newerVersionAvailable
					.withWidth("100%")
					.withStyleName( "alert alert-warning" )
					.add(
						new MLabel()
							.withContentMode( ContentMode.HTML)
							.withValue(
								"Newer version is available " +
										"<a href='" + cvUrl + "'>" + latestSlVersion.getNotation() + ". " + latestSlVersion.getNumber() +"</a> "
							)
					);
				
				topViewSection.add( newerVersionAvailable );
			}
		});

		MLabel topTitle = new MLabel();
		topTitle.withStyleName("topTitle").withContentMode(ContentMode.HTML)
				.withValue(cvItem.getCvScheme().getOwnerAgency().get(0).getName() + " Controlled Vocabulary for "
						+ cvItem.getCvScheme().getTitleByLanguage( sourceLanguage.toString() ) + "</strong>");

		MLabel logoLabel = new MLabel()
			.withContentMode( ContentMode.HTML )
			.withWidth("120px");
			
		if( agency.getLogo() != null && !agency.getLogo().isEmpty())
			logoLabel.setValue(  "<img style=\"width:120px\" alt=\"" + agency.getName() + " logo\" src='" + agency.getLogo() + "'>");
			
		MCssLayout topHead = new MCssLayout();
		topHead.withFullWidth().add( logoLabel, topTitle);

		MCssLayout titleSmall = new MCssLayout();
		titleSmall.withFullWidth().add( lTitle.withWidth("140px").withStyleName("leftPart"),
				new MLabel(cvItem.getCvScheme().getTitleByLanguage( sourceLanguage.toString() )).withStyleName("rightPart"));

		MCssLayout description = new MCssLayout();
		description.withFullWidth().add(lDefinition.withWidth("140px").withStyleName("leftPart"),
				new MLabel(cvItem.getCvScheme().getDescriptionByLanguage( sourceLanguage.toString() )).withStyleName("rightPart"));

		MCssLayout code = new MCssLayout();
		code.withFullWidth().add(lCode.withWidth("140px").withStyleName("leftPart"),
				new MLabel(cvItem.getCvScheme().getCode()).withStyleName("rightPart"));

		MCssLayout titleSmallOl = new MCssLayout();
		titleSmallOl.withFullWidth().add(
				lTitleOl.withWidth("140px").withStyleName("leftPart"),
				new MLabel(cvItem.getCvScheme().getTitleByLanguage(selectedLang.toString())).withStyleName("rightPart"));

		MCssLayout descriptionOl = new MCssLayout();
		descriptionOl.withFullWidth().add(
				lDefinitionOl.withWidth("140px").withStyleName("leftPart"),
				new MLabel(cvItem.getCvScheme().getDescriptionByLanguage(selectedLang.toString())).withStyleName("rightPart"));

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
		
		langVersDateLayout.add(
				new MCssLayout()
					.withStyleName("col-des-4")
					.add(
							lDate.withWidth("140px").withStyleName("leftPart"),
							new MLabel(currentVersion.getPublicationDate() == null ? "":currentVersion.getPublicationDate().toString()).withStyleName("rightPart"))
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
		
		detailTreeGrid = new TreeGrid<>(CVConcept.class);
		detailTreeGrid.addStyleNames("undefined-height");
		detailTreeGrid.removeAllColumns();
		detailTreeGrid.setHeight("800px");
		detailTreeGrid.setWidthUndefined();
		
		updateDetailGrid();
		
		// Set CV item object in the ActionLayout
//		editorCvActionLayout.setCvItem(cvItem);
		
		detailTreeGrid.setSelectionMode( SelectionMode.SINGLE );
		
		detailTreeGrid.addColumn(concept -> concept.getNotation())
			.setCaption("Code")
			.setEditorComponent(codeEditor, (concept, value) -> concept.setNotation(value))
			.setExpandRatio(1)
			.setId("code");
	
//		detailTreeGrid.addColumn(concept -> concept.getPrefLabelByLanguage( sourceLanguage.toString() ))
//			.setCaption(i18n.get("view.detail.cvconcept.column.sl.title", locale))
//			.setEditorComponent(prefLabelEditor, (concept, value) -> concept.setPrefLabelByLanguage( sourceLanguage.toString() , value))
//			.setExpandRatio(1)
//			.setId("prefLabelSl");

//		if( !selectedLang.equals( Language.valueOfEnum( "english" ) ))
			detailTreeGrid.addColumn(concept -> concept.getPrefLabelByLanguage(selectedLang.toString()))
				.setCaption(i18n.get("view.detail.cvconcept.column.tl.title", locale, selectedLang.toString() ))
				//.setEditorBinding(prefLabelBinding)
				.setEditorComponent(prefLanguageEditor, (concept, value) -> concept.setPrefLabelByLanguage( selectedLang.toString(), value))
				.setExpandRatio(1)
				.setId("prefLabelTl");// Component(prefLanguageEditor,
//		
//		detailTreeGrid.addColumn(concept -> {
//					return new MLabel( concept.getDescriptionByLanguage( sourceLanguage.toString() )).withStyleName( "word-brake-normal" );
//				}, new ComponentRenderer())
//				.setCaption(i18n.get("view.detail.cvconcept.column.sl.definition", locale))
//				.setExpandRatio(3)
//				.setId("definitionSl");
		
//		if( !selectedLang.equals( Language.valueOfEnum( "english" ) ))
			detailTreeGrid.addColumn(concept -> {
				return new MLabel( concept.getDescriptionByLanguage(selectedLang.toString())).withStyleName( "word-brake-normal" );
			}, new ComponentRenderer())
			.setCaption(i18n.get("view.detail.cvconcept.column.tl.definition", locale, selectedLang.toString() ))
			.setExpandRatio(3)
			.setId("definitionTl");
		
		detailTreeGrid.setSizeFull();
		
		
		detailTreeGrid.asSingleSelect().addValueChangeListener( event -> {		
			if (event.getValue() != null) {
				cvItem.setCvConcept( event.getValue() );
				
				// get code
				code = codeService.getByUri( cvItem.getCvConcept().getId());
				if( code == null )
					code = CodeDTO.generateFromCVConcept( cvItem.getCvConcept() );
				
//				editorCodeActionLayout.setCvConcept( cvItem.getCvConcept() );
//				editorCodeActionLayout.setCurrentCode(code);
				
				// get concept
				ConceptDTO.getConceptFromCode(currentVersion.getConcepts(), code.getId()).ifPresent( conceptDTO -> {
					if( conceptDTO.isPersisted()) {
						currentConcept = conceptDTO;
//						editorCodeActionLayout.setCurrentConcept(conceptDTO);
					} else {
						// query in database for updated one
						ConceptDTO conceptFromDb = conceptService.findOneByCodeNotationAndId( code.getNotation(), code.getId() );
						if( conceptFromDb != null ) {
							currentConcept = conceptFromDb;
//							editorCodeActionLayout.setCurrentConcept(conceptFromDb);
						}
					}
				});
								
//				refreshCodeActionButton();
				
            } else {
            	cvItem.setCvConcept(  null );
//            	editorCodeActionLayout.clearCode();
//				refreshCodeActionButton();
            }
		});
		
		// select row programatically
		if(cvItem.getCvConcept() != null ) {
			detailTreeGrid.select( cvItem.getCvConcept());
			//detailTreeGrid.scrollTo( 13 );
			
			// get code
			code = codeService.getByUri( cvItem.getCvConcept().getContainerId());
			if( code == null )
				code = CodeDTO.generateFromCVConcept( cvItem.getCvConcept() );
		}
		
		detailTreeGrid.getColumns().stream().forEach( column -> column.setSortable( false ));
				
		detailLayout.addComponents(detailTreeGrid);
		//detailLayout.setMargin(false);
		//detailLayout.setSpacing(false);
		detailLayout.setSizeFull();
		//detailLayout.setExpandRatio(detailTreeGrid, 1);
		
		versionLayout = new VersionLayout(i18n, locale, eventBus, agency, vocabulary, vocabularyChangeService, configService);
		versionContentLayout.add( versionLayout );
		
		identityLayout = new IdentityLayout(i18n, locale, eventBus, agency, currentVersion, versionService, configService, true);
		identifyLayout.add( identityLayout );
		
		ddiUsageLayout = new DdiUsageLayout(i18n, locale, eventBus, agency, currentVersion, versionService, true);
		ddiLayout.add(ddiUsageLayout);
		
		licenseLayoutContent = new LicenseLayout(i18n, locale, eventBus, agency, currentVersion, versionService, licenses,  true);
		licenseLayout.add( licenseLayoutContent );
		
		exportLayoutContent = new ExportLayout(i18n, locale, eventBus, cvItem, vocabulary, agency, versionService, configService, licenses, templateEngine, true);
		exportLayout.add(exportLayoutContent);
		exportLayout.setSizeFull();
		
		detailTab.addSelectedTabChangeListener( e -> {
			TabSheet tabsheet = e.getTabSheet();
			 // Find the tab (here we know it's a layout)
	        Layout tab = (Layout) tabsheet.getSelectedTab();
	        
			if( tabsheet.getTab(tab).getId().equals("export")) {
				vocabulary = vocabularyService.findOne(currentVersion.getVocabularyId());
				// get all version put it on the map
				orderedLanguageVersionMap = versionService.getOrderedLanguageVersionMap(vocabulary.getId());
				exportLayoutContent.updateGrid(currentVersion, orderedLanguageVersionMap);
			}
			else if (tabsheet.getTab(tab).getId().equals("version")) {
				versionLayout.refreshContent(currentVersion);
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
	
	

	@SuppressWarnings("unchecked")
	public void updateDetailGrid() {		
		dataProvider = (TreeDataProvider<CVConcept>) detailTreeGrid.getDataProvider();
		cvCodeTreeData = dataProvider.getTreeData();
		cvCodeTreeData.clear();
		// assign the tree structure
		List<DDIStore> ddiConcepts = stardatDDIService.findByIdAndElementType(cvItem.getCvScheme().getContainerId(), DDIElement.CVCONCEPT);
		CvCodeTreeUtils.buildCvConceptTree(ddiConcepts, cvItem.getCvScheme(), cvCodeTreeData);
		
		cvItem.setCvConceptTreeData(cvCodeTreeData);
		// refresh tree
		dataProvider.refreshAll();
		// expand all nodes
		detailTreeGrid.expand( cvItem.getFlattenedCvConceptStreams().collect(Collectors.toList()));
		// auto select nodes 
		CVConcept currentCvConcept = cvItem.getCVConceptMap().get( cvItem.getCurrentConceptId());
		if( currentCvConcept != null ) {
			cvItem.setCvConcept(currentCvConcept);
		}
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

	public Grid<CVConcept> getDetailGrid() {
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
			case CVCONCEPT_DELETED:
				
				ConfirmDialog.show( this.getUI(), "Confirm",
				"Are you sure you want to delete the concept \"" + cvItem.getCvConcept().getPrefLabelByLanguage( configService.getDefaultSourceLanguage() ) + "\"?", "yes",
				"cancel",
		
					dialog -> {
						if( dialog.isConfirmed() ) {
							stardatDDIService.deleteConceptTree(cvCodeTreeData, cvItem.getCvConcept());
							cvCodeTreeData.removeItem( cvItem.getCvConcept() );
							
							cvItem.setCvConcept( null );
//							editorCodeActionLayout.clearCode();
//							refreshCodeActionButton();
							
							if( code.isPersisted()) {
								codeService.deleteCodeTree(code, vocabulary.getId());
							}
							
							detailTreeGrid.getDataProvider().refreshAll();
//							actionPanel.conceptSelectedChange( null );
							
							// save change log
							VocabularyChangeDTO changeDTO = new VocabularyChangeDTO();
							changeDTO.setVocabularyId( vocabulary.getId());
//							changeDTO.setVersionId( editorCodeActionLayout.getCurrentVersion().getId()); 
							changeDTO.setChangeType( "Code deleted" );
							changeDTO.setDescription( "Code " + code.getNotation() + " added");
							changeDTO.setDate( LocalDateTime.now() );
							UserDetails loggedUser = SecurityUtils.getLoggedUser();
							changeDTO.setUserId( loggedUser.getId() );
							changeDTO.setUserName( loggedUser.getFirstName() + " " + loggedUser.getLastName());
							vocabularyChangeService.save(changeDTO);
							
							// reindex
							vocabularyService.index(vocabulary);
							
							setCode( null );
						}
					}

				);
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
		
		detailTreeGrid.getColumn("code").setCaption( "Code" );
//		detailTreeGrid.getColumn("prefLabelSl").setCaption( i18n.get("view.detail.cvconcept.column.sl.title", locale) );
		if( detailTreeGrid.getColumn("prefLabelTl") != null )
			detailTreeGrid.getColumn("prefLabelTl").setCaption( i18n.get("view.detail.cvconcept.column.tl.title", locale, selectedLang) );
//		detailTreeGrid.getColumn("definitionSl").setCaption( i18n.get("view.detail.cvconcept.column.sl.definition", locale) );
		if( detailTreeGrid.getColumn("definitionTl") != null )
			detailTreeGrid.getColumn("definitionTl").setCaption( i18n.get("view.detail.cvconcept.column.tl.definition", locale, selectedLang) );
		
//		actionPanel.updateMessageStrings(locale);
	}
}
