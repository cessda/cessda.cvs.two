package eu.cessda.cvmanager.ui.view.publication;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import java.util.Objects;

public abstract class AbstractGridRow extends CustomComponent
{
    private static final Logger log = LoggerFactory.getLogger( AbstractGridRow.class );
    private static final long serialVersionUID = 615278673528017164L;

    protected final VocabularyDTO vocabulary;
    protected final AgencyDTO agency;
    protected final ConfigurationService configService;
    protected final MHorizontalLayout hLayout = new MHorizontalLayout();
    protected final MCssLayout vLayout = new MCssLayout();
    protected final MCssLayout languageLayout = new MCssLayout();
    protected final MCssLayout titleLayout = new MCssLayout();
    protected final MCssLayout codeList = new MCssLayout();
    protected final MLabel slTitle = new MLabel();
    protected final MLabel tlTitle = new MLabel();
    protected final MLabel desc = new MLabel();
    protected final MLabel version = new MLabel();
    protected MCssLayout container = new MCssLayout();
    protected Language currentSelectedLanguage;
    protected Language sourceLanguage;

    public AbstractGridRow( VocabularyDTO voc, AgencyDTO agency, ConfigurationService configService )
    {
        this.vocabulary = voc;
        this.agency = agency;
        this.configService = configService;

        if ( vocabulary.getSourceLanguage() != null )
        {
            sourceLanguage = Language.getByIso( vocabulary.getSourceLanguage() );
        }
        else
        {
            sourceLanguage = Language.ENGLISH;
        }

        if ( this.vocabulary.getSelectedLang() != null )
        {
            currentSelectedLanguage = this.vocabulary.getSelectedLang();
        }
        else
        {
            currentSelectedLanguage = sourceLanguage;
        }

        setCompositionRoot( container );
    }

    protected void initLayout()
    {
        slTitle.withStyleName( "marginright20" ).withContentMode( ContentMode.HTML );

        tlTitle.withContentMode( ContentMode.HTML );

        desc.withContentMode( ContentMode.HTML ).withFullWidth();
        version.withContentMode( ContentMode.HTML );
        codeList.withFullWidth();

        titleLayout.withUndefinedSize().withStyleName( "pull-left" ).add( slTitle, tlTitle );

        version.withFullWidth();
        codeList.withFullWidth();

        vLayout.withFullWidth().add( titleLayout, desc, version, codeList, languageLayout );

        MLabel logoLabel = new MLabel().withContentMode( ContentMode.HTML ).withWidth( "120px" );

        if ( agency.getLogo() != null && !agency.getLogo().isEmpty() )
        {
            logoLabel.setValue( "<img style=\"max-width:120px;max-height:80px\" alt=\"" + agency.getName() + " logo\" src='" + agency.getLogo() + "'>" );
        }

        hLayout.withFullWidth().add( logoLabel, vLayout ).withExpand( vLayout, 1.0f );

        container.withStyleName( "itemcontainer" );
        container.withFullWidth();
        container.add( hLayout, new MLabel( "<hr class=\"fancy-line\"/>" ).withContentMode( ContentMode.HTML ).withFullSize() );
    }

    public MCssLayout getContainer()
    {
        return container;
    }

    public void setContainer( MCssLayout container )
    {
        this.container = container;
    }

    protected void setContent( String baseUrl )
    {
        codeList.removeAllComponents();
        codeList.setVisible( false );

        String title = vocabulary.getTitleByLanguage( currentSelectedLanguage );

        if ( title != null )
        {
            String definition;

            if ( currentSelectedLanguage != null )
            {
                baseUrl += "?lang=" + currentSelectedLanguage.getIso();
                definition = vocabulary.getDefinitionByLanguage( currentSelectedLanguage );
                version.setValue( "Version: " + vocabulary.getVersionByLanguage( currentSelectedLanguage ) + " "
                        + ( currentSelectedLanguage.equals( sourceLanguage ) ? "" : "_" + currentSelectedLanguage.getIso() )
                        + " <a href='" + baseUrl + "&tab=download" + "'>Download</a>" );
            }
            else
            {
                definition = "";
            }

            slTitle.setValue( "<a href='" + baseUrl + "'>" + title + "</a>" );
            log.info( "URL is: {}", slTitle.getValue() );

            tlTitle.setValue( "<a href='" + baseUrl + "'>" + vocabulary.getNotation() + "</a>" );
            desc.setValue( definition );

            if ( !vocabulary.getCodes().isEmpty() )
            {
                codeList.setVisible( true );
                for ( CodeDTO code : vocabulary.getCodes() )
                {
                    String codeTitle = code.getTitleByLanguage( currentSelectedLanguage );
                    String codeDefinition = code.getDefinitionByLanguage( currentSelectedLanguage );
                    if ( codeDefinition == null )
                    {
                        codeDefinition = "";
                    }
                    if ( codeTitle != null )
                    {
                        codeList.add( new MLabel( "<a href=\"" + baseUrl + "?code="
                                + code.getNotation().replace( ".", "-" ) + "\">"
                                + codeTitle + "</a>" + " " + codeDefinition )
                                .withContentMode( ContentMode.HTML )
                                .withFullWidth()
                        );
                    }
                }
            }
        }
    }

    protected void applyButtonStyle( Button pressedButton )
    {
        for ( Component c : languageLayout )
        {
            if ( c instanceof Button )
            {
                c.removeStyleName( "button-language-selected" );
            }
        }
        pressedButton.addStyleName( "button-language-selected" );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        if ( !super.equals( o ) ) return false;
        AbstractGridRow that = (AbstractGridRow) o;
        return Objects.equals( vocabulary, that.vocabulary ) &&
                Objects.equals( agency, that.agency ) &&
                Objects.equals( configService, that.configService ) &&
                Objects.equals( container, that.container ) &&
                Objects.equals( hLayout, that.hLayout ) &&
                Objects.equals( vLayout, that.vLayout ) &&
                Objects.equals( languageLayout, that.languageLayout ) &&
                Objects.equals( titleLayout, that.titleLayout ) &&
                Objects.equals( codeList, that.codeList ) &&
                Objects.equals( slTitle, that.slTitle ) &&
                Objects.equals( tlTitle, that.tlTitle ) &&
                Objects.equals( desc, that.desc ) &&
                Objects.equals( version, that.version ) &&
                currentSelectedLanguage == that.currentSelectedLanguage &&
                sourceLanguage == that.sourceLanguage;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), vocabulary, agency, configService, container, hLayout, vLayout, languageLayout, titleLayout, codeList, slTitle, tlTitle, desc, version, currentSelectedLanguage, sourceLanguage );
    }
}
