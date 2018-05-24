package eu.cessda.cvmanager.ui.component;

import java.util.Iterator;
import java.util.List;

import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVEditor;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.LanguageLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Image;

import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.ui.view.DetailView;

public class CvSchemeComponent extends CustomComponent {

	final static Logger log = LoggerFactory.getLogger(CvSchemeComponent.class);

	private CVScheme cvScheme;
	private List<CVConcept> cvConcepts;
	private String keyword;

	private static final long serialVersionUID = 4932510587869607326L;

	private MCssLayout container = new MCssLayout();
	private MHorizontalLayout hLayout = new MHorizontalLayout();
	private MVerticalLayout vLayout = new MVerticalLayout();
	private MCssLayout languageLayout = new MCssLayout();
	private MCssLayout titleLayout = new MCssLayout();
	private MLabel enTitle = new MLabel();
	private MLabel olTitle = new MLabel();
	private MLabel desc = new MLabel();
	private MLabel version = new MLabel();

	private MLabel conceptList = new MLabel();
	private Image logo;

	private transient ConfigurationService configService;

	public CvSchemeComponent(CVScheme cvScheme, ConfigurationService configService, String keyword) {
		this.cvScheme = cvScheme;
		this.configService = configService;
		this.keyword = keyword;

		setCompositionRoot(container);
		initLayout();
	}

	private void initLayout() {
		enTitle.withStyleName("marginright20").withContentMode(ContentMode.HTML);

		olTitle.withContentMode(ContentMode.HTML);

		desc.withContentMode(ContentMode.HTML).withFullWidth();
		version.withContentMode(ContentMode.HTML);
		conceptList.withContentMode(ContentMode.HTML);

		languageLayout.withFullWidth();
		String sourceLanguage = configService.getDefaultSourceLanguage();

		cvScheme.getLanguagesByTitle().forEach(item -> {
			MButton langButton = new MButton(item.toUpperCase());
			langButton.withStyleName("langbutton").addClickListener(e -> {
				applyButtonStyle(e.getButton());
				setContent(e.getButton().getCaption().toLowerCase());
			});
			languageLayout.add(langButton);
			if (item.equals(sourceLanguage)) {
				langButton.addStyleName("font-bold");
				langButton.setDescription("source language");
				langButton.click();
			}
		});

		titleLayout.withFullWidth().add(enTitle, olTitle);

		vLayout.withMargin(false).withFullWidth().add(languageLayout, titleLayout, desc, version, conceptList);

		Resource res = new ThemeResource("img/ddi-logo-r.png");
		if (cvScheme.getOwnerAgency() != null && !cvScheme.getOwnerAgency().isEmpty()) {
			CVEditor agency = cvScheme.getOwnerAgency().get(0);
			if (agency.getLogoPath() != null && !agency.getLogoPath().isEmpty())
				res = new ThemeResource(agency.getLogoPath());
		}
		
		logo = new Image(null, res);
		logo.setWidth("120px");

		hLayout.withFullWidth().add(logo, vLayout).withExpand(vLayout, 1.0f);

		container.withStyleName("itemcontainer").withFullWidth().add(hLayout);
		// Initial
		setContent("en");
	}

	public Image getLogo() {
		return logo;
	}

	public void setLogo(Image logo) {
		this.logo = logo;
	}

	public List<CVConcept> getCvElements() {
		return cvConcepts;
	}

	public void setCvElements(List<CVConcept> cvElements) {
		this.cvConcepts = cvElements;
	}

	public MCssLayout getContainer() {
		return container;
	}

	public void setContainer(MCssLayout container) {
		this.container = container;
	}

	private void setContent(String language) {
		enTitle.setValue("<a href='" + configService.getServerContextPath() + "/#!" + DetailView.VIEW_NAME + "/"
				+ cvScheme.getContainerId() + "'>" + cvScheme.getTitleByLanguage("en") + "</a>");
//		log.info("URL is: " + enTitle.getValue());

		olTitle.setValue("<a href='" + configService.getServerContextPath() + "/#!" + DetailView.VIEW_NAME + "/"
				+ cvScheme.getContainerId() + "'>" + cvScheme.getCode() + "</a>");
		desc.setValue(cvScheme.getDescriptionByLanguage(language));
		version.setValue("Version: " + cvScheme.getVersion().getPublicationVersion() + " "
				+ (language.equals("en") ? "" : "_" + language) + "<a href='" + configService.getServerContextPath() + "/#!" + DetailView.VIEW_NAME + "/"
				+ cvScheme.getContainerId() +"?tab=download"+ "'>Download</a>");

		List<CVConcept> theConceptList = cvScheme.getConceptList();

		if (!theConceptList.isEmpty()) {
			StringBuilder sConcepts = new StringBuilder();
			sConcepts.append("Found in code(s): ");

			int index= 0;
			for (CVConcept concept : theConceptList) {
				if( index > 0)
					sConcepts.append( ", ");
				sConcepts.append( getMatchesConceptFromKeyword(concept, language));
				index++;
			}
			conceptList.setValue(sConcepts.toString());
		}
	}
	
	private String getMatchesConceptFromKeyword(CVConcept concept, String language) {
		String conceptLink = "<a href='" + configService.getServerContextPath() + "/#!" + DetailView.VIEW_NAME + "/"
				+ cvScheme.getContainerId() + "/" + concept.getNotation() + "'>" + concept.getPrefLabelByLanguage(language) + "</a>";
		for (LanguageLabel languagelabel : concept.getPrefLabel()) {
			if( languagelabel.getLabel().toLowerCase().contains(keyword)) {
				conceptLink = "<a href='" + configService.getServerContextPath() + "/#!" + DetailView.VIEW_NAME + "/"
						+ cvScheme.getContainerId() + "/" + concept.getNotation() + "?lang=" + languagelabel.getLanguage() + "'>" +languagelabel.getLabel() + "</a>";
			}
		};
		return conceptLink;
	}

	private void applyButtonStyle(Button pressedButton) {

		Iterator<Component> iterate = languageLayout.iterator();
		while (iterate.hasNext()) {
			Component c = iterate.next();
			if (c instanceof Button) {
				((Button) c).removeStyleName("button-pressed");
			}
		}
		pressedButton.addStyleName("button-pressed");
	}

}
