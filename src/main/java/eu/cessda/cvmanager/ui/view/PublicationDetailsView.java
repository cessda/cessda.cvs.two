package eu.cessda.cvmanager.ui.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.vaadin.ui.*;
import eu.cessda.cvmanager.utils.CvManagerSecurityUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityService;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.ui.view.LoginView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.ComponentRenderer;

import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.LicenceService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyChangeService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.LicenceDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.ui.layout.DdiUsageLayout;
import eu.cessda.cvmanager.ui.layout.ExportLayout;
import eu.cessda.cvmanager.ui.layout.IdentityLayout;
import eu.cessda.cvmanager.ui.layout.LicenseLayout;
import eu.cessda.cvmanager.ui.layout.VersionLayout;
import eu.cessda.cvmanager.utils.CvCodeTreeUtils;

@UIScope
@SpringView(name = PublicationDetailsView.VIEW_NAME)
public class PublicationDetailsView extends CvView {

	private static final Logger log = LoggerFactory.getLogger(PublicationDetailsView.class);
	private static final long serialVersionUID = 6904286186508174249L;
	public static final String VIEW_NAME = "detail";
	private Locale locale = UI.getCurrent().getLocale();
	private final SpringTemplateEngine templateEngine;
	private final AgencyService agencyService;
	private final VocabularyService vocabularyService;
	private final VersionService versionService;
	private final CodeService codeService;
	private final ConceptService conceptService;
	private final VocabularyChangeService vocabularyChangeService;
	private final LicenceService licenceService;
	
	private Language selectedLang = Language.ENGLISH;

	private MCssLayout topSection = new MCssLayout().withFullWidth();
	private MCssLayout topViewSection = new MCssLayout().withFullWidth();
	private MCssLayout bottomSection = new MCssLayout().withFullWidth();
	private MCssLayout bottomViewSection = new MCssLayout().withFullWidth();
	
	private MCssLayout newerVersionAvailable = new MCssLayout();
	private MCssLayout vocabularyIsWithdrawn = new MCssLayout();
	
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
	private MLabel lNotesOl = new MLabel();
	private MLabel lVersionOl = new MLabel();
	private MLabel lDateOl = new MLabel();
	
	private LanguageMenu langMenu;
	private String highlightCode;
	
	private VersionDTO currentVersion;
	private VersionDTO currentSlVersion;
	private VersionDTO latestSlVersion;
	
	private MLabel versionLabel = new MLabel();

	private List<MButton> langButtons = new ArrayList<>();
	private Map<VersionDTO,MButton> langSubButtons = new HashMap<>();
	
	private TreeGrid<ConceptDTO> detailTreeGrid = new TreeGrid<>(ConceptDTO.class);

	private TreeData<ConceptDTO> cvCodeTreeData;
	private MCssLayout languageLayout = new MCssLayout();
	private TreeDataProvider<ConceptDTO> dataProvider;
	
	private VersionLayout versionLayout;
	private ExportLayout exportLayoutContent;
	private IdentityLayout identityLayout;
	private DdiUsageLayout ddiUsageLayout;
	private LicenseLayout licenseLayoutContent;
	
	private Map<String, List<VersionDTO>> orderedLanguageVersionMap;
	private List<LicenceDTO> licenses;
	private Language sourceLanguage;
	private String activeTab;


	public PublicationDetailsView(I18N i18n, EventBus.UIEventBus eventBus, ConfigurationService configService,
			StardatDDIService stardatDDIService, SecurityService securityService, AgencyService agencyService,
			VocabularyService vocabularyService, VersionService versionService, CodeService codeService, ConceptService conceptService,
			SpringTemplateEngine templateEngine, VocabularyChangeService vocabularyChangeService, LicenceService licenceService) {
		super(i18n, eventBus, configService, stardatDDIService, securityService, agencyService, vocabularyService, 
				codeService, PublicationDetailsView.VIEW_NAME);
		this.templateEngine = templateEngine;
		this.agencyService = agencyService;
		this.vocabularyService = vocabularyService;
		this.versionService = versionService;
		this.codeService = codeService;
		this.conceptService = conceptService;
		this.vocabularyChangeService = vocabularyChangeService;
		this.licenceService = licenceService;
	}

	@PostConstruct
	public void init() {
		languageLayout.withFullWidth();

		topSection.add(topViewSection);

		bottomSection.add(bottomViewSection);

		mainContainer
		.withStyleName("margin-left10")
			.add(
				topSection, 
				languageLayout, 
				bottomSection
			);
	}

	
	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		cvItem.clear();
		
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
						try {
							selectedLang = Language.getEnum( selectedLanguage );
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}
					} else {
						selectedLang = null;
					}
					if(  mappedParams.get("tab") != null )
						activeTab = mappedParams.get("tab");
					else
						activeTab = "detail";
				} else {
					selectedLang = null;
					activeTab = "detail";
				}
				LoginView.NAVIGATETO_VIEWNAME = PublicationDetailsView.VIEW_NAME + "/" + itemPathPart[0];
				if( itemPathPart.length > 0 )
					cvItem.setCurrentNotation(itemPathPart[0]);
				if( mappedParams.get("url") != null)
					cvItem.setCurrentCvId( mappedParams.get("url") );
				if(  mappedParams.get("code") != null )
					highlightCode = mappedParams.get("code");

				setDetails() ;

			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

		}
		
	}
	
	
	private void setDetails() {
		// activate home button
		topMenuButtonUpdateActive(0);
				
		refreshCvScheme();

		// update breadcrumb
		breadcrumbs
			.addItem(getAgency().getName(), "agency/" + agency.getName())
			.addItem( vocabulary.getNotation() + " " + currentVersion.getNumber(), null)
			.build();

		updateMessageStrings(UI.getCurrent().getLocale());
		
		topPanel.setVisible( false );
	}

	private void refreshCvScheme() {
		languageLayout.removeAllComponents();
		
		if( cvItem.getCurrentCvId() == null ) {
			if( cvItem.getCurrentNotation() != null ) {
				vocabulary = vocabularyService.getByNotation(cvItem.getCurrentNotation());
				
				if(vocabulary != null ) {
					Optional<VersionDTO> slVer = vocabulary.getLatestSlVersion( true );
					if( slVer.isPresent() ) {
						currentSlVersion = slVer.get();
						currentVersion = currentSlVersion;
					}
				}
			}
		} else {
			currentVersion = versionService.getByUri( cvItem.getCurrentCvId() );
			if( currentVersion != null)
				vocabulary = vocabularyService.findOne( currentVersion.getVocabularyId() );
			else{
				vocabulary = vocabularyService.getByNotation(cvItem.getCurrentNotation());
				vocabulary.getLatestSlVersion(true).ifPresent(v -> currentVersion = v);
			}
			// find correct version by selected language
			if( selectedLang != null && !vocabulary.getSourceLanguage().equals( selectedLang.getLanguage())) {
				vocabulary.getVersionByUriSlAndLangauge( cvItem.getCurrentCvId(), selectedLang.getLanguage())
				.ifPresent( ver -> currentVersion = ver);
			}
			
			if( !currentVersion.getItemType().equals( ItemType.SL.toString()))
				vocabulary.getVersionByUri( currentVersion.getUriSl()).ifPresent( c -> currentSlVersion = c );
			else
				currentSlVersion = currentVersion;
		}
		

		if(  cvItem.getCvScheme() != null ) {
			String owner = cvItem.getCvScheme().getOwnerAgency().get(0).getName();
			if( owner != null && !owner.isEmpty() )
				setAgency( agencyService.findByName( owner));
		}
		else
			agency = agencyService.findByName( getVocabulary().getAgencyName());
		
		if( getAgency() == null)
			setAgency( agencyService.findByName( "CESSDA" ));
		
		// get all available licenses
		licenses = licenceService.findAll();
				
		sourceLanguage = Language.valueOfEnum( vocabulary.getSourceLanguage());
		selectedLang = Language.valueOfEnum( currentVersion.getLanguage());
		
		orderedLanguageVersionMap = versionService.getOrderedLanguageVersionMap(vocabulary.getId());
		langMenu = new LanguageMenu(orderedLanguageVersionMap, currentVersion);
		languageLayout.add(langMenu);
		
		updateDetailContent();
		
	}

	private void initTopViewSection() {
		topViewSection.removeAllComponents();
		
		if( SecurityUtils.isAuthenticated() && !vocabulary.isWithdrawn()) {
			String baseUrl = configService.getServerContextPath() + "/#!" + EditorDetailsView.VIEW_NAME + "/" + vocabulary.getNotation();
			topViewSection.add( new MLabel()
					.withContentMode( ContentMode.HTML)
					.withStyleName("pull-right")
					.withValue(
						" " +
								"<a href='" + baseUrl + "'> View in Editor</a> "
					) );
		}
		
		if( vocabulary.isWithdrawn()) {
			vocabularyIsWithdrawn.removeAllComponents();
			vocabularyIsWithdrawn
				.withWidth("100%")
				.withStyleName( "alert alert-danger" )
				.add(
					new MLabel()
						.withContentMode( ContentMode.HTML)
						.withValue(
							"Warning! This CV \"" + vocabulary.getNotation() + "\" has been withdrawn."
						)
				);
			
			topViewSection.add( vocabularyIsWithdrawn );
		}
		
		vocabulary
		.getLatestVersionByLanguage( sourceLanguage.toString(), null, Status.PUBLISHED.toString())
		.ifPresent( slVersion -> {
			String baseUrl = configService.getServerContextPath() + "/#!" + PublicationDetailsView.VIEW_NAME + "/";
			latestSlVersion = slVersion;
			// warning about newer version
			if( latestSlVersion.getId() > currentVersion.getId()) {
				
				String cvUrl = null;
				try {
					cvUrl = baseUrl +  vocabulary.getNotation() + "?url=" +URLEncoder.encode(latestSlVersion.getUri(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					cvUrl = baseUrl  + vocabulary.getNotation() + "?url=" + latestSlVersion.getUri();
					log.error(e.getMessage(), e);
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
				new MLabel( currentSlVersion.getTitle()).withStyleName("rightPart"));

		MCssLayout description = new MCssLayout();
		description.withFullWidth().add(lDefinition.withWidth("140px").withStyleName("leftPart"),
				new MLabel(currentSlVersion.getDefinition()).withStyleName("rightPart").withContentMode( ContentMode.HTML));

		String notesValue = currentSlVersion.getNotes();
		if( notesValue == null || notesValue.isEmpty() )
			notesValue = vocabulary.getNotes();
		MCssLayout notes = new MCssLayout();
		notes.withFullWidth().add(lNotes.withWidth("140px").withStyleName("leftPart"),
				new MLabel( notesValue ).withStyleName("rightPart").withContentMode( ContentMode.HTML));
		if( notesValue == null || notesValue.isEmpty() )
			notes.setVisible( false );

		MCssLayout code = new MCssLayout();
		code.withFullWidth().add(lCode.withWidth("140px").withStyleName("leftPart"),
				new MLabel(currentVersion.getNotation()).withStyleName("rightPart"));

		MCssLayout titleSmallOl = new MCssLayout();
		titleSmallOl.withFullWidth().add(
				lTitleOl.withWidth("140px").withStyleName("leftPart"),
				new MLabel(currentVersion.getTitle()).withStyleName("rightPart"));

		MCssLayout descriptionOl = new MCssLayout();
		descriptionOl.withFullWidth().add(
				lDefinitionOl.withWidth("140px").withStyleName("leftPart"),
				new MLabel(currentVersion.getDefinition()).withStyleName("rightPart").withContentMode( ContentMode.HTML));

		notesValue = currentVersion.getNotes() == null ? "":currentVersion.getNotes();
		MCssLayout notesOl = new MCssLayout();
		notesOl.withFullWidth().add(lNotesOl.withWidth("140px").withStyleName("leftPart"),
				new MLabel( notesValue ).withStyleName("rightPart").withContentMode( ContentMode.HTML));
		if( notesValue.isEmpty() )
			notesOl.setVisible( false );

		if (selectedLang.toString().equals(configService.getDefaultSourceLanguage())) {
			titleSmallOl.setVisible(false);
			descriptionOl.setVisible(false);
			notesOl.setVisible(false);
		}
		
		versionLabel.withStyleName("rightPart");

		MCssLayout langVersDateLayout = new MCssLayout();
		langVersDateLayout.withFullWidth().add(
				new MCssLayout().withStyleName("col-des-4")
						.add(lLang.withWidth("140px").withStyleName("leftPart"),
								new MLabel(currentVersion.getDetailLanguage()).withStyleName("rightPart")),
				new MCssLayout()
						.withStyleName("col-des-4")
						.add(
								lVersion.withWidth("110px").withStyleName("leftPart"),
								versionLabel
						)
				);
		
		langVersDateLayout.add(
				new MCssLayout()
					.withStyleName("col-des-4")
					.add(
							lDate.withWidth("160px").withStyleName("leftPart"),
							new MLabel(currentVersion.getPublicationDate() == null ? "":currentVersion.getPublicationDate().toString()).withStyleName("rightPart"))
				);

		topViewSection.add(topHead, titleSmall, description, notes, code, titleSmallOl, descriptionOl, notesOl, langVersDateLayout);
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
		
		detailTreeGrid = new TreeGrid<>(ConceptDTO.class);
		detailTreeGrid.addStyleNames("undefined-height");
		detailTreeGrid.removeAllColumns();
		detailTreeGrid.setHeight("800px");
		detailTreeGrid.setWidthUndefined();
		
		updateDetailGrid();
		
		detailTreeGrid.setSelectionMode( SelectionMode.NONE );
		
		detailTreeGrid.addColumn(code -> code.getNotation()  + "("  + code.getCodeId() + ")")
			.setCaption("Code")
			.setExpandRatio(1)
			.setId("code");
	
		detailTreeGrid.addColumn(concept -> concept.getTitle())
			.setCaption(i18n.get("view.detail.cvconcept.column.tl.title", locale, selectedLang.toString() ))
			.setExpandRatio(1)
			.setId("prefLabelTl");

		detailTreeGrid.addColumn(concept -> {
			MLabel definitionLabel = new MLabel( concept.getDefinition())
					.withStyleName( "word-brake-normal" ).withContentMode( ContentMode.HTML );
			definitionLabel.setId( "code-" + concept.getNotation().replace(".", "-"));
			return definitionLabel;
		}, new ComponentRenderer())
			.setCaption(i18n.get("view.detail.cvconcept.column.tl.definition", locale, selectedLang.toString() ))
			.setExpandRatio(3)
			.setId("definitionTl");
		
		detailTreeGrid.setSizeFull();
		
		detailTreeGrid.getColumns().stream().forEach( column -> column.setSortable( false ));
				
		detailLayout.addComponents(detailTreeGrid);
		//detailLayout.setMargin(false);
		//detailLayout.setSpacing(false);
		detailLayout.setSizeFull();
		//detailLayout.setExpandRatio(detailTreeGrid, 1);
		
		versionLayout = new VersionLayout(i18n, locale, eventBus, agency, vocabulary, vocabularyChangeService, configService, conceptService, versionService, true);
		versionContentLayout.add( versionLayout );
		
		identityLayout = new IdentityLayout(i18n, locale, eventBus, agency, currentVersion, versionService, configService, null, true);
		identifyLayout.add( identityLayout );
		
		ddiUsageLayout = new DdiUsageLayout(i18n, locale, eventBus, agency, currentVersion, versionService, true);
		ddiLayout.add(ddiUsageLayout);
		
		licenseLayoutContent = new LicenseLayout(i18n, locale, eventBus, agency, currentVersion, configService, versionService, licenses,  true);
		licenseLayout.add( licenseLayoutContent );
		
		exportLayoutContent = new ExportLayout(i18n, locale, eventBus, cvItem, vocabulary, agency, versionService, codeService, configService, stardatDDIService, licenses, templateEngine, true);
		exportLayout.add(exportLayoutContent);
		exportLayout.setSizeFull();
		
		detailTab.addSelectedTabChangeListener( e -> {
			TabSheet tabsheet = e.getTabSheet();
			 // Find the tab (here we know it's a layout)
	        Layout tab = (Layout) tabsheet.getSelectedTab();
	        
			if( tabsheet.getTab(tab).getId().equals("export")) {
				vocabulary = vocabularyService.findOne(currentVersion.getVocabularyId());
				// get all version put it on the map
				exportLayoutContent.updateGrid(currentVersion, orderedLanguageVersionMap);
			}
			else if (tabsheet.getTab(tab).getId().equals("version")) {
				versionLayout.refreshContent(currentVersion);
			}
			
		});

		bottomViewSection.add(detailTab);
		
		setActiveTab();
		
		executeJavascriptFunction();
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
				"var mainContainer=document.getElementById('main-container'); " +
				"mainContainer.scrollTop = 0;"
			);
		}
	}

	private void setActiveTab() {
		if( activeTab != null) {
			switch( activeTab) {
				case "download":
					vocabulary = vocabularyService.findOne(currentVersion.getVocabularyId());
					// get all version put it on the map
					exportLayoutContent.updateGrid(currentVersion, orderedLanguageVersionMap);
					detailTab.setSelectedTab(5);
					break;
				default:
					detailTab.setSelectedTab(0);
			}
		} else
			detailTab.setSelectedTab(0);
	}
	
	

	@SuppressWarnings("unchecked")
	public void updateDetailGrid() {				
		dataProvider  = (TreeDataProvider<ConceptDTO>) detailTreeGrid.getDataProvider();
		cvCodeTreeData = dataProvider.getTreeData();
		cvCodeTreeData.clear();
		// assign the tree structure
		
		CvCodeTreeUtils.buildConceptTree( vocabulary, currentVersion, cvCodeTreeData);
		// refresh tree
		dataProvider.refreshAll();
		// expand all nodes
		detailTreeGrid.expand( currentVersion.getConcepts() );
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
		lNotes.setValue( "CV notes" );
		lLang.setValue( i18n.get("view.detail.cvscheme.label.language", locale));
		lVersion.setValue( i18n.get("view.detail.cvscheme.label.sl.version", locale));
		lDate.setValue( i18n.get("view.detail.cvscheme.label.sl.publicationdate", locale));
		lTitleOl.setValue( i18n.get("view.detail.cvscheme.label.tl.title", locale, selectedLang));
		lDefinitionOl.setValue( i18n.get("view.detail.cvscheme.label.tl.definition", locale, selectedLang));
		lVersionOl.setValue( i18n.get("view.detail.cvscheme.label.tl.version", locale));
		lDateOl.setValue( i18n.get("view.detail.cvscheme.label.tl.publicationdate", locale));
		lNotesOl.setValue( "CV notes (" + selectedLang + ")" );


		detailTab.getTab(0).setCaption( i18n.get("view.detail.cvconcept.tab.detail", locale));
		detailTab.getTab(1).setCaption( i18n.get("view.detail.cvconcept.tab.version", locale));
		detailTab.getTab(2).setCaption( i18n.get("view.detail.cvconcept.tab.identity", locale));
		detailTab.getTab(3).setCaption( i18n.get("view.detail.cvconcept.tab.ddi", locale));
		detailTab.getTab(4).setCaption( i18n.get("view.detail.cvconcept.tab.license", locale));
		detailTab.getTab(5).setCaption( i18n.get("view.detail.cvconcept.tab.export", locale));
		
		detailTreeGrid.getColumn("code").setCaption( i18n.get("view.detail.cvconcept.column.sl.code", locale) );
//		detailTreeGrid.getColumn("prefLabelSl").setCaption( i18n.get("view.detail.cvconcept.column.sl.title", locale) );
		if( detailTreeGrid.getColumn("prefLabelTl") != null )
			detailTreeGrid.getColumn("prefLabelTl").setCaption( i18n.get("view.detail.cvconcept.column.tl.title", locale, selectedLang) );
//		detailTreeGrid.getColumn("definitionSl").setCaption( i18n.get("view.detail.cvconcept.column.sl.definition", locale) );
		if( detailTreeGrid.getColumn("definitionTl") != null )
			detailTreeGrid.getColumn("definitionTl").setCaption( i18n.get("view.detail.cvconcept.column.tl.definition", locale, selectedLang) );
		
//		actionPanel.updateMessageStrings(locale);
	}
	
	class LanguageMenu extends CustomComponent{
		private static final long serialVersionUID = 1L;
		private Map<String, List<VersionDTO>> versionMaps;
		
		private MCssLayout langLayout = new MCssLayout();
		
		private Long targetVersionId;
		
		public LanguageMenu(Map<String, List<VersionDTO>> versionMap, VersionDTO targetVersion) {
			// filter out versions that not related to current SL version
			this.versionMaps = getFilteredVersionMap(targetVersion, versionMap);
			this.targetVersionId = targetVersion.getId();
			
			langButtons.clear();
			
			for( Map.Entry<String, List<VersionDTO>> entry : versionMaps.entrySet()){
				langLayout.add( generateLanguageMenu( entry.getKey(), entry.getValue()));
			}
			
			setStyleName("align-right");
			setCompositionRoot( langLayout );
		}
		
		private MCssLayout generateLanguageMenu( String langIso, List<VersionDTO> versions) {
			MCssLayout languageMenu = new MCssLayout();
			MCssLayout languageSubMenu = new MCssLayout();
			MButton languageButton = new MButton( langIso.toUpperCase() ).withDescription( versions.get(0).getDetailLanguage());
			langButtons.add(languageButton);
			
			languageButton.withStyleName("langbutton-publish");
			if( langIso.equalsIgnoreCase( sourceLanguage.toString() )) {
				languageButton.addStyleNames( "button-source-language", "font-bold" );
				languageButton.setDescription( "source language" );
			}
			
			if( langIso.equalsIgnoreCase( selectedLang.toString() ))
				languageButton.addStyleName( "button-language-selected" );
			
			languageButton.addClickListener( e-> {
				currentVersion = versions.get(0);

				updateDetailContent();
			});
			
			for(VersionDTO version : versions) {
				MButton versionButton = new MButton( version.getNumber() );
				langSubButtons.put(version,versionButton);
				versionButton.withStyleName( "sub-button-menu" );
				if( version.getId().equals( targetVersionId )) {
					versionButton.withIcon( VaadinIcons.CHECK );
				}
				versionButton.addClickListener( e -> {
					currentVersion = version;
					
					updateDetailContent();
				});
				
				languageSubMenu.add( versionButton );
			}
			
			languageSubMenu
				.withStyleName("language-submenu");
		
			languageMenu
				.withStyleName("language-menu")
				.add( languageButton, languageSubMenu);
			
			return languageMenu;
		}
		
		private Map<String, List<VersionDTO>> getFilteredVersionMap ( VersionDTO pivotVersion , Map<String, List<VersionDTO>> versionMap) {
			Map<String, List<VersionDTO>> filteredVersionMap = new LinkedHashMap<>();
			if( pivotVersion == null ) {
				//get latest SL version
				for(Map.Entry<String, List<VersionDTO>> entry : versionMap.entrySet()) {
					pivotVersion = entry.getValue().get(0);
					//break after first loop since SL version is always in the first entry
					break;
				}
			}
			
			// always get one SL version
			if( pivotVersion.getItemType().equals( ItemType.TL.toString())) {
				if(vocabulary.getVersionByUri( pivotVersion.getUriSl()).isPresent())
					pivotVersion = vocabulary.getVersionByUri( pivotVersion.getUriSl()).get();
			}
			
			for(Map.Entry<String, List<VersionDTO>> entry : versionMap.entrySet()) {
				if( entry.getKey().equals( pivotVersion.getLanguage() ) )
					filteredVersionMap.put( entry.getKey(), Arrays.asList(pivotVersion));
				else {
					// get only SL that within SL version
					List<VersionDTO> oldValue = entry.getValue();
					List<VersionDTO> newValue = new ArrayList<>();
					for(VersionDTO eachVersion: oldValue) {
						if( !eachVersion.getStatus().equals(Status.PUBLISHED.toString())) // only show published one
							continue;
						if( eachVersion.getUriSl() != null && eachVersion.getUriSl().equals(pivotVersion.getUri()))
							newValue.add(eachVersion);
					}
					if( !newValue.isEmpty() ) {
						filteredVersionMap.put( entry.getKey(), newValue);
					}
				}

			}
			
			return filteredVersionMap;
		}
	}
	
	private void updateDetailContent() {
		cvItem.setCurrentLanguage( currentVersion.getLanguage());
		setSelectedLang( Language.getEnum( currentVersion.getLanguage()) );
		
		langButtons.forEach( b -> {
			if( b.getCaption().equalsIgnoreCase( currentVersion.getLanguage()))
				b.addStyleName("button-language-selected");
			else
				b.removeStyleName("button-language-selected");
		});
		
		langSubButtons.forEach( (k, v) -> {
			if( k.equals( currentVersion))
				v.withIcon( VaadinIcons.CHECK );
			else
				v.setIcon(null);
		});
		
		versionLabel.setValue( currentVersion.getNumber() + (selectedLang.equals( sourceLanguage ) ? ""
				: "-" + selectedLang.toString()) );
		
		initTopViewSection();
		initBottomViewSection();
		
		setCode( null );
		updateMessageStrings(locale);
	}
}
