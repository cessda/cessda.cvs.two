package eu.cessda.cvmanager.ui.view.publication;

import java.util.Iterator;
import java.util.List;

import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVEditor;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.LanguageLabel;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.service.dto.AgencyDTO;
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
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.view.DetailView;

public class VocabularyGridRow extends CustomComponent {

	final static Logger log = LoggerFactory.getLogger(VocabularyGridRow.class);

	private final VocabularyDTO vocabulary;
	private final AgencyDTO agency;
	private List<CodeDTO> codes;
	private String keyword;

	private static final long serialVersionUID = 4932510587869607326L;

	private MCssLayout container = new MCssLayout();
	private MHorizontalLayout hLayout = new MHorizontalLayout();
	private MVerticalLayout vLayout = new MVerticalLayout();
	private MCssLayout languageLayout = new MCssLayout();
	private MCssLayout titleLayout = new MCssLayout();
	private MLabel slTitle = new MLabel();
	private MLabel tlTitle = new MLabel();
	private MLabel desc = new MLabel();
	private MLabel version = new MLabel();

	private MLabel conceptList = new MLabel();
	private Image logo;

	private transient ConfigurationService configService;

	public VocabularyGridRow(VocabularyDTO voc, AgencyDTO agency, ConfigurationService configService, String keyword) {
		this.vocabulary = voc;
		this.agency = agency;
		this.configService = configService;
		this.keyword = keyword;

		setCompositionRoot(container);
		initLayout();
	}

	private void initLayout() {
		slTitle.withStyleName("marginright20").withContentMode(ContentMode.HTML);

		tlTitle.withContentMode(ContentMode.HTML);

		desc.withContentMode(ContentMode.HTML).withFullWidth();
		version.withContentMode(ContentMode.HTML);
		conceptList.withContentMode(ContentMode.HTML);

		languageLayout.withFullWidth();
		String sourceLanguage = configService.getDefaultSourceLanguage();
		
		Language.getIsoFromLanguage( vocabulary.getLanguages() ).forEach(item -> {
			MButton langButton = new MButton(item.toUpperCase());
			langButton.withStyleName("langbutton").addClickListener(e -> {
				applyButtonStyle(e.getButton());
				setContent(Language.getEnum( e.getButton().getCaption().toLowerCase() ));
			});
			languageLayout.add(langButton);
			if (item.equals(sourceLanguage)) {
				langButton.addStyleName("font-bold");
				langButton.setDescription("source language");
				langButton.click();
			}
		});

		titleLayout.withFullWidth().add(slTitle, tlTitle);

		vLayout.withMargin(false).withFullWidth().add(languageLayout, titleLayout, desc, version, conceptList);
		
		logo = new Image(null, new ThemeResource( agency.getLogopath() ));
		logo.setWidth("120px");

		hLayout.withFullWidth().add(logo, vLayout).withExpand(vLayout, 1.0f);

		container.withStyleName("itemcontainer").withFullWidth().add(hLayout);
		// Initial
		setContent( Language.ENGLISH );
	}

	public Image getLogo() {
		return logo;
	}

	public void setLogo(Image logo) {
		this.logo = logo;
	}


	public MCssLayout getContainer() {
		return container;
	}

	public void setContainer(MCssLayout container) {
		this.container = container;
	}

	private void setContent(Language language) {
		Language sourceLanguage = Language.getEnumByName( vocabulary.getSourceLanguage() );
		slTitle.setValue("<a href='" + configService.getServerContextPath() + "/#!" + DetailView.VIEW_NAME + "/"
				+ vocabulary.getUri() + "'>" + vocabulary.getTitleByLanguage(sourceLanguage) + "</a>");
		log.info("URL is: " + slTitle.getValue());

		tlTitle.setValue("<a href='" + configService.getServerContextPath() + "/#!" + DetailView.VIEW_NAME + "/"
				+ vocabulary.getUri()  + "'>" + vocabulary.getNotation() + "</a>");
		desc.setValue(vocabulary.getDefinitionByLanguage(language));
		version.setValue("Version: " + vocabulary.getVersion() + " "
				+ (language.equals("en") ? "" : "_" + language) + "<a href='" + configService.getServerContextPath() + "/#!" + DetailView.VIEW_NAME + "/"
				+ vocabulary.getUri() +"?tab=download"+ "'>Download</a>");

		// COmpare Code with InnerHit
//		List<CVConcept> theConceptList = vocabulary.getConceptList();
//
//		if (!theConceptList.isEmpty()) {
//			StringBuilder sConcepts = new StringBuilder();
//			sConcepts.append("Found in code(s): ");
//
//			int index= 0;
//			for (CVConcept concept : theConceptList) {
//				if( index > 0)
//					sConcepts.append( ", ");
//				sConcepts.append( getMatchesConceptFromKeyword(concept, language));
//				index++;
//			}
//			conceptList.setValue(sConcepts.toString());
//		}
	}
	
	private String getMatchesConceptFromKeyword(CodeDTO code, Language language) {
//		String conceptLink = "<a href='" + configService.getServerContextPath() + "/#!" + DetailView.VIEW_NAME + "/"
//				+ vocabulary.getContainerId() + "/" + concept.getNotation() + "'>" + concept.getPrefLabelByLanguage(language) + "</a>";
//		for (LanguageLabel languagelabel : concept.getPrefLabel()) {
//			if( languagelabel.getLabel().toLowerCase().contains(keyword)) {
//				conceptLink = "<a href='" + configService.getServerContextPath() + "/#!" + DetailView.VIEW_NAME + "/"
//						+ vocabulary.getContainerId() + "/" + concept.getNotation() + "?lang=" + languagelabel.getLanguage() + "'>" +languagelabel.getLabel() + "</a>";
//			}
//		};
//		return conceptLink;
		return null;
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
