package eu.cessda.cvmanager.ui.layout;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.dto.AgencyDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;

import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.dto.LicenceDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.utils.CvManagerSecurityUtils;
import eu.cessda.cvmanager.utils.WorkflowUtils;

public class DialogMSLicenseLayout extends MCssLayout implements Translatable {
	
	private static final long serialVersionUID = -2461005203070668382L;
	private final VersionDTO version;
	private final AgencyDTO agency;
	
	private MCssLayout editLayout = new MCssLayout().withFullSize();	
	private ComboBox<LicenceDTO> licensesCb = new ComboBox<>( "Edit Licence" );
    private MLabel licensePreview = new MLabel().withContentMode( ContentMode.HTML);
    	
	public DialogMSLicenseLayout( 
			AgencyDTO agencyDTO, VersionDTO versionDTO,  List<LicenceDTO> licenses) {
		super();
		this.version = versionDTO;
		this.agency= agencyDTO;
		licensesCb.setItems( licenses );
		
		if( version.getLicenseId() != null) 
       	 	licenses.stream().filter( p -> p.getId().equals( version.getLicenseId() )).findFirst().ifPresent( 
       			 license -> {
       				 licensesCb.setValue(license);
       				 setLicensePreview(license);
       			 });
		else if( agency.getLicenseId() != null )
			 licenses.stream().filter( p -> p.getId().equals( agency.getLicenseId() )).findFirst().ifPresent( 
	       			 license -> {
	       				 licensesCb.setValue(license);
	       				 setLicensePreview(license);
	       			 });
		this.withFullWidth();
		init();
	}
	
	private void init() {
        licensesCb.setWidth( "100%" );
        licensesCb.setTextInputAllowed( false );
        licensesCb.setItemCaptionGenerator( item -> item.getId() + " " + item.getName());
        licensesCb.addValueChangeListener( e -> {
        	if( e.getValue() != null) {
        		setLicensePreview( e.getValue());
        		version.setLicenseId(e.getValue().getId());
        		version.setLicense(e.getValue().getName());
        	}
        	else
        		licensePreview.setValue("");
        });

		editLayout
			.add(
				licensesCb
			);
		this
			.add( 
				editLayout,
				licensePreview
			);
	}
	
	
	private String toXHTML( String html ) {
	    final Document document = Jsoup.parse(html);
	    document.select("script,.hidden,link").remove();
	    document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);    
	    return document.body().html();
	}
	
    private void setLicensePreview( LicenceDTO licenseDto ) {
    	if( licenseDto.isPersisted())
    		licensePreview.setValue( "<p>"
    			+ "<img style=\"max-width:120px\" alt=\"" + licenseDto.getName() + "\" style=\"border-width:0\" src=\"" + licenseDto.getLogoLink() + "\">"
    			+ "&nbsp;&nbsp;&nbsp;<span>This work is licensed under a "
    			+ "<a rel=\"license\" href=\"" + licenseDto.getLink() + "\">" + licenseDto.getName() + "</a>.</span>"
    			+ "</p>" );
    	else
    		licensePreview.setValue( "" );
    }

	@Override
	public void updateMessageStrings(Locale locale) {
		
	}

}
