package eu.cessda.cvmanager.ui.view.publication;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.view.PublicationDetailsView;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class VocabularyGridRowPublish extends CustomComponent {

	private static final Logger log = LoggerFactory.getLogger(VocabularyGridRowPublish.class);

	private final VocabularyDTO vocabulary;
	private final AgencyDTO agency;
	private static final String BUTTON_LANGUAGE_SELECTED = "button-language-selected";

	private static final long serialVersionUID = 4932510587869607326L;

	private MCssLayout container = new MCssLayout();
	private MHorizontalLayout hLayout = new MHorizontalLayout();
	private MCssLayout vLayout = new MCssLayout();
	private MCssLayout languageLayout = new MCssLayout();
	private MCssLayout titleLayout = new MCssLayout();
	private MCssLayout codeList = new MCssLayout();
	private MLabel slTitle = new MLabel();
	private MLabel tlTitle = new MLabel();
	private MLabel desc = new MLabel();
	private MLabel version = new MLabel();

	private transient ConfigurationService configService;
	private Language currentSelectedLanguage;
	private Language sourceLanguage;

	public VocabularyGridRowPublish(VocabularyDTO voc, AgencyDTO agency, ConfigurationService configService) {
		this.vocabulary = voc;
		this.agency = agency;
		this.configService = configService;

		if (vocabulary.getSourceLanguage() != null)
			sourceLanguage = Language.getByIso(vocabulary.getSourceLanguage());
		else
			sourceLanguage = Language.ENGLISH;

		if (this.vocabulary.getSelectedLang() != null)
			currentSelectedLanguage = this.vocabulary.getSelectedLang();
		else
			currentSelectedLanguage = sourceLanguage;

		setCompositionRoot(container);
		initLayout();
	}

	private void initLayout() {
		slTitle.withStyleName("marginright20").withContentMode(ContentMode.HTML);

		tlTitle.withContentMode(ContentMode.HTML);

		desc.withContentMode(ContentMode.HTML).withFullWidth();
		version.withContentMode(ContentMode.HTML);
		codeList.withFullWidth();

		languageLayout.withUndefinedSize();

		List<String> languages = new ArrayList<>();
		// add tls if exist
		if (vocabulary.getLanguages().size() > 1) {
			languages.addAll(
					vocabulary.getLanguagesPublished().stream().filter(p -> !p.equals(vocabulary.getSourceLanguage()))
							.sorted(Comparator.reverseOrder()).collect(Collectors.toList()));
		}
		// add source language
		languages.add(vocabulary.getSourceLanguage());
		languages.forEach(item -> {
			MButton langButton = new MButton(item.toUpperCase());
			langButton.addStyleName("langbutton");

			if (item.equalsIgnoreCase(sourceLanguage.getIso())) {
				langButton.addStyleName("button-source-language");
				langButton.setDescription("Source language");
			}

			if (item.equalsIgnoreCase(currentSelectedLanguage.getIso()))
				langButton.addStyleName(BUTTON_LANGUAGE_SELECTED);

			langButton.addClickListener(e -> {
				applyButtonStyle(e.getButton());
				currentSelectedLanguage = Language.getByIso(e.getButton().getCaption().toLowerCase());
				setContent();
			});
			languageLayout.add(langButton);
			if (item.equalsIgnoreCase(sourceLanguage.getIso())) {
				langButton.addStyleName("font-bold");
				langButton.setDescription("source language"
						+ (langButton.getDescription() != null && !langButton.getDescription().isEmpty()
								? " (" + langButton.getDescription() + ")"
								: ""));
				langButton.click();
			}
		});

		titleLayout.withUndefinedSize().withStyleName("pull-left").add(slTitle, tlTitle);

		version.withFullWidth();
		codeList.withFullWidth();

		vLayout.withFullWidth().add(titleLayout, desc, version, codeList, languageLayout);

		MLabel logoLabel = new MLabel().withContentMode(ContentMode.HTML).withWidth("120px");

		if (agency.getLogo() != null && !agency.getLogo().isEmpty())
			logoLabel.setValue("<img style=\"max-width:120px;max-height:80px\" alt=\"" + agency.getName()
					+ " logo\" src='" + agency.getLogo() + "'>");

		hLayout.withFullWidth().add(logoLabel, vLayout).withExpand(vLayout, 1.0f);

		container.withStyleName("itemcontainer").withFullWidth().add(hLayout,
				new MLabel("<hr class=\"fancy-line\"/>").withContentMode(ContentMode.HTML).withFullSize());
		// Initial
		setContent();
	}

	public MCssLayout getContainer() {
		return container;
	}

	public void setContainer(MCssLayout container) {
		this.container = container;
	}

	private void setContent() {
		codeList.removeAllComponents();
		codeList.setVisible(false);

		String title = vocabulary.getTitleByLanguage(currentSelectedLanguage);
		String definition = vocabulary.getDefinitionByLanguage(currentSelectedLanguage);

		if (title == null)
			return;

		if (definition == null)
			definition = "";

		String baseUrl = configService.getServerContextPath() + "/#!" + PublicationDetailsView.VIEW_NAME + "/"
				+ vocabulary.getNotation() + "?url=";
		try {
			baseUrl += URLEncoder.encode(vocabulary.getUri(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			baseUrl += vocabulary.getUri();
			log.error(e.getMessage(), e);
		}
		if (currentSelectedLanguage != null)
			baseUrl += "&lang=" + currentSelectedLanguage;

		slTitle.setValue("<a href='" + baseUrl + "'>" + title + "</a>");

		tlTitle.setValue("<a href='" + baseUrl + "'>" + vocabulary.getNotation() + "</a>");
		desc.setValue(definition);
		if (currentSelectedLanguage != null) {
			version.setValue("Version: " + vocabulary.getVersionByLanguage(currentSelectedLanguage) + " "
					+ (currentSelectedLanguage.equals(sourceLanguage) ? "" : "_" + currentSelectedLanguage.getIso())
					+ " <a href='" + baseUrl + "&tab=download" + "'>Download</a>");
		}

		if (!vocabulary.getCodes().isEmpty()) {
			codeList.setVisible(true);
			for (CodeDTO code : vocabulary.getCodes()) {
				String codeTitle = code.getTitleByLanguage(currentSelectedLanguage);
				String codeDefinition = code.getDefinitionByLanguage(currentSelectedLanguage);
				if (codeDefinition == null)
					codeDefinition = "";
				if (codeTitle != null) {
					codeList.add(new MLabel("<a href=\"" + baseUrl + "&code=" + code.getNotation().replace(".", "-")
							+ "\">" + codeTitle + "</a>" + " " + codeDefinition).withContentMode(ContentMode.HTML)
									.withFullWidth());
				}
			}
		}
	}

	private void applyButtonStyle(Button pressedButton) {

		for (Component c : languageLayout) {
			if (c instanceof Button) {
				c.removeStyleName(BUTTON_LANGUAGE_SELECTED);
			}
		}
		pressedButton.addStyleName(BUTTON_LANGUAGE_SELECTED);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		VocabularyGridRowPublish that = (VocabularyGridRowPublish) o;
		return Objects.equals(vocabulary, that.vocabulary) &&
				Objects.equals(agency, that.agency) &&
				Objects.equals(container, that.container) &&
				Objects.equals(hLayout, that.hLayout) &&
				Objects.equals(vLayout, that.vLayout) &&
				Objects.equals(languageLayout, that.languageLayout) &&
				Objects.equals(titleLayout, that.titleLayout) &&
				Objects.equals(codeList, that.codeList) &&
				Objects.equals(slTitle, that.slTitle) &&
				Objects.equals(tlTitle, that.tlTitle) &&
				Objects.equals(desc, that.desc) &&
				Objects.equals(version, that.version) &&
				Objects.equals(configService, that.configService) &&
				currentSelectedLanguage == that.currentSelectedLanguage &&
				sourceLanguage == that.sourceLanguage;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), vocabulary, agency, container, hLayout, vLayout, languageLayout, titleLayout, codeList, slTitle, tlTitle, desc, version, configService, currentSelectedLanguage, sourceLanguage);
	}
}
