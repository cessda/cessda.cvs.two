package eu.cessda.cvmanager.ui.view;

import java.nio.charset.Charset;
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
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.export.utils.SaxParserUtils;
import eu.cessda.cvmanager.repository.search.VocabularySearchRepository;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.mapper.VocabularyMapper;
import eu.cessda.cvmanager.ui.CVManagerUI;
import eu.cessda.cvmanager.ui.layout.EditorCodeActionLayout;
import eu.cessda.cvmanager.ui.layout.EditorCvActionLayout;
import eu.cessda.cvmanager.ui.layout.ExportLayout;
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
	
	private Language selectedLang = Language.ENGLISH;

	private MCssLayout topSection = new MCssLayout().withFullWidth();
	private MCssLayout topViewSection = new MCssLayout().withFullWidth();
//	private MCssLayout topEditSection = new MCssLayout().withFullWidth();
	private MCssLayout bottomSection = new MCssLayout().withFullWidth();
	private MCssLayout bottomViewSection = new MCssLayout().withFullWidth();
//	private MCssLayout bottomEditSection = new MCssLayout().withFullWidth();
	
	private MCssLayout detailLayout = new MCssLayout().withFullWidth();
	private MCssLayout identifyLayout = new MCssLayout().withFullWidth();
	private MCssLayout ddiLayout = new MCssLayout().withFullWidth();
	private MCssLayout licenseLayout = new MCssLayout().withFullWidth();
	private MCssLayout exportLayout = new MCssLayout().withFullWidth();

	private TextField codeEditor = new TextField();
	private TextField prefLanguageEditor = new TextField();
	private TextField prefLabelEditor = new TextField();
//	private ComboBox<CVEditor> editorCb = new ComboBox<>();
	private static final CVEditor[] cvEditors = new CVEditor[2];
	
	private TabSheet detailTab = new TabSheet();
	
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
	
	private boolean enableTreeDragAndDrop;
	
	private VersionDTO currentVersion;
	private MLabel versionLabel = new MLabel();

//	private View oldView;
	
	private TreeGrid<CVConcept> detailTreeGrid = new TreeGrid<>(CVConcept.class);
	private TreeGridDragSource<CVConcept> dragSource;
	private TreeGridDropTarget<CVConcept> dropTarget;

	private TreeData<CVConcept> cvCodeTreeData;
	private MCssLayout languageLayout = new MCssLayout();
	private List<CVConcept> draggedItems;
	private TreeDataProvider<CVConcept> dataProvider;
	
	private ExportLayout exportLayoutContent;
	private Binder<CVScheme> cvSchemeBinder = new Binder<>();
	private String sourceLanguage;
	private String activeTab;
	
	private EditorCvActionLayout editorCvActionLayout;
	private EditorCodeActionLayout editorCodeActionLayout;

	public DetailView(I18N i18n, EventBus.UIEventBus eventBus, ConfigurationService configService,
			StardatDDIService stardatDDIService, SecurityService securityService, AgencyService agencyService,
			VocabularyService vocabularyService, VocabularyMapper vocabularyMapper, 
			VersionService versionService, CodeService codeService, VocabularySearchRepository vocabularySearchRepository,
			TemplateEngine templateEngine) {
		super(i18n, eventBus, configService, stardatDDIService, securityService, agencyService, vocabularyService, vocabularyMapper, 
				codeService, vocabularySearchRepository, DetailView.VIEW_NAME);
		this.templateEngine = templateEngine;
		this.agencyService = agencyService;
		this.vocabularyService = vocabularyService;
		this.versionService = versionService;
		this.codeService = codeService;
		eventBus.subscribe( this, DetailView.VIEW_NAME );
	}

	@PostConstruct
	public void init() {
		
		editorCvActionLayout = new EditorCvActionLayout("block.action.cv", "block.action.cv.show", i18n, 
				stardatDDIService, agencyService, vocabularyService, vocabularyMapper, 
				vocabularySearchRepository, eventBus);
		
		editorCodeActionLayout = new EditorCodeActionLayout("block.action.code", "block.action.code.show", i18n,
				stardatDDIService, agencyService, vocabularyService, versionService, codeService, eventBus);
		

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
//				buttonLayout, 
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
//		setOldView(event.getOldView());
//		actionPanel.conceptSelectedChange( null );

		if (event.getParameters() != null) {
			try {
				String[] itemPath = event.getParameters().split("\\?");
				String[] itemPathPart = itemPath[0].split("/");
				if(itemPath.length > 1) {
					List<NameValuePair> params = URLEncodedUtils.parse( itemPath[1],  Charset.forName("UTF-8"));
					Map<String, String> mappedParams = params.stream().collect(
					        Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
					String selectedLanguage = mappedParams.get("lang");
					if( selectedLanguage != null ) {
						cvItem.setCurrentLanguage( mappedParams.get("lang") );
						selectedLang = Language.getEnum( selectedLanguage );
						
						// set selected language and version
						editorCvActionLayout.setSelectedLanguage(selectedLang);
					}
					if(  mappedParams.get("tab") != null )
						activeTab = mappedParams.get("tab");
					else
						activeTab = "detail";
				} else {
					activeTab = "detail";
				}
				LoginView.NAVIGATETO_VIEWNAME = DetailView.VIEW_NAME + "/" + itemPathPart[0];
				cvItem.setCurrentCvId(itemPathPart[0]);
				if( itemPathPart.length > 1 )
					cvItem.setCurrentConceptId(itemPathPart[1]);
				
				setDetails() ;

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
//		updateActionPanel();
	}
	
//	@Override
//	protected void updateActionPanel() {
//		actionPanel.setSlRoleActionButtonVisible( SecurityUtils.isCurrentUserAllowCreateCvSl( getAgency()) );
//		actionPanel.setTlRoleActionButtonVisible( SecurityUtils.isCurrentUserAllowCreateCvTl( getAgency() ) );
//		
//		super.updateActionPanel();
//	}
	
	private void setDetails() {
//		setFormMode(FormMode.view);
		
		refreshCvScheme();
//		refreshCvConcepts();
		
		// update breadcrumb
		breadcrumbs
			.addItem(getAgency().getName(), "agency")
			.addItem( vocabulary.getNotation() + " " + vocabulary.getVersionNumber() + " (" + vocabulary.getStatus() + ")", null)
			.build();

		initTopViewSection();
//		initTopEditSection();
		initBottomViewSection();
		//initBottomEditSection();
		updateMessageStrings(UI.getCurrent().getLocale());
		
		// TODO: Workaround so that the translation label visible ~ not efficient need correct solution
		initTopViewSection();
		
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
		
		List<DDIStore> ddiSchemes = stardatDDIService.findByIdAndElementType(cvItem.getCurrentCvId(), DDIElement.CVSCHEME);
		
		// find in Vocabulary entity as well
		setVocabulary(  vocabularyService.getByUri( cvItem.getCurrentCvId() ) );
		
		if (ddiSchemes != null && !ddiSchemes.isEmpty()) {
			cvItem.setCvScheme( new CVScheme(ddiSchemes.get(0)) );
		}
		
		//TODO, remove this "if" after all vocabularies belong to agency
		if( getVocabulary() == null ) {
			
			List<CVEditor> owners = cvItem.getCvScheme().getEditor();
			if( owners != null && !owners.isEmpty() )
				setAgency( agencyService.findByName( owners.get( 0 ).getName()));
			
			if( getAgency() == null)
				setAgency( agencyService.findOne(1L) );
			
			setVocabulary( VocabularyDTO.generateFromCVScheme( cvItem.getCvScheme()) );
			
			getVocabulary().setAgencyId( getAgency().getId());
			getVocabulary().setAgencyName( getAgency().getName());
		} else {
			setAgency( agencyService.findByName( getVocabulary().getAgencyName()));
		}
		
		Set<String> languages = cvItem.getCvScheme().getLanguagesByTitle();
		
		sourceLanguage = Language.getEnumByName( vocabulary.getSourceLanguage().toString()).toString();
		
		editorCvActionLayout.setSourceLanguage( Language.getEnumByName( vocabulary.getSourceLanguage()));
		editorCvActionLayout.setCvScheme( cvItem.getCvScheme() );
		editorCvActionLayout.setAgency( agency );
		editorCvActionLayout.setVocabulary( vocabulary );
		
		
		editorCodeActionLayout.setCvScheme( cvItem.getCvScheme() );
		editorCodeActionLayout.setVocabulary(vocabulary);
		
		String clickedLanguage = cvItem.getCurrentLanguage() == null ? sourceLanguage : cvItem.getCurrentLanguage();

		languages.forEach(item -> {
			
			MButton langButton = new MButton(item.toUpperCase());
			langButton.withStyleName("langbutton");
			
			if( item.equalsIgnoreCase( sourceLanguage.toString() ))
				langButton.addStyleName( "button-source-language" );
			
			if( item.equalsIgnoreCase( selectedLang.toString() ))
				langButton.addStyleName( "button-language-selected" );
			
			// determine the status
			vocabulary.getLatestVersionByLanguage( Language.getEnum(item).name().toLowerCase())
			.ifPresent( versionDTO -> {
				if( versionDTO.getStatus().equals( Status.DRAFT.toString())) {
					// TODO: check detail for editor or publication page
					if( !breadcrumbItemMap.isEmpty() && breadcrumbItemMap.get("editor-search") != null )
						langButton.addStyleName( "status-draft" );
					else
						langButton.setVisible( false );
				}
				else if( versionDTO.getStatus().equals( Status.REVIEW.toString())) {
					if( !breadcrumbItemMap.isEmpty() && breadcrumbItemMap.get("editor-search") != null )
						langButton.addStyleName( "status-review" );
					else
						langButton.setVisible( false );
				}
			});
			
			langButton.addClickListener(e -> {
				applyButtonStyle(e.getButton());
				
				cvItem.setCurrentLanguage(e.getButton().getCaption().toLowerCase());
				setSelectedLang( Language.getEnum( e.getButton().getCaption().toLowerCase()) );
//				actionPanel.languageSelectionChange( configService.getDefaultSourceLanguage(), cvItem.getCurrentLanguage());
				
//				setFormMode(FormMode.view);
				
				initTopViewSection();
//				initTopEditSection();
				initBottomViewSection();
				
				editorCvActionLayout.setSelectedLanguage(selectedLang);
				
				currentVersion = null;
				editorCvActionLayout.setCurrentVersion( null );
				editorCodeActionLayout.setCurrentVersion( null );
				vocabulary.getLatestVersionByLanguage(selectedLang)
					.ifPresent( v -> {
						editorCvActionLayout.setCurrentVersion(v);
						editorCodeActionLayout.setCurrentVersion(v);
						currentVersion = v;
						
						versionLabel.setValue( currentVersion.getNumber() + (selectedLang.toString().equals( sourceLanguage ) ? ""
								: "-" + selectedLang.toString())  + 
								( currentVersion.getStatus().equals( Status.PUBLISHED.toString() ) ? "":" (" + currentVersion.getStatus() + ")"));
					});
				
				
				
				
//				if( SecurityUtils.isCurrentUserAllowEditCv( agency , selectedLang))
//					editButton.setVisible( true );
//				else
//					editButton.setVisible( false );
				
//				actionPanel.conceptSelectedChange( null );
				setCode( null );
				updateMessageStrings(locale);
				refreshCvActionButton();
				refreshCodeActionButton();
			});
			languageLayout.add(langButton);
			if( item.equals(sourceLanguage)) {
				langButton.addStyleName("font-bold");
				langButton.setDescription( "source language" );
			}
			if( item.equals(clickedLanguage) ) {
				editorCvActionLayout.setSelectedLanguage( Language.getEnum( item) );
				langButton.click();
			}
		});
	}

	private void initTopViewSection() {
		topViewSection.removeAllComponents();

		MLabel topTitle = new MLabel();
		topTitle.withStyleName("topTitle").withContentMode(ContentMode.HTML)
				.withValue(cvItem.getCvScheme().getOwnerAgency().get(0).getName() + " Controlled Vocabulary for "
						+ cvItem.getCvScheme().getTitleByLanguage( sourceLanguage ) + "</strong>");

		Resource res = new ThemeResource("img/ddi-logo-r.png");
		
		//TODO: remove this workaround
		if( cvItem.getCvScheme().getOwnerAgency().get(0).getName().equals("CESSDA"))
			res = new ThemeResource("img/cessda.png");
		
		Image logo = new Image(null, res);
		logo.setWidth("100px");

		MCssLayout topHead = new MCssLayout();
		topHead.withFullWidth().add(logo, topTitle);

		MCssLayout titleSmall = new MCssLayout();
		titleSmall.withFullWidth().add( lTitle.withWidth("140px").withStyleName("leftPart"),
				new MLabel(cvItem.getCvScheme().getTitleByLanguage( sourceLanguage )).withStyleName("rightPart"));

		MCssLayout description = new MCssLayout();
		description.withFullWidth().add(lDefinition.withWidth("140px").withStyleName("leftPart"),
				new MLabel(cvItem.getCvScheme().getDescriptionByLanguage( sourceLanguage )).withStyleName("rightPart"));

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
		
		if( vocabulary.getStatus().equals( Status.PUBLISHED.toString()))
			langVersDateLayout.add(
					new MCssLayout()
						.withStyleName("col-des-4")
						.add(
								lDate.withWidth("140px").withStyleName("leftPart"),
								new MLabel(cvItem.getCvScheme().getVersion().getPublicationDate().toString()).withStyleName("rightPart"))
					);

		topViewSection.add(topHead, titleSmall, description, code, titleSmallOl, descriptionOl, langVersDateLayout);
	}

	private void initBottomViewSection() {
		bottomViewSection.removeAllComponents();
		detailLayout.removeAllComponents();
		exportLayout.removeAllComponents();
		
		exportLayout.withHeight("450px");
		detailLayout.setHeight("800px");

		detailTab = new TabSheet();
		detailTab.setStyleName("detail-tab");
		detailTab.setHeightUndefined();
		detailTab.setWidth("100%");
	
		detailTab.addTab(detailLayout, i18n.get("view.detail.cvconcept.tab.detail", locale));
		detailTab.addTab(identifyLayout, i18n.get("view.detail.cvconcept.tab.identity", locale));
		detailTab.addTab(ddiLayout, i18n.get("view.detail.cvconcept.tab.ddi", locale));
		detailTab.addTab(licenseLayout, i18n.get("view.detail.cvconcept.tab.license", locale));
		detailTab.addTab(exportLayout, i18n.get("view.detail.cvconcept.tab.export", locale));
		
		setActiveTab();
		
		detailTreeGrid = new TreeGrid<>(CVConcept.class);
		detailTreeGrid.addStyleNames("undefined-height");
		detailTreeGrid.removeAllColumns();
		detailTreeGrid.setHeight("800px");
		detailTreeGrid.setWidthUndefined();
		
		updateDetailGrid();	
		
		detailTreeGrid.setSelectionMode( SelectionMode.SINGLE );
		
		detailTreeGrid.addColumn(concept -> concept.getNotation())
			.setCaption("Code")
			.setEditorComponent(codeEditor, (concept, value) -> concept.setNotation(value))
			.setExpandRatio(1)
			.setId("code");
	
		detailTreeGrid.addColumn(concept -> concept.getPrefLabelByLanguage( sourceLanguage ))
			.setCaption(i18n.get("view.detail.cvconcept.column.sl.title", locale))
			.setEditorComponent(prefLabelEditor, (concept, value) -> concept.setPrefLabelByLanguage( sourceLanguage , value))
			.setExpandRatio(1)
			.setId("prefLabelSl");

		if( !selectedLang.equals( Language.getEnumByName( vocabulary.getSourceLanguage() ) ))
			detailTreeGrid.addColumn(concept -> concept.getPrefLabelByLanguage(selectedLang.toString()))
				.setCaption(i18n.get("view.detail.cvconcept.column.tl.title", locale, selectedLang.toString() ))
				//.setEditorBinding(prefLabelBinding)
				.setEditorComponent(prefLanguageEditor, (concept, value) -> concept.setPrefLabelByLanguage( selectedLang.toString(), value))
				.setExpandRatio(1)
				.setId("prefLabelTl");// Component(prefLanguageEditor,
		
		detailTreeGrid.addColumn(concept -> {
					return new MLabel( concept.getDescriptionByLanguage( sourceLanguage )).withStyleName( "word-brake-normal" );
				}, new ComponentRenderer())
				.setCaption(i18n.get("view.detail.cvconcept.column.sl.definition", locale))
				.setExpandRatio(3)
				.setId("definitionSl");
		
		if( !selectedLang.equals( Language.getEnumByName( vocabulary.getSourceLanguage() ) ))
			detailTreeGrid.addColumn(concept -> {
				return new MLabel( concept.getDescriptionByLanguage(selectedLang.toString())).withStyleName( "word-brake-normal" );
			}, new ComponentRenderer())
			.setCaption(i18n.get("view.detail.cvconcept.column.tl.definition", locale, selectedLang.toString() ))
			.setExpandRatio(3)
			.setId("definitionTl");
		
		detailTreeGrid.setSizeFull();
		detailTreeGrid.addItemClickListener( event -> {
			if( SecurityContextHolder.getContext().getAuthentication() != null && event.getMouseEventDetails().isDoubleClick() ) {
				if(  SecurityUtils.isCurrentUserAllowEditCv( agency , selectedLang) ) {
					
					detailTreeGrid.asSingleSelect().clear();
					
					cvItem.setCvConcept( event.getItem() );

					// get code
					code = codeService.getByUri( cvItem.getCvConcept().getContainerId());
					if( code == null )
						code = CodeDTO.generateFromCVConcept( cvItem.getCvConcept() );
					
					Window window = new DialogEditCodeWindow(eventBus, stardatDDIService, codeService, cvItem.getCvScheme(), cvItem.getCvConcept(), selectedLang, vocabulary, code, i18n, locale);
					getUI().addWindow(window);
				} else {
					Notification.show( "you are not allowed to edit this code" );
				}
			}
		});
		
		detailTreeGrid.asSingleSelect().addValueChangeListener( event -> {		
			if (event.getValue() != null) {
				cvItem.setCvConcept( event.getValue() );
//				actionPanel.conceptSelectedChange( cvItem.getCvConcept() );
				
				// get code
				code = codeService.getByUri( cvItem.getCvConcept().getContainerId());
				if( code == null )
					code = CodeDTO.generateFromCVConcept( cvItem.getCvConcept() );
				
            } else {
            	cvItem.setCvConcept(  null );
            }
		});
		
//		if(enableTreeDragAndDrop && actionPanel.isEnableSort())
//			enableTreeGridDragAndDropSort();
		
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
		
		exportLayoutContent = new ExportLayout(i18n, locale, eventBus, cvItem, configService, templateEngine);
		exportLayout.add(exportLayoutContent);

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
	                	
	                	getUI().addWindow( new DialogMultipleOption("Code move options", "Move the code <strong>\"" + (draggedRow.getNotation() == null ? draggedRow.getPrefLabelByLanguage( sourceLanguage ): draggedRow.getNotation()) + "\"</strong> as a next sibling or as a child of <strong>\"" + 
        						(targetRow.getNotation() == null ? targetRow.getPrefLabelByLanguage( sourceLanguage ): targetRow.getNotation())+ "\"</strong>?", optionButtons, 
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
	                						stardatDDIService.storeTopConcept(cvItem.getCvScheme(), cvCodeTreeData.getRootItems());
	                					} else { // reorder narrower
	                						stardatDDIService.storeNarrowerConcept( targetNodeParent, cvCodeTreeData.getChildren( targetNodeParent ));
	                					}
	                					  
	                					// dragged node not from topConcepts, need to reorder the narrower list
	                					// from previous parent
	                					if( !parentSame ) {
		                					if( draggedNodeParent != null ) {
		                						stardatDDIService.storeNarrowerConcept( draggedNodeParent, cvCodeTreeData.getChildren( draggedNodeParent ));
		                					} else {
		                						stardatDDIService.storeTopConcept(cvItem.getCvScheme(), cvCodeTreeData.getRootItems());
		                					}
	                					}
	                				} 
	                				else if (selectedOptionNumber == 1) { //move as child
	                					// Possibility
	                					// as topconcept to child from root/leaf concept
	                					// as child child to  child from root/leaf concept (only concept narrower affected)
            							cvCodeTreeData.setParent(draggedRow, targetRow);
            							dataProvider.refreshAll();
            							detailTreeGrid.expand(draggedRow, targetRow);
            							
            							// update topconcept, if dragged top concept is null
            							if( draggedNodeParent == null ) { // dragged node was topconcept
            								stardatDDIService.storeTopConcept(cvItem.getCvScheme(), cvCodeTreeData.getRootItems());
            							} else {
            								stardatDDIService.storeNarrowerConcept( draggedNodeParent, cvCodeTreeData.getChildren( draggedNodeParent ));
            							}
            							// update new parent child order
            							stardatDDIService.storeNarrowerConcept( targetRow, cvCodeTreeData.getChildren( targetRow ));
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
		List<DDIStore> ddiConcepts = stardatDDIService.findByIdAndElementType(cvItem.getCvScheme().getContainerId(), DDIElement.CVCONCEPT);
		CvCodeTreeUtils.buildCvConceptTree(ddiConcepts, cvItem.getCvScheme(), cvCodeTreeData);
		
		cvItem.setCvCodeTreeData(cvCodeTreeData);
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
				updateDetailGrid();
				
				break;
//			case CVCONCEPT_ADD_DIALOG:
//				CVConcept newCVConcept = new CVConcept();
//				newCVConcept.loadSkeleton(newCVConcept.getDefaultDialect());
//				newCVConcept.createId();
//				newCVConcept.setContainerId( cvItem.getCvScheme().getContainerId());
//
//				DialogAddCodeWindow2 dialogAddCodeWindow1 = new DialogAddCodeWindow2(eventBus, stardatDDIService, vocabularyService, codeService, cvItem.getCvScheme(), newCVConcept, null, getVocabulary(), getAgency(), i18n, UI.getCurrent().getLocale());
//				getUI().addWindow(dialogAddCodeWindow1);
//				
//				break;
			case CVCONCEPT_TRANSLATION_DIALOG:
				if( cvCodeTreeData == null || cvCodeTreeData.getRootItems().isEmpty()) {
					Notification.show("Please add code first");
				} else if( cvItem.getCvScheme().getLanguagesByTitle().size() == 1) {
					Notification.show("Please add CV translation first");
				}
				else {
					Window windowTranslate = new DialogTranslateCodeWindow(eventBus, stardatDDIService, vocabularyService, codeService, cvItem.getCvScheme(), cvItem.getCvConcept(), getVocabulary(), getAgency(), code, i18n, UI.getCurrent().getLocale());
					getUI().addWindow( windowTranslate );
				}
				break;
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
				"Are you sure you want to delete the concept \"" + cvItem.getCvConcept().getPrefLabelByLanguage( configService.getDefaultSourceLanguage() ) + "\"?", "yes",
				"cancel",
		
					dialog -> {
						if( dialog.isConfirmed() ) {
							stardatDDIService.deleteConceptTree(cvCodeTreeData, cvItem.getCvConcept());
							cvCodeTreeData.removeItem( cvItem.getCvConcept() );
							
							if( code.isPersisted())
								codeService.delete( code );
							
							detailTreeGrid.getDataProvider().refreshAll();
//							actionPanel.conceptSelectedChange( null );
							setCode( null );
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
		detailTab.getTab(1).setCaption( i18n.get("view.detail.cvconcept.tab.identity", locale));
		detailTab.getTab(2).setCaption( i18n.get("view.detail.cvconcept.tab.ddi", locale));
		detailTab.getTab(3).setCaption( i18n.get("view.detail.cvconcept.tab.license", locale));
		detailTab.getTab(4).setCaption( i18n.get("view.detail.cvconcept.tab.export", locale));
		
		detailTreeGrid.getColumn("code").setCaption( "Code" );
		detailTreeGrid.getColumn("prefLabelSl").setCaption( i18n.get("view.detail.cvconcept.column.sl.title", locale) );
		if( detailTreeGrid.getColumn("prefLabelTl") != null )
			detailTreeGrid.getColumn("prefLabelTl").setCaption( i18n.get("view.detail.cvconcept.column.tl.title", locale, selectedLang) );
		detailTreeGrid.getColumn("definitionSl").setCaption( i18n.get("view.detail.cvconcept.column.sl.definition", locale) );
		if( detailTreeGrid.getColumn("definitionTl") != null )
			detailTreeGrid.getColumn("definitionTl").setCaption( i18n.get("view.detail.cvconcept.column.tl.definition", locale, selectedLang) );
		
//		actionPanel.updateMessageStrings(locale);
	}
}
