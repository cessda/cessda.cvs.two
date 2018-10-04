package eu.cessda.cvmanager.ui.view.publication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Image;

import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.view.DetailView;
import eu.cessda.cvmanager.ui.view.DetailsView;

public class VocabularyGridRowPublish extends CustomComponent {

	final static Logger log = LoggerFactory.getLogger(VocabularyGridRowPublish.class);

	private final VocabularyDTO vocabulary;
	private final AgencyDTO agency;
	private List<CodeDTO> codes;

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
		
		if( vocabulary.getSourceLanguage() != null) 
			sourceLanguage = Language.valueOfEnum( vocabulary.getSourceLanguage() );
		else
			sourceLanguage = Language.ENGLISH;
		
		if( this.vocabulary.getSelectedLang() != null )
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
//		conceptList.withContentMode(ContentMode.HTML);
		codeList.withFullWidth();

		languageLayout.withUndefinedSize().withStyleName( "pull-right" );
		
		vocabulary.getLanguagesPublished().forEach(item -> {
			MButton langButton = new MButton(item.toUpperCase());
			langButton.addStyleName( "langbutton" );
			
			if( item.equalsIgnoreCase( sourceLanguage.toString() ))
				langButton.addStyleName( "button-source-language" );
			
			if( item.equalsIgnoreCase( currentSelectedLanguage.toString() ))
				langButton.addStyleName( "button-language-selected" );
			
			// determine the status
			vocabulary.getLatestVersionByLanguage( item )
			.ifPresent( versionDTO -> {
				if( versionDTO.getStatus().equals( Status.DRAFT.toString())) {
					langButton.addStyleName( "status-draft" );
					langButton.setDescription("DRAFT");
				}
				else if( versionDTO.getStatus().equals( Status.INITIAL_REVIEW.toString())) {
					langButton.addStyleName( "status-review-initial" );
					langButton.setDescription("INITIAL_REVIEW");
				}else if( versionDTO.getStatus().equals( Status.FINAL_REVIEW.toString())) {
					langButton.addStyleName( "status-review-final" );
					langButton.setDescription("FINAL_REVIEW");
				}
			});
			
			langButton.addClickListener(e -> {
				applyButtonStyle(e.getButton());
				currentSelectedLanguage = Language.getEnum( e.getButton().getCaption().toLowerCase() );
				setContent();
			});
			languageLayout.add(langButton);
			if (item.equals(sourceLanguage.toString().toUpperCase())) {
				langButton.addStyleName("font-bold");
				langButton.setDescription("source language" + (langButton.getDescription() != null && !langButton.getDescription().isEmpty()? " (" + langButton.getDescription() + ")":""));
				langButton.click();
			}
		});

		titleLayout
			.withUndefinedSize()
			.withStyleName( "pull-left" )
			.add(slTitle, tlTitle);

		vLayout
			.withFullWidth()
			.add(languageLayout, titleLayout, desc, version, codeList);
		
		MLabel logoLabel = new MLabel()
			.withContentMode( ContentMode.HTML )
			.withWidth("120px");
		
		if( agency.getLogo() != null && !agency.getLogo().isEmpty())
			logoLabel.setValue(  "<img style=\"max-width:120px;max-height:80px\" alt=\"" + agency.getName() + " logo\" src='" + agency.getLogo() + "'>");

		hLayout.withFullWidth().add(logoLabel, vLayout).withExpand(vLayout, 1.0f);

		container
			.withStyleName("itemcontainer")
			.withFullWidth()
			.add( 
				hLayout,
				new MLabel("<hr class=\"fancy-line\"/>").withContentMode( ContentMode.HTML ).withFullSize());
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
		codeList.setVisible( false );
		
		String title = vocabulary.getTitleByLanguage( currentSelectedLanguage );
		String definition = vocabulary.getDefinitionByLanguage( currentSelectedLanguage );
		
		if( title == null )
			return;
		
		if( definition == null )
			definition = "";
		
		String baseUrl = configService.getServerContextPath() + "/#!" + DetailView.VIEW_NAME + "/" + vocabulary.getNotation() + "?url=";
		try {
			baseUrl += URLEncoder.encode(vocabulary.getUri(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			baseUrl += vocabulary.getUri();
			e.printStackTrace();
		}
		
		slTitle.setValue("<a href='" + baseUrl + "'>" + title + "</a>");
		log.info("URL is: " + slTitle.getValue());

		tlTitle.setValue("<a href='" + baseUrl  + "'>" + vocabulary.getNotation() + "</a>");
		desc.setValue( definition );
		version.setValue("Version: "+ vocabulary.getVersionByLanguage(currentSelectedLanguage) + " "
				+ (currentSelectedLanguage.equals( sourceLanguage ) ? "" : "_" + currentSelectedLanguage.toString()) + " <a href='" + baseUrl +"&tab=download"+ "'>Download</a>");
		
		if( !vocabulary.getCodes().isEmpty() ) {
			codeList.setVisible( true );
			for( CodeDTO code: vocabulary.getCodes() ) {
				String codeTitle = code.getTitleByLanguage( currentSelectedLanguage );
				String codeDefinition = code.getDefinitionByLanguage( currentSelectedLanguage );
				if( codeDefinition == null )
					codeDefinition = "";
				if( codeTitle != null ) {
					codeList.add(
						new MLabel( "<a href=\"" + baseUrl + "&code=" + code.getNotation().replace(".", "-") + "\">" + codeTitle + "</a>" 
								+ " " + codeDefinition )
						.withContentMode( ContentMode.HTML )
						.withFullWidth()
					);
				}
			}
		}
	}

	private void applyButtonStyle(Button pressedButton) {

		Iterator<Component> iterate = languageLayout.iterator();
		while (iterate.hasNext()) {
			Component c = iterate.next();
			if (c instanceof Button) {
				((Button) c).removeStyleName("button-language-selected");
			}
		}
		pressedButton.addStyleName("button-language-selected");
	}

}
