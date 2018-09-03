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

import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.export.utils.SaxParserUtils;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.CVManagerUI;
import eu.cessda.cvmanager.ui.layout.ExportLayout;
import eu.cessda.cvmanager.ui.view.window.DialogAddCodeWindow;
import eu.cessda.cvmanager.ui.view.window.DialogAddCodeWindow2;
import eu.cessda.cvmanager.ui.view.window.DialogEditCodeWindow;
import eu.cessda.cvmanager.ui.view.window.DialogMultipleOption;
import eu.cessda.cvmanager.ui.view.window.DialogTranslateCodeWindow;
import eu.cessda.cvmanager.utils.CvCodeTreeUtils;

@UIScope
@SpringView(name = DetailView2.VIEW_NAME)
public class DetailView2 extends CvManagerView {

	private static final long serialVersionUID = 6904286186508174249L;
	public static final String VIEW_NAME = "detailold";
	private Locale locale = UI.getCurrent().getLocale();
	private final TemplateEngine templateEngine;
	private final AgencyService agencyService;
	private final VocabularyService vocabularyService;
	private final CodeService codeService;
	private final VersionService versionService;
	
	private Language selectedLang = Language.ENGLISH;
	private FormMode formMode;

	private MCssLayout buttonLayout = new MCssLayout();
	private MButton editButton = new MButton("Edit").withStyleName(ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL,
			"pull-right");
	private MButton saveButton = new MButton("Save").withStyleName(ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL,
			"pull-right");
	
	private MButton deleteButton = new MButton("Delete").withStyleName(ValoTheme.BUTTON_DANGER, ValoTheme.BUTTON_SMALL,
			"pull-right");
	private MButton cancelButton = new MButton("Cancel").withStyleName(ValoTheme.BUTTON_SMALL, "pull-right");

	private MCssLayout topSection = new MCssLayout().withFullWidth();
	private MCssLayout topViewSection = new MCssLayout().withFullWidth();
	private MCssLayout topEditSection = new MCssLayout().withFullWidth();
	private MCssLayout bottomSection = new MCssLayout().withFullWidth();
	private MCssLayout bottomViewSection = new MCssLayout().withFullWidth();
	private MCssLayout bottomEditSection = new MCssLayout().withFullWidth();
	
	private MCssLayout detailLayout = new MCssLayout().withFullWidth();
	private MCssLayout identifyLayout = new MCssLayout().withFullWidth();
	private MCssLayout ddiLayout = new MCssLayout().withFullWidth();
	private MCssLayout licenseLayout = new MCssLayout().withFullWidth();
	private MCssLayout exportLayout = new MCssLayout().withFullWidth();

	private TextField codeEditor = new TextField();
	private TextField prefLanguageEditor = new TextField();
	private TextField prefLabelEditor = new TextField();
	private ComboBox<CVEditor> editorCb = new ComboBox<>();
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

	private View oldView;
	
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

	public DetailView2( I18N i18n, EventBus.UIEventBus eventBus, ConfigurationService configService, 
			StardatDDIService stardatDDIService, SecurityService securityService, AgencyService agencyService,
			VocabularyService vocabularyService, VersionService versionService, CodeService codeService, TemplateEngine templateEngine) {
		super(i18n, eventBus, configService, stardatDDIService, securityService, agencyService, vocabularyService, codeService, DetailView2.VIEW_NAME);
		this.templateEngine = templateEngine;
		this.agencyService = agencyService;
		this.vocabularyService = vocabularyService;
		this.codeService = codeService;
		this.versionService = versionService;
		eventBus.subscribe( this, DetailView2.VIEW_NAME );
	}

	@PostConstruct
	public void init() {
		
		cvEditors[0] = new CVEditor("DDI", "DDI");
		cvEditors[0].setLogoPath("img/ddi-logo-r.png");
		cvEditors[1] = new CVEditor("CESSDA", "CESSDA");
		cvEditors[1].setLogoPath("img/cessda.png");
		
		editorCb.setItems(cvEditors);
		editorCb.setItemCaptionGenerator(CVEditor::getName);
		editorCb.setEmptySelectionAllowed( false );
		editorCb .setTextInputAllowed( false );
		
		MButton backToResults = new MButton(FontAwesome.BACKWARD, this::back);
		backToResults.setCaption("Back");
		backToResults.withStyleName(ValoTheme.BUTTON_FRIENDLY, ValoTheme.BUTTON_SMALL, "pull-right", "marginleft20");

		editButton.addClickListener(e -> setFormMode(FormMode.edit));
		cancelButton.addClickListener(e -> setFormMode(FormMode.view));
		saveButton.addClickListener( this::doSaveConcept );
		deleteButton.addClickListener( this::deleteScheme);
		
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
		if( SecurityUtils.isCurrentUserAllowEditCv( agency , selectedLang))
			editButton.setVisible( true );
		else
			editButton.setVisible( false );
		actionPanel.setVisible( true );
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		
		locale = UI.getCurrent().getLocale();
		setOldView(event.getOldView());
		actionPanel.conceptSelectedChange( null );

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
					}
					if(  mappedParams.get("tab") != null )
						activeTab = mappedParams.get("tab");
					else
						activeTab = "detail";
				} else {
					activeTab = "detail";
				}
				LoginView.NAVIGATETO_VIEWNAME = DetailView2.VIEW_NAME + "/" + itemPathPart[0];
				cvItem.setCurrentCvId(itemPathPart[0]);
				if( itemPathPart.length > 1 )
					cvItem.setCurrentConceptId(itemPathPart[1]);
				
				setDetails() ;

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		updateActionPanel();
	}
	
	@Override
	protected void updateActionPanel() {
		actionPanel.setSlRoleActionButtonVisible( SecurityUtils.isCurrentUserAllowCreateCvSl( getAgency()) );
		actionPanel.setTlRoleActionButtonVisible( SecurityUtils.isCurrentUserAllowCreateCvTl( getAgency() ) );
		
		super.updateActionPanel();
	}
	
	private void setDetails() {
		setFormMode(FormMode.view);
		
		refreshCvScheme();
//		refreshCvConcepts();

		initTopViewSection();
		initTopEditSection();
		initBottomViewSection();
		//initBottomEditSection();
		updateMessageStrings(UI.getCurrent().getLocale());
		
		// TODO: Workaround so that the translation label viible ~ not efficient need correct solution
		initTopViewSection();
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
		
		// update breadcrumb
		getBreadcrumbs()
			.addItem(getAgency().getName(), "agency")
			.build();
		
				
		Set<String> languages = cvItem.getCvScheme().getLanguagesByTitle();
		sourceLanguage = Language.valueOfEnum( vocabulary.getSourceLanguage().toString()).toString();
		String clickedLanguage = cvItem.getCurrentLanguage() == null ? sourceLanguage : cvItem.getCurrentLanguage();

		languages.forEach(item -> {
			MButton langButton = new MButton(item.toUpperCase());
			langButton.withStyleName("langbutton").addClickListener(e -> {
				applyButtonStyle(e.getButton());
				
				cvItem.setCurrentLanguage(e.getButton().getCaption().toLowerCase());
				setSelectedLang( Language.getEnum( e.getButton().getCaption().toLowerCase()) );
				actionPanel.languageSelectionChange( configService.getDefaultSourceLanguage(), cvItem.getCurrentLanguage());
				
				setFormMode(FormMode.view);
				
				initTopViewSection();
				initTopEditSection();
				initBottomViewSection();
				
				if( SecurityUtils.isCurrentUserAllowEditCv( agency , selectedLang))
					editButton.setVisible( true );
				else
					editButton.setVisible( false );
				
				actionPanel.conceptSelectedChange( null );
				setCode( null );
				updateMessageStrings(locale);
			});
			languageLayout.add(langButton);
			if( item.equals(sourceLanguage)) {
				langButton.addStyleName("font-bold");
				langButton.setDescription( "source language" );
			}
			if( item.equals(clickedLanguage) ) {
				langButton.click();
			}
		});
	}
	
	private void doSaveConcept() {
		if( selectedLang.equals( Language.valueOfEnum( vocabulary.getSourceLanguage() )  )) {
			List<CVEditor> editorSet = cvItem.getCvScheme().getOwnerAgency();
			editorSet.clear();
			editorSet.add( editorCb.getValue() );
		}
		cvItem.getCvScheme().save();
		DDIStore ddiStore = stardatDDIService.saveElement(cvItem.getCvScheme().ddiStore, SecurityUtils.getCurrentUserLogin().get(), "CV updated");
		
		// update vocabulary on database as well
		vocabularyService.save(vocabulary);
			
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
		titleSmall.withFullWidth().add( lTitle.withWidth("120px").withStyleName("leftPart"),
				new MLabel(cvItem.getCvScheme().getTitleByLanguage( sourceLanguage )).withStyleName("rightPart"));

		MCssLayout description = new MCssLayout();
		description.withFullWidth().add(lDefinition.withWidth("120px").withStyleName("leftPart"),
				new MLabel(cvItem.getCvScheme().getDescriptionByLanguage( sourceLanguage )).withStyleName("rightPart"));

		MCssLayout code = new MCssLayout();
		code.withFullWidth().add(lCode.withWidth("120px").withStyleName("leftPart"),
				new MLabel(cvItem.getCvScheme().getCode()).withStyleName("rightPart"));

		MCssLayout titleSmallOl = new MCssLayout();
		titleSmallOl.withFullWidth().add(
				lTitleOl.withWidth("120px").withStyleName("leftPart"),
				new MLabel(cvItem.getCvScheme().getTitleByLanguage(selectedLang.toString())).withStyleName("rightPart"));

		MCssLayout descriptionOl = new MCssLayout();
		descriptionOl.withFullWidth().add(
				lDefinitionOl.withWidth("120px").withStyleName("leftPart"),
				new MLabel(cvItem.getCvScheme().getDescriptionByLanguage(selectedLang.toString())).withStyleName("rightPart"));

		if (selectedLang.toString().equals(configService.getDefaultSourceLanguage())) {
			titleSmallOl.setVisible(false);
			descriptionOl.setVisible(false);
		}

		MCssLayout langSec = new MCssLayout();
		langSec.withFullWidth().add(
				new MCssLayout().withWidth("33%")
						.add(lLang.withWidth("120px").withStyleName("leftPart"),
								new MLabel(selectedLang.toString()).withStyleName("rightPart")),
				new MCssLayout().withWidth("33%").add(
						lVersion.withWidth("180px").withStyleName("leftPart"),
						new MLabel(cvItem.getCvScheme().getVersion().getPublicationVersion() + (selectedLang.toString().equals( sourceLanguage ) ? ""
								: "-" + selectedLang.toString())).withStyleName("rightPart")),
				new MCssLayout().withWidth("33%").add(
						lDate.withWidth("140px").withStyleName("leftPart"),
						new MLabel(cvItem.getCvScheme().getVersion().getPublicationDate().toString()).withStyleName("rightPart")));

		topViewSection.add(topHead, titleSmall, description, code, titleSmallOl, descriptionOl, langSec);
	}

	private void initTopEditSection() {
		topEditSection.removeAllComponents();

		MLabel topTitle = new MLabel();
		topTitle.withStyleName("topTitle").withContentMode(ContentMode.HTML)
				.withValue(cvItem.getCvScheme().getOwnerAgency().get(0).getName() + " Controlled Vocabulary for "
						+ cvItem.getCvScheme().getTitleByLanguage( sourceLanguage ) + "</strong>");
		
		MLabel topTitleEdit = new MLabel();
		topTitle.withStyleName("topTitle").withContentMode(ContentMode.HTML)
				.withValue( " Controlled Vocabulary for "
						+ cvItem.getCvScheme().getTitleByLanguage( sourceLanguage ) + "</strong>");

		Resource res = new ThemeResource("img/ddi-logo-r.png");
		
		//TODO: remove this workaround
				if( cvItem.getCvScheme().getOwnerAgency().get(0).getName().equals("CESSDA"))
					res = new ThemeResource("img/cessda.png");
				
		for( CVEditor editor : cvEditors) {
			if( editor.getName().equals( cvItem.getCvScheme().getOwnerAgency().get(0).getName() ))
				editorCb.setValue(editor);
		}
		
				
		Image logo = new Image(null, res);
		logo.setWidth("100px");

		MCssLayout topHead = new MCssLayout();
		topHead.withFullWidth().add(logo, topTitle);
		
		editorCb.setStyleName("rightPart");
		MCssLayout topHeadEdit = new MCssLayout();
		topHeadEdit.withFullWidth().add( lAgency.withWidth("120px").withStyleName("leftPart"),
				editorCb);

		MTextField titleField = new MTextField();
		titleField.withStyleName("editField");//.withValue(cvItem.getCvScheme().getTitleByLanguage("en"));

		MCssLayout titleSmall = new MCssLayout();
		titleSmall.withFullWidth().add( lTitle2.withWidth("120px").withStyleName("leftPart"),
				selectedLang.equals( Language.valueOfEnum( vocabulary.getSourceLanguage() )) ? titleField
						: new MLabel(cvItem.getCvScheme().getTitleByLanguage( sourceLanguage )).withStyleName("rightPart"));

		TextArea descField = new TextArea();
		descField.setStyleName("editField");
		//descField.setValue(cvScheme.getDescriptionByLanguage("en"));

		MCssLayout description = new MCssLayout();
		description.withFullWidth().add(lDefinition2.withWidth("120px").withStyleName("leftPart"),
				selectedLang.equals(Language.valueOfEnum( vocabulary.getSourceLanguage() )) ? descField
						: new MLabel(cvItem.getCvScheme().getDescriptionByLanguage( sourceLanguage )).withStyleName("rightPart"));

		MTextField codeField = new MTextField();
		codeField.withStyleName("editField");
		//.withValue("CODE_TEST");

		MCssLayout code = new MCssLayout();
		code.withFullWidth().add(lCode2.withWidth("120px").withStyleName("leftPart"),
				selectedLang.equals(Language.valueOfEnum( vocabulary.getSourceLanguage() )) ? codeField : new MLabel(cvItem.getCvScheme().getCode()).withStyleName("rightPart"));
		
		
		cvSchemeBinder.setBean(cvItem.getCvScheme());
		
		// Binder original language
		cvSchemeBinder.bind( titleField, 
				cScheme -> cScheme.getTitleByLanguage( sourceLanguage ),
				(cScheme, value) -> cScheme.setTitleByLanguage(  sourceLanguage , value) );
		
		cvSchemeBinder.bind( descField, 
				cScheme -> cScheme.getDescriptionByLanguage( sourceLanguage ),
				(cScheme, value) -> cScheme.setDescriptionByLanguage( sourceLanguage , value) );

		cvSchemeBinder.bind( codeField, 
				cScheme -> cScheme.getCode(),
				(cScheme, value) -> cScheme.setCode( value) );

		MCssLayout langSec = new MCssLayout();
		langSec.withFullWidth()
				.add(new MCssLayout().withWidth("33%").add(
						lLang2.withWidth("120px").withStyleName("leftPart"),
						new MLabel( sourceLanguage ).withStyleName("rightPart")),
						new MCssLayout().withWidth("33%").add(
								lVersion2.withWidth("180px").withStyleName("leftPart"),
								new MLabel(cvItem.getCvScheme().getVersion().getPublicationVersion()).withStyleName("rightPart")),
						new MCssLayout().withWidth("33%").add(
								lDate2.withWidth("140px").withStyleName("leftPart"),
								new MLabel(cvItem.getCvScheme().getVersion().getPublicationDate().toString())
										.withStyleName("rightPart")));

		MTextField titleFieldOl = new MTextField();
		titleFieldOl.withStyleName("editField").withValue(cvItem.getCvScheme().getTitleByLanguage(selectedLang.toString()));

		MCssLayout titleSmallOl = new MCssLayout();
		titleSmallOl.withFullWidth().add(
				lTitleOl2.withWidth("120px").withStyleName("leftPart"),
				titleFieldOl);

		TextArea descFieldOl = new TextArea();
		descFieldOl.setStyleName("editField");
		descFieldOl.setValue(cvItem.getCvScheme().getDescriptionByLanguage(selectedLang.toString()));

		MCssLayout descriptionOl = new MCssLayout();
		descriptionOl.withFullWidth().add(
				lDefinitionOl2.withWidth("120px").withStyleName("leftPart"),
				descFieldOl);
		
		// Binder other language
		cvSchemeBinder.bind( titleFieldOl, 
				cScheme -> cScheme.getTitleByLanguage( getSelectedLang().toString()),
				(cScheme, value) -> cScheme.setTitleByLanguage( getSelectedLang().toString() , value) );
		
		cvSchemeBinder.bind( descFieldOl, 
				cScheme -> cScheme.getDescriptionByLanguage( getSelectedLang().toString() ),
				(cScheme, value) -> cScheme.setDescriptionByLanguage( getSelectedLang().toString() , value) );

		MCssLayout langSecOl = new MCssLayout();
		langSecOl.withFullWidth().add(
				new MCssLayout().withWidth("33%")
						.add(lLang3.withWidth("120px").withStyleName("leftPart"),
								new MLabel(selectedLang.toString()).withStyleName("rightPart")),
				new MCssLayout().withWidth("33%").add(
						lVersion3.withWidth("180px").withStyleName("leftPart"),
						new MLabel(cvItem.getCvScheme().getVersion().getPublicationVersion() + (selectedLang.equals( Language.valueOfEnum( vocabulary.getSourceLanguage() )) ? ""
								: "-" + selectedLang.toString())).withStyleName("rightPart")),
				new MCssLayout().withWidth("33%").add(
						lDate3.withWidth("140px").withStyleName("leftPart"),
						new MLabel(cvItem.getCvScheme().getVersion().getPublicationDate().toString()).withStyleName("rightPart")));

		if (selectedLang.equals( Language.valueOfEnum( vocabulary.getSourceLanguage() ))) {
			topHead.setVisible( false );
			topHeadEdit.setVisible( true );
			titleSmallOl.setVisible(false);
			descriptionOl.setVisible(false);
			langSecOl.setVisible(false);
			langSec.setVisible(true);
		} else {
			topHead.setVisible( true );
			topHeadEdit.setVisible( false );
			titleSmallOl.setVisible(true);
			descriptionOl.setVisible(true);
			langSecOl.setVisible(true);
			langSec.setVisible(false);
		}

		topEditSection.add(topHead, topHeadEdit, titleSmall, description, code, langSec, 
				titleSmallOl, descriptionOl, langSecOl, deleteButton);
	}
	
	public void deleteScheme( ClickEvent event) {
		ConfirmDialog.show( this.getUI(), "Confirm",
				"Are you sure you want to delete the vocabulary \"" + cvItem.getCvScheme().getTitleByLanguage( selectedLang.toString() ) + "\"?", "yes",
				"cancel",
		
					dialog -> {
						if( dialog.isConfirmed() ) {
							// delete from stardat ddiflatdb
							stardatDDIService.deleteScheme( cvItem.getCvScheme());
							// delete from database and index
							if( vocabulary.isPersisted() )
								vocabularyService.delete( vocabulary );
							// navigate to search view
							UI.getCurrent().getNavigator().navigateTo(SearchView.VIEW_NAME);
						}
					}
				);
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

		if( !selectedLang.equals( Language.valueOfEnum( vocabulary.getSourceLanguage() ) ))
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
		
		if( !selectedLang.equals( Language.valueOfEnum( vocabulary.getSourceLanguage() ) ))
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
					
//					Window window = new DialogEditCodeWindow(eventBus, stardatDDIService, codeService, cvItem.getCvScheme(), cvItem.getCvConcept(), selectedLang, vocabulary, code, i18n, locale);
//					getUI().addWindow(window);
				} else {
					Notification.show( "you are not allowed to edit this code" );
				}
			}
		});
		
		detailTreeGrid.asSingleSelect().addValueChangeListener( event -> {		
			if (event.getValue() != null) {
				cvItem.setCvConcept( event.getValue() );
				actionPanel.conceptSelectedChange( cvItem.getCvConcept() );
				
				// get code
				code = codeService.getByUri( cvItem.getCvConcept().getContainerId());
				if( code == null )
					code = CodeDTO.generateFromCVConcept( cvItem.getCvConcept() );
				
            } else {
            	cvItem.setCvConcept(  null );
            }
		});
		
		if(enableTreeDragAndDrop && actionPanel.isEnableSort())
			enableTreeGridDragAndDropSort();
		
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
//		
//		exportLayoutContent = new ExportLayout(i18n, locale, eventBus, cvItem, vocabulary, agency, versionService, configService, templateEngine);
//		exportLayout.add(exportLayoutContent);

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

	public Language getSelectedLang() {
		return selectedLang;
	}

	public void setSelectedLang(Language selectedLang) {
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
			case CVCONCEPT_CREATED:
				updateDetailGrid();
				
				break;
			case CVCONCEPT_ADD_DIALOG:
				CVConcept newCVConcept = new CVConcept();
				newCVConcept.loadSkeleton(newCVConcept.getDefaultDialect());
				newCVConcept.createId();
				newCVConcept.setContainerId( cvItem.getCvScheme().getContainerId());

				DialogAddCodeWindow2 dialogAddCodeWindow1 = new DialogAddCodeWindow2(eventBus, stardatDDIService, vocabularyService, codeService, cvItem.getCvScheme(), newCVConcept, null, getVocabulary(), getAgency(), i18n, UI.getCurrent().getLocale());
				getUI().addWindow(dialogAddCodeWindow1);
				
				break;
			case CVCONCEPT_TRANSLATION_DIALOG:
//				if( cvCodeTreeData == null || cvCodeTreeData.getRootItems().isEmpty()) {
//					Notification.show("Please add code first");
//				} else if( cvItem.getCvScheme().getLanguagesByTitle().size() == 1) {
//					Notification.show("Please add CV translation first");
//				}
//				else {
//					Window windowTranslate = new DialogTranslateCodeWindow(eventBus, stardatDDIService, vocabularyService, codeService, cvItem.getCvScheme(), cvItem.getCvConcept(), getVocabulary(), getAgency(), code, i18n, UI.getCurrent().getLocale());
//					getUI().addWindow( windowTranslate );
//				}
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
							actionPanel.conceptSelectedChange( null );
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
		
		actionPanel.updateMessageStrings(locale);
	}
}
