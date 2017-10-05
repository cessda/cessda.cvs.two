package eu.cessda.cvmanager.ui.view;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.vaadin.spring.events.EventBus;
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
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@UIScope
@SpringView(name = DetailView.VIEW_NAME)
public class DetailView extends MVerticalLayout implements View {

	private static final long serialVersionUID = 6904286186508174249L;
	public static final String VIEW_NAME = "Detail";

	private final EventBus.UIEventBus eventBus;

	private final String ITEM_TITLE = "Title";
	private final String ITEM_DEF = "Definition";
	private final String ITEM_CODE = "Code";
	private final String ITEM_LANG = "Language";
	private final String ITEM_VERSION = "Latest published version";
	private final String ITEM_PUBLICATION = "Date of publication";

	private RestClient client = new RestClient("http://localhost:8080/stardat-ddiflatdb");

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

	private MCssLayout topSection = new MCssLayout().withFullWidth();
	private MCssLayout topViewSection = new MCssLayout().withFullWidth();
	private MCssLayout topEditSection = new MCssLayout().withFullWidth();
	private MCssLayout bottomSection = new MCssLayout().withFullWidth();
	private MCssLayout bottomViewSection = new MCssLayout().withFullWidth();
	private MCssLayout bottomEditSection = new MCssLayout().withFullWidth();

	private TextField prefLanguageEditor = new TextField();
	private TextField prefLabelEditor = new TextField();

	// private Image headerImage = new Image();
	private Image footerImage = new Image();

	// CvItem cvItem;

	private CVScheme cvScheme;
	private Binder<CVConcept> binder;
	private List<CVConcept> concepts = new ArrayList<CVConcept>();
	private MCssLayout languageLayout = new MCssLayout();

	// The opened search hit at the results grid (null at the begining)
	// private SearchHit selectedItem = null;

	// private List<CvItem> cvItems = new ArrayList<>();

	private MHorizontalLayout titleLayout = new MHorizontalLayout();
	private Image logoImage;

	public DetailView(EventBus.UIEventBus eventBus) {
		this.eventBus = eventBus;
	}
	// private

	@PostConstruct
	public void init() {

		List<DDIStore> ddiSchemes = client.getElementList("thesoz", DDIElement.CVSCHEME);
		if (ddiSchemes != null && !ddiSchemes.isEmpty())
			cvScheme = new CVScheme(ddiSchemes.get(0));

		setFormMode(FormMode.view);
		// cvItem = addDummyData1();

		initTopViewSection();
		initTopEditSection();
		initBottomViewSection();
		initBottomEditSection();

		editButton.addClickListener(e -> setFormMode(FormMode.edit));
		cancelButton.addClickListener(e -> setFormMode(FormMode.view));

		buttonLayout.withFullWidth().withStyleName("alignTextRight").add(editButton, cancelButton, saveButton);

		languageLayout.withFullWidth();

		topSection.add(topViewSection, topEditSection);

		bottomSection.add(bottomViewSection, bottomEditSection);

		// the layout that contains all
		mainLayout.withSpacing(true).withMargin(true).withFullWidth().add(buttonLayout, topSection, languageLayout,
				bottomSection);

		this.withHeightUndefined().add(mainLayout);
	}

	private void initTopViewSection() {
		topViewSection.removeAllComponents();

		MLabel topTitle = new MLabel();
		topTitle.withStyleName("topTitle").withContentMode(ContentMode.HTML)
				.withValue( /* cvScheme.getOwnerAgency().get(0).getName() */ "DDI" + " Controlled Vocabulary for "
						+ cvScheme.getTitleByLanguage("EN") + "</strong>");

		Resource res = new ThemeResource("img/ddi-logo-r.png");
		Image logo = new Image(null, res);
		logo.setWidth("100px");

		MCssLayout topHead = new MCssLayout();
		topHead.withFullWidth().add(logo, topTitle);

		MCssLayout titleSmall = new MCssLayout();
		titleSmall.withFullWidth().add(new MLabel(ITEM_TITLE + ":").withWidth("120px").withStyleName("leftPart"),
				new MLabel(cvScheme.getTitleByLanguage(selectedLang)).withStyleName("rightPart"));

		MCssLayout description = new MCssLayout();
		description.withFullWidth().add(new MLabel(ITEM_DEF + ":").withWidth("120px").withStyleName("leftPart"),
				new MLabel(cvScheme.getDescriptionByLanguage(selectedLang)).withStyleName("rightPart"));

		MCssLayout code = new MCssLayout();
		code.withFullWidth().add(new MLabel(ITEM_CODE + ":").withWidth("120px").withStyleName("leftPart"),
				new MLabel("CODE_TEST").withStyleName("rightPart"));

		MCssLayout langSec = new MCssLayout();
		langSec.withFullWidth()
				.add(new MCssLayout().withWidth("33%").add(
						new MLabel(ITEM_LANG + ":").withWidth("120px").withStyleName("leftPart"),
						new MLabel(selectedLang).withStyleName("rightPart")),
						new MCssLayout().withWidth("33%").add(
								new MLabel(ITEM_VERSION + ":").withWidth("180px").withStyleName("leftPart"),
								new MLabel("1.0-" + selectedLang).withStyleName("rightPart")),
						new MCssLayout().withWidth("33%").add(
								new MLabel(ITEM_PUBLICATION + ":").withWidth("140px").withStyleName("leftPart"),
								new MLabel("10.10.2017").withStyleName("rightPart")));

		topViewSection.add(topHead, titleSmall, description, code, langSec);
	}

	private void initTopEditSection() {
		topEditSection.removeAllComponents();

		MLabel topTitle = new MLabel();
		topTitle.withStyleName("topTitle").withContentMode(ContentMode.HTML)
				.withValue( /* cvScheme.getOwnerAgency().get(0).getName() */ "DDI" + " Controlled Vocabulary for "
						+ cvScheme.getTitleByLanguage("EN") + "</strong>");

		Resource res = new ThemeResource("img/ddi-logo-r.png");
		Image logo = new Image(null, res);
		logo.setWidth("100px");

		MCssLayout topHead = new MCssLayout();
		topHead.withFullWidth().add(logo, topTitle);

		MTextField titleField = new MTextField();
		titleField.withStyleName("editField").withValue(cvScheme.getTitleByLanguage("EN"));

		MCssLayout titleSmall = new MCssLayout();
		titleSmall.withFullWidth().add(new MLabel(ITEM_TITLE + ":").withWidth("120px").withStyleName("leftPart"),
				selectedLang.equals("EN") ? titleField
						: new MLabel(cvScheme.getTitleByLanguage("EN")).withStyleName("rightPart"));

		TextArea descField = new TextArea();
		descField.setStyleName("editField");
		descField.setValue(cvScheme.getDescriptionByLanguage("EN"));

		MCssLayout description = new MCssLayout();
		description.withFullWidth().add(new MLabel(ITEM_DEF + ":").withWidth("120px").withStyleName("leftPart"),
				selectedLang.equals("EN") ? descField
						: new MLabel(cvScheme.getDescriptionByLanguage("EN")).withStyleName("rightPart"));

		MTextField codeField = new MTextField();
		codeField.withStyleName("editField").withValue("CODE_TEST");

		MCssLayout code = new MCssLayout();
		code.withFullWidth().add(new MLabel(ITEM_CODE + ":").withWidth("120px").withStyleName("leftPart"),
				selectedLang.equals("EN") ? codeField
						: new MLabel(cvScheme.getTitleByLanguage("EN")).withStyleName("rightPart"));

		MCssLayout langSec = new MCssLayout();
		langSec.withFullWidth().add(
				new MCssLayout().withWidth("33%")
						.add(new MLabel(ITEM_LANG + ":").withWidth("120px").withStyleName("leftPart"),
								new MLabel("EN").withStyleName("rightPart")),
				new MCssLayout().withWidth("33%").add(
						new MLabel(ITEM_VERSION + ":").withWidth("180px").withStyleName("leftPart"),
						new MLabel("1.0_" + "EN").withStyleName("rightPart")),
				new MCssLayout().withWidth("33%").add(
						new MLabel(ITEM_PUBLICATION + ":").withWidth("140px").withStyleName("leftPart"),
						new MLabel("10.10.2017").withStyleName("rightPart")));

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
		langSecOl.withFullWidth()
				.add(new MCssLayout().withWidth("33%").add(
						new MLabel(ITEM_LANG + ":").withWidth("120px").withStyleName("leftPart"),
						new MLabel(selectedLang).withStyleName("rightPart")),
						new MCssLayout().withWidth("33%").add(
								new MLabel(ITEM_VERSION + ":").withWidth("180px").withStyleName("leftPart"),
								new MLabel("1.0_" + selectedLang).withStyleName("rightPart")),
						new MCssLayout().withWidth("33%").add(
								new MLabel(ITEM_PUBLICATION + ":").withWidth("140px").withStyleName("leftPart"),
								new MLabel("10.10.2017").withStyleName("rightPart")));

		if (selectedLang.equals("EN")) {
			titleSmallOl.setVisible(false);
			descriptionOl.setVisible(false);
			langSecOl.setVisible(false);
		}

		topEditSection.add(topHead, titleSmall, description, code, langSec, titleSmallOl, descriptionOl, langSecOl);
	}

	private void initBottomViewSection() {
		bottomViewSection.removeAllComponents();

		List<DDIStore> ddiConcepts = client.getElementList("thesoz", DDIElement.CVCONCEPT);

		ddiConcepts.forEach(ddiConcept -> concepts.add(new CVConcept(ddiConcept)));

		Set<String> languages = new LinkedHashSet<>();
		concepts.forEach(concept -> {
			languages.addAll(concept.getLanguagesByPrefLabel());

		});

		languages.forEach(item ->

		{
			MButton langBUtton = new MButton(item);
			langBUtton.withStyleName("langbutton").addClickListener(e -> {
				setSelectedLang(e.getButton().getCaption());
				if (formMode.equals(FormMode.view))
					initTopViewSection();
				else
					initTopEditSection();
			});
			languageLayout.add(langBUtton);
		});

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

		// initialize the results grid
		Grid<CVConcept> detailGrid = new Grid<>(CVConcept.class);
		binder = detailGrid.getEditor().getBinder();
		binder.addValueChangeListener(event -> Notification.show("Binder Event"));
		detailGrid.setItems(concepts);
		// detailGrid.setItems( cvItem.getCvElements() );
		// detailGrid.addStyleName(ValoTheme.TABLE_BORDERLESS);

		// SourceAsString column
		// results.addColumn(SearchHit::getSourceAsString).setCaption("Study").setId("study");

		detailGrid.removeAllColumns();
		detailGrid.addColumn(CVConcept::getId).setCaption("URI").setExpandRatio(1);

		detailGrid.addColumn(concept -> concept.getPrefLabelByLanguage("en")).setCaption("en")
				.setEditorComponent(prefLabelEditor, (concept, value) -> concept.setPrefLabelByLanguage("en", value))
				.setWidth(100);

		Binding<CVConcept, String> prefLabelBinding = binder.bind(prefLanguageEditor,
				concept -> concept.getPrefLabelByLanguage(selectedLang),
				(concept, label) -> concept.setPrefLabelByLanguage(selectedLang, label));

		detailGrid.addColumn(concept -> concept.getPrefLabelByLanguage(selectedLang)).setCaption(selectedLang)
				.setEditorBinding(prefLabelBinding).setWidth(100);// Component(prefLanguageEditor,
		// (concept, value) ->
		// updateConcept(concept,
		// value, "en"));

		detailGrid.addColumn(concept -> concept.getDescriptionByLanguage(selectedLang)).setCaption("Definition")
				.setExpandRatio(1);

		detailGrid.setSizeFull();

		detailLayout.addComponents(detailGrid);
		detailLayout.setMargin(true);
		detailLayout.setSpacing(true);
		detailLayout.setSizeFull();
		detailLayout.setExpandRatio(detailGrid, 1);

		bottomViewSection.add(detailTab);
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

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

	// private CvItem addDummyData1(){
	// List<CvElement> cvElements = new ArrayList<>();
	// cvElements.add( new CvElement( "FR" , "Méthode d'agrégation", "Identifie
	// le type d'agrégation utilisé pour combiner les catégories liées,
	// généralement dans une branche commune d'une hiérarchie, pour fournir des
	// informations à un niveau plus large que le niveau auquel des observations
	// détaillées sont prises. (À partir du: Glossaire statistique de l'OCDE)",
	// "1.0","10 April 2017"));
	// cvElements.add( new CvElement( "ES" , "Método de agregación", "Identifica
	// el tipo de agregación utilizada para combinar categorías relacionadas,
	// normalmente dentro de una rama común de una jerarquía, para proporcionar
	// información a un nivel más amplio que el nivel en el que se toman
	// observaciones detalladas. (De: El Glosario de términos estadísticos de la
	// OCDE)", "1.0","10 August 2017"));
	// cvElements.add( new CvElement( "DE" , "Aggregationsmethode",
	// "Identifiziert die Art der Aggregation, die verwendet wird, um verwandte
	// Kategorien zu kombinieren, in der Regel innerhalb eines gemeinsamen
	// Zweiges einer Hierarchie, um Informationen auf einer breiteren Ebene als
	// die Ebene, auf der detaillierte Beobachtungen getroffen werden. (Aus: Das
	// OECD-Glossar der statistischen Begriffe)", "1.0","15 August 2017"));
	// cvElements.add( new CvElement( "EN" , "AggregationMethod", "Identifies
	// the type of aggregation used to combine related categories, usually
	// within a common branch of a hierarchy, to provide information at a
	// broader level than the level at which detailed observations are taken.
	// (From: The OECD Glossary of Statistical Terms)", "1.0", "18 August
	// 2016"));
	//
	// return new CvItem("img/ddi-logo-r.png", cvElements);
	// }
	//
	// private CvItem addDummyData2(){
	// List<CvElement> cvElements = new ArrayList<>();
	// cvElements.add( new CvElement( "ES" , "Unidad de Análisis", "Unidad de
	// Análisis Identifica el tipo de agregación utilizada para combinar
	// categorías relacionadas, normalmente dentro de una rama común de una
	// jerarquía, para proporcionar información a un nivel más amplio que el
	// nivel en el que se toman observaciones detalladas. (De: El Glosario de
	// términos estadísticos de la OCDE)", "1.0"));
	// cvElements.add( new CvElement( "DE" , "Analyseeinheit", "Analyseeinheit
	// Identifiziert die Art der Aggregation, die verwendet wird, um verwandte
	// Kategorien zu kombinieren, in der Regel innerhalb eines gemeinsamen
	// Zweiges einer Hierarchie, um Informationen auf einer breiteren Ebene als
	// die Ebene, auf der detaillierte Beobachtungen getroffen werden. (Aus: Das
	// OECD-Glossar der statistischen Begriffe)", "1.0"));
	// cvElements.add( new CvElement( "EN" , "Analysis Unit", "Analysis Unit
	// Identifies the type of aggregation used to combine related categories,
	// usually within a common branch of a hierarchy, to provide information at
	// a broader level than the level at which detailed observations are taken.
	// (From: The OECD Glossary of Statistical Terms)", "1.0"));
	//
	// Resource res = new ThemeResource( "img/ddi-logo-r.png" );
	// Image image = new Image( null, res );
	// image.setWidth( "100" );
	//
	// return new CvItem("img/ddi-logo-r.png", cvElements);
	// }

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

}
