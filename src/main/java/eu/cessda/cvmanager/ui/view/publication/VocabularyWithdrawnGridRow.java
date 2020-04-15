package eu.cessda.cvmanager.ui.view.publication;

import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.view.WithdrawnDetailView;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.service.dto.AgencyDTO;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MCssLayout;

public class VocabularyWithdrawnGridRow extends AbstractGridRow
{

	private static final long serialVersionUID = 4932510587869607326L;

	public VocabularyWithdrawnGridRow( VocabularyDTO voc, AgencyDTO agency, ConfigurationService configService )
	{
		super( voc, agency, configService );
		initLayout();
	}

	@Override
	protected void initLayout()
	{
		super.initLayout();

		languageLayout.withUndefinedSize().withStyleName( "pull-right" );

		vocabulary.getLanguages().forEach( item ->
		{
			MButton langButton = new MButton( item.toUpperCase() );
			langButton.addStyleName( "langbutton" );

			if ( item.equalsIgnoreCase( sourceLanguage.getIso() ) )
			{
				langButton.addStyleName( "button-source-language" );
			}

			if ( item.equalsIgnoreCase( currentSelectedLanguage.getIso() ) )
			{
				langButton.addStyleName( "button-language-selected" );
			}

			// determine the status
			vocabulary.getLatestVersionByLanguage( item )
					.ifPresent( versionDTO ->
					{
						if ( versionDTO.getStatus().equals( Status.DRAFT.toString() ) )
						{
							langButton.addStyleName( "status-draft" );
							langButton.setDescription( "DRAFT" );
						}
						else if ( versionDTO.getStatus().equals( Status.INITIAL_REVIEW.toString() ) )
						{
							langButton.addStyleName( "status-review-initial" );
							langButton.setDescription( "INITIAL_REVIEW" );
						}
						else if ( versionDTO.getStatus().equals( Status.FINAL_REVIEW.toString() ) )
						{
							langButton.addStyleName( "status-review-final" );
							langButton.setDescription( "FINAL_REVIEW" );
						}
					} );

			langButton.addClickListener( e ->
			{
				applyButtonStyle( e.getButton() );
				currentSelectedLanguage = Language.getByIso( e.getButton().getCaption().toLowerCase() );
				setContent();
			} );
			languageLayout.add( langButton );
			if ( item.equalsIgnoreCase( sourceLanguage.getIso() ) )
			{
				langButton.addStyleName( "font-bold" );
				langButton.setDescription( "source language" + ( langButton.getDescription() != null && !langButton.getDescription().isEmpty() ? " (" + langButton.getDescription() + ")" : "" ) );
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
		String baseUrl = configService.getServerContextPath() + "/#!" + WithdrawnDetailView.VIEW_NAME + "/" + vocabulary.getNotation();
		super.setContent( baseUrl );
	}
}
