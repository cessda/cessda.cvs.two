package eu.cessda.cvmanager.ui.view.publication;

import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.view.PublicationDetailsView;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.service.dto.AgencyDTO;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MCssLayout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class VocabularyGridRowPublish extends AbstractGridRow
{

	private static final String BUTTON_LANGUAGE_SELECTED = "button-language-selected";

	private static final long serialVersionUID = 4932510587869607326L;

	public VocabularyGridRowPublish( VocabularyDTO voc, AgencyDTO agency, ConfigurationService configService )
	{
		super( voc, agency, configService );
		initLayout();
	}

	@Override
	protected void initLayout()
	{
		super.initLayout();

		languageLayout.withUndefinedSize();

		List<String> languages;
		// add tls if exist
		if ( vocabulary.getLanguages().size() > 1 )
		{
			languages = vocabulary.getLanguagesPublished().stream()
					.filter( p -> !p.equals( vocabulary.getSourceLanguage() ) )
					.sorted( Comparator.reverseOrder() )
					.collect( Collectors.toList() );
		}
		else
		{
			languages = new ArrayList<>();
		}

		// add source language
		languages.add( vocabulary.getSourceLanguage() );
		languages.forEach( item ->
		{
			MButton langButton = new MButton( item.toUpperCase() );
			langButton.addStyleName( "langbutton" );

			if ( item.equalsIgnoreCase( sourceLanguage.getIso() ) )
			{
				langButton.addStyleName( "button-source-language" );
				langButton.setDescription( "Source language" );
			}

			if ( item.equalsIgnoreCase( currentSelectedLanguage.getIso() ) )
			{
				langButton.addStyleName( BUTTON_LANGUAGE_SELECTED );
			}

			langButton.addClickListener( e ->
			{
				applyButtonStyle( e.getButton() );
				currentSelectedLanguage = Language.getByIso( e.getButton().getCaption().toLowerCase() );
				setContent();
			} );
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

		// Initial
		setContent();
	}

	public MCssLayout getContainer()
	{
		return container;
	}

	public void setContainer( MCssLayout container )
	{
		this.container = container;
	}

	private void setContent()
	{
		String baseUrl = configService.getServerContextPath() + "/#!" + PublicationDetailsView.VIEW_NAME + "/"
				+ vocabulary.getNotation() + "?url=";
		try
		{
			baseUrl += URLEncoder.encode( vocabulary.getUri(), "UTF-8" );
		}
		catch ( UnsupportedEncodingException e )
		{
			throw new IllegalStateException( e );
		}

		super.setContent( baseUrl );
	}
}
