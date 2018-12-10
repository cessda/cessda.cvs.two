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

import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.dto.LicenceDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.utils.CvManagerSecurityUtils;

public class LicenseLayout extends MCssLayout implements Translatable {
	
	private static final long serialVersionUID = -2461005203070668382L;
	private final I18N i18n;
	private final Locale locale;
	private final VersionDTO version;
	private final AgencyDTO agency;
	private final VersionService versionService;
	
	private enum LayoutMode{ READ, EDIT };
	
	private MCssLayout infoLayout = new MCssLayout().withFullSize();
	private MLabel copyrightInfo = new MLabel().withContentMode( ContentMode.HTML);
	
	private MLabel citationLabel = new MLabel("Citation").withContentMode( ContentMode.HTML).withFullWidth();
	private MLabel citationInfo = new MLabel().withContentMode( ContentMode.HTML).withFullWidth();;
	
	private MCssLayout editLayout = new MCssLayout().withFullSize();

	private MButton editSwitchButton = new MButton( "Edit" );
	private MCssLayout buttonLayout = new MCssLayout().withFullWidth();
	private MButton saveButton = new MButton( "Save" );
	private MButton cancelButton = new MButton( "Cancel" );
	private boolean readOnly;
	
	private ComboBox<LicenceDTO> licensesCb = new ComboBox<>( "Edit Licence" );
    private MLabel licensePreview = new MLabel().withContentMode( ContentMode.HTML);
    
    private RichTextArea citationEditor = new RichTextArea( "Edit Citation" );
	
	public LicenseLayout(I18N i18n, Locale locale, UIEventBus eventBus, 
			AgencyDTO agencyDTO, VersionDTO versionDTO,
			VersionService versionService, List<LicenceDTO> licenses,
			boolean readOnly) {
		super();
		this.i18n = i18n;
		this.locale = locale;
		this.version = versionDTO;
		this.agency= agencyDTO;
		this.versionService = versionService;
		this.readOnly = readOnly;
		licensesCb.setItems( licenses );
		if( version.getLicenseId() != null) 
       	 licenses.stream().filter( p -> p.getId().equals( version.getLicenseId() )).findFirst().ifPresent( 
       			 license -> {
       				 licensesCb.setValue(license);
       				 setLicensePreview(license);
       			 });
		
		this.withFullWidth();
		init();
	}
	
	private void init() {
		switchMode( LayoutMode.READ );
		
		int year = LocalDate.now().getYear();
		if( version.getStatus().equals( Status.PUBLISHED.toString())) {
			year = version.getPublicationDate().getYear();
		}
		copyrightInfo.setValue( "<p>Copyright © <a href='" + agency.getLink() + "' target='_blank'>" + agency.getName() + "</a> " + year + ".</p>");
		copyrightInfo.withFullWidth();
		editSwitchButton
			.withStyleName("pull-right")
			.withVisible( false )
			.addClickListener( e -> switchMode( LayoutMode.EDIT));
		
		citationEditor.setWidth("100%");
		citationEditor.setHeight("240px");
		if( version.getCitation() != null ) {
			citationEditor.setValue( version.getCitation() );
			citationInfo.setValue( version.getCitation() );
		}
		else{
			citationLabel.setVisible( false );
		}
		
		if( CvManagerSecurityUtils.isAuthenticated() && CvManagerSecurityUtils.isCurrentUserAllowToEditMetadata(agency, version)  && !readOnly) {
			editSwitchButton.setVisible( true );
		} else {
			editSwitchButton.setVisible( false );
		}
		
		infoLayout
			.add(
				citationLabel,
				citationInfo,
				editSwitchButton
			);
		
		
        licensesCb.setWidth( "100%" );
        licensesCb.setTextInputAllowed( false );
        licensesCb.setItemCaptionGenerator( item -> item.getId() + " " + item.getName());
        licensesCb.addValueChangeListener( e -> {
        	if( e.getValue() != null)
        		setLicensePreview( e.getValue());
        	else
        		licensePreview.setValue("");
        });
		
		saveButton
			.withStyleName("pull-right")
			.addClickListener( e -> {
				if( citationEditor.getValue().isEmpty()) {
					version.setCitation( "" );
					citationLabel.setVisible( false );
					citationInfo.setValue("");
				}
				else {
					version.setCitation( toXHTML( citationEditor.getValue() ) );
					citationLabel.setVisible( true );
					citationInfo.setValue( version.getCitation());
				}
				if( licensesCb.getValue() != null)
		    		version.setLicenseId( licensesCb.getValue().getId() );
		    	else
		    		version.setLicenseId( null );
				versionService.save(version);
				setLicensePreview( licensesCb.getValue() );
				switchMode( LayoutMode.READ);
			});
		
		cancelButton
			.withStyleName("pull-right")
			.addClickListener( e -> switchMode( LayoutMode.READ));
		
		buttonLayout
			.add( saveButton, cancelButton);
		
		editLayout
			.add(
				licensesCb,
				citationEditor,
				buttonLayout
			);
		this
			.add( 
				copyrightInfo,
				editLayout,
				licensePreview,
				infoLayout
				
			);
	}
	
	private void switchMode( LayoutMode layoutMode) {
		if( layoutMode.equals( LayoutMode.READ)) {
			infoLayout.setVisible( true );
			editLayout.setVisible( false );
		} else {
			infoLayout.setVisible( false );
			editLayout.setVisible( true );
		}
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
